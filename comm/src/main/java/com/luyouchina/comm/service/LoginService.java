package com.luyouchina.comm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AssertionHolder;
import org.jasig.cas.client.validation.Assertion;

import com.alibaba.fastjson.JSON;
import com.luyouchina.comm.IndustryUtil;
import com.luyouchina.comm.JsonUtil;
import com.luyouchina.comm.MQUtil;
import com.luyouchina.comm.ResponseDto;
import com.luyouchina.comm.StringUtil;
import com.luyouchina.comm.model.CommBaseParam;
import com.luyouchina.comm.model.CommonFunction;
import com.luyouchina.comm.model.MemOrganization;
import com.luyouchina.comm.model.MemTeacherinfo;
import com.luyouchina.comm.model.NewLoginUser;
import com.luyouchina.comm.model.Origin;
import com.luyouchina.comm.model.ResponseObject;

/**
 * @author : zhuyu
 * @version : 1.00
 * @create time : 2015年10月29日上午9:10:51
 * @description :
 * @History :
 */
public class LoginService {

	/**
	 * 用户信息存放session
	 * 
	 * @param request
	 */
	public void setSession(HttpServletRequest request) {
		HttpSession session = request.getSession();

		// 获取登录用户数据
		Assertion obj = AssertionHolder.getAssertion();
		if (obj != null) {
			AttributePrincipal principal = obj.getPrincipal();
			// 放入session
			NewLoginUser user = JSON.parseObject(JsonUtil.toJson(principal.getAttributes()), NewLoginUser.class);
			// 获取行业标示号
			String industryno = IndustryUtil.getIndustryno(request, CommBaseParam.INDUSTRYNO);
			// 获取账号信息
			String account = getAccountStatus(user.getAccno());
			if(account != null){
				// 账号被禁用 
				if(account.equals("false")){
					user.setAccstatus(9);
					session.setAttribute(CommBaseParam.sessionUser, user);
					return;
				}
			}
			
			
			String logintype = user.getLogintype();
			// 老数据处理
			if (StringUtil.isEmpty(logintype)) {
				// 获取机构信息
				ResponseDto dto = getOrganization(user.getAccno(), industryno);
				if (CommonFunction.isNotNull(dto.getData())) {
					MemOrganization org = JSON.parseObject(JsonUtil.toJson(dto.getData()), MemOrganization.class);
					session.setAttribute(CommBaseParam.sessionORG, org);
					user.setOrgname(org.getOrgname());
					user.setOrgid(org.getOrgid());
					user.setOrgno(org.getOrgno());
					user.setLogpic(org.getLogpic());
					user.setOrgstatus(org.getOrgstatus());
					user.setStcid(org.getStcid());
					user.setAccountstatus(1);
				}

				if (user.getAccountstatus() == null) {
					// 获取教师信息
					dto = getTeacherdetail(user.getAccno());
					if (CommonFunction.isNotNull(dto.getData())) {
						MemTeacherinfo teacherinfo = JSON.parseObject(JsonUtil.toJson(dto.getData()), MemTeacherinfo.class);
						// 获取机构信息
						ResponseDto temp = getOrganizationdetail(teacherinfo.getOrgid());
						MemOrganization org = JSON.parseObject(JsonUtil.toJson(temp.getData()), MemOrganization.class);
//						if (industryno.equals(org.getIndustryno())) {
							session.setAttribute(CommBaseParam.sessionTeacher, teacherinfo);
							user.setTeacherid(teacherinfo.getTeacherid());
							user.setTeacherheadimg(teacherinfo.getHeadimg());
							user.setTeachername(teacherinfo.getTeachername());
							user.setAccountstatus(2);
							session.setAttribute(CommBaseParam.sessionORG, org);
							user.setOrgname(org.getOrgname());
							user.setOrgid(org.getOrgid());
							user.setOrgno(org.getOrgno());
							user.setLogpic(org.getLogpic());
							user.setOrgstatus(org.getOrgstatus());
							user.setStcid(org.getStcid());
//						}
					}
				}

				if (user.getAccountstatus() == null) {
					// 获取组织机构
					dto = getCustomOrg(user.getAccno());
					if (CommonFunction.isNotNull(dto.getData())) {
						MemOrganization org = JSON.parseObject(JsonUtil.toJson(dto.getData()), MemOrganization.class);
						session.setAttribute(CommBaseParam.sessionORG, org);
						user.setOrgname(org.getOrgname());
						user.setOrgid(org.getOrgid());
						user.setOrgno(org.getOrgno());
						user.setLogpic(org.getLogpic());
						user.setOrgstatus(org.getOrgstatus());
						user.setStcid(org.getStcid());
						user.setAccountstatus(3);
					}
				}
			} else {
				// 企业
				if (user.getLogintype().equals(CommBaseParam.TYPE_ORG)) {
					ResponseDto dto = getOrganization(user.getAccno(), industryno);
					if (CommonFunction.isNotNull(dto.getData())) {
						MemOrganization org = JSON.parseObject(JsonUtil.toJson(dto.getData()), MemOrganization.class);
						session.setAttribute(CommBaseParam.sessionORG, org);
						user.setOrgname(org.getOrgname());
						user.setOrgid(org.getOrgid());
						user.setOrgno(org.getOrgno());
						user.setLogpic(org.getLogpic());
						user.setOrgstatus(org.getOrgstatus());
						user.setStcid(org.getStcid());
						user.setAccountstatus(1);
					}
				}

				// 教师
				if (user.getLogintype().equals(CommBaseParam.TYPE_TEACHER)) {
					ResponseDto dto = getTeacherdetail(user.getAccno());
					if (CommonFunction.isNotNull(dto.getData())) {
						MemTeacherinfo teacherinfo = JSON.parseObject(JsonUtil.toJson(dto.getData()), MemTeacherinfo.class);
						// 获取机构信息
						ResponseDto temp = getOrganizationdetail(teacherinfo.getOrgid());
						MemOrganization org = JSON.parseObject(JsonUtil.toJson(temp.getData()), MemOrganization.class);
//						if (industryno.equals(org.getIndustryno())) {
							session.setAttribute(CommBaseParam.sessionTeacher, teacherinfo);
							user.setTeacherid(teacherinfo.getTeacherid());
							user.setTeacherheadimg(teacherinfo.getHeadimg());
							user.setTeachername(teacherinfo.getTeachername());
							user.setAccountstatus(2);
							session.setAttribute(CommBaseParam.sessionORG, org);
							user.setOrgname(org.getOrgname());
							user.setOrgid(org.getOrgid());
							user.setOrgno(org.getOrgno());
							user.setLogpic(org.getLogpic());
							user.setOrgstatus(org.getOrgstatus());
							user.setStcid(org.getStcid());
//						}
					}
				}

				// 机构下属
				// if (user.getLogintype().equals(CommBaseParam.TYPE_CUSTOM)) {
				if (user.getAccountstatus() == null) {
					ResponseDto dto = getCustomOrg(user.getAccno());
					if (CommonFunction.isNotNull(dto.getData())) {
						MemOrganization org = JSON.parseObject(JsonUtil.toJson(dto.getData()), MemOrganization.class);
//						if (industryno.equals(org.getIndustryno())) {
							session.setAttribute(CommBaseParam.sessionORG, org);
							user.setOrgname(org.getOrgname());
							user.setOrgid(org.getOrgid());
							user.setOrgno(org.getOrgno());
							user.setLogpic(org.getLogpic());
							user.setOrgstatus(org.getOrgstatus());
							user.setStcid(org.getStcid());
							user.setAccountstatus(3);
//						}
					}
				}
			}

			// 判断是否为特殊用户
			if (user.getAccountstatus() == null) {
				Map<String, Object> mem = getMemdetail(user.getAccno());
				if (mem == null) {
					user.setAccountstatus(4);
				} else {
					user.setAccountstatus(5);
				}
			}
			// 用户信息放入
			session.setAttribute(CommBaseParam.sessionUser, user);
		} else {
			session.removeAttribute(CommBaseParam.sessionUser);
			session.removeAttribute(CommBaseParam.sessionORG);
			session.removeAttribute(CommBaseParam.sessionTeacher);
		}
	}


