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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.ReflectUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;

import com.luyouchina.comm.ConfigUtil;
import com.luyouchina.comm.ConfigUtil.Config;
import com.luyouchina.comm.ShardedJedisCommands;

/**
 * The filter that handles all the work of validating ticket requests.
 * <p>
 * This filter can be configured with the following values:
 * <ul>
 * <li><code>redirectAfterValidation</code> - redirect the CAS client to the
 * same URL without the ticket. (default: true, Will be forced to false when
 * {@link #useSession} is false.)</li>
 * <li><code>exceptionOnValidationFailure</code> - throw an exception if the
 * validation fails. Otherwise, continue processing. (default: true)</li>
 * <li><code>useSession</code> - store any of the useful information in a
 * session attribute. (default: true)</li>
 * <li><code>hostnameVerifier</code> - name of class implementing a
 * {@link HostnameVerifier}.</li>
 * <li><code>hostnameVerifierConfig</code> - name of configuration class
 * (constructor argument of verifier).</li>
 * </ul>
 *
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.1
 */
public abstract class AbstractTicketValidationFilter extends AbstractCasFilter {

	/************************************************/
	private String[] excludePaths;// url路径

	private String replacename;

	private static ShardedJedisCommands shardedJedisConn = new ShardedJedisCommands();// 缓存连接

	private List<String> list;
	/************************************************/

	/** The TicketValidator we will use to validate tickets. */
	private TicketValidator ticketValidator;

	/**
	 * Specify whether the filter should redirect the user agent after a
	 * successful validation to remove the ticket parameter from the query
	 * string.
	 */
	private boolean redirectAfterValidation = true;

	/**
	 * Determines whether an exception is thrown when there is a ticket
	 * validation failure.
	 */
	private boolean exceptionOnValidationFailure = false;

	/**
	 * Specify whether the Assertion should be stored in a session attribute
	 * {@link AbstractCasFilter#CONST_CAS_ASSERTION}.
	 */
	private boolean useSession = true;

	/**
	 * Template method to return the appropriate validator.
	 *
	 * @param filterConfig
	 *            the FilterConfiguration that may be needed to construct a
	 *            validator.
	 * @return the ticket validator.
	 */
	protected TicketValidator getTicketValidator(final FilterConfig filterConfig) {
		return this.ticketValidator;
	}

