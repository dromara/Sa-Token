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
package cn.dev33.satoken.jboot;

import cn.dev33.satoken.exception.SaTokenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * {@link SaTokenPathFilter} 路由配置与钩子函数测试
 */
public class SaTokenPathFilterTest {

    /** 链式配置 include/exclude 和钩子函数应该能正常读写 */
    @Test
    public void configure_includeExcludeAndHooks() {
        SaTokenPathFilter filter = new SaTokenPathFilter()
                .addInclude("/**")
                .setIncludeList(Arrays.asList("/api/**"))
                .addExclude("/favicon.ico")
                .setExcludeList(Arrays.asList("/health"))
                .setAuth(r -> {})
                .setBeforeAuth(r -> {})
                .setError(e -> "err:" + e.getMessage());

        Assertions.assertEquals("/api/**", filter.includeList.get(0));
        Assertions.assertEquals("/health", filter.excludeList.get(0));
        Assertions.assertEquals("err:msg", filter.error.run(new SaTokenException("msg")));
    }

    /** addInclude / addExclude 应该是往列表里追加，不是覆盖 */
    @Test
    public void addIncludeAndExclude_shouldAppend() {
        SaTokenPathFilter filter = new SaTokenPathFilter()
                .addInclude("/a", "/b")
                .addExclude("/x", "/y");
        Assertions.assertEquals(Arrays.asList("/a", "/b"), filter.includeList);
        Assertions.assertEquals(Arrays.asList("/x", "/y"), filter.excludeList);
    }

    /** 默认 error 策略遇到异常应该再包一层 SaTokenException 抛出 */
    @Test
    public void defaultErrorStrategy_rethrow() {
        SaTokenPathFilter filter = new SaTokenPathFilter();
        Assertions.assertThrows(SaTokenException.class, () -> filter.error.run(new RuntimeException("x")));
    }

    /** 默认 auth / beforeAuth 应该是空操作，调了也不该炸 */
    @Test
    public void defaultAuthHooks_shouldBeNoop() {
        SaTokenPathFilter filter = new SaTokenPathFilter();
        filter.beforeAuth.run(null);
        filter.auth.run(null);
    }
}
