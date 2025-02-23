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

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.serializer.SaSerializerTemplate;

import java.io.*;

/**
 * 序列化器次级实现: jdk序列化
 *
 * @author click33
 * @since 1.41.0
 */
public interface SaSerializerTemplateForJdk extends SaSerializerTemplate {

	@Override
	default String objectToString(Object obj) {
		byte[] bytes = objectToBytes(obj);
		if (bytes == null) {
			return null;
		}
		return bytesToString(bytes);
    }

	@Override
	default Object stringToObject(String str) {
		if(str == null) {
			return null;
		}
		byte[] bytes = stringToBytes(str);
		return bytesToObject(bytes);
    }

	@Override
	default byte[] objectToBytes(Object obj) {
		if (obj == null) {
			return null;
		}
		try (
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos)
		) {
			oos.writeObject(obj);
			return baos.toByteArray();
		} catch (IOException e) {
			throw new SaTokenException(e);
		}
	}

	@Override
	default Object bytesToObject(byte[] bytes) {
		if(bytes == null) {
			return null;
		}
		try (
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais)
		) {
			return ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new SaTokenException(e);
		}
	}

	/**
	 * byte[] 转换为 String
	 * @param bytes /
	 * @return /
	 */
	String bytesToString(byte[] bytes);

	/**
	 * String 转换为 byte[]
	 * @param str /
	 * @return /
	 */
	byte[] stringToBytes(String str);

}
