package com.luyouchina.comm.model;

import com.luyouchina.comm.model.Version;

public class CommBaseParam {

	// 每页条数
	public static final Integer rows = 10;
	// 最大所有
	public static final Integer maxrows = 100000;
	// substatus :状态 正常0 等待对方同意7 已移除9
	public static final Integer substatus_0 = 0;
	public static final Integer substatus_7 = 7;
	public static final Integer substatus_9 = 9;

	/**
	 * 系统版本
	 */
	public static final Version version = new Version("04", "v2.0.1.150520");
	// 子系统代码
	public static final String systemCode = "01";
	public static final String systemVersion = "0.1";

	public static final String STATUS_Z1 = "1";
	public static final String STATUS_0 = "0";
	public static final String STATUS_F1 = "-1";
	
	
	/**
	 *  账号类别
	 */
	public static final String TYPE_ORG = "orgno";
	public static final String TYPE_CUSTOM  = "custom";
	public static final String TYPE_TEACHER  = "teacher";

	/**
	 * 登录tgt开头
	 */
	public static final String LOGIN_BEGIN = "login_"; // 登录tgt开头
	
	/**
	 * 默认行业标示
	 */
	public static final String INDUSTRYNO = "G01";
	public static final String INDUSTRYNO_TYPE = "cls_";

	public static final String INDUSTRYNO_C = "C01";
	public static final String INDUSTRYNO_Q = "Q01";

	// 默认
	public static final int MORENROWS = 2;
	public static final int MORENPAGE = 1;
	public static final int MORENBIGSIZE = 1000000;

	// 系统参数
	public static final String sysParam = "sysParam";
	// 当前页
	public static final int PAGENO = 1;
	// 每页条数
	public static final int ROWS = 10;
	// 删除
	public static final int ISDELETE_LING = 0;
	public static final int ISDELETE_JIU = 9;
	// 状态
	public static final int STATUS_LING = 0;
	public static final int STATUS_JIU = 9;

	public static final String pay_key = "luyou_2015";
	public static final String charset = "UTF-8";
	// public static final String IMGCODE="acf95b4ca29b4316abc8ec9899f6c1c5";
	public static final String IMGCODE = "IMGCODE";
	/**
	 * session 用户
	 */
	public static final String sessionUser = "loginUser";
	public static final String sessionTeacher = "teacherInfo";
	public static final String sessionORGID = "ORGID";
	public static final String sessionORG = "ORG";


}