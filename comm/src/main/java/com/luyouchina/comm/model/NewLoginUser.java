/**
 *
 */
package com.luyouchina.comm.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author : zhuyu
 * @version : 1.00
 * @create time : 2015年10月29日上午9:10:08
 * @description :
 * @History :
 */
public class NewLoginUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1978785675977934402L;

	private String accid;
	private String nickname;
	private String loginkey;
	private String acclogin;
	private String logintype;
	private Integer accstatus;
	private String lastlogindate;
	private String loginnum;

	// MemMemberinfo
	private String memid;
	private String memname;
	private String mobile;
	private Integer accbalance;
	private Integer scorebalance;
	private String accno;
	private String identification;
	private String mail;
	private String registerdate;
	private String registertype;
	private String headimg;
	private String sex;
	private String sexname;
	private Timestamp lupdatetime;
	private String createuser;
	private Timestamp createtime;
	private String lupdateuser;
	private String memnote;

	private String orgid;// 机构名称
	private String orgname;// 机构名称
	private String bkgroundpic;// 机构背景图
	private String logpic;// 机构logo
	private String orgno;// 企业标示号
	private String orgstatus;
	private String stcid;
	private String teacherid;// 教师id
	private String teachername;// 教师姓名
	private String teacherheadimg;// 教师头像

	private Integer accountstatus;// 账户类型 1，企业 2，教师 3，组织机构用户 4.特殊用户 5.普通用户

	public String getTeacherid() {
		return teacherid;
	}

	public void setTeacherid(String teacherid) {
		this.teacherid = teacherid;
	}

	/**
	 * @return the accid
	 */
	public String getAccid() {
		return this.accid;
	}

	/**
	 * @param accid
	 *            the accid to set
	 */
	public void setAccid(String accid) {
		this.accid = accid;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return this.nickname;
	}

	/**
	 * @param nickname
	 *            the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return the loginkey
	 */
	public String getLoginkey() {
		return this.loginkey;
	}

	/**
	 * @param loginkey
	 *            the loginkey to set
	 */
	public void setLoginkey(String loginkey) {
		this.loginkey = loginkey;
	}

	/**
	 * @return the acclogin
	 */
	public String getAcclogin() {
		return this.acclogin;
	}

	/**
	 * @param acclogin
	 *            the acclogin to set
	 */
	public void setAcclogin(String acclogin) {
		this.acclogin = acclogin;
	}

	/**
	 * @return the logintype
	 */
	public String getLogintype() {
		return this.logintype;
	}

	/**
	 * @param logintype
	 *            the logintype to set
	 */
	public void setLogintype(String logintype) {
		this.logintype = logintype;
	}

	/**
	 * @return the accstatus
	 */
	public Integer getAccstatus() {
		return this.accstatus;
	}

	/**
	 * @param accstatus
	 *            the accstatus to set
	 */
	public void setAccstatus(Integer accstatus) {
		this.accstatus = accstatus;
	}

	/**
	 * @return the lastlogindate
	 */
	public String getLastlogindate() {
		return this.lastlogindate;
	}

	/**
	 * @param lastlogindate
	 *            the lastlogindate to set
	 */
	public void setLastlogindate(String lastlogindate) {
		this.lastlogindate = lastlogindate;
	}

	/**
	 * @return the loginnum
	 */
	public String getLoginnum() {
		return this.loginnum;
	}

	/**
	 * @param loginnum
	 *            the loginnum to set
	 */
	public void setLoginnum(String loginnum) {
		this.loginnum = loginnum;
	}

	/**
	 * @return the memid
	 */
	public String getMemid() {
		return this.memid;
	}

	/**
	 * @param memid
	 *            the memid to set
	 */
	public void setMemid(String memid) {
		this.memid = memid;
	}

	/**
	 * @return the memname
	 */
	public String getMemname() {
		return this.memname;
	}

	/**
	 * @param memname
	 *            the memname to set
	 */
	public void setMemname(String memname) {
		this.memname = memname;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return this.mobile;
	}

	/**
	 * @param mobile
	 *            the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return the accbalance
	 */
	public Integer getAccbalance() {
		return this.accbalance;
	}

	/**
	 * @param accbalance
	 *            the accbalance to set
	 */
	public void setAccbalance(Integer accbalance) {
		this.accbalance = accbalance;
	}

	/**
	 * @return the scorebalance
	 */
	public Integer getScorebalance() {
		return this.scorebalance;
	}

	/**
	 * @param scorebalance
	 *            the scorebalance to set
	 */
	public void setScorebalance(Integer scorebalance) {
		this.scorebalance = scorebalance;
	}

	/**
	 * @return the accno
	 */
	public String getAccno() {
		return this.accno;
	}

	/**
	 * @param accno
	 *            the accno to set
	 */
	public void setAccno(String accno) {
		this.accno = accno;
	}

	/**
	 * @return the identification
	 */
	public String getIdentification() {
		return this.identification;
	}

	/**
	 * @param identification
	 *            the identification to set
	 */
	public void setIdentification(String identification) {
		this.identification = identification;
	}

	/**
	 * @return the mail
	 */
	public String getMail() {
		return this.mail;
	}

	/**
	 * @param mail
	 *            the mail to set
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * @return the registerdate
	 */
	public String getRegisterdate() {
		return this.registerdate;
	}

	/**
	 * @param registerdate
	 *            the registerdate to set
	 */
	public void setRegisterdate(String registerdate) {
		this.registerdate = registerdate;
	}

	/**
	 * @return the registertype
	 */
	public String getRegistertype() {
		return this.registertype;
	}

	/**
	 * @param registertype
	 *            the registertype to set
	 */
	public void setRegistertype(String registertype) {
		this.registertype = registertype;
	}

	/**
	 * @return the headimg
	 */
	public String getHeadimg() {
		return this.headimg;
	}

	/**
	 * @param headimg
	 *            the headimg to set
	 */
	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}

	/**
	 * @return the sex
	 */
	public String getSex() {
		return this.sex;
	}

	/**
	 * @param sex
	 *            the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * @return the sexname
	 */
	public String getSexname() {
		return this.sexname;
	}

	/**
	 * @param sexname
	 *            the sexname to set
	 */
	public void setSexname(String sexname) {
		this.sexname = sexname;
	}

	/**
	 * @return the lupdatetime
	 */
	public Timestamp getLupdatetime() {
		return this.lupdatetime;
	}

	/**
	 * @param lupdatetime
	 *            the lupdatetime to set
	 */
	public void setLupdatetime(Timestamp lupdatetime) {
		this.lupdatetime = lupdatetime;
	}

	/**
	 * @return the createuser
	 */
	public String getCreateuser() {
		return this.createuser;
	}

	/**
	 * @param createuser
	 *            the createuser to set
	 */
	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}

	/**
	 * @return the createtime
	 */
	public Timestamp getCreatetime() {
		return this.createtime;
	}

	/**
	 * @param createtime
	 *            the createtime to set
	 */
	public void setCreatetime(Timestamp createtime) {
		this.createtime = createtime;
	}

	/**
	 * @return the lupdateuser
	 */
	public String getLupdateuser() {
		return this.lupdateuser;
	}

	/**
	 * @param lupdateuser
	 *            the lupdateuser to set
	 */
	public void setLupdateuser(String lupdateuser) {
		this.lupdateuser = lupdateuser;
	}

	/**
	 * @return the memnote
	 */
	public String getMemnote() {
		return this.memnote;
	}

	/**
	 * @param memnote
	 *            the memnote to set
	 */
	public void setMemnote(String memnote) {
		this.memnote = memnote;
	}

	/**
	 * @return the orgid
	 */
	public String getOrgid() {
		return this.orgid;
	}

	/**
	 * @param orgid
	 *            the orgid to set
	 */
	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}

	/**
	 * @return the orgname
	 */
	public String getOrgname() {
		return this.orgname;
	}

	/**
	 * @param orgname
	 *            the orgname to set
	 */
	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}

	/**
	 * @return the bkgroundpic
	 */
	public String getBkgroundpic() {
		return this.bkgroundpic;
	}

	/**
	 * @param bkgroundpic
	 *            the bkgroundpic to set
	 */
	public void setBkgroundpic(String bkgroundpic) {
		this.bkgroundpic = bkgroundpic;
	}

	/**
	 * @return the logpic
	 */
	public String getLogpic() {
		return this.logpic;
	}

	/**
	 * @param logpic
	 *            the logpic to set
	 */
	public void setLogpic(String logpic) {
		this.logpic = logpic;
	}

	/**
	 * @return the orgno
	 */
	public String getOrgno() {
		return this.orgno;
	}

	/**
	 * @param orgno
	 *            the orgno to set
	 */
	public void setOrgno(String orgno) {
		this.orgno = orgno;
	}

	/**
	 * @return the orgstatus
	 */
	public String getOrgstatus() {
		return this.orgstatus;
	}

	/**
	 * @param orgstatus
	 *            the orgstatus to set
	 */
	public void setOrgstatus(String orgstatus) {
		this.orgstatus = orgstatus;
	}

	/**
	 * @return the stcid
	 */
	public String getStcid() {
		return this.stcid;
	}

	/**
	 * @param stcid
	 *            the stcid to set
	 */
	public void setStcid(String stcid) {
		this.stcid = stcid;
	}

	/**
	 * @return the teachername
	 */
	public String getTeachername() {
		return this.teachername;
	}

	/**
	 * @param teachername
	 *            the teachername to set
	 */
	public void setTeachername(String teachername) {
		this.teachername = teachername;
	}

	/**
	 * @return the teacherheadimg
	 */
	public String getTeacherheadimg() {
		return this.teacherheadimg;
	}

	/**
	 * @param teacherheadimg
	 *            the teacherheadimg to set
	 */
	public void setTeacherheadimg(String teacherheadimg) {
		this.teacherheadimg = teacherheadimg;
	}

	/**
	 * @return the accountstatus
	 */
	public Integer getAccountstatus() {
		return accountstatus;
	}

	/**
	 * @param accountstatus
	 *            the accountstatus to set
	 */
	public void setAccountstatus(Integer accountstatus) {
		this.accountstatus = accountstatus;
	}

}
