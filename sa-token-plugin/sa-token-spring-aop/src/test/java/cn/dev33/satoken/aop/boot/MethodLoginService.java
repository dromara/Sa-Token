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
import org.springframework.stereotype.Service;

/**
 * 方法上挂 {@code @SaCheckLogin} 的业务 Bean，用来确认 AOP 切到了方法
 *
 * @author click33
 * @since 1.46.0
 */
@Service
public class MethodLoginService {

    /** 未登录必须拦，登录后应该放行 */
    @SaCheckLogin
    public String needLogin() {
        return "ok";
    }

}
