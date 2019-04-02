package com.luyouchina.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;

public final class HttpUtils {
	private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

	public static String validateRequired(String paramName, HttpServletRequest request, String[] values) throws LyException {
		String value = request.getParameter(paramName);
		if (StringUtils.isBlank(value)) {
			throw new LyException(String.format("请传递参数：[%s]", paramName));
		}
		if (values != null && !ArrayUtils.contains(values, value)) {
			throw new LyException(String.format("[%1s] must be one of %2s", paramName, StringUtils.join(values, '/')));
		}
		return value;
	}

	public static Integer validateInt(String paramName, HttpServletRequest request, boolean required) throws LyException {
		String value = request.getParameter(paramName);
		Integer v = null;
		if (StringUtils.isBlank(value)) {
			if (required) {
				throw new LyException(String.format("请传递参数：[%s]", paramName));
			}
		} else {
			try {
				v = new Integer(value);
			} catch (NumberFormatException e) {
				throw new LyException(String.format("参数 [%s] 值需为整型数据", paramName));
			}
		}
		return v;
	}

	public static String validateString(String paramName, HttpServletRequest request, boolean required) throws LyException {
		String value = request.getParameter(paramName);
		if (StringUtils.isBlank(value)) {
			if (required) {
				throw new LyException(String.format("请传递参数：[%s]", paramName));
			}
		}
		return value;
	}

	public static double validateDouble(String paramName, HttpServletRequest request, boolean required) throws LyException {
		String value = request.getParameter(paramName);
		double v = 0;
		if (StringUtils.isBlank(value)) {
			if (required) {
				throw new LyException(String.format("请传递参数：[%s]", paramName));
			}
		} else {
			try {
				v = Double.parseDouble(value);
			} catch (NumberFormatException e) {
				throw new LyException(String.format("参数 [%s] 值需为浮点数据", paramName));
			}
		}
		return v;
	}

	public static Map<Object, Object> getParamMap(HttpServletRequest request) throws LyException {
		Map<Object, Object> map = null;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			StringBuffer sbuf = new StringBuffer();
			String s;
			while ((s = br.readLine()) != null) {
				sbuf.append(s);
			}
			log.debug("post json: {}", sbuf);
			Map<?, ?> m = JsonUtil.fromJson(sbuf.toString(), Map.class);
			if (m != null) {
				map = new HashMap<Object, Object>(m);
			}
		} catch (JsonParseException e) {
			throw new LyException("post json 格式不正确", e);
		} catch (IOException e) {
			throw new LyException("读取post数据出错", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return map;
	}

	public static String validateRequired(String paramName, Map<Object, Object> map, String[] values) throws LyException {
		String value = String.valueOf(map.get(paramName));
		if (StringUtils.equals(value, "null")) {
			throw new LyException(String.format("请传递参数：[%s]", paramName));
		}
		if (values != null && !ArrayUtils.contains(values, value)) {
			throw new LyException(String.format("[%1s] must be one of %2s", paramName, StringUtils.join(values, '/')));
		}
		return value;
	}

	public static void validateRequired(Map<String, Object> param, String... keys) throws LyException {
		for (String key : keys) {
			if (!param.containsKey(key) || param.get(key) == null) {
				throw new LyException(String.format("请传递参数：[%s]", key));
			}
		}
	}

	/**
	 * 将 bootstrapTable分页参数转成 核心平台需要的参数
	 * 
	 * @param param
	 */
	public static void convertPagingParam(Map<String, Object> param) {
		if (param != null) {
			if (param.containsKey("pageNumber") && param.get("pageNumber") != null && !param.containsKey("page")) {
				if (param.get("pageNumber") instanceof Integer) {
					param.put("page", (Integer) param.get("pageNumber"));
				} else {
					param.put("page", Integer.valueOf((String) param.get("pageNumber")));
				}
				param.remove("pageNumber");
			} else if (param.get("page") == null) {
				param.put("page", 1);
			} else if (param.get("page") != null) {
				if (param.get("page") instanceof Integer) {
					param.put("page", (Integer) param.get("page"));
				} else {
					param.put("page", Integer.valueOf((String) param.get("page")));
				}
			}
			if (param.containsKey("pageSize") && param.get("pageSize") != null && !param.containsKey("rows")) {
				if (param.get("pageSize") instanceof Integer) {
					param.put("rows", (Integer) param.get("pageSize"));
				} else {
					param.put("rows", Integer.valueOf((String) param.get("pageSize")));
				}
				param.remove("pageSize");
			} else if (param.get("rows") == null) {
				param.put("rows", 10);
			} else if (param.get("rows") != null) {
				if (param.get("rows") instanceof Integer) {
					param.put("rows", (Integer) param.get("rows"));
				} else {
					param.put("rows", Integer.valueOf((String) param.get("rows")));
				}
			}
		}
	}

	/**
	 * 获取简单类型参数
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, Object> getRequestParameter(HttpServletRequest request) {
		Map<String, Object> param = new HashMap<String, Object>();
		// 读取get参数
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			if (request.getParameterValues(name).length > 1) {
				param.put(name, request.getParameterValues(name));
			} else {
				param.put(name, request.getParameter(name));
			}
		}
		// 读取post参数
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			StringBuffer sbuf = new StringBuffer();
			String s;
			while ((s = br.readLine()) != null) {
				sbuf.append(s);
			}
			if (sbuf.length() > 0) {
				log.debug("post json: {}", sbuf);
				Map<String, Object> postParam = JsonUtil.fromJson(sbuf.toString(), new TypeReference<Map<String, Object>>() {
				});
				if (postParam != null) {
					param.putAll(postParam);
				}
			}
		} catch (IOException e) {
			log.error("读取post数据出错:", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Map<String, Object> data = formDataToObject(param);
		log.debug("提交参数: {}", JsonUtil.toJson(data));
		return data;
	}

	/**
	 * 获取复杂类型参数（包含集合、数组、嵌套类等）
	 * 
	 * @param request
	 * @param type
	 * @return
	 */
	public static <T> T getRequestParameter(HttpServletRequest request, Class<T> type) {
		try {
			T object = type.newInstance();
			Map<String, Object> param = new HashMap<String, Object>();
			// 读取get参数
			param.putAll(request.getParameterMap());
			// 读取post参数
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(request.getInputStream()));
				StringBuffer sbuf = new StringBuffer();
				String s;
				while ((s = br.readLine()) != null) {
					sbuf.append(s);
				}
				if (sbuf.length() > 0) {
					Map<String, Object> postParam = JsonUtil.fromJson(sbuf.toString(), new TypeReference<Map<String, Object>>() {
					});

					if (postParam != null) {
						param.putAll(postParam);
					}
				}
			} catch (IOException e) {
				log.error("读取post数据出错:", e);
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			Map<String, Object> data = formDataToObject(param);
			log.debug("提交参数: {}", JsonUtil.toJson(data));

			BeanUtils.populate(object, data);

			return object;
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			log.error("获取参数发生错误：", e);
			throw new LyException("获取参数发生错误");
		}
	}

