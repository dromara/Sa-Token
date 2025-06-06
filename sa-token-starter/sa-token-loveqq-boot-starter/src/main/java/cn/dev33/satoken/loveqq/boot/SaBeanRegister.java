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
package cn.dev33.satoken.loveqq.boot;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.loveqq.boot.context.path.ApplicationContextPathLoading;
import cn.dev33.satoken.loveqq.boot.filter.SaFirewallCheckFilter;
import cn.dev33.satoken.loveqq.boot.filter.SaTokenContextFilter;
import cn.dev33.satoken.loveqq.boot.filter.SaTokenCorsFilter;
import cn.dev33.satoken.loveqq.boot.support.SaPathMatcherHolder;
import cn.dev33.satoken.strategy.SaStrategy;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Bean;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.ConfigurationProperties;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Import;

/**
 * 注册Sa-Token所需要的Bean
 * <p> Bean 的注册与注入应该分开在两个文件中，否则在某些场景下会造成循环依赖
 *
 * @author click33
 */
@Component
@Import(config = {
        SaFirewallCheckFilter.class,
        SaTokenContextFilter.class,
        SaTokenCorsFilter.class
})
public class SaBeanRegister {

    public SaBeanRegister() {
        // 重写路由匹配算法
        SaStrategy.instance.routeMatcher = SaPathMatcherHolder::match;
    }

    /**
     * 获取配置Bean
     *
     * @return 配置对象
     */
    @Bean
    @ConfigurationProperties("sa-token")
    public SaTokenConfig getSaTokenConfig() {
        return new SaTokenConfig();
    }

    /**
     * 应用上下文路径加载器
     *
     * @return /
     */
    @Bean
    public ApplicationContextPathLoading getApplicationContextPathLoading() {
        return new ApplicationContextPathLoading();
    }
}
