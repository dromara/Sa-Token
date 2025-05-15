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
package cn.dev33.satoken.sign.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API 参数签名校验：必须具有正确的参数签名才可以通过校验
 *
 * <p> 可标注在方法、类上（效果等同于标注在此类的所有方法上）
 *
 * @author click33
 * @since 1.41.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface SaCheckSign {

	/**
	 * 多实例下的 appid 值，用于区分不同的实例，如不填写则代表使用全局默认实例 <br/>
	 * 允许以 #{} 的形式指定为请求参数，如：#{appid}
	 *
	 * @return /
	 */
	String appid() default "";

	/**
	 * 指定参与签名的参数有哪些，如果不填写则默认为全部参数
	 *
	 * @return /
	 */
	String [] verifyParams() default {};

}
