/*
 * Copyright 2020-2099 sa-token.cc
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
package cn.dev33.satoken.core.context.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.context.SaTokenContextDefaultImpl;
import cn.dev33.satoken.exception.SaTokenException;

/**
 * 默认上下文测试 
 * 
 * @author click33
 * @since: 2022-9-5
 */
public class SaTokenContextDefaultImplTest {

	@Test
	public void testSaTokenContextDefaultImpl() {
		SaTokenContextDefaultImpl context = new SaTokenContextDefaultImpl();
		Assertions.assertThrows(SaTokenException.class, () -> context.getStorage());
		Assertions.assertThrows(SaTokenException.class, () -> context.getRequest());
		Assertions.assertThrows(SaTokenException.class, () -> context.getResponse());
	}
	
}
