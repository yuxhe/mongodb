/**
 * 
 */
package com.luyouchina.comm;

import redis.clients.jedis.ShardedJedis;

/**
 * 圈子相关
 * 
 * @author lfj
 *
 */
public class GroupUtil {

	public static ShardedJedis shardedJedisConn = CacheUtil.sharedJedisPool.getResource();// 缓存连接

	public static void genGroupNo(Integer total) {

	}

	public static String getGroupNo() {

		return "";
	}

}
