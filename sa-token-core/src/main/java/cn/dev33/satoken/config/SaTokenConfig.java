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
package cn.dev33.satoken.config;

import cn.dev33.satoken.stp.parameter.enums.SaLogoutMode;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutRange;
import cn.dev33.satoken.stp.parameter.enums.SaReplacedRange;
import cn.dev33.satoken.util.SaFoxUtil;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sa-Token 配置类 Model
 *
 * <p>
 *     你可以通过yml、properties、java代码等形式配置本类参数，具体请查阅官方文档:
 *     <a href="https://sa-token.cc">https://sa-token.cc</a>
 * </p>
 *
 * @author click33
 * @since 1.10.0
 */
public class SaTokenConfig implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;

	/** token 名称 （同时也是： cookie 名称、提交 token 时参数的名称、存储 token 时的 key 前缀） */
	private String tokenName = "satoken";

	/** token 有效期（单位：秒） 默认30天，-1 代表永久有效 */
	private long timeout = 60 * 60 * 24 * 30;

	/**
	 * token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
	 * （例如可以设置为 1800 代表 30 分钟内无操作就冻结）
	 */
	private long activeTimeout = -1;

	/**
	 * 是否启用动态 activeTimeout 功能，如不需要请设置为 false，节省缓存请求次数
	 */
	private Boolean dynamicActiveTimeout = false;

	/**
	 * 是否允许同一账号多地同时登录 （为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
	 */
	private Boolean isConcurrent = true;

	/**
	 * 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token, 为 false 时每次登录新建一个 token）
	 */
	private Boolean isShare = false;

	/**
	 * 当 isConcurrent=false 时，顶人下线的范围 (CURR_DEVICE_TYPE=当前指定的设备类型端, ALL_DEVICE_TYPE=所有设备类型端)
	 */
	private SaReplacedRange replacedRange = SaReplacedRange.CURR_DEVICE_TYPE;

	/**
	 * 同一账号最大登录数量，-1代表不限 （只有在 isConcurrent=true, isShare=false 时此配置项才有意义）
	 */
	private int maxLoginCount = 12;

	/**
	 * 溢出 maxLoginCount 的客户端，将以何种方式注销下线 (LOGOUT=注销下线, KICKOUT=踢人下线, REPLACED=顶人下线)
	 */
	private SaLogoutMode overflowLogoutMode = SaLogoutMode.LOGOUT;

	/**
	 * 在每次创建 token 时的最高循环次数，用于保证 token 唯一性（-1=不循环尝试，直接使用）
	 */
	private int maxTryTimes = 12;

	/**
	 * 是否尝试从请求体里读取 token
	 */
	private Boolean isReadBody = true;

	/**
	 * 是否尝试从 header 里读取 token
	 */
	private Boolean isReadHeader = true;

	/**
	 * 是否尝试从 cookie 里读取 token
	 */
	private Boolean isReadCookie = true;

	/**
	 * 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
	 */
	private Boolean isLastingCookie = true;

	/**
	 * 是否在登录后将 token 写入到响应头
	 */
	private Boolean isWriteHeader = false;

	/**
	 * 注销范围 (TOKEN=只注销当前 token 的会话，ACCOUNT=注销当前 token 指向的 loginId 其所有客户端会话)
	 * <br/> (此参数只在调用 StpUtil.logout() 时有效)
	 */
	private SaLogoutRange logoutRange = SaLogoutRange.TOKEN;

	/**
	 * 如果 token 已被冻结，是否保留其操作权 (是否允许此 token 调用注销API)
	 * <br/> (此参数只在调用 StpUtil.[logout/kickout/replaced]ByTokenValue("token") 时有效)
	 */
	private Boolean isLogoutKeepFreezeOps = false;

	/**
	 * 在注销 token 后，是否保留其对应的 Token-Session
	 */
	private Boolean isLogoutKeepTokenSession = false;

	/**
	 * 在登录时，是否立即创建对应的 Token-Session （true=在登录时立即创建，false=在第一次调用 getTokenSession() 时创建）
	 */
	private Boolean rightNowCreateTokenSession = false;

	/**
	 * token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
	 */
	private String tokenStyle = "uuid";

	/**
	 * 默认 SaTokenDao 实现类中，每次清理过期数据间隔的时间（单位: 秒），默认值30秒，设置为 -1 代表不启动定时清理
	 */
	private int dataRefreshPeriod = 30;

	/**
	 * 获取 Token-Session 时是否必须登录（如果配置为true，会在每次获取 getTokenSession() 时校验当前是否登录）
	 */
	private Boolean tokenSessionCheckLogin = true;

	/**
	 * 是否打开自动续签 activeTimeout （如果此值为 true, 框架会在每次直接或间接调用 getLoginId() 时进行一次过期检查与续签操作）
	 */
	private Boolean autoRenew = true;

	/**
	 * token 前缀, 前端提交 token 时应该填写的固定前缀，格式样例(satoken: Bearer xxxx-xxxx-xxxx-xxxx)
	 */
	private String tokenPrefix;

	/**
	 * cookie 模式是否自动填充 token 前缀
	 */
	private Boolean cookieAutoFillPrefix = false;

	/**
	 * 是否在初始化配置时在控制台打印版本字符画
	 */
	private Boolean isPrint = true;

	/**
	 * 是否打印操作日志
	 */
	private Boolean isLog = false;

	/**
	 * 日志等级（trace、debug、info、warn、error、fatal），此值与 logLevelInt 联动
	 */
	private String logLevel = "trace";

	/**
	 * 日志等级 int 值（1=trace、2=debug、3=info、4=warn、5=error、6=fatal），此值与 logLevel 联动
	 */
	private int logLevelInt = 1;

	/**
	 * 是否打印彩色日志
	 */
	private Boolean isColorLog = null;

	/**
	 * jwt秘钥（只有集成 jwt 相关模块时此参数才会生效）
	 */
	private String jwtSecretKey;

	/**
	 * Http Basic 认证的默认账号和密码，冒号隔开，例如：sa:123456
	 */
	private String httpBasic = "";

	/**
	 * Http Digest 认证的默认账号和密码，冒号隔开，例如：sa:123456
	 */
	private String httpDigest = "";

	/**
	 * 配置当前项目的网络访问地址
	 */
	private String currDomain;

	/**
	 * Same-Token 的有效期 (单位: 秒)
	 */
	private long sameTokenTimeout = 60 * 60 * 24;

	/**
	 * 是否校验 Same-Token（部分rpc插件有效）
	 */
	private Boolean checkSameToken = false;

	/**
	 * Cookie配置对象 
	 */
	public SaCookieConfig cookie = new SaCookieConfig();

	/**
	 * API 签名配置对象
	 */
	public SaSignConfig sign = new SaSignConfig();

	/**
	 * API 签名配置 多实例
	 */
	public Map<String, SaSignConfig> signMany = new LinkedHashMap<>();

	/**
	 * API Key 相关配置
	 */
	public SaApiKeyConfig apiKey = new SaApiKeyConfig();

	/**
	 * @return token 名称 （同时也是： cookie 名称、提交 token 时参数的名称、存储 token 时的 key 前缀）
	 */
	public String getTokenName() {
		return tokenName;
	}

	/**
	 * @param tokenName token 名称 （同时也是： cookie 名称、提交 token 时参数的名称、存储 token 时的 key 前缀）
	 * @return 对象自身
	 */
	public SaTokenConfig setTokenName(String tokenName) {
		this.tokenName = tokenName;
		return this;
	}

	/**
	 * @return token 有效期（单位：秒） 默认30天，-1 代表永久有效
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout token 有效期（单位：秒） 默认30天，-1 代表永久有效
	 * @return 对象自身
	 */
	public SaTokenConfig setTimeout(long timeout) {
		this.timeout = timeout;
		return this;
	}

	/**
	 * @return token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
	 * 							（例如可以设置为 1800 代表 30 分钟内无操作就冻结）
	 */
	public long getActiveTimeout() {
		return activeTimeout;
	}

	/**
	 * @param activeTimeout token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
	 * 								（例如可以设置为 1800 代表 30 分钟内无操作就冻结）
	 * @return 对象自身
	 */
	public SaTokenConfig setActiveTimeout(long activeTimeout) {
		this.activeTimeout = activeTimeout;
		return this;
	}

	/**
	 * @return 是否启用动态 activeTimeout 功能，如不需要请设置为 false，节省缓存请求次数
	 */
	public Boolean getDynamicActiveTimeout() {
		return dynamicActiveTimeout;
	}

	/**
	 * @param dynamicActiveTimeout 是否启用动态 activeTimeout 功能，如不需要请设置为 false，节省缓存请求次数
	 * @return 对象自身
	 */
	public SaTokenConfig setDynamicActiveTimeout(Boolean dynamicActiveTimeout) {
		this.dynamicActiveTimeout = dynamicActiveTimeout;
		return this;
	}

	/**
	 * @return 是否允许同一账号多地同时登录 （为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
	 */
	public Boolean getIsConcurrent() {
		return isConcurrent;
	}

	/**
	 * @param isConcurrent 是否允许同一账号多地同时登录 （为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
	 * @return 对象自身
	 */
	public SaTokenConfig setIsConcurrent(Boolean isConcurrent) {
		this.isConcurrent = isConcurrent;
		return this;
	}

	/**
	 * @return 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个token, 为 false 时每次登录新建一个 token）
	 */
	public Boolean getIsShare() {
		return isShare;
	}

	/**
	 * @param isShare 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个token, 为 false 时每次登录新建一个 token）
	 * @return 对象自身
	 */
	public SaTokenConfig setIsShare(Boolean isShare) {
		this.isShare = isShare;
		return this;
	}

	/**
	 * @return 同一账号最大登录数量，-1代表不限 （只有在 isConcurrent=true, isShare=false 时此配置项才有意义）
	 */
	public int getMaxLoginCount() {
		return maxLoginCount;
	}

	/**
	 * @param maxLoginCount 同一账号最大登录数量，-1代表不限 （只有在 isConcurrent=true, isShare=false 时此配置项才有意义）
	 * @return 对象自身 
	 */
	public SaTokenConfig setMaxLoginCount(int maxLoginCount) {
		this.maxLoginCount = maxLoginCount;
		return this;
	}

	/**
	 * @return 在每次创建 token 时的最高循环次数，用于保证 token 唯一性（-1=不循环尝试，直接使用）
	 */
	public int getMaxTryTimes() {
		return maxTryTimes;
	}

	/**
	 * @param maxTryTimes 在每次创建 token 时的最高循环次数，用于保证 token 唯一性（-1=不循环尝试，直接使用）
	 * @return 对象自身
	 */
	public SaTokenConfig setMaxTryTimes(int maxTryTimes) {
		this.maxTryTimes = maxTryTimes;
		return this;
	}

	/**
	 * @return 是否尝试从请求体里读取 token
	 */
	public Boolean getIsReadBody() {
		return isReadBody;
	}

	/**
	 * @param isReadBody 是否尝试从请求体里读取 token
	 * @return 对象自身
	 */
	public SaTokenConfig setIsReadBody(Boolean isReadBody) {
		this.isReadBody = isReadBody;
		return this;
	}

	/**
	 * @return 是否尝试从 header 里读取 token
	 */
	public Boolean getIsReadHeader() {
		return isReadHeader;
	}

	/**
	 * @param isReadHeader 是否尝试从 header 里读取 token
	 * @return 对象自身
	 */
	public SaTokenConfig setIsReadHeader(Boolean isReadHeader) {
		this.isReadHeader = isReadHeader;
		return this;
	}

	/**
	 * @return 是否尝试从 cookie 里读取 token
	 */
	public Boolean getIsReadCookie() {
		return isReadCookie;
	}

	/**
	 * @param isReadCookie 是否尝试从 cookie 里读取 token
	 * @return 对象自身
	 */
	public SaTokenConfig setIsReadCookie(Boolean isReadCookie) {
		this.isReadCookie = isReadCookie;
		return this;
	}

	/**
	 * 获取 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
	 *
	 * @return isLastingCookie /
	 */
	public Boolean getIsLastingCookie() {
		return this.isLastingCookie;
	}

	/**
	 * 设置 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
	 *
	 * @param isLastingCookie /
	 * @return 对象自身
	 */
	public SaTokenConfig setIsLastingCookie(Boolean isLastingCookie) {
		this.isLastingCookie = isLastingCookie;
		return this;
	}

	/**
	 * @return 是否在登录后将 token 写入到响应头
	 */
	public Boolean getIsWriteHeader() {
		return isWriteHeader;
	}

	/**
	 * @param isWriteHeader 是否在登录后将 token 写入到响应头
	 * @return 对象自身
	 */
	public SaTokenConfig setIsWriteHeader(Boolean isWriteHeader) {
		this.isWriteHeader = isWriteHeader;
		return this;
	}

	/**
	 * @return token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
	 */
	public String getTokenStyle() {
		return tokenStyle;
	}

	/**
	 * @param tokenStyle token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
	 * @return 对象自身
	 */
	public SaTokenConfig setTokenStyle(String tokenStyle) {
		this.tokenStyle = tokenStyle;
		return this;
	}

	/**
	 * @return 默认 SaTokenDao 实现类中，每次清理过期数据间隔的时间（单位: 秒），默认值30秒，设置为 -1 代表不启动定时清理
	 */
	public int getDataRefreshPeriod() {
		return dataRefreshPeriod;
	}

	/**
	 * @param dataRefreshPeriod 默认 SaTokenDao 实现类中，每次清理过期数据间隔的时间（单位: 秒），默认值30秒，设置为 -1 代表不启动定时清理
	 * @return 对象自身
	 */
	public SaTokenConfig setDataRefreshPeriod(int dataRefreshPeriod) {
		this.dataRefreshPeriod = dataRefreshPeriod;
		return this;
	}

	/**
	 * @return 获取 Token-Session 时是否必须登录（如果配置为true，会在每次获取 getTokenSession() 时校验当前是否登录）
	 */
	public Boolean getTokenSessionCheckLogin() {
		return tokenSessionCheckLogin;
	}

	/**
	 * @param tokenSessionCheckLogin 获取 Token-Session 时是否必须登录（如果配置为true，会在每次获取 getTokenSession() 时校验当前是否登录）
	 * @return 对象自身
	 */
	public SaTokenConfig setTokenSessionCheckLogin(Boolean tokenSessionCheckLogin) {
		this.tokenSessionCheckLogin = tokenSessionCheckLogin;
		return this;
	}

	/**
	 * @return 是否打开自动续签 activeTimeout （如果此值为 true, 框架会在每次直接或间接调用 getLoginId() 时进行一次过期检查与续签操作）
	 */
	public Boolean getAutoRenew() {
		return autoRenew;
	}

	/**
	 * @param autoRenew 是否打开自动续签 activeTimeout （如果此值为 true, 框架会在每次直接或间接调用 getLoginId() 时进行一次过期检查与续签操作）
	 * @return 对象自身
	 */
	public SaTokenConfig setAutoRenew(Boolean autoRenew) {
		this.autoRenew = autoRenew;
		return this;
	}

	/**
	 * @return token 前缀, 前端提交 token 时应该填写的固定前缀，格式样例(satoken: Bearer xxxx-xxxx-xxxx-xxxx)
	 */
	public String getTokenPrefix() {
		return tokenPrefix;
	}

	/**
	 * @param tokenPrefix token 前缀, 前端提交 token 时应该填写的固定前缀，格式样例(satoken: Bearer xxxx-xxxx-xxxx-xxxx)
	 * @return 对象自身
	 */
	public SaTokenConfig setTokenPrefix(String tokenPrefix) {
		this.tokenPrefix = tokenPrefix;
		return this;
	}

	/**
	 * @return cookie 模式是否自动填充 token 前缀
	 */
	public Boolean getCookieAutoFillPrefix() {
		return cookieAutoFillPrefix;
	}

	/**
	 * @param cookieAutoFillPrefix cookie 模式是否自动填充 token 前缀
	 * @return 对象自身
	 */
	public SaTokenConfig setCookieAutoFillPrefix(Boolean cookieAutoFillPrefix) {
		this.cookieAutoFillPrefix = cookieAutoFillPrefix;
		return this;
	}

	/**
	 * @return 是否在初始化配置时在控制台打印版本字符画
	 */
	public Boolean getIsPrint() {
		return isPrint;
	}

	/**
	 * @param isPrint 是否在初始化配置时在控制台打印版本字符画
	 * @return 对象自身
	 */
	public SaTokenConfig setIsPrint(Boolean isPrint) {
		this.isPrint = isPrint;
		return this;
	}

	/**
	 * @return 是否打印操作日志
	 */
	public Boolean getIsLog() {
		return isLog;
	}

	/**
	 * @param isLog 是否打印操作日志
	 * @return 对象自身
	 */
	public SaTokenConfig setIsLog(Boolean isLog) {
		this.isLog = isLog;
		return this;
	}

	/**
	 * @return 日志等级（trace、debug、info、warn、error、fatal），此值与 logLevelInt 联动
	 */
	public String getLogLevel() {
		return logLevel;
	}

	/**
	 * @param logLevel 日志等级（trace、debug、info、warn、error、fatal），此值与 logLevelInt 联动
	 * @return 对象自身
	 */
	public SaTokenConfig setLogLevel(String logLevel) {
		this.logLevel = logLevel;
		this.logLevelInt = SaFoxUtil.translateLogLevelToInt(logLevel);
		return this;
	}

	/**
	 * @return 日志等级 int 值（1=trace、2=debug、3=info、4=warn、5=error、6=fatal），此值与 logLevel 联动
	 */
	public int getLogLevelInt() {
		return logLevelInt;
	}

	/**
	 * @param logLevelInt 日志等级 int 值（1=trace、2=debug、3=info、4=warn、5=error、6=fatal），此值与 logLevel 联动
	 * @return 对象自身
	 */
	public SaTokenConfig setLogLevelInt(int logLevelInt) {
		this.logLevelInt = logLevelInt;
		this.logLevel = SaFoxUtil.translateLogLevelToString(logLevelInt);
		return this;
	}

	/**
	 * 获取：是否打印彩色日志
	 *
	 * @return isColorLog 是否打印彩色日志
	 */
	public Boolean getIsColorLog() {
		return this.isColorLog;
	}

	/**
	 * 设置：是否打印彩色日志
	 *
	 * @param isColorLog 是否打印彩色日志
	 * @return 对象自身
	 */
	public SaTokenConfig setIsColorLog(Boolean isColorLog) {
		this.isColorLog = isColorLog;
		return this;
	}

	/**
	 * @return jwt秘钥（只有集成 jwt 相关模块时此参数才会生效）
	 */
	public String getJwtSecretKey() {
		return jwtSecretKey;
	}

	/**
	 * @param jwtSecretKey jwt秘钥（只有集成 jwt 相关模块时此参数才会生效）
	 * @return 对象自身
	 */
	public SaTokenConfig setJwtSecretKey(String jwtSecretKey) {
		this.jwtSecretKey = jwtSecretKey;
		return this;
	}

	/**
	 * @return Http Basic 认证的默认账号和密码，冒号隔开，例如：sa:123456
	 */
	public String getHttpBasic() {
		return httpBasic;
	}

	/**
	 * @param httpBasic Http Basic 认证的默认账号和密码，冒号隔开，例如：sa:123456
	 * @return 对象自身
	 */
	public SaTokenConfig setHttpBasic(String httpBasic) {
		this.httpBasic = httpBasic;
		return this;
	}

	/**
	 * @return Http Digest 认证的默认账号和密码，冒号隔开，例如：sa:123456
	 */
	public String getHttpDigest() {
		return httpDigest;
	}

	/**
	 * @param httpDigest Http Digest 认证的默认账号和密码，冒号隔开，例如：sa:123456
	 * @return 对象自身
	 */
	public SaTokenConfig setHttpDigest(String httpDigest) {
		this.httpDigest = httpDigest;
		return this;
	}

	/**
	 * @return 配置当前项目的网络访问地址
	 */
	public String getCurrDomain() {
		return currDomain;
	}

	/**
	 * @param currDomain 配置当前项目的网络访问地址
	 * @return 对象自身
	 */
	public SaTokenConfig setCurrDomain(String currDomain) {
		this.currDomain = currDomain;
		return this;
	}

	/**
	 * @return Same-Token 的有效期 (单位: 秒)
	 */
	public long getSameTokenTimeout() {
		return sameTokenTimeout;
	}

	/**
	 * @param sameTokenTimeout Same-Token 的有效期 (单位: 秒)
	 * @return 对象自身
	 */
	public SaTokenConfig setSameTokenTimeout(long sameTokenTimeout) {
		this.sameTokenTimeout = sameTokenTimeout;
		return this;
	}

	/**
	 * @return 是否校验Same-Token（部分rpc插件有效）
	 */
	public Boolean getCheckSameToken() {
		return checkSameToken;
	}

	/**
	 * @param checkSameToken 是否校验Same-Token（部分rpc插件有效）
	 * @return 对象自身
	 */
	public SaTokenConfig setCheckSameToken(Boolean checkSameToken) {
		this.checkSameToken = checkSameToken;
		return this;
	}

	/**
	 * 获取 当 isConcurrent=false 时，顶人下线的范围 (CURR_DEVICE_TYPE=当前指定的设备类型端 ALL_DEVICE_TYPE=所有设备类型端)
	 *
	 * @return /
	 */
	public SaReplacedRange getReplacedRange() {
		return this.replacedRange;
	}

	/**
	 * 设置 当 isConcurrent=false 时，顶人下线的范围 (CURR_DEVICE_TYPE=当前指定的设备类型端 ALL_DEVICE_TYPE=所有设备类型端)
	 *
	 * @param replacedRange /
	 * @return 对象自身
	 */
	public SaTokenConfig setReplacedRange(SaReplacedRange replacedRange) {
		this.replacedRange = replacedRange;
		return this;
	}

	/**
	 * 获取 溢出 maxLoginCount 的客户端，将以何种方式注销下线 (LOGOUT=注销下线, KICKOUT=踢人下线, REPLACED=顶人下线)
	 *
	 * @return /
	 */
	public SaLogoutMode getOverflowLogoutMode() {
		return this.overflowLogoutMode;
	}

	/**
	 * 设置 溢出 maxLoginCount 的客户端，将以何种方式注销下线 (LOGOUT=注销下线, KICKOUT=踢人下线, REPLACED=顶人下线)
	 *
	 * @param overflowLogoutMode /
	 * @return 对象自身
	 */
	public SaTokenConfig setOverflowLogoutMode(SaLogoutMode overflowLogoutMode) {
		this.overflowLogoutMode = overflowLogoutMode;
		return this;
	}

	/**
	 * 获取 注销范围 (TOKEN=只注销当前 token 的会话，ACCOUNT=注销当前 token 指向的 loginId 其所有客户端会话)  <br> (此参数只在调用 StpUtil.logout() 时有效)
	 *
	 * @return /
	 */
	public SaLogoutRange getLogoutRange() {
		return this.logoutRange;
	}

	/**
	 * 设置 注销范围 (TOKEN=只注销当前 token 的会话，ACCOUNT=注销当前 token 指向的 loginId 其所有客户端会话)  <br> (此参数只在调用 StpUtil.logout() 时有效)
	 *
	 * @param logoutRange /
	 * @return 对象自身
	 */
	public SaTokenConfig setLogoutRange(SaLogoutRange logoutRange) {
		this.logoutRange = logoutRange;
		return this;
	}

	/**
	 * 获取 如果 token 已被冻结，是否保留其操作权 (是否允许此 token 调用注销API)  <br> (此参数只在调用 StpUtil.[logoutkickoutreplaced]ByTokenValue("token") 时有效)
	 *
	 * @return isLogoutKeepFreezeOps /
	 */
	public Boolean getIsLogoutKeepFreezeOps() {
		return this.isLogoutKeepFreezeOps;
	}

	/**
	 * 设置 如果 token 已被冻结，是否保留其操作权 (是否允许此 token 调用注销API)  <br> (此参数只在调用 StpUtil.[logoutkickoutreplaced]ByTokenValue("token") 时有效)
	 *
	 * @param isLogoutKeepFreezeOps /
	 * @return 对象自身
	 */
	public SaTokenConfig setIsLogoutKeepFreezeOps(Boolean isLogoutKeepFreezeOps) {
		this.isLogoutKeepFreezeOps = isLogoutKeepFreezeOps;
		return this;
	}

	/**
	 * 获取 在注销 token 后，是否保留其对应的 Token-Session
	 *
	 * @return isLogoutKeepTokenSession /
	 */
	public Boolean getIsLogoutKeepTokenSession() {
		return this.isLogoutKeepTokenSession;
	}

	/**
	 * 设置 在注销 token 后，是否保留其对应的 Token-Session
	 *
	 * @param isLogoutKeepTokenSession /
	 * @return 对象自身
	 */
	public SaTokenConfig setIsLogoutKeepTokenSession(Boolean isLogoutKeepTokenSession) {
		this.isLogoutKeepTokenSession = isLogoutKeepTokenSession;
		return this;
	}

	/**
	 * 获取 在登录时，是否立即创建对应的 Token-Session （true=在登录时立即创建，false=在第一次调用 getTokenSession() 时创建）
	 *
	 * @return /
	 */
	public Boolean getRightNowCreateTokenSession() {
		return this.rightNowCreateTokenSession;
	}

	/**
	 * 设置 在登录时，是否立即创建对应的 Token-Session （true=在登录时立即创建，false=在第一次调用 getTokenSession() 时创建）
	 *
	 * @param rightNowCreateTokenSession /
	 * @return 对象自身
	 */
	public SaTokenConfig setRightNowCreateTokenSession(Boolean rightNowCreateTokenSession) {
		this.rightNowCreateTokenSession = rightNowCreateTokenSession;
		return this;
	}

	/**
	 * @return Cookie 全局配置对象
	 */
	public SaCookieConfig getCookie() {
		return cookie;
	}

	/**
	 * @param cookie Cookie 全局配置对象
	 * @return 对象自身 
	 */
	public SaTokenConfig setCookie(SaCookieConfig cookie) {
		this.cookie = cookie;
		return this;
	}

	/**
	 * @return API 签名全局配置对象
	 */
	public SaSignConfig getSign() {
		return sign;
	}

	/**
	 * @param sign API 签名全局配置对象
	 * @return 对象自身
	 */
	public SaTokenConfig setSign(SaSignConfig sign) {
		this.sign = sign;
		return this;
	}

	/**
	 * 获取 API 签名配置 多实例
	 *
	 * @return /
	 */
	public Map<String, SaSignConfig> getSignMany() {
		return this.signMany;
	}

	/**
	 * 设置 API 签名配置 多实例
	 *
	 * @param signMany /
	 * @return /
	 */
	public SaTokenConfig setSignMany(Map<String, SaSignConfig> signMany) {
		this.signMany = signMany;
		return this;
	}

	/**
	 * API Key 相关配置
	 *
	 * @return /
	 */
	public SaApiKeyConfig getApiKey() {
		return this.apiKey;
	}

	/**
	 * 设置 API Key 相关配置
	 *
	 * @param apiKey /
	 * @return /
	 */
	public SaTokenConfig setApiKey(SaApiKeyConfig apiKey) {
		this.apiKey = apiKey;
		return this;
	}


	@Override
	public String toString() {
		return "SaTokenConfig ["
				+ "tokenName=" + tokenName 
				+ ", timeout=" + timeout 
				+ ", activeTimeout=" + activeTimeout
				+ ", dynamicActiveTimeout=" + dynamicActiveTimeout
				+ ", isConcurrent=" + isConcurrent
				+ ", isShare=" + isShare
				+ ", replacedRange=" + replacedRange
				+ ", maxLoginCount=" + maxLoginCount
				+ ", overflowLogoutMode=" + overflowLogoutMode
				+ ", maxTryTimes=" + maxTryTimes
				+ ", isReadBody=" + isReadBody
				+ ", isReadHeader=" + isReadHeader 
				+ ", isReadCookie=" + isReadCookie
				+ ", isLastingCookie=" + isLastingCookie
				+ ", isWriteHeader=" + isWriteHeader
				+ ", logoutRange=" + logoutRange
				+ ", isLogoutKeepFreezeOps=" + isLogoutKeepFreezeOps
				+ ", isLogoutKeepTokenSession=" + isLogoutKeepTokenSession
				+ ", rightNowCreateTokenSession=" + rightNowCreateTokenSession
				+ ", tokenStyle=" + tokenStyle
				+ ", dataRefreshPeriod=" + dataRefreshPeriod 
				+ ", tokenSessionCheckLogin=" + tokenSessionCheckLogin
				+ ", autoRenew=" + autoRenew
				+ ", tokenPrefix=" + tokenPrefix
				+ ", cookieAutoFillPrefix=" + cookieAutoFillPrefix
				+ ", isPrint=" + isPrint 
				+ ", isLog=" + isLog 
				+ ", logLevel=" + logLevel 
				+ ", logLevelInt=" + logLevelInt
				+ ", isColorLog=" + isColorLog
				+ ", jwtSecretKey=" + jwtSecretKey 
				+ ", httpBasic=" + httpBasic
				+ ", httpDigest=" + httpDigest
				+ ", currDomain=" + currDomain 
				+ ", sameTokenTimeout=" + sameTokenTimeout
				+ ", checkSameToken=" + checkSameToken 
				+ ", cookie=" + cookie
				+ ", sign=" + sign
				+ ", signMany=" + signMany
				+ ", apiKey=" + apiKey
				+ "]";
	}



	// ------------------- 过期方法 -------------------

	/**
	 * <h2> 请更改为 getActiveTimeout() </h2>
	 * @return token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
	 * 							（例如可以设置为 1800 代表 30 分钟内无操作就冻结）
	 */
	@Deprecated
	public long getActivityTimeout() {
		System.err.println("配置项已过期，请更换：sa-token.activity-timeout -> sa-token.active-timeout");
		return activeTimeout;
	}

	/**
	 * <h2> 请更改为 setActiveTimeout() </h2>
	 * @param activityTimeout token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
	 * 								（例如可以设置为 1800 代表 30 分钟内无操作就冻结）
	 * @return 对象自身
	 */
	@Deprecated
	public SaTokenConfig setActivityTimeout(long activityTimeout) {
		System.err.println("配置项已过期，请更换：sa-token.activity-timeout -> sa-token.active-timeout");
		this.activeTimeout = activityTimeout;
		return this;
	}

	/**
	 * <h2> 请更改为 getHttpBasic() </h2>
	 * @return Http Basic 认证的默认账号和密码
	 */
	@Deprecated
	public String getBasic() {
		return httpBasic;
	}

	/**
	 * <h2> 请更改为 setHttpBasic() </h2>
	 * @param basic Http Basic 认证的默认账号和密码
	 * @return 对象自身
	 */
	@Deprecated
	public SaTokenConfig setBasic(String basic) {
		this.httpBasic = basic;
		return this;
	}

}
