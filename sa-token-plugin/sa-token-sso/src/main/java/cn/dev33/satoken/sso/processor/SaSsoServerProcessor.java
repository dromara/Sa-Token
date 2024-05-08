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
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.name.ApiName;
import cn.dev33.satoken.sso.name.ParamName;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * SSO 请求处理器 （Server端）
 * 
 * @author click33
 * @since 1.38.0
 */
public class SaSsoServerProcessor {

	/**
	 * 全局默认实例
	 */
	public static SaSsoServerProcessor instance = new SaSsoServerProcessor();

	/**
	 * 底层 SaSsoServerTemplate 对象
	 */
	public SaSsoServerTemplate ssoServerTemplate =  new SaSsoServerTemplate();

	// ----------- SSO-Server 端路由分发 -----------

	/**
	 * 分发 Server 端所有请求
	 * @return 处理结果
	 */
	public Object dister() {

		// 获取对象
		SaRequest req = SaHolder.getRequest();
		SaSsoServerConfig cfg = ssoServerTemplate.getServerConfig();
		ApiName apiName = ssoServerTemplate.apiName;

		// ------------------ 路由分发 ------------------

		// ---------- SSO-Server端：授权地址
		if(req.isPath(apiName.ssoAuth)) {
			return ssoAuth();
		}

		// ---------- SSO-Server端：RestAPI 登录接口
		if(req.isPath(apiName.ssoDoLogin)) {
			return ssoDoLogin();
		}

		// ---------- SSO-Server端：校验ticket 获取账号id
		if(req.isPath(apiName.ssoCheckTicket) && cfg.getIsHttp()) {
			return ssoCheckTicket();
		}

		// ---------- SSO-Server端：单点注销
		if(req.isPath(apiName.ssoSignout)) {
			return ssoSignout();
		}

		// 默认返回
		return SaSsoConsts.NOT_HANDLE;
	}

	/**
	 * SSO-Server端：授权地址
	 * @return 处理结果
	 */
	public Object ssoAuth() {
		// 获取对象
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		SaSsoServerConfig cfg = ssoServerTemplate.getServerConfig();
		StpLogic stpLogic = ssoServerTemplate.getStpLogic();
		ParamName paramName = ssoServerTemplate.paramName;

		// ---------- 此处有两种情况分开处理：
		// ---- 情况1：在SSO认证中心尚未登录，需要先去登录
		if( ! stpLogic.isLogin()) {
			return cfg.notLoginView.get();
		}
		// ---- 情况2：在SSO认证中心已经登录，需要重定向回 Client 端，而这又分为两种方式：
		String mode = req.getParam(paramName.mode, "");
		String redirect = req.getParam(paramName.redirect);

		// 方式1：直接重定向回Client端 (mode=simple)
		if(mode.equals(SaSsoConsts.MODE_SIMPLE)) {

			// 若 redirect 为空，则选择 homeRoute，若 homeRoute 也为空，则抛出异常
			if(SaFoxUtil.isEmpty(redirect)) {
				if(SaFoxUtil.isEmpty(cfg.getHomeRoute())) {
					throw new SaSsoException("未指定 redirect 参数，也未配置 homeRoute 路由，无法完成重定向操作").setCode(SaSsoErrorCode.CODE_30014);
				}
				return res.redirect(cfg.getHomeRoute());
			}
			ssoServerTemplate.checkRedirectUrl(redirect);
			return res.redirect(redirect);
		} else {
			// 方式2：带着ticket参数重定向回Client端 (mode=ticket)

			// 校验提供的client是否为非法字符
			String client = req.getParam(paramName.client);
			if(SaSsoConsts.CLIENT_WILDCARD.equals(client)) {
				throw new SaSsoException("无效 client 标识：" + client).setCode(SaSsoErrorCode.CODE_30013);
			}

			// 若 redirect 为空，则选择 homeRoute，若 homeRoute 也为空，则抛出异常
			if(SaFoxUtil.isEmpty(redirect)) {
				if(SaFoxUtil.isEmpty(cfg.getHomeRoute())) {
					throw new SaSsoException("未指定 redirect 参数，也未配置 homeRoute 路由，无法完成重定向操作").setCode(SaSsoErrorCode.CODE_30014);
				}
				return res.redirect(cfg.getHomeRoute());
			}

			// 构建并跳转
			String redirectUrl = ssoServerTemplate.buildRedirectUrl(stpLogic.getLoginId(), client, redirect);
			// 构建成功，说明 redirect 地址合法，此时需要更新一下该账号的Session有效期
			if(cfg.getAutoRenewTimeout()) {
				stpLogic.renewTimeout(stpLogic.getConfigOrGlobal().getTimeout());
			}
			// 跳转
			return res.redirect(redirectUrl);
		}
	}

