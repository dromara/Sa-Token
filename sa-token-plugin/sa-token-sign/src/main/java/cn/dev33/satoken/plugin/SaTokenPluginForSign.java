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
package cn.dev33.satoken.plugin;

import cn.dev33.satoken.sign.annotation.handle.SaCheckSignHandler;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;

/**
 * SaToken 插件安装：API 参数签名 组件
 *
 * @author click33
 * @since 1.43.0
 */
public class SaTokenPluginForSign implements SaTokenPlugin {

    @Override
    public void install() {
        // 安装 API 参数签名 鉴权注解
        SaAnnotationStrategy.instance.registerAnnotationHandler(new SaCheckSignHandler());
    }

}