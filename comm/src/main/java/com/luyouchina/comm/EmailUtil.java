package com.luyouchina.comm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailUtil {

	private static final Logger log = LoggerFactory.getLogger(EmailUtil.class);

	public static final String CREATE_USER = "create_user";
	public static final String RESET_USER = "reset_user";
	public static final String MOD_PHONE = "mod_phone";
	public static final String BIND_EMAIL = "bind_email";
	public static final String CODE_SEND_SUCCESS = "+OK|SUCCESS";
	public static final String SRV_CODE_SEND_SUCCESS = "+OK|SUCESS";
	public static String EMAIL_SRV_URL = "http://srv800.luyouchina.com/interface/email.aspx";

	/**
	 * 设置邮件发送服务地址，默认：http://srv800.luyouchina.com/interface/email.aspx
	 * 
	 * @param sms_srv_url
	 */
	public static void setSmsSrvUrl(String email_srv_url) {
		EmailUtil.EMAIL_SRV_URL = email_srv_url;
	}

	/** 
	 * @author : zhuyu
	 * @version : 1.00
	 * @create time : 2015年9月2日上午10:55:07 
	 * @param templateName
	 * @param recvs
	 * @param params
	 * @return boolean 
	 * @description : 邮件发送
	*/	
	public static boolean doSendEmail(String templateName, List<String> recvs, List<String> params) {
		log.error("开始发送邮件。。。");
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

			log.error("邮件发送内容：" + message);

			URL url = new URL(EMAIL_SRV_URL);
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
			log.error("邮件发送结果：" + result);
			if (result.startsWith(SmsUtil.SRV_CODE_SEND_SUCCESS)) {
				return true;
			}
		} catch (Exception e) {
			log.error("发送邮件错误：", e);
		}
		return false;
	}
}