	/**
	 * SSO-Server端：RestAPI 登录接口
	 * @return 处理结果
	 */
	public Object ssoDoLogin() {
		// 获取对象
		SaRequest req = SaHolder.getRequest();
		SaSsoServerConfig cfg = ssoServerTemplate.getServerConfig();
		ParamName paramName = ssoServerTemplate.paramName;

		// 处理
		return cfg.doLoginHandle.apply(req.getParam(paramName.name), req.getParam(paramName.pwd));
	}

	/**
	 * SSO-Server端：校验ticket 获取账号id [模式三]
	 * @return 处理结果
	 */
	public Object ssoCheckTicket() {
		ParamName paramName = ssoServerTemplate.paramName;

		// 1、获取参数
		SaRequest req = SaHolder.getRequest();
		SaSsoServerConfig ssoServerConfig = ssoServerTemplate.getServerConfig();
		String client = req.getParam(paramName.client);
		String ticket = req.getParamNotNull(paramName.ticket);
		String sloCallback = req.getParam(paramName.ssoLogoutCall);

		// 2、校验提供的client是否为非法字符
		if(SaSsoConsts.CLIENT_WILDCARD.equals(client)) {
			return SaResult.error("无效 client 标识：" + client);
		}

		// 3、校验签名
		if(ssoServerConfig.getIsCheckSign()) {
			ssoServerTemplate.getSignTemplate(client).checkRequest(req,
					paramName.client, paramName.ticket, paramName.ssoLogoutCall);
		} else {
			SaSsoManager.printNoCheckSignWarningByRuntime();
		}

		// 4、校验ticket，获取 loginId
		Object loginId = ssoServerTemplate.checkTicket(ticket, client);
		if(SaFoxUtil.isEmpty(loginId)) {
			return SaResult.error("无效ticket：" + ticket);
		}

		// 5、注册此客户端的单点注销回调URL
		ssoServerTemplate.registerSloCallbackUrl(loginId, client, sloCallback);

		// 6、给 client 端响应结果
		long remainSessionTimeout = ssoServerTemplate.getStpLogic().getSessionTimeoutByLoginId(loginId);
		SaResult result = SaResult.data(loginId).set(paramName.remainSessionTimeout, remainSessionTimeout);
		result = ssoServerConfig.checkTicketAppendData.apply(loginId, result);
		return result;
	}

	/**
	 * SSO-Server端：单点注销
	 * @return 处理结果
	 */
	public Object ssoSignout() {
		// 获取对象
		SaRequest req = SaHolder.getRequest();
		SaSsoServerConfig cfg = ssoServerTemplate.getServerConfig();
		ParamName paramName = ssoServerTemplate.paramName;

		// SSO-Server端：单点注销 [用户访问式]   (不带loginId参数)
		if(cfg.getIsSlo() && ! req.hasParam(paramName.loginId)) {
			return ssoSignoutByUserVisit();
		}

		// SSO-Server端：单点注销 [Client调用式]  (带loginId参数 & isHttp=true)
		if(cfg.getIsHttp() && cfg.getIsSlo() && req.hasParam(paramName.loginId)) {
			return ssoSignoutByClientHttp();
		}

		// 默认返回
		return SaSsoConsts.NOT_HANDLE;
	}

	/**
	 * SSO-Server端：单点注销 [用户访问式]
	 * @return 处理结果
	 */
	public Object ssoSignoutByUserVisit() {
		// 获取对象
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		Object loginId = ssoServerTemplate.getStpLogic().getLoginIdDefaultNull();

		// 单点注销
		if(SaFoxUtil.isNotEmpty(loginId)) {
			ssoServerTemplate.ssoLogout(loginId);
		}

		// 完成
		return ssoLogoutBack(req, res);
	}

	/**
	 * SSO-Server端：单点注销 [Client调用式]
	 * @return 处理结果
	 */
	public Object ssoSignoutByClientHttp() {
		ParamName paramName = ssoServerTemplate.paramName;

		// 获取参数
		SaRequest req = SaHolder.getRequest();
		String loginId = req.getParam(paramName.loginId);
		String client = req.getParam(paramName.client);

		// step.1 校验签名
		if(ssoServerTemplate.getServerConfig().getIsCheckSign()) {
			ssoServerTemplate.getSignTemplate(client).checkRequest(req, paramName.client, paramName.loginId);
		} else {
			SaSsoManager.printNoCheckSignWarningByRuntime();
		}

		// step.2 单点注销
		ssoServerTemplate.ssoLogout(loginId);

		// 响应
		return SaResult.ok();
	}

	/**
	 * 封装：单点注销成功后返回结果
	 * @param req SaRequest对象
	 * @param res SaResponse对象
	 * @return 返回结果
	 */
	public Object ssoLogoutBack(SaRequest req, SaResponse res) {
        return SaSsoProcessorHelper.ssoLogoutBack(req, res, ssoServerTemplate.paramName);
    }

}
