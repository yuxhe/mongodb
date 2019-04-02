package com.luyouchina.comm.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.luyouchina.comm.ClassLoadUtil;
import com.luyouchina.comm.HttpUtils;
import com.luyouchina.comm.ResponseDto;
import com.luyouchina.comm.annotaion.ReqHandleMethod;
import com.luyouchina.comm.annotaion.ReqHandler;
import com.luyouchina.comm.model.NewLoginUser;

@MultipartConfig(location = "/", maxFileSize = 1024 * 1024 * 10000)
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 2736503424990714731L;
	private Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

	/**
	 * 每个模块对应一个命令Map<br>
	 * 结构：{"模块名":{"命令名":{处理命令的对象和方法}}}
	 */
	private Map<String, Map<String, HandlerMethod>> modulesMap = new HashMap<String, Map<String, HandlerMethod>>();

	@Override
	public void init(ServletConfig config) throws ServletException {
		String pack = config.getInitParameter("scanPack");
		List<Class<?>> classes = ClassLoadUtil.LoadClasses(pack, true);
		if (CollectionUtils.isEmpty(classes)) {
			log.warn("Load 0 Class From {}", pack);
		} else {
			log.info("开始扫描接口：{}", pack);
			scanReqHandler(classes);
			log.info("扫描接口完毕");
		}

	}

	/**
	 * 扫描cmd处理方法
	 */
	private void scanReqHandler(List<Class<?>> classes) {
		for (Class<?> clazz : classes) {
			ReqHandler handler = clazz.getAnnotation(ReqHandler.class);
			if (handler == null) {
				continue;
			}

			String module = handler.value();
			Map<String, HandlerMethod> methodMap = modulesMap.get(module);
			if (methodMap == null) {
				methodMap = new HashMap<String, HandlerMethod>();
				modulesMap.put(module, methodMap);
			}

			Object obj = null;
			try {
				obj = clazz.newInstance();
			} catch (Exception e) {
				log.error("initialize {} error", clazz.getName());
				e.printStackTrace();
				continue;
			}

			Method[] methods = clazz.getDeclaredMethods();
			for (Method m : methods) {
				ReqHandleMethod rm = m.getAnnotation(ReqHandleMethod.class);
				if (rm != null) {
					String v = rm.value();
					if (v != null) {
						methodMap.put(v, new HandlerMethod(obj, m));
						log.info("Find Cmd [{}] 's Handle Method [{}.{}]", v, clazz.getSimpleName(), m.getName());
					}
				}
			}

		}
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cmd = req.getParameter("cmd");
		String module = req.getParameter("module");
		// String module = req.getRequestURI().replace(req.getContextPath() + "/", "");

		log.debug("module={}, Cmd={}, Req={}, params={}", module, cmd, req.getRequestURI(), req.getParameterMap());

		if (StringUtils.isBlank(module)) {
			log.info("Parameter [module] is null");
			HttpUtils.printResult(0, "请输入参数：[module]", null, resp);
			return;
		}

		if (StringUtils.isBlank(cmd)) {
			log.info("Parameter [cmd] is null");
			HttpUtils.printResult(0, "请输入参数：[cmd]", null, resp);
			return;
		}

		Map<String, HandlerMethod> methodMap = modulesMap.get(module);
		if (methodMap == null) {
			log.info("Handler For Module=" + module + " Not Found");
			HttpUtils.printResult(0, "您请求的地址 module=" + module + " 不存在", null, resp);
			return;
		}
		HandlerMethod hm = methodMap.get(cmd.trim());
		if (hm == null) {
			log.info("Handler For Cmd=" + cmd + " Not Found");
			HttpUtils.printResult(0, "您请求的地址 cmd=" + cmd + " 不存在", null, resp);
			return;
		}

		try {
			ReqHandleMethod reqHandleMethod = hm.getMethod().getAnnotation(ReqHandleMethod.class);
			if (StringUtils.isNotEmpty(reqHandleMethod.sessionKey())) {
				NewLoginUser user = (NewLoginUser) req.getSession().getAttribute(reqHandleMethod.sessionKey());
				if (user == null) {
					resp.setStatus(401);
					ResponseDto dto = new ResponseDto();
					dto.setStatus("-2");
					dto.setMsg("未登录或登陆已过期，请重新登陆");
					HttpUtils.printJson(dto, resp);
					return;
				}
				if (reqHandleMethod.role().length > 0) {
					List<String> roles = new ArrayList<String>();
					roles.addAll(Arrays.asList(reqHandleMethod.role()));
					if (!roles.contains(String.valueOf(user.getAccountstatus()))) {
						resp.setStatus(401);
						ResponseDto dto = new ResponseDto();
						dto.setStatus("-3");
						dto.setMsg("抱歉，您没有权限访问该页面");
						HttpUtils.printJson(dto, resp);
						return;
					}
				}
			}

			hm.getMethod().invoke(hm.getHandler(), req, resp);
		} catch (Exception e) {
			e.printStackTrace();
			HttpUtils.printResult(0, e.getCause().getMessage(), null, resp);
		}
	}

	public static class HandlerMethod {
		private Object handler;
		private Method method;

		public HandlerMethod(Object handler, Method method) {
			this.handler = handler;
			this.method = method;
		}

		public HandlerMethod() {

		}

		public Object getHandler() {
			return handler;
		}

		public void setHandler(Object handler) {
			this.handler = handler;
		}

		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}
	}
}
