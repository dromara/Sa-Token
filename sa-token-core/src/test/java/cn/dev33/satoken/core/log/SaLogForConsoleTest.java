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
package cn.dev33.satoken.core.log;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.log.SaLogForConsole;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * SaLogForConsole 控制台日志输出测试
 */
@SaTokenTest
public class SaLogForConsoleTest {

	private final SaLogForConsole log = new SaLogForConsole();

	/** println 应按日志级别与彩色配置输出 */
	@Test
	void println_respectsLogLevelAndColor() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsLog(true);
		config.setLogLevel("trace");
		config.setLogLevelInt(SaLogForConsole.trace);
		config.setIsColorLog(true);
		SaManager.setConfig(config);

		PrintStream original = System.out;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
		try {
			log.trace("trace {}", "msg");
			log.debug("debug {}", "msg");
			log.info("info {}", "msg");
			log.warn("warn {}", "msg");
			log.error("error {}", "msg");
			log.fatal("fatal {}", "msg");

			String output = out.toString();
			Assertions.assertTrue(output.contains("SA [TRACE]-->:"));
			Assertions.assertTrue(output.contains("SA [INFO] -->:"));
			Assertions.assertTrue(output.contains("SA [FATAL]-->:"));
			Assertions.assertTrue(output.contains("\033[32m"));
		} finally {
			System.setOut(original);
		}
	}

	/** 日志关闭或低于级别时应跳过输出 */
	@Test
	void println_skipsWhenLogDisabledOrBelowLevel() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsLog(false);
		SaManager.setConfig(config);

		PrintStream original = System.out;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
		try {
			log.info("should-not-print");
			Assertions.assertEquals("", out.toString());
		} finally {
			System.setOut(original);
		}

		config.setIsLog(true);
		config.setLogLevelInt(SaLogForConsole.error);
		config.setIsColorLog(false);
		SaManager.setConfig(config);

		out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
		try {
			log.info("filtered-info");
			log.error("visible-error");
			String output = out.toString();
			Assertions.assertFalse(output.contains("filtered-info"));
			Assertions.assertTrue(output.contains("visible-error"));
			Assertions.assertFalse(output.contains("\033["));
		} finally {
			System.setOut(original);
		}
	}

	/** 直接调用 println 应格式化输出消息 */
	@Test
	void println_directCall() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsLog(true);
		config.setLogLevelInt(SaLogForConsole.debug);
		config.setIsColorLog(false);
		SaManager.setConfig(config);

		PrintStream original = System.out;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
		try {
			log.println(SaLogForConsole.debug, SaLogForConsole.DEBUG_COLOR,
					SaLogForConsole.DEBUG_PREFIX, "direct {}", "call");
			Assertions.assertTrue(out.toString().contains("direct call"));
		} finally {
			System.setOut(original);
		}
	}

}
