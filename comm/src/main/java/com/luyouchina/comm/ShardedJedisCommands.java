package com.luyouchina.comm;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.luyouchina.comm.ConfigUtil.Config;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;

/**
 * @author lifajun
 *
 */
public class ShardedJedisCommands implements JedisCommands {

	private static final Logger log = LoggerFactory.getLogger(ShardedJedisCommands.class);

	public ShardedJedisCommands() {
		if (CacheUtil.sharedJedisPool == null) {
			// 初始化缓存
			CacheUtil.initRedis(ConfigUtil.getConfig(Config.redis_uri));
		}
	}

	/**
	 * 返还到连接池
	 * 
	 * @param pool
	 * @param redis
	 */
	public static void returnResource(ShardedJedisPool sharedJedisPool, ShardedJedis shardedJedis) {
		if (shardedJedis != null) {
			sharedJedisPool.returnResource(shardedJedis);
		}
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public String set(String key, String value) {
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			return shardedJedis.set(key, value);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis.set", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return null;
	}

	/**
	 * @param key
	 * @param value
	 * @param nxxx
	 * @param expx
	 * @param time
	 * @return
	 */
	@Override
	public String set(String key, String value, String nxxx, String expx, long time) {
		ShardedJedis shardedJedis = null;
		String data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.set(key, value, nxxx, expx, time);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public String get(String key) {
		ShardedJedis shardedJedis = null;
		String value = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			value = shardedJedis.get(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return value;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Boolean exists(String key) {
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			return shardedJedis.exists(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
			return false;
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Long persist(String key) {
		ShardedJedis shardedJedis = null;
		Long value = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			value = shardedJedis.persist(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return value;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public String type(String key) {
		ShardedJedis shardedJedis = null;
		String value = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			value = shardedJedis.type(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return value;
	}

	/**
	 * @param key
	 * @param seconds
	 * @return
	 */
	@Override
	public Long expire(String key, int seconds) {
		ShardedJedis shardedJedis = null;
		Long value = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			value = shardedJedis.expire(key, seconds);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return value;
	}

	/**
	 * @param key
	 * @param unixTime
	 * @return
	 */
	@Override
	public Long expireAt(String key, long unixTime) {
		ShardedJedis shardedJedis = null;
		Long value = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			value = shardedJedis.expireAt(key, unixTime);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return value;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Long ttl(String key) {
		ShardedJedis shardedJedis = null;
		Long value = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			value = shardedJedis.ttl(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return value;
	}

	/**
	 * @param key
	 * @param offset
	 * @param value
	 * @return
	 */
	@Override
	public Boolean setbit(String key, long offset, boolean value) {
		ShardedJedis shardedJedis = null;
		Boolean data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.setbit(key, offset, value);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param offset
	 * @param value
	 * @return
	 */
	@Override
	public Boolean setbit(String key, long offset, String value) {
		ShardedJedis shardedJedis = null;
		Boolean data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.setbit(key, offset, value);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param offset
	 * @return
	 */
	@Override
	public Boolean getbit(String key, long offset) {
		ShardedJedis shardedJedis = null;
		Boolean value = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			value = shardedJedis.getbit(key, offset);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return value;
	}

	/**
	 * @param key
	 * @param offset
	 * @param value
	 * @return
	 */
	@Override
	public Long setrange(String key, long offset, String value) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.setrange(key, offset, value);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param startOffset
	 * @param endOffset
	 * @return
	 */
	@Override
	public String getrange(String key, long startOffset, long endOffset) {
		ShardedJedis shardedJedis = null;
		String value = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			value = shardedJedis.getrange(key, startOffset, endOffset);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return value;
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public String getSet(String key, String value) {
		ShardedJedis shardedJedis = null;
		String data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.getSet(key, value);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public Long setnx(String key, String value) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.setnx(key, value);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param seconds
	 * @param value
	 * @return
	 */
	@Override
	public String setex(String key, int seconds, String value) {
		ShardedJedis shardedJedis = null;
		String data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.setex(key, seconds, value);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param integer
	 * @return
	 */
	@Override
	public Long decrBy(String key, long integer) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.decrBy(key, integer);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Long decr(String key) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.decr(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param integer
	 * @return
	 */
	@Override
	public Long incrBy(String key, long integer) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.incrBy(key, integer);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Long incr(String key) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.incr(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public Long append(String key, String value) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.append(key, value);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	@Override
	public String substr(String key, int start, int end) {
		ShardedJedis shardedJedis = null;
		String data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.substr(key, start, end);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	@Override
	public Long hset(String key, String field, String value) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.hset(key, field, value);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param field
	 * @return
	 */
	@Override
	public String hget(String key, String field) {
		ShardedJedis shardedJedis = null;
		String data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.hget(key, field);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	@Override
	public Long hsetnx(String key, String field, String value) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.hsetnx(key, field, value);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param hash
	 * @return
	 */
	@Override
	public String hmset(String key, Map<String, String> hash) {
		ShardedJedis shardedJedis = null;
		String data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.hmset(key, hash);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param fields
	 * @return
	 */
	@Override
	public List<String> hmget(String key, String... fields) {
		ShardedJedis shardedJedis = null;
		List<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.hmget(key, fields);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	@Override
	public Long hincrBy(String key, String field, long value) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.hincrBy(key, field, value);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param field
	 * @return
	 */
	@Override
	public Boolean hexists(String key, String field) {
		ShardedJedis shardedJedis = null;
		Boolean data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.hexists(key, field);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param field
	 * @return
	 */
	@Override
	public Long hdel(String key, String... field) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.hdel(key, field);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Long hlen(String key) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.hlen(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Set<String> hkeys(String key) {
		ShardedJedis shardedJedis = null;
		Set<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.hkeys(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public List<String> hvals(String key) {
		ShardedJedis shardedJedis = null;
		List<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.hvals(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Map<String, String> hgetAll(String key) {
		ShardedJedis shardedJedis = null;
		Map<String, String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.hgetAll(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param string
	 * @return
	 */
	@Override
	public Long rpush(String key, String... string) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.rpush(key, string);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param string
	 * @return
	 */
	@Override
	public Long lpush(String key, String... string) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.lpush(key, string);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Long llen(String key) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.llen(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	@Override
	public List<String> lrange(String key, long start, long end) {
		ShardedJedis shardedJedis = null;
		List<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.lrange(key, start, end);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	@Override
	public String ltrim(String key, long start, long end) {
		ShardedJedis shardedJedis = null;
		String data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.ltrim(key, start, end);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param index
	 * @return
	 */
	@Override
	public String lindex(String key, long index) {
		ShardedJedis shardedJedis = null;
		String data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.lindex(key, index);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param index
	 * @param value
	 * @return
	 */
	@Override
	public String lset(String key, long index, String value) {
		ShardedJedis shardedJedis = null;
		String data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.lset(key, index, value);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param count
	 * @param value
	 * @return
	 */
	@Override
	public Long lrem(String key, long count, String value) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.lrem(key, count, value);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public String lpop(String key) {
		ShardedJedis shardedJedis = null;
		String data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.lpop(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public String rpop(String key) {
		ShardedJedis shardedJedis = null;
		String data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.rpop(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param member
	 * @return
	 */
	@Override
	public Long sadd(String key, String... member) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.sadd(key, member);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Set<String> smembers(String key) {
		ShardedJedis shardedJedis = null;
		Set<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.smembers(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param member
	 * @return
	 */
	@Override
	public Long srem(String key, String... member) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.srem(key, member);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public String spop(String key) {
		ShardedJedis shardedJedis = null;
		String data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.spop(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Long scard(String key) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.scard(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param member
	 * @return
	 */
	@Override
	public Boolean sismember(String key, String member) {
		ShardedJedis shardedJedis = null;
		Boolean data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.sismember(key, member);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public String srandmember(String key) {
		ShardedJedis shardedJedis = null;
		String data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.srandmember(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param count
	 * @return
	 */
	@Override
	public List<String> srandmember(String key, int count) {
		ShardedJedis shardedJedis = null;
		List<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.srandmember(key, count);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Long strlen(String key) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.strlen(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	@Override
	public Long zadd(String key, double score, String member) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zadd(key, score, member);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param scoreMembers
	 * @return
	 */
	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zadd(key, scoreMembers);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	@Override
	public Set<String> zrange(String key, long start, long end) {
		ShardedJedis shardedJedis = null;
		Set<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrange(key, start, end);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param member
	 * @return
	 */
	@Override
	public Long zrem(String key, String... member) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrem(key, member);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	@Override
	public Double zincrby(String key, double score, String member) {
		ShardedJedis shardedJedis = null;
		Double data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zincrby(key, score, member);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param member
	 * @return
	 */
	@Override
	public Long zrank(String key, String member) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrank(key, member);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param member
	 * @return
	 */
	@Override
	public Long zrevrank(String key, String member) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrevrank(key, member);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	@Override
	public Set<String> zrevrange(String key, long start, long end) {
		ShardedJedis shardedJedis = null;
		Set<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrevrange(key, start, end);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	@Override
	public Set<Tuple> zrangeWithScores(String key, long start, long end) {
		ShardedJedis shardedJedis = null;
		Set<Tuple> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrangeWithScores(key, start, end);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	@Override
	public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
		ShardedJedis shardedJedis = null;
		Set<Tuple> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrevrangeWithScores(key, start, end);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Long zcard(String key) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zcard(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param member
	 * @return
	 */
	@Override
	public Double zscore(String key, String member) {
		ShardedJedis shardedJedis = null;
		Double data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zscore(key, member);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public List<String> sort(String key) {
		ShardedJedis shardedJedis = null;
		List<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.sort(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param sortingParameters
	 * @return
	 */
	@Override
	public List<String> sort(String key, SortingParams sortingParameters) {
		ShardedJedis shardedJedis = null;
		List<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.sort(key, sortingParameters);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	@Override
	public Long zcount(String key, double min, double max) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zcount(key, min, max);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	@Override
	public Long zcount(String key, String min, String max) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zcount(key, min, max);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		ShardedJedis shardedJedis = null;
		Set<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrangeByScore(key, min, max);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	@Override
	public Set<String> zrangeByScore(String key, String min, String max) {
		ShardedJedis shardedJedis = null;
		Set<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrangeByScore(key, min, max);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param max
	 * @param min
	 * @return
	 */
	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min) {
		ShardedJedis shardedJedis = null;
		Set<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrevrangeByScore(key, max, min);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return
	 */
	@Override
	public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
		ShardedJedis shardedJedis = null;
		Set<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrangeByScore(key, min, max, offset, count);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param max
	 * @param min
	 * @return
	 */
	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min) {
		ShardedJedis shardedJedis = null;
		Set<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrevrangeByScore(key, max, min);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return
	 */
	@Override
	public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
		ShardedJedis shardedJedis = null;
		Set<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrangeByScore(key, min, max, offset, count);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param max
	 * @param min
	 * @param offset
	 * @param count
	 * @return
	 */
	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
		ShardedJedis shardedJedis = null;
		Set<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrevrangeByScore(key, max, min, offset, count);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
		ShardedJedis shardedJedis = null;
		Set<Tuple> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrangeByScoreWithScores(key, min, max);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param max
	 * @param min
	 * @return
	 */
	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
		ShardedJedis shardedJedis = null;
		Set<Tuple> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrevrangeByScoreWithScores(key, max, min);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return
	 */
	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
		ShardedJedis shardedJedis = null;
		Set<Tuple> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrangeByScoreWithScores(key, min, max, offset, count);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param max
	 * @param min
	 * @param offset
	 * @param count
	 * @return
	 */
	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
		ShardedJedis shardedJedis = null;
		Set<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrevrangeByScore(key, max, min, offset, count);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
		ShardedJedis shardedJedis = null;
		Set<Tuple> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrangeByScoreWithScores(key, min, max);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param max
	 * @param min
	 * @return
	 */
	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
		ShardedJedis shardedJedis = null;
		Set<Tuple> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrevrangeByScoreWithScores(key, max, min);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return
	 */
	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
		ShardedJedis shardedJedis = null;
		Set<Tuple> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrangeByScoreWithScores(key, min, max, offset, count);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param max
	 * @param min
	 * @param offset
	 * @param count
	 * @return
	 */
	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
		ShardedJedis shardedJedis = null;
		Set<Tuple> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param max
	 * @param min
	 * @param offset
	 * @param count
	 * @return
	 */
	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
		ShardedJedis shardedJedis = null;
		Set<Tuple> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	@Override
	public Long zremrangeByRank(String key, long start, long end) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zremrangeByRank(key, start, end);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	@Override
	public Long zremrangeByScore(String key, double start, double end) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zremrangeByScore(key, start, end);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	@Override
	public Long zremrangeByScore(String key, String start, String end) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zremrangeByScore(key, start, end);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	@Override
	public Long zlexcount(String key, String min, String max) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zlexcount(key, min, max);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	@Override
	public Set<String> zrangeByLex(String key, String min, String max) {
		ShardedJedis shardedJedis = null;
		Set<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrangeByLex(key, min, max);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return
	 */
	@Override
	public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
		ShardedJedis shardedJedis = null;
		Set<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zrangeByLex(key, min, max, offset, count);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	@Override
	public Long zremrangeByLex(String key, String min, String max) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zremrangeByLex(key, min, max);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param where
	 * @param pivot
	 * @param value
	 * @return
	 */
	@Override
	public Long linsert(String key, LIST_POSITION where, String pivot, String value) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.linsert(key, where, pivot, value);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param string
	 * @return
	 */
	@Override
	public Long lpushx(String key, String... string) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.lpushx(key, string);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param string
	 * @return
	 */
	@Override
	public Long rpushx(String key, String... string) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.rpushx(key, string);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param arg
	 * @return
	 * @deprecated
	 */
	@Override
	public List<String> blpop(String arg) {
		ShardedJedis shardedJedis = null;
		List<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.blpop(arg);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param timeout
	 * @param key
	 * @return
	 */
	@Override
	public List<String> blpop(int timeout, String key) {
		ShardedJedis shardedJedis = null;
		List<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.blpop(timeout, key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param arg
	 * @return
	 * @deprecated
	 */
	@Override
	public List<String> brpop(String arg) {
		ShardedJedis shardedJedis = null;
		List<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.brpop(arg);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param timeout
	 * @param key
	 * @return
	 */
	@Override
	public List<String> brpop(int timeout, String key) {
		ShardedJedis shardedJedis = null;
		List<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.brpop(timeout, key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Long del(String key) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.del(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param string
	 * @return
	 */
	@Override
	public String echo(String string) {
		ShardedJedis shardedJedis = null;
		String data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.echo(string);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param dbIndex
	 * @return
	 */
	@Override
	public Long move(String key, int dbIndex) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.move(key, dbIndex);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Long bitcount(String key) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.bitcount(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	@Override
	public Long bitcount(String key, long start, long end) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.bitcount(key, start, end);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param cursor
	 * @return
	 * @deprecated
	 */
	@Override
	public ScanResult<Entry<String, String>> hscan(String key, int cursor) {
		ShardedJedis shardedJedis = null;
		ScanResult<Entry<String, String>> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.hscan(key, cursor);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param cursor
	 * @return
	 * @deprecated
	 */
	@Override
	public ScanResult<String> sscan(String key, int cursor) {
		ShardedJedis shardedJedis = null;
		ScanResult<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.sscan(key, cursor);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param cursor
	 * @return
	 * @deprecated
	 */
	@Override
	public ScanResult<Tuple> zscan(String key, int cursor) {
		ShardedJedis shardedJedis = null;
		ScanResult<Tuple> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zscan(key, cursor);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param cursor
	 * @return
	 */
	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor) {
		ShardedJedis shardedJedis = null;
		ScanResult<Entry<String, String>> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.hscan(key, cursor);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param cursor
	 * @return
	 */
	@Override
	public ScanResult<String> sscan(String key, String cursor) {
		ShardedJedis shardedJedis = null;
		ScanResult<String> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.sscan(key, cursor);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param cursor
	 * @return
	 */
	@Override
	public ScanResult<Tuple> zscan(String key, String cursor) {
		ShardedJedis shardedJedis = null;
		ScanResult<Tuple> data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.zscan(key, cursor);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @param elements
	 * @return
	 */
	@Override
	public Long pfadd(String key, String... elements) {
		ShardedJedis shardedJedis = null;
		Long data = null;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.pfadd(key, elements);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public long pfcount(String key) {
		ShardedJedis shardedJedis = null;
		long data = 0l;
		try {
			shardedJedis = CacheUtil.sharedJedisPool.getResource();
			data = shardedJedis.pfcount(key);
		} catch (Exception e) {
			CacheUtil.sharedJedisPool.returnBrokenResource(shardedJedis);
			log.error("jedis:", e);
		} finally {
			returnResource(CacheUtil.sharedJedisPool, shardedJedis);
		}
		return data;
	}
}
