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
package cn.dev33.satoken.integration.reactor.boot4.diff;

import cn.dev33.satoken.spring.pathmatch.SaPathMatcherHolder;
import cn.dev33.satoken.spring.pathmatch.SaPathPatternParserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;

/**
 * Boot 4 路由匹配差异测试：验证 Boot 4 下 PathPattern 语义与 Boot 2 的不同之处。
 */
public class PathPatternIntegrationTest {

    /** 当前测试必须在 Boot 4 环境运行 */
    @Test
    public void shouldRunOnBoot4() {
        Assertions.assertTrue(SpringBootVersion.getVersion().startsWith("4."),
                "本模块只验证 Boot 4 差异，公共逻辑见 integration-reactor-boot2");
    }

    /** Boot 4 下 PathPatternParser 不应匹配尾斜杠路径 */
    @Test
    public void pathPatternParser_shouldNotMatchTrailingSlash_onBoot4() {
        Assertions.assertFalse(SaPathPatternParserUtil.match("/user/get", "/user/get/"));
    }

    /** AntPathMatcher 在 Boot 4 下仍不应匹配尾斜杠 */
    @Test
    public void antPathMatcher_shouldNotMatchTrailingSlash_onBoot4() {
        Assertions.assertFalse(SaPathMatcherHolder.getPathMatcher().match("/user/get", "/user/get/"));
    }

}
