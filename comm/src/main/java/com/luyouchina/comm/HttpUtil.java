/**
 * 
 */
package com.luyouchina.comm;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author lifj
 *
 */
public class HttpUtil {

	public static URLConnection sendPostRequest(String url, Map<String, Object> params, Map<String, Object> headers) throws Exception {
		StringBuilder buf = new StringBuilder();
		Set<Entry<String, Object>> entrys = null;
		// 如果存在参数，则放在HTTP请求体，形如name=aaa&age=10
		if (params != null && !params.isEmpty()) {
			entrys = params.entrySet();
			for (Map.Entry<String, Object> entry : entrys) {
				buf.append(entry.getKey()).append("=").append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8")).append("&");
			}
			buf.deleteCharAt(buf.length() - 1);
		}
		URL url1 = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(1000);
		conn.setDoOutput(true);
		OutputStream out = conn.getOutputStream();
		out.write(buf.toString().getBytes("UTF-8"));
		if (headers != null && !headers.isEmpty()) {
			entrys = headers.entrySet();
			for (Map.Entry<String, Object> entry : entrys) {
				conn.setRequestProperty(entry.getKey(), String.valueOf(entry.getValue()));
			}
		}
		conn.getResponseCode(); // 为了发送成功
		return conn;
	}

	public static byte[] sendPost(String url, Map<String, Object> params, Map<String, Object> headers) throws Exception {
		StringBuilder buf = new StringBuilder();
		Set<Entry<String, Object>> entrys = null;
		// 如果存在参数，则放在HTTP请求体，形如name=aaa&age=10
		if (params != null && !params.isEmpty()) {
			entrys = params.entrySet();
			for (Map.Entry<String, Object> entry : entrys) {
				buf.append(entry.getKey()).append("=").append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8")).append("&");
			}
			buf.deleteCharAt(buf.length() - 1);
		}
		URL url1 = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(1000);
		conn.setDoOutput(true);
		OutputStream out = conn.getOutputStream();
		out.write(buf.toString().getBytes("UTF-8"));
		if (headers != null && !headers.isEmpty()) {
			entrys = headers.entrySet();
			for (Map.Entry<String, Object> entry : entrys) {
				conn.setRequestProperty(entry.getKey(), String.valueOf(entry.getValue()));
			}
		}
		// 刷新、关闭
		out.flush();
		out.close();
		conn.getResponseCode(); // 为了发送成功
		InputStream is = conn.getInputStream();
		if (is != null) {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			is.close();
			return outStream.toByteArray();
		}
		return null;
	}

	private static class TrustAnyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	/**
	 * post方式请求服务器(https协议)
	 * 
	 * @param url
	 *            请求地址
	 * @param content
	 *            参数
	 * @param charset
	 *            编码
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 */
	public static byte[] postHttps(String url, String content, String charset) throws NoSuchAlgorithmException, KeyManagementException, IOException {
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());

		URL console = new URL(url);
		HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
		conn.setConnectTimeout(2000);
		conn.setSSLSocketFactory(sc.getSocketFactory());
		conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
		conn.setDoOutput(true);
		conn.connect();

		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.write(content.getBytes(charset));
		// 刷新、关闭
		out.flush();
		out.close();
		InputStream is = conn.getInputStream();
		if (is != null) {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			is.close();
			return outStream.toByteArray();
		}
		return null;
	}

}
