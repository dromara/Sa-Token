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
package cn.dev33.satoken.core.util;
import cn.dev33.satoken.util.SaFoxUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
/**
 * {@link SaFoxUtil#isUrl(String)} 专项测试（SSO / OAuth2 redirect_url 校验依赖此方法）
 *
 * @author click33
 * @since 1.46.0
 */
public class SaFoxUtilIsUrlTest {
	/** isUrl 应识别 http/https 域名、端口及带认证信息的 URL */
	@Test
	public void isUrl_httpHttps_domainAndPort() {
		assertUrl(true,
				"https://sa-token.com",
				"https://www.baidu.com/",
				"http://sa-sso-client1.com:9003/sso/login",
				"https://sa-token.com:443/sso/login?back=/",
				"HTTP://SA-TOKEN.COM/path",
				"https://user:pass@sa-token.com/callback"
		);
	}
	/** isUrl 应识别方括号包裹的 IPv6 字面量及端口 */
	@Test
	public void isUrl_ipv6_bracketLiteral() {
		assertUrl(true,
				"http://[::1]/sso/login",
				"http://[::1]:9003/sso/login",
				"http://[2001:db8::1]:8080/callback",
				"http://[2001:0db8:85a3:0000:0000:8a2e:0370:7334]:9003/sso/pushC",
				"https://[fe80::1%25en0]:8080/oauth2/callback",
				"http://[::1]:9003/sso/login?ticket=xxx&back=/"
		);
	}
	/** isUrl 应识别 ftp 与 file 协议 URL */
	@Test
	public void isUrl_ftpAndFile() {
		assertUrl(true,
				"ftp://files.example.com/pub/readme.txt",
				"file:///C:/temp/a.txt",
				"file://localhost/tmp/a.txt"
		);
	}
	/** isUrl 应拒绝 null、空串、错误协议及非 http(s)/ftp/file 的 scheme */
	@Test
	public void isUrl_nullEmptyAndWrongScheme() {
		assertUrl(false,
				null,
				"",
				"   ",
				"htt://www.baidu.com/",
				"https:www.baidu.com/",
				"httpswwwbaiducom/",
				"javascript://alert(1)",
				"ws://localhost:8080/ws",
				"mailto:test@example.com"
		);
	}
	/** isUrl 应拒绝缺少主机或 authority 格式错误的 URL */
	@Test
	public void isUrl_missingHostOrMalformedAuthority() {
		assertUrl(false,
				"http://",
				"https://",
				"ftp://",
				"http://?a=1",
				"https://#frag",
				"http://[::1",
				"http://::1]",
				"http://]/sso/login"
		);
	}
	/** isUrl 应拒绝未用方括号包裹的 IPv6 字面量 */
	@Test
	public void isUrl_ipv6_rejectUnbracketedLiteral() {
		// RFC 3986：带端口的 IPv6 必须使用方括号；裸写多个冒号会造成解析歧义
		assertUrl(false,
				"http://2001:db8::1:8080/callback",
				"http://::1:9003/sso/login",
				"https://fe80::1:8080/callback"
		);
	}
	/** isUrl 应拒绝整个 URL 以逗号结尾的历史非法格式 */
	@Test
	public void isUrl_legacyInvalidSuffixAndComma() {
		assertUrl(false,
				"https://www.baidu.com/,",
				"http://sa-token.com/path,"
		);
	}
	/** isUrl 应允许路径中间含逗号的合法 URL */
	@Test
	public void isUrl_trailingCommaInPathIsAllowed() {
		// 仅拒绝「整个 URL 以逗号结尾」的历史行为；路径中间的逗号仍视为合法
		Assertions.assertTrue(SaFoxUtil.isUrl("https://www.baidu.com/a,b"));
	}
	private static void assertUrl(boolean expected, String... urls) {
		for (String url : urls) {
			Assertions.assertEquals(expected, SaFoxUtil.isUrl(url), () -> "isUrl(" + url + ")");
		}
	}
}
