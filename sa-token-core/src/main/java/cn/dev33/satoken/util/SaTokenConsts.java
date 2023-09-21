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
package cn.dev33.satoken.util;

/**
 * Sa-Token 常量类
 *
 * <p>
 *     一般的常量采用就近原则，定义在各自相应的模块中。
 *     但有一些常量没有明确的归属模块，会在很多模块中使用到，比如版本号、开源地址等，属于全局性的础性常量，这些常量统一定义在此类中。
 * </p>
 *
 * @author click33
 * @since 1.8.0
 */
public class SaTokenConsts {

	private SaTokenConsts() {
	}
	
	// ------------------ Sa-Token 版本信息
	
	/**
	 * Sa-Token 当前版本号 
	 */
	public static final String VERSION_NO = "v1.36.0";

	/**
	 * Sa-Token 开源地址 Gitee 
	 */
	public static final String GITEE_URL = "https://gitee.com/dromara/sa-token";

	/**
	 * Sa-Token 开源地址 GitHub  
	 */
	public static final String GITHUB_URL = "https://github.com/dromara/sa-token";

	/**
	 * Sa-Token 开发文档地址 
	 */
	public static final String DEV_DOC_URL = "https://sa-token.cc";
	
	
	// ------------------ 常量 key 标记
	
	/**
	 * 常量 key 标记: 如果 token 为本次请求新创建的，则以此字符串为 key 存储在当前请求 str 中
	 */
	public static final String JUST_CREATED = "JUST_CREATED_"; 	

	/**
	 * 常量 key 标记: 如果 token 为本次请求新创建的，则以此字符串为 key 存储在当前 request 中（不拼接前缀，纯Token）
	 */
	public static final String JUST_CREATED_NOT_PREFIX = "JUST_CREATED_NOT_PREFIX_"; 	

	/**
	 * 常量 key 标记: 如果本次请求已经验证过 activeTimeout, 则以此 key 在 storage 中做一个标记
	 */
	public static final String TOKEN_ACTIVE_TIMEOUT_CHECKED_KEY = "TOKEN_ACTIVE_TIMEOUT_CHECKED_KEY_";

	/**
	 * 常量 key 标记: 在登录时，默认使用的设备类型 
	 */
	public static final String DEFAULT_LOGIN_DEVICE = "default-device"; 

	/**
	 * 常量 key 标记: 在封禁账号时，默认封禁的服务类型 
	 */
	public static final String DEFAULT_DISABLE_SERVICE = "login"; 

	/**
	 * 常量 key 标记: 在封禁账号时，默认封禁的等级 
	 */
	public static final int DEFAULT_DISABLE_LEVEL = 1; 

	/**
	 * 常量 key 标记: 在封禁账号时，可使用的最小封禁级别 
	 */
	public static final int MIN_DISABLE_LEVEL = 1; 

	/**
	 * 常量 key 标记: 账号封禁级别，表示未被封禁 
	 */
	public static final int NOT_DISABLE_LEVEL = -2; 
	
	/**
	 * 常量 key 标记: 在进行临时身份切换时使用的 key
	 */
	public static final String SWITCH_TO_SAVE_KEY = "SWITCH_TO_SAVE_KEY_"; 

	/**
	 * 常量 key 标记: 在进行 Token 二级验证时，使用的 key
	 */
	@Deprecated
	public static final String SAFE_AUTH_SAVE_KEY = "SAFE_AUTH_SAVE_KEY_"; 

	/**
	 * 常量 key 标记: 在进行 Token 二级认证时，写入的 value 值
	 */
	public static final String SAFE_AUTH_SAVE_VALUE = "SAFE_AUTH_SAVE_VALUE"; 

	/**
	 * 常量 key 标记: 在进行 Token 二级验证时，默认的业务类型 
	 */
	public static final String DEFAULT_SAFE_AUTH_SERVICE = "important"; 

	/**
	 * 常量 key 标记: 临时 Token 认证模块，默认的业务类型 
	 */
	public static final String DEFAULT_TEMP_TOKEN_SERVICE = "record"; 


	// ------------------ token-style 相关
	
	/**
	 * Token风格: uuid 
	 */
	public static final String TOKEN_STYLE_UUID = "uuid"; 
	
	/**
	 * Token风格: 简单uuid (不带下划线) 
	 */
	public static final String TOKEN_STYLE_SIMPLE_UUID = "simple-uuid"; 
	
	/**
	 * Token风格: 32位随机字符串 
	 */
	public static final String TOKEN_STYLE_RANDOM_32 = "random-32"; 
	
	/**
	 * Token风格: 64位随机字符串 
	 */
	public static final String TOKEN_STYLE_RANDOM_64 = "random-64"; 
	
	/**
	 * Token风格: 128位随机字符串 
	 */
	public static final String TOKEN_STYLE_RANDOM_128 = "random-128"; 
	
	/**
	 * Token风格: tik风格 (2_14_16) 
	 */
	public static final String TOKEN_STYLE_TIK = "tik";


	// ------------------ SaSession 的类型

	/**
	 * SaSession 的类型: Account-Session
	 */
	public static final String SESSION_TYPE__ACCOUNT = "Account-Session";

	/**
	 * SaSession 的类型: Token-Session
	 */
	public static final String SESSION_TYPE__TOKEN = "Token-Session";

	/**
	 * SaSession 的类型: Custom-Session
	 */
	public static final String SESSION_TYPE__CUSTOM = "Custom-Session";


	// ------------------ 其它

	/**
	 * 连接 Token 前缀和 Token 值的字符
	 */
	public static final String TOKEN_CONNECTOR_CHAT  = " "; 
	
	/**
	 * 切面、拦截器、过滤器等各种组件的注册优先级顺序
	 */
	public static final int ASSEMBLY_ORDER = -100;

	
	// =================== 废弃 ===================  

	/**
	 * 请更换为 JUST_CREATED  
	 */
	@Deprecated
	public static final String JUST_CREATED_SAVE_KEY = JUST_CREATED;

	/**
	 * 请更换为 TOKEN_ACTIVE_TIMEOUT_CHECKED_KEY
	 */
	@Deprecated
	public static final String TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY = TOKEN_ACTIVE_TIMEOUT_CHECKED_KEY;


}
