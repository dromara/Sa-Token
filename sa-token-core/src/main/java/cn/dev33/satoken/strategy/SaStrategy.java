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
package cn.dev33.satoken.strategy;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.RequestPathInvalidException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.fun.strategy.*;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaTokenConsts;

import java.util.UUID;

/**
 * Sa-Token 策略对象
 * <p>
 * 此类统一定义框架内的一些关键性逻辑算法，方便开发者进行按需重写，例：
 * </p>
 * <pre>
 // SaStrategy全局单例，所有方法都用以下形式重写
 SaStrategy.instance.setCreateToken((loginId, loginType) -》 {
 // 自定义Token生成的算法
 return "xxxx";
 });
 * </pre>
 *
 * @author click33
 * @since 1.27.0
 */
public final class SaStrategy {

	private SaStrategy() {
	}

	/**
	 * 获取 SaStrategy 对象的单例引用
	 */
	public static final SaStrategy instance = new SaStrategy();


	// ----------------------- 所有策略

	/**
	 * 创建 Token 的策略
	 */
	public SaCreateTokenFunction createToken = (loginId, loginType) -> {
		// 根据配置的tokenStyle生成不同风格的token
		String tokenStyle = SaManager.getStpLogic(loginType).getConfigOrGlobal().getTokenStyle();

		switch (tokenStyle) {
			// uuid
			case SaTokenConsts.TOKEN_STYLE_UUID:
				return UUID.randomUUID().toString();

			// 简单uuid (不带下划线)
			case SaTokenConsts.TOKEN_STYLE_SIMPLE_UUID:
				return UUID.randomUUID().toString().replaceAll("-", "");

			// 32位随机字符串
			case SaTokenConsts.TOKEN_STYLE_RANDOM_32:
				return SaFoxUtil.getRandomString(32);

			// 64位随机字符串
			case SaTokenConsts.TOKEN_STYLE_RANDOM_64:
				return SaFoxUtil.getRandomString(64);

			// 128位随机字符串
			case SaTokenConsts.TOKEN_STYLE_RANDOM_128:
				return SaFoxUtil.getRandomString(128);

			// tik风格 (2_14_16)
			case SaTokenConsts.TOKEN_STYLE_TIK:
				return SaFoxUtil.getRandomString(2) + "_" + SaFoxUtil.getRandomString(14) + "_" + SaFoxUtil.getRandomString(16) + "__";

			// 默认，还是uuid
			default:
				SaManager.getLog().warn("配置的 tokenStyle 值无效：{}，仅允许以下取值: " +
						"uuid、simple-uuid、random-32、random-64、random-128、tik", tokenStyle);
				return UUID.randomUUID().toString();
		}
	};

	/**
	 * 创建 Session 的策略
	 */
	public SaCreateSessionFunction createSession = (sessionId) -> {
		return new SaSession(sessionId);
	};

	/**
	 * 判断：集合中是否包含指定元素（模糊匹配）
	 */
	public SaHasElementFunction hasElement = (list, element) -> {

		// 空集合直接返回false
		if(list == null || list.size() == 0) {
			return false;
		}

		// 先尝试一下简单匹配，如果可以匹配成功则无需继续模糊匹配
		if (list.contains(element)) {
			return true;
		}

		// 开始模糊匹配
		for (String patt : list) {
			if(SaFoxUtil.vagueMatch(patt, element)) {
				return true;
			}
		}

		// 走出for循环说明没有一个元素可以匹配成功
		return false;
	};

	/**
	 * 生成唯一式 token 的算法
	 */
	public SaGenerateUniqueTokenFunction generateUniqueToken = (elementName, maxTryTimes, createTokenFunction, checkTokenFunction) -> {

		// 为方便叙述，以下代码注释均假设在处理生成 token 的场景，但实际上本方法也可能被用于生成 code、ticket 等

		// 循环生成
		for (int i = 1; ; i++) {
			// 生成 token
			String token = createTokenFunction.get();

			// 如果 maxTryTimes == -1，表示不做唯一性验证，直接返回
			if (maxTryTimes == -1) {
				return token;
			}

			// 如果 token 在DB库查询不到数据，说明是个可用的全新 token，直接返回
			if (checkTokenFunction.apply(token)) {
				return token;
			}

			// 如果已经循环了 maxTryTimes 次，仍然没有创建出可用的 token，那么抛出异常
			if (i >= maxTryTimes) {
				throw new SaTokenException(elementName + " 生成失败，已尝试" + i + "次，生成算法过于简单或资源池已耗尽");
			}
		}
	};

	/**
	 * 创建 StpLogic 的算法
	 */
	public SaCreateStpLogicFunction createStpLogic = (loginType) -> {
		return new StpLogic(loginType);
	};

	/**
	 * 请求 path 不允许出现的字符
	 */
	public static String[] INVALID_CHARACTER = {
			"//", "\\",
			"%2e", "%2E",	// .
			"%2f", "%2F",	// /
			"%5c", "%5C",	// \
			"%25"	// 空格
	};

	/**
	 * 校验请求 path 的算法
	 */
	public SaCheckRequestPathFunction checkRequestPath = (requestPath, extArg1, extArg2) -> {

		// 不允许为null
		if(requestPath == null) {
			throw new RequestPathInvalidException("非法请求：null", null);
		}
		// 不允许包含非法字符
		for (String item : INVALID_CHARACTER) {
			if (requestPath.contains(item)) {
				throw new RequestPathInvalidException("非法请求：" + requestPath, requestPath);
			}
		}
		// 不允许出现跨目录
		if(requestPath.contains("/.") || requestPath.contains("\\.")) {
			throw new RequestPathInvalidException("非法请求：" + requestPath, requestPath);
		}
	};


	/**
	 * 当请求 path 校验不通过时处理方案的算法，自定义示例：
	 * <pre>
	 * 		SaStrategy.instance.requestPathInvalidHandle = (e, extArg1, extArg2) -> {
	 * 			// 自定义处理逻辑 ...
	 *      };
	 * </pre>
	 */
	public SaRequestPathInvalidHandleFunction requestPathInvalidHandle = null;


	// ----------------------- 重写策略 set连缀风格

	/**
	 * 重写创建 Token 的策略
	 *
	 * @param createToken /
	 * @return /
	 */
	public SaStrategy setCreateToken(SaCreateTokenFunction createToken) {
		this.createToken = createToken;
		return this;
	}

	/**
	 * 重写创建 Session 的策略
	 *
	 * @param createSession /
	 * @return /
	 */
	public SaStrategy setCreateSession(SaCreateSessionFunction createSession) {
		this.createSession = createSession;
		return this;
	}

	/**
	 * 判断：集合中是否包含指定元素（模糊匹配）
	 *
	 * @param hasElement /
	 * @return /
	 */
	public SaStrategy setHasElement(SaHasElementFunction hasElement) {
		this.hasElement = hasElement;
		return this;
	}

	/**
	 * 生成唯一式 token 的算法
	 *
	 * @param generateUniqueToken /
	 * @return /
	 */
	public SaStrategy setGenerateUniqueToken(SaGenerateUniqueTokenFunction generateUniqueToken) {
		this.generateUniqueToken = generateUniqueToken;
		return this;
	}

	/**
	 * 创建 StpLogic 的算法
	 *
	 * @param createStpLogic /
	 * @return /
	 */
	public SaStrategy setCreateStpLogic(SaCreateStpLogicFunction createStpLogic) {
		this.createStpLogic = createStpLogic;
		return this;
	}

	//

	/**
	 * 请更换为 instance
	 */
	@Deprecated
	public static final SaStrategy me = instance;

}