	/**
	 * 处理表单中复杂数据
	 * 
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> formDataToObject(Map<String, Object> param) {
		Map<String, Object> data = new HashMap<String, Object>();
		if (param != null && !param.isEmpty()) {
			Set<String> keys = param.keySet();
			List<String> keylist = new ArrayList<String>();
			keylist.addAll(keys);
			Collections.sort(keylist, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					Pattern p = Pattern.compile("^(.+?)\\[([0-9]{1,})\\]");
					Matcher m1 = p.matcher(o1);
					Matcher m2 = p.matcher(o2);
					if (m1.find() && m2.find()) {
						String g1 = m1.group(1);
						String g2 = m2.group(1);
						int index1 = Integer.valueOf(m1.group(2));
						int index2 = Integer.valueOf(m2.group(2));
						if (g1.compareTo(g2) > 1) {
							return 1;
						} else if (g1.compareTo(g2) == 0) {
							if (index1 > index2) {
								return 1;
							} else if (index1 == index2) {
								return 0;
							} else {
								return -1;
							}
						} else {
							return -1;
						}

					} else {
						return o1.compareTo(o2);
					}
				}
			});

			for (String key : keylist) {
				if (param.get(key) == null) {
					continue;
				}
				try {
					Pattern p = Pattern.compile("^(.+?)\\[([0-9]{1,})\\][\\.](.+?)$");
					Matcher m = p.matcher(key);

					Pattern p2 = Pattern.compile("^(.+?)\\[([0-9]{0,})\\]$");
					Matcher m2 = p2.matcher(key);
					if (m.find()) {// a[0].b
						String prop1 = m.group(1);
						int index = Integer.valueOf(m.group(2));
						String prop2 = m.group(3);
						List<Map<String, Object>> list;
						if (data.get(prop1) == null) {
							list = new ArrayList<Map<String, Object>>();
							Map<String, Object> map = new HashMap<String, Object>();
							BeanUtils.setProperty(map, prop2, param.get(key));
							list.add(index, map);
							data.put(prop1, list);
						} else {
							list = (List<Map<String, Object>>) data.get(prop1);
							if (list.size() > index && list.get(index) != null) {
								Map<String, Object> map = list.get(index);
								BeanUtils.setProperty(map, prop2, param.get(key));

							} else {
								Map<String, Object> map = new HashMap<String, Object>();
								BeanUtils.setProperty(map, prop2, param.get(key));

								list.add(index, map);
							}
							data.put(prop1, list);
						}
					} else if (m2.find()) {// a[]或者a[0]
						String prop1 = m2.group(1);
						String index = m2.group(2);
						List<Object> list = (List<Object>) data.get(prop1);
						if (list == null) {
							list = new ArrayList<Object>();
							if (StringUtil.isNotEmpty(index)) {
								list.add(Integer.valueOf(index), param.get(key));
							} else {
								if (param.get(key) instanceof String[]) {
									list.addAll(Arrays.asList((String[]) param.get(key)));
								} else {
									list.add(param.get(key));
								}
							}
							data.put(prop1, list);
						} else {
							if (StringUtil.isNotEmpty(index)) {
								int i = Integer.valueOf(index);
								list.add(i, param.get(key));
							} else {
								if (param.get(key) instanceof String[]) {
									list.addAll(Arrays.asList((String[]) param.get(key)));
								} else {
									list.add(param.get(key));
								}
							}
							data.put(prop1, list);
						}

					} else if (key.contains(".")) {
						String[] prop = key.split("\\.");
						Map<String, Object> map = new HashMap<String, Object>();
						BeanUtils.setProperty(map, prop[1], param.get(key));
						data.put(prop[0], map);
					} else {
						// BeanUtils.setProperty(data, key, param.get(key));

						data.put(key, param.get(key));
					}
				} catch (Exception e) {
					log.error("获取参数{}发生错误：{}", key, e);
				}
			}
		}
		return data;
	}

	/**
	 * 获取复杂类型参数（包含集合、数组、嵌套类等）
	 * 
	 * @param request
	 * @param type
	 *            参数结构类
	 * @return
	 */
	public static <T> Map<String, Object> getRequestParameterMap(HttpServletRequest request, Class<T> type) {
		Map<String, Object> param = new HashMap<String, Object>();

		T object = getRequestParameter(request, type);

		if (object == null) {
			return param;
		}

		param = JsonUtil.fromJson(JsonUtil.toJson(object), new TypeReference<Map<String, Object>>() {
		});

		return param;
	}

