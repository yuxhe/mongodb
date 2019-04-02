/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.luyouchina.comm.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.authentication.AuthenticationRedirectStrategy;
import org.jasig.cas.client.authentication.ContainsPatternUrlPatternMatcherStrategy;
import org.jasig.cas.client.authentication.DefaultAuthenticationRedirectStrategy;
import org.jasig.cas.client.authentication.DefaultGatewayResolverImpl;
import org.jasig.cas.client.authentication.ExactUrlPatternMatcherStrategy;
import org.jasig.cas.client.authentication.GatewayResolver;
import org.jasig.cas.client.authentication.RegexUrlPatternMatcherStrategy;
import org.jasig.cas.client.authentication.UrlPatternMatcherStrategy;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.ReflectUtils;
import org.jasig.cas.client.validation.Assertion;

import com.luyouchina.comm.ConfigUtil;
import com.luyouchina.comm.ConfigUtil.Config;
import com.luyouchina.comm.JsonUtil;
import com.luyouchina.comm.ShardedJedisCommands;

/**
 * Filter implementation to intercept all requests and attempt to authenticate
 * the user by redirecting them to CAS (unless the user has a ticket).
 * <p>
 * This filter allows you to specify the following parameters (at either the
 * context-level or the filter-level):
 * <ul>
 * <li><code>casServerLoginUrl</code> - the url to log into CAS, i.e.
 * https://cas.rutgers.edu/login</li>
 * <li><code>renew</code> - true/false on whether to use renew or not.</li>
 * <li><code>gateway</code> - true/false on whether to use gateway or not.</li>
 * </ul>
 *
 * <p>
 * Please see AbstractCasFilter for additional properties.
 * </p>
 *
 * @author Scott Battaglia
 * @author Misagh Moayyed
 * @since 3.0
 */
public class AuthenticationFilter extends AbstractCasFilter {
	/**
	 * The URL to the CAS Server login.
	 */
	private String casServerLoginUrl;

	/**
	 * Whether to send the renew request or not.
	 */
	private boolean renew = false;

	/**
	 * Whether to send the gateway request or not.
	 */
	private boolean gateway = false;

	private GatewayResolver gatewayStorage = new DefaultGatewayResolverImpl();

	private AuthenticationRedirectStrategy authenticationRedirectStrategy = new DefaultAuthenticationRedirectStrategy();

	/************************************************/
	private String[] excludePaths;// url路径

	private String replacename;

	private static ShardedJedisCommands shardedJedisConn = new ShardedJedisCommands();// 缓存连接

	private List<String> list;
	/************************************************/

	private UrlPatternMatcherStrategy ignoreUrlPatternMatcherStrategyClass = null;

	private static final Map<String, Class<? extends UrlPatternMatcherStrategy>> PATTERN_MATCHER_TYPES = new HashMap<String, Class<? extends UrlPatternMatcherStrategy>>();

	static {
		PATTERN_MATCHER_TYPES.put("CONTAINS", ContainsPatternUrlPatternMatcherStrategy.class);
		PATTERN_MATCHER_TYPES.put("REGEX", RegexUrlPatternMatcherStrategy.class);
		PATTERN_MATCHER_TYPES.put("EXACT", ExactUrlPatternMatcherStrategy.class);
	}

	protected void initInternal(final FilterConfig filterConfig) throws ServletException {
		if (!isIgnoreInitConfiguration()) {
			super.initInternal(filterConfig);

			// 获取上下文中的路径
			String _excludePaths = ConfigUtil.getConfig(Config.exclude_paths);
			if (CommonUtils.isNotBlank(_excludePaths)) {
				setExcludePaths(_excludePaths.trim().split(","));
			}
			
			setReplacename(getPropertyFromInitParams(filterConfig, "serverName", null));

			setCasServerLoginUrl(getPropertyFromInitParams(filterConfig, "casServerLoginUrl", null));
			logger.trace("Loaded CasServerLoginUrl parameter: {}", this.casServerLoginUrl);
			setRenew(parseBoolean(getPropertyFromInitParams(filterConfig, "renew", "false")));
			logger.trace("Loaded renew parameter: {}", this.renew);
			setGateway(parseBoolean(getPropertyFromInitParams(filterConfig, "gateway", "false")));
			logger.trace("Loaded gateway parameter: {}", this.gateway);

			final String ignorePattern = getPropertyFromInitParams(filterConfig, "ignorePattern", null);
			logger.trace("Loaded ignorePattern parameter: {}", ignorePattern);

			final String ignoreUrlPatternType = getPropertyFromInitParams(filterConfig, "ignoreUrlPatternType", "REGEX");
			logger.trace("Loaded ignoreUrlPatternType parameter: {}", ignoreUrlPatternType);

			if (ignorePattern != null) {
				final Class<? extends UrlPatternMatcherStrategy> ignoreUrlMatcherClass = PATTERN_MATCHER_TYPES.get(ignoreUrlPatternType);
				if (ignoreUrlMatcherClass != null) {
					this.ignoreUrlPatternMatcherStrategyClass = ReflectUtils.newInstance(ignoreUrlMatcherClass.getName());
				} else {
					try {
						logger.trace("Assuming {} is a qualified class name...", ignoreUrlPatternType);
						this.ignoreUrlPatternMatcherStrategyClass = ReflectUtils.newInstance(ignoreUrlPatternType);
					} catch (final IllegalArgumentException e) {
						logger.error("Could not instantiate class [{}]", ignoreUrlPatternType, e);
					}
				}
				if (this.ignoreUrlPatternMatcherStrategyClass != null) {
					this.ignoreUrlPatternMatcherStrategyClass.setPattern(ignorePattern);
				}
			}

			final String gatewayStorageClass = getPropertyFromInitParams(filterConfig, "gatewayStorageClass", null);

			if (gatewayStorageClass != null) {
				this.gatewayStorage = ReflectUtils.newInstance(gatewayStorageClass);
			}

			final String authenticationRedirectStrategyClass = getPropertyFromInitParams(filterConfig, "authenticationRedirectStrategyClass", null);

			if (authenticationRedirectStrategyClass != null) {
				this.authenticationRedirectStrategy = ReflectUtils.newInstance(authenticationRedirectStrategyClass);
			}
		}
	}

