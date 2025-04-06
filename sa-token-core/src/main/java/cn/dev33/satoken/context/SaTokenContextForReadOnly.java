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
import cn.dev33.satoken.context.model.SaTokenContextModelBox;

/**
 * Sa-Token 上下文处理器次级实现：只读上下文
 * 
 * @author click33
 * @since 1.42.0
 */
public interface SaTokenContextForReadOnly extends SaTokenContext {

	@Override
	default void setContext(SaRequest req, SaResponse res, SaStorage stg) {

	}

	@Override
	default void clearContext() {

	}

	@Override
	default SaTokenContextModelBox getModelBox() {
		return null;
	}

}
