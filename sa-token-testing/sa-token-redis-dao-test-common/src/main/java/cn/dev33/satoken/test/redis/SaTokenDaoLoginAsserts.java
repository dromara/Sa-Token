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
package com.pj.test.redis;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.serializer.SaSerializerTemplate;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJdkUseBase64;
import cn.dev33.satoken.stp.StpUtil;
import org.junit.jupiter.api.Assertions;

/**
 * 用 {@link StpUtil#login(Object)} 验证容器里的 Dao 真能落到 Redis。
 * 测完把 {@link SaManager} 里的 Dao 换回去，避免污染同模块其它用例。
 *
 * @author click33
 * @since 1.46.0
 */
public final class SaTokenDaoLoginAsserts {

	private SaTokenDaoLoginAsserts() {
	}

	/** 登录后，token -> loginId 应该写进传入的 Dao */
	public static void assertLoginWritesTokenToDao(SaTokenDao dao) {
		SaTokenDao previousDao = SaManager.getSaTokenDao();
		SaSerializerTemplate previousSerializer = SaManager.getSaSerializerTemplate();
		try {
			// 插件测试不带 jackson，登录写 Session 改走 JDK 序列化，避免踩默认 JSON 空实现
			SaManager.setSaSerializerTemplate(new SaSerializerTemplateForJdkUseBase64());
			SaManager.setSaTokenDao(dao);
			SaTokenContextMockUtil.setMockContext(() -> {
				StpUtil.login(10001);
				String tokenValue = StpUtil.getTokenValue();
				Assertions.assertNotNull(tokenValue);
				String key = StpUtil.getStpLogic().splicingKeyTokenValue(tokenValue);
				Assertions.assertEquals("10001", dao.get(key));
			});
		} finally {
			SaManager.setSaTokenDao(previousDao);
			SaManager.setSaSerializerTemplate(previousSerializer);
		}
	}

}
