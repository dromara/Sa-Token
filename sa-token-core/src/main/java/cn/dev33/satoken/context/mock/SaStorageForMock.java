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
package cn.dev33.satoken.context.mock;

import cn.dev33.satoken.context.model.SaStorage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 对 SaStorage 包装类的实现（Mock 版）
 *
 * @author click33
 * @since 1.42.0
 */
public class SaStorageForMock implements SaStorage {

	public Map<String, Object> dataMap = new LinkedHashMap<>();

	/**
	 * 获取底层源对象 
	 */
	@Override
	public Object getSource() {
		return null;
	}

	/**
	 * 在 [Request作用域] 里写入一个值 
	 */
	@Override
	public SaStorageForMock set(String key, Object value) {
		dataMap.put(key, value);
		return this;
	}

	/**
	 * 在 [Request作用域] 里获取一个值 
	 */
	@Override
	public Object get(String key) {
		return dataMap.get(key);
	}

	/**
	 * 在 [Request作用域] 里删除一个值 
	 */
	@Override
	public SaStorageForMock delete(String key) {
		dataMap.remove(key);
		return this;
	}

}
