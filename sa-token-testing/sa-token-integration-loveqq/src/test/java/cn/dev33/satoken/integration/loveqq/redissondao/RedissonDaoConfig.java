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

import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Bean;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Configuration;

/**
 * mock 一份 RedissonClient，不连真 Redis。
 */
@Configuration
public class RedissonDaoConfig {

	/** 给 ConditionalOnBean(RedissonClient) 一条能命中的生产路径 */
	@Bean
	public RedissonClient redissonClient() {
		return Mockito.mock(RedissonClient.class);
	}

}
