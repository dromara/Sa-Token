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
package cn.dev33.satoken.loveqq.boot.utils;

import cn.dev33.satoken.loveqq.boot.testsupport.LoveqqTestHelper;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerResponse;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaTokenConsts;
import com.kfyty.loveqq.framework.core.exception.ResolvableException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaTokenOperateUtil} 写回结果测试
 */
@SaTokenTest
public class SaTokenOperateUtilTest {

	/** 没设 Content-Type 时应该写成 text/plain 并把内容写进输出流 */
	@Test
	public void writeResult_defaultContentType() {
		TestServerResponse response = LoveqqTestHelper.newResponse();
		SaTokenOperateUtil.writeResult(response, "blocked");
		Assertions.assertEquals(SaTokenConsts.CONTENT_TYPE_TEXT_PLAIN, response.getContentType());
		Assertions.assertEquals("blocked", response.bodyText());
	}

	/** 已经设过 Content-Type 时不应该被覆盖 */
	@Test
	public void writeResult_keepExistingContentType() {
		TestServerResponse response = LoveqqTestHelper.newResponse();
		response.setContentType("application/json");
		SaTokenOperateUtil.writeResult(response, "{}");
		Assertions.assertEquals("application/json", response.getContentType());
		Assertions.assertEquals("{}", response.bodyText());
	}

	/** 输出流写出失败时应该包成 ResolvableException */
	@Test
	public void writeResult_ioException_wrap() {
		TestServerResponse response = LoveqqTestHelper.newResponse().failOnOutputStream();
		Assertions.assertThrows(ResolvableException.class, () -> SaTokenOperateUtil.writeResult(response, "x"));
	}

	/** 工具类默认构造应该能 new 出来 */
	@Test
	public void constructor_shouldCreateInstance() {
		Assertions.assertNotNull(new SaTokenOperateUtil());
	}

}
