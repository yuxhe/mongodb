package com.luyouchina.comm.model;

/**
 * @author lifajun
 *
 */
public class Version {

	/**
	 * 系统代码
	 */
	public String systemCode;
	/**
	 * 系统版本
	 */
	public String systemVersion;

	public Version() {
		super();
	}

	public Version(String systemCode, String systemVersion) {
		super();
		this.systemCode = systemCode;
		this.systemVersion = systemVersion;
	}

	/**
	 * @return the systemCode
	 */
	public String getSystemCode() {
		return systemCode;
	}

	/**
	 * @param systemCode
	 *            the systemCode to set
	 */
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	/**
	 * @return the systemVersion
	 */
	public String getSystemVersion() {
		return systemVersion;
	}

	/**
	 * @param systemVersion
	 *            the systemVersion to set
	 */
	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}

}
