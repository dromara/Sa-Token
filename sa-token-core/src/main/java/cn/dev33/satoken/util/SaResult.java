package cn.dev33.satoken.util;

import java.io.Serializable;


/**
 * 对Ajax请求返回Json格式数据的简易封装 
 * @author kong
 *
 */
public class SaResult implements Serializable{

	private static final long serialVersionUID = 1L;	
	
	public static final int CODE_SUCCESS = 200;		
	public static final int CODE_ERROR = 500;		

	/**
	 * 状态码
	 */
	public int code; 	
	
	/**
	 * 描述信息 
	 */
	public String msg; 	
	
	/**
	 * 携带对象
	 */
	public Object data; 

	/**
	 * 给code赋值，连缀风格
	 */
	public SaResult setCode(int code) {
		this.code = code;
		return this;
	}

	/**
	 * 给msg赋值，连缀风格
	 */
	public SaResult setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	/**
	 * 给data赋值，连缀风格
	 */
	public SaResult setData(Object data) {
		this.data = data;
		return this;
	}

	// ============================  构建  ================================== 
	
	public SaResult(int code, String msg, Object data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}
	
	// 构建成功
	public static SaResult ok() {
		return new SaResult(CODE_SUCCESS, "ok", null);
	}
	public static SaResult ok(String msg) {
		return new SaResult(CODE_SUCCESS, msg, null);
	}
	public static SaResult data(Object data) {
		return new SaResult(CODE_SUCCESS, "ok", data);
	}
	
	// 构建失败
	public static SaResult error() {
		return new SaResult(CODE_ERROR, "error", null);
	}
	public static SaResult error(String msg) {
		return new SaResult(CODE_ERROR, msg, null);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "{"
				+ "\"code\": " + this.code
				+ ", \"msg\": \"" + this.msg + "\""
				+ ", \"data\": \"" + this.data + "\""
				+ "}";
	}
	
}
