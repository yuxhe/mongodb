/**
 * 
 */
package com.luyouchina.comm.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author lfj
 *
 */
public class RequestObject implements Serializable {

	private static final long serialVersionUID = 6516644765579504659L;

	/**
	 * 区分模块
	 */
	private String module;
	/**
	 * 接口名
	 */
	private String method;
	/**
	 * 参数
	 */
	private Map<String, Object> param;
	/**
	 * 来源信息
	 */
	private Origin origin;

	/**
	 * @return the module
	 */
	public String getModule() {
		return module;
	}

	/**
	 * @param module
	 *            the module to set
	 */
	public void setModule(String module) {
		this.module = module;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method
	 *            the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the param
	 */
	public Map<String, Object> getParam() {
		return param;
	}

	/**
	 * @param param
	 *            the param to set
	 */
	public void setParam(Map<String, Object> param) {
		this.param = param;
	}

	/**
	 * @return the origin
	 */
	public Origin getOrigin() {
		return origin;
	}

	/**
	 * @param origin
	 *            the origin to set
	 */
	public void setOrigin(Origin origin) {
		this.origin = origin;
	}

}
