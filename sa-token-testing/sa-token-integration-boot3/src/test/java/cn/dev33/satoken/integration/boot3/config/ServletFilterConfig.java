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
package cn.dev33.satoken.integration.boot3.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Boot 3 集成测试用 SaServletFilter 配置：按生产写法注册全局鉴权过滤器。
 */
@Configuration
public class ServletFilterConfig {

    /** 按生产写法注册 SaServletFilter：拦截 /filter/**，放行 /filter/open/**，登录校验 + 异常转 401 */
    @Bean
    public SaServletFilter saServletFilter() {
        return new SaServletFilter()
                .addInclude("/filter/**")
                .addExclude("/filter/open/**")
                .setBeforeAuth(r -> SaHolder.getResponse().setHeader("X-Before-Auth", "yes"))
                .setAuth(r -> SaRouter.match("/filter/**", r2 -> StpUtil.checkLogin()))
                .setError(e -> SaResult.error(e.getMessage()).setCode(401));
    }

}
