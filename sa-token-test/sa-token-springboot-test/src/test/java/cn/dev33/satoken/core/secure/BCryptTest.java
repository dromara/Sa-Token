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
package cn.dev33.satoken.core.secure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.secure.BCrypt;

/**
 * BCrypt 加密测试
 * 
 * @author dream.
 * @since 2022/1/20
 */
public class BCryptTest {

	@Test
	public void testCheckpw() {
		final String hashed = BCrypt.hashpw("12345");
//		System.out.println(hashed);
		Assertions.assertTrue(BCrypt.checkpw("12345", hashed));
		Assertions.assertFalse(BCrypt.checkpw("123456", hashed));
	}
	
}