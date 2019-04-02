package com.luyouchina.comm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmsUtil {

	private static final Logger log = LoggerFactory.getLogger(SmsUtil.class);

	public static final String REGISTER = "register";
	public static final String SRV_CODE_SEND_SUCCESS = "+OK|SUCESS";
	public static final String SRV_CODE_SEND_ERROR_SIZE = "-ERR|OUT_OF_SEND_SIZE";
	public static final String SRV_CODE_SEND_ERROR_FAIL1 = "-ERR|error:-14";
	public static final String SRV_CODE_SEND_ERROR_FAIL2 = "-ERR|error:-14.1";
	public static final String RECOVER = "recover";
	public static final String PASSWORD = "password";
	public static final String PASSWORDRECOVER = "Password-recover";
	public static final String PASSWORDRESET = "Password-sys-reset";
	public static final String MOD_PHONE = "mod_phone";
	public static final String BIND_EMAIL = "bind_email";
	public static String SMS_SRV_URL = "http://appservice.luyouchina.com/SmsService/sms/template";

	/**
	 * 设置短信发送服务地址，默认：http://appservice.luyouchina.com/SmsService/sms/template
	 * 
	 * @param sms_srv_url
	 */
	public static void setSmsSrvUrl(String sms_srv_url) {
		SmsUtil.SMS_SRV_URL = sms_srv_url;
	}

	/**
	 * 短信发送
	 * 
	 * @param templateName
	 * @param recvs
	 * @param params
	 * @return
	 */
	public static boolean doSendSms(String templateName, List<String> recvs, List<String> params) {
		log.info("开始发送短信。。。");
		// 使用base64转码
		try {
			StringBuffer message = new StringBuffer();
			message.append("<request><template_name>" + templateName + "</template_name><recvs>");
			for (String email : recvs) {
				message.append("<item>" + email + "</item>");
			}
			message.append("</recvs><values>");
			for (String param : params) {
				message.append("<item>" + Base64Util.encode(param.getBytes("UTF-8")) + "</item>");
			}
			message.append("</values></request>");
			log.info("短信发送内容：" + message);
			System.out.println("短信发送内容：" + message);
			URL url = new URL(SMS_SRV_URL);
			URLConnection con = url.openConnection();
			con.setDoOutput(true);
			con.setRequestProperty("Pragma:", "no-cache");
			con.setRequestProperty("Cache-Control", "no-cache");
			con.setRequestProperty("Content-Type", "text/xml");
			OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
			String xmlInfo = message.toString();
			out.write(new String(xmlInfo.getBytes("UTF-8")));
			out.flush();
			out.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = "";
			String result = "";
			for (line = br.readLine(); line != null; line = br.readLine()) {
				result += line;
			}
			log.info("短信发送结果：" + result);
			System.out.println("短信发送结果：" + result);
			if (result.startsWith(SmsUtil.SRV_CODE_SEND_SUCCESS)) {
				return true;
			} else if (result.startsWith(SmsUtil.SRV_CODE_SEND_ERROR_SIZE) || result.startsWith(SmsUtil.SRV_CODE_SEND_ERROR_FAIL1)
					|| result.startsWith(SmsUtil.SRV_CODE_SEND_ERROR_FAIL2)) {
				return false;
			}
		} catch (Exception e) {
			log.error("发送短信错误：", e);
		}
		return false;
	}

}
