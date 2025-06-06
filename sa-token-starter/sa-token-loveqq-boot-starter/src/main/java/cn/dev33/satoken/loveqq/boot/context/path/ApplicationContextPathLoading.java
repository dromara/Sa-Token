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
package cn.dev33.satoken.loveqq.boot.context.path;

import cn.dev33.satoken.application.ApplicationInfo;
import cn.dev33.satoken.util.SaFoxUtil;
import com.kfyty.loveqq.framework.core.autoconfig.CommandLineRunner;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Value;

/**
 * 应用上下文路径加载器
 *
 * @author click33
 * @since 1.37.0
 */
public class ApplicationContextPathLoading implements CommandLineRunner {
    @Value("${k.mvc.tomcat.contextPath:}")
    private String contextPath;

    @Override
    public void run(String... args) throws Exception {

        String routePrefix = "";

        if (SaFoxUtil.isNotEmpty(contextPath)) {
            if (!contextPath.startsWith("/")) {
                contextPath = "/" + contextPath;
            }
            if (contextPath.endsWith("/")) {
                contextPath = contextPath.substring(0, contextPath.length() - 1);
            }
            routePrefix += contextPath;
        }

        if (SaFoxUtil.isNotEmpty(routePrefix) && !routePrefix.equals("/")) {
            ApplicationInfo.routePrefix = routePrefix;
        }
    }
}