	public static Map<Object, Object> getRequestAttribute(HttpServletRequest request) {
		Map<Object, Object> param = new HashMap<Object, Object>();

		Enumeration<String> names = request.getAttributeNames();

		while (names.hasMoreElements()) {
			String name = names.nextElement();
			param.put(name, request.getAttribute(name));
		}

		return param;
	}

	public static int validateInt(String paramName, Map<Object, Object> map, boolean required) throws LyException {
		String value = String.valueOf(map.get(paramName));
		int v = 0;
		if (StringUtils.equals(value, "null")) {
			if (required) {
				throw new LyException(String.format("请传递参数：[%s]", paramName));
			}
		} else {
			try {
				v = Integer.parseInt(value);
				if (v <= 0) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				throw new LyException(String.format("参数 [%s] 值需为整型数据", paramName));
			}
		}
		return v;
	}

	public static List<Object> validateList(String paramName, Map<Object, Object> map, boolean required) throws LyException {
		List<Object> list = null;

		Object value = map.get(paramName);
		if (value == null) {
			if (required) {
				throw new LyException(String.format("请传递参数： [%s]", paramName));
			}
		} else if (value instanceof List) {
			list = new ArrayList<Object>((List<?>) value);
		} else {
			throw new LyException(String.format("参数 [%s] 值需为list型数据", paramName));
		}

		return list;
	}

	public static void printJson(ResponseDto dto, HttpServletResponse response) {
		response.setContentType("application/json;charset=UTF-8");
		try {
			response.getWriter().print(JsonUtil.toJson(dto));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printJson(Object data, HttpServletResponse response) {
		response.setContentType("application/json;charset=UTF-8");
		try {
			response.getWriter().print(JsonUtil.toJson(data));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void printJson(String json, HttpServletResponse response) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().print(json);
	}

	public static void printText(String text, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().print(text);
	}

	public static void printResult(int status, String msg, Map<String, Object> extData, HttpServletResponse response) throws IOException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("status", status);
		data.put("msg", msg == null ? "" : msg.trim());
		if (extData != null) {
			data.putAll(extData);
		}
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().print(JsonUtil.toJson(data));
	}

	public static void printError(int status, String error, HttpServletResponse response) throws IOException {
		response.setStatus(status);
		printText(error, response);
	}
}
