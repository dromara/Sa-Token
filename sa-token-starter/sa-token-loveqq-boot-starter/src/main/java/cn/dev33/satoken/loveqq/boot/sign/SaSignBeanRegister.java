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
package cn.dev33.satoken.loveqq.boot.sign;

import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.config.SaSignManyConfigWrapper;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Bean;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.ConfigurationProperties;
import com.kfyty.loveqq.framework.core.autoconfig.condition.annotation.ConditionalOnClass;

/**
 * 注册 Sa-Token API 参数签名所需要的 Bean
 *
 * @author click33
 * @since 1.43.0
 */
@Component
@ConditionalOnClass("cn.dev33.satoken.sign.SaSignManager")
public class SaSignBeanRegister {
    /**
     * 获取 API 参数签名配置对象
     *
     * @return 配置对象
     */
    @Bean
    @ConfigurationProperties("sa-token.sign")
    public SaSignConfig getSaSignConfig() {
        return new SaSignConfig();
    }

    /**
     * 获取 API 参数签名 Many 配置对象
     *
     * @return 配置对象
     */
    @Bean
    @ConfigurationProperties("sa-token")
    public SaSignManyConfigWrapper getSaSignManyConfigWrapper() {
        return new SaSignManyConfigWrapper();
    }
}
