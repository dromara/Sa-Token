package cn.dev33.satoken.exception;

import java.util.Arrays;
import java.util.List;

/**
 * 一个异常：代表用户没有登录 
 * @author kong 
 */
public class NotLoginException extends SaTokenException {
	
	/**
	 * 序列化版本号 
	 */
	private static final long serialVersionUID = 6806129545290130142L;
	
	
	// ------------------- 异常类型常量  -------------------- 
	
	/*
	 * 这里简述一下为什么要把常量设计为String类型 
	 * 因为loginId刚取出的时候类型为String，为了避免两者相比较时不必要的类型转换带来的性能消耗，故在此直接将常量类型设计为String 
	 */
	
	/** 表示未提供token */
	public static final String NOT_TOKEN = "-1";
	public static final String NOT_TOKEN_MESSAGE = "未提供token";
	
	/** 表示token无效 */
	public static final String INVALID_TOKEN = "-2";
	public static final String INVALID_TOKEN_MESSAGE = "token无效";
	
	/** 表示token已过期 */
	public static final String TOKEN_TIMEOUT = "-3";
	public static final String TOKEN_TIMEOUT_MESSAGE = "token已过期";
	
	/** 表示token已被顶下线 */
	public static final String BE_REPLACED = "-4";
	public static final String BE_REPLACED_MESSAGE = "token已被顶下线";
	
	/** 表示token已被踢下线 */
	public static final String KICK_OUT = "-5";
	public static final String KICK_OUT_MESSAGE = "token已被踢下线";

	/** 默认的提示语 */
	public static final String DEFAULT_MESSAGE = "当前会话未登录";
	
	
	/** 
	 * 代表异常token的标志集合 
	 */
	public static final List<String> ABNORMAL_LIST = Arrays.asList(NOT_TOKEN, INVALID_TOKEN, TOKEN_TIMEOUT, BE_REPLACED, KICK_OUT); 
	

	/**
	 * 异常类型 
	 */
	private String type;
	
	/**
	 * 获取异常类型 
	 * @return 异常类型 
	 */
	public String getType() {
		return type;
	}
	
	
	/**
	 * 账号类型 
	 */
	private String loginType;
	
	/** 
	 * 获得账号类型 
	 * @return 账号类型
	 */
	public String getLoginType() {
		return loginType;
	}
	
	
	/**
	 * 构造方法创建一个 
	 * @param message 异常消息 
	 * @param loginType 账号类型
	 * @param type 类型 
	 */
	public NotLoginException(String message, String loginType, String type) {
		super(message);	
        this.loginType = loginType;
        this.type = type;
    }
	
	/**
	 * 静态方法构建一个NotLoginException 
	 * @param loginType 账号类型
	 * @param type 账号类型 
	 * @return 构建完毕的异常对象 
	 */
	public static NotLoginException newInstance(String loginType, String type) {
		String message = null;
		if(NOT_TOKEN.equals(type)) {
			message = NOT_TOKEN_MESSAGE;
		}
		else if(INVALID_TOKEN.equals(type)) {
			message = INVALID_TOKEN_MESSAGE;
		}
		else if(TOKEN_TIMEOUT.equals(type)) {
			message = TOKEN_TIMEOUT_MESSAGE;
		}
		else if(BE_REPLACED.equals(type)) {
			message = BE_REPLACED_MESSAGE;
		}
		else if(KICK_OUT.equals(type)) {
			message = KICK_OUT_MESSAGE;
		}
		else {
			message = DEFAULT_MESSAGE;
		}
		return new NotLoginException(message, loginType, type);
    }

}
