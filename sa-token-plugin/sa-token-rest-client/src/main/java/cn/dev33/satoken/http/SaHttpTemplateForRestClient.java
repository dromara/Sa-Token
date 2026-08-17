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
package cn.dev33.satoken.http;

import cn.dev33.satoken.SaManager;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Http 请求处理器， RestClient 版实现
 *
 * @author click33
 * @since 1.45.0
 */
public class SaHttpTemplateForRestClient implements SaHttpTemplate {

	private final RestClient restClient = RestClient.create();

	@Override
	public String get(String url) {
		SaManager.log.debug("发起请求，GET：{}", url);
		String res = restClient.get()
				.uri(url)
				.retrieve()
				.body(String.class);
		SaManager.log.debug("返回结果：{}", res);
		return res;
	}

	@Override
	public String postByFormData(String url, Map<String, Object> params) {
		SaManager.log.debug("发起请求，POST：{}\t参数：{}", url, params);
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		params.forEach((key, value) -> formData.add(key, value == null ? "" : String.valueOf(value)));
		String res = restClient.post()
				.uri(url)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(formData)
				.retrieve()
				.body(String.class);
		SaManager.log.debug("返回结果：{}", res);
		return res;
	}

}
