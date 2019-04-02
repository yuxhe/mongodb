/**
 *
 */
package com.luyouchina.comm.model;

import java.math.BigDecimal;

/**
 * @author gzg
 */
public class MemOrganization {
	private String orgid;
	private String accno;
	private String orgname;
	private String industryno;
	private String[] bkgroundpic;
	private String logpic;
	private String orgbarcode;
	private String orgbarcodepic;
	private String businesslic;
	private String irspic;
	private String taxpic;
	private String orgstatus;
	private BigDecimal accbalance;
	private BigDecimal scorebalance;
	private String orgtype;
	private String orgcontact;
	private String contactmobile;
	private String legalperson;
	private String city;
	private String address;
	private String createuser;
	private String createtime;
	private String lupdateuser;
	private String lupdatetime;
	private String orgdesc;

	private String provinces;
	private String citys;
	private String districts;

	private String stcid;
	private Integer isdelete;
	private String orgno;// 企业标示号

	// 复制copy
	private String provincesc;
	private String citysc;
	private String districtsc;

	// add by lipx 20151025 新增座机号
	private String contactphone;

	public String getOrgno() {
		return orgno;
	}

	public void setOrgno(String orgno) {
		this.orgno = orgno;
	}

	public String getStcid() {
		return stcid;
	}

	public void setStcid(String stcid) {
		this.stcid = stcid;
	}

	public Integer getIsdelete() {
		return isdelete;
	}

	public void setIsdelete(Integer isdelete) {
		this.isdelete = isdelete;
	}

	/**
	 * @return the provincesc
	 */
	public String getProvincesc() {
		return provincesc;
	}

	/**
	 * @param provincesc
	 *            the provincesc to set
	 */
	public void setProvincesc(String provincesc) {
		this.provincesc = provincesc;
	}

	/**
	 * @return the citysc
	 */
	public String getCitysc() {
		return citysc;
	}

	/**
	 * @param citysc
	 *            the citysc to set
	 */
	public void setCitysc(String citysc) {
		this.citysc = citysc;
	}

	/**
	 * @return the districtsc
	 */
	public String getDistrictsc() {
		return districtsc;
	}

	/**
	 * @param districtsc
	 *            the districtsc to set
	 */
	public void setDistrictsc(String districtsc) {
		this.districtsc = districtsc;
	}

	public String getProvinces() {
		return provinces;
	}

	public void setProvinces(String provinces) {
		this.provinces = provinces;
	}

	public String getCitys() {

		return citys;
	}

	public void setCitys(String citys) {
		this.citys = citys;
	}

	public String getDistricts() {
		return districts;
	}

	public void setDistricts(String districts) {
		this.districts = districts;
	}


	/**
	 * @return the orgid
	 */
	public String getOrgid() {
		return orgid;
	}

