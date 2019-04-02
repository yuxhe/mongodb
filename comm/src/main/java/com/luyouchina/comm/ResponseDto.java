/**
 * 
 */
package com.luyouchina.comm;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.luyouchina.comm.model.ResponseObject;

/**
 * @author lfj
 *
 */
public class ResponseDto {

	private String status = "-1";// -1WEB服务异常，0：核心平台返回错误编码，1 请求成功，-2需要登陆，-3权限不足
	private String msg = "服务发生异常，请联系管理员";
	private Object data;

	public ResponseDto() {
	}

	/**
	 * 组装返回数据
	 * 
	 * @param res
	 */
	@SuppressWarnings("rawtypes")
	public ResponseDto(ResponseObject res) {
		if ((res.getErrorMsg() == null || res.getErrorMsg().getId() == null) && !"[]".equals(res.getData())) {
			Object data = res.getData();
			if (data instanceof String) {
				this.setSuccess((String) data);
			} else if (data instanceof LinkedHashMap) {
				this.setSuccess((LinkedHashMap) data);
			} else if (data instanceof HashMap) {
				this.setSuccess((HashMap) data);
			} else {
				this.setSuccess(data);
			}

		} else {
			this.setError(res.getErrorMsg().getMsg());
		}
	}

	public void setSuccess(Object data) {
		this.status = "1";
		this.data = data;
		this.msg = "请求处理成功";
	}

	public void setError(String errorMsg) {
		this.status = "0";
		this.msg = errorMsg;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
