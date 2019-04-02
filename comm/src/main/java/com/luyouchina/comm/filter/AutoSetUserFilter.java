package com.luyouchina.comm.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.type.TypeReference;
import com.luyouchina.comm.JsonUtil;
import com.luyouchina.comm.ShardedJedisCommands;
import com.luyouchina.comm.SingleBeanFactory;
import com.luyouchina.comm.SysParamUtil;
import com.luyouchina.comm.model.CommBaseParam;
import com.luyouchina.comm.model.NewLoginUser;
import com.luyouchina.comm.service.LoginService;

/**
 * Servlet Filter implementation class AutoSetUserFilter
 */
public class AutoSetUserFilter implements Filter {

	private LoginService loginService;

	public AutoSetUserFilter() {
		loginService = SingleBeanFactory.getBean(LoginService.class);
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession();
		if (session.getAttribute(CommBaseParam.sessionUser) == null) {
			// 用户信息存放session
			loginService.setSession(httpRequest);
		} 

		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

}
