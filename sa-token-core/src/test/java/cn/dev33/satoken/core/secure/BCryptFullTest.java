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

/**
 * BCrypt 剩余路径覆盖
 */
public class BCryptFullTest {

	/** hashpw 无显式盐值时应自动生成并可通过 checkpw 校验 */
	@Test
	void hashpwWithoutExplicitSalt() {
		String hashed = BCrypt.hashpw("plain-text");
		Assertions.assertTrue(hashed.startsWith("$2a$"));
		Assertions.assertTrue(BCrypt.checkpw("plain-text", hashed));
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

	/** hashpw 在非法次版本 $2z$ 时应抛出 IllegalArgumentException */
	@Test
	void hashpw_invalidMinorRevision() {
		String salt = BCrypt.gensalt(4).replace("$2a$", "$2z$");
		Assertions.assertThrows(IllegalArgumentException.class, () -> BCrypt.hashpw("pwd", salt));
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

	/** gensalt 轮数超过上限时应抛出 IllegalArgumentException */
	@Test
	void gensalt_logRoundsExceedMaximum() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> BCrypt.gensalt(31));
	}

	/** checkpw 在哈希格式不完整或非法时应返回 false */
	@Test
	void checkpw_invalidHash_returnsFalse() {
		Assertions.assertFalse(BCrypt.checkpw("pwd", "$2a$04$invalid-salt-value!!!"));
		Assertions.assertFalse(BCrypt.checkpw("pwd", "$2a$04$abcdefghijklmnopqr"));
	}

}
