package com.pj.test;

import java.io.Serializable;
import java.util.List;


/**
 * ajax请求返回Json格式数据的封装 
 */
public class AjaxJson implements Serializable{

	private static final long serialVersionUID = 1L;	// 序列化版本号
	
	public static final int CODE_SUCCESS = 200;			// 成功状态码
	public static final int CODE_ERROR = 500;			// 错误状态码
	public static final int CODE_WARNING = 501;			// 警告状态码
	public static final int CODE_NOT_JUR = 403;			// 无权限状态码
	public static final int CODE_NOT_LOGIN = 401;		// 未登录状态码
	public static final int CODE_INVALID_REQUEST = 400;	// 无效请求状态码

	public int code; 	// 状态码
	public String msg; 	// 描述信息 
	public Object data; // 携带对象
	public Long dataCount;	// 数据总数，用于分页 
	
	/**
	 * 返回code  
	 * @return
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * 给msg赋值，连缀风格
	 */
	public AjaxJson setMsg(String msg) {
		this.msg = msg;
		return this;
	}
	public String getMsg() {
		return this.msg;
	}

	/**
	 * 给data赋值，连缀风格
	 */
	public AjaxJson setData(Object data) {
		this.data = data;
		return this;
	}

	/**
	 * 将data还原为指定类型并返回
	 */
	@SuppressWarnings("unchecked")
	public <T> T getData(Class<T> cs) {
		return (T) data;
	}
	
	// ============================  构建  ================================== 
	
	public AjaxJson(int code, String msg, Object data, Long dataCount) {
		this.code = code;
		this.msg = msg;
		this.data = data;
		this.dataCount = dataCount;
	}
	
	// 返回成功
	public static AjaxJson getSuccess() {
		return new AjaxJson(CODE_SUCCESS, "ok", null, null);
	}
	public static AjaxJson getSuccess(String msg) {
		return new AjaxJson(CODE_SUCCESS, msg, null, null);
	}
	public static AjaxJson getSuccess(String msg, Object data) {
		return new AjaxJson(CODE_SUCCESS, msg, data, null);
	}
	public static AjaxJson getSuccessData(Object data) {
		return new AjaxJson(CODE_SUCCESS, "ok", data, null);
	}
	public static AjaxJson getSuccessArray(Object... data) {
		return new AjaxJson(CODE_SUCCESS, "ok", data, null);
	}
	
	// 返回失败
	public static AjaxJson getError() {
		return new AjaxJson(CODE_ERROR, "error", null, null);
	}
	public static AjaxJson getError(String msg) {
		return new AjaxJson(CODE_ERROR, msg, null, null);
	}
	
	// 返回警告 
	public static AjaxJson getWarning() {
		return new AjaxJson(CODE_ERROR, "warning", null, null);
	}
	public static AjaxJson getWarning(String msg) {
		return new AjaxJson(CODE_WARNING, msg, null, null);
	}
	
	// 返回未登录
	public static AjaxJson getNotLogin() {
		return new AjaxJson(CODE_NOT_LOGIN, "未登录，请登录后再次访问", null, null);
	}
	
	// 返回没有权限的 
	public static AjaxJson getNotJur(String msg) {
		return new AjaxJson(CODE_NOT_JUR, msg, null, null);
	}
	
	// 返回一个自定义状态码的
	public static AjaxJson get(int code, String msg){
		return new AjaxJson(code, msg, null, null);
	}
	
	// 返回分页和数据的
	public static AjaxJson getPageData(Long dataCount, Object data){
		return new AjaxJson(CODE_SUCCESS, "ok", data, dataCount);
	}
	
	// 返回，根据受影响行数的(大于0=ok，小于0=error)
	public static AjaxJson getByLine(int line){
		if(line > 0){
			return getSuccess("ok", line);
		}
		return getError("error").setData(line); 
	}

	// 返回，根据布尔值来确定最终结果的  (true=ok，false=error)
	public static AjaxJson getByBoolean(boolean b){
		return b ? getSuccess("ok") : getError("error"); 
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public String toString() {
		String data_string = null;
		if(data == null){
			
		} else if(data instanceof List){
			data_string = "List(length=" + ((List)data).size() + ")";
		} else {
			data_string = data.toString();
		}
		return "{"
				+ "\"code\": " + this.getCode()
				+ ", \"msg\": \"" + this.getMsg() + "\""
				+ ", \"data\": " + data_string
				+ ", \"dataCount\": " + dataCount
				+ "}";
	}
	
	
	
	
	
}
