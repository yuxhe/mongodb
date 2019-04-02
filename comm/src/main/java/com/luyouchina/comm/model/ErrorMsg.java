/**
 * 
 */
package com.luyouchina.comm.model;

/**
 * @author lfj
 *
 */
public class ErrorMsg {

	private String id;
	private String msg;

	public ErrorMsg() {
	}

	/**
	 * @param id
	 * @param msg
	 */
	public ErrorMsg(String id, String msg) {
		super();
		this.id = id;
		this.msg = msg;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg
	 *            the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

}
