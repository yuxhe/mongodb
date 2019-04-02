/**
 * 
 */
package com.luyouchina.comm;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数验证
 * 
 * @author lfj
 *
 */
public class ParamUtil {

	/**
	 * 参数传入 前端参数 param ,传入待验证的必传的参数，按数组方式传入
	 * 
	 * @param param
	 * @param keys
	 * @return
	 */
	public static String RequiredValid(Map<String, Object> param, String... keys) {
		String MissParam = "";
		for (int i = 0; i < keys.length; i++) {
			if (!param.containsKey(keys[i])) {
				MissParam = "出错啦,缺少参数[".concat(keys[i]).concat("]");
				break;
			} else if (param.get(keys[i]) == null || "".equals(param.get(keys[i]))) {
				MissParam = "出错啦,缺少参数[".concat(keys[i]).concat("]");
				break;
			}
		}
		return MissParam;
	}

	/**
	 * 参数传入 保留为空的参数
	 * 
	 * @param param
	 * @param keys
	 * @return
	 */
	public static Object KeepNode(Object object) {
		if (object == null) {
			return "";
		} else {
			return object;
		}

	}

	/**
	 * 组装键
	 * 
	 * @param param
	 * @param keys
	 * @return
	 */
	public static Map<String, Object> setKeyMap(Map<String, Object> param, String... keys) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < keys.length; i++) {
			if (!param.containsKey(keys[i])) {
				map.put(keys[i], "");
			} else {
				map.put(keys[i], param.get(keys[i]));
			}
		}
		return map;
	}

	/**
	 * 按键返回数据
	 * 
	 * @param param
	 * @param keys
	 * @return
	 */
	public static Map<String, Object> getKeyMap(Map<String, Object> param, String... keys) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < keys.length; i++) {
			if (param.containsKey(keys[i]) && param.get(keys[i]) != null) {
				map.put(keys[i], param.get(keys[i]));
			}
		}

		return map;
	}

}
