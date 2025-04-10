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

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaTokenContextModelBox;
import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.solon.util.SaSolonOperateUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

/**
 * CORS 跨域策略过滤器 (基于 Solon)
 *
 * @author click33
 * @since 1.42.0
 */
public class SaTokenCorsFilterForSolon implements Filter {

	@Override
	public void doFilter(Context ctx, FilterChain chain) throws Throwable {
		
		try {
			SaTokenContextModelBox box = SaHolder.getContext().getModelBox();
			SaStrategy.instance.corsHandle.execute(box.getRequest(), box.getResponse(), box.getStorage());
		}
		catch (StopMatchException ignored) {}
		catch (BackResultException e) {
			SaSolonOperateUtil.writeResult(ctx, e.getMessage());
			return;
		}

		chain.doFilter(ctx);
	}

}
