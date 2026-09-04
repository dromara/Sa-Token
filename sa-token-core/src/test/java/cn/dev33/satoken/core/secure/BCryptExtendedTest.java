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
package cn.dev33.satoken.core.secure;

import cn.dev33.satoken.secure.BCrypt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

/**
 * BCrypt 扩展路径测试
 *
 * @author click33
 * @since 1.46.0
 */
public class BCryptExtendedTest {

	/** gensalt 默认、自定义轮数及指定 SecureRandom 应生成合法盐值 */
	@Test
	void gensaltVariants() {
		String defaultSalt = BCrypt.gensalt();
		Assertions.assertTrue(defaultSalt.startsWith("$2a$10$"));

		String customSalt = BCrypt.gensalt(4);
		Assertions.assertTrue(customSalt.startsWith("$2a$04$"));

		SecureRandom random = new SecureRandom(new byte[] {1, 2, 3, 4});
		String seededSalt = BCrypt.gensalt(4, random);
		Assertions.assertTrue(seededSalt.startsWith("$2a$04$"));
	}

	/** hashpw 使用自定义盐值后 checkpw 应能正确校验 */
	@Test
	void hashpwWithCustomSaltAndRevision() {
		String salt = BCrypt.gensalt(4);
		String hashed = BCrypt.hashpw("secret", salt);
		Assertions.assertTrue(BCrypt.checkpw("secret", hashed));
		Assertions.assertFalse(BCrypt.checkpw("wrong", hashed));
	}

	/** hashpw 传入非法盐值应抛出 IllegalArgumentException */
	@Test
	void hashpwInvalidSaltThrows() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> BCrypt.hashpw("pwd", "invalid-salt"));
	}

	/** checkpw 在哈希格式非法时应返回 false */
	@Test
	void checkpwReturnsFalseOnInvalidHash() {
		Assertions.assertFalse(BCrypt.checkpw("pwd", "not-a-bcrypt-hash"));
	}

	/** crypt 在盐长不足或轮数过小时应抛出 IllegalArgumentException */
	@Test
	void cryptValidatesRoundsAndSaltLength() {
		BCrypt bcrypt = new BCrypt();
		byte[] password = "pwd".getBytes();
		byte[] badSalt = new byte[8];

		Assertions.assertThrows(IllegalArgumentException.class,
				() -> bcrypt.crypt(password, badSalt, 4, new int[6]));
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> bcrypt.crypt(password, new byte[16], 3, new int[6]));
	}

	/** gensalt 轮数超过上限时应抛出 IllegalArgumentException */
	@Test
	void gensaltRejectsTooManyRounds() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> BCrypt.gensalt(31));
	}

}
