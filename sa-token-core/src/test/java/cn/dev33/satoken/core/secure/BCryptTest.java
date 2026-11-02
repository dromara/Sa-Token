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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import cn.dev33.satoken.secure.BCrypt;

import java.security.SecureRandom;
import java.util.Random;
/**
 * BCrypt 加密测试
 * 
 * @author dream.
 * @since 2022/1/20
 */
public class BCryptTest {
	/** checkpw 应正确校验 hashpw 生成的密码哈希 */
	@Test
	public void testCheckpw() {
		final String hashed = BCrypt.hashpw("12345");
		Assertions.assertTrue(BCrypt.checkpw("12345", hashed));
		Assertions.assertFalse(BCrypt.checkpw("123456", hashed));
	}

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

	/** 同一颗 java.util.Random 种子两次 gensalt 应该打出同一条盐，hashpw 应对上黄金哈希 */
	@Test
	void gensalt_seededRandom_sameSaltAndHash() {
		String salt = BCrypt.gensalt(4, seededSecureRandom(12345L));
		Assertions.assertEquals("$2a$04$zgAdVBExWWKgobhsc.dG4e", salt);
		Assertions.assertEquals("$2a$04$zgAdVBExWWKgobhsc.dG4e", BCrypt.gensalt(4, seededSecureRandom(12345L)));
		Assertions.assertEquals("$2a$04$zgAdVBExWWKgobhsc.dG4el234hY.uTwWn/tsPKKbq5BSZ9lA.GRC",
				BCrypt.hashpw("secret", salt));
	}

	/** 用可复现的 Random 喂 SecureRandom.nextBytes，不改生产 API */
	private static SecureRandom seededSecureRandom(long seed) {
		return new SecureRandom() {
			private final Random random = new Random(seed);
			@Override
			public void nextBytes(byte[] bytes) {
				random.nextBytes(bytes);
			}
		};
	}

	/** gensalt 轮数超过上限时应抛出 IllegalArgumentException */
	@Test
	void gensaltRejectsTooManyRounds() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> BCrypt.gensalt(31));
	}

	/** hashpw 使用自定义盐值后 checkpw 应能正确校验 */
	@Test
	void hashpwWithCustomSaltAndRevision() {
		String salt = BCrypt.gensalt(4);
		String hashed = BCrypt.hashpw("secret", salt);
		Assertions.assertTrue(BCrypt.checkpw("secret", hashed));
		Assertions.assertFalse(BCrypt.checkpw("wrong", hashed));
	}

	/** hashpw 应兼容 $2$ 旧版盐值格式 */
	@Test
	void hashpw_legacyTwoDollarSaltFormat() {
		String modernSalt = BCrypt.gensalt(10);
		String legacySalt = "$2$10$" + modernSalt.substring(7);
		String hashed = BCrypt.hashpw("legacy", legacySalt);
		Assertions.assertTrue(BCrypt.checkpw("legacy", hashed));
	}

	/** hashpw 应支持 $2b$ 次版本修订 */
	@Test
	void hashpw_minorRevisionB() {
		String salt = BCrypt.gensalt(4);
		String minorBSalt = salt.replace("$2a$", "$2b$");
		String hashed = BCrypt.hashpw("minor-b", minorBSalt);
		Assertions.assertTrue(BCrypt.checkpw("minor-b", hashed));
	}

	/** hashpw 应支持 $2x$ 与 $2y$ 次版本修订 */
	@Test
	void hashpw_minorRevisionX_and_Y() {
		String saltX = BCrypt.gensalt(4).replace("$2a$", "$2x$");
		String saltY = BCrypt.gensalt(4).replace("$2a$", "$2y$");
		String hashX = BCrypt.hashpw("minor-x", saltX);
		String hashY = BCrypt.hashpw("minor-y", saltY);
		Assertions.assertTrue(BCrypt.checkpw("minor-x", hashX));
		Assertions.assertTrue(BCrypt.checkpw("minor-y", hashY));
	}

	/** hashpw 在非法次版本 $2z$ 时应抛出 IllegalArgumentException */
	@Test
	void hashpw_invalidMinorRevision() {
		String salt = BCrypt.gensalt(4).replace("$2a$", "$2z$");
		Assertions.assertThrows(IllegalArgumentException.class, () -> BCrypt.hashpw("pwd", salt));
	}

	/** hashpw 传入非法盐值应抛出 IllegalArgumentException */
	@Test
	void hashpwInvalidSaltThrows() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> BCrypt.hashpw("pwd", "invalid-salt"));
	}

	/** hashpw 在盐值缺少轮数段时应抛出 IllegalArgumentException */
	@Test
	void hashpw_missingSaltRounds() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> BCrypt.hashpw("pwd", "$2a$$abcdefghijklmnopqr"));
	}

	/** hashpw 在轮数超过上限时应抛出 IllegalArgumentException */
	@Test
	void hashpw_roundsExceedMaximum() {
		String salt = BCrypt.gensalt(4).replace("$2a$04$", "$2a$31$");
		Assertions.assertThrows(IllegalArgumentException.class, () -> BCrypt.hashpw("pwd", salt));
	}

	/** hashpw 应拒绝 Base64 盐值中的无效字符，且不接受超出 bcrypt 字符表的字符 */
	@Test
	void hashpw_rejectsInvalidBase64SaltCharacters() {
		String validSalt = "$2a$04$......................";
		Assertions.assertTrue(BCrypt.checkpw("pwd", BCrypt.hashpw("pwd", validSalt)));

		Assertions.assertThrows(IllegalArgumentException.class,
				() -> BCrypt.hashpw("pwd", "$2a$04$!....................."));
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> BCrypt.hashpw("pwd", "$2a$04$.\u0100...................."));
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> BCrypt.hashpw("pwd", "$2a$04$..!..................."));
	}

	/** hashpw 应校验版本、轮数及盐值长度边界 */
	@Test
	void hashpw_validatesSaltBoundaries() {
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> BCrypt.hashpw("pwd", "$3a$04$......................"));
		Assertions.assertThrows(NumberFormatException.class,
				() -> BCrypt.hashpw("pwd", "$2a$xx$......................"));
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> BCrypt.hashpw("pwd", "$2a$03$......................"));
		Assertions.assertFalse(BCrypt.checkpw("pwd", "$2a$04$....................."));
	}

	/** checkpw 在哈希格式非法时应返回 false */
	@Test
	void checkpwReturnsFalseOnInvalidHash() {
		Assertions.assertFalse(BCrypt.checkpw("pwd", "not-a-bcrypt-hash"));
	}

	/** checkpw 在哈希格式不完整或非法时应返回 false */
	@Test
	void checkpw_invalidHash_returnsFalse() {
		Assertions.assertFalse(BCrypt.checkpw("pwd", "$2a$04$invalid-salt-value!!!"));
		Assertions.assertFalse(BCrypt.checkpw("pwd", "$2a$04$abcdefghijklmnopqr"));
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
	
}
