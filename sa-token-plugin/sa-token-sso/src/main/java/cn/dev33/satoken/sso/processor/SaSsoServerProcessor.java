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
import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.name.ApiName;
import cn.dev33.satoken.sso.name.ParamName;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;
import cn.dev33.satoken.util.SaSugar;

import java.util.Map;

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

		// sso-server：授权地址
		if(req.isPath(apiName.ssoAuth)) {
			return ssoAuth();
		}

		// sso-server：RestAPI 登录接口
		if(req.isPath(apiName.ssoDoLogin)) {
			return ssoDoLogin();
		}

		// sso-server：单点注销
		if(req.isPath(apiName.ssoSignout)) {
			return ssoSignout();
		}

		// sso-server：接收推送消息
		if(req.isPath(apiName.ssoPushS)) {
			return ssoPush();
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
			return ssoServerTemplate.strategy.notLoginView.get();
		}
		// ---- 情况2：在SSO认证中心已经登录，需要重定向回 Client 端，而这又分为两种方式：
		String mode = req.getParam(paramName.mode, SaSsoConsts.MODE_TICKET);
		String redirect = req.getParam(paramName.redirect);
		String client = req.getParam(paramName.client);

		// 若 redirect 为空，则选择 homeRoute，若 homeRoute 也为空，则抛出异常
		if(SaFoxUtil.isEmpty(redirect)) {
			if(SaFoxUtil.isEmpty(cfg.getHomeRoute())) {
				throw new SaSsoException("未指定 redirect 参数，也未配置 homeRoute 路由，无法完成重定向操作").setCode(SaSsoErrorCode.CODE_30014);
			}
			ssoServerTemplate.strategy.jumpToRedirectUrlNotice.run(cfg.getHomeRoute());
			return res.redirect(cfg.getHomeRoute());
		}

		String redirectUrl = SaSugar.get(() -> {
			// 方式1：直接重定向回Client端 (mode=simple)
			if(mode.equals(SaSsoConsts.MODE_SIMPLE)) {
				ssoServerTemplate.checkRedirectUrl(client, redirect);
				return redirect;
			} else {
				// 方式2：带着 ticket 参数重定向回Client端 (mode=ticket)

				// 构建并跳转
				String _redirectUrl = ssoServerTemplate.buildRedirectUrl(client, redirect, stpLogic.getLoginId(), stpLogic.getTokenValue());
				// 构建成功，说明 redirect 地址合法，此时需要更新一下该账号的Session有效期
				if(cfg.getAutoRenewTimeout()) {
					stpLogic.renewTimeout(stpLogic.getConfigOrGlobal().getTimeout());
				}
				return _redirectUrl;
			}
		});

		// 跳转
		ssoServerTemplate.strategy.jumpToRedirectUrlNotice.run(redirectUrl);
		return res.redirect(redirectUrl);
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
		return ssoServerTemplate.strategy.doLoginHandle.apply(req.getParam(paramName.name), req.getParam(paramName.pwd));
	}

	/**
	 * SSO-Server端：单点注销
	 * @return 处理结果
	 */
	public Object ssoSignout() {
		// 获取对象
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		Object loginId = ssoServerTemplate.getStpLogic().getLoginIdDefaultNull();
		boolean singleDeviceIdLogout = req.isParam(ssoServerTemplate.paramName.singleDeviceIdLogout, "true");

		// 单点注销
		if(SaFoxUtil.isNotEmpty(loginId)) {
			SaLogoutParameter logoutParameter = ssoServerTemplate.getStpLogic().createSaLogoutParameter();
			if(singleDeviceIdLogout) {
				logoutParameter.setDeviceId(ssoServerTemplate.getStpLogic().getLoginDeviceId());
			}
			ssoServerTemplate.ssoLogout(loginId, logoutParameter);
		}

		// 完成
		return SaSsoProcessorHelper.ssoLogoutBack(req, res, ssoServerTemplate.paramName);
	}

	/**
	 * SSO-Server端：接收推送消息
	 *
	 * @return 处理结果
	 */
	public Object ssoPush() {
		ParamName paramName = ssoServerTemplate.paramName;
		SaSsoServerConfig ssoServerConfig = ssoServerTemplate.getServerConfig();

		// 1、获取参数
		SaRequest req = SaHolder.getRequest();
		String client = req.getParam(paramName.client);

		// 2、校验提供的client是否为非法字符
		if(SaSsoConsts.CLIENT_WILDCARD.equals(client)) {
			return SaResult.error("无效 client 标识：" + client);
		}

		// 3、校验签名
		Map<String, String> paramMap = req.getParamMap();
		if(ssoServerConfig.getIsCheckSign()) {
			ssoServerTemplate.getSignTemplate(client).checkParamMap(paramMap);
		} else {
			SaSsoManager.printNoCheckSignWarningByRuntime();
		}

		// 处理消息
		SaSsoMessage message = new SaSsoMessage(paramMap);
		if( ! ssoServerTemplate.messageHolder.hasHandle(message.getType())) {
			return SaResult.error("未能找到消息处理器: " + message.getType());
		}
		return ssoServerTemplate.handleMessage(message);
	}

}
