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
package cn.dev33.satoken.integration.boot2.diff;

import cn.dev33.satoken.spring.pathmatch.SaPathMatcherHolder;
import cn.dev33.satoken.spring.pathmatch.SaPathPatternParserUtil;
import cn.dev33.satoken.spring.pathmatch.SaPatternsRequestConditionHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;

/**
 * Boot 2 路由匹配差异测试：验证 {@link SaPatternsRequestConditionHolder} 与 Boot 2 下的 PathPattern 行为。
 */
public class PathPatternIntegrationTest {

    /** Boot 2 下 SaPatternsRequestConditionHolder 应该能匹配尾斜杠路径 */
    @Test
    public void patternsRequestCondition_shouldMatchTrailingSlash_onBoot2() {
        Assertions.assertTrue(SpringBootVersion.getVersion().startsWith("2."));
        Assertions.assertTrue(SaPatternsRequestConditionHolder.match("/user/get", "/user/get/"));
    }

    /** Boot 2 下 PathPatternParser 应该能匹配尾斜杠路径 */
    @Test
    public void pathPatternParser_shouldMatchTrailingSlash_onBoot2() {
        Assertions.assertTrue(SpringBootVersion.getVersion().startsWith("2."));
        Assertions.assertTrue(SaPathPatternParserUtil.match("/user/get", "/user/get/"));
    }

    /** AntPathMatcher 基本匹配能力在 Boot 2 下应该正常 */
    @Test
    public void antPathMatcher_shouldWork_onBoot2() {
        Assertions.assertTrue(SaPathMatcherHolder.getPathMatcher().match("/user/**", "/user/get/list"));
        Assertions.assertFalse(SaPathMatcherHolder.getPathMatcher().match("/user/get", "/user/get/"));
    }

}
