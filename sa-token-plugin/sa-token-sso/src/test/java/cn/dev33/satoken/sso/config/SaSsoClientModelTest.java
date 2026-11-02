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
package cn.dev33.satoken.sso.config;

import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Server 上登记的 Client 模型：getter/setter、splicingPushUrl、非法 allowUrl
 */
public class SaSsoClientModelTest {

	/** 默认值应该是 isPush=false、isSlo=true、pushUrl=/sso/pushC */
	@Test
	public void defaults_matchProduction() {
		SaSsoClientModel m = new SaSsoClientModel();
		Assertions.assertNull(m.getClient());
		Assertions.assertEquals("", m.getAllowUrl());
		Assertions.assertEquals(Boolean.FALSE, m.getIsPush());
		Assertions.assertEquals(Boolean.TRUE, m.getIsSlo());
		Assertions.assertNull(m.getSecretKey());
		Assertions.assertNull(m.getServerUrl());
		Assertions.assertEquals("/sso/pushC", m.getPushUrl());
	}

	/** setter 应该能连缀写回 */
	@Test
	public void gettersAndSetters_roundTrip() {
		SaSsoClientModel m = new SaSsoClientModel()
				.setClient("c")
				.setAllowUrl("http://c.com/*")
				.setIsPush(true)
				.setIsSlo(false)
				.setSecretKey("k")
				.setServerUrl("http://c.com")
				.setPushUrl("/push");
		Assertions.assertEquals("c", m.getClient());
		Assertions.assertEquals("http://c.com/*", m.getAllowUrl());
		Assertions.assertEquals(Boolean.TRUE, m.getIsPush());
		Assertions.assertEquals(Boolean.FALSE, m.getIsSlo());
		Assertions.assertEquals("k", m.getSecretKey());
		Assertions.assertEquals("http://c.com", m.getServerUrl());
		Assertions.assertEquals("/push", m.getPushUrl());
	}

	/** setAllow 应该把地址写进 allowUrl */
	@Test
	public void setAllow_joinsUrls() {
		SaSsoClientModel m = new SaSsoClientModel().setAllow("http://a.com/*", "http://b.com/*");
		Assertions.assertTrue(m.getAllowUrl().contains("http://a.com/*"));
	}

	/** splicingPushUrl 合法时应该拼出绝对地址 */
	@Test
	public void splicingPushUrl_valid() {
		SaSsoClientModel m = new SaSsoClientModel()
				.setClient("c")
				.setServerUrl("http://c.com");
		Assertions.assertEquals("http://c.com/sso/pushC", m.splicingPushUrl());
	}

	/** splicingPushUrl 拼出来不是 url 时应该抛 30023 */
	@Test
	public void splicingPushUrl_invalidThrows30023() {
		SaSsoClientModel m = new SaSsoClientModel().setClient("c").setPushUrl("not-a-url");
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class, m::splicingPushUrl);
		Assertions.assertEquals(SaSsoErrorCode.CODE_30023, ex.getCode());
		Assertions.assertTrue(ex.getMessage().contains("c"));
	}

	/** setAllowUrl 空串应该跳过校验 */
	@Test
	public void setAllowUrl_emptySkipsCheck() {
		Assertions.assertEquals("", new SaSsoClientModel().setAllowUrl("").getAllowUrl());
	}

	/** allowUrl 写成 domain* 应该抛 30015 */
	@Test
	public void setAllowUrl_invalidThrows30015() {
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class,
				() -> new SaSsoClientModel().setAllowUrl("http://example.com*"));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30015, ex.getCode());
	}

	/** toString 里应该能看到 client、isPush */
	@Test
	public void toString_containsFields() {
		String text = new SaSsoClientModel().setClient("c1").toString();
		Assertions.assertTrue(text.startsWith("SaSsoClientModel ["));
		Assertions.assertTrue(text.contains("client=c1"));
		Assertions.assertTrue(text.contains("isPush="));
	}

}
