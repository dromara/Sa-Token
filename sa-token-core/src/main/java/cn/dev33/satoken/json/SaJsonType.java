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
package cn.dev33.satoken.json;

/**
 * 标记接口：表示此类允许参与 Sa-Token JSON 多态序列化 / 反序列化（DefaultTyping 白名单）。
 * <p>
 * 框架内部需要持久化到 Redis 等的 Model，应实现此接口；
 * 业务 Model 若需存入 Session 等，也应实现此接口，或通过 {@link cn.dev33.satoken.strategy.SaJsonStrategy} 注册。
 * </p>
 *
 * @author click33
 * @since 1.46.0
 */
public interface SaJsonType {
}