	public void init() {
		super.init();
		CommonUtils.assertNotNull(this.casServerLoginUrl, "casServerLoginUrl cannot be null.");
	}

	public final void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {

		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final HttpServletResponse response = (HttpServletResponse) servletResponse;
		// 获取域名配置
		Map<String, String> industryinfo = shardedJedisConn.hgetAll("industryinfo");
		list = new ArrayList<String>();
		if (industryinfo != null) {
			for (String key : industryinfo.keySet()) {
				if (key.split(":").length == 2) {
					key = key + ":80/";
				} else {
					key = key + "/";
				}
				list.add(key);
			}
		}

		if (excludePaths != null) {
			List<String> paths = Arrays.asList(excludePaths);
			list.addAll(paths);
		}
		// 获取访问路径
		String url = request.getRequestURL().toString();
		String uri = request.getRequestURI();
		url = url.replace(uri, "");
		if (url.split(":").length == 2) {
			url = url + ":80/";
		} else {
			url = url + "/";
		}
		System.out.println("访问地址：" + url);
		String urls = "";
		// 获取访问来源

		for (String path : list) {
			if (CommonUtils.isNotBlank(path)) {
				if (CommonUtils.isNotBlank(url) && url.startsWith(path)) {
					urls = path;
					break;
				}
			}
		}

		if (isRequestUrlExcluded(request)) {
			logger.debug("Request is ignored.");
			filterChain.doFilter(request, response);
			return;
		}

		final HttpSession session = request.getSession(false);
		final Assertion assertion = session != null ? (Assertion) session.getAttribute(CONST_CAS_ASSERTION) : null;

		if (assertion != null) {
			filterChain.doFilter(request, response);
			return;
		}

		// final String serviceUrl = constructServiceUrl(request, response);

		// 根据域名替换xml的 serverName
		/************************************************/
		System.out.println("获取的路径" + urls);
		System.out.println("配置的路径" + this.replacename);
		String serviceUrl = constructServiceUrl(request, response);
		System.out.println(serviceUrl + "转换前地址");
		serviceUrl = serviceUrl.replace(this.replacename, urls);
		System.out.println(serviceUrl + "转换后地址");
		/************************************************/

		final String ticket = retrieveTicketFromRequest(request);

		final boolean wasGatewayed = this.gateway && this.gatewayStorage.hasGatewayedAlready(request, serviceUrl);

		if (CommonUtils.isNotBlank(ticket) || wasGatewayed) {
			filterChain.doFilter(request, response);
			return;
		}

		final String modifiedServiceUrl;

		logger.debug("no ticket and no assertion found");
		if (this.gateway) {
			logger.debug("setting gateway attribute in session");
			modifiedServiceUrl = this.gatewayStorage.storeGatewayInformation(request, serviceUrl);
		} else {
			modifiedServiceUrl = serviceUrl;
		}

		logger.debug("Constructed service url: {}", modifiedServiceUrl);

		final String urlToRedirectTo = CommonUtils.constructRedirectUrl(this.casServerLoginUrl, getServiceParameterName(), modifiedServiceUrl, this.renew, this.gateway);

		logger.debug("redirecting to \"{}\"", urlToRedirectTo);
		// 判断是否是一个ajax请求
		Boolean type = false;
		String requestType = request.getHeader("X-Requested-With");
		if (requestType != null && requestType.equals("XMLHttpRequest")) {
			type = true;
		} else {
			type = false;
		}
		System.out.println(urlToRedirectTo);
		// 分别跳转
		if (type) {
			response.setContentType("text/json;charset=UTF-8");
			Map<String, String> map = new HashMap<String, String>();
			map.put("url", urlToRedirectTo);
			response.getWriter().write(JsonUtil.toJson(map));
		} else {
			this.authenticationRedirectStrategy.redirect(request, response, urlToRedirectTo);
		}
	}

	public final void setRenew(final boolean renew) {
		this.renew = renew;
	}

	public final void setGateway(final boolean gateway) {
		this.gateway = gateway;
	}

	public final void setCasServerLoginUrl(final String casServerLoginUrl) {
		this.casServerLoginUrl = casServerLoginUrl;
	}

	public final void setGatewayStorage(final GatewayResolver gatewayStorage) {
		this.gatewayStorage = gatewayStorage;
	}

	private boolean isRequestUrlExcluded(final HttpServletRequest request) {
		if (this.ignoreUrlPatternMatcherStrategyClass == null) {
			return false;
		}

		final StringBuffer urlBuffer = request.getRequestURL();
		if (request.getQueryString() != null) {
			urlBuffer.append("?").append(request.getQueryString());
		}
		final String requestUri = urlBuffer.toString();
		return this.ignoreUrlPatternMatcherStrategyClass.matches(requestUri);
	}

	public String[] getExcludePaths() {
		return excludePaths;
	}

	public void setExcludePaths(String[] excludePaths) {
		this.excludePaths = excludePaths;
	}

	public String getReplacename() {
		return replacename;
	}

	public void setReplacename(String replacename) {
		this.replacename = replacename;
	}
}