	/**
	 * @author : zhuyu
	 * @version : 1.00
	 * @create time : 2015年10月28日下午7:29:43
	 * @param accno
	 * @param industryno
	 * @return ResponseDto
	 * @description : 获取机构（新）
	 */
	private ResponseDto getOrganization(String accno, String industryno) {
		ResponseObject object = new ResponseObject();
		try {
			Origin origin = new Origin(CommBaseParam.systemCode, CommBaseParam.systemVersion);
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("accno", accno);
//			param.put("industryno", industryno);
			param.put("page", CommBaseParam.PAGENO);
			param.put("rows", CommBaseParam.MORENBIGSIZE);
			object = MQUtil.call("mem", "organizationlist", param, origin);
			if (CommonFunction.errorMsgIdIsNull(object)) {
				Map<String, Object> dataMap = (Map<String, Object>) object.getData();
				List<Map<String, Object>> list = (List<Map<String, Object>>) dataMap.get("list");
				if (CommonFunction.isNotNull(list) && list.size() > 0) {
					ResponseDto responseDto = new ResponseDto();
					responseDto.setData(list.get(0));
					responseDto.setMsg("请求处理成功");
					responseDto.setStatus(CommBaseParam.STATUS_Z1);
					return responseDto;
				} else {
					ResponseDto responseDto = new ResponseDto();
					responseDto.setData(null);
					responseDto.setMsg("请求处理成功");
					responseDto.setStatus(CommBaseParam.STATUS_Z1);
					return responseDto;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseDto(object);
	}

	/**
	 * @author : zhuyu
	 * @version : 1.00
	 * @create time : 2015年10月28日下午7:33:19
	 * @param accno
	 * @return ResponseDto
	 * @description : 获取教师（新）
	 */
	private ResponseDto getTeacherdetail(String accno) {
		ResponseObject object = new ResponseObject();
		try {
			Origin origin = new Origin(CommBaseParam.systemCode, CommBaseParam.systemVersion);
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("accno", accno);
			param.put("teachstatus", 0);
			object = MQUtil.call("mem", "getTeacherDetail", param, origin);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseDto(object);
	}


	/**
	 * @author : zhuyu
	 * @version : 1.00
	 * @create time : 2015年10月28日下午7:37:14
	 * @param accno
	 * @return ResponseDto
	 * @description : 获取组织架构 人员 机构
	 */
	private ResponseDto getCustomOrg(String accno) {
		ResponseObject object = new ResponseObject();
		try {
			Origin origin = new Origin(CommBaseParam.systemCode, CommBaseParam.systemVersion);
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("accno", accno);
			object = MQUtil.call("org", "getCustomOrg", param, origin);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseDto(object);
	}

	/**
	 * @author : zhuyu
	 * @version : 1.00
	 * @create time : 2015年11月2日下午4:23:21
	 * @param orgid
	 * @return ResponseDto
	 * @description : 根据机构id获取机构信息
	 */
	private ResponseDto getOrganizationdetail(String orgid) {
		ResponseObject object = new ResponseObject();
		try {
			Origin origin = new Origin(CommBaseParam.systemCode, CommBaseParam.systemVersion);
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("orgid", orgid);
			object = MQUtil.call("mem", "organizationdetail", param, origin);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseDto(object);
	}

	/**
	 * @author : zhuyu
	 * @version : 1.00
	 * @create time : 2015年11月3日上午9:36:51
	 * @param accno
	 * @return Map<String,Object>
	 * @description : 获取用户信息
	 */
	private Map<String, Object> getMemdetail(String accno) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("accno", accno);
		param.put("flush", 1);
		Origin origin = new Origin(CommBaseParam.systemCode, CommBaseParam.systemVersion);
		ResponseObject reso = MQUtil.call("mem", "memdetail", param, origin);
		if (reso != null && reso.getErrorMsg() != null && reso.getErrorMsg().getId() != null) {
			return null;
		}
		return (Map<String, Object>) reso.getData();
	}

	
	/** 
	 * @author : zhuyu
	 * @version : 1.00
	 * @create time : 2015年12月23日下午2:27:27 
	 * @param accno
	 * @return Map<String,Object> 
	 * @description : 获取账号状态
	*/	
	private String getAccountStatus(String accno) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("accno", accno);
		Origin origin = new Origin(CommBaseParam.systemCode, CommBaseParam.systemVersion);
		ResponseObject reso = MQUtil.call("mem", "checkAccountStatus", param, origin);
		if (reso != null && reso.getErrorMsg() != null && reso.getErrorMsg().getId() != null) {
			return null;
		}
		return (String) reso.getData();
	}
}
