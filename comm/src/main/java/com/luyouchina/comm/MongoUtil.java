/**
 * 
 */
package com.luyouchina.comm;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.gridfs.GridFS;
import com.mongodb.util.JSON;

/**
 * @author lfj
 *
 */
public class MongoUtil {

	private static final Logger log = LoggerFactory.getLogger(MongoUtil.class);
	private static MongoClient mongo = null;
	private static MongoClientURI mongoClientURI = null;

	/**
	 * 初始化连接池
	 * 
	 * @param uri
	 *            mongo连接URI,格式如：mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database[.collection]][?options]]
	 * @throws UnknownHostException
	 */
	public static void initMongoClient(String uri) throws UnknownHostException {
		// 其他参数根据实际情况进行添加
		// private final static int connectionsPerHost = 200;// 每个主机的连接数
		// private final static int threadsAllowedToBlockForConnectionMultiplier = 100; // 线程队列数，它以上面connectionsPerHost值相乘的结果就是线程队列最大值。如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
		// private final static int connectTimeout = 2000; // 连接超时的毫秒。0是默认和无限
		// private final static int maxWaitTime = 1000; // 最大等待连接的线程阻塞时间
		// private final static int socketTimeout = 1000; // socket超时。0是默认和无限
		MongoClientOptions.Builder builder = MongoClientOptions.builder().connectionsPerHost(2000).threadsAllowedToBlockForConnectionMultiplier(500).connectTimeout(5000).maxWaitTime(120000)
				.socketTimeout(0).readPreference(ReadPreference.secondaryPreferred()).writeConcern(WriteConcern.REPLICA_ACKNOWLEDGED).socketKeepAlive(true).minConnectionsPerHost(20);
				/*
				 * socketKeepAlive=false
				 * #true:假如链接不能建立时,驱动将重试相同的server,有最大的重试次数,默认为15次,这样可以避免一些server因为一些阻塞操作零时down而驱动抛出异常,这个对平滑过度到一个新的master,也是很有用的,注意:当集群为复制集时,驱动将在这段时间里,尝试链接到旧的master上,而不会马上链接到新master上
				 */

		/*
		 * MongoClientOptions.Builder builder = MongoClientOptions.builder().connectionsPerHost(200).threadsAllowedToBlockForConnectionMultiplier(100).connectTimeout(5000)
		 * .maxWaitTime(5000).socketTimeout(5000).writeConcern(WriteConcern.REPLICA_ACKNOWLEDGED);
		 */
		// mongoClientURI = new MongoClientURI("mongodb://luyoutest:luyoutest123@192.168.1.13:21001,192.168.1.14:21001/luyoutest", builder);
		mongoClientURI = new MongoClientURI(uri, builder);

		mongo = new MongoClient(mongoClientURI);

		log.info("初始化数据库连接成功：{}", uri);
	}

	/**
	 * 关闭数据库连接
	 */
	public static void close() {
		if (mongo != null) {
			log.info("关闭数据库连接：{}", mongoClientURI.getURI());
			mongo.close();
		}
	}

	/**
	 * 获取当前连接数据库名称
	 * 
	 * @return
	 */
	public static String getDatabase() {
		if (mongoClientURI == null) {
			log.error("在MongoClientURI没有初始化的情况下获取Database");
			throw new LyException("000", "MongoClientURI没有初始化");
		}
		return mongoClientURI.getDatabase();
	}

	/**
	 * 获取当前连接DB
	 * 
	 * @return
	 */
	public static DB getDB() {
		if (mongo == null) {
			log.error("在MongoClient没有初始化的情况下获取DB");
			throw new LyException("000", "MongoClient没有初始化");
		}
		return mongo.getDB(getDatabase());
	}

	/**
	 * 获取DB连接
	 * 
	 * @param dbName
	 * @return
	 */
	public static DB getDB(String dbName) {
		if (mongo == null) {
			log.error("在MongoClient没有初始化的情况下获取DB");
			throw new LyException("000", "MongoClient没有初始化");
		}
		return mongo.getDB(dbName);
	}

	/**
	 * 获取upload GridFS
	 */
	public static GridFS getGridFS() {
		GridFS myFS = new GridFS(getDB(), "upload");
		return myFS;
	}

	/**
	 * 获取指定GridFS
	 */
	public static GridFS getGridFS(String bucket) {
		GridFS myFS = new GridFS(getDB(), bucket);
		return myFS;
	}

