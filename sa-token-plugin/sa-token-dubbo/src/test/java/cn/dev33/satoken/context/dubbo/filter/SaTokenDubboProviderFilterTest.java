/*
 * Copyright 2020-2099 sa-token.com
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
package cn.dev33.satoken.context.dubbo.filter;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.dubbo.support.DubboTestSupport;
import cn.dev33.satoken.exception.SameTokenInvalidException;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcInvocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Provider Filter：Same-Token 校验，含小写 attachment 兜底。
 */
@SaTokenTest
public class SaTokenDubboProviderFilterTest {

	private final SaTokenDubboProviderFilter filter = new SaTokenDubboProviderFilter();

	/** 每条用例后清掉 RpcContext */
	@AfterEach
	public void cleanup() {
		DubboTestSupport.cleanup();
	}

	/** 没开 checkSameToken 时应该直接放行 */
	@Test
	public void checkSameTokenOff_passesThrough() {
		SaManager.getConfig().setCheckSameToken(false);
		Result result = filter.invoke(DubboTestSupport.okInvoker("ok"), DubboTestSupport.invocation());
		Assertions.assertEquals("ok", result.getValue());
	}

	/** 开了校验且 Same-Token 正确时应该放行 */
	@Test
	public void validSameToken_passesThrough() {
		SaManager.getConfig().setCheckSameToken(true);
		RpcInvocation invocation = DubboTestSupport.invocation();
		invocation.setAttachment(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
		Result result = filter.invoke(DubboTestSupport.okInvoker("ok"), invocation);
		Assertions.assertEquals("ok", result.getValue());
	}

	/** 开了校验但没带 Same-Token 时应该抛 SameTokenInvalidException */
	@Test
	public void missingSameToken_throws() {
		SaManager.getConfig().setCheckSameToken(true);
		Assertions.assertThrows(SameTokenInvalidException.class,
				() -> filter.invoke(DubboTestSupport.okInvoker("ok"), DubboTestSupport.invocation()));
	}

	/** 开了校验但 Same-Token 不对时应该抛 SameTokenInvalidException */
	@Test
	public void wrongSameToken_throws() {
		SaManager.getConfig().setCheckSameToken(true);
		RpcInvocation invocation = DubboTestSupport.invocation();
		invocation.setAttachment(SaSameUtil.SAME_TOKEN, "not-the-token");
		Assertions.assertThrows(SameTokenInvalidException.class,
				() -> filter.invoke(DubboTestSupport.okInvoker("ok"), invocation));
	}

	/** attachment 被协议改成小写时，也应该能校验通过（issues/I4WXQG） */
	@Test
	public void lowercaseSameTokenKey_stillValid() {
		SaManager.getConfig().setCheckSameToken(true);
		RpcInvocation invocation = DubboTestSupport.invocation();
		invocation.setAttachment(SaSameUtil.SAME_TOKEN.toLowerCase(), SaSameUtil.getToken());
		Result result = filter.invoke(DubboTestSupport.okInvoker("ok"), invocation);
		Assertions.assertEquals("ok", result.getValue());
	}

}
