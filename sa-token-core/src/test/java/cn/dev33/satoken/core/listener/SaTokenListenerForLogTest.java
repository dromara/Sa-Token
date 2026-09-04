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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.handler.SaIgnoreHandler;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.listener.SaTokenListenerForLog;
import cn.dev33.satoken.log.SaLog;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * SaTokenListenerForLog 测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaTokenListenerForLogTest {

	private SaLog previousLog;

	@BeforeEach
	void captureLog() {
		previousLog = SaManager.getLog();
	}

	@AfterEach
	void restoreLog() {
		SaManager.setLog(previousLog);
	}

	/** 各监听事件应委托给 SaManager 配置的日志组件 */
	@Test
	void eventsWriteInfoLog() {
		RecordingLog log = new RecordingLog();
		SaManager.setLog(log);
		log.infoMessages.clear();
		SaTokenListenerForLog listener = new SaTokenListenerForLog();

		listener.doLogin("login", 1, "token", new SaLoginParameter());
		listener.doLogout("login", 1, "token");
		listener.doKickout("login", 1, "token");
		listener.doReplaced("login", 1, "token");
		listener.doDisable("login", 1, "shop", 1, 60);
		listener.doUntieDisable("login", 1, "shop");
		listener.doOpenSafe("login", "token", "pay", 60);
		listener.doCloseSafe("login", "token", "pay");
		listener.doCreateSession("session");
		listener.doLogoutSession("session");
		listener.doRenewTimeout("login", 1, "token", 60);
		listener.doRegisterComponent("component", new Object());
		listener.doRegisterAnnotationHandler(new SaIgnoreHandler());
		listener.doSetStpLogic(new StpLogic("login"));
		listener.doSetConfig(new SaTokenConfig());

		Assertions.assertEquals(15, log.infoMessages.size());
		Assertions.assertTrue(log.infoMessages.get(0).contains("登录成功"));
	}

	/** 可选监听参数为 null 时不应产生日志或抛出异常 */
	@Test
	void optionalEventArgumentsCanBeNull() {
		RecordingLog log = new RecordingLog();
		SaManager.setLog(log);
		log.infoMessages.clear();
		SaTokenListenerForLog listener = new SaTokenListenerForLog();

		listener.doRegisterComponent("component", null);
		listener.doRegisterAnnotationHandler(null);
		listener.doSetStpLogic(null);
		listener.doSetConfig(null);

		Assertions.assertEquals(1, log.infoMessages.size());
	}

	private static class RecordingLog implements SaLog {
		private final List<String> infoMessages = new ArrayList<>();

		@Override
		public void trace(String str, Object... args) {
		}

		@Override
		public void debug(String str, Object... args) {
		}

		@Override
		public void info(String str, Object... args) {
			infoMessages.add(str);
		}

		@Override
		public void warn(String str, Object... args) {
		}

		@Override
		public void error(String str, Object... args) {
		}

		@Override
		public void fatal(String str, Object... args) {
		}
	}

}
