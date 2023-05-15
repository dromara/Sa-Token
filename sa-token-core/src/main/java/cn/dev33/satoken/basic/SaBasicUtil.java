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
package cn.dev33.satoken.basic;

/**
 * Sa-Token Http Basic 认证模块，Util 工具类
 *
 * @author click33
 * @since <= 1.34.0
 */
public class SaBasicUtil {

	private SaBasicUtil() {
	}
	
	/**
	 * 底层使用的 SaBasicTemplate 对象
	 */
	public static SaBasicTemplate saBasicTemplate = new SaBasicTemplate();

	/**
	 * 获取浏览器提交的 Basic 参数 （裁剪掉前缀并解码）
	 * @return 值
	 */
	public static String getAuthorizationValue() {
		return saBasicTemplate.getAuthorizationValue();
	}
	
	/**
	 * 对当前会话进行 Basic 校验（使用全局配置的账号密码），校验不通过则抛出异常  
	 */
	public static void check() {
		saBasicTemplate.check();
	}

	/**
	 * 对当前会话进行 Basic 校验（手动设置账号密码），校验不通过则抛出异常  
	 * @param account 账号（格式为 user:password）
	 */
	public static void check(String account) {
		saBasicTemplate.check(account);
	}

	/**
	 * 对当前会话进行 Basic 校验（手动设置 Realm 和 账号密码），校验不通过则抛出异常 
	 * @param realm 领域 
	 * @param account 账号（格式为 user:password）
	 */
	public static void check(String realm, String account) {
		saBasicTemplate.check(realm, account);
	}

}
