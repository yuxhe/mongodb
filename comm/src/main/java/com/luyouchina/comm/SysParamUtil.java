package com.luyouchina.comm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.luyouchina.comm.model.Origin;
import com.mongodb.util.JSON;

/**
 * 系统参数相关
 * 
 * @author lfj
 *
 */
public class SysParamUtil {

	private static final Logger log = LoggerFactory.getLogger(SysParamUtil.class);

	private static ShardedJedisCommands shardedJedisConn = new ShardedJedisCommands();// 缓存连接

	/**
	 * 加载系统参数
	 * 
	 * @param origin
	 */
	// @SuppressWarnings("unchecked")
	@Deprecated
	public static void loadSysParam(final Origin origin) {
		log.info("开始获取系统参数................");
		/*
		 * Map<String, Object> param = new HashMap<String, Object>();
		 * 
		 * ResponseObject response = MQUtil.call("common", "getSysParamlist", param, origin);
		 * 
		 * if (response.getErrorMsg() != null && response.getErrorMsg().getId() != null) {
		 * log.error("加载系统参数出错：{}", response.getErrorMsg().getMsg());
		 * return;
		 * }
		 * 
		 * List<Map<String, Object>> list = (List<Map<String, Object>>) response.getData();
		 * 
		 * for (Map<String, Object> map : list) {
		 * String code = (String) map.get("sysparamcode");
		 * SysParam.put(code, map);
		 * log.info("加载系统参数{}={}", code, JsonUtil.toJson(map));
		 * }
		 */
		log.info("获取系统参数成功................");
	}

	/**
	 * 获取所有系统参数
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Map<String, Object>> getAllSysParam() {
		Map<String, String> value = shardedJedisConn.hgetAll("sysparam");
		if (value != null && !value.isEmpty()) {
			Map<String, Map<String, Object>> data = new HashMap<String, Map<String, Object>>();

			Set<String> keys = value.keySet();
			for (String key : keys) {
				data.put(key, (Map<String, Object>) JSON.parse(value.get(key)));
			}

			return data;
		} else {
			return null;
		}
	}

	/**
	 * 根据code获取系统参数
	 * 
	 * @param code
	 *            系统参数code
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getSysParam(String code) {
		String value = shardedJedisConn.hget("sysparam", code);
		if (value != null) {
			Map<String, Object> map = (Map<String, Object>) JSON.parse(value);
			return (String) map.get("sysparamvalue");
		} else {
			return null;
		}
	}

	/**
	 * 加载业务参数
	 */
	// @SuppressWarnings("unchecked")
	@Deprecated
	public static void loadBusParam(final Origin origin) {
		log.info("开始获取业务参数................");
		// Map<String, Object> param = new HashMap<String, Object>();
		// param.put("type", "A");
		//
		// ResponseObject response = MQUtil.call("common", "getBusParam", param, origin);
		//
		// if (response.getErrorMsg() != null && response.getErrorMsg().getId() != null) {
		// log.error("加载业务参数出错：{}", response.getErrorMsg().getMsg());
		// return;
		// }
		//
		// List<Map<String, Object>> list = (List<Map<String, Object>>) response.getData();
		//
		// for (Map<String, Object> map : list) {
		// String busparamcode = (String) map.get("busparamcode");
		// String parbusparamcode = (String) map.get("parbusparamcode");
		//
		// busParam.put(busparamcode, map);
		//
		// if (StringUtil.isNotEmpty(parbusparamcode)) {
		// List<Map<String, Object>> children = parentBusParam.get(parbusparamcode);
		// if (children == null) {
		// children = new ArrayList<Map<String, Object>>();
		// }
		// children.add(map);
		// parentBusParam.put(parbusparamcode, children);
		// } else {
		// parentBusParam.put(parbusparamcode, new ArrayList<Map<String, Object>>());
		// }
		//
		// log.info("加载业务参数{}={}", busparamcode, JsonUtil.toJson(map));
		// }
		log.info("获取业务参数成功................");
	}

	/**
	 * 根据code获取业务参数
	 * 
	 * @param busparamcode
	 * @return
	 */
	public static Map<String, Object> getBusParam(String busparamcode) {
		Map<String, Object> data;
		String json = shardedJedisConn.hget("busparameter", busparamcode);
		if (StringUtil.isEmpty(json)) {
			return null;
		}
		data = JsonUtil.fromJson(json, new TypeReference<Map<String, Object>>() {
		});

		return data;
	}

	/**
	 * 获取多个系统参数.例如：getBusParam("code1","code2",...);
	 * 
	 * @param busparamcode
	 * @return {"code1":{...},"code2":{...},...}
	 */
	public static Map<String, Object> getBusParam(String... busparamcodes) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (String busparamcode : busparamcodes) {
			Map<String, Object> data;
			String json = shardedJedisConn.hget("busparameter", busparamcode);
			if (StringUtil.isEmpty(json)) {
				return null;
			}
			data = JsonUtil.fromJson(json, new TypeReference<Map<String, Object>>() {
			});
			map.put(busparamcode, data);
		}

		return map;
	}

	/**
	 * 根据父code获取列表
	 * 
	 * @param parbusparamcode
	 * @return
	 */
	public static List<Map<String, Object>> getBusParamList(String parbusparamcode) {
		List<Map<String, Object>> data;
		String json = shardedJedisConn.hget("parent_busparameter", parbusparamcode);
		if (StringUtil.isEmpty(json)) {
			return null;
		}
		data = JsonUtil.fromJson(json, new TypeReference<List<Map<String, Object>>>() {
		});

		return data;
	}

}
