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
package cn.dev33.satoken.plugin;

import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.util.SaFoxUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * SSO Client {@code buildServerAuthUrl}：避免重复追加 back 参数
 */
public class SaSsoClientTemplateBuildServerAuthUrlTest {

	private SaSsoClientConfig backupClientConfig;
	private SaSsoClientTemplate template;

	@BeforeEach
	public void setup() {
		backupClientConfig = SaSsoManager.getClientConfig();
		SaSsoManager.setClientConfig(new SaSsoClientConfig().setServerUrl("http://sso-server.com"));
		template = new SaSsoClientTemplate();
	}

	@AfterEach
	public void restore() {
		SaSsoManager.setClientConfig(backupClientConfig);
	}

	@Test
	public void appendBack_whenClientLoginUrlHasNoBack() {
		String url = template.buildServerAuthUrl("http://client.com/sso/login", "http://client.com/index");
		Assertions.assertTrue(url.contains("?back=" + SaFoxUtil.encodeUrl("http://client.com/index")));
	}

	@Test
	public void skipAppend_whenPlainQuestionBackAlreadyPresent() {
		String clientLoginUrl = "http://client.com/sso/login?back=http://client.com/index";
		String url = template.buildServerAuthUrl(clientLoginUrl, "http://client.com/other");
		Assertions.assertTrue(url.contains(clientLoginUrl));
		Assertions.assertFalse(url.contains("&back="));
	}

	@Test
	public void skipAppend_whenPlainAmpersandBackAlreadyPresent() {
		String clientLoginUrl = "http://client.com/sso/login?foo=1&back=http://client.com/index";
		String url = template.buildServerAuthUrl(clientLoginUrl, "http://client.com/other");
		Assertions.assertTrue(url.contains(clientLoginUrl));
		Assertions.assertEquals(1, countIgnoreCase(url, "&back="));
	}

	@Test
	public void skipAppend_whenFullyEncodedQuestionBackAlreadyPresent() {
		String clientLoginUrl = "http://client.com/sso/login%3Fback%3Dhttp%3A%2F%2Fclient.com%2Findex";
		String url = template.buildServerAuthUrl(clientLoginUrl, "http://client.com/other");
		Assertions.assertTrue(url.contains(clientLoginUrl));
		Assertions.assertFalse(url.toLowerCase().contains("back="));
	}

	@Test
	public void skipAppend_whenFullyEncodedAmpersandBackAlreadyPresent() {
		String clientLoginUrl = "http://client.com/sso/login%3Ffoo%3D1%26back%3Dhttp%3A%2F%2Fclient.com%2Findex";
		String url = template.buildServerAuthUrl(clientLoginUrl, "http://client.com/other");
		Assertions.assertTrue(url.contains(clientLoginUrl));
		Assertions.assertFalse(url.toLowerCase().contains("back="));
	}

	@Test
	public void stillAppend_whenParamNameOnlyLooksLikeBack() {
		String url = template.buildServerAuthUrl("http://client.com/sso/login?abcback=1", "http://client.com/index");
		Assertions.assertTrue(url.contains("&back=" + SaFoxUtil.encodeUrl("http://client.com/index")));
	}

	@Test
	public void skipAppend_whenBackIsEmpty() {
		String clientLoginUrl = "http://client.com/sso/login";
		String url = template.buildServerAuthUrl(clientLoginUrl, "");
		Assertions.assertFalse(url.contains("back="));
		Assertions.assertTrue(url.endsWith("redirect=" + clientLoginUrl));
	}

	private static int countIgnoreCase(String text, String needle) {
		String lower = text.toLowerCase();
		String n = needle.toLowerCase();
		int count = 0;
		int from = 0;
		while ((from = lower.indexOf(n, from)) != -1) {
			count++;
			from += n.length();
		}
		return count;
	}

}
