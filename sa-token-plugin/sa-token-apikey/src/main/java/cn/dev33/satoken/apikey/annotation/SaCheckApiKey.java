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
package cn.dev33.satoken.apikey.annotation;

import cn.dev33.satoken.annotation.SaMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API Key 校验：指定请求中必须包含有效的 ApiKey ，并且包含指定的 scope
 *
 * <p> 可标注在方法、类上（效果等同于标注在此类的所有方法上）
 *
 * @author click33
 * @since 1.42.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface SaCheckApiKey {

	/**
	 * 指定 API key 必须包含的权限 [ 数组 ]
	 *
	 * @return /
	 */
	String [] scope() default {};

	/**
	 * 验证模式：AND | OR，默认AND
	 *
	 * @return /
	 */
	SaMode mode() default SaMode.AND;

}
