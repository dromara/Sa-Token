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
import cn.dev33.satoken.fun.strategy.SaFirewallCheckFailHandleFunction;
import cn.dev33.satoken.fun.strategy.SaFirewallCheckFunction;
import cn.dev33.satoken.strategy.hooks.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 防火墙策略
 *
 * @author click33
 * @since 1.40.0
 */
public final class SaFirewallStrategy {

	/**
	 * 全局单例引用
	 */
	public static final SaFirewallStrategy instance = new SaFirewallStrategy();


	// ----------------------- 所有策略

	public List<SaFirewallCheckHook> checkHooks = new ArrayList<>();

	private SaFirewallStrategy() {
		checkHooks.add(SaFirewallCheckHookForWhitePath.instance);
		checkHooks.add(SaFirewallCheckHookForBlackPath.instance);
		checkHooks.add(SaFirewallCheckHookForPathDangerCharacter.instance);
		checkHooks.add(SaFirewallCheckHookForDirectoryTraversal.instance);
		checkHooks.add(SaFirewallCheckHookForHost.instance);
		checkHooks.add(SaFirewallCheckHookForHttpMethod.instance);
		checkHooks.add(SaFirewallCheckHookForHeader.instance);
		checkHooks.add(SaFirewallCheckHookForParameter.instance);
	}

	// 注册一个防火墙校验 hook
	public void registerCheckHook(SaFirewallCheckHook checkHook) {
		SaManager.getLog().info("防火墙校验 hook 注册成功: " + checkHook.getClass());
		checkHooks.add(checkHook);
	}

	/**
	 * 防火墙校验函数
	 */
	public SaFirewallCheckFunction check = (req, res, extArg) -> {
		for (SaFirewallCheckHook checkHook : checkHooks) {
			checkHook.execute(req, res, extArg);
		}
	};

	/**
	 * 当请求 path 校验不通过时地处理方案，自定义示例：
	 * <pre>
	 * 		SaFirewallStrategy.instance.checkFailHandle = (e, req, res, extArg) -> {
	 * 			// 自定义处理逻辑 ...
	 *      };
	 * </pre>
	 */
	public SaFirewallCheckFailHandleFunction checkFailHandle = null;


}
