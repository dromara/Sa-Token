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

import cn.dev33.satoken.exception.RequestPathInvalidException;
import cn.dev33.satoken.fun.strategy.SaCheckRequestPathFunction;
import cn.dev33.satoken.fun.strategy.SaRequestPathInvalidHandleFunction;

/**
 * Sa-Token 防火墙策略
 *
 * @author click33
 * @since 1.40.0
 */
public final class SaFirewallStrategy {

	private SaFirewallStrategy() {
	}

	/**
	 * 全局单例引用
	 */
	public static final SaFirewallStrategy instance = new SaFirewallStrategy();


	// ----------------------- 所有策略


	/**
	 * 请求 path 黑名单
	 */
	public String[] blackPaths = {};

	/**
	 * 请求 path 白名单
	 */
	public String[] whitePaths = {};

	/**
	 * 请求 path 不允许出现的字符
	 */
	public String[] invalidCharacter = {
			"//",           // //
			"\\",			// \
			"%2e", "%2E",	// .
			"%2f", "%2F",	// /
			"%5c", "%5C",	// \
			";", "%3b", "%3B",	// ;    // 参考资料：https://mp.weixin.qq.com/s/77CIDZbgBwRunJeluofPTA
			"%25"			// 空格
	};

	/**
	 * 校验请求 path 的算法
	 */
	public SaCheckRequestPathFunction checkRequestPath = (requestPath, extArg1, extArg2) -> {
		// 1、如果在白名单里，则直接放行
		for (String item : whitePaths) {
			if (requestPath.equals(item)) {
				return;
			}
		}

		// 2、如果在黑名单里，则抛出异常
		for (String item : blackPaths) {
			if (requestPath.equals(item)) {
				throw new RequestPathInvalidException("非法请求：" + requestPath, requestPath);
			}
		}

		// 3、检查是否包含非法字符

		// 不允许为null
		if(requestPath == null) {
			throw new RequestPathInvalidException("非法请求：null", null);
		}
		// 不允许包含非法字符
		for (String item : invalidCharacter) {
			if (requestPath.contains(item)) {
				throw new RequestPathInvalidException("非法请求：" + requestPath, requestPath);
			}
		}
		// 不允许出现跨目录字符
		if(requestPath.contains("/.") || requestPath.contains("\\.")) {
			throw new RequestPathInvalidException("非法请求：" + requestPath, requestPath);
		}
	};


	/**
	 * 当请求 path 校验不通过时处理方案的算法，自定义示例：
	 * <pre>
	 * 		SaFirewallStrategy.instance.requestPathInvalidHandle = (e, extArg1, extArg2) -> {
	 * 			// 自定义处理逻辑 ...
	 *      };
	 * </pre>
	 */
	public SaRequestPathInvalidHandleFunction requestPathInvalidHandle = null;


}
