/**
 * 
 */
package com.luyouchina.comm;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author lifj
 *
 */
public class LocalIPUtil {

	/**
	 * IceWee 2013.07.19
	 * 获取本地IP列表（针对多网卡情况）
	 *
	 * @return
	 */
	public static List<String> getLocalIPList() {
		List<String> ipList = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			NetworkInterface networkInterface;
			Enumeration<InetAddress> inetAddresses;
			InetAddress inetAddress;
			String ip;
			while (networkInterfaces.hasMoreElements()) {
				networkInterface = networkInterfaces.nextElement();
				inetAddresses = networkInterface.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					inetAddress = inetAddresses.nextElement();
					if (inetAddress != null && inetAddress instanceof Inet4Address) { // IPV4
						ip = inetAddress.getHostAddress();
						if ("127.0.0.1".equals(ip)) {
							continue;
						}
						ipList.add(ip);
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return ipList;
	}

}
