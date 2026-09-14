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

import org.springframework.stereotype.Service;

/**
 * 挂了自定义 {@link ExtraCheck} 的业务 Bean
 *
 * @author click33
 * @since 1.46.0
 */
@Service
public class ExtraCheckService {

    /** 命中自定义注解时应该被 handler 拦住，不应该执行到方法体 */
    @ExtraCheck
    public String hit() {
        return "should-not-run";
    }

}
