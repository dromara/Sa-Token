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
package cn.dev33.satoken.sso.processor;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.name.ApiName;
import cn.dev33.satoken.sso.name.ParamName;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * SSO 请求处理器 （Client端）
 * 
 * @author click33
 * @since 1.38.0
 */
public class SaSsoClientProcessor {

	/**
	 * 全局默认实例
	 */
	public static SaSsoClientProcessor instance = new SaSsoClientProcessor();

	/**
	 * 底层 SaSsoClientTemplate 对象
	 */
	public SaSsoClientTemplate ssoClientTemplate = new SaSsoClientTemplate();

	// ----------- SSO-Client 端路由分发 -----------

	/**
	 * 分发 Client 端所有请求
	 * @return 处理结果
	 */
	public Object dister() {
		ApiName apiName = ssoClientTemplate.apiName;

		// 获取对象
		SaRequest req = SaHolder.getRequest();
		SaSsoClientConfig cfg = ssoClientTemplate.getClientConfig();

		// ------------------ 路由分发 ------------------

		// ---------- SSO-Client端：登录地址
		if(req.isPath(apiName.ssoLogin)) {
			return ssoLogin();
		}

		// ---------- SSO-Client端：单点注销
		if(req.isPath(apiName.ssoLogout)) {
			return ssoLogout();
		}

		// ---------- SSO-Client端：单点注销的回调 	[模式三]
		if(req.isPath(apiName.ssoLogoutCall) && cfg.getIsSlo() && cfg.getIsHttp()) {
			return ssoLogoutCall();
		}

		// 默认返回
		return SaSsoConsts.NOT_HANDLE;
	}

	/**
	 * SSO-Client端：登录地址
	 * @return 处理结果
	 */
	public Object ssoLogin() {
		// 获取对象
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		SaSsoClientConfig cfg = ssoClientTemplate.getClientConfig();
		StpLogic stpLogic = ssoClientTemplate.getStpLogic();
		ApiName apiName = ssoClientTemplate.apiName;
		ParamName paramName = ssoClientTemplate.paramName;

		// 获取参数
		String back = req.getParam(paramName.back, "/");
		String ticket = req.getParam(paramName.ticket);

		// 如果当前Client端已经登录，则无需访问SSO认证中心，可以直接返回
		if(stpLogic.isLogin()) {
			return res.redirect(back);
		}
		/*
		 * 此时有两种情况:
		 * 情况1：ticket无值，说明此请求是Client端访问，需要重定向至SSO认证中心
		 * 情况2：ticket有值，说明此请求从SSO认证中心重定向而来，需要根据ticket进行登录
		 */
		if(ticket == null) {
			// 获取当前项目的 sso 登录地址
			// 全局配置了就是用全局的，否则使用当前请求的地址
			String currSsoLoginUrl;
			if(SaFoxUtil.isNotEmpty(cfg.getCurrSsoLogin())) {
				currSsoLoginUrl = cfg.getCurrSsoLogin();
			} else {
				currSsoLoginUrl = SaHolder.getRequest().getUrl();
			}
			// 构建url
			String serverAuthUrl = ssoClientTemplate.buildServerAuthUrl(currSsoLoginUrl, back);
			return res.redirect(serverAuthUrl);
		} else {
			// 1、校验ticket，获取 loginId
			CheckTicketResult ctr = checkTicketByMode2Or3(ticket, apiName.ssoLogin);

			// 2、如果开发者自定义了ticket结果值处理函数，则使用自定义的函数
			if(cfg.ticketResultHandle != null) {
				return cfg.ticketResultHandle.apply(ctr, back);
			}

			// 3、登录并重定向至back地址
			stpLogic.login(ctr.loginId, ctr.remainSessionTimeout);
			return res.redirect(back);
		}
	}

	/**
	 * SSO-Client端：单点注销
	 * @return 处理结果
	 */
	public Object ssoLogout() {
		// 获取对象
		SaSsoClientConfig cfg = ssoClientTemplate.getClientConfig();

		// 无论登录时选择的是模式二还是模式三
		//  在注销时都应该按照模式三的方法，通过 http 请求调用 sso-server 的单点注销接口来做到全端下线
		if(cfg.getIsSlo()) {
			return ssoLogoutByMode3();
		}

		// 默认返回
		return SaSsoConsts.NOT_HANDLE;
	}

	/**
	 * SSO-Client端：单点注销 [模式二]
	 * @return 处理结果
	 */
	public Object ssoLogoutByMode2() {
		// 获取对象
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		StpLogic stpLogic = ssoClientTemplate.getStpLogic();

		// 开始处理
		if(stpLogic.isLogin()) {
			stpLogic.logout(stpLogic.getLoginId());
		}

		// 返回
		return ssoLogoutBack(req, res);
	}

	/**
	 * SSO-Client端：单点注销 [模式三]
	 * @return 处理结果
	 */
	public Object ssoLogoutByMode3() {
		// 获取对象
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		StpLogic stpLogic = ssoClientTemplate.getStpLogic();

		// 如果未登录，则无需注销
		if( ! stpLogic.isLogin()) {
			return ssoLogoutBack(req, res);
		}

		// 调用 sso-server 认证中心单点注销API
		String url = ssoClientTemplate.buildSloUrl(stpLogic.getLoginId());
		SaResult result = ssoClientTemplate.request(url);

		// 校验响应状态码
		if(SaResult.CODE_SUCCESS == result.getCode()) {
			// 极端场景下，sso-server 中心的单点注销可能并不会通知到此 client 端，所以这里需要再补一刀
			if(stpLogic.isLogin()) {
				stpLogic.logout();
			}
			return ssoLogoutBack(req, res);
		} else {
			// 将 sso-server 回应的消息作为异常抛出
			throw new SaSsoException(result.getMsg()).setCode(SaSsoErrorCode.CODE_30006);
		}
	}

