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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaPathPatternParserUtil} PathPattern 匹配测试
 */
public class SaPathPatternParserUtilTest {

    /** 精确路径应该能匹配成功 */
    @Test
    public void match_exactPath_shouldMatch() {
        Assertions.assertTrue(SaPathPatternParserUtil.match("/user/get", "/user/get"));
    }

    /** 不匹配的路径应该返回 false */
    @Test
    public void match_differentPath_shouldNotMatch() {
        Assertions.assertFalse(SaPathPatternParserUtil.match("/user/get", "/user/list"));
    }

}
