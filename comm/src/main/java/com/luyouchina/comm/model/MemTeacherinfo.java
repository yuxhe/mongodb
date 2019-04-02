/**
*
*/
package com.luyouchina.comm.model;

import java.io.Serializable;

/**
 * @author gzg
 */
public class MemTeacherinfo implements Serializable{
	private static final long serialVersionUID = -6069144778391793494L;
	
	private String teacherid;
	private String accno;
	private String orgid;
	private String teachername;
	private String teachenote;
	private String teatitle;
	private String teaposition;
	private String headimg;
	private String registerdate;
	private String registertype;
	private String createuser;
	private String createtime;
	private String lupdateuser;
	private String lupdatetime;
	
	private String mobile;
	private String password;
	
	private Integer teachstatus;
	
	
	
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return the orgid
	 */
	public String getOrgid() {
		return orgid;
	}
	/**
	 * @param orgid the orgid to set
	 */
	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}
	/**
	 * @return the teacherid
	 */
	public String getTeacherid() {
		return teacherid;
	}
	/**
	 * @param teacherid the teacherid to set
	 */
	public void setTeacherid(String teacherid) {
		this.teacherid = teacherid;
	}
	/**
	 * @return the accno
	 */
	public String getAccno() {
		return accno;
	}
	/**
	 * @param accno the accno to set
	 */
	public void setAccno(String accno) {
		this.accno = accno;
	}
	/**
	 * @return the teachername
	 */
	public String getTeachername() {
		return teachername;
	}
	/**
	 * @param teachername the teachername to set
	 */
	public void setTeachername(String teachername) {
		this.teachername = teachername;
	}
	/**
	 * @return the teatitle
	 */
	public String getTeatitle() {
		return teatitle;
	}
	/**
	 * @param teatitle the teatitle to set
	 */
	public void setTeatitle(String teatitle) {
		this.teatitle = teatitle;
	}
	/**
	 * @return the teaposition
	 */
	public String getTeaposition() {
		return teaposition;
	}
	/**
	 * @param teaposition the teaposition to set
	 */
	public void setTeaposition(String teaposition) {
		this.teaposition = teaposition;
	}
	/**
	 * @return the registerdate
	 */
	public String getRegisterdate() {
		return registerdate;
	}
	/**
	 * @param registerdate the registerdate to set
	 */
	public void setRegisterdate(String registerdate) {
		this.registerdate = registerdate;
	}
	/**
	 * @return the registertype
	 */
	public String getRegistertype() {
		return registertype;
	}
	/**
	 * @param registertype the registertype to set
	 */
	public void setRegistertype(String registertype) {
		this.registertype = registertype;
	}
	/**
	 * @return the createuser
	 */
	public String getCreateuser() {
		return createuser;
	}
	/**
	 * @param createuser the createuser to set
	 */
	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}
	/**
	 * @return the createtime
	 */
	public String getCreatetime() {
		return createtime;
	}
	/**
	 * @param createtime the createtime to set
	 */
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	/**
	 * @return the lupdateuser
	 */
	public String getLupdateuser() {
		return lupdateuser;
	}
	/**
	 * @param lupdateuser the lupdateuser to set
	 */
	public void setLupdateuser(String lupdateuser) {
		this.lupdateuser = lupdateuser;
	}
	/**
	 * @return the lupdatetime
	 */
	public String getLupdatetime() {
		return lupdatetime;
	}
	/**
	 * @param lupdatetime the lupdatetime to set
	 */
	public void setLupdatetime(String lupdatetime) {
		this.lupdatetime = lupdatetime;
	}
	public String getTeachenote() {
		return teachenote;
	}
	public void setTeachenote(String teachenote) {
		this.teachenote = teachenote;
	}
	public String getHeadimg() {
		return headimg;
	}
	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}
	/**
	 * @return the teachstatus
	 */
	public Integer getTeachstatus() {
		return teachstatus;
	}
	/**
	 * @param teachstatus the teachstatus to set
	 */
	public void setTeachstatus(Integer teachstatus) {
		this.teachstatus = teachstatus;
	}
	
	

}
