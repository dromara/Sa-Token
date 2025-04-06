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

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.FirewallCheckException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.solon.model.SaRequestForSolon;
import cn.dev33.satoken.solon.model.SaResponseForSolon;
import cn.dev33.satoken.solon.util.SaSolonOperateUtil;
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
		catch (StopMatchException ignored) {}
		catch (BackResultException e) {
			SaSolonOperateUtil.writeResult(ctx, e.getMessage());
			return;
		}
		catch (FirewallCheckException e) {
			if(SaFirewallStrategy.instance.checkFailHandle == null) {
				SaSolonOperateUtil.writeResult(ctx, e.getMessage());
			} else {
				SaFirewallStrategy.instance.checkFailHandle.run(e, saRequest, saResponse, null);
			}
			return;
		}
		// 更多异常则不处理，交由 Web 框架处理

		chain.doFilter(ctx);
	}

}
