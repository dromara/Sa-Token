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
package cn.dev33.satoken.integration.grpc.fixture;

/**
 * Provider 回给 Consumer 的登录快照，用字符串过 gRPC。
 */
public class LoginSnapshot {

	private boolean login;
	private String token;
	private String loginId;
	private String tag;

	/** 把 "login|loginId|token|tag" 拆回快照 */
	public static LoginSnapshot parse(String raw) {
		String[] parts = raw.split("\\|", -1);
		LoginSnapshot snapshot = new LoginSnapshot();
		snapshot.setLogin("true".equals(parts[0]));
		snapshot.setLoginId(emptyToNull(parts.length > 1 ? parts[1] : ""));
		snapshot.setToken(emptyToNull(parts.length > 2 ? parts[2] : ""));
		snapshot.setTag(emptyToNull(parts.length > 3 ? parts[3] : ""));
		return snapshot;
	}

	/** 编成 "login|loginId|token|tag" 过 gRPC */
	public String encode() {
		return (login ? "true" : "false") + "|"
				+ (loginId == null ? "" : loginId) + "|"
				+ (token == null ? "" : token) + "|"
				+ (tag == null ? "" : tag);
	}

	private static String emptyToNull(String value) {
		return value == null || value.isEmpty() ? null : value;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
