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
package cn.dev33.satoken.context.model;

import cn.dev33.satoken.application.SaSetValueInterface;

/**
 * Storage Model，请求作用域的读取值对象。
 *
 * <p> 在一次请求范围内: 存值、取值。数据在请求结束后失效。
 * 
 * @author click33
 * @since <= 1.34.0
 */
public interface SaStorage extends SaSetValueInterface {

	/**
	 * 获取底层被包装的源对象
	 * @return /
	 */
	public Object getSource();

	// ---- 实现接口存取值方法 

	/** 取值 */
	@Override
	public Object get(String key);

	/** 写值 */
	@Override
	public SaStorage set(String key, Object value);

	/** 删值 */
	@Override
	public SaStorage delete(String key);

}
