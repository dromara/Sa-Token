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
package cn.dev33.satoken.integration.loveqq.redissondao;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisson;
import com.kfyty.loveqq.framework.boot.K;
import com.kfyty.loveqq.framework.core.autoconfig.ApplicationContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * 容器里有 RedissonClient 时应该注册出 SaTokenDaoForRedisson
 */
public class RedissonDaoInjectTest {

	private static ApplicationContext ctx;

	/** 起带 mock RedissonClient 的应用 */
	@BeforeAll
	public static void start() {
		ctx = K.start(RedissonDaoApp.class);
	}

	/** 测完把容器关掉 */
	@AfterAll
	public static void stop() throws Exception {
		if (ctx != null) {
			ctx.close();
		}
	}

	/** 有 RedissonClient Bean 时应该装配出 Redisson Dao */
	@Test
	public void saTokenDao_shouldBeRedissonImpl() {
		SaTokenDao dao = ctx.getBean(SaTokenDao.class);
		Assertions.assertTrue(dao instanceof SaTokenDaoForRedisson);
	}

}
