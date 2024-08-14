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
package cn.dev33.satoken.oauth2.function;


import java.util.List;
import java.util.function.BiFunction;

/**
 * 函数式接口：OAuth-Server端 确认授权时返回的View
 *
 * <p>  参数：无  </p>
 * <p>  返回：view 视图  </p>
 *
 * @author click33
 * @since 1.39.0
 */
@FunctionalInterface
public interface SaOAuth2ConfirmViewFunction extends BiFunction<String, List<String>, Object> {

}