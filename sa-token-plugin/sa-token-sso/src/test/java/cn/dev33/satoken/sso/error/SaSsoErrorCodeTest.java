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
package cn.dev33.satoken.sso.error;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SSO 错误码常量要对得上文档里的 30001-30015、30021-30024
 */
public class SaSsoErrorCodeTest {

	/** 30001 到 30015 应该就是这些固定数字 */
	@Test
	public void codes_30001_to_30015() {
		Assertions.assertEquals(30001, SaSsoErrorCode.CODE_30001);
		Assertions.assertEquals(30002, SaSsoErrorCode.CODE_30002);
		Assertions.assertEquals(30003, SaSsoErrorCode.CODE_30003);
		Assertions.assertEquals(30004, SaSsoErrorCode.CODE_30004);
		Assertions.assertEquals(30005, SaSsoErrorCode.CODE_30005);
		Assertions.assertEquals(30006, SaSsoErrorCode.CODE_30006);
		Assertions.assertEquals(30007, SaSsoErrorCode.CODE_30007);
		Assertions.assertEquals(30008, SaSsoErrorCode.CODE_30008);
		Assertions.assertEquals(30009, SaSsoErrorCode.CODE_30009);
		Assertions.assertEquals(30010, SaSsoErrorCode.CODE_30010);
		Assertions.assertEquals(30011, SaSsoErrorCode.CODE_30011);
		Assertions.assertEquals(30012, SaSsoErrorCode.CODE_30012);
		Assertions.assertEquals(30013, SaSsoErrorCode.CODE_30013);
		Assertions.assertEquals(30014, SaSsoErrorCode.CODE_30014);
		Assertions.assertEquals(30015, SaSsoErrorCode.CODE_30015);
	}

	/** 30021 到 30024 应该就是消息相关这几个码 */
	@Test
	public void codes_30021_to_30024() {
		Assertions.assertEquals(30021, SaSsoErrorCode.CODE_30021);
		Assertions.assertEquals(30022, SaSsoErrorCode.CODE_30022);
		Assertions.assertEquals(30023, SaSsoErrorCode.CODE_30023);
		Assertions.assertEquals(30024, SaSsoErrorCode.CODE_30024);
	}

}
