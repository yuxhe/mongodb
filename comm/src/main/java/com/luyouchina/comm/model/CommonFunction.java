package com.luyouchina.comm.model;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CommonFunction {

	public static Origin getOrigin(HttpServletRequest request, Version version) {
		Origin origin = new Origin(version);
		HttpSession session = request.getSession();
		NewLoginUser user = (NewLoginUser) session.getAttribute(CommBaseParam.sessionUser);
		if (user != null) {
			origin.setReqaccno(user.getAccno());
			origin.setReqaccname(user.getMemname());
		}
		return origin;
	}

	/**
	 * 判断errorMsg的id 是否为空
	 * 
	 * @param responseObject
	 * @return
	 */
	public static boolean errorMsgIdIsNull(ResponseObject responseObject) {
		return responseObject.getErrorMsg().getId() == null ? true : false;
	}

	/**
	 * 验证一个对象是否为null，为空返回false，否则返回true
	 * 
	 * @param obj
	 * @return boolean
	 */
	public static boolean isNotNull(Object obj) {
		return obj != null ? true : false;
	}

	/**
	 * 验证字符串是否为空，为空返回false，否则返回true
	 * 
	 * @param str
	 *            字符串
	 * @return boolean
	 */
	public static boolean isNotNull(String str) {
		// return !(str==null||"".equals(str.trim())||str.trim().length()==0);
		return !(str == null || "".equals(str) || str.length() == 0);
	}

	/**
	 * 获取表单提交过来的参数map集合
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map getParams(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setCharacterEncoding("UTF-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			Map<String, String> params = new HashMap<String, String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
				}
				// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "UTF-8");
				params.put(name, valueStr);
			}
			return params;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 用户没有登录 返回"-2"
	 * 
	 * @param response
	 */
	// public static void notLogin(HttpServletRequest request,HttpServletResponse response){
	// /*String url = request.getHeader("Referer");
	// String goLogin="http://192.168.1.16:8080/auth/login?service=http://192.168.1.111:8090/train/login";
	// String allUrl=goLogin+"?"+url;
	// System.out.println("******************: "+allUrl);*/
	// ResponseDto res=new ResponseDto();
	// res.setMsg("请登录!");
	// res.setStatus("-2");
	// //res.setData(allUrl);
	// HttpUtils.printJson(res, response);
	// }

	/**
	 * 清理缓存
	 * 
	 * @param response
	 */
	public static void notCache(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setHeader("pragma", "no-cache");
			response.setHeader("cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

}