	/**
	 * 获取当前数据库对象
	 * 
	 * @param databaseName
	 * @return
	 */
	// public static MongoDatabase getMongoDatabase(String databaseName) {
	// if (mongo == null) {
	// log.error("在MongoClient没有初始化的情况下获取MongoDatabase");
	// throw new LyException("000", "MongoClient没有初始化");
	// }
	// return mongo.getDatabase(databaseName);
	// }

	/**
	 * 获取集合（表）
	 * 
	 * @param collectionName
	 */
	public static DBCollection getCollection(String db, String collectionName) {
		return getDB(db).getCollection(collectionName);
	}

	/**
	 * 获取集合（表）
	 * 
	 * @param collectionName
	 */
	public static DBCollection getCollection(String collectionName) {
		return getDB(mongoClientURI.getDatabase()).getCollection(collectionName);
	}

	/**
	 * 获取集合
	 * 
	 * @param collectionName
	 * @return
	 */
	// public static MongoCollection<Document> getMongoCollection(String collectionName) {
	// return getMongoDatabase(mongoClientURI.getDatabase()).getCollection(collectionName);
	// }

	/**
	 * 获取集合
	 * 
	 * @param databaseName
	 * @param collectionName
	 * @return
	 */
	// public static MongoCollection<Document> getMongoCollection(String databaseName, String collectionName) {
	// return getMongoDatabase(databaseName).getCollection(collectionName);
	// }

	/**
	 * 插入文档
	 * 
	 * @param collectionName
	 * @param document
	 */
	// public void insert(String collectionName, Document document) {
	// getMongoCollection(collectionName).insertOne(document);
	// }

	/**
	 * 插入
	 * 
	 * @param collectionName
	 * @param map
	 */
	public void insert(String collectionName, Map<String, Object> map) {
		try {
			DBObject dbObject = toDBObject(map);
			getCollection(collectionName).insert(dbObject);
		} catch (MongoException e) {
			log.error("MongoException:" + e.getMessage());
		}
	}

	/**
	 * 插入
	 * 
	 * @param collectionName
	 * @param json
	 */
	public void insert(String collectionName, String json) {
		try {
			DBObject dbObject = toDBObject(json);
			getCollection(collectionName).insert(dbObject);
		} catch (MongoException e) {
			log.error("MongoException:" + e.getMessage());
		}
	}

	/**
	 * 插入
	 * 
	 * @param collectionName
	 * @param obj
	 */
	public void insert(String collectionName, Object obj) {
		try {
			DBObject dbObject = toDBObject(obj);
			getCollection(collectionName).insert(dbObject);
		} catch (MongoException e) {
			log.error("MongoException:" + e.getMessage());
		}
	}

	/**
	 * 批量插入
	 * 
	 * @param collectionName
	 * @param list
	 */
	public void insertBatch(String collectionName, List<Object> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		try {
			List<DBObject> listDB = new ArrayList<DBObject>();
			for (int i = 0; i < list.size(); i++) {
				DBObject dbObject = toDBObject(list.get(i));
				listDB.add(dbObject);
			}
			getCollection(collectionName).insert(listDB);
		} catch (MongoException e) {
			log.error("MongoException:" + e.getMessage());
		}
	}

	/**
	 * 删除
	 * 
	 * @param collectionName
	 * @param map
	 */
	public void delete(String collectionName, Map<String, Object> map) {
		DBObject obj = toDBObject(map);
		getCollection(collectionName).remove(obj);
	}

	/**
	 * 删除
	 * 
	 * @param collectionName
	 * @param obj
	 */
	public void delete(String collectionName, Object obj) {
		DBObject dbobj = toDBObject(obj);
		getCollection(collectionName).remove(dbobj);
	}

	/**
	 * 删除全部
	 * 
	 * @param collectionName
	 * @param map
	 */
	public void deleteAll(String collectionName) {
		List<DBObject> rs = findAll(collectionName);
		if (rs != null && !rs.isEmpty()) {
			for (int i = 0; i < rs.size(); i++) {
				getCollection(collectionName).remove(rs.get(i));
			}
		}
	}

	/**
	 * 批量删除
	 * 
	 * @param collectionName
	 * @param list
	 */
	public void deleteBatch(String collectionName, List<Object> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		for (int i = 0; i < list.size(); i++) {
			getCollection(collectionName).remove(toDBObject(list.get(i)));
		}
	}

	/**
	 * 计算满足条件条数
	 * 
	 * @param collectionName
	 * @param map
	 */
	public long getCount(String collectionName, Map<String, Object> map) {
		return getCollection(collectionName).getCount(toDBObject(map));
	}

	/**
	 * 计算集合总条数
	 * 
	 * @param collectionName
	 * @param map
	 */
	public long getCount(String collectionName) {
		return getCollection(collectionName).find().count();
	}

