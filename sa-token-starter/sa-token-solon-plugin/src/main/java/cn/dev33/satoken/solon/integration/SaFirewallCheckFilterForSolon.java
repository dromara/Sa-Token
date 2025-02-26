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
package cn.dev33.satoken.solon.integration;

import cn.dev33.satoken.exception.FirewallCheckException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.solon.model.SaRequestForSolon;
import cn.dev33.satoken.solon.model.SaResponseForSolon;
import cn.dev33.satoken.strategy.SaFirewallStrategy;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

/**
 * 防火墙校验过滤器 (基于 Solon)
 *
 * @author noear
 * @since 1.41.0
 */
public class SaFirewallCheckFilterForSolon implements Filter {

	@Override
	public void doFilter(Context ctx, FilterChain chain) throws Throwable {

		SaRequestForSolon saRequest = new SaRequestForSolon();
		SaResponseForSolon saResponse = new SaResponseForSolon();

		try {
			SaFirewallStrategy.instance.check.execute(saRequest, saResponse, null);
		}
		catch (StopMatchException e) {
			// 如果是 StopMatchException 异常，代表通过了防火墙验证，进入 Controller
		}
		catch (FirewallCheckException e) {
			// FirewallCheckException 异常则交由异常处理策略处理
			if(SaFirewallStrategy.instance.checkFailHandle == null) {
				ctx.render(e.getMessage());
			} else {
				SaFirewallStrategy.instance.checkFailHandle.run(e, saRequest, saResponse, null);
			}
			ctx.setHandled(true);
			return;
		}
		// 更多异常则不处理，交由 Web 框架处理

		chain.doFilter(ctx);
	}

}
