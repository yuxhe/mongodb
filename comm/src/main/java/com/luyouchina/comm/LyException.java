/**
 * 
 */
package com.luyouchina.comm;

/**
 * @author lfj
 *
 */
public class LyException extends RuntimeException {

	private static final long serialVersionUID = -5909682326183340167L;

	private String id;
	private String msg;

	/**
	 * 
	 */
	public LyException() {
		super();
	}

	public LyException(String msg) {
		super(msg);

	}

	/**
	 * @param : throwable 异常
	 */
	public LyException(Throwable throwable) {
		super(throwable);
	}

	public LyException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	/**
	 * @param id
	 * @param msg
	 */
	public LyException(String id, String msg) {
		super(msg);
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
