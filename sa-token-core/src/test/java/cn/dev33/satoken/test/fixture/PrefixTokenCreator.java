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
package cn.dev33.satoken.test.fixture;

import cn.dev33.satoken.fun.strategy.SaCreateTokenFunction;

/**
 * 用户项目里那种自定义 Token 生成策略：固定前缀 + 账号 + 序号。
 *
 * @author click33
 * @since 1.46.0
 */
public class PrefixTokenCreator implements SaCreateTokenFunction {

	private final String prefix;
	private int seq;

	/** 按用户指定的前缀生成 token */
	public PrefixTokenCreator(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public String apply(Object loginId, String loginType) {
		return prefix + "-" + loginId + "-" + (++seq);
	}

}