	/**
	 * 更新
	 * 
	 * @param collectionName
	 * @param setFields
	 * @param whereFields
	 */
	public void update(String collectionName, Map<String, Object> setFields, Map<String, Object> whereFields) {
		DBObject obj1 = toDBObject(setFields);
		DBObject obj2 = toDBObject(whereFields);
		getCollection(collectionName).updateMulti(obj1, obj2);
	}

	/**
	 * 查找对象（根据主键_id）
	 * 
	 * @param collectionName
	 * @param _id
	 */
	public DBObject findById(String collectionName, String _id) {
		DBObject obj = new BasicDBObject();
		obj.put("_id", new ObjectId(_id));
		return getCollection(collectionName).findOne(obj);
	}

	/**
	 * 查找集合所有对象
	 * 
	 * @param collectionName
	 */
	public List<DBObject> findAll(String collectionName) {
		return getCollection(collectionName).find().toArray();
	}

	/**
	 * 查找（返回一个对象）
	 * 
	 * @param map
	 * @param collectionName
	 */
	public DBObject findOne(String collectionName, Map<String, Object> map) {
		DBCollection coll = getCollection(collectionName);
		return coll.findOne(toDBObject(map));
	}

	/**
	 * 查找（返回一个List<DBObject>）
	 * 
	 * @param <DBObject>
	 * @param map
	 * @param collectionName
	 * @throws Exception
	 */
	public List<DBObject> find(String collectionName, Map<String, Object> map) throws Exception {
		DBCollection coll = getCollection(collectionName);
		DBCursor c = coll.find(toDBObject(map));
		if (c != null)
			return c.toArray();
		else
			return null;
	}

	/**
	 * java 对象转换成DBObject对象
	 * 
	 * @param obj
	 * @return
	 */
	public static DBObject toDBObject(Object obj) {
		return (DBObject) JSON.parse(JsonUtil.toJson(obj));
	}

	/**
	 * 
	 * @param json
	 * @return
	 */
	public static DBObject jsonToDBObject(String json) {
		Map<String, Object> map = JsonUtil.fromJson(json, new TypeReference<Map<String, Object>>() {
		});

		DBObject object = mapToDBObject(map);
		return object;
	}

	/**
	 * 
	 * @param json
	 * @return
	 */
	public static List<DBObject> jsonToDBObjectList(String json) {
		List<Map<String, Object>> maplist = JsonUtil.fromJson(json, new TypeReference<List<Map<String, Object>>>() {
		});
		List<DBObject> list = new ArrayList<DBObject>();
		for (Map<String, Object> map : maplist) {
			list.add(mapToDBObject(map));
		}

		return list;
	}

	/**
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static DBObject mapToDBObject(Map<String, Object> map) {
		DBObject object = new BasicDBObject();

		if (map == null) {
			return object;
		}

		Set<String> keys = map.keySet();
		for (String key : keys) {
			if (map.get(key) instanceof List) {
				List l = (List) map.get(key);
				if (l != null && l.size() > 0 && l.get(0) instanceof Map) {
					List<Map<String, Object>> list = (List<Map<String, Object>>) map.get(key);
					List<DBObject> list2 = new ArrayList<DBObject>();
					for (Map<String, Object> item : list) {
						list2.add(new BasicDBObject(item));
					}
					object.put(key, list2);
				} else {
					object.put(key, map.get(key));
				}
			} else {
				object.put(key, map.get(key));
			}
		}
		return object;
	}

	/**
	 * 字段过滤
	 * 
	 * @param object
	 *            DBObject对象
	 * @param keys
	 *            要保留的key
	 * @return
	 */
	public static DBObject keyFilter(DBObject object, String... keys) {
		if (object != null) {
			DBObject data = new BasicDBObject();
			for (String key : keys) {
				data.put(key, object.get(key));
			}
			return data;
		}
		return null;
	}

	/**
	 * json 字符串转换成DBObject对象
	 * 
	 * @param json
	 * @return
	 */
	public static DBObject toDBObject(String json) {
		return (DBObject) JSON.parse(json);
	}

	/**
	 * 计算总页数
	 * 
	 * @param rows
	 *            每页条数
	 * @param total
	 *            总条数
	 * @return
	 */
	public static Long getTotalPage(Integer rows, Long total) {
		return (total + rows - 1) / rows;
	}

	/**
	 * 计算总页数
	 * 
	 * @param rows
	 *            每页条数
	 * @param total
	 *            总条数
	 * @return
	 */
	public static Object getTotalPage(Integer rows, int total) {
		return (total + Long.valueOf(rows) - 1) / rows;
	}

}
