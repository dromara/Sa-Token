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
package cn.dev33.satoken.solon.model;

import cn.dev33.satoken.context.model.SaResponse;
import org.noear.solon.core.handle.Context;

/**
 * @author noear
 * @since 1.4
 */
public class SaResponseForSolon implements SaResponse {

	protected Context ctx;

	public SaResponseForSolon() {
		this(Context.current());
	}

	public SaResponseForSolon(Context ctx) {
		this.ctx = ctx;
	}

	@Override
	public Object getSource() {
		return ctx;
	}

	@Override
	public SaResponse setStatus(int sc) {
		ctx.status(sc);
		return this;
	}

	@Override
	public SaResponse setHeader(String name, String value) {
		ctx.headerSet(name, value);
		return this;
	}

	/**
	 * 在响应头里添加一个值
	 *
	 * @param name  名字
	 * @param value 值
	 * @return 对象自身
	 */
	public SaResponse addHeader(String name, String value) {
		ctx.headerAdd(name, value);
		return this;
	}

	@Override
	public Object redirect(String url) {
		ctx.redirect(url);
		return null;
	}
}
