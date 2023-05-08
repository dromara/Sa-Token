package cn.dev33.satoken.util;

/**
 * 字符串格式化工具，将字符串中的 {} 按序替换为参数
 * <p>
 * 	本工具类 copy 自 Hutool：
 * 		https://github.com/dromara/hutool/blob/v5-master/hutool-core/src/main/java/cn/hutool/core/text/StrFormatter.java
 * </p>
 *
 * @author Looly
 * @since <= 1.34.0
 */
public class StrFormatter {

	/**
	 * 占位符 
	 */
	public static String EMPTY_JSON = "{}";
	
	public static char C_BACKSLASH = '\\';
	
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
	 * @return 结果
	 */
	public static String format(String strPattern, Object... argArray) {
		return formatWith(strPattern, EMPTY_JSON, argArray);
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
	 * @return 结果
	 * @since 5.7.14
	 */
	public static String formatWith(String strPattern, String placeHolder, Object... argArray) {
		if (SaFoxUtil.isEmpty(strPattern) || SaFoxUtil.isEmpty(placeHolder) || SaFoxUtil.isEmpty(argArray)) {
			return strPattern;
		}
		final int strPatternLength = strPattern.length();
		final int placeHolderLength = placeHolder.length();

		// 初始化定义好的长度以获得更好的性能
		final StringBuilder sbuf = new StringBuilder(strPatternLength + 50);

		int handledPosition = 0;// 记录已经处理到的位置
		int delimIndex;// 占位符所在位置
		for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
			delimIndex = strPattern.indexOf(placeHolder, handledPosition);
			if (delimIndex == -1) {// 剩余部分无占位符
				if (handledPosition == 0) { // 不带占位符的模板直接返回
					return strPattern;
				}
				// 字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
				sbuf.append(strPattern, handledPosition, strPatternLength);
				return sbuf.toString();
			}

			// 转义符
			if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == C_BACKSLASH) {// 转义符
				if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == C_BACKSLASH) {// 双转义符
					// 转义符之前还有一个转义符，占位符依旧有效
					sbuf.append(strPattern, handledPosition, delimIndex - 1);
					sbuf.append(String.valueOf(argArray[argIndex]));
					handledPosition = delimIndex + placeHolderLength;
				} else {
					// 占位符被转义
					argIndex--;
					sbuf.append(strPattern, handledPosition, delimIndex - 1);
					sbuf.append(placeHolder.charAt(0));
					handledPosition = delimIndex + 1;
				}
			} else {// 正常占位符
				sbuf.append(strPattern, handledPosition, delimIndex);
				sbuf.append(String.valueOf(argArray[argIndex]));
				handledPosition = delimIndex + placeHolderLength;
			}
		}

		// 加入最后一个占位符后所有的字符
		sbuf.append(strPattern, handledPosition, strPatternLength);

		return sbuf.toString();
	}

}
