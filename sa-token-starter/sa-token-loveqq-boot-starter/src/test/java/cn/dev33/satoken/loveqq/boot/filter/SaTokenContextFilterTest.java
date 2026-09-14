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
package cn.dev33.satoken.loveqq.boot.filter;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.loveqq.boot.testsupport.LoveqqTestHelper;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerRequest;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerResponse;
import cn.dev33.satoken.test.SaTokenTest;
import com.kfyty.loveqq.framework.web.core.filter.Filter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@link SaTokenContextFilter} 上下文写入与清理测试
 */
@SaTokenTest
public class SaTokenContextFilterTest {

	/** 每个用例开始前挂上 LoveQQ 策略 */
	@BeforeEach
	public void setUp() {
		LoveqqTestHelper.ensureLoveqqStrategy();
	}

	/** 过滤器返回的 Continue 回调执行前应该能拿到上下文，跑完 finally 要清掉 */
	@Test
	public void doFilter_setAndClearContext() {
		TestServerRequest request = LoveqqTestHelper.newGetRequest("/ctx");
		TestServerResponse response = LoveqqTestHelper.newResponse();
		SaTokenContextFilter filter = new SaTokenContextFilter();

		Filter.Continue result = filter.doFilter(request, response);
		Assertions.assertTrue(result._continue_());
		Assertions.assertEquals("/ctx", SaHolder.getRequest().getRequestPath());

		result.finally_run();
		Assertions.assertThrows(Exception.class, () -> SaHolder.getRequest().getRequestPath());
	}

}
