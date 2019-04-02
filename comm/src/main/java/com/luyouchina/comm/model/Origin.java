/**
 * 
 */
package com.luyouchina.comm.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author lfj
 *
 */
public class Origin {

	private String reqaccno;// 接口调用者用户标示
	private String reqaccname;// 接口调用者用户姓名
	private String systemcode;// 子系统代码
	private String version;// 版本号
	private String description;// 描述备注
	private String loginkey;// 登陆标识
	private String industryno;// 行业标示号
	private String industryname;// 行业名称

	public Origin() {
	}

	public Origin(Version version) {
		this.systemcode = version.getSystemCode();
		this.version = version.getSystemVersion();
	}

	public Origin(HttpServletRequest request, Version version) {
		this(version);
		HttpSession session = request.getSession();
		NewLoginUser user = (NewLoginUser) session.getAttribute(CommBaseParam.sessionUser);
		if (user != null) {
			this.reqaccno = user.getAccno();
			this.reqaccname = user.getMemname();
		}
	}

	/**
	 * 
	 * @param systemcode
	 *            子系统代码
	 * @param version
	 *            版本号
	 */
	public Origin(String systemcode, String version) {
		super();
		this.systemcode = systemcode;
		this.version = version;
	}

	/**
	 * 
	 * @param systemcode
	 *            子系统代码
	 * @param version
	 *            版本号
	 * @param description
	 *            描述备注
	 */
	public Origin(String systemcode, String version, String description) {
		super();
		this.systemcode = systemcode;
		this.version = version;
		this.description = description;
	}

	/**
	 * @param reqaccno
	 *            接口调用者用户标示
	 * @param reqaccname
	 *            接口调用者用户姓名
	 * @param systemcode
	 *            子系统代码
	 * @param version
	 *            版本号
	 */
	public Origin(String reqaccno, String reqaccname, String systemcode, String version) {
		super();
		this.reqaccno = reqaccno;
		this.reqaccname = reqaccname;
		this.systemcode = systemcode;
		this.version = version;
	}

	/**
	 * @param reqaccno
	 *            接口调用者用户标示
	 * @param reqaccname
	 *            接口调用者用户姓名
	 * @param systemcode
	 *            子系统代码
	 * @param version
	 *            版本号
	 * @param description
	 *            描述备注
	 */
	public Origin(String reqaccno, String reqaccname, String systemcode, String version, String description) {
		super();
		this.reqaccno = reqaccno;
		this.reqaccname = reqaccname;
		this.systemcode = systemcode;
		this.version = version;
		this.description = description;
	}

	/**
	 * 获取接口调用者用户标示
	 * 
	 * @return the reqaccno
	 */
	public String getReqaccno() {
		return reqaccno;
	}

	/**
	 * 设置接口调用者用户标示
	 * 
	 * @param reqaccno
	 *            the reqaccno to set
	 */
	public void setReqaccno(String reqaccno) {
		this.reqaccno = reqaccno;
	}

	/**
	 * 获取接口调用者用户姓名
	 * 
	 * @return the reqaccname
	 */
	public String getReqaccname() {
		return reqaccname;
	}

	/**
	 * 设置接口调用者用户姓名
	 * 
	 * @param reqaccname
	 *            the reqaccname to set
	 */
	public void setReqaccname(String reqaccname) {
		this.reqaccname = reqaccname;
	}

	/**
	 * 获取子系统代码
	 * 
	 * @return the systemcode
	 */
	public String getSystemcode() {
		return systemcode;
	}

	/**
	 * 设置子系统代码
	 * 
	 * @param systemcode
	 *            the systemcode to set
	 */
	public void setSystemcode(String systemcode) {
		this.systemcode = systemcode;
	}

	/**
	 * 获取 版本号
	 * 
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * 设置 版本号
	 * 
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * 获取 描述备注
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置 描述备注
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the loginkey
	 */
	public String getLoginkey() {
		return loginkey;
	}

	/**
	 * @param loginkey
	 *            the loginkey to set
	 */
	public void setLoginkey(String loginkey) {
		this.loginkey = loginkey;
	}

	/**
	 * @return the industryno
	 */
	public String getIndustryno() {
		return industryno;
	}

	/**
	 * @param industryno
	 *            the industryno to set
	 */
	public void setIndustryno(String industryno) {
		this.industryno = industryno;
	}

	/**
	 * @return the industryname
	 */
	public String getIndustryname() {
		return industryname;
	}

	/**
	 * @param industryname
	 *            the industryname to set
	 */
	public void setIndustryname(String industryname) {
		this.industryname = industryname;
	}

}
