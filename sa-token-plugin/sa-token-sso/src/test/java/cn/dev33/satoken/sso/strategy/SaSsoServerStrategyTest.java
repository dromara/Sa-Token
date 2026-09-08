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
package cn.dev33.satoken.sso.strategy;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.NotImplException;
import cn.dev33.satoken.http.SaHttpTemplate;
import cn.dev33.satoken.sso.support.StubSaJsonTemplate;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Server 策略默认 lambda：登录视图、doLogin、发请求、跳转通知
 */
@SaTokenTest
public class SaSsoServerStrategyTest {

	/** 默认未登录视图应该提示尚未登录 */
	@Test
	public void notLoginView_defaultText() {
		Object view = new SaSsoServerStrategy().notLoginView.get();
		Assertions.assertTrue(String.valueOf(view).contains("尚未登录"));
	}

	/** 默认 doLoginHandle 应该直接回 error */
	@Test
	public void doLoginHandle_defaultError() {
		Object out = new SaSsoServerStrategy().doLoginHandle.apply("n", "p");
		Assertions.assertTrue(out instanceof SaResult);
		Assertions.assertEquals(SaResult.CODE_ERROR, ((SaResult) out).getCode());
	}

	/** 默认 sendRequest 走默认 HttpTemplate 时应该抛未实现 */
	@Test
	public void sendRequest_defaultThrowsNotImpl() {
		SaManager.setSaHttpTemplate(new cn.dev33.satoken.http.SaHttpTemplateDefaultImpl());
		Assertions.assertThrows(NotImplException.class,
				() -> new SaSsoServerStrategy().sendRequest.apply("http://x"));
	}

	/** requestAsSaResult 在 stub 了 json 和 http 后应该解析出 code */
	@Test
	public void requestAsSaResult_parsesStubJson() {
		SaManager.setSaJsonTemplate(new StubSaJsonTemplate());
		SaManager.setSaHttpTemplate(new SaHttpTemplate() {
			@Override
			public String get(String url) {
				return "{\"code\":200,\"msg\":\"ok\"}";
			}
			@Override
			public String postByFormData(String url, Map<String, Object> params) {
				return "";
			}
		});
		SaResult r = new SaSsoServerStrategy().requestAsSaResult("http://x");
		Assertions.assertEquals(200, r.getCode());
		Assertions.assertEquals("ok", r.getMsg());
	}

	/** jumpToRedirectUrlNotice 默认应该是空操作 */
	@Test
	public void jumpToRedirectUrlNotice_defaultNoop() {
		new SaSsoServerStrategy().jumpToRedirectUrlNotice.run("http://x");
	}

	/** checkTicketAppendData 默认应该原样返回 result */
	@Test
	public void checkTicketAppendData_defaultIdentity() {
		SaResult src = SaResult.ok();
		SaResult out = new SaSsoServerStrategy().checkTicketAppendData.apply(1, src);
		Assertions.assertSame(src, out);
	}

	/** 默认 asyncRun 应该另起线程把任务跑完 */
	@Test
	public void asyncRun_defaultStartsThread() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		AtomicBoolean ran = new AtomicBoolean(false);
		new SaSsoServerStrategy().asyncRun.run(() -> {
			ran.set(true);
			latch.countDown();
		});
		Assertions.assertTrue(latch.await(3, TimeUnit.SECONDS));
		Assertions.assertTrue(ran.get());
	}

}
