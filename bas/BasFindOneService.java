/**
 * 
 */
package com.luyouchina.train.core.modules.bas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;

import com.luyouchina.comm.ParamUtil;
import com.luyouchina.comm.model.RequestObject;
import com.luyouchina.comm.model.ResponseObject;
import com.luyouchina.train.core.common.annotation.ServiceMethod;
import com.luyouchina.train.core.common.annotation.ServiceModule;
import com.luyouchina.train.core.common.repository.BasicRepository;
import com.luyouchina.train.core.common.repository.BasicServiceConfRepository;
import com.luyouchina.train.core.common.util.SingleMongoFactory;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;

/**
 * @author yuxh
 *
 */
@ServiceModule("bas")
public class BasFindOneService {
	private BasicServiceConfRepository basicServiceConfRepository;
	public BasFindOneService() {
		 basicServiceConfRepository = SingleMongoFactory.getBean(BasicServiceConfRepository.class, "service_findone_conf");
	}

	// --------------------------------------------------------------------------------------------------------
	/**
	 * 【XXXX】动态通用查询
	 * 算法要领        解析传入的参数，依据下面的配置项处理
	 * 服务名：        1.service       格式为aaa.bbb字符串   前端传入的服务名
	 * 必传验证键    2.mustkeys      格式为 [aaa,bbb,ccc]数组
	 * 查询参数键        3.findkeys     格式为{aaa:I,bbb:L,a:"P",b:[a,b...],c:>=,d:>=D}.....
	 * 内定固化键    4.fixedkeys     格式为 {键:值,aaa:[1,2,3],...}
	 * 排序键            5.sortkeys      格式为{aaa:A,bbb:D,....}
	 * 查询集合表    6.collection    格式为 字符串
	 * 增加打入的键            7.addkeys  格式为 {aaa:xxx,bbb:xxx,...}
	 * 打入嵌入的集合    支持多层         8.embedkeys [{集合:xxx,inkeys:{key:xxx,bbb:[xxx,yyy,..数组]},usedkeys:{keyold:keynew,...可多个},needkeys:[aaa,bb,...]},
	 *  ....可多个集合支持递归分]
	 * 替换键             9.replacekeys  格式为 {aaa:xxx,...}
	 * 汉化代码来自缓存  10.namerediskeys  格式为 {aaa:xxx,...}
	 * 移除key  11.removekeys     格式为 [aaa,bbb,ccc,....]
	 * 类型转换     12.transformkeys    格式为  {aaa:tofront,bbb:toback,ccc:toboolean....}
	 * 待返回的键 13.returnkeys   格式为   [aaa,bbb,ccc]
	 * 无数据返回的提示  14. message   {id:msg}
	 * 
	 *   { "service":XXX,
	 *   collection:xxx,
	 *   mustkeys:[xxx,...数组],
	 *   findkeys:{aaa:xxx,bbb:(I,L,[,P,>,<,>=,<=,>=D,<=D,)},
	 *   fixedkeys:{aaa:xxx,bbb:[XXX,...数组]},
	 *   sortkeys:{aaa:A,bbb:D,....}, 暂时未使用哈
	 *   addkeys:{aaa:xxx,bbb:xxx,...},
	 *   embedkeys:[{集合:xxx,inkeys:{key:xxx,bbb:[xxx,yyy,..数组]},usedkeys:{keyold:keynew,...可多个},needkeys:[aaa,bb,...]},
	 *  ....可多个集合支持递归分],
	 *   replacekeys:{aaa:xxx,...},
	 *   namerediskeys:{aaa:xxx,...},
	 *   removekeys:[aaa,bbb,ccc,....],
	 *   transformkeys:{aaa:xxx,bbb:xxx,....},
	 *   returnkeys:[aaa,bbb,ccc],
	 *   message:{id:msg}}
	 * }
	 * @param request
	 * @return
	 * 
	 */
	@ServiceMethod("getFindOne")
	public ResponseObject getFindOne(RequestObject request) {
		ResponseObject response = new ResponseObject(request);
		Map<String, Object> param = new HashMap<String, Object>();
		param = request.getParam();
		//1.service  格式为aaa.bbb
		String  serviceName=(String)param.get("service");
		if (serviceName==null) {
		    response.setErrorMsg("0004", "缺少服务名");
			return response;
		}
		List<BasicDBObject> list = new ArrayList<BasicDBObject>();
		BasicDBObject queryObj = new BasicDBObject(); // 构建查询条件
		queryObj.put("service",serviceName);
		DBObject object = new BasicDBObject();
		object = basicServiceConfRepository.findOne(queryObj);
		if (object==null) {
			response.setErrorMsg("0004", "服务未配置");
			return response;
		}
		//2.mustkeys  格式为 [aaa,bbb,ccc]
        
		if (object.get("mustkeys") instanceof BasicDBList) {
			BasicDBList mustArr=(BasicDBList)object.get("mustkeys");
			String[] mustkeys=new String[mustArr.size()];	
			mustkeys=(String[])mustArr.toArray(mustkeys);
			//System.out.println(java.util.Arrays.asList(mustkeys));
			String MissParam = ParamUtil.RequiredValid(param,mustkeys);
			if (!"".equals(MissParam)) {
				response.setErrorMsg("0003", MissParam);
				return response;
			}
			
		}	
		// ---------------------------------------追加参数核查
		
		//3.findkeys:{aaa:xxx,bbb:(I,L,[,P,>,<,>=,<=,>=D,<=D,)},
		//{可选参数 模糊、in等、时间的>=、<= ,及可选参数的转换}
		for (String Dbfindkey:((DBObject)object.get("findkeys")).keySet()) {
			if ("rows".equals(Dbfindkey) || "page".equals(Dbfindkey) || "lastitemid".equals(Dbfindkey)) {
				continue;
			}
		        switch ((String)((DBObject)object.get("findkeys")).get(Dbfindkey)) {
		        case "_id":
					if (param.get(Dbfindkey)!=null && !"".equals(param.get(Dbfindkey))) {
						list.add(new BasicDBObject(Dbfindkey, new ObjectId((String)param.get(Dbfindkey))));	
					}
					break;
				case "I":
					if (param.get(Dbfindkey)!=null && !"".equals(param.get(Dbfindkey))) {
						list.add(new BasicDBObject(Dbfindkey, Integer.valueOf((String)param.get(Dbfindkey))));	
					}
					break;
				case "L":
					if (param.get(Dbfindkey)!=null && !"".equals(param.get(Dbfindkey))) {
						list.add(new BasicDBObject(Dbfindkey, Long.valueOf((String)param.get(Dbfindkey))));		
					}
					break;
				case "[":
					if (param.get(Dbfindkey)!=null && !"".equals(param.get(Dbfindkey)) && !"[]".equals(param.get(Dbfindkey))) {
						if (param.get(Dbfindkey).toString().indexOf("\"") >= 0) {
							List<String> datas = (List<String>)param.get(Dbfindkey);
							if (datas != null && datas.size() > 0) {
								list.add(new BasicDBObject(Dbfindkey, new BasicDBObject(
										QueryOperators.IN, datas)));
							} else {
								List<Integer> dataInt = (List<Integer>) param.get(Dbfindkey);
								if (dataInt != null && dataInt.size() > 0) {
									list.add(new BasicDBObject(Dbfindkey,
											new BasicDBObject(QueryOperators.IN,
													dataInt)));
								}
							}

						}
					}
					break;
				case ">=":
					if (param.get(Dbfindkey)!=null && !"".equals(param.get(Dbfindkey))) {
						list.add(new BasicDBObject(Dbfindkey, new BasicDBObject(QueryOperators.GTE,param.get(Dbfindkey))));
					}
					break;
				case ">":
					if (param.get(Dbfindkey)!=null && !"".equals(param.get(Dbfindkey))) {
						list.add(new BasicDBObject(Dbfindkey, new BasicDBObject(QueryOperators.GT,param.get(Dbfindkey))));
					}
					break;
				case "<=D":
					if (param.get(Dbfindkey)!=null && !"".equals(param.get(Dbfindkey))) {
						param.put(Dbfindkey,param.get(Dbfindkey).toString().concat(" 23:59:59"));
						list.add(new BasicDBObject(Dbfindkey, new BasicDBObject(QueryOperators.LTE,param.get(Dbfindkey))));
					}
					break;
			    case "<D":
					if (param.get(Dbfindkey)!=null && !"".equals(param.get(Dbfindkey))) {
						param.put(Dbfindkey,param.get(Dbfindkey).toString().concat(" 23:59:59"));
						list.add(new BasicDBObject(Dbfindkey, new BasicDBObject(QueryOperators.LT,param.get(Dbfindkey))));
					}
					break;
			    case "<=":
					if (param.get(Dbfindkey)!=null && !"".equals(param.get(Dbfindkey))) {
						list.add(new BasicDBObject(Dbfindkey, new BasicDBObject(QueryOperators.LTE,param.get(Dbfindkey))));
					}
					break;
			    case "<":
					if (param.get(Dbfindkey)!=null && !"".equals(param.get(Dbfindkey))) {
						list.add(new BasicDBObject(Dbfindkey, new BasicDBObject(QueryOperators.LT,param.get(Dbfindkey))));
					}
					break;
			    case "P":
					if (param.get(Dbfindkey)!=null && !"".equals(param.get(Dbfindkey))) {
						list.add(new BasicDBObject(Dbfindkey, Pattern.compile("^.*" + Pattern.quote(param.get(Dbfindkey).toString()) + ".*$", Pattern.CASE_INSENSITIVE)));
					}
					break;
				default:
					if (param.get(Dbfindkey)!=null && !"".equals(param.get(Dbfindkey))) {
						list.add(new BasicDBObject(Dbfindkey,param.get(Dbfindkey)));	
					}
					break;
				}
		}
	
		//4.fixedkeys  格式为aaa#bbb;aaa#[1,2,3]
		//fixedkeys:{aaa:xxx,bbb:[XXX,...数组]},
		//{内定参数isdelete等，方式 isdelete#9;aa#[1,2,3]
		for (String Dbfixedkey:((DBObject)object.get("fixedkeys")).keySet()) {
			if (((DBObject)object.get("fixedkeys")).get(Dbfixedkey) instanceof Integer) {
				list.add(new BasicDBObject(Dbfixedkey, Integer.valueOf(((DBObject)object.get("fixedkeys")).get(Dbfixedkey).toString())));
			}else if (((DBObject)object.get("fixedkeys")).get(Dbfixedkey) instanceof String) {
				list.add(new BasicDBObject(Dbfixedkey,((DBObject)object.get("fixedkeys")).get(Dbfixedkey)));
			}else if (((DBObject)object.get("fixedkeys")).get(Dbfixedkey) instanceof BasicDBList) {
				list.add(new BasicDBObject(Dbfixedkey, new BasicDBObject(
						QueryOperators.IN, ((DBObject)object.get("fixedkeys")).get(Dbfixedkey))));
			}
		}
		
		queryObj.put(QueryOperators.AND, list);
		BasicDBObject sortObj = new BasicDBObject(); //
		//5.sortkeys   格式 sortkeys:{aaa:A,bbb:D,....},
		
		/*
		for (String Dbsortkey:((DBObject)object.get("sortkeys")).keySet()) {
			if ("A".equalsIgnoreCase(((DBObject)object.get("sortkeys")).get(Dbsortkey).toString())) {
				sortObj.append(Dbsortkey, 1);
			} else {
				sortObj.append(Dbsortkey, -1);
			}
		}
		*/
		
		//6.collection   格式为 aaa
		BasicRepository  basicRepository=new  BasicRepository((String)object.get("collection"));
		queryObj.remove("service");
		//System.out.println(queryObj);
		queryObj.put(QueryOperators.AND, list);
		DBObject objectOne = basicRepository.findOne(queryObj);
		
		//7.addkeys    格式为 {aaa:bbb;ccc:ppp}
		//  调整为支持分层键的加入哈
		
		for (String Dbaddkey:((DBObject)object.get("addkeys")).keySet()) {							
			basicRepository.addKey(objectOne,"",Dbaddkey, ((DBObject)object.get("addkeys")).get(Dbaddkey).toString());
		}
		
		//8.embedkeys   格式为 aaa#bbb;ccc#ppp,ddd
		//打入节点   关联集合表#关联键集合a,b,c#返回打入的键哈a,b,c
		//embed
		/*
		 * embedkeys:[{集合:xxx,inkeys:{key:xxx,bbb:[xxx,yyy,..数组]},usedkeys:{keyold:keynew,...可多个},needkeys:[aaa,bb,..]},
	     ....可多个集合支持递归分]  ->四个键collection,inkeys,usedkeys,needkeys
		 */
			
		for (DBObject embedkey:(List<DBObject>)object.get("embedkeys")) {
			//String[] strArray = new String[ (BasicDBList)(embedkey.get("needkeys"))]; 	
			BasicDBList embedArr=(BasicDBList)(embedkey.get("needkeys"));
			String[] strArray=new String[embedArr.size()];	
			strArray=(String[])embedArr.toArray(strArray);
			basicRepository.addCodeName(objectOne,"", embedkey.get("collection").toString(),embedkey.get("method").toString(),
					(DBObject)embedkey.get("inkeys"), 
					(DBObject)embedkey.get("usedkeys") ,strArray);
			
		}
		//addCodeName(Object object,String parentkey, String searchCollection,String method,DBObject inkeys, DBObject usedkeys,String... addKeys) {
		
		
		//9.replacekeys  格式为 {aaa:bbb;ccc:ddd}
		//替换key	
		for (String Dbreplacekey:((DBObject)object.get("replacekeys")).keySet()) {				
			basicRepository.replaceKey(objectOne,"",Dbreplacekey, ((DBObject)object.get("replacekeys")).get(Dbreplacekey).toString());
		}
				
		//10.namerediskeys  格式为  {aaa:bbb;ccc:ddd}
		//处理代码来自缓存  汉化业务代码  支持子层的汉化
		for (String Dbnamerediskey:((DBObject)object.get("namerediskeys")).keySet()) {				
			basicRepository.addCodeNameRedis(objectOne,"",Dbnamerediskey,((DBObject)object.get("namerediskeys")).get(Dbnamerediskey).toString());// 旧键、新键
		}
		
		
		//11.removekeys  格式为 [aaa,bbb]
		//移除key  支持子层的去除节点
		
		BasicDBList removeArr=(BasicDBList)object.get("removekeys");
		String[] removekeys=new String[removeArr.size()];	
		removekeys=(String[])removeArr.toArray(removekeys);
		basicRepository.removeFields(objectOne,"", removekeys);
	
		//12.transformkeys    格式为 transformkeys:{aaa:xxx,bbb:xxx,....}
		//transformkeys
		//类型变换tofront /1000  toback * 1000,假如子表类型变换哈 比如含有数字的转换
	    for (String Dbtransformkey:((DBObject)object.get("transformkeys")).keySet()) {
	    	switch (((DBObject)object.get("transformkeys")).get(Dbtransformkey).toString()) {
			case "tofront":
				basicRepository.covertToFront(objectOne,"", Dbtransformkey);
				break;
			case "toback":
				basicRepository.covertToBack(objectOne,"", Dbtransformkey);
				break;
			case "toboolean":
				basicRepository.covertToBoolean(objectOne,"",Dbtransformkey);
				break;
			default:
				break;
			}
		 }	
		//13.returnkeys   格式为  returnkeys:[aaa,bbb,ccc]
	    //只返回指定的域   
	    BasicDBList returnArr=(BasicDBList)object.get("returnkeys");
		String[] returnkeys=new String[returnArr.size()];	
		returnkeys=(String[])returnArr.toArray(returnkeys);
	    
		objectOne=(DBObject)basicRepository.returnFields(objectOne,null,"",returnkeys);
		//-----------------------------------------------------------------------------------
		//14.处理错误提示信息 message id#msg
		if (objectOne==null && (DBObject)object.get("message")!=null) {
			response.setErrorMsg((String)((DBObject)object.get("message")).get("id"), (String)((DBObject)object.get("message")).get("msg"));//返回提示信息
		}
		//-----------------------------------------------------------------------------------
		response.setData(objectOne);
		return response;
	}
	// --------------------------------------------------------------------------------------------------------
}
