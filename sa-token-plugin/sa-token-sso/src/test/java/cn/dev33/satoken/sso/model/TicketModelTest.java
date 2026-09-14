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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * TicketModel 构造、getter/setter、toString
 */
public class TicketModelTest {

	/** 无参构造应该带上 createTime */
	@Test
	public void noArgCtor_fillsCreateTime() {
		long before = System.currentTimeMillis();
		TicketModel m = new TicketModel();
		Assertions.assertTrue(m.getCreateTime() >= before);
	}

	/** 全参构造应该把四个字段都塞进去 */
	@Test
	public void fullCtor_setsFields() {
		TicketModel m = new TicketModel("t1", "c1", 10001, "tok");
		Assertions.assertEquals("t1", m.getTicket());
		Assertions.assertEquals("c1", m.getClient());
		Assertions.assertEquals(10001, m.getLoginId());
		Assertions.assertEquals("tok", m.getTokenValue());
	}

	/** setter 应该连缀写回，getter 能读到 */
	@Test
	public void gettersAndSetters_roundTrip() {
		TicketModel m = new TicketModel()
				.setTicket("t")
				.setClient("c")
				.setLoginId("u")
				.setTokenValue("tv")
				.setCreateTime(123L);
		Assertions.assertEquals("t", m.getTicket());
		Assertions.assertEquals("c", m.getClient());
		Assertions.assertEquals("u", m.getLoginId());
		Assertions.assertEquals("tv", m.getTokenValue());
		Assertions.assertEquals(123L, m.getCreateTime());
	}

	/** toString 里应该能看到 ticket、client */
	@Test
	public void toString_containsMainFields() {
		String text = new TicketModel("t", "c", 1, "tok").toString();
		Assertions.assertTrue(text.contains("ticket='t'"));
		Assertions.assertTrue(text.contains("client='c'"));
		Assertions.assertTrue(text.contains("tokenValue="));
	}

}
