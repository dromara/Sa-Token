/*
 * Copyright 2020-2099 sa-token.cc
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
package cn.dev33.satoken.springboot;

import cn.dev33.satoken.spring.pathmatch.SaPathMatcherHolder;
import cn.dev33.satoken.spring.pathmatch.SaPathPatternParserUtil;
import cn.dev33.satoken.spring.pathmatch.SaPatternsRequestConditionHolder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;

/**
 * SaPathMatcher 路由匹配测试
 * 
 * @author click33  
 *
 */
public class SaPathMatcherTest {

	// 开始 
	@BeforeAll
    public static void beforeClass() {
    	
    }

	// 结束 
    @AfterAll
    public static void afterClass() {
    	
    }

    // 测试，SaPathMatcherHolder
    @Test
    public void testSaPathMatcherHolder() {
        Assertions.assertTrue(SaPathMatcherHolder.getPathMatcher().match("/user/get", "/user/get"));
        Assertions.assertTrue(SaPathMatcherHolder.getPathMatcher().match("/user/*", "/user/get"));
        Assertions.assertTrue(SaPathMatcherHolder.getPathMatcher().match("/user/**", "/user/get/list"));
        Assertions.assertTrue(SaPathMatcherHolder.getPathMatcher().match("/user/**/page", "/user/get/list/page"));
        Assertions.assertTrue(SaPathMatcherHolder.getPathMatcher().match("/user/get/{id}", "/user/get/123"));
        Assertions.assertTrue(SaPathMatcherHolder.getPathMatcher().match("/user/get/{id}/page", "/user/get/123/page"));
        Assertions.assertTrue(SaPathMatcherHolder.getPathMatcher().match("/*.js", "/sa.js"));
        Assertions.assertTrue(SaPathMatcherHolder.getPathMatcher().match("/user/**/*.js", "/user/sa.js"));

        // SaPathMatcherHolder 无法匹配斜杠后缀
        Assertions.assertFalse(SaPathMatcherHolder.getPathMatcher().match("/user/get", "/user/get/"));
    }

    // 测试，SaPatternsRequestConditionHolder
    @Test
    public void testSaPatternsRequestConditionHolder() {

        Assertions.assertTrue(SaPatternsRequestConditionHolder.match("/user/get", "/user/get"));
        Assertions.assertTrue(SaPatternsRequestConditionHolder.match("/user/*", "/user/get"));
        Assertions.assertTrue(SaPatternsRequestConditionHolder.match("/user/**", "/user/get/list"));
        Assertions.assertTrue(SaPatternsRequestConditionHolder.match("/user/**/page", "/user/get/list/page"));
        Assertions.assertTrue(SaPatternsRequestConditionHolder.match("/user/get/{id}", "/user/get/123"));
        Assertions.assertTrue(SaPatternsRequestConditionHolder.match("/user/get/{id}/page", "/user/get/123/page"));
        Assertions.assertTrue(SaPatternsRequestConditionHolder.match("/*.js", "/sa.js"));
        Assertions.assertTrue(SaPatternsRequestConditionHolder.match("/user/**/*.js", "/user/sa.js"));

        // SaPatternsRequestConditionHolder 可匹配斜杠后缀
        Assertions.assertTrue(SaPatternsRequestConditionHolder.match("/user/get", "/user/get/"));
    }

    // 测试，testSaPathPatternParserUtil
    @Test
    public void testSaPathPatternParserUtil() {

        Assertions.assertTrue(SaPathPatternParserUtil.match("/user/get", "/user/get"));
        Assertions.assertTrue(SaPathPatternParserUtil.match("/user/*", "/user/get"));
        Assertions.assertTrue(SaPathPatternParserUtil.match("/user/**", "/user/get/list"));
        // PathPatternParser 不允许 ** 后面还有内容
        // Assertions.assertTrue(SaPathPatternParserUtil.match("/user/**/page", "/user/get/list/page"));
        Assertions.assertTrue(SaPathPatternParserUtil.match("/user/get/{id}", "/user/get/123"));
        Assertions.assertTrue(SaPathPatternParserUtil.match("/user/get/{id}/page", "/user/get/123/page"));
        Assertions.assertTrue(SaPathPatternParserUtil.match("/*.js", "/sa.js"));
        // Assertions.assertTrue(SaPathPatternParserUtil.match("/user/**/*.js", "/user/sa.js"));

        // SaPathPatternParserUtil
        //      在 springboot2.x 版本下 可匹配斜杠后缀
        //      在 springboot3.x 版本下 不可匹配斜杠后缀
        if(SpringBootVersion.getVersion().startsWith("2.")) {
            Assertions.assertTrue(SaPathPatternParserUtil.match("/user/get", "/user/get/"));
        }
        if(SpringBootVersion.getVersion().startsWith("3.")) {
            Assertions.assertFalse(SaPathPatternParserUtil.match("/user/get", "/user/get/"));
        }
    }












}
