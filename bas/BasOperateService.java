/**
 * 
 */
package com.luyouchina.train.core.modules.bas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.luyouchina.comm.DateUtil;
import com.luyouchina.comm.RandUtil;
import com.luyouchina.comm.model.RequestObject;
import com.luyouchina.comm.model.ResponseObject;
import com.luyouchina.train.core.common.annotation.ServiceMethod;
import com.luyouchina.train.core.common.annotation.ServiceModule;
import com.luyouchina.train.core.common.repository.BasicRepository;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * @author yuxh
 *
 */
@ServiceModule("trans")
public class BasOperateService {
	// --------------------------------------------------------------------------------------------------------
	/**
	 * 【XXXX】动态通用、增、删、查、修改操作 支持事务的操作哈`
	 * 算法要领      
update单条数据修改	{transId:"xxx",collection:XXX,query:{aaa:bbb,…},data:{aaa:xxx,bbb:xxx},returnFields:[aa,bb,cc],string:false}
updateMuli多行修改{transId:"xxx",collection:XXX,query:{aaa:bbb,…},data:{aaa:xxx,bbb:xxx}}
insert 单条数据插入{transId:"xxx",collection:XXX,data:{aaa:xxx,bbb:xxx},returnFields:[aa,bb,cc],string:false}
insetList多条插入	{transId:"xxx",collection:XXX,data:[{_id:xxx,aaa:xxx,bbb:xxx}]}
delete删除数据	{transId:"xxx",collection:XXX,query:{aaa:bbb,…}}
select提取单条数据	{transId:"xxx",collection:XXX,query:{aaa:bbb,…},sort:{},returnFields:[aa,bb,cc],string:false}
selectList提取多条数据	{transId:"xxx",collection:XXX,query:{aaa:bbb,…},returnFields:[aa,bb,cc]}	 
	      结束事务的处理    这是另一处理函数     更改状态
                回滚的数据项
      //{transId:"xxxx":state:pending,操作时间:XXXX,时间搓:xxx,:operates:[{集合,operate:delete,query:XXXX,data:{}}]]
      //{transId:"xxxx":state:pending,操作时间:XXXX,时间搓:xxx,:operates:[{集合,operate:delete,data:[{query:xxx},data:{aaa:bbb,ccc:ppp}]}]]
	 * @param request
	 * @return
	 * 
	 */
	@ServiceMethod("update")
	public ResponseObject update(RequestObject request) {
		ResponseObject response = new ResponseObject(request);
		Map<String, Object> params = new HashMap<String, Object>();
		params = request.getParam();
		//-1、取事务id号 transId------------------------------------
		String transId=(String)params.get("transId");
		if (transId==null || "".equals(transId.trim())) {
			response.setErrorMsg("880101", "无效事务，系统异常");
			return  response;
		}		
		BasicDBObject  rollquery=new BasicDBObject();
		rollquery.put("transId",transId);
		//-2、操作固化为 update单条
		String operate="update";
		String collection=(String)params.get("collection");
		if (collection==null || "".equals(collection))  {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("880107", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880102", "无效集合参数，系统异常");
			return  response;
		}
		//-3、查询条件对象 query
		DBObject  query=new BasicDBObject();
		if  (params.get("query") instanceof java.util.LinkedHashMap ) {
			//查询
			query=(DBObject)JSON.parse(JSON.serialize(params.get("query")));
		}else {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("880107", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880103", "无效查询数据，系统异常");
			return  response;
		}
			
		//-4、更新的数据 对象域 data	
		DBObject data=new BasicDBObject();
		if (params.get("data") instanceof java.util.LinkedHashMap) {
			data=(DBObject)JSON.parse(JSON.serialize(params.get("data")));
		}else {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("880107", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880104", "待更新的节点data数据无效");
			return  response;
		}
		
		//-5、待返回的节点域 returnFields
		String[] returnFields={};
		if (params.get("returnFields") instanceof  java.util.ArrayList) {
        	List<String> returnDBO=(List<String>)(params.get("returnFields"));
			returnFields=new String[returnDBO.size()];	
			returnFields=(String[])returnDBO.toArray(returnFields);
        }
        
		//-6、查询出旧数据 用以放入待回滚的数据项中
		BasicRepository  basicRepository=new  BasicRepository(collection);
		DBObject  dbo=basicRepository.findOne(query);
		//集合   查询条件   旧数据遍历出来
		//{transId:"xxxx":state:pending,操作时间:XXXX,时间搓:xxx,:operates:[{集合,operate:delete,query:XXXX,data:{}}]]
		if (dbo==null) {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("880107", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880105", "无数据，更新异常");
			return  response;
		}
		//分解出数据项  dataDBO 取出第一层数据节点
		List<String> retFields=new ArrayList<String>();
		getOneLevelfields(data,retFields);
		String[] rollDatafields=new String[retFields.size()];	
		rollDatafields=(String[])retFields.toArray(rollDatafields);
		//转换到数组哈--------------------------
		DBObject rollDBO=basicRepository.returnFields(dbo, rollDatafields);//回滚数据
		//System.out.println("retFields="+data);
		//System.out.println("rollDatafields="+java.util.Arrays.toString(rollDatafields));
		//System.out.println("rollDBO="+rollDBO);
		
		BasicDBObject  rolldata=new BasicDBObject();
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("transId",transId);
		map.put("state","pending");
		map.put("createtime",DateUtil.getDateTime());
		map.put("timestamp",System.currentTimeMillis());
		map.put("endtime",System.currentTimeMillis()+1000);
		rolldata.append("$set",map);
				
		Map<String,Object> mapoperates=new HashMap<String, Object>();
		mapoperates.put("collection", collection);
		mapoperates.put("operate","update");
		
		mapoperates.put("query",new BasicDBObject("_id",new ObjectId(dbo.get("_id").toString())));
		mapoperates.put("data",rollDBO);
		rolldata.append("$push",new BasicDBObject("operates",mapoperates));
	    //System.out.println("rolldata="+rolldata);	
		
		BasicRepository  basicRepositoryRoll=new  BasicRepository("trans_rollback");
		//-7、插入新的待回滚数据操作
		if (basicRepositoryRoll.updateOrinsert(rollquery, rolldata)!=1) {
			response.setErrorMsg("880106", "事务异常");
			return  response;
		}
		//-8、正式更改数据
		if ("update".equals(operate)) {
			if (basicRepository.update(query, data)!=1) {
				//不成功，则回滚整个事务
				if (rollBackData(rollquery)!=1) {
					response.setErrorMsg("880107", "回滚失败，系统异常");
					return  response;
				}
			}else {
				//-9、返回键值域项
				((BasicDBObject)query).clear();
				query.put("_id",new ObjectId(dbo.get("_id").toString()));//按主键查询数据
				DBObject  fields=new BasicDBObject();
				for (String key:returnFields) {
		        	fields.put(key,1);
		        }				
				DBObject  returndbo=basicRepository.findOne(query,fields);
				for(String field:returnFields){
			        if  (field.startsWith("*")) {
			        	basicRepository.replaceId(returndbo,field.substring(field.indexOf("*") + 1));
			        	break;
			        }
				}
				//返回指定的键域				
				if (params.get("string")!=null) {
					if (!returndbo.toMap().isEmpty()) {
						response.setData(returndbo.get(params.get("string").toString()));
					}else {
						response.setData("");
					}
				}else {
					response.setData(returndbo);
				}
				
			}
		}
		return response;
	}
	// --------------------------------------------------------------------------------------------------------

	@ServiceMethod("updateMuli")
	public ResponseObject updateMuli(RequestObject request) {
		ResponseObject response = new ResponseObject(request);
		Map<String, Object> params = new HashMap<String, Object>();
		params = request.getParam();
		//-1、取事务id号 transId------------------------------------
		String transId=(String)params.get("transId");
		if (transId==null || "".equals(transId.trim())) {
			response.setErrorMsg("880101", "无效事务，系统异常");
			return  response;
		}
		
		BasicDBObject  rollquery=new BasicDBObject();
		rollquery.put("transId",transId);
				
		//-2、操作固化为 update单条
		String operate="updateMuli";
		String collection=(String)params.get("collection");
		if (collection==null || "".equals(collection))  {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("880107", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880102", "无效集合参数，系统异常");
			return  response;
		}
		//-3、查询条件对象 query
		DBObject  query=new BasicDBObject();  
		if  (params.get("query") instanceof java.util.LinkedHashMap ) {
			//查询
			query=(DBObject)JSON.parse(JSON.serialize(params.get("query")));	
		}else {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("880107", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880103", "无效查询数据，系统异常");
			return  response;
		}
			
		//-4、更新的数据 对象域 data	
		DBObject data=new BasicDBObject();
		if (params.get("data") instanceof java.util.LinkedHashMap) {
			data=(DBObject)JSON.parse(JSON.serialize(params.get("data")));
		}else {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("880107", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880104", "待更新的节点data数据无效");
			return  response;
		}
		
		//-5、待返回的节点域 returnFields
		/*
		String[] returnFields={};
        if (params.get("returnFields") instanceof  java.util.ArrayList) {
        	List<String> returnDBO=(List<String>)(params.get("returnFields"));
			returnFields=new String[returnDBO.size()];	
			returnFields=(String[])returnDBO.toArray(returnFields);
        }
        */
		
		//-6、查询出旧数据 用以放入待回滚的数据项中
		BasicRepository  basicRepository=new  BasicRepository(collection);
		List<DBObject>   dbo=basicRepository.findList(query);
		//集合   查询条件   旧数据遍历出来
		//{transId:"xxxx":state:pending,操作时间:XXXX,时间搓:xxx,:operates:[{集合,operate:delete,query:XXXX,data:[{},...]}]]
		if (dbo==null) {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("880107", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880105", "无数据，更新异常");
			return  response;
		}
		//分解出数据项  dataDBO 取出第一层数据节点
		List<String> retFields=new ArrayList<String>();
		getOneLevelfields(data,retFields);
		//-----------------------------------加入_id且同时去重复
		retFields.add("_id");
		HashSet h = new HashSet(retFields);
		retFields.clear();
		retFields.addAll(h);
	    //-----------------------------------end yuxh on 2015-10-27 
		String[] rollDatafields=new String[retFields.size()];	
		rollDatafields=(String[])retFields.toArray(rollDatafields);
		
		//转换到数组哈--------------------------
		List<DBObject> rollDBO=basicRepository.returnFields(dbo, rollDatafields);//回滚数据
		List<DBObject> rollDBOSub=new ArrayList<DBObject>();
		//query:{},data:{}
		for (DBObject dboRoll:rollDBO) {
			DBObject DBOsub=new BasicDBObject();
			DBObject querysub=new BasicDBObject();
			DBObject dataSub=new BasicDBObject();
			for (String key:dboRoll.keySet()) {
				if ("_id".equals(key)) {
					querysub.put("_id", new ObjectId(dboRoll.get("_id").toString()));
				}else {
					DBOsub.put(key,dboRoll.get(key));
				}
			}
			dataSub.put("query", querysub);
			dataSub.put("data", DBOsub);
			rollDBOSub.add(dataSub);
		}
		
		BasicDBObject  rolldata=new BasicDBObject();
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("transId",transId);
		map.put("state","pending");
		map.put("createtime",DateUtil.getDateTime());
		map.put("timestamp",System.currentTimeMillis());
		map.put("endtime",System.currentTimeMillis()+1000);
		rolldata.append("$set",map);
				
		Map<String,Object> mapoperates=new HashMap<String, Object>();
		mapoperates.put("collection", collection);
		mapoperates.put("operate","updateMuli");
		//循环构造查询条件--------------------------------------------------------------------
		/*
		List<Object>  idList=new ArrayList<Object>();
		for (DBObject queryA:dbo) {
			idList.add(queryA.get("_id"));
		}
		mapoperates.put("query",new BasicDBObject("_id",new BasicDBObject("$in",idList)));
		*/
		//--------------------------------------------------------------------------------
		mapoperates.put("data",rollDBOSub);		//query:{},data:{} 		
		rolldata.append("$push",new BasicDBObject("operates",mapoperates));	
		BasicRepository  basicRepositoryRoll=new  BasicRepository("trans_rollback");
		//-7、插入新的待回滚数据操作
		if (basicRepositoryRoll.updateOrinsert(rollquery, rolldata)!=1) {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("880107", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880106", "事务异常");
			return  response;
		}
		//-8、正式更改数据
		if ("updateMuli".equals(operate)) {
			if (basicRepository.updateMulti(query, data)!=1) {
				//不成功，则回滚整个事务
				if (rollBackData(rollquery)!=1) {
					response.setErrorMsg("880107", "回滚失败，系统异常");
					return  response;
				}
			}
		}
		return response;
	}
	// --------------------------------------------------------------------------------------------------------
	
	@ServiceMethod("insert")
	public ResponseObject insert(RequestObject request) {
		ResponseObject response = new ResponseObject(request);
		Map<String, Object> params = new HashMap<String, Object>();
		params = request.getParam();
		//{transId:"xxx",collection:XXX,data:{aaa:xxx,bbb:xxx},returnFields:[aa,bb,cc]}
		//-1、取事务id号 transId------------------------------------
		String transId=(String)params.get("transId");
		if (transId==null || "".equals(transId.trim())) {
			response.setErrorMsg("880101", "无效事务，系统异常");
			return  response;
		}
		BasicDBObject  rollquery=new BasicDBObject();
		rollquery.put("transId",transId);
		
		//-2、操作固化为 insert单条
		String operate="insert";
		String collection=(String)params.get("collection");
		if (collection==null || "".equals(collection))  {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("880107", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880102", "无效集合参数，系统异常");
			return  response;
		}
		
		//-3、查询条件对象 query
		/*
		DBObject  query=new BasicDBObject();  
		if  (params.get("query") instanceof java.util.LinkedHashMap ) {
			//查询
			query=(DBObject)JSON.parse(JSON.serialize(params.get("query")));	
		}else {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("880107", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880103", "无效查询数据，系统异常");
			return  response;
		}
		*/
		
		//-4、更新的数据 对象域 data	
		DBObject data=new BasicDBObject();
		if (params.get("data") instanceof java.util.LinkedHashMap) {
			data=(DBObject)JSON.parse(JSON.serialize(params.get("data")));
		}else {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("880107", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880104", "待更新的节点data数据无效");
			return  response;
		}
		
		//-5、待返回的节点域 returnFields
		String[] returnFields={};
		if (params.get("returnFields") instanceof  java.util.ArrayList) {
        	List<String> returnDBO=(List<String>)(params.get("returnFields"));
			returnFields=new String[returnDBO.size()];	
			returnFields=(String[])returnDBO.toArray(returnFields);
        }
		BasicRepository  basicRepository=new  BasicRepository(collection);
		//-6、正式插入数据
		if ("insert".equals(operate)) {
			if ("0".equals(basicRepository.insert(data))) {
				//不成功，则回滚整个事务
				if (rollBackData(rollquery)!=1) {
					response.setErrorMsg("880107", "回滚失败，系统异常");
					return  response;
				}
			}else {

				BasicDBObject  rolldata=new BasicDBObject();			
				Map<String,Object> map=new HashMap<String, Object>();
				map.put("transId",transId);
				map.put("state","pending");
				map.put("createtime",DateUtil.getDateTime());
				map.put("timestamp",System.currentTimeMillis());
				map.put("endtime",System.currentTimeMillis()+1000);
				rolldata.append("$set",map);
						
				Map<String,Object> mapoperates=new HashMap<String, Object>();
				mapoperates.put("collection", collection);
				mapoperates.put("operate","delete");
				
				mapoperates.put("query",new BasicDBObject("_id",new ObjectId(data.get("_id").toString())));			
				rolldata.append("$push",new BasicDBObject("operates",mapoperates));
						
				BasicRepository  basicRepositoryRoll=new  BasicRepository("trans_rollback");
				//-7、插入新的待回滚数据操作
				if (basicRepositoryRoll.updateOrinsert(rollquery, rolldata)!=1) {
					response.setErrorMsg("880106", "事务异常");
					return  response;
				}
					
				//-8、返回键值域项
				List<DBObject> addlist=new ArrayList<DBObject>();
				//返回指定的键域
				if (params.get("string")!=null) {
					DBObject dbostring=(DBObject)(basicRepository.returnFields(data, addlist, "", returnFields));
					if (!dbostring.toMap().isEmpty()) {
						response.setData(dbostring.get(params.get("string").toString()));
					}else {
						response.setData("");
					}
				}else {
				    response.setData(basicRepository.returnFields(data, addlist, "", returnFields));
				}
			}
		}
		return response;
	}
	// --------------------------------------------------------------------------------------------------------
	
	@ServiceMethod("insertList")
	public ResponseObject insertList(RequestObject request) {
		ResponseObject response = new ResponseObject(request);
		Map<String, Object> params = new HashMap<String, Object>();
		params = request.getParam();
		//{transId:"xxx",collection:XXX,data:[{_id:xxx,aaa:xxx,bbb:xxx}]}
		//-1、取事务id号 transId------------------------------------
		String transId=(String)params.get("transId");
		if (transId==null || "".equals(transId.trim())) {
			response.setErrorMsg("880101", "无效事务，系统异常");
			return  response;
		}
		BasicDBObject  rollquery=new BasicDBObject();
		rollquery.put("transId",transId);
		
		//-2、操作固化为 insert多条
		String operate="insertList";
		String collection=(String)params.get("collection");
		if (collection==null || "".equals(collection))  {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("880107", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880102", "无效集合参数，系统异常");
			return  response;
		}
		//-3、查询条件对象 query
		/*
		DBObject  query=new BasicDBObject();  
		if  (params.get("query") instanceof java.util.LinkedHashMap ) {
			//查询
			query=(DBObject)JSON.parse(JSON.serialize(params.get("query")));	
		}else {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("880107", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880103", "无效查询数据，系统异常");
			return  response;
		}
		*/	
		//-4、更新的数据 对象域 data	
	    
		//System.out.println(params.get("data").getClass().getName());
		//List<DBObject> data = (List<DBObject>)params.get("data") ;//(List<DBObject>)JsonUtil.fromJson((String)params.get("data"),new TypeReference<List<HashMap>>() {});
		List<DBObject> data=new ArrayList<DBObject>();
		for  (Map map: (List<Map>)params.get("data"))  {
			 BasicDBObject dbobject=new BasicDBObject();
			 dbobject.putAll(map);
			 data.add(dbobject);
		}
		
		if (data.isEmpty()) {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("880107", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880104", "待更新的节点data数据无效");
			return  response;
		}
		
		/*
		if (params.get("data") instanceof java.util.LinkedHashMap) {
			//修改的数据集合 ????????
			data=(List<DBObject>)JsonUtil.fromJson(params.get("data"),)
			data=(List<DBObject>)JSON.parse(JSON.serialize(params.get("data")));
		}else {
			response.setErrorMsg("880104", "待更新的节点data数据无效");
			return  response;
		}
	    */
		
		//-5、待返回的节点域 returnFields
		String[] returnFields={};
		if (params.get("returnFields") instanceof  java.util.ArrayList) {
        	List<String> returnDBO=(List<String>)(params.get("returnFields"));
			returnFields=new String[returnDBO.size()];	
			returnFields=(String[])returnDBO.toArray(returnFields);
        }
		BasicRepository  basicRepository=new  BasicRepository(collection);
		//-6、正式插入数据
		if ("insertList".equals(operate)) {
			if ("fail".equals(basicRepository.insertList(data))) {
				//不成功，则回滚整个事务
				if (rollBackData(rollquery)!=1) {
					response.setErrorMsg("880107", "回滚失败，系统异常");
					return  response;
				}
			}else {

				DBObject  rolldata=new BasicDBObject();			
				Map<String,Object> map=new HashMap<String, Object>();
				map.put("transId",transId);
				map.put("state","pending");
				map.put("createtime",DateUtil.getDateTime());
				map.put("timestamp",System.currentTimeMillis());
				map.put("endtime",System.currentTimeMillis()+1000);
				rolldata.put("$set",map);
						
				Map<String,Object> mapoperates=new HashMap<String, Object>();
				mapoperates.put("collection", collection);
				mapoperates.put("operate","delete");
				
				List<ObjectId>  queryList=new ArrayList<ObjectId>();
				for (DBObject dataSub:data) {
					queryList.add(new ObjectId(dataSub.get("_id").toString()));
				}
				
				mapoperates.put("query",new BasicDBObject("_id",new BasicDBObject("##in",queryList)));
				rolldata.put("$push",new BasicDBObject("operates",mapoperates));		
				BasicRepository  basicRepositoryRoll=new  BasicRepository("trans_rollback");
				//-7、插入新的待回滚数据操作
				if (basicRepositoryRoll.updateOrinsert(rollquery, rolldata)!=1) {
					response.setErrorMsg("880106", "事务异常");
					return  response;
				}
				/*
				List<DBObject>  addlist=new ArrayList<DBObject>();
				System.out.println("rolldat1="+rolldata);
				Object object=returnFields(rolldata,addlist,"","$","##");
				if (object instanceof BasicDBObject || object instanceof BasicDBList) {
					
					rolldata=(DBObject)object;
				}
				if (basicRepositoryRoll.updateOrinsert(rollquery, rolldata)!=1) {
					response.setErrorMsg("880106", "事务异常");
					return  response;
				}
				*/
				
			}
		}
		return response;
	}
	// --------------------------------------------------------------------------------------------------------
	
	@ServiceMethod("delete")
	public ResponseObject delete(RequestObject request) {
		ResponseObject response = new ResponseObject(request);
		Map<String, Object> params = new HashMap<String, Object>();
		params = request.getParam();
		//{transId:"xxx",collection:XXX,query:{aaa:bbb,…}}
		//-1、取事务id号 transId------------------------------------
		String transId=(String)params.get("transId");
		if (transId==null || "".equals(transId.trim())) {
			response.setErrorMsg("880101", "无效事务，系统异常");
			return  response;
		}
		BasicDBObject  rollquery=new BasicDBObject();
		rollquery.put("transId",transId);
		
		//-2、操作固化为 delete
		String operate="delete";
		String collection=(String)params.get("collection");
		if (collection==null || "".equals(collection))  {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("8801071", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880102", "无效集合参数，系统异常");
			return  response;
		}
		//-3、查询条件对象 query
		DBObject  query=new BasicDBObject();  
		if  (params.get("query") instanceof java.util.LinkedHashMap ) {
			//查询
			query=(DBObject)JSON.parse(JSON.serialize(params.get("query")));	
		}else {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("8801072", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880103", "无效查询数据，系统异常");
			return  response;
		}
		
		//-5、待返回的节点域 returnFields
		/*
		String[] returnFields={};
        if (params.get("returnFields") instanceof  java.util.ArrayList) {
        	List<String> returnDBO=(List<String>)(params.get("returnFields"));
			returnFields=new String[returnDBO.size()];	
			returnFields=(String[])returnDBO.toArray(returnFields);
        }
        */
		
		//-6、查询出旧数据 用以放入待回滚的数据项中
		BasicRepository  basicRepository=new  BasicRepository(collection);
		Object  rollDBO=basicRepository.findList(query);
		//集合   查询条件   旧数据遍历出来
		//{transId:"xxxx":state:pending,操作时间:XXXX,时间搓:xxx,:operates:[{集合,operate:delete,query:XXXX,data:{}}]]
		if (rollDBO==null) {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("8801073", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880105", "无数据，更新异常");
			return  response;
		}

		BasicDBObject  rolldata=new BasicDBObject();
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("transId",transId);
		map.put("state","pending");
		map.put("createtime",DateUtil.getDateTime());
		map.put("timestamp",System.currentTimeMillis());
		map.put("endtime",System.currentTimeMillis()+1000);
		rolldata.append("$set",map);
				
		Map<String,Object> mapoperates=new HashMap<String, Object>();
		mapoperates.put("collection", collection);
		mapoperates.put("operate","insert");
		mapoperates.put("data",rollDBO);				
		rolldata.append("$push",new BasicDBObject("operates",mapoperates));
				
		BasicRepository  basicRepositoryRoll=new  BasicRepository("trans_rollback");
		//-7、插入新的待回滚数据操作
		if (basicRepositoryRoll.updateOrinsert(rollquery, rolldata)!=1) {
			if (rollBackData(rollquery)!=1) {
				response.setErrorMsg("8801074", "回滚失败，系统异常");
				return  response;
			}
			response.setErrorMsg("880106", "事务异常");
			return  response;
		}
		//-8、正式更改数据
		if ("delete".equals(operate)) {
			if ("fail".equals(basicRepository.del(query))) {
				//不成功，则回滚整个事务
				if (rollBackData(rollquery)!=1) {
					response.setErrorMsg("8801075", "回滚失败，系统异常");
					return  response;
				}
			}
			
		}
		return response;
	}
	// --------------------------------------------------------------------------------------------------------
	
	/**
	*    select 提取单条数据
	*/
	@ServiceMethod("select")
	public ResponseObject select(RequestObject request) {
		ResponseObject response = new ResponseObject(request);
		Map<String, Object> params = new HashMap<String, Object>();
		params = request.getParam();
		//{transId:"xxx",collection:XXX,query:{aaa:bbb,…},sort:{},returnFields:[aa,bb,cc]}
		//-1、取事务id号 transId------------------------------------
		String transId=(String)params.get("transId");
		if (transId!=null && "".equals(transId.trim())) {
			transId=null;
		}
		/*
		if (transId==null ) {
			response.setErrorMsg("880101", "无效事务，系统异常");
			return  response;
		}
		*/
		BasicDBObject  rollquery=new BasicDBObject();
		rollquery.put("transId",transId);
		//-2、操作固化为 select单条
		String operate="select";
		String collection=(String)params.get("collection");
		if ((collection==null || "".equals(collection)))  {
			if (transId!=null) {
				if (rollBackData(rollquery) != 1) {
					response.setErrorMsg("880107", "回滚失败，系统异常");
					return response;
				}
			}
			response.setErrorMsg("880102", "无效集合参数，系统异常");
			return  response;
		}
		//-3、查询条件对象 query 
		DBObject  query=new BasicDBObject();
		if  (params.get("query") instanceof java.util.LinkedHashMap ) {
			//查询
			query=(DBObject)JSON.parse(JSON.serialize(params.get("query")));
		}else {
			if (transId!=null) {
				if (rollBackData(rollquery) != 1) {
					response.setErrorMsg("880107", "回滚失败，系统异常");
					return response;
				}
			}
			response.setErrorMsg("880103", "无效查询数据，系统异常");
			return  response;
		}
		//-4、查询排序
		DBObject  sort=new BasicDBObject();
		if  (params.get("sort")!=null) {
			sort=(DBObject)JSON.parse(JSON.serialize(params.get("sort")));
		}
		//-4、更新的数据 对象域 data	
		/*
		DBObject data=new BasicDBObject();
		if (params.get("data") instanceof BasicDBObject) {
			//修改的数据集合
			data=(DBObject)params.get("data");
		}else {
			response.setErrorMsg("880104", "待更新的节点data数据无效");
			return  response;
		}
		*/
		
		//-5、待返回的节点域 returnFields
		String[] returnFields={};
		DBObject fields=new BasicDBObject();
        if (params.get("returnFields") instanceof  java.util.ArrayList) {
        	List<String> returnDBO=(List<String>)(params.get("returnFields"));
			returnFields=new String[returnDBO.size()];	
			returnFields=(String[])returnDBO.toArray(returnFields);
        }
        //组合成后端可识别的
        for (String key:returnFields) {
        	fields.put(key,1);
        }
		BasicRepository  basicRepository=new  BasicRepository(collection);
		//-6、正式插入数据
		
		if ("select".equals(operate)) {
			DBObject dbo=new BasicDBObject();
			if (!((BasicDBObject)sort).toMap().isEmpty() && !((BasicDBObject)fields).toMap().isEmpty()) {
				dbo=basicRepository.findOne(query,fields,sort);
			}else if (((BasicDBObject)sort).toMap().isEmpty() && !((BasicDBObject)fields).toMap().isEmpty()) {
				dbo=basicRepository.findOne(query,fields);
			}else {
				dbo=basicRepository.findOne(query);
			}
			
			for(String field:returnFields){
		        if  (field.startsWith("*")) {
		        	basicRepository.replaceId(dbo,field.substring(field.indexOf("*") + 1));
		        	break;
		        }
			}
			
			if (dbo==null) {
				//不成功，则回滚整个事务
				if (transId!=null) {
					if (rollBackData(rollquery) != 1) {
						response.setErrorMsg("880107", "回滚失败，系统异常");
						return response;
					}
				}
			}else {	
				//-8、返回键值域项
				//返回指定的键域
				if (params.get("string")!=null) {
					if (!dbo.toMap().isEmpty()) {
						response.setData(dbo.get(params.get("string").toString()));
					}else {
						response.setData("");
					}
				}else {
				    response.setData(dbo);
				}
			}
		}
		return response;
	}
	// --------------------------------------------------------------------------------------------------------
	
	/**
	*    selectList 提取多条数据
	*/
	@ServiceMethod("selectList")
	public ResponseObject selectList(RequestObject request) {
		ResponseObject response = new ResponseObject(request);
		Map<String, Object> params = new HashMap<String, Object>();
		params = request.getParam();
		//{transId:"xxx",collection:XXX,data:{aaa:xxx,bbb:xxx},returnFields:[aa,bb,cc]}
		//-1、取事务id号 transId------------------------------------
		String transId=(String)params.get("transId");
		if (transId!=null && "".equals(transId.trim())) {
			transId=null;
		}
		/*
		if (transId==null ) {
			response.setErrorMsg("880101", "无效事务，系统异常");
			return  response;
		}
		*/
		BasicDBObject  rollquery=new BasicDBObject();
		rollquery.put("transId",transId);
		//-2、操作固化为 select单条
		String operate="selectList";
		String collection=(String)params.get("collection");
		if ((collection==null || "".equals(collection)))  {
			if (transId!=null) {
				if (rollBackData(rollquery) != 1) {
					response.setErrorMsg("880107", "回滚失败，系统异常");
					return response;
				}
			}
			response.setErrorMsg("880102", "无效集合参数，系统异常");
			return  response;
		}
		//-3、查询条件对象 query
		DBObject  query=new BasicDBObject();  
		if  (params.get("query") instanceof java.util.LinkedHashMap ) {
			//查询
			query=(DBObject)JSON.parse(JSON.serialize(params.get("query")));	
		}else {
			if (transId!=null) {
               if (rollBackData(rollquery) != 1) {
				  response.setErrorMsg("880107", "回滚失败，系统异常");
				  return response;
			    }
			}
			response.setErrorMsg("880103", "无效查询数据，系统异常");
			return  response;
		}
		
		//-4、查询排序
		DBObject  sort=new BasicDBObject();
		if  (params.get("sort")!=null) {
			sort=(DBObject)JSON.parse(JSON.serialize(params.get("sort")));
		}
		//-4、更新的数据 对象域 data	
		/*
		DBObject data=new BasicDBObject();
		if (params.get("data") instanceof BasicDBObject) {
			//修改的数据集合
			data=(DBObject)params.get("data");
		}else {
			response.setErrorMsg("880104", "待更新的节点data数据无效");
			return  response;
		}
		*/
		
		//-5、待返回的节点域 returnFields
		String[] returnFields={};
		DBObject fields=new BasicDBObject();
		if (params.get("returnFields") instanceof  java.util.ArrayList) {
        	List<String> returnDBO=(List<String>)(params.get("returnFields"));
			returnFields=new String[returnDBO.size()];	
			returnFields=(String[])returnDBO.toArray(returnFields);
        }
		 //组合成后端可识别的
        for (String key:returnFields) {
        	fields.put(key,1);
        }
		BasicRepository  basicRepository=new  BasicRepository(collection);
		//-6、正式插入数据
		if ("selectList".equals(operate)) {
			List<DBObject>  dboList=basicRepository.findList(query,fields,sort);//查询、字段过滤、排序
			for(String field:returnFields){
		        if  (field.startsWith("*")) {
		        	basicRepository.replaceId(dboList,field.substring(field.indexOf("*") + 1));
		        	break;
		        }
			}
			
			if (dboList.isEmpty()) {
				//不成功，则回滚整个事务
				if (transId!=null ) {
					if (rollBackData(rollquery) != 1) {
						response.setErrorMsg("880107", "回滚失败，系统异常");
						return response;
					}
				}
			}else {	
				//-8、返回键值域项
				//List<DBObject> addlist=new ArrayList<DBObject>();
				//返回指定的键域
				//basicRepository.returnFields(dboList, addlist, "", returnFields)
				response.setData(dboList);
			}
		}
		return response;
	}
	// --------------------------------------------------------------------------------------------------------
	
	
/**
*    beginTrans 开始事务 即申请事务号
*/
	@ServiceMethod("beginTrans")
	public ResponseObject beginTrans(RequestObject request) {
		ResponseObject response = new ResponseObject(request);
		Map<String, Object> params = new HashMap<String, Object>();
		params = request.getParam();
		String  transId=(String)params.get("transId");
		if (transId==null || "".equals(transId)) {
			//--------------------------------申请的事务号
			response.setData(RandUtil.uuid());
			return  response;
		}else {
			//删除原事务的回滚数据
			endTrans(request);
			response.setData(transId);
		}
		return response;
	}
	
/**
*    endTrans 事务结束
*/
@ServiceMethod("endTrans")
public ResponseObject endTrans(RequestObject request) {
	   ResponseObject response = new ResponseObject(request);
	   Map<String, Object> params = new HashMap<String, Object>();
	   params = request.getParam();
	   String  transId=(String)params.get("transId");
	   DBObject rollquery=new BasicDBObject();
	   rollquery.put("transId",transId);
	   BasicRepository  basicRepositoryRoll=new  BasicRepository("trans_rollback");
	   DBObject updateobject =new BasicDBObject();
	   updateobject.put("state","completed");
	   //basicRepositoryRoll.del(rollquery);
	   if (basicRepositoryRoll.update(rollquery,updateobject)!=1) {
		   response.setErrorMsg("880201","事务结束失败");		  
		}
	   return  response;
  }


/**
 *   rollBackData 数据回滚处理  (传入查询条件)	
 */
 public int  rollBackData(DBObject  query) {
		  BasicRepository  basicRepositoryRoll=new  BasicRepository("trans_rollback");
		  DBObject dbo=basicRepositoryRoll.findOne(query);
		  DBObject updateobject=new BasicDBObject();
		  updateobject.put("state","completed");
		  if (dbo!=null) {
			//{transId:"xxxx":state:pending,操作时间:XXXX,时间搓:xxx,:operates:[{集合,operate:delete,query:XXXX,data:{}}]]
			List<DBObject>  operates=(List<DBObject>)dbo.get("operates");
			Collections.reverse(operates); //恢复事务须数组逆序操作，重点哈
			for (DBObject operateDBO:operates) {
				if ("update".equals((String)operateDBO.get("operate"))) {
					DBObject  rollquery=(DBObject)operateDBO.get("query");
					DBObject  rollData=(DBObject)operateDBO.get("data");
					BasicRepository  basicRepositoryRollData=new  BasicRepository((String)operateDBO.get("collection"));
					if  (basicRepositoryRollData.update(rollquery, rollData)!=1) {
						return -1;
					}
				}else if ("updateMuli".equals((String)operateDBO.get("operate"))) {
					List<DBObject>  rollDataList=(List<DBObject>)operateDBO.get("data");
					BasicRepository  basicRepositoryRollData=new  BasicRepository((String)operateDBO.get("collection"));
					for (DBObject rollData:rollDataList) {
						//query:{},data:{}
						if  (basicRepositoryRollData.update((DBObject)rollData.get("query"),(DBObject)rollData.get("data"))!=1) {
							return -1;
						}					
					}				
				}
				else if ("delete".equals((String)operateDBO.get("operate"))) {
					DBObject  rollquery=(DBObject)operateDBO.get("query");
					List<DBObject>  addlist=new ArrayList<DBObject>();
					Object object=returnFields(rollquery,addlist,"","##","$");
					//System.out.println("class="+object.getClass().getName());
					if (object instanceof BasicDBObject || object instanceof BasicDBList) {
						rollquery=(DBObject)object;
					}
					//System.out.println("aaa="+operateDBO.get("collection"));
					//System.out.println("bbb="+rollquery);
					BasicRepository  basicRepositoryRollData=new  BasicRepository((String)operateDBO.get("collection"));
					if  (!"success".equals(basicRepositoryRollData.del(rollquery))) {
						//System.out.println("collection3="+(String)operateDBO.get("collection"));
						//System.out.println("rollquery="+rollquery);
						return -1;
					}
					
				}else if ("insert".equals((String)operateDBO.get("operate"))) {
					if (operateDBO.get("data") instanceof BasicDBObject) {
						DBObject rollData = (DBObject) operateDBO.get("data");
						BasicRepository basicRepositoryRollData = new BasicRepository(
								(String) operateDBO.get("collection"));
						if ("0".equals(basicRepositoryRollData.insert(rollData))) {
							return -1;
						}
					}else if (operateDBO.get("data") instanceof BasicDBList) {
						List<DBObject> rollData = (List<DBObject>) operateDBO.get("data");
						BasicRepository basicRepositoryRollData = new BasicRepository(
								(String) operateDBO.get("collection"));
						if ("fail".equals(basicRepositoryRollData.insertList(rollData))) {
							return -1;
						}
					}					
				}				
			}
			/*  测试验证时可放开 
			if ("fail".equals(basicRepositoryRoll.del(query))) {
				return -1;
			}
			*/		
			if (basicRepositoryRoll.update(query,updateobject)!=1) {
				return -1;
			}
		  }
		  return  1;
	}
 
 
/**
 *   rollDelTrans 删除事务数据
 * @param query
 * @return
 */
 public  int  rollDelTrans(DBObject  query) {
	 BasicRepository  basicRepositoryRoll=new  BasicRepository("trans_rollback");
	 if ("fail".equals(basicRepositoryRoll.del(query))) {
		return -1;
	 }
	 return 1;
 }
 
/**
 * 返回第一层数据节点，非$类型的节点哈
 * @param dbo
 * @param retFields
 */
void getOneLevelfields(DBObject dbo,List<String> retFields)  {
	      if (dbo!=null) {
	    	  for (String key:dbo.keySet()) {
	    		  if (key.startsWith("$")) {
	    			  if  (dbo.get(key) instanceof BasicDBObject) {
	    				   getOneLevelfields((DBObject)dbo.get(key),retFields);
	    			  }else if (key.indexOf(".$") >=0) {
	    				  retFields.add(key.substring(0,key.indexOf(".$")));//从节点的更新
	    			  }else {
	    				  retFields.add(key);//直接加入哈
	    			  }
	    		  }else {
	    			  retFields.add(key);//直接加入哈
	    		  }
	    	  }
	      }
  }

/**
*
*
*  A database name cannot contain any of these characters: /, \, ., ", *, <, >, :, |, ?, $, (a
   single space), or \0 (the null character). Basically, stick with alphanumeric ASCII
       集合的键关键字不能包含$ -> 所以采取替换键的方式递归处理键值  $ -##  ## ->$
   replace(/"type":".*?"/,'type:""') 
*/

/*
 * returnFieldsSub  返回指定的键值域，支持递归、分层
 * 算法：2015-10-30     修正后的算法要领
 *   1、查找键key  2 、数据键  pkey   3、type类型  对象{ }   数组[], 4、value值
 *   组合出list<DBObject> 对象列表    处理替换键哈
 *   如何闭环呢？ 假如类型存在则
 */
public List<DBObject> returnFieldsSub(Object object, List<DBObject>  addlist,String parentkey,String oldkey,String newkey) {
	List<Object> dbobjectlist=null;
	if  (addlist==null) {
		addlist=new ArrayList<DBObject>();
	}
	String parentkeyTmp="";
	if (!"".equals(parentkey)) {
		parentkeyTmp=parentkey+".";
	}
		
	if (parentkeyTmp.indexOf("..") >=0) {
		parentkeyTmp=parentkeyTmp.substring(0,parentkeyTmp.length() - 1);
	}
	
	String  pkey="";
	if (parentkeyTmp.split(".").length >0)  {
	    pkey=parentkeyTmp.split(".")[parentkeyTmp.split(".").length - 1];
    }
    
	if (object instanceof  org.bson.types.ObjectId)  {
		DBObject adddbobject1=new BasicDBObject();
		adddbobject1.put("key", "");//key.  ????
		adddbobject1.put("type", "{");
		adddbobject1.put("pkey", "");
		addlist.add(adddbobject1);
		
		ObjectId  Objectid=(ObjectId)object;
		DBObject adddbobject2=new BasicDBObject();
		adddbobject2.put("key", "");//key.  ????
		adddbobject2.put("type", "");
		adddbobject2.put("pkey", "##oid");
		adddbobject2.put("value", Objectid.toString());
		addlist.add(adddbobject2);
		//System.out.println("adddbobjectId="+adddbobject2);
		DBObject adddbobject3=new BasicDBObject();
		adddbobject3.put("key", "");//key.  ????
		adddbobject3.put("type", "}");
		adddbobject3.put("pkey", "");
		addlist.add(adddbobject3);
	}
	
	//System.out.println("searchkeys="+searchkeys);
	//System.out.println("parentkey="+parentkey);
	if (object instanceof ArrayList) {
		//开环
		if ("".equals(parentkey)) {
			DBObject adddbobject=new BasicDBObject();
			adddbobject.put("key", "$");
			adddbobject.put("type", "[");
			adddbobject.put("pkey","");
			addlist.add(adddbobject);
		}
		
		/*
		else {
			DBObject adddbobject=new BasicDBObject();
			adddbobject.put("keya", parentkeyTmp);//key.  ????
			adddbobject.put("type", "[");
			adddbobject.put("pkey", pkey);
			addlist.add(adddbobject);
		}
		*/
	   dbobjectlist=(List<Object>)object;
	   /*
	   if ("".equals(parentkey)) {
			System.out.println("dbobjectlist="+dbobjectlist);
		}
	   */
	   for (int i=0;i<dbobjectlist.size();i++) {
		   if (dbobjectlist.get(i) instanceof  org.bson.types.ObjectId)  {
			   returnFieldsSub(dbobjectlist.get(i),addlist,parentkeyTmp,oldkey,newkey); 
		   }else if (dbobjectlist.get(i) instanceof  BasicDBObject) {
			   returnFieldsSub(dbobjectlist.get(i),addlist,parentkeyTmp,oldkey,newkey); 
		   }
	   }
	   
	   //闭环
	   if ("".equals(parentkey)) {
			DBObject adddbobject=new BasicDBObject();
			adddbobject.put("key", "$");
			adddbobject.put("type", "]");
			adddbobject.put("pkey", "");
			addlist.add(adddbobject);					
		}
	   /*
	   else {
			DBObject adddbobject=new BasicDBObject();
			adddbobject.put("keya", parentkeyTmp);//key.  ????
			adddbobject.put("type", "]");
			adddbobject.put("pkey", pkey);
			addlist.add(adddbobject);
		}
		*/		
	}
	
	
	if (object instanceof BasicDBObject) {
		//System.out.println("classsss1="+object);
		//数组,对象,对象,单值 字符串continue,整数
		BasicDBObject dbobject=(BasicDBObject)object;	
		DBObject adddbobject=new BasicDBObject();
		adddbobject.put("key", parentkeyTmp);//key.  ????
		adddbobject.put("type", "{");
		adddbobject.put("pkey", pkey.replace(oldkey,newkey));
		addlist.add(adddbobject);
		for (String key1 : dbobject.keySet()) {
			//System.out.println("classsss2="+key1);
			//System.out.println("classsss3="+dbobject.get(key1).getClass().getName());
			if (dbobject.get(key1) instanceof BasicDBList
					&& dbobject.get(key1).toString().replace(" ", "")
							.indexOf("[{") == 0) {
				DBObject adddbobject1=new BasicDBObject();
				adddbobject1.put("key", parentkeyTmp+key1);//key.  ????
				adddbobject1.put("type", "[");
				adddbobject1.put("pkey", key1.replace(oldkey,newkey));
				addlist.add(adddbobject1);
				returnFieldsSub((List<DBObject>) (dbobject.get(key1)),addlist,
						parentkeyTmp + key1,oldkey,newkey); // BasicDBList类型则继续递归
				//闭环
				DBObject adddbobject2=new BasicDBObject();
				adddbobject2.put("key", parentkeyTmp+key1);//key.  ????
				adddbobject2.put("type", "]");
				adddbobject2.put("pkey", "");
				addlist.add(adddbobject2);		
			} else if (dbobject.get(key1) instanceof BasicDBObject) {
				DBObject adddbobject1=new BasicDBObject();
				adddbobject1.put("key", parentkeyTmp+key1);//key.  ????
				adddbobject1.put("type", "{");
				adddbobject1.put("pkey", key1.replace(oldkey,newkey));
				addlist.add(adddbobject1);
				returnFieldsSub((BasicDBObject) (dbobject.get(key1)),addlist,
						parentkeyTmp + key1,oldkey,newkey); // BasicDBObject类型则继续递归
				//闭环
				DBObject adddbobject2=new BasicDBObject();
				adddbobject2.put("key", parentkeyTmp+key1);//key.  ????
				adddbobject2.put("type", "}");
				adddbobject2.put("pkey", "");
				addlist.add(adddbobject2);
			}else if (dbobject.get(key1) instanceof BasicDBList) {
				DBObject adddbobject1=new BasicDBObject();
				adddbobject1.put("key", parentkeyTmp+key1);//key.  ????
				adddbobject1.put("type", "");
				adddbobject1.put("value", dbobject.get(key1));
				adddbobject1.put("pkey", key1.replace(oldkey,newkey));
				addlist.add(adddbobject1);
			}else if (dbobject.get(key1) instanceof java.util.ArrayList) {
				DBObject adddbobject1=new BasicDBObject();
				adddbobject1.put("key", parentkeyTmp+key1);//key.  ????
				adddbobject1.put("type", "[");
				adddbobject1.put("pkey", key1.replace(oldkey,newkey));
				addlist.add(adddbobject1);
	
				for (ObjectId objectid:(List<ObjectId>)dbobject.get(key1)) {
					
					DBObject adddbobject22=new BasicDBObject();
					adddbobject22.put("key", parentkeyTmp+key1);//key.  ????
					
					adddbobject22.put("type", "{");
					
					adddbobject22.put("pkey", "");
					addlist.add(adddbobject22);
					
					DBObject adddbobject2=new BasicDBObject();
					adddbobject2.put("key", parentkeyTmp+key1);//key.  ????
					adddbobject2.put("type", "");
					adddbobject2.put("pkey", "##oid");
					adddbobject2.put("value", objectid.toString());
					addlist.add(adddbobject2);
					
					DBObject adddbobject23=new BasicDBObject();
					adddbobject23.put("key", parentkeyTmp+key1);//key.  ????
					adddbobject23.put("type", "}");
					adddbobject23.put("pkey", "");
					addlist.add(adddbobject23);
					
				}
				
				DBObject adddbobject3=new BasicDBObject();
				adddbobject3.put("key", parentkeyTmp+key1);//key.  ????
				adddbobject3.put("type", "]");
				adddbobject3.put("pkey", "");
				addlist.add(adddbobject3);
								
			}
			else if (dbobject.get(key1) instanceof java.util.HashMap) {
				    //System.out.println("classsss="+dbobject.get(key1));
					DBObject adddbobject1=new BasicDBObject();
					adddbobject1.put("key", parentkeyTmp+key1);//key.  ????
					adddbobject1.put("type", "{");
					adddbobject1.put("pkey", key1.replace(oldkey,newkey));
					addlist.add(adddbobject1);
					BasicDBObject dbmap=new BasicDBObject();
					dbmap.putAll((Map)dbobject.get(key1));
					returnFieldsSub(dbmap,addlist,
							parentkeyTmp + key1,oldkey,newkey); // BasicDBObject类型则继续递归
					//闭环
					DBObject adddbobject2=new BasicDBObject();
					adddbobject2.put("key", parentkeyTmp+key1);//key.  ????
					adddbobject2.put("type", "}");
					adddbobject2.put("pkey", "");
					addlist.add(adddbobject2);
			}else if (dbobject.get(key1) instanceof org.bson.types.ObjectId) {
					  DBObject adddbobject1 = new BasicDBObject();
					  adddbobject1.put("key", parentkeyTmp + key1);// key. ????
					  adddbobject1.put("type", "{");
					  adddbobject1.put("pkey", "_id");
					  adddbobject1.put("value","");
					  addlist.add(adddbobject1);
				
     					DBObject adddbobject2=new BasicDBObject();
						adddbobject2.put("key", parentkeyTmp+key1);//key.  ????
						adddbobject2.put("type", "");
						adddbobject2.put("pkey", "##oid");
						adddbobject2.put("value", ((ObjectId)dbobject.get(key1)).toString());
						addlist.add(adddbobject2);
						//System.out.println("adddbobject2="+adddbobject2);	
						DBObject adddbobject3 = new BasicDBObject();
						adddbobject3.put("key", parentkeyTmp + key1);// key. ????
						adddbobject3.put("type", "}");
						adddbobject3.put("pkey", "");
						adddbobject3.put("value","");
						addlist.add(adddbobject3);
						  
			}else {
				DBObject adddbobject1=new BasicDBObject();
				adddbobject1.put("key",parentkeyTmp+key1);//key.  ????
				adddbobject1.put("type", "");
				//System.out.println("class="+dbobject.get(key1).getClass().getName());
				//System.out.println("value="+dbobject.get(key1));
				adddbobject1.put("value", dbobject.get(key1));
				adddbobject1.put("pkey", key1.replace(oldkey,newkey));
				addlist.add(adddbobject1);	
			}	
			
		}
		
		//闭环
		DBObject adddbobject1=new BasicDBObject();
		adddbobject1.put("key", parentkeyTmp);//key.  ????
		adddbobject1.put("type", "}");
		adddbobject.put("pkey","");
		addlist.add(adddbobject1);
	}
    return addlist;			
}

   
/*
 *  2015-10-30 修正后的算法 ，动态截取返回的节点  传入处理的object,递归处理数据的addlist,传入的父母键parentkey,截取的键域keys
 */
public Object  returnFields(Object object, List<DBObject>  addlist,String parentkey,String oldkey,String newkey) {
	    addlist=returnFieldsSub(object,addlist,parentkey,oldkey,newkey);//递归找出所有键值
	   
		//清空干掉无关的数据,保留$ 及关键节点的信息
	    //算法思路：1、删除无关节点 -- 通过查找节点 前缀匹配、2、组装json报文还原最初格式
		//System.out.println("addlist2="+addlist);
		//组合数据串
		StringBuffer addbuffer=new StringBuffer("");
		String  pretype="";
		//System.out.println("addlistxxx="+ addlist);
		for (DBObject dbfor :addlist) {
			if (dbfor.get("type")!=null && !"".equals(dbfor.get("type"))) {
				if  (dbfor.get("key").toString().endsWith(".")) {
					continue;
				}
				else if (!"".equals(dbfor.get("type")) &&  dbfor.get("pkey")!=null && !"".equals(dbfor.get("pkey"))) {
					if  ("}".equals(pretype) || "]".equals(pretype)) {
						addbuffer.append(",");
					}
					addbuffer.append(JSON.serialize(dbfor.get("pkey")).replace(oldkey,newkey)+":");
					addbuffer.append(dbfor.get("type"));
					
				}else {
					if ((addbuffer.toString().endsWith("}") || addbuffer.toString().endsWith("]")) &&
							("{".equals(dbfor.get("type")) || "[".equals(dbfor.get("type")))) {
						addbuffer.append(",");
					}
					addbuffer.append(dbfor.get("type"));
				}
								
				pretype=dbfor.get("type").toString();
				
			}else {
				if ("}".equals(pretype) || "]".equals(pretype) || "".equals(pretype)) {
					addbuffer.append(","+JSON.serialize(dbfor.get("pkey")).replace(oldkey, newkey)+":");
				}else {
				    addbuffer.append(JSON.serialize(dbfor.get("pkey")).replace(oldkey, newkey)+":");
				}
				if (dbfor.get("value")==null) {
					addbuffer.append("null");
				}else {
				    addbuffer.append(JSON.serialize(dbfor.get("value")));
				}
				pretype=dbfor.get("type").toString();
			}
		}
		//System.out.println("addlist1="+addlist);
		//System.out.println("object="+ JSON.parse(addbuffer.toString())) ;	
		return JSON.parse(addbuffer.toString());
		
}

}

