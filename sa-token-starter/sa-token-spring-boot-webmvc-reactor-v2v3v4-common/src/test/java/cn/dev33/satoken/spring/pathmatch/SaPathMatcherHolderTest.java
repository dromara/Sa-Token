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
package cn.dev33.satoken.spring.pathmatch;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * {@link SaPathMatcherHolder} 路由匹配器持有测试
 */
public class SaPathMatcherHolderTest {

    @AfterEach
    public void tearDown() {
        SaPathMatcherHolder.pathMatcher = null;
    }

    /** 未注入时 getPathMatcher 应该懒加载 AntPathMatcher */
    @Test
    public void getPathMatcher_whenUnset_shouldCreateAntPathMatcher() {
        PathMatcher matcher = SaPathMatcherHolder.getPathMatcher();

        Assertions.assertNotNull(matcher);
        Assertions.assertInstanceOf(AntPathMatcher.class, matcher);
        Assertions.assertTrue(matcher.match("/user/**", "/user/list"));
    }

    /** setPathMatcher 后 getPathMatcher 应该返回注入实例 */
    @Test
    public void setPathMatcher_shouldUseInjectedInstance() {
        PathMatcher custom = new AntPathMatcher();
        SaPathMatcherHolder.setPathMatcher(custom);

        Assertions.assertSame(custom, SaPathMatcherHolder.getPathMatcher());
    }

}
