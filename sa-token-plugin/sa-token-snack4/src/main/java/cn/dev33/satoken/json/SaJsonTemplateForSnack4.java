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
package cn.dev33.satoken.json;

import cn.dev33.satoken.exception.SaJsonConvertException;
import cn.dev33.satoken.strategy.SaJsonStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.codec.TypeChecker;

import java.util.List;
import java.util.Map;

/**
 * JSON 转换器， Snack4 版实现
 *
 * @author click33
 * @author noear
 * @since 1.41.0
 */
public class SaJsonTemplateForSnack4 implements SaJsonTemplate {

	/**
	 * 带多态类型信息的 Options（用于 Session 等复杂对象序列化 / 反序列化）
	 */
	public final Options options;

	/**
	 * 无多态类型配置的 Options（用于 Map 等简单 JSON 解析）
	 */
	public final Options mapOptions;

	public SaJsonTemplateForSnack4() {
		List<Class<?>> allowTypeList = SaJsonStrategy.instance.getSaJsonAllowTypeList();
		this.options = buildTypedOptions(allowTypeList);
		this.mapOptions = Options.of();
	}

	/**
	 * 序列化：对象 -> json 字符串
	 */
	@Override
	public String objectToJson(Object obj) {
		if (SaFoxUtil.isEmpty(obj)) {
			return null;
		}
		if (obj instanceof Map) {
			return ONode.ofBean(obj, mapOptions).toJson();
		}
		return ONode.ofBean(obj, options).toJson();
	}

	/**
	 * 反序列化：json 字符串 → 对象
	 */
	@Override
	public <T> T jsonToObject(String jsonStr, Class<T> type) {
		if (SaFoxUtil.isEmpty(jsonStr)) {
			return null;
		}
		try {
			return ONode.deserialize(jsonStr, type, options);
		} catch (RuntimeException e) {
			throw toSaJsonConvertException(e);
		}
	}

	/**
	 * 将 json 字符串解析为 Map
	 */
	@Override
	public Map<String, Object> jsonToMap(String jsonStr) {
		if (SaFoxUtil.isEmpty(jsonStr)) {
			return null;
		}
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = ONode.deserialize(jsonStr, Map.class, mapOptions);
			return map;
		} catch (RuntimeException e) {
			throw new SaJsonConvertException(e);
		}
	}


	// ----------------------- 内部方法

	/**
	 * 根据 {@link SaJsonStrategy} 白名单构建 Snack4 多态反序列化 Options
	 */
	public static Options buildTypedOptions(List<Class<?>> allowTypeList) {
		Options opts = Options.of(
				Feature.Write_ClassName,
				Feature.Write_NotRootClassName,
				Feature.Read_AutoType
		);
		opts.addChecker(className -> {
			for (Class<?> allowType : allowTypeList) {
				if (isAllowType(className, allowType, opts)) {
					return TypeChecker.ALLOW;
				}
			}
			return TypeChecker.DENY;
		});
		return opts;
	}

	static boolean isAllowType(String className, Class<?> allowType, Options opts) {
		if (allowType.getName().equals(className)) {
			return true;
		}
		Class<?> clz = opts.loadClass(className, false);
		return clz != null && allowType.isAssignableFrom(clz);
	}

	/**
	 * 将 Snack4 反序列化异常包装为 {@link SaJsonConvertException}；
	 * 若为多态类型白名单拦截，则明确提示无法反序列化的类型名。
	 */
	static SaJsonConvertException toSaJsonConvertException(Throwable e) {
		String blockedType = findBlockedTypeClassName(e);
		if (blockedType != null) {
			return new SaJsonConvertException(
					"无法反序列化的类型：" + blockedType + "，请先将其注册到 JSON 全局类型白名单",
					e);
		}
		return new SaJsonConvertException(e);
	}

	static String findBlockedTypeClassName(Throwable e) {
		for (Throwable t = e; t != null; t = t.getCause()) {
			String message = t.getMessage();
			if (message == null) {
				continue;
			}
			if (message.startsWith("Blocked type, class: ")) {
				return message.substring("Blocked type, class: ".length());
			}
		}
		return null;
	}

}
