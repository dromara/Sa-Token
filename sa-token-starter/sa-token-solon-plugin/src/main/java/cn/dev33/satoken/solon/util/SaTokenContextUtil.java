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
package cn.dev33.satoken.solon.util;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.context.model.SaTokenContextModelBox;
import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.solon.model.SaRequestForSolon;
import cn.dev33.satoken.solon.model.SaResponseForSolon;
import cn.dev33.satoken.solon.model.SaStorageForSolon;
import org.noear.solon.core.handle.Context;

/**
 * SaTokenContext 上下文读写工具类
 *
 * @author click33
 * @since 1.42.0
 */
public class SaTokenContextUtil {

	/**
	 * 写入当前上下文
	 */
	public static void setContext(Context ctx) {
		SaRequest req = new SaRequestForSolon(ctx);
		SaResponse res = new SaResponseForSolon(ctx);
		SaStorage stg = new SaStorageForSolon(ctx);
		SaManager.getSaTokenContext().setContext(req, res, stg);
	}

	/**
	 * 清除当前上下文
	 */
	public static void clearContext() {
		SaManager.getSaTokenContext().clearContext();
	}

	/**
	 * 写入上下文对象, 并在执行函数后将其清除
	 * @param ctx /
	 * @param fun /
	 */
	public static void setContext(Context ctx, SaFunction fun) {
		try {
			setContext(ctx);
			fun.run();
		} finally {
			clearContext();
		}
	}

	/**
	 * 获取当前 ModelBox
	 * @return /
	 */
	public static SaTokenContextModelBox getModelBox() {
		return SaManager.getSaTokenContext().getModelBox();
	}

	/**
	 * 获取当前 Context
	 * @return /
	 */
	public static Context getContext() {
		return (Context) getModelBox().getStorage().getSource();
	}

}
