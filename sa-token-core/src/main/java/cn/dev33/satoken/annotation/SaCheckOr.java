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
package cn.dev33.satoken.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 批量注解鉴权：只要满足其中一个注解即可通过验证
 *
 * <p> 可标注在方法、类上（效果等同于标注在此类的所有方法上）
 *
 * @author click33
 * @since 1.35.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface SaCheckOr {

    /**
     * 设定 @SaCheckLogin，参考 {@link SaCheckLogin}
     *
     * @return /
     */
    SaCheckLogin[] login() default {};

    /**
     * 设定 @SaCheckPermission，参考 {@link SaCheckPermission}
     *
     * @return /
     */
    SaCheckPermission[] permission() default {};

    /**
     * 设定 @SaCheckRole，参考 {@link SaCheckRole}
     *
     * @return /
     */
    SaCheckRole[] role() default {};

    /**
     * 设定 @SaCheckSafe，参考 {@link SaCheckSafe}
     *
     * @return /
     */
    SaCheckSafe[] safe() default {};

    /**
     * 设定 @SaCheckHttpBasic，参考 {@link SaCheckHttpBasic}
     *
     * @return /
     */
    SaCheckHttpBasic[] httpBasic() default {};

    /**
     * 设定 @SaCheckBasic，参考 {@link SaCheckHttpDigest}
     *
     * @return /
     */
    SaCheckHttpDigest[] httpDigest() default {};

    /**
     * 设定 @SaCheckDisable，参考 {@link SaCheckDisable}
     *
     * @return /
     */
    SaCheckDisable[] disable() default {};

}
