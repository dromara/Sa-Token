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

import com.github.fppt.jedismock.RedisServer;

import java.io.IOException;

/**
 * 给 Redis Dao 单测起内嵌 Redis 用的小工具
 *
 * @author click33
 * @since 1.46.0
 */
public final class JedisMockRedisSupport {

	private JedisMockRedisSupport() {
	}

	/** 启动一个无密码的内嵌 Redis，端口随机 */
	public static RedisServer startServer() throws IOException {
		RedisServer redisServer = RedisServer.newRedisServer();
		redisServer.start();
		return redisServer;
	}

	/** 关掉内嵌 Redis */
	public static void stopServer(RedisServer redisServer) throws IOException {
		if (redisServer != null) {
			redisServer.stop();
		}
	}

}