	/**
	 * SSO-Client端：单点注销的回调 [模式三]
	 * @return 处理结果
	 */
	public Object ssoLogoutCall() {

		// 获取对象
		SaRequest req = SaHolder.getRequest();
		StpLogic stpLogic = ssoClientTemplate.getStpLogic();
		ParamName paramName = ssoClientTemplate.paramName;
		SaSsoClientConfig ssoConfig = ssoClientTemplate.getClientConfig();

		// 获取参数
		String loginId = req.getParamNotNull(paramName.loginId);
		// String client = req.getParam(paramName.client);
		// String autoLogout = req.getParam(paramName.autoLogout);

		// 校验参数签名
		if(ssoConfig.getIsCheckSign()) {
			ssoClientTemplate.getSignTemplate(ssoConfig.getClient()).
					checkRequest(req, paramName.loginId, paramName.client, paramName.autoLogout);
		} else {
			SaSsoManager.printNoCheckSignWarningByRuntime();
		}

		// 注销当前应用端会话
		stpLogic.logout(loginId);

		// 响应
		return SaResult.ok("单点注销回调成功");
	}

	// 工具方法

	/**
	 * 封装：校验ticket，取出loginId，如果 ticket 无效则抛出异常
	 * @param ticket ticket码
	 * @param currUri 当前路由的uri，用于计算单点注销回调地址
	 * @return loginId
	 */
	public CheckTicketResult checkTicketByMode2Or3(String ticket, String currUri) {
		SaSsoClientConfig cfg = ssoClientTemplate.getClientConfig();
		ApiName apiName = ssoClientTemplate.apiName;
		ParamName paramName = ssoClientTemplate.paramName;

		// --------- 两种模式
		if(cfg.getIsHttp()) {
			// q1、使用模式三：使用 http 请求从认证中心校验ticket

			// 计算当前 sso-client 的单点注销回调地址
			String ssoLogoutCall = null;
			if(cfg.getIsSlo()) {
				// 如果配置了回调地址，就使用配置的值：
				if(SaFoxUtil.isNotEmpty(cfg.getCurrSsoLogoutCall())) {
					ssoLogoutCall = cfg.getCurrSsoLogoutCall();
				}
				// 如果提供了当前 uri，则根据此值来计算：
				else if(SaFoxUtil.isNotEmpty(currUri)) {
					ssoLogoutCall = SaHolder.getRequest().getUrl().replace(currUri, apiName.ssoLogoutCall);
				}
				// 否则视为不注册单点注销回调地址
				else {
				}
			}

			// 构建请求URL
			String checkUrl = ssoClientTemplate.buildCheckTicketUrl(ticket, ssoLogoutCall);

			// 发起请求
			SaResult result = ssoClientTemplate.request(checkUrl);

			// 校验
			if(result.getCode() != null && result.getCode() == SaResult.CODE_SUCCESS) {
				// 取出 loginId
				Object loginId = result.getData();
				if(SaFoxUtil.isEmpty(loginId)) {
					throw new SaSsoException("无效ticket：" + ticket).setCode(SaSsoErrorCode.CODE_30004);
				}
				// 取出 Session 剩余有效期
				Long remainSessionTimeout = result.get(paramName.remainSessionTimeout, Long.class);
				if(remainSessionTimeout == null) {
					remainSessionTimeout = ssoClientTemplate.getStpLogic().getConfig().getTimeout();
				}
				// 构建返回
				return new CheckTicketResult(loginId, remainSessionTimeout);
			} else {
				// 将 sso-server 回应的消息作为异常抛出
				throw new SaSsoException(result.getMsg()).setCode(SaSsoErrorCode.CODE_30005);
			}
		} else {
			// q2、使用模式二：直连Redis校验ticket
			// 		注意此处调用了 SaSsoServerProcessor 处理器里的方法，
			// 		这意味着如果你的 sso-server 端重写了 SaSsoServerProcessor 里的部分方法，
			// 		而在当前 sso-client 没有按照相应格式重写 SaSsoClientProcessor 里的方法，
			// 		可能会导致调用失败（注意是可能，而非一定），
			// 		解决方案为：在当前 sso-client 端也按照 sso-server 端的格式重写 SaSsoClientProcessor 里的方法

			// 取出 loginId
			Object loginId = SaSsoServerProcessor.instance.ssoServerTemplate.checkTicket(ticket, cfg.getClient());
			if(SaFoxUtil.isEmpty(loginId)) {
				throw new SaSsoException("无效ticket：" + ticket).setCode(SaSsoErrorCode.CODE_30004);
			}
			// 取出 Session 剩余有效期
			long remainSessionTimeout = ssoClientTemplate.getStpLogic().getSessionTimeoutByLoginId(loginId);
			// 构建返回
			return new CheckTicketResult(loginId, remainSessionTimeout);
		}
	}

	/**
	 * 封装：单点注销成功后返回结果
	 * @param req SaRequest对象
	 * @param res SaResponse对象
	 * @return 返回结果
	 */
	public Object ssoLogoutBack(SaRequest req, SaResponse res) {
		return SaSsoProcessorHelper.ssoLogoutBack(req, res, ssoClientTemplate.paramName);
	}


	public static class CheckTicketResult {
		public Object loginId;
		public long remainSessionTimeout;
		public CheckTicketResult(Object loginId, long remainSessionTimeout) {
			this.loginId = loginId;
			this.remainSessionTimeout = remainSessionTimeout;
		}
		@Override
		public String toString() {
			return "CheckTicketResult{" +
					"loginId=" + loginId +
					", remainSessionTimeout=" + remainSessionTimeout +
					'}';
		}
	}

}
