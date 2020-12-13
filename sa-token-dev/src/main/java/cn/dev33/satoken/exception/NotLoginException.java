package cn.dev33.satoken.exception;

import java.util.Arrays;
import java.util.List;

/**
 * 没有登陆抛出的异常
 * @author kong
 *
 */
public class NotLoginException extends RuntimeException {
	
	/**
	 *
	 */
	private static final long serialVersionUID = 6806129545290130142L;
	
	
	// ------------------- 异常类型常量  -------------------- 
	
	/*
	 * 这里简述一下为什么要把常量设计为String类型 
	 * 因为loginId刚取出的时候类型为String，为了避免两者相比较时不必要的类型转换带来的性能消耗，故在此直接将常量类型设计为String 
	 */
	
	/** 表示未提供token */
	public static final String NOT_TOKEN = "-1";
	
	/** 表示token无效 */
	public static final String INVALID_TOKEN = "-2";
	
	/** 表示token已被顶下线 */
	public static final String BE_REPLACED = "-3";
	
	/** 表示token已过期 */
	public static final String TOKEN_TIMEOUT = "-4";
	
	/** 
	 * 代表异常token的标志集合 
	 */
	public static final List<String> ABNORMAL_LIST = Arrays.asList(NOT_TOKEN, INVALID_TOKEN, BE_REPLACED, TOKEN_TIMEOUT); 
	

	/**
	 * 异常类型 
	 */
	private String type;
	/**
	 * 获取异常类型 
	 * @return
	 */
	public String getType() {
		return type;
	}
	
	
	/**
	 * loginKey 
	 */
	private String loginKey;
	/** 
	 * 获得loginKey
	 * @return login_key
	 */
	public String getLoginKey() {
		return loginKey;
	}
	
	
	

//	/**
//	 * 创建一个
//	 */
//	public NotLoginException() {
//        this(StpUtil.stpLogic.loginKey);
//    }
	
	/**
	 *  创建一个
	 * @param loginKey login_key
	 */
	public NotLoginException(String loginKey, String type) {
		// 这里到底要不要拼接上login_key呢？纠结
        super("当前会话未登录");	
        this.loginKey = loginKey;
        this.type = type;
    }

}
