package com.luyouchina.comm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.luyouchina.comm.model.Origin;
import com.luyouchina.comm.model.ResponseObject;
import com.luyouchina.comm.model.Version;

/**
 * @author lifajun
 *
 */
public class IndustryUtil {

	private static final Logger log = LoggerFactory.getLogger(IndustryUtil.class);

	public static Map<String, Map<String, Object>> industryMap = new HashMap<String, Map<String, Object>>();

	private static ShardedJedisCommands shardedJedisConn = new ShardedJedisCommands();// 缓存连接

	/**
	 * 加载行业数据
	 * 
	 * @param origin
	 */
	@SuppressWarnings("unchecked")
	public static void loadIndustry(final Origin origin) {
		log.debug("开始获取行业................");
		Map<String, String> industryinfo = shardedJedisConn.hgetAll("industryinfo");
		if (industryinfo != null) {
			for (String key : industryinfo.keySet()) {
				Map<String, Object> map = JsonUtil.fromJson(industryinfo.get(key), new TypeReference<Map<String, Object>>() {
				});
				industryMap.put(key, map);
			}
		} else {
			Map<String, Object> param = new HashMap<String, Object>();
			ResponseObject response = MQUtil.call("common", "getIndustrylist", param, origin);

			if (response.getErrorMsg() != null && response.getErrorMsg().getId() != null) {
				log.error("加载行业出错：{}", response.getErrorMsg().getMsg());
				return;
			}

			List<Map<String, Object>> list = (List<Map<String, Object>>) response.getData();

			for (Map<String, Object> map : list) {
				String domainname = (String) map.get("domainname");
				industryMap.put(domainname, map);
				log.debug("加载行业:{}", JsonUtil.toJson(map));
			}
		}

		log.debug("获取行业成功................");

	}

	/**
	 * 获取请求所属行业
	 * 
	 * @param request
	 * @return
	 */
	public static String getIndustryno(HttpServletRequest request) {
		Origin origin = new Origin(new Version("comm", "0.0.1"));
		origin.setReqaccno("0000000000");
		origin.setReqaccname("0000000000");
		origin.setDescription("加载行业");
		loadIndustry(origin);
		StringBuffer url = request.getRequestURL();
		String domainname = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
		// log.info("获取{}对应行业................", domainname);
		// Map<String, Object> industry = industryMap.get(domainname);
		// if (industry != null) {
		// return (String) industry.get("industryno");
		// }
		String urls = request.getHeader("Referer");
		log.debug("***************获取的访问地址为" + domainname);
		log.debug("***************获取的访问地址为" + urls);
		log.debug("***************获取的地址为" + request.getRequestURL());
		String industryno = null;
		if (StringUtil.isNoneEmpty(domainname)) {
			Iterator<String> keys = IndustryUtil.industryMap.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				if (key.equals("")) {
					continue;
				}
				if (domainname.startsWith(key)) {
					Map<String, Object> industry = IndustryUtil.industryMap.get(key);
					if (industry != null) {
						industryno = (String) industry.get("industryno");
						break;
					}
				}
			}
		}
		return industryno;
	}

	/**
	 * 获取请求域名对应行业
	 * 
	 * @param request
	 * @param defaultIndustry
	 *            默认行业
	 * @return
	 */
	public static String getIndustryno(HttpServletRequest request, String defaultIndustry) {
		String industry = getIndustryno(request);
		if (StringUtil.isNotEmpty(industry)) {
			return industry;
		}
		return defaultIndustry;
	}

	/**
	 * 获取公共行业
	 * 
	 * @return
	 */
	public static List<String> getPublicIndustry() {
		if (industryMap.isEmpty()) {
			Origin origin = new Origin(new Version("comm", "0.0.1"));
			origin.setReqaccno("0000000000");
			origin.setReqaccname("0000000000");
			origin.setDescription("加载行业");
			loadIndustry(origin);
		}
		List<String> industryno = new ArrayList<String>();

		Set<String> keys = industryMap.keySet();

		for (String key : keys) {
			Map<String, Object> industry = industryMap.get(key);
			if (0 == (Integer) industry.get("iscomm")) {
				industryno.add((String) industry.get("industryno"));
			}
		}

		return industryno;
	}

	/** 
	 * @author : zhuyu
	 * @version : 1.00
	 * @create time : 2015年9月6日下午5:01:24 
	 * @param url
	 * @param defaultIndustry
	 * @return String 
	 * @description : 根据URL路径获取行业标识号
	*/	
	public static String getIndustrynoByUrl(String url, String defaultIndustry) {
		Origin origin = new Origin(new Version("comm", "0.0.1"));
		origin.setReqaccno("0000000000");
		origin.setReqaccname("0000000000");
		origin.setDescription("加载行业");
		loadIndustry(origin);
		String industryno = null;
		if (StringUtil.isNoneEmpty(url)) {
			Iterator<String> keys = IndustryUtil.industryMap.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				if (key.equals("")) {
					continue;
				}
				if (url.startsWith(key)) {
					Map<String, Object> industry = IndustryUtil.industryMap.get(key);
					if (industry != null) {
						industryno = (String) industry.get("industryno");
						break;
					}
				}
			}
		}
		
		if (StringUtil.isNotEmpty(industryno)) {
			return industryno;
		}
		return defaultIndustry;
	}
	
}
