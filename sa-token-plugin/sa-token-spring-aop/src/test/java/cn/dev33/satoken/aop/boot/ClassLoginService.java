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
package cn.dev33.satoken.aop.boot;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import org.springframework.stereotype.Service;

/**
 * 类上挂 {@code @SaCheckLogin} 的业务 Bean，用来确认 {@code @within} 和 {@code @SaIgnore}
 *
 * @author click33
 * @since 1.46.0
 */
@SaCheckLogin
@Service
public class ClassLoginService {

    /** 类上有登录校验时，调用任意方法都应该先过鉴权 */
    public String any() {
        return "class-ok";
    }

    /** 方法上的 {@code @SaIgnore} 应该跳过类上的登录校验 */
    @SaIgnore
    public String ignored() {
        return "ignored";
    }

}
