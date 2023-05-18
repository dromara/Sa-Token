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

import cn.dev33.satoken.util.SaFoxUtil;

import java.util.Arrays;
import java.util.List;

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
	
	/** 表示未能读取到有效 token */
	public static final String NOT_TOKEN = "-1";
	public static final String NOT_TOKEN_MESSAGE = "未能读取到有效 token";
	
	/** 表示 token 无效 */
	public static final String INVALID_TOKEN = "-2";
	public static final String INVALID_TOKEN_MESSAGE = "token 无效";
	
	/** 表示 token 已过期 */
	public static final String TOKEN_TIMEOUT = "-3";
	public static final String TOKEN_TIMEOUT_MESSAGE = "token 已过期";
	
	/** 表示 token 已被顶下线 */
	public static final String BE_REPLACED = "-4";
	public static final String BE_REPLACED_MESSAGE = "token 已被顶下线";
	
	/** 表示 token 已被踢下线 */
	public static final String KICK_OUT = "-5";
	public static final String KICK_OUT_MESSAGE = "token 已被踢下线";

	/** 表示 token 已被冻结 */
	public static final String TOKEN_FREEZE = "-6";
	public static final String TOKEN_FREEZE_MESSAGE = "token 已被冻结";

	/** 表示 未按照指定前缀提交 token */
	public static final String NO_PREFIX = "-7";
	public static final String NO_PREFIX_MESSAGE = "未按照指定前缀提交 token";

	/** 默认的提示语 */
	public static final String DEFAULT_MESSAGE = "当前会话未登录";
	
	
	/** 
	 * 代表异常 token 的标志集合
	 */
	public static final List<String> ABNORMAL_LIST =
			Arrays.asList(NOT_TOKEN, INVALID_TOKEN, TOKEN_TIMEOUT, BE_REPLACED, KICK_OUT, TOKEN_FREEZE, NO_PREFIX);
	

	/**
	 * 异常类型 
	 */
	private final String type;
	
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
	private final String loginType;
	
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
	 * 静态方法构建一个 NotLoginException
	 * @param loginType 账号类型
	 * @param type 未登录场景值
	 * @param message 异常描述信息
	 * @param token 引起异常的 token 值，可不填，如果填了会拼接到异常描述信息后面
	 * @return 构建完毕的异常对象
	 */
	public static NotLoginException newInstance(String loginType, String type, String message, String token) {
		if(SaFoxUtil.isNotEmpty(token)) {
			message = message + "：" + token;
		}
		return new NotLoginException(message, loginType, type);
    }

}