	/**
	 * Gets the ssl config to use for HTTPS connections if one is configured for
	 * this filter.
	 * 
	 * @param filterConfig
	 *            Servlet filter configuration.
	 * @return Properties that can contains key/trust info for Client Side
	 *         Certificates
	 */
	protected Properties getSSLConfig(final FilterConfig filterConfig) {
		final Properties properties = new Properties();
		final String fileName = getPropertyFromInitParams(filterConfig, "sslConfigFile", null);

		if (fileName != null) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(fileName);
				properties.load(fis);
				logger.trace("Loaded {} entries from {}", properties.size(), fileName);
			} catch (final IOException ioe) {
				logger.error(ioe.getMessage(), ioe);
			} finally {
				CommonUtils.closeQuietly(fis);
			}
		}
		return properties;
	}

	/**
	 * Gets the configured {@link HostnameVerifier} to use for HTTPS connections
	 * if one is configured for this filter.
	 * 
	 * @param filterConfig
	 *            Servlet filter configuration.
	 * @return Instance of specified host name verifier or null if none
	 *         specified.
	 */
	protected HostnameVerifier getHostnameVerifier(final FilterConfig filterConfig) {
		final String className = getPropertyFromInitParams(filterConfig, "hostnameVerifier", null);
		logger.trace("Using hostnameVerifier parameter: {}", className);
		final String config = getPropertyFromInitParams(filterConfig, "hostnameVerifierConfig", null);
		logger.trace("Using hostnameVerifierConfig parameter: {}", config);
		if (className != null) {
			if (config != null) {
				return ReflectUtils.newInstance(className, config);
			} else {
				return ReflectUtils.newInstance(className);
			}
		}
		return null;
	}

	protected void initInternal(final FilterConfig filterConfig) throws ServletException {
		setExceptionOnValidationFailure(parseBoolean(getPropertyFromInitParams(filterConfig, "exceptionOnValidationFailure", "false")));
		logger.trace("Setting exceptionOnValidationFailure parameter: {}", this.exceptionOnValidationFailure);
		setRedirectAfterValidation(parseBoolean(getPropertyFromInitParams(filterConfig, "redirectAfterValidation", "true")));
		logger.trace("Setting redirectAfterValidation parameter: {}", this.redirectAfterValidation);
		setUseSession(parseBoolean(getPropertyFromInitParams(filterConfig, "useSession", "true")));
		logger.trace("Setting useSession parameter: {}", this.useSession);

		// 获取上下文中的路径
		String _excludePaths = ConfigUtil.getConfig(Config.exclude_paths);
		if (CommonUtils.isNotBlank(_excludePaths)) {
			setExcludePaths(_excludePaths.trim().split(","));
		}

		System.out.println("获取的Config地址 : " + _excludePaths);
		System.out.println("验证配置的地址 : " + this.replacename);

		setReplacename(getPropertyFromInitParams(filterConfig, "serverName", null));
		if (!this.useSession && this.redirectAfterValidation) {
			logger.warn("redirectAfterValidation parameter may not be true when useSession parameter is false. Resetting it to false in order to prevent infinite redirects.");
			setRedirectAfterValidation(false);
		}

		setTicketValidator(getTicketValidator(filterConfig));
		super.initInternal(filterConfig);
	}

	public void init() {
		super.init();
		CommonUtils.assertNotNull(this.ticketValidator, "ticketValidator cannot be null.");
	}

	/**
	 * Pre-process the request before the normal filter process starts. This
	 * could be useful for pre-empting code.
	 *
	 * @param servletRequest
	 *            The servlet request.
	 * @param servletResponse
	 *            The servlet response.
	 * @param filterChain
	 *            the filter chain.
	 * @return true if processing should continue, false otherwise.
	 * @throws IOException
	 *             if there is an I/O problem
	 * @throws ServletException
	 *             if there is a servlet problem.
	 */
	protected boolean preFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
		return true;
	}

	/**
	 * Template method that gets executed if ticket validation succeeds.
	 * Override if you want additional behavior to occur if ticket validation
	 * succeeds. This method is called after all ValidationFilter processing
	 * required for a successful authentication occurs.
	 *
	 * @param request
	 *            the HttpServletRequest.
	 * @param response
	 *            the HttpServletResponse.
	 * @param assertion
	 *            the successful Assertion from the server.
	 */
	protected void onSuccessfulValidation(final HttpServletRequest request, final HttpServletResponse response, final Assertion assertion) {
		// nothing to do here.
	}

	/**
	 * Template method that gets executed if validation fails. This method is
	 * called right after the exception is caught from the ticket validator but
	 * before any of the processing of the exception occurs.
	 *
	 * @param request
	 *            the HttpServletRequest.
	 * @param response
	 *            the HttpServletResponse.
	 */
	protected void onFailedValidation(final HttpServletRequest request, final HttpServletResponse response) {
		// nothing to do here.
	}

	public final void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
		if (!preFilter(servletRequest, servletResponse, filterChain)) {
			return;
		}
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final HttpServletResponse response = (HttpServletResponse) servletResponse;
		final String ticket = retrieveTicketFromRequest(request);

		// 获取域名配置
		Map<String, String> industryinfo = shardedJedisConn.hgetAll("industryinfo");
		// 为了获取最新配置的地址 ，每次重置
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
//		System.out.println("访问地址: " + url);
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

		if (urls.equals("")) {
			System.out.println("***没有找到对应的配置地址***");

		}

		if (CommonUtils.isNotBlank(ticket)) {
			logger.debug("Attempting to validate ticket: {}", ticket);

			try {
				/************************/
//				System.out.println("验证获取的路径 : " + urls);
//				System.out.println("验证配置的路径 : " + this.replacename);
				String realurl = constructServiceUrl(request, response);
//				System.out.println(realurl + " : 验证转换前地址");
				realurl = realurl.replace(this.replacename, urls);
//				System.out.println(realurl + " : 验证转换后地址");
				final Assertion assertion = this.ticketValidator.validate(ticket, realurl);
				/************************/

				// final Assertion assertion =
				// this.ticketValidator.validate(ticket,
				// constructServiceUrl(request, response));

				logger.debug("Successfully authenticated user: {}", assertion.getPrincipal().getName());
				System.out.println("Successfully authenticated user: {" + assertion.getPrincipal().getName() + "}");
				request.setAttribute(CONST_CAS_ASSERTION, assertion);

				if (this.useSession) {
					request.getSession().setAttribute(CONST_CAS_ASSERTION, assertion);
				}
				onSuccessfulValidation(request, response, assertion);

				if (this.redirectAfterValidation) {
					System.out.println("Redirecting after successful ticket validation.");
					logger.debug("Redirecting after successful ticket validation.");
					/************************/
					response.sendRedirect(realurl);
					/************************/
					// response.sendRedirect(constructServiceUrl(request,
					// response));
					return;
				}
			} catch (final TicketValidationException e) {
				logger.debug(e.getMessage(), e);

				onFailedValidation(request, response);

				if (this.exceptionOnValidationFailure) {
					throw new ServletException(e);
				}

				response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());

				return;
			}
		}

		filterChain.doFilter(request, response);

	}

	public final void setTicketValidator(final TicketValidator ticketValidator) {
		this.ticketValidator = ticketValidator;
	}

	public final void setRedirectAfterValidation(final boolean redirectAfterValidation) {
		this.redirectAfterValidation = redirectAfterValidation;
	}

	public final void setExceptionOnValidationFailure(final boolean exceptionOnValidationFailure) {
		this.exceptionOnValidationFailure = exceptionOnValidationFailure;
	}

	public final void setUseSession(final boolean useSession) {
		this.useSession = useSession;
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

	public static void main(String[] args) {
		String a = "http://tdpx2.koalor.com:80/train/login?http://tdpx2.koalor.com/train_web/html/main/class.html";
		String b = "http://tdpx2.koalor.com:80/";
		System.err.println(a.replace("http://tdpx2.koalor.com:80/", b));

	}

}