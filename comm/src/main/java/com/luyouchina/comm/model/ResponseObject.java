/**
 * 
 */
package com.luyouchina.comm.model;

import java.io.Serializable;

/**
 * @author lfj
 *
 */
public class ResponseObject implements Serializable {

	private static final long serialVersionUID = -2751539446935440085L;

	/**
	 * 区分模块
	 */
	// private String module;
	/**
	 * 接口名
	 */
	// private String method;
	/**
	 * 结果数据
	 */
	private Object data;
	/**
	 * 错误信息
	 */
	private ErrorMsg errorMsg;

	public ResponseObject() {
		this.setErrorMsg(new ErrorMsg());
		this.setData(new Object());
	}

	/**
	 * 
	 * @param request
	 */
	public ResponseObject(RequestObject request) {
		// this.setModule(request.getModule());
		// this.setMethod(request.getMethod());
		this.setErrorMsg(new ErrorMsg());
		this.setData(new Object());
	}

	/**
	 * @param request
	 * @param id
	 * @param msg
	 */
	public ResponseObject(RequestObject request, String id, String msg) {
		this(request);
		this.setErrorMsg(new ErrorMsg(id, msg));
	}

	/**
	 * 
	 * @param id
	 * @param msg
	 */
	public ResponseObject(String id, String msg) {
		this.setErrorMsg(new ErrorMsg(id, msg));
	}

	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @return the errorMsg
	 */
	public ErrorMsg getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @return the method
	 */
	// public String getMethod() {
	// return method;
	// }

	/**
	 * @return the module
	 */
	// public String getModule() {
	// return module;
	// }

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * @param errorMsg
	 *            the errorMsg to set
	 */
	public void setErrorMsg(ErrorMsg errorMsg) {
		this.errorMsg = errorMsg;
	}

	public void setErrorMsg(String id, String msg) {
		this.errorMsg = new ErrorMsg(id, msg);
	}

	/**
	 * @param method
	 *            the method to set
	 */
	// public void setMethod(String method) {
	// this.method = method;
	// }

	/**
	 * @param module
	 *            the module to set
	 */
	// public void setModule(String module) {
	// this.module = module;
	// }

}
