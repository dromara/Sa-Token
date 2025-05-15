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
package cn.dev33.satoken.http;

import cn.dev33.satoken.SaManager;
import cn.zhxu.okhttps.OkHttps;

import java.util.Map;

/**
 * Http 请求处理器， OkHttps 版实现
 * 
 * @author click33
 * @since 1.43.0
 */
public class SaHttpTemplateForOkHttps implements SaHttpTemplate {

	@Override
	public String get(String url) {
		SaManager.log.debug("发起请求，GET：{}", url);
		String res = OkHttps.sync(url).get().getBody().toString();
		SaManager.log.debug("返回结果：{}", res);
		return res;
	}

	@Override
	public String postByFormData(String url, Map<String, Object> params) {
		SaManager.log.debug("发起请求，POST：{}\t参数：{}", url, params);
		String res = OkHttps.sync(url).addBodyPara(params).post().getBody().toString();
		SaManager.log.debug("返回结果：{}", res);
		return res;
	}

}
