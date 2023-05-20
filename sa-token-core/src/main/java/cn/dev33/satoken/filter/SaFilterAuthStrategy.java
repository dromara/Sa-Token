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
package cn.dev33.satoken.filter;

/**
 * Sa-Token 全局过滤器 - 认证策略封装，方便 lambda 表达式风格调用
 *
 * @author click33
 * @since 1.17.0
 */
@FunctionalInterface
public interface SaFilterAuthStrategy {
	
	/**
	 * 执行方法 
	 * @param obj 无含义参数，留作扩展
	 */
	void run(Object obj);
	
}
