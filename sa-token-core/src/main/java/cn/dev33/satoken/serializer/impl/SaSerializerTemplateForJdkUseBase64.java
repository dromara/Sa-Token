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
package cn.dev33.satoken.serializer.impl;

import java.util.Base64;

/**
 * 序列化器: jdk序列化、Base64 编码 (体积+33%)
 *
 * @author click33
 * @since 1.41.0
 */
public class SaSerializerTemplateForJdkUseBase64 implements SaSerializerTemplateForJdk {

	@Override
	public String bytesToString(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	@Override
	public byte[] stringToBytes(String str) {
		return Base64.getDecoder().decode(str);
	}

}
