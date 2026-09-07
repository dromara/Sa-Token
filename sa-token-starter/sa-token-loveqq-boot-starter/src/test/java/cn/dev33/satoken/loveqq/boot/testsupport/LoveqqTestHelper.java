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
package cn.dev33.satoken.loveqq.boot.testsupport;

import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.loveqq.boot.SaBeanRegister;
import cn.dev33.satoken.loveqq.boot.utils.SaTokenContextUtil;
import com.kfyty.loveqq.framework.web.core.http.ServerRequest;
import com.kfyty.loveqq.framework.web.core.http.ServerResponse;

/**
 * LoveQQ 插件单测公共辅助：注册策略、造请求、临时挂上 Sa-Token 上下文。
 */
public final class LoveqqTestHelper {

	private LoveqqTestHelper() {
	}

	/** 用 SaBeanRegister 构造器挂上 LoveQQ 版 SaRequest/SaResponse/SaStorage 创建策略 */
	public static void ensureLoveqqStrategy() {
		new SaBeanRegister();
	}

	/** 造一个 GET 请求 */
	public static TestServerRequest newGetRequest(String path) {
		return new TestServerRequest().method("GET").path(path);
	}

	/** 造一个空响应 */
	public static TestServerResponse newResponse() {
		return new TestServerResponse();
	}

	/** 写入 Sa-Token 上下文再执行。Filter / 拦截器走 SaRouter 时必须先有上下文。 */
	public static void withSaContext(ServerRequest request, ServerResponse response, SaFunction action) {
		ensureLoveqqStrategy();
		SaTokenContextUtil.setContext(request, response, action);
	}

}
