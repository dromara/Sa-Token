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

import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户项目里那种事件侦听器：把关键事件名记下来，方便断言副作用。
 *
 * @author click33
 * @since 1.46.0
 */
public class RecordingSaTokenListener extends SaTokenListenerForSimple {

	/** 按发生顺序记下的事件名 */
	public final List<String> events = new ArrayList<>();

	/** 最近一次登录/踢人/顶号/续期带上的 token */
	public String lastToken;

	@Override
	public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter loginParameter) {
		events.add("login");
		lastToken = tokenValue;
	}

	@Override
	public void doKickout(String loginType, Object loginId, String tokenValue) {
		events.add("kickout");
		lastToken = tokenValue;
	}

	@Override
	public void doReplaced(String loginType, Object loginId, String tokenValue) {
		events.add("replaced");
		lastToken = tokenValue;
	}

	@Override
	public void doRenewTimeout(String loginType, Object loginId, String tokenValue, long timeout) {
		events.add("renewTimeout");
		lastToken = tokenValue;
	}

}
