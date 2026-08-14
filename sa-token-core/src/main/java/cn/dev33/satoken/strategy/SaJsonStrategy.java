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
package cn.dev33.satoken.strategy;

import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.json.SaJsonType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Sa-Token JSON 全局策略：统一管理多态 JSON 允许反序列化的类型白名单。
 * <p>
 * 每次调用 {@link #getSaJsonAllowTypeList()} 都会现场合并各来源并返回一份新 List 快照。
 * 首次调用 {@link #getSaJsonAllowTypeList()} 之后 {@link #isInit()} 为 true，不可再 {@link #registerAllowType(Class)}。
 * 请在 JSON 相关插件初始化之前完成自定义类型注册（或在 {@code main} 中、Spring 启动前调用）。
 * </p>
 *
 * @author click33
 * @since 1.46.0
 */
public final class SaJsonStrategy {

	private SaJsonStrategy() {
	}

	/**
	 * 获取 SaJsonStrategy 对象的单例引用
	 */
	public static final SaJsonStrategy instance = new SaJsonStrategy();

	/**
	 * SPI 配置文件路径：{@code META-INF/satoken/sa-json-type.list}
	 */
	public static final String SPI_FILE = "META-INF/satoken/sa-json-type.list";

	/**
	 * 用户通过 {@link #registerAllowType(Class)} 动态注册的类型集合。
	 * <p>
	 * 在 {@link #isInit()} 为 false 时可继续写入；每次 {@link #getSaJsonAllowTypeList()} 会与 JDK 内置类型、
	 * {@link SaJsonType} 标记、SPI 文件声明的类型合并去重后返回快照。
	 * 使用 {@link LinkedHashSet} 保持注册顺序并避免重复。
	 * </p>
	 */
	private final Set<Class<?>> customTypes = new LinkedHashSet<>();

	/**
	 * 是否已初始化：{@code true} 表示 {@link #getSaJsonAllowTypeList()} 至少执行过一次。
	 * 初始化后不可再 {@link #registerAllowType(Class)}；可通过 {@link #resetState()} 重置为 false（不清空 {@link #customTypes}）。
	 */
	private volatile boolean isInit;

	/**
	 * 注册允许反序列化的类型（初始化前有效）。
	 * <p> 注册 {@link Object} 时会在 {@code System.err} 输出郑重警告（等同于放开多态反序列化类型限制）。 </p>
	 *
	 * @param type 允许参与多态反序列化的类型
	 * @throws SaTokenException 已初始化后再次注册，或 {@code type} 为 {@code null} 时
	 */
	public void registerAllowType(Class<?> type) {
		synchronized (this) {
			if (isInit) {
				throw new SaTokenException("SaJsonStrategy 已初始化，无法再注册允许反序列化的类型：" + type.getName())
						.setCode(SaErrorCode.CODE_10005);
			}
			if (type == null) {
				throw new SaTokenException("registerAllowType 参数 type 不可以是 null").setCode(SaErrorCode.CODE_10005);
			}
			if (type == Object.class) {
				System.err.println("[Sa-Token 安全警告] registerAllowType(Object.class) 将放开 JSON 多态反序列化的类型限制，");
				System.err.println("[Sa-Token 安全警告] @class / @type 等类型标记可被用来实例化 classpath 上任意类型；");
				System.err.println("[Sa-Token 安全警告] 请确保持久化层与 JSON 数据来源均不可被攻击者篡改，生产环境强烈不建议注册 Object.class。");
			}
			customTypes.add(type);
		}
	}

	/**
	 * 是否已初始化
	 *
	 * @return {@code true} 表示 {@link #getSaJsonAllowTypeList()} 至少执行过一次，不可再 {@link #registerAllowType(Class)}
	 */
	public boolean isInit() {
		return isInit;
	}

	/**
	 * 获取允许反序列化的类型列表（每次调用返回一份新的合并快照；初始化后禁止再 register）。
	 *
	 * @return 合并 JDK 内置类型、{@link SaJsonType} 标记、SPI 声明及自定义注册后的类型列表（去重，保持合并顺序）
	 */
	public List<Class<?>> getSaJsonAllowTypeList() {
		synchronized (this) {
			isInit = true;
			LinkedHashSet<Class<?>> merged = new LinkedHashSet<>();
			merged.addAll(getJdkAllowTypeList());
			merged.addAll(getSaJsonTypeMarkerList());
			merged.addAll(loadSpiAllowTypeList());
			merged.addAll(getCustomAllowTypeList());
			return new ArrayList<>(merged);
		}
	}

	/**
	 * 1、JDK 内置允许类型
	 * <p>
	 * 含集合结构锚点（{@link Map}、{@link Collection}），以及实体 / Session 中常见的 JDK 值类型：
	 * 旧版日期（{@link Date}、{@link Calendar}）、{@code java.time}、金额（{@link BigDecimal}）、{@link UUID} 等。
	 * </p>
	 *
	 * @return JDK 内置允许类型列表
	 */
	public List<Class<?>> getJdkAllowTypeList() {
		List<Class<?>> list = new ArrayList<>(24);
		// 集合结构锚点
		list.add(Map.class);
		list.add(Collection.class);
		// 旧版日期（non-final，DefaultTyping 会写入 @class）
		list.add(Date.class);
		list.add(Calendar.class);
		// java.time（Java 8+，实体字段常见）
		list.add(LocalDateTime.class);
		list.add(LocalDate.class);
		list.add(LocalTime.class);
		list.add(Instant.class);
		list.add(ZonedDateTime.class);
		list.add(OffsetDateTime.class);
		list.add(OffsetTime.class);
		list.add(YearMonth.class);
		list.add(Year.class);
		list.add(MonthDay.class);
		list.add(Duration.class);
		list.add(Period.class);
		// 数值 / 金额
		list.add(BigDecimal.class);
		list.add(BigInteger.class);
		// 其它常见值类型
		list.add(UUID.class);
		list.add(Enum.class);
		return list;
	}

	/**
	 * 2、SPI 文件声明的类型
	 *
	 * @return {@link #SPI_FILE} 中声明的类型列表（空行与 {@code #} 开头行会被忽略）
	 * @throws SaTokenException SPI 资源读取失败，或类名无法加载时
	 */
	public List<Class<?>> loadSpiAllowTypeList() {
		List<Class<?>> list = new ArrayList<>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try {
			Enumeration<URL> resources = classLoader.getResources(SPI_FILE);
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				try (InputStream is = url.openStream();
					 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
					String line;
					while ((line = reader.readLine()) != null) {
						line = line.trim();
						if (line.isEmpty() || line.startsWith("#")) {
							continue;
						}
						Class<?> clazz = Class.forName(line, true, classLoader);
						list.add(clazz);
					}
				}
			}
		} catch (Exception e) {
			throw new SaTokenException("加载 sa-json-type.list 失败: " + e.getMessage(), e)
					.setCode(SaErrorCode.CODE_10005);
		}
		return list;
	}

	/**
	 * 3、{@link SaJsonType} 标记接口（允许所有实现类）
	 *
	 * @return 仅包含 {@link SaJsonType} 的类型列表
	 */
	public List<Class<?>> getSaJsonTypeMarkerList() {
		return Collections.singletonList(SaJsonType.class);
	}

	/**
	 * 4、用户自定义注册的类型
	 *
	 * @return 通过 {@link #registerAllowType(Class)} 注册的类型列表（注册顺序，不含重复）
	 */
	public List<Class<?>> getCustomAllowTypeList() {
		return new ArrayList<>(customTypes);
	}

	/**
	 * 重置初始化状态
	 */
	public void resetState() {
		isInit = false;
	}

}
