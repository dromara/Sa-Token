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
 * See the License for the specific language governing permissions.
 * limitations under the License.
 */
package cn.dev33.satoken.sso.support;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaResponseForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.fun.SaRetGenericFunction;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.config.SaSsoClientModel;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SSO 插件单测现场：换干净 Processor 模板、装默认同步策略、挂 mock 请求。
 */
public final class SsoTestSupport {

	/** 测试用签名秘钥 */
	public static final String SECRET = "sso-unit-secret-key";

	/** 测试用 client 标识 */
	public static final String CLIENT = "sso-client3";

	/** 测试用 client 登录地址 */
	public static final String CLIENT_LOGIN = "http://sso-client.com/sso/login";

	/** 测试用 allow-url */
	public static final String ALLOW = "http://sso-client.com/*";

	private SsoTestSupport() {
	}

	/** 给全局 Processor 换上新模板，并把 Server 异步改成同步，避免断言抢跑 */
	public static void resetProcessors() {
		SaSsoServerProcessor.instance.ssoServerTemplate = new SaSsoServerTemplate();
		SaSsoClientProcessor.instance.ssoClientTemplate = new SaSsoClientTemplate();
		SaSsoServerProcessor.instance.ssoServerTemplate.strategy.asyncRun = fun -> fun.run();
	}

	/** 装一套能过签名和 allowUrl 的 server / client 配置，并挂扁平 JSON */
	public static void installDefaultConfig() {
		SaSsoClientModel client = new SaSsoClientModel()
				.setClient(CLIENT)
				.setAllowUrl(ALLOW)
				.setSecretKey(SECRET)
				.setIsPush(true)
				.setIsSlo(true)
				.setServerUrl("http://sso-client.com");
		SaSsoManager.setServerConfig(new SaSsoServerConfig()
				.setSecretKey(SECRET)
				.setIsCheckSign(true)
				.setAllowAnonClient(false)
				.setTicketTimeout(300)
				.addClient(client));
		SaSsoManager.setClientConfig(new SaSsoClientConfig()
				.setClient(CLIENT)
				.setServerUrl("http://sso-server.com")
				.setSecretKey(SECRET)
				.setIsHttp(true)
				.setIsSlo(true)
				.setIsCheckSign(true)
				.setRegLogoutCall(false));
		SaManager.setSaJsonTemplate(new StubSaJsonTemplate());
	}

	/** 当前 mock 响应里记下的跳转地址 */
	public static String redirectTo() {
		return ((SaResponseForMock) SaHolder.getResponse()).redirectTo;
	}

	/** 当前 mock 响应头 */
	public static String header(String name) {
		return ((SaResponseForMock) SaHolder.getResponse()).headerMap.get(name);
	}

	/** 挂 mock 请求（只给 path）再跑一段代码 */
	public static <T> T withPath(String path, SaRetGenericFunction<T> fun) {
		return withRequest(path, null, fun);
	}

	/** 挂 mock 请求（path + 参数）再跑一段代码 */
	public static <T> T withRequest(String path, Map<String, String> params, SaRetGenericFunction<T> fun) {
		return SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = path;
			req.url = "http://127.0.0.1" + path;
			req.method = "GET";
			if (params != null) {
				req.parameterMap.putAll(params);
			}
			return fun.run();
		});
	}

	/** 方便拼请求参数 */
	public static Map<String, String> params(String... kv) {
		Map<String, String> map = new LinkedHashMap<>();
		for (int i = 0; i < kv.length; i += 2) {
			map.put(kv[i], kv[i + 1]);
		}
		return map;
	}

	/** 给参数补上 timestamp / nonce / sign，再转成 String map 塞进 mock 请求 */
	public static Map<String, String> signed(SaSsoServerTemplate template, String client, Map<String, Object> raw) {
		Map<String, Object> signed = template.getSignTemplate(client).addSignParams(raw);
		Map<String, String> out = new LinkedHashMap<>();
		for (Map.Entry<String, Object> e : signed.entrySet()) {
			out.put(e.getKey(), e.getValue() == null ? null : String.valueOf(e.getValue()));
		}
		return out;
	}

	/** 用 client 模板给参数签名 */
	public static Map<String, String> signedByClient(SaSsoClientTemplate template, Map<String, Object> raw) {
		Map<String, Object> signed = template.getSignTemplate().addSignParams(raw);
		Map<String, String> out = new LinkedHashMap<>();
		for (Map.Entry<String, Object> e : signed.entrySet()) {
			out.put(e.getKey(), e.getValue() == null ? null : String.valueOf(e.getValue()));
		}
		return out;
	}

}
