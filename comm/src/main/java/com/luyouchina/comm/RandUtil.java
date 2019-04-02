/**
 * 
 */
package com.luyouchina.comm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * @author lfj
 *
 */
public class RandUtil {

	/**
	 * 生成随机数字
	 * 
	 * @param length
	 * @return
	 */
	public static String RandomNum(int length) {
		// 35是因为数组是从0开始的，26个字母+10个数字
		final int maxNum = 36;
		int i; // 生成的随机数
		int count = 0; // 生成的密码的长度
		char[] str = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer sb = new StringBuffer("");
		Random r = new Random();
		while (count < length) {
			// 生成随机数，取绝对值，防止生成负数
			i = Math.abs(r.nextInt(maxNum)); // 生成的数最大为36-1

			if (i >= 0 && i < str.length) {
				sb.append(str[i]);
				count++;
			}
		}
		return sb.toString();
	}

	/**
	 * 生成随机数字和字母
	 * 
	 * @param length
	 * @return
	 */
	public static String RandomString(int length) {
		String val = "";
		Random random = new Random();

		// 参数length，表示生成几位随机数
		for (int i = 0; i < length; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			// 输出字母还是数字
			if ("char".equalsIgnoreCase(charOrNum)) {
				// 输出是大写字母还是小写字母
				int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val += (char) (random.nextInt(26) + temp);
			} else if ("num".equalsIgnoreCase(charOrNum)) {
				val += String.valueOf(random.nextInt(10));
			}
		}
		return val;
	}

	/**
	 * 生成uuid(去掉了-)
	 * 
	 * @return
	 */
	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/** 
     * 生成：五位随机数 + 当前年月日时分秒  yyMMddHHmmss 共17位
     *  
     * @return 
     */  
    public static String getRandNo() {  
        SimpleDateFormat simpleDateFormat;  
        simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");  
        Date date = new Date();  
        String str = simpleDateFormat.format(date);  
        Random random = new Random();  
        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数  
        return rannum + str;// 当前时间  
    }  
}
