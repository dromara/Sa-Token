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
package cn.dev33.satoken.util;

/**
 * 字符串格式化工具，将字符串中的 {} 按序替换为参数
 * <p>
 * 	本工具类 copy 自 Hutool：
 * 		https://github.com/dromara/hutool/blob/v5-master/hutool-core/src/main/java/cn/hutool/core/text/StrFormatter.java
 * </p>
 *
 * @author Looly
 * @since 1.33.0
 */
public class StrFormatter {

	/**
	 * 占位符（保留原有 public 访问权限，避免破坏外部依赖）
	 * @deprecated 语义不明确，建议内部使用 {@link #DEFAULT_PLACEHOLDER} 替代
	 */
	@Deprecated
	public static String EMPTY_JSON = "{}";

	/**
	 * 反斜杠转义字符（保留原有 public 访问权限，避免破坏外部依赖）
	 * @deprecated 命名不规范，建议内部使用 {@link #BACKSLASH_CHAR} 替代
	 */
	@Deprecated
	public static char C_BACKSLASH = '\\';

	/**
	 * 新增内部规范常量（private，仅内部使用）
	 * 默认占位符 */
	private static final String DEFAULT_PLACEHOLDER = "{}";

	/**
	 * 反斜杠转义字符
	 * */
	private static final char BACKSLASH_CHAR = '\\';

	/**
	 * 字符串构建器初始扩容长度
	 * */
	private static final int BUFFER_INIT_CAPACITY = 50;

	/**
	 * 格式化字符串<br>
	 * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
	 * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
	 * 例：<br>
	 * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
	 * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
	 * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
	 *
	 * @param strPattern 字符串模板
	 * @param argArray   参数列表
	 * @return 格式化后的结果
	 */
	public static String format(String strPattern, Object... argArray) {
		return formatWith(strPattern, DEFAULT_PLACEHOLDER, argArray);
	}

	/**
	 * 格式化字符串<br>
	 * 此方法只是简单将指定占位符 按照顺序替换为参数<br>
	 * 如果想输出占位符使用 \\转义即可，如果想输出占位符之前的 \ 使用双转义符 \\\\ 即可<br>
	 * 例：<br>
	 * 通常使用：format("this is {} for {}", "{}", "a", "b") =》 this is a for b<br>
	 * 转义{}： format("this is \\{} for {}", "{}", "a", "b") =》 this is {} for a<br>
	 * 转义\： format("this is \\\\{} for {}", "{}", "a", "b") =》 this is \a for b<br>
	 *
	 * @param strPattern  字符串模板
	 * @param placeHolder 占位符，例如{}
	 * @param argArray    参数列表
	 * @return 格式化后的结果
	 * @since 1.33.0
	 */
	public static String formatWith(String strPattern, String placeHolder, Object... argArray) {
		if (SaFoxUtil.isEmpty(strPattern) || SaFoxUtil.isEmpty(placeHolder) || SaFoxUtil.isEmpty(argArray)) {
			return strPattern;
		}
		final int strPatternLength = strPattern.length();
		final int placeHolderLength = placeHolder.length();

		// 初始化定义好的长度以获得更好的性能
		final StringBuilder sbu = new StringBuilder(strPatternLength + BUFFER_INIT_CAPACITY);

		int handledPosition = 0;// 记录已经处理到的位置
		int delimIndex;// 占位符所在位置
		for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
			delimIndex = strPattern.indexOf(placeHolder, handledPosition);
			if (delimIndex == -1) {// 剩余部分无占位符
				if (handledPosition == 0) { // 不带占位符的模板直接返回
					return strPattern;
				}
				// 字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
				sbu.append(strPattern, handledPosition, strPatternLength);
				return sbu.toString();
			}

			// 转义符
			if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == BACKSLASH_CHAR) {// 转义符
				if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == BACKSLASH_CHAR) {// 双转义符
					// 转义符之前还有一个转义符，占位符依旧有效
					sbu.append(strPattern, handledPosition, delimIndex - 1);
					sbu.append(argArray[argIndex]);
					handledPosition = delimIndex + placeHolderLength;
				} else {
					// 占位符被转义
					argIndex--;
					sbu.append(strPattern, handledPosition, delimIndex - 1);
					sbu.append(placeHolder.charAt(0));
					handledPosition = delimIndex + 1;
				}
			} else {// 正常占位符
				sbu.append(strPattern, handledPosition, delimIndex);
				sbu.append(argArray[argIndex]);
				handledPosition = delimIndex + placeHolderLength;
			}
		}

		// 加入最后一个占位符后所有的字符
		sbu.append(strPattern, handledPosition, strPatternLength);

		return sbu.toString();
	}

}