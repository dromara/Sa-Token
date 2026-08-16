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
package cn.dev33.satoken.core.stp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.servlet.util.SaTokenContextServletUtil;
import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.dev33.satoken.springboot.StartUpApplication;
import cn.dev33.satoken.stp.StpUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * loginId 冒号校验测试
 */
@SpringBootTest(classes = StartUpApplication.class)
public class AllowLoginIdColonTest {

	private Boolean originalAllowLoginIdColon;

	@BeforeEach
	public void setUp() {
		SaTokenContextServletUtil.setContext(SpringMVCUtil.getRequest(), SpringMVCUtil.getResponse());
		originalAllowLoginIdColon = SaManager.getConfig().getAllowLoginIdColon();
		SaManager.getConfig().setAllowLoginIdColon(false);
	}

	@AfterEach
	public void tearDown() {
		StpUtil.logout();
		SaManager.getConfig().setAllowLoginIdColon(originalAllowLoginIdColon);
		SaTokenContextServletUtil.clearContext();
	}

	@Test
	public void testDefaultDisallowLoginIdColon() {
		Assertions.assertEquals(false, SaManager.getConfig().getAllowLoginIdColon());
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class, () -> StpUtil.login("user:admin"));
		Assertions.assertEquals(SaErrorCode.CODE_11018, ex.getCode());
	}

	@Test
	public void testAllowLoginIdColonWhenEnabled() {
		SaManager.getConfig().setAllowLoginIdColon(true);
		StpUtil.login("user:admin");
		Assertions.assertEquals("user:admin", StpUtil.getLoginId());
		Assertions.assertTrue(StpUtil.isLogin());
	}

}
