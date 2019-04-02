package com.luyouchina.comm;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.luyouchina.comm.model.Origin;
import com.luyouchina.comm.model.RequestObject;
import com.luyouchina.comm.model.ResponseObject;
import com.luyouchina.mq.UserRequester;

/**
 * @author lfj
 *
 */
public class MQUtil {

	private static final Logger log = LoggerFactory.getLogger(MQUtil.class);

	public static UserRequester userRequester;

	/**
	 * 核心平台MQ调用初始化
	 * 
	 * @param mqurl
	 *            mq链接uri
	 */
	public static void init(String mquri) {
		log.warn("初始化MQ连接：{}", mquri);
		// zmq初始化
		userRequester = new UserRequester(mquri);
	}

	/**
	 * 销毁MQ
	 */
	public static void destroy() {
		log.warn("销毁MQ.........");
		if (userRequester != null) {
			userRequester.destroy();
		}
	}

	/**
	 * 核心平台接口调用（调用之前请先调用初始化方法）
	 * 
	 * @param module
	 *            模块
	 * @param method
	 *            方法
	 * @param param
	 *            参数
	 * @param origin
	 *            调用者信息
	 * @return
	 */
	public static ResponseObject call(String module, String method, Map<String, Object> param, Origin origin) {
		long bt = System.currentTimeMillis();
		RequestObject request = new RequestObject();
		request.setModule(module);// 模块
		request.setMethod(method);// 方法
		request.setParam(param);// 参数
		request.setOrigin(origin);// 调用者信息

		String receive;
		try {
			receive = userRequester.sendMsg(JsonUtil.toJson(request));
			if (com.luyouchina.mq.Constants.timeOut.equals(receive)) {
				log.debug("调用{}.{}超时", module, method);

				ResponseObject response = new ResponseObject();
				response.setData(new Object());
				response.setErrorMsg("0005", "链接超时");
				log.debug("执行 {}.{} , 耗时 {} ms.", module, method, System.currentTimeMillis() - bt);
				return response;
			} else if (com.luyouchina.mq.Constants.toobusy.equals(receive)) {
				log.debug("调用{}.{}，系统繁忙", module, method);

				ResponseObject response = new ResponseObject();
				response.setData(new Object());
				response.setErrorMsg("0006", "系统繁忙");
				log.debug("执行 {}.{} , 耗时 {} ms.", module, method, System.currentTimeMillis() - bt);
				return response;
			} else if (com.luyouchina.mq.Constants.exception.equals(receive)) {
				log.debug("调用{}.{}，系统发生异常", module, method);

				ResponseObject response = new ResponseObject();
				response.setData(new Object());
				response.setErrorMsg("0002", "系统发生异常");
				log.debug("执行 {}.{} , 耗时 {} ms.", module, method, System.currentTimeMillis() - bt);
				return response;
			}
		} catch (UnsupportedEncodingException e) {
			log.debug("调用{}.{}失败：{}", e);

			ResponseObject response = new ResponseObject();
			response.setData(new Object());
			response.setErrorMsg("0000", "调用失败");
			log.debug("执行 {}.{} , 耗时 {} ms.", module, method, System.currentTimeMillis() - bt);
			return response;
		}

		log.debug("调用{}.{}服务器返回结果：{}", request.getModule(), request.getMethod(), receive);
		log.debug("执行 {}.{} , 耗时 {} ms.", module, method, System.currentTimeMillis() - bt);
		return JsonUtil.fromJson(receive, ResponseObject.class);
	}

	/**
	 * 直接发送消息
	 * 
	 * @param param
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws TimeoutException
	 */
	public static String sendMsg(String param) throws UnsupportedEncodingException, TimeoutException {
		String receive;
		long bt = System.currentTimeMillis();
		receive = userRequester.sendMsg(param);
		if (com.luyouchina.mq.Constants.timeOut.equals(receive)) {
			log.debug("发送消息：{}超时", param);

			ResponseObject response = new ResponseObject();
			response.setData(new Object());
			response.setErrorMsg("0000", "链接超时");
			return JsonUtil.toJson(response);
		} else if (com.luyouchina.mq.Constants.toobusy.equals(receive)) {
			log.debug("MQ发送消息，系统繁忙");

			ResponseObject response = new ResponseObject();
			response.setData(new Object());
			response.setErrorMsg("0006", "系统繁忙");
			log.debug("发送MQ消息 耗时 {} ms.", System.currentTimeMillis() - bt);
			return JsonUtil.toJson(response);
		} else if (com.luyouchina.mq.Constants.exception.equals(receive)) {
			log.debug("MQ发送消息，系统发生异常");

			ResponseObject response = new ResponseObject();
			response.setData(new Object());
			response.setErrorMsg("0002", "系统发生异常");
			log.debug("MQ发送消息耗时 {} ms.", System.currentTimeMillis() - bt);
			return JsonUtil.toJson(response);
		}
		return receive;
	}
}
