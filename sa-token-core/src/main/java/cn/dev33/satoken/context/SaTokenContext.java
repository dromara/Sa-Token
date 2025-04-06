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
package cn.dev33.satoken.context;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;

/**
 * Sa-Token 上下文处理器
 *
 * <p> 上下文处理器封装了当前应用环境的底层操作，是 Sa-Token 对接不同 web 框架的关键，详细可参考在线文档 “自定义 SaTokenContext 指南”章节 </p>
 *
 * @author click33
 * @since 1.16.0
 */
public interface SaTokenContext {

	/**
	 * 获取当前请求的 Request 包装对象
	 * @see SaRequest
	 * 
	 * @return /
	 */
	SaRequest getRequest();

	/**
	 * 获取当前请求的 Response 包装对象
	 * @see SaResponse
	 * 
	 * @return /
	 */
	SaResponse getResponse();

	/**
	 * 获取当前请求的 Storage 包装对象
	 * @see SaStorage
	 * 
	 * @return /
	 */
	SaStorage getStorage();

	/**
	 * 判断：在本次请求中，此上下文是否可用。
	 * <p> 例如在部分 rpc 调用时， 一级上下文会返回 false，这时候框架就会选择使用二级上下文来处理请求 </p>
	 *
	 * @return / 
	 */
	default boolean isValid() {
		return false;
	}
	
}
