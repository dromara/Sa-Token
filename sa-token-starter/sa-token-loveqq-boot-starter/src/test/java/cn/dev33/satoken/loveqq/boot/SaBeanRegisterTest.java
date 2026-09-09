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
package cn.dev33.satoken.loveqq.boot;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisson;
import cn.dev33.satoken.loveqq.boot.context.path.ApplicationContextPathLoading;
import cn.dev33.satoken.loveqq.boot.model.LoveqqSaRequest;
import cn.dev33.satoken.loveqq.boot.testsupport.LoveqqTestHelper;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerRequest;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.redisson.api.RedissonClient;

/**
 * {@link SaBeanRegister} 策略重写与默认 Bean 工厂测试
 */
@SaTokenTest
public class SaBeanRegisterTest {

	/** 构造时应该把路由匹配和 Request/Response/Storage 创建策略改成 LoveQQ 实现 */
	@Test
	public void constructor_shouldOverrideSaStrategy() {
		new SaBeanRegister();
		Assertions.assertTrue(SaStrategy.instance.routeMatcher.apply("/user/**", "/user/1"));
		Assertions.assertFalse(SaStrategy.instance.routeMatcher.apply("/user/**", "/admin/1"));

		TestServerRequest request = LoveqqTestHelper.newGetRequest("/x");
		Assertions.assertTrue(SaStrategy.instance.createSaRequest.apply(request) instanceof LoveqqSaRequest);
		Assertions.assertTrue(SaStrategy.instance.createSaResponse.apply(LoveqqTestHelper.newResponse())
				instanceof cn.dev33.satoken.loveqq.boot.model.LoveqqSaResponse);
		Assertions.assertTrue(SaStrategy.instance.createSaStorage.apply(request)
				instanceof cn.dev33.satoken.loveqq.boot.model.LoveqqSaStorage);
	}

	/** getSaTokenConfig 应该 new 一份默认配置 */
	@Test
	public void getSaTokenConfig_returnDefault() {
		SaTokenConfig config = new SaBeanRegister().getSaTokenConfig();
		Assertions.assertNotNull(config);
	}

	/** 有 RedissonClient 时应该组装出 Redisson Dao，但不连真 Redis */
	@Test
	public void saTokenDao_withMockRedisson() {
		RedissonClient redisson = Mockito.mock(RedissonClient.class);
		SaTokenDao dao = new SaBeanRegister().saTokenDaoForRedisson(redisson);
		Assertions.assertTrue(dao instanceof SaTokenDaoForRedisson);
	}

	/** 上下文路径加载器工厂应该能 new 出来 */
	@Test
	public void getApplicationContextPathLoading_notNull() {
		ApplicationContextPathLoading loading = new SaBeanRegister().getApplicationContextPathLoading();
		Assertions.assertNotNull(loading);
	}

}
