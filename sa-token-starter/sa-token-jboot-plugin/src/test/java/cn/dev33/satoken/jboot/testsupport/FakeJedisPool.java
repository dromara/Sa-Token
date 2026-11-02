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
package cn.dev33.satoken.jboot.testsupport;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 单测用的假 JedisPool：不连真 Redis，getResource 返回指定 Jedis 或按指定异常炸掉。
 */
public class FakeJedisPool extends JedisPool {

    private final Jedis jedis;
    private final RuntimeException fail;

    /** 每次 getResource 都把这份 Jedis 交出去 */
    public FakeJedisPool(Jedis jedis) {
        super("127.0.0.1", 1);
        this.jedis = jedis;
        this.fail = null;
    }

    /** 每次 getResource 都抛这个异常，用来打连接失败分支 */
    public FakeJedisPool(RuntimeException fail) {
        super("127.0.0.1", 1);
        this.jedis = null;
        this.fail = fail;
    }

    @Override
    public Jedis getResource() {
        if (fail != null) {
            throw fail;
        }
        return jedis;
    }
}
