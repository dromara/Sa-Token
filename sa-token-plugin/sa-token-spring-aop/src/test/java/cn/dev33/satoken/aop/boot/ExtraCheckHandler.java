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

import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.exception.SaTokenException;
import org.springframework.stereotype.Component;

import java.lang.reflect.AnnotatedElement;

/**
 * 容器里注册的自定义注解处理器，切点表达式应该带上 {@link ExtraCheck}
 *
 * @author click33
 * @since 1.46.0
 */
@Component
public class ExtraCheckHandler implements SaAnnotationHandlerInterface<ExtraCheck> {

    /** 告诉策略表这个 handler 对应哪个注解 */
    @Override
    public Class<ExtraCheck> getHandlerAnnotationClass() {
        return ExtraCheck.class;
    }

    /** 命中时直接抛，方便测例看见它真的跑了 */
    @Override
    public void checkMethod(ExtraCheck at, AnnotatedElement element) {
        throw new SaTokenException("extra-check");
    }

}
