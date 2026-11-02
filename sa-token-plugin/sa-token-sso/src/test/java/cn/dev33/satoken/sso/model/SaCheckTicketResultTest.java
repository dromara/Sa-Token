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
package cn.dev33.satoken.sso.model;

import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 校验 ticket 结果封装的字段和 toString
 */
public class SaCheckTicketResultTest {

	/** 公开字段应该能直接读写，toString 能带上它们 */
	@Test
	public void fieldsAndToString() {
		SaCheckTicketResult r = new SaCheckTicketResult();
		r.loginId = 10001;
		r.tokenValue = "tok";
		r.deviceId = "dev";
		r.remainTokenTimeout = 10L;
		r.remainSessionTimeout = 20L;
		r.centerId = "center";
		r.result = SaResult.ok();
		Assertions.assertEquals(10001, r.loginId);
		Assertions.assertEquals("tok", r.tokenValue);
		Assertions.assertEquals("dev", r.deviceId);
		Assertions.assertEquals(10L, r.remainTokenTimeout);
		Assertions.assertEquals(20L, r.remainSessionTimeout);
		Assertions.assertEquals("center", r.centerId);
		Assertions.assertNotNull(r.result);
		String text = r.toString();
		Assertions.assertTrue(text.contains("loginId=10001"));
		Assertions.assertTrue(text.contains("tokenValue='tok'"));
		Assertions.assertTrue(text.contains("centerId=center"));
	}

}
