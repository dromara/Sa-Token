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
package cn.dev33.satoken.sso.message;

import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SSO 消息构造、类型校验、取值删值
 */
public class SaSsoMessageTest {

	/** 无参构造应该是空消息 */
	@Test
	public void noArgCtor_empty() {
		SaSsoMessage msg = new SaSsoMessage();
		Assertions.assertNull(msg.getType());
		Assertions.assertTrue(msg.isEmpty());
	}

	/** 按类型构造应该写好 msgType */
	@Test
	public void typeCtor_setsMsgType() {
		SaSsoMessage msg = new SaSsoMessage("checkTicket");
		Assertions.assertEquals("checkTicket", msg.getType());
		Assertions.assertEquals("checkTicket", msg.get(SaSsoMessage.MSG_TYPE));
	}

	/** Map 构造应该把参数原样拷进来 */
	@Test
	public void mapCtor_copiesEntries() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(SaSsoMessage.MSG_TYPE, "signout");
		map.put("loginId", 1);
		SaSsoMessage msg = new SaSsoMessage(map);
		Assertions.assertEquals("signout", msg.getType());
		Assertions.assertEquals(1, msg.get("loginId"));
	}

	/** setType / set / delete 应该能改 map */
	@Test
	public void setAndDelete_mutateSelf() {
		SaSsoMessage msg = new SaSsoMessage();
		Assertions.assertSame(msg, msg.setType("logoutCall"));
		Assertions.assertSame(msg, msg.set("k", "v"));
		Assertions.assertEquals("v", msg.get("k"));
		Assertions.assertSame(msg, msg.delete("k"));
		Assertions.assertNull(msg.get("k"));
	}

	/** 没写 msgType 时 checkType 应该抛 30022 */
	@Test
	public void checkType_emptyThrows30022() {
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class, () -> new SaSsoMessage().checkType());
		Assertions.assertEquals(SaSsoErrorCode.CODE_30022, ex.getCode());
		new SaSsoMessage("ok").checkType();
	}

	/** getValueNotNull 缺参应该抛 30024，有值就返回 */
	@Test
	public void getValueNotNull_missingThrows30024() {
		SaSsoMessage msg = new SaSsoMessage("t").set("ticket", "abc");
		Assertions.assertEquals("abc", msg.getValueNotNull("ticket"));
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class, () -> msg.getValueNotNull("nope"));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30024, ex.getCode());
		Assertions.assertTrue(ex.getMessage().contains("nope"));
	}

}
