/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.exception;

import java.util.Arrays;
import java.util.List;

import cn.dev33.satoken.util.SaFoxUtil;

/**
 * 一个异常：代表会话未能通过登录认证校验
 *
 * @author click33
 * @since <= 1.34.0
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
	public static final String NOT_TOKEN_MESSAGE = "未能读取到有效 token";
	
	/** 表示token无效 */
	public static final String INVALID_TOKEN = "-2";
	public static final String INVALID_TOKEN_MESSAGE = "token 无效";
	
	/** 表示token已过期 */
	public static final String TOKEN_TIMEOUT = "-3";
	public static final String TOKEN_TIMEOUT_MESSAGE = "token 已过期";
	
	/** 表示token已被顶下线 */
	public static final String BE_REPLACED = "-4";
	public static final String BE_REPLACED_MESSAGE = "token 已被顶下线";
	
	/** 表示token已被踢下线 */
	public static final String KICK_OUT = "-5";
	public static final String KICK_OUT_MESSAGE = "token 已被踢下线";

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
		return newInstance(loginType, type, null);
    }

	/**
	 * 静态方法构建一个NotLoginException 
	 * @param loginType 账号类型
	 * @param type 账号类型 
	 * @param token 引起异常的Token值 
	 * @return 构建完毕的异常对象 
	 */
	public static NotLoginException newInstance(String loginType, String type, String token) {
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
		if(SaFoxUtil.isEmpty(token) == false) {
			message = message + "：" + token;
		}
		return new NotLoginException(message, loginType, type);
    }

}
