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

import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.config.SaSignManyConfigWrapper;
import cn.dev33.satoken.sign.template.SaSignTemplate;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Autowired;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.kfyty.loveqq.framework.core.autoconfig.condition.annotation.ConditionalOnClass;

/**
 * 注入 Sa-Token API 参数签名 所需要的 Bean
 *
 * @author click33
 * @since 1.43.0
 */
@Component
@ConditionalOnClass("cn.dev33.satoken.sign.SaSignManager")
public class SaSignBeanInject {
    /**
     * 注入 API 参数签名配置对象
     *
     * @param saSignConfig 配置对象
     */
    @Autowired(required = false)
    public void setSignConfig(SaSignConfig saSignConfig) {
        SaSignManager.setConfig(saSignConfig);
    }

    /**
     * 注入 API 参数签名配置对象
     *
     * @param signManyConfigWrapper 配置对象
     */
    @Autowired(required = false)
    public void setSignManyConfig(SaSignManyConfigWrapper signManyConfigWrapper) {
        SaSignManager.setSignMany(signManyConfigWrapper.getSignMany());
    }

    /**
     * 注入自定义的 参数签名 模版方法 Bean
     *
     * @param saSignTemplate 参数签名 Bean
     */
    @Autowired(required = false)
    public void setSaSignTemplate(SaSignTemplate saSignTemplate) {
        SaSignManager.setSaSignTemplate(saSignTemplate);
    }
}
