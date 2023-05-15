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
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.InvalidContextException;

/**
 * Sa-Token 上下文处理器 [ 默认实现类 ]
 * 
 * <p>  
 * 	一般情况下框架会为你自动注入合适的上下文处理器，如果代码断点走到了此默认实现类，
 * 	说明你引入的依赖有问题或者错误的调用了 Sa-Token 的API， 请在 [ 在线开发文档 → 附录 → 常见问题排查 ] 中按照提示进行排查
 * </p>
 * 
 * @author click33
 * @since <= 1.34.0
 */
public class SaTokenContextDefaultImpl implements SaTokenContext {
	
	/**
	 * 默认的上下文处理器对象  
	 */
	public static SaTokenContextDefaultImpl defaultContext = new SaTokenContextDefaultImpl();

	/**
	 * 错误提示语
	 */
	public static final String ERROR_MESSAGE = "未能获取有效的上下文处理器";

	@Override
	public SaRequest getRequest() {
		throw new InvalidContextException(ERROR_MESSAGE).setCode(SaErrorCode.CODE_10001);
	}

	@Override
	public SaResponse getResponse() {
		throw new InvalidContextException(ERROR_MESSAGE).setCode(SaErrorCode.CODE_10001);
	}

	@Override
	public SaStorage getStorage() {
		throw new InvalidContextException(ERROR_MESSAGE).setCode(SaErrorCode.CODE_10001);
	}

	@Override
	public boolean matchPath(String pattern, String path) {
		throw new InvalidContextException(ERROR_MESSAGE).setCode(SaErrorCode.CODE_10001);
	}

}
