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
package cn.dev33.satoken.reactor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * Reactor Boot 3 Starter 应通过 AutoConfiguration.imports 声明上下文注册器
 */
public class AutoConfigurationImportsTest {

    private static final String IMPORTS_PATH =
            "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports";
    private static final String REGISTER_CLASS = "cn.dev33.satoken.reactor.spring.SaTokenContextRegister";

    /** classpath 上应该能找到 AutoConfiguration.imports，并且声明了 Reactor 版 SaTokenContextRegister */
    @Test
    public void imports_shouldDeclareSaTokenContextRegister() throws Exception {
        String all = readAllImports();
        Assertions.assertTrue(all.contains(REGISTER_CLASS), all);
        Assertions.assertNotNull(Class.forName(REGISTER_CLASS));
    }

    /** 把 classpath 上所有 AutoConfiguration.imports 拼起来 */
    private String readAllImports() throws Exception {
        StringBuilder all = new StringBuilder();
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(IMPORTS_PATH);
        while (urls.hasMoreElements()) {
            try (InputStream in = urls.nextElement().openStream()) {
                all.append(new String(in.readAllBytes(), StandardCharsets.UTF_8)).append('\n');
            }
        }
        return all.toString();
    }

}
