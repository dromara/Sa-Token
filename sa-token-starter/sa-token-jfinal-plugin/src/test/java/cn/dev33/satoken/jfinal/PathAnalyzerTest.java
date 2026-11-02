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
package cn.dev33.satoken.jfinal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link PathAnalyzer} 路由表达式匹配测试
 */
public class PathAnalyzerTest {

    /** 精确路径应该能匹配上，多一段就不该匹配 */
    @Test
    public void matches_exactPath() {
        PathAnalyzer analyzer = PathAnalyzer.get("/user/info");
        Assertions.assertTrue(analyzer.matches("/user/info"));
        Assertions.assertFalse(analyzer.matches("/user/info/extra"));
        Assertions.assertFalse(analyzer.matches("/user"));
    }

    /** 同一个表达式并发 get 时，应该都拿到缓存里的同一个对象 */
    @Test
    public void get_concurrentSameExpr_shouldShareInstance() throws Exception {
        final String expr = "/concurrent/" + java.util.UUID.randomUUID();
        final PathAnalyzer[] results = new PathAnalyzer[16];
        Thread[] threads = new Thread[16];
        java.util.concurrent.CountDownLatch start = new java.util.concurrent.CountDownLatch(1);
        for (int i = 0; i < threads.length; i++) {
            final int idx = i;
            threads[i] = new Thread(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                results[idx] = PathAnalyzer.get(expr);
            });
            threads[i].start();
        }
        start.countDown();
        for (Thread thread : threads) {
            thread.join();
        }
        for (PathAnalyzer analyzer : results) {
            Assertions.assertSame(results[0], analyzer);
        }
    }

    /** 同一个表达式再 get 一次，应该拿到缓存里的同一个对象 */
    @Test
    public void get_shouldReuseCache() {
        PathAnalyzer first = PathAnalyzer.get("/cached/path");
        PathAnalyzer second = PathAnalyzer.get("/cached/path");
        Assertions.assertSame(first, second);
    }

    /** 没写前导斜杠时，应该自动补上再匹配 */
    @Test
    public void matches_withoutLeadingSlash() {
        PathAnalyzer analyzer = PathAnalyzer.get("plain");
        Assertions.assertTrue(analyzer.matches("/plain"));
    }

    /** 单星号只吃一段路径，吃不了多层 */
    @Test
    public void matches_singleStar() {
        PathAnalyzer analyzer = PathAnalyzer.get("/user/*");
        Assertions.assertTrue(analyzer.matches("/user/1"));
        Assertions.assertFalse(analyzer.matches("/user/1/profile"));
    }

    /** 双星号应该能匹配多层路径 */
    @Test
    public void matches_doubleStar() {
        PathAnalyzer analyzer = PathAnalyzer.get("/api/**");
        Assertions.assertTrue(analyzer.matches("/api/a"));
        Assertions.assertTrue(analyzer.matches("/api/a/b/c"));
    }

    /** {id} 只吃不含斜杠的一段 */
    @Test
    public void matches_pathVariable() {
        PathAnalyzer analyzer = PathAnalyzer.get("/user/{id}");
        Assertions.assertTrue(analyzer.matches("/user/1001"));
        Assertions.assertFalse(analyzer.matches("/user/1001/extra"));
    }

    /** {id_} 这种写法应该能吃到带斜杠的剩余路径 */
    @Test
    public void matches_pathVariableWithUnderscore() {
        PathAnalyzer analyzer = PathAnalyzer.get("/file/{path_}");
        Assertions.assertTrue(analyzer.matches("/file/a/b/c.txt"));
    }

    /** 点号应该按字面量匹配，不当正则通配 */
    @Test
    public void matches_literalDot() {
        PathAnalyzer analyzer = PathAnalyzer.get("/static/app.js");
        Assertions.assertTrue(analyzer.matches("/static/app.js"));
        Assertions.assertFalse(analyzer.matches("/static/appXjs"));
    }

    /** 美元符也应该按字面量匹配 */
    @Test
    public void matches_literalDollar() {
        PathAnalyzer analyzer = PathAnalyzer.get("/price/$id");
        Assertions.assertTrue(analyzer.matches("/price/$id"));
    }

    /** matcher 应该能拿到正则 Matcher，并能 find */
    @Test
    public void matcher_shouldFind() {
        PathAnalyzer analyzer = PathAnalyzer.get("/user/{id}");
        Assertions.assertTrue(analyzer.matcher("/user/9").find());
        Assertions.assertFalse(analyzer.matcher("/order/9").find());
    }
}
