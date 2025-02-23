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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.serializer.SaSerializerTemplate;

/**
 * 序列化器: 使用 json 转换器
 *
 * @author click33
 * @since 1.41.0
 */
public class SaSerializerTemplateForJson implements SaSerializerTemplate {

	@Override
	public String objectToString(Object obj) {
		return SaManager.getSaJsonTemplate().objectToJson(obj);
	}

	@Override
	public Object stringToObject(String str) {
		return SaManager.getSaJsonTemplate().jsonToObject(str);
	}

	@Override
	public byte[] objectToBytes(Object obj) {
		throw new ApiDisabledException("json 序列化器不支持 Object -> byte[]");
	}

	@Override
	public Object bytesToObject(byte[] bytes) {
		throw new ApiDisabledException("json 序列化器不支持 byte[] -> Object");
	}

}
