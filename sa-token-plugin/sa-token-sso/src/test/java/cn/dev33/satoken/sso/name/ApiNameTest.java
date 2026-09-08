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
package cn.dev33.satoken.sso.name;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SSO 路由名默认值、加前缀、换前缀、toString
 */
public class ApiNameTest {

	/** 默认路由应该都是 /sso/ 开头那一套 */
	@Test
	public void defaults_useSsoPrefix() {
		ApiName api = new ApiName();
		Assertions.assertEquals("/sso/auth", api.ssoAuth);
		Assertions.assertEquals("/sso/doLogin", api.ssoDoLogin);
		Assertions.assertEquals("/sso/checkTicket", api.ssoCheckTicket);
		Assertions.assertEquals("/sso/pushS", api.ssoPushS);
		Assertions.assertEquals("/sso/userinfo", api.ssoUserinfo);
		Assertions.assertEquals("/sso/signout", api.ssoSignout);
		Assertions.assertEquals("/sso/login", api.ssoLogin);
		Assertions.assertEquals("/sso/logout", api.ssoLogout);
		Assertions.assertEquals("/sso/isLogin", api.ssoIsLogin);
		Assertions.assertEquals("/sso/logoutCall", api.ssoLogoutCall);
		Assertions.assertEquals("/sso/pushC", api.ssoPushC);
	}

	/** addPrefix 应该在每条路由前面再拼一段 */
	@Test
	public void addPrefix_prependsAllPaths() {
		ApiName api = new ApiName().addPrefix("/sso-user");
		Assertions.assertEquals("/sso-user/sso/auth", api.ssoAuth);
		Assertions.assertEquals("/sso-user/sso/doLogin", api.ssoDoLogin);
		Assertions.assertEquals("/sso-user/sso/checkTicket", api.ssoCheckTicket);
		Assertions.assertEquals("/sso-user/sso/pushS", api.ssoPushS);
		Assertions.assertEquals("/sso-user/sso/userinfo", api.ssoUserinfo);
		Assertions.assertEquals("/sso-user/sso/signout", api.ssoSignout);
		Assertions.assertEquals("/sso-user/sso/login", api.ssoLogin);
		Assertions.assertEquals("/sso-user/sso/logout", api.ssoLogout);
		Assertions.assertEquals("/sso-user/sso/isLogin", api.ssoIsLogin);
		Assertions.assertEquals("/sso-user/sso/pushC", api.ssoPushC);
		Assertions.assertEquals("/sso-user/sso/logoutCall", api.ssoLogoutCall);
	}

	/** replacePrefix 应该把开头的 /sso 换成新前缀 */
	@Test
	public void replacePrefix_replacesLeadingSso() {
		ApiName api = new ApiName().replacePrefix("/sso-admin");
		Assertions.assertEquals("/sso-admin/auth", api.ssoAuth);
		Assertions.assertEquals("/sso-admin/doLogin", api.ssoDoLogin);
		Assertions.assertEquals("/sso-admin/checkTicket", api.ssoCheckTicket);
		Assertions.assertEquals("/sso-admin/pushS", api.ssoPushS);
		Assertions.assertEquals("/sso-admin/userinfo", api.ssoUserinfo);
		Assertions.assertEquals("/sso-admin/signout", api.ssoSignout);
		Assertions.assertEquals("/sso-admin/login", api.ssoLogin);
		Assertions.assertEquals("/sso-admin/logout", api.ssoLogout);
		Assertions.assertEquals("/sso-admin/isLogin", api.ssoIsLogin);
		Assertions.assertEquals("/sso-admin/pushC", api.ssoPushC);
		Assertions.assertEquals("/sso-admin/logoutCall", api.ssoLogoutCall);
	}

	/** toString 里应该能看到各条路由 */
	@Test
	public void toString_containsRouteFields() {
		String text = new ApiName().toString();
		Assertions.assertTrue(text.contains("ssoAuth="));
		Assertions.assertTrue(text.contains("ssoPushC="));
		Assertions.assertTrue(text.contains("/sso/auth"));
	}

}
