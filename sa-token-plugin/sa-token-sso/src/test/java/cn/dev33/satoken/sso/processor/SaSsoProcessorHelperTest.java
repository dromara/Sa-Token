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
package cn.dev33.satoken.sso.processor;

import cn.dev33.satoken.sso.name.ParamName;
import cn.dev33.satoken.sso.support.SsoTestSupport;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.sso.support.SsoTest;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 注销回跳：self、url、没 back
 */
@SsoTest
public class SaSsoProcessorHelperTest {

	/** back=self 应该回一段刷新脚本，并带上 html Content-Type */
	@Test
	public void ssoLogoutBack_selfReturnsScript() {
		Object out = SsoTestSupport.withRequest("/sso/signout", SsoTestSupport.params("back", SaSsoConsts.SELF), () -> {
			Object r = SaSsoProcessorHelper.ssoLogoutBack(
					cn.dev33.satoken.context.SaHolder.getRequest(),
					cn.dev33.satoken.context.SaHolder.getResponse(),
					new ParamName());
			Assertions.assertEquals("text/html; charset=utf-8", SsoTestSupport.header("Content-Type"));
			return r;
		});
		Assertions.assertTrue(String.valueOf(out).contains("document.referrer"));
	}

	/** back 是 url 时应该记到 redirectTo */
	@Test
	public void ssoLogoutBack_urlRedirects() {
		String to = SsoTestSupport.withRequest("/sso/signout", SsoTestSupport.params("back", "http://app.com/home"), () -> {
			SaSsoProcessorHelper.ssoLogoutBack(
					cn.dev33.satoken.context.SaHolder.getRequest(),
					cn.dev33.satoken.context.SaHolder.getResponse(),
					new ParamName());
			return SsoTestSupport.redirectTo();
		});
		Assertions.assertEquals("http://app.com/home", to);
	}

	/** 没有 back 时应该返回注销成功的 JSON */
	@Test
	public void ssoLogoutBack_noBackReturnsJson() {
		Object out = SsoTestSupport.withPath("/sso/signout", () ->
				SaSsoProcessorHelper.ssoLogoutBack(
						cn.dev33.satoken.context.SaHolder.getRequest(),
						cn.dev33.satoken.context.SaHolder.getResponse(),
						new ParamName()));
		Assertions.assertTrue(out instanceof SaResult);
		Assertions.assertEquals(SaResult.CODE_SUCCESS, ((SaResult) out).getCode());
		Assertions.assertTrue(((SaResult) out).getMsg().contains("单点注销成功"));
	}

	/** 无参构造也要点一下，别让覆盖率漏掉 */
	@Test
	public void ctor_canNew() {
		Assertions.assertNotNull(new SaSsoProcessorHelper());
	}

}