	/**
	 * @param orgid
	 *            the orgid to set
	 */
	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}

	/**
	 * @return the accno
	 */
	public String getAccno() {
		return accno;
	}

	/**
	 * @param accno
	 *            the accno to set
	 */
	public void setAccno(String accno) {
		this.accno = accno;
	}

	/**
	 * @return the orgname
	 */
	public String getOrgname() {
		return orgname;
	}

	/**
	 * @param orgname
	 *            the orgname to set
	 */
	public void setOrgname(String orgname) {
		this.orgname = orgname;
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
	 * @return the bkgroundpic
	 */
	public String[] getBkgroundpic() {
		return bkgroundpic;
	}

	/**
	 * @param bkgroundpic
	 *            the bkgroundpic to set
	 */
	public void setBkgroundpic(String[] bkgroundpic) {
		this.bkgroundpic = bkgroundpic;
	}

	/**
	 * @return the logpic
	 */
	public String getLogpic() {
		return logpic;
	}

	/**
	 * @param logpic
	 *            the logpic to set
	 */
	public void setLogpic(String logpic) {
		this.logpic = logpic;
	}

	/**
	 * @return the orgbarcode
	 */
	public String getOrgbarcode() {
		return orgbarcode;
	}

	/**
	 * @param orgbarcode
	 *            the orgbarcode to set
	 */
	public void setOrgbarcode(String orgbarcode) {
		this.orgbarcode = orgbarcode;
	}

	/**
	 * @return the orgbarcodepic
	 */
	public String getOrgbarcodepic() {
		return orgbarcodepic;
	}

	/**
	 * @param orgbarcodepic
	 *            the orgbarcodepic to set
	 */
	public void setOrgbarcodepic(String orgbarcodepic) {
		this.orgbarcodepic = orgbarcodepic;
	}

	/**
	 * @return the businesslic
	 */
	public String getBusinesslic() {
		return businesslic;
	}

	/**
	 * @param businesslic
	 *            the businesslic to set
	 */
	public void setBusinesslic(String businesslic) {
		this.businesslic = businesslic;
	}

	/**
	 * @return the irspic
	 */
	public String getIrspic() {
		return irspic;
	}

	/**
	 * @param irspic
	 *            the irspic to set
	 */
	public void setIrspic(String irspic) {
		this.irspic = irspic;
	}

	/**
	 * @return the taxpic
	 */
	public String getTaxpic() {
		return taxpic;
	}

	/**
	 * @param taxpic
	 *            the taxpic to set
	 */
	public void setTaxpic(String taxpic) {
		this.taxpic = taxpic;
	}

	/**
	 * @return the orgstatus
	 */
	public String getOrgstatus() {
		return orgstatus;
	}

	/**
	 * @param orgstatus
	 *            the orgstatus to set
	 */
	public void setOrgstatus(String orgstatus) {
		this.orgstatus = orgstatus;
	}

	/**
	 * @return the accbalance
	 */
	public BigDecimal getAccbalance() {
		return accbalance;
	}

	/**
	 * @param accbalance
	 *            the accbalance to set
	 */
	public void setAccbalance(BigDecimal accbalance) {
		this.accbalance = accbalance;
	}

	/**
	 * @return the scorebalance
	 */
	public BigDecimal getScorebalance() {
		return scorebalance;
	}

	/**
	 * @param scorebalance
	 *            the scorebalance to set
	 */
	public void setScorebalance(BigDecimal scorebalance) {
		this.scorebalance = scorebalance;
	}

	/**
	 * @return the orgtype
	 */
	public String getOrgtype() {
		return orgtype;
	}

	/**
	 * @param orgtype
	 *            the orgtype to set
	 */
	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}

	/**
	 * @return the orgcontact
	 */
	public String getOrgcontact() {
		return orgcontact;
	}

	/**
	 * @param orgcontact
	 *            the orgcontact to set
	 */
	public void setOrgcontact(String orgcontact) {
		this.orgcontact = orgcontact;
	}

	/**
	 * @return the contactmobile
	 */
	public String getContactmobile() {
		return contactmobile;
	}

	/**
	 * @param contactmobile
	 *            the contactmobile to set
	 */
	public void setContactmobile(String contactmobile) {
		this.contactmobile = contactmobile;
	}

	public String getContactphone() {
		return contactphone;
	}

	public void setContactphone(String contactphone) {
		this.contactphone = contactphone;
	}

	/**
	 * @return the legalperson
	 */
	public String getLegalperson() {
		return legalperson;
	}

	/**
	 * @param legalperson
	 *            the legalperson to set
	 */
	public void setLegalperson(String legalperson) {
		this.legalperson = legalperson;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		// StringBuffer temp = new StringBuffer();
		// if (provinces != null) {
		// temp.append(provinces+",");
		// }
		// if (citys != null) {
		// temp.append(citys+",");
		// }
		// if (districts != null) {
		// temp.append(districts);
		// }
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		/*
		 * if (city != null) { String[] temp = city.split(","); if (temp.length
		 * == 1) { provinces = temp[0]; } if (temp.length == 2) { citys =
		 * temp[1]; } if (temp.length == 3) { districts = temp[2]; } }
		 */
		this.city = city;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the createuser
	 */
	public String getCreateuser() {
		return createuser;
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
	public String getCreatetime() {
		return createtime;
	}

	/**
	 * @param createtime
	 *            the createtime to set
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
	 * @param lupdateuser
	 *            the lupdateuser to set
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
	 * @param lupdatetime
	 *            the lupdatetime to set
	 */
	public void setLupdatetime(String lupdatetime) {
		this.lupdatetime = lupdatetime;
	}

	public String getOrgdesc() {
		return orgdesc;
	}

	public void setOrgdesc(String orgdesc) {
		this.orgdesc = orgdesc;
	}

}
