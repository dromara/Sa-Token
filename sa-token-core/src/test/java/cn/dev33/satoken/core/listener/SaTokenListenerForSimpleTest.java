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
package cn.dev33.satoken.core.listener;

import cn.dev33.satoken.annotation.handler.SaIgnoreHandler;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaTokenListenerForSimple 空实现方法调用测试
 */
public class SaTokenListenerForSimpleTest {

	/** 空实现所有方法调用不应抛出异常 */
	@Test
	void invokeAllMethodsWithoutException() {
		SaTokenListenerForSimple listener = new SaTokenListenerForSimple();
		SaLoginParameter loginParameter = new SaLoginParameter();
		SaLogoutParameter logoutParameter = new SaLogoutParameter();

		Assertions.assertDoesNotThrow(() -> {
			listener.doLogin("login", 1, "token", loginParameter);
			listener.doBeforeLogout("login", 1, "token", logoutParameter);
			listener.doBeforeKickout("login", 1, "token", logoutParameter);
			listener.doBeforeReplaced("login", 1, "token", logoutParameter);
			listener.doLogout("login", 1, "token");
			listener.doKickout("login", 1, "token");
			listener.doReplaced("login", 1, "token");
			listener.doDisable("login", 1, "shop", 1, 60);
			listener.doUntieDisable("login", 1, "shop");
			listener.doOpenSafe("login", "token", "pay", 120);
			listener.doCloseSafe("login", "token", "pay");
			listener.doCreateSession("session-id");
			listener.doLogoutSession("session-id");
			listener.doRenewTimeout("login", 1, "token", 3600);
			listener.doRegisterComponent("SaLog", new Object());
			listener.doRegisterAnnotationHandler(new SaIgnoreHandler());
			listener.doSetStpLogic(new StpLogic("test"));
			listener.doSetConfig(new SaTokenConfig());
		});
	}

}
