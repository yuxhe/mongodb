/**
 * 
 */
package com.luyouchina.comm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统配置工具类
 * 
 * @author lfj
 *
 */
public class ConfigUtil {

	private static final Logger log = LoggerFactory.getLogger(ConfigUtil.class);

	public static Map<String, String> ConfigMap = new HashMap<String, String>();

	/**
	 * 加载系统配置文件：文件名：config.properties，文件目录，直接在tomcat中运行：tomcat/bin;eclipse中的tomcat运行：eclipse根目录
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Properties loadConfig() {
		Properties properties = new Properties();
		String fileName = "config.properties";
		try {
			// 回去tomcat bin路径，如果是在eclipse中则获取eclipse跟路径
			String str = new File("").getAbsolutePath();

			// 将%20换成空格（这是因为假如文件所在的目录带有空格的话，会在取得的字符串上变成%20）
			str = URLDecoder.decode(str, "UTF-8");
			log.error("加载配置文件：{}", str + File.separator + fileName);
			FileInputStream input = new FileInputStream(str + File.separator + fileName);
			properties.load(input);

			Set<Object> keys = properties.keySet();

			for (Object key : keys) {
				ConfigMap.put(key.toString(), properties.getProperty(key.toString()));
				log.warn("加载系统配置{}={}：", key.toString(), properties.getProperty(key.toString()));
			}
		} catch (IOException e) {
			log.error("加载系统配置出错：", e);
			System.exit(0);
			return null;
		}

		return properties;
	}

	/**
	 * 获取配置参数
	 * 
	 * @param key
	 * @return
	 */
	public static String getConfig(Config key) {
		return ConfigMap.get(key.toString());
	}

	/**
	 * 获取MQ连接url
	 * 
	 * @param ip
	 *            ip配置key
	 * @param port
	 *            端口配置key
	 * @return
	 */
	public static String getMQConnectionUrl(Config ip, Config port) {
		return MessageFormat.format("tcp://{0}:{1}", getConfig(ip), getConfig(port));
	}

	/**
	 * 获取MQ监听url
	 * 
	 * @param port
	 * @return
	 */
	public static String getMQListenerUrl(Config port) {
		return MessageFormat.format("tcp://*:{0}", getConfig(port));
	}

	public enum Config {
		/**
		 * IM数据库,格式：mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database[.collection]][?options]]
		 */
		datebase_luyouim("datebase_luyouim"),
		/**
		 * 日志数据库,格式：mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database[.collection]][?options]]
		 */
		datebase_luyoulog("datebase_luyoulog"),
		/**
		 * 资源数据库,格式：mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database[.collection]][?options]]
		 */
		datebase_luyouresource("datebase_luyouresource"),
		/**
		 * 搜索数据库,格式：mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database[.collection]][?options]]
		 */
		datebase_luyousearch("datebase_luyousearch"),
		/**
		 * 核心平台数据库,格式：mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database[.collection]][?options]]
		 */
		datebase_luyoutcore("datebase_luyoutcore"),
		/**
		 * 考试系统数据库,格式：mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database[.collection]][?options]]
		 */
		datebase_luyouexam("datebase_luyouexam"),
		/**
		 * 支付系统数据库,格式：mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database[.collection]][?options]]
		 */
		datebase_luyoupay("datebase_luyoupay"),
		/**
		 * 直播系统数据库,格式：mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database[.collection]][?options]]
		 */
		datebase_luyoulive("datebase_luyoulive"),
		/**
		 * 课件系统数据库,格式：mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database[.collection]][?options]]
		 */
		datebase_luyoucus("datebase_luyoucus"),
		/**
		 * redis连接URL,格式：redis://:foobared@localhost:6380
		 */
		redis_uri("redis_uri"),
		/**
		 * 核心平台MQ监听端口
		 */
		core_mq_listener_port("core_mq_listener_port"),
		/**
		 * 核心平台MQ连接IP
		 */
		core_mq_ip("core_mq_ip"),
		/**
		 * 支付MQ监听端口
		 */
		pay_mq_listener_port("pay_mq_listener_port"),
		/**
		 * 支付MQ连接IP
		 */
		pay_mq_ip("pay_mq_ip"),
		/**
		 * 日志监听地址端口
		 */
		log_mq_listener_port("log_mq_listener_port"),
		/**
		 * 日志客服端MQ连接IP
		 */
		log_mq_ip("log_mq_ip"),
		/**
		 * nsq服务器ip地址
		 */
		nsq_ip("nsq_ip"),
		/**
		 * 多域名配置
		 */
		exclude_paths("exclude_paths"),
		/**
		 * 腾讯音视频私钥存放位置
		 */
		tencent_eckey("tencent_eckey"),
		/**
		 * IM服务器id
		 */
		im_server_ip("im_server_ip"),
		/**
		 * NSQ地址查找IP
		 */
		nsqlookup_ip("nsqlookup_ip"),
		/**
		 * cas登陆验证地址（for IM）
		 */
		cas_im_login_url("cas_im_login_url");

		private String name;

		private Config(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

}
