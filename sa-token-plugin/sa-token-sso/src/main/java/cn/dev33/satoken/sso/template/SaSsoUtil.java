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
package cn.dev33.satoken.sso.template;

import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;

import java.util.Map;

/**
 * Sa-Token-SSO 单点登录模块 工具类
 * 
 * @author click33
 * @since 1.30.0
 */
public class SaSsoUtil {

	// ---------------------- Ticket 操作 ---------------------- 
	
	/**
	 * 根据 账号id 创建一个 Ticket码 
	 * @param loginId 账号id 
	 * @param client 客户端标识 
	 * @return Ticket码 
	 */
	public static String createTicket(Object loginId, String client) {
		return SaSsoServerProcessor.instance.ssoServerTemplate.createTicket(loginId, client);
	}
	
	/**
	 * 删除 Ticket 
	 * @param ticket Ticket码
	 */
	public static void deleteTicket(String ticket) {
		SaSsoServerProcessor.instance.ssoServerTemplate.deleteTicket(ticket);
	}
	
	/**
	 * 删除 Ticket索引 
	 * @param loginId 账号id 
	 */
	public static void deleteTicketIndex(Object loginId) {
		SaSsoServerProcessor.instance.ssoServerTemplate.deleteTicketIndex(loginId);
	}

	/**
	 * 根据 Ticket码 获取账号id，如果Ticket码无效则返回null 
	 * @param ticket Ticket码
	 * @return 账号id 
	 */
	public static Object getLoginId(String ticket) {
		return SaSsoServerProcessor.instance.ssoServerTemplate.getLoginId(ticket);
	}

	/**
	 * 根据 Ticket码 获取账号id，并转换为指定类型 
	 * @param <T> 要转换的类型 
	 * @param ticket Ticket码
	 * @param cs 要转换的类型 
	 * @return 账号id 
	 */
	public static <T> T getLoginId(String ticket, Class<T> cs) {
		return SaSsoServerProcessor.instance.ssoServerTemplate.getLoginId(ticket, cs);
	}

	/**
	 * 校验 Ticket 码，获取账号id，如果此ticket是有效的，则立即删除 
	 * @param ticket Ticket码
	 * @return 账号id 
	 */
	public static Object checkTicket(String ticket) {
		return SaSsoServerProcessor.instance.ssoServerTemplate.checkTicket(ticket);
	}
	
	/**
	 * 校验ticket码，获取账号id，如果此ticket是有效的，则立即删除 
	 * @param ticket Ticket码
	 * @param client client 标识 
	 * @return 账号id 
	 */
	public static Object checkTicket(String ticket, String client) {
		return SaSsoServerProcessor.instance.ssoServerTemplate.checkTicket(ticket, client);
	}

	/**
	 * 获取：所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket) 
	 * @return see note 
	 */
	public static String getAllowUrl() {
		return SaSsoServerProcessor.instance.ssoServerTemplate.getAllowUrl();
	}

	/**
	 * 校验重定向url合法性
	 * @param url 下放ticket的url地址 
	 */
	public static void checkRedirectUrl(String url) {
		SaSsoServerProcessor.instance.ssoServerTemplate.checkRedirectUrl(url);
	}

	
	// ------------------- SSO 模式三 ------------------- 
	
	/**
	 * 构建URL：校验ticket的URL 
	 * @param ticket ticket码
	 * @param ssoLogoutCallUrl 单点注销时的回调URL 
	 * @return 构建完毕的URL 
	 */
	public static String buildCheckTicketUrl(String ticket, String ssoLogoutCallUrl) {
		return SaSsoClientProcessor.instance.ssoClientTemplate.buildCheckTicketUrl(ticket, ssoLogoutCallUrl);
	}

	/**
	 * 为指定账号id注册单点注销回调URL 
	 * @param loginId 账号id
	 * @param client 指定客户端标识，可为null
	 * @param sloCallbackUrl 单点注销时的回调URL 
	 */
	public static void registerSloCallbackUrl(Object loginId, String client, String sloCallbackUrl) {
		SaSsoServerProcessor.instance.ssoServerTemplate.registerSloCallbackUrl(loginId, client, sloCallbackUrl);
	}

	/**
	 * 构建URL：单点注销URL 
	 * @param loginId 要注销的账号id
	 * @return 单点注销URL 
	 */
	public static String buildSloUrl(Object loginId) {
		return SaSsoClientProcessor.instance.ssoClientTemplate.buildSloUrl(loginId);
	}

	/**
	 * 指定账号单点注销 
	 * @param loginId 指定账号 
	 */
	public static void ssoLogout(Object loginId) {
		SaSsoServerProcessor.instance.ssoServerTemplate.ssoLogout(loginId);
	}

	/**
	 * 获取：查询数据
	 * @param paramMap 查询参数
	 * @return 查询结果
	 */
	public static Object getData(Map<String, Object> paramMap) {
		return SaSsoClientProcessor.instance.ssoClientTemplate.getData(paramMap);
	}

	/**
	 * 根据自定义 path 查询数据 （此方法需要配置 sa-token.sso.server-url 地址）
	 * @param path 自定义 path
	 * @param paramMap 查询参数
	 * @return 查询结果
	 */
	public static Object getData(String path, Map<String, Object> paramMap) {
		return SaSsoClientProcessor.instance.ssoClientTemplate.getData(path, paramMap);
	}


	// ---------------------- 构建URL ---------------------- 

	/**
	 * 构建URL：Server端 单点登录地址
	 * @param clientLoginUrl Client端登录地址 
	 * @param back 回调路径 
	 * @return [SSO-Server端-认证地址 ]
	 */
	public static String buildServerAuthUrl(String clientLoginUrl, String back) {
		return SaSsoClientProcessor.instance.ssoClientTemplate.buildServerAuthUrl(clientLoginUrl, back);
	}

	/**
	 * 构建URL：Server端向Client下放ticket的地址
	 * @param loginId 账号id 
	 * @param client 客户端标识 
	 * @param redirect Client端提供的重定向地址 
	 * @return see note 
	 */
	public static String buildRedirectUrl(Object loginId, String client, String redirect) {
		return SaSsoServerProcessor.instance.ssoServerTemplate.buildRedirectUrl(loginId, client, redirect);
	}

	/**
	 * 构建URL：Server端 getData 地址，带签名等参数
	 * @param paramMap 查询参数
	 * @return /
	 */
	public static String buildGetDataUrl(Map<String, Object> paramMap) {
		return SaSsoClientProcessor.instance.ssoClientTemplate.buildGetDataUrl(paramMap);
	}

	/**
	 * 构建URL：Server 端自定义 path 地址，带签名等参数 （此方法需要配置 sa-token.sso.server-url 地址）
	 * @param paramMap 请求参数
	 * @return /
	 */
	public static String buildCustomPathUrl(String path, Map<String, Object> paramMap) {
		return SaSsoClientProcessor.instance.ssoClientTemplate.buildCustomPathUrl(path, paramMap);
	}


}
