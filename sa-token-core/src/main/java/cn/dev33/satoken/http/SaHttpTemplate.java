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

import java.util.Map;

/**
 * Http 请求处理器
 * 
 * @author click33
 * @since 1.42.0
 */
public interface SaHttpTemplate {

	/**
	 * get 请求
	 *
	 * @param url /
	 * @return /
	 */
	String get(String url);

	/**
	 * post 请求，form-data 格式参数
	 *
	 * @param url /
	 * @param params /
	 * @return /
	 */
	String postByFormData(String url, Map<String, Object> params);

}
