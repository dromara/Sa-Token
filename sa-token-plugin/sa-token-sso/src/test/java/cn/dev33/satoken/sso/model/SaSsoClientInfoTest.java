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

import cn.dev33.satoken.sso.util.SaSsoConsts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 已登录 client 信息：模式三构造、getter、getIndex 返回 long
 */
public class SaSsoClientInfoTest {

	/** 空构造应该全是默认值 */
	@Test
	public void noArgCtor_defaults() {
		SaSsoClientInfo info = new SaSsoClientInfo();
		Assertions.assertEquals(0, info.getMode());
		Assertions.assertNull(info.getClient());
	}

	/** 三参构造应该按模式三填好 client、回调、index */
	@Test
	public void mode3Ctor_fillsFields() {
		long before = System.currentTimeMillis();
		SaSsoClientInfo info = new SaSsoClientInfo("c1", "http://c/sso/logoutCall", 7);
		Assertions.assertEquals(SaSsoConsts.SSO_MODE_3, info.getMode());
		Assertions.assertEquals("c1", info.getClient());
		Assertions.assertEquals("http://c/sso/logoutCall", info.getSloCallbackUrl());
		Assertions.assertEquals(7L, info.getIndex());
		Assertions.assertTrue(info.getRegTime() >= before);
	}

	/** getter/setter 应该能来回写，getIndex 生产上就是返回 long */
	@Test
	public void gettersAndSetters_roundTrip() {
		SaSsoClientInfo info = new SaSsoClientInfo()
				.setMode(2)
				.setClient("x")
				.setSloCallbackUrl("u")
				.setRegTime(99L)
				.setIndex(4);
		Assertions.assertEquals(2, info.getMode());
		Assertions.assertEquals("x", info.getClient());
		Assertions.assertEquals("u", info.getSloCallbackUrl());
		Assertions.assertEquals(99L, info.getRegTime());
		Assertions.assertEquals(4L, info.getIndex());
	}

	/** toString 文案里还写着 SaSsoClientModel，按生产原样认 */
	@Test
	public void toString_keepsProductionPrefix() {
		String text = new SaSsoClientInfo("c", "u", 1).toString();
		Assertions.assertTrue(text.contains("SaSsoClientModel{"));
		Assertions.assertTrue(text.contains("client='c'"));
	}

}
