package com.luyouchina.comm;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例bean工厂
 */
public class SingleBeanFactory {

	private static Map<String, Object>	beanMap	= new HashMap<String, Object>();

	@SuppressWarnings("unchecked")
	public static synchronized <T> T getBean(Class<T> clazz) {
		String cn = clazz.getName();
		
		T bean = (T) beanMap.get(cn);
		
		if (bean == null) {
			try {
				bean = (T) Class.forName(cn).newInstance();
				beanMap.put(cn, bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return bean;
	}
}
