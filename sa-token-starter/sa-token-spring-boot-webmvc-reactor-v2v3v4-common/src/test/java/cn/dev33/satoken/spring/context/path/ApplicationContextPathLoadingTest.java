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
package cn.dev33.satoken.spring.context.path;

import cn.dev33.satoken.application.ApplicationInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;

/**
 * {@link ApplicationContextPathLoading} 上下文路径加载测试
 */
public class ApplicationContextPathLoadingTest {

    private String originalRoutePrefix;

    @BeforeEach
    public void setUp() {
        originalRoutePrefix = ApplicationInfo.routePrefix;
        ApplicationInfo.routePrefix = null;
    }

    @AfterEach
    public void tearDown() {
        ApplicationInfo.routePrefix = originalRoutePrefix;
    }

    /** 未配置 context-path 和 servlet.path 时不应该写入 routePrefix */
    @Test
    public void run_withoutPaths_shouldNotSetRoutePrefix() throws Exception {
        ApplicationContextPathLoading loading = new ApplicationContextPathLoading();
        loading.run(new DefaultApplicationArguments(new String[0]));

        Assertions.assertNull(ApplicationInfo.routePrefix);
    }

    /** context-path 应该自动补全前导斜杠并去掉尾斜杠 */
    @Test
    public void run_withContextPath_shouldNormalize() throws Exception {
        ApplicationContextPathLoading loading = new ApplicationContextPathLoading();
        loading.contextPath = "api/";
        loading.run(new DefaultApplicationArguments(new String[0]));

        Assertions.assertEquals("/api", ApplicationInfo.routePrefix);
    }

    /** servlet.path 应该与 context-path 拼接成最终 routePrefix */
    @Test
    public void run_withContextAndServletPath_shouldConcat() throws Exception {
        ApplicationContextPathLoading loading = new ApplicationContextPathLoading();
        loading.contextPath = "/api";
        loading.servletPath = "admin/";
        loading.run(new DefaultApplicationArguments(new String[0]));

        Assertions.assertEquals("/api/admin", ApplicationInfo.routePrefix);
    }

    /** 仅配置 servlet.path 时也应该能单独生效 */
    @Test
    public void run_withServletPathOnly_shouldNormalize() throws Exception {
        ApplicationContextPathLoading loading = new ApplicationContextPathLoading();
        loading.servletPath = "app";
        loading.run(new DefaultApplicationArguments(new String[0]));

        Assertions.assertEquals("/app", ApplicationInfo.routePrefix);
    }

    /** context-path 已带前导斜杠时不应该重复拼接 */
    @Test
    public void run_withLeadingSlashContextPath_shouldKeepNormalized() throws Exception {
        ApplicationContextPathLoading loading = new ApplicationContextPathLoading();
        loading.contextPath = "/api";
        loading.run(new DefaultApplicationArguments(new String[0]));

        Assertions.assertEquals("/api", ApplicationInfo.routePrefix);
    }

    /** servlet.path 已带前导斜杠时应该正确拼接 */
    @Test
    public void run_withLeadingSlashServletPath_shouldConcat() throws Exception {
        ApplicationContextPathLoading loading = new ApplicationContextPathLoading();
        loading.servletPath = "/admin";
        loading.run(new DefaultApplicationArguments(new String[0]));

        Assertions.assertEquals("/admin", ApplicationInfo.routePrefix);
    }

    /** servlet.path 带尾斜杠时应该去掉尾斜杠 */
    @Test
    public void run_withTrailingSlashServletPath_shouldStripSuffix() throws Exception {
        ApplicationContextPathLoading loading = new ApplicationContextPathLoading();
        loading.servletPath = "admin/";
        loading.run(new DefaultApplicationArguments(new String[0]));

        Assertions.assertEquals("/admin", ApplicationInfo.routePrefix);
    }

    /** 规范化后仅为根路径 "/" 时不应该写入 routePrefix */
    @Test
    public void run_withRootOnly_shouldNotSetRoutePrefix() throws Exception {
        ApplicationContextPathLoading loading = new ApplicationContextPathLoading();
        loading.contextPath = "/";
        loading.run(new DefaultApplicationArguments(new String[0]));

        Assertions.assertNull(ApplicationInfo.routePrefix);
    }

}
