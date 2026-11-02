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
package cn.dev33.satoken.solon.testsupport;

import cn.dev33.satoken.solon.SaBeanRegister;
import cn.dev33.satoken.solon.util.SaTokenContextSolonUtil;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextHolder;
import org.noear.solon.core.handle.Handler;

/**
 * Solon 插件单测公共辅助：注册策略、造请求、临时挂上 Context.current()。
 */
public final class SolonTestHelper {

	/** Solon 把主处理器存在这个 attr 键上 */
	public static final String ATTR_MAIN_HANDLER = "ATTR_MAIN_HANDLER";

	private SolonTestHelper() {
	}

	/** 用 SaBeanRegister 构造器挂上 Solon 版 SaRequest/SaResponse/SaStorage 创建策略 */
	public static void ensureSolonStrategy() {
		new SaBeanRegister();
	}

	/** 造一个 GET 请求上下文 */
	public static TestSolonContext newGetContext(String path) {
		return new TestSolonContext().method("GET").path(path).url("http://localhost" + path);
	}

	/** 把主处理器写进 Context，供 SaTokenFilter 走 mainHandler 分支 */
	public static void setMainHandler(Context ctx, Handler handler) {
		ctx.attrSet(ATTR_MAIN_HANDLER, handler);
	}

	/** 把 ctx 设成当前线程 Context，跑完清掉 */
	public static void withCurrent(Context ctx, Runnable action) {
		ContextHolder.currentSet(ctx);
		try {
			action.run();
		} finally {
			ContextHolder.currentRemove();
		}
	}

	/**
	 * 写入 Sa-Token 上下文再执行。Filter / 拦截器走 SaRouter 时必须先有上下文。
	 */
	public static void withSaContext(Context ctx, ThrowingRunnable action) {
		ensureSolonStrategy();
		SaTokenContextSolonUtil.setContext(ctx, () -> {
			try {
				action.run();
			} catch (RuntimeException e) {
				throw e;
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		});
	}

	/**
	 * Solon.app() 没起来时 ctx.render 会 NPE。单测只确认写回分支被走进去了，真渲染交给 HTTP 集成测。
	 */
	public static void runMayNpeOnRender(ThrowingRunnable action) {
		try {
			action.run();
		} catch (NullPointerException e) {
			if (e.getMessage() != null && e.getMessage().contains("renderManager")) {
				return;
			}
			throw e;
		} catch (RuntimeException e) {
			Throwable cause = e.getCause();
			if (cause instanceof NullPointerException && cause.getMessage() != null
					&& cause.getMessage().contains("renderManager")) {
				return;
			}
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/** 允许抛检查异常的一段逻辑 */
	@FunctionalInterface
	public interface ThrowingRunnable {
		void run() throws Throwable;
	}

}
