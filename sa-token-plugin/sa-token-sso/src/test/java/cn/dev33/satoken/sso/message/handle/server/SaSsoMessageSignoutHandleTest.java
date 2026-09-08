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
package cn.dev33.satoken.sso.message.handle.server;

import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.support.SsoTestSupport;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Server 端单点注销消息处理器
 */
@SaTokenTest
public class SaSsoMessageSignoutHandleTest {

	/** 每个用例前换干净模板 */
	@BeforeEach
	public void reset() {
		SsoTestSupport.resetProcessors();
		SsoTestSupport.installDefaultConfig();
		SaSsoServerProcessor.instance.ssoServerTemplate.strategy.sendRequest = url -> "ok";
	}

	/** isSlo=false 时应该回错误文案 */
	@Test
	public void handle_isSloFalse_returnsError() {
		SaSsoManager.getServerConfig().setIsSlo(false);
		SaSsoMessageSignoutHandle handle = new SaSsoMessageSignoutHandle();
		Assertions.assertEquals(SaSsoConsts.MESSAGE_SIGNOUT, handle.getHandlerType());
		Object out = handle.handle(SaSsoServerProcessor.instance.ssoServerTemplate,
				new SaSsoMessage(SaSsoConsts.MESSAGE_SIGNOUT).set("loginId", 1));
		Assertions.assertTrue(out instanceof SaResult);
		Assertions.assertEquals(SaResult.CODE_ERROR, ((SaResult) out).getCode());
		Assertions.assertTrue(((SaResult) out).getMsg().contains("未开启单点注销"));
	}

	/** 开了单点注销时应该把账号注销并回 ok */
	@Test
	public void handle_isSloTrue_logsOut() {
		SaSsoMessageSignoutHandle handle = new SaSsoMessageSignoutHandle();
		Object out = SsoTestSupport.withPath("/tmp", () -> {
			StpUtil.login(10001);
			return handle.handle(SaSsoServerProcessor.instance.ssoServerTemplate,
					new SaSsoMessage(SaSsoConsts.MESSAGE_SIGNOUT)
							.set("client", SsoTestSupport.CLIENT)
							.set("loginId", 10001)
							.set("deviceId", "d"));
		});
		Assertions.assertTrue(out instanceof SaResult);
		Assertions.assertEquals(SaResult.CODE_SUCCESS, ((SaResult) out).getCode());
	}

}
