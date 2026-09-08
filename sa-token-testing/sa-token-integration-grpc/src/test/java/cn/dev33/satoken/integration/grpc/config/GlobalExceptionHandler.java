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
package cn.dev33.satoken.integration.grpc.config;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.util.SaResult;
import io.grpc.StatusRuntimeException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 把 Sa-Token / gRPC 异常收成 JSON，方便断言，别渲成 500 页。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/** Same-Token 校验失败等都会进这儿 */
	@ExceptionHandler(SaTokenException.class)
	public SaResult handleSaToken(SaTokenException e) {
		return SaResult.error(e.getMessage()).setCode(e.getCode());
	}

	/** Provider 端上下文没挂上时，StpUtil 会从 gRPC 冒成 StatusRuntimeException */
	@ExceptionHandler(StatusRuntimeException.class)
	public SaResult handleGrpc(StatusRuntimeException e) {
		return SaResult.error(e.getMessage());
	}

}
