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
package cn.dev33.satoken.config;


import java.io.Serializable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token SSO 单点登录模块 配置类 Model
 *
 * @author click33
 * @since 1.30.0
 */
public class SaSsoConfig implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;


	// ----------------- Server端相关配置

	/**
	 * 指定当前系统集成 SSO 时使用的模式（约定型配置项，不对代码逻辑产生任何影响）
	 */
	public String mode = "";

	/**
	 * Ticket有效期 (单位: 秒) 
	 */
	public long ticketTimeout = 60 * 5;
	
	/**
	 * 所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket) 
	 */
	public String allowUrl = "*";

	/**
	 * 是否打开单点注销功能 
	 */
	public Boolean isSlo = true; 
	
	/**
	 * 是否打开模式三（此值为 true 时将使用 http 请求：校验ticket值、单点注销、获取userinfo） 
	 */
	public Boolean isHttp = false;


	// ----------------- Client端相关配置 

//	/**
//	 * 指定当前系统集成 SSO 时使用的模式（约定型配置项，不对代码逻辑产生任何影响）
//	 */
//	public String mode = "";  // 同Server端，不再重复声明

	/**
	 * 当前 Client 名称标识，用于和 ticket 码的互相锁定 
	 */
	public String client;

	/**
	 * 配置 Server 端单点登录授权地址 
	 */
	public String authUrl = "/sso/auth";

	// /**
	// * 是否打开单点注销功能
	// */
	// public Boolean isSlo = true;  // 同Server端，不再重复声明

	// /**
	//  * 是否打开模式三（此值为 true 时将使用 http 请求：校验ticket值、单点注销、获取userinfo）
	//  */
	// public Boolean isHttp = false;  // 同Server端，不再重复声明

	// /**
	//  * 接口调用秘钥 (用于SSO模式三单点注销的接口通信身份校验)
	// */
	// public String secretkey;  // 同Server端，不再重复声明

	/**
	 * 配置 Server 端的 ticket 校验地址 
	 */
	public String checkTicketUrl = "/sso/checkTicket";

	/**
	 * 配置 Server 端查询数据 getData 地址
	 */
	public String getDataUrl = "/sso/getData";

	/**
	 * 配置 Server 端查询 userinfo 地址 
	 */
	public String userinfoUrl = "/sso/userinfo";

	/**
	 * 配置 Server 端单点注销地址 
	 */
	public String sloUrl = "/sso/signout";

	/**
	 * 配置当前 Client 端的登录地址（为空时自动获取）
	 */
	public String currSsoLogin;

	/**
	 * 配置当前 Client 端的单点注销回调URL （为空时自动获取） 
	 */
	public String currSsoLogoutCall;

	/**
	 * 配置 Server 端主机总地址，拼接在 authUrl、checkTicketUrl、getDataUrl、sloUrl 属性前面，用以简化各种 url 配置
	 */
	public String serverUrl;

	/**
	 * 是否校验参数签名（方便本地调试用的一个配置项，生产环境请务必为true）
	 */
	public Boolean isCheckSign = true;


	/**
	 * 获取 指定当前系统集成 SSO 时使用的模式（约定型配置项，不对代码逻辑产生任何影响）
	 *
	 * @return /
	 */
	public String getMode() {
		return this.mode;
	}

	/**
	 * 设置 指定当前系统集成 SSO 时使用的模式（约定型配置项，不对代码逻辑产生任何影响）
	 *
	 * @param mode /
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * @return Ticket有效期 (单位: 秒)  
	 */
	public long getTicketTimeout() {
		return ticketTimeout;
	}

	/**
	 * @param ticketTimeout Ticket有效期 (单位: 秒)   
	 * @return 对象自身
	 */
	public SaSsoConfig setTicketTimeout(long ticketTimeout) {
		this.ticketTimeout = ticketTimeout;
		return this;
	}

	/**
	 * @return 所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket) 
	 */
	public String getAllowUrl() {
		return allowUrl;
	}

	/**
	 * @param allowUrl 所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket) 
	 * @return 对象自身
	 */
	public SaSsoConfig setAllowUrl(String allowUrl) {
		this.allowUrl = allowUrl;
		return this;
	}

	/**
	 * @return 是否打开单点注销功能 
	 */
	public Boolean getIsSlo() {
		return isSlo;
	}

	/**
	 * @param isSlo 是否打开单点注销功能 
	 * @return 对象自身 
	 */
	public SaSsoConfig setIsSlo(Boolean isSlo) {
		this.isSlo = isSlo;
		return this;
	}

	/**
	 * @return isHttp 是否打开模式三（此值为 true 时将使用 http 请求：校验ticket值、单点注销、获取userinfo） 
	 */
	public Boolean getIsHttp() {
		return isHttp;
	}

	/**
	 * @param isHttp 是否打开模式三（此值为 true 时将使用 http 请求：校验ticket值、单点注销、获取userinfo） 
	 * @return 对象自身 
	 */
	public SaSsoConfig setIsHttp(Boolean isHttp) {
		this.isHttp = isHttp;
		return this;
	}

	/**
	 * @return 当前 Client 名称标识，用于和 ticket 码的互相锁定 
	 */
	public String getClient() {
		return client;
	}

	/**
	 * @param client 当前 Client 名称标识，用于和 ticket 码的互相锁定 
	 */
	public SaSsoConfig setClient(String client) {
		this.client = client;
		return this;
	}
	
	/**
	 * @return 配置的 Server 端单点登录授权地址 
	 */
	public String getAuthUrl() {
		return authUrl;
	}

	/**
	 * @param authUrl 配置 Server 端单点登录授权地址  
	 * @return 对象自身
	 */
	public SaSsoConfig setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
		return this;
	}

	/**
	 * @return 配置的 Server 端的 ticket 校验地址  
	 */
	public String getCheckTicketUrl() {
		return checkTicketUrl;
	}

	/**
	 * @param checkTicketUrl 配置 Server 端的 ticket 校验地址  
	 * @return 对象自身
	 */
	public SaSsoConfig setCheckTicketUrl(String checkTicketUrl) {
		this.checkTicketUrl = checkTicketUrl;
		return this;
	}

	/**
	 * @return Server 端查询数据 getData 地址
	 */
	public String getGetDataUrl() {
		return getDataUrl;
	}

	/**
	 * @param getDataUrl 配置 Server 端查询数据 getData 地址
	 * @return 对象自身
	 */
	public SaSsoConfig setGetDataUrl(String getDataUrl) {
		this.getDataUrl = getDataUrl;
		return this;
	}

	/**
	 * @return 配置的 Server 端查询 userinfo 地址 
	 */
	public String getUserinfoUrl() {
		return userinfoUrl;
	}

	/**
	 * @param userinfoUrl 配置 Server 端查询 userinfo 地址 
	 * @return 对象自身 
	 */
	public SaSsoConfig setUserinfoUrl(String userinfoUrl) {
		this.userinfoUrl = userinfoUrl;
		return this;
	}

	/**
	 * @return 配置 Server 端单点注销地址  
	 */
	public String getSloUrl() {
		return sloUrl;
	}

	/**
	 * @param sloUrl 配置 Server 端单点注销地址  
	 * @return 对象自身
	 */
	public SaSsoConfig setSloUrl(String sloUrl) {
		this.sloUrl = sloUrl;
		return this;
	}

	/**
	 * @return 配置当前 Client 端的登录地址（为空时自动获取）
	 */
	public String getCurrSsoLogin() {
		return currSsoLogin;
	}

	/**
	 * @param currSsoLogin 配置当前 Client 端的登录地址（为空时自动获取）
	 * @return 对象自身
	 */
	public SaSsoConfig setCurrSsoLogin(String currSsoLogin) {
		this.currSsoLogin = currSsoLogin;
		return this;
	}

	/**
	 * @return 配置当前 Client 端的单点注销回调URL （为空时自动获取）
	 */
	public String getCurrSsoLogoutCall() {
		return currSsoLogoutCall;
	}

	/**
	 * @param currSsoLogoutCall 配置当前 Client 端的单点注销回调URL （为空时自动获取）
	 * @return 对象自身
	 */
	public SaSsoConfig setCurrSsoLogoutCall(String currSsoLogoutCall) {
		this.currSsoLogoutCall = currSsoLogoutCall;
		return this;
	}

	/**
	 * @return 配置的 Server 端主机总地址，拼接在 authUrl、checkTicketUrl、getDataUrl、sloUrl 属性前面，用以简化各种 url 配置
	 */
	public String getServerUrl() {
		return serverUrl;
	}

	/**
	 * @param serverUrl 配置 Server 端主机总地址，拼接在 authUrl、checkTicketUrl、getDataUrl、sloUrl 属性前面，用以简化各种 url 配置
	 * @return 对象自身
	 */
	public SaSsoConfig setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
		return this;
	}

	/**
	 * 获取 是否校验参数签名（方便本地调试用的一个配置项，生产环境请务必为true）
	 *
	 * @return isCheckSign 是否校验参数签名（方便本地调试用的一个配置项，生产环境请务必为true）
	 */
	public Boolean getIsCheckSign() {
		return this.isCheckSign;
	}

	/**
	 * 设置 是否校验参数签名（方便本地调试用的一个配置项，生产环境请务必为true）
	 *
	 * @param isCheckSign 是否校验参数签名（方便本地调试用的一个配置项，生产环境请务必为true）
	 */
	public SaSsoConfig setIsCheckSign(Boolean isCheckSign) {
		this.isCheckSign = isCheckSign;
		return this;
	}

	@Override
	public String toString() {
		return "SaSsoConfig ["
				+ "mode=" + mode
				+ ", ticketTimeout=" + ticketTimeout
				+ ", allowUrl=" + allowUrl 
				+ ", isSlo=" + isSlo
				+ ", isHttp=" + isHttp
				+ ", client=" + client 
				+ ", authUrl=" + authUrl 
				+ ", checkTicketUrl=" + checkTicketUrl
				+ ", getDataUrl=" + getDataUrl
				+ ", userinfoUrl=" + userinfoUrl 
				+ ", sloUrl=" + sloUrl
				+ ", currSsoLogin=" + currSsoLogin
				+ ", currSsoLogoutCall=" + currSsoLogoutCall
				+ ", serverUrl=" + serverUrl
				+ ", isCheckSign=" + isCheckSign
				+ "]";
	}
	

	// 额外添加的一些函数 
	
	/**
	 * @return 获取拼接url：Server 端单点登录授权地址 
	 */
	public String splicingAuthUrl() {
		return SaFoxUtil.spliceTwoUrl(getServerUrl(), getAuthUrl());
	}

	/**
	 * @return 获取拼接url：Server 端的 ticket 校验地址  
	 */
	public String splicingCheckTicketUrl() {
		return SaFoxUtil.spliceTwoUrl(getServerUrl(), getCheckTicketUrl());
	}

	/**
	 * @return 获取拼接url：Server 端查询数据 getData 地址
	 */
	public String splicingGetDataUrl() {
		return SaFoxUtil.spliceTwoUrl(getServerUrl(), getGetDataUrl());
	}

	/**
	 * @return 获取拼接url：Server 端查询 userinfo 地址 
	 */
	public String splicingUserinfoUrl() {
		return SaFoxUtil.spliceTwoUrl(getServerUrl(), getUserinfoUrl());
	}

	/**
	 * @return 获取拼接url：Server 端单点注销地址 
	 */
	public String splicingSloUrl() {
		return SaFoxUtil.spliceTwoUrl(getServerUrl(), getSloUrl());
	}
	
	/**
	 * 以数组形式写入允许的授权回调地址 
	 * @param url 所有集合 
	 * @return 对象自身
	 */
	public SaSsoConfig setAllow(String ...url) {
		this.allowUrl = SaFoxUtil.arrayJoin(url);
		return this;
	}

	
	// -------------------- SaSsoHandle 所有回调函数 -------------------- 
	

	/**
	 * SSO-Server端：未登录时返回的View
	 */
	public Supplier<Object> notLoginView = () -> {
		return "当前会话在SSO-Server认证中心尚未登录（当前未配置登录视图）";
	};

	/**
	 * SSO-Server端：登录函数
	 */
	public BiFunction<String, String, Object> doLoginHandle = (name, pwd) -> {
		return SaResult.error();
	};

	/**
	 * SSO-Client端：自定义校验Ticket返回值的处理逻辑 （每次从认证中心获取校验Ticket的结果后调用）
	 * <p> 参数：loginId, back
	 * <p> 返回值：返回给前端的值
	 */
	public BiFunction<Object, String, Object> ticketResultHandle = null;

	/**
	 * SSO-Client端：发送Http请求的处理函数
	 */
	public Function<String, String> sendHttp = url -> {
		throw new SaSsoException("请配置 Http 请求处理器").setCode(SaSsoErrorCode.CODE_30010);
	};


	// -------------------- 废弃方法 --------------------

	/**
	 * <h2> 属性为 public，请直接访问 </h2>
	 * @param notLoginView SSO-Server端：未登录时返回的View
	 * @return 对象自身
	 */
	@Deprecated
	public SaSsoConfig setNotLoginView(Supplier<Object> notLoginView) {
		this.notLoginView = notLoginView;
		return this;
	}

	/**
	 * <h2> 属性为 public，请直接访问 </h2>
	 * @return 函数 SSO-Server端：未登录时返回的View
	 */
	@Deprecated
	public Supplier<Object> getNotLoginView() {
		return notLoginView;
	}

	/**
	 * <h2> 属性为 public，请直接访问 </h2>
	 * @param doLoginHandle SSO-Server端：登录函数
	 * @return 对象自身
	 */
	@Deprecated
	public SaSsoConfig setDoLoginHandle(BiFunction<String, String, Object> doLoginHandle) {
		this.doLoginHandle = doLoginHandle;
		return this;
	}

	/**
	 * <h2> 属性为 public，请直接访问 </h2>
	 * @return 函数 SSO-Server端：登录函数
	 */
	@Deprecated
	public BiFunction<String, String, Object> getDoLoginHandle() {
		return doLoginHandle;
	}

	/**
	 * <h2> 属性为 public，请直接访问 </h2>
	 * @param ticketResultHandle SSO-Client端：自定义校验Ticket返回值的处理逻辑 （每次从认证中心获取校验Ticket的结果后调用）
	 * @return 对象自身
	 */
	@Deprecated
	public SaSsoConfig setTicketResultHandle(BiFunction<Object, String, Object> ticketResultHandle) {
		this.ticketResultHandle = ticketResultHandle;
		return this;
	}

	/**
	 * <h2> 属性为 public，请直接访问 </h2>
	 * @return 函数 SSO-Client端：自定义校验Ticket返回值的处理逻辑 （每次从认证中心获取校验Ticket的结果后调用）
	 */
	@Deprecated
	public BiFunction<Object, String, Object> getTicketResultHandle() {
		return ticketResultHandle;
	}

	/**
	 * <h2> 属性为 public，请直接访问 </h2>
	 * @param sendHttp SSO-Client端：发送Http请求的处理函数
	 * @return 对象自身
	 */
	@Deprecated
	public SaSsoConfig setSendHttp(Function<String, String> sendHttp) {
		this.sendHttp = sendHttp;
		return this;
	}

	/**
	 * <h2> 属性为 public，请直接访问 </h2>
	 * @return 函数 SSO-Client端：发送Http请求的处理函数
	 */
	@Deprecated
	public Function<String, String> getSendHttp() {
		return sendHttp;
	}

	/**
	 * @return 配置当前 Client 端的单点注销回调URL （为空时自动获取）
	 */
	public String getSsoLogoutCall() {
		return currSsoLogoutCall;
	}

	/**
	 * @param ssoLogoutCall 配置当前 Client 端的单点注销回调URL （为空时自动获取）
	 * @return 对象自身
	 */
	public SaSsoConfig setSsoLogoutCall(String ssoLogoutCall) {
		this.currSsoLogoutCall = ssoLogoutCall;
		return this;
	}

}
