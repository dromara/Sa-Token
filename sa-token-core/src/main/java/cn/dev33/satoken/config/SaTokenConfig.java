package cn.dev33.satoken.config;

import java.io.Serializable;

import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Sa-Token 配置类 Model 
 * <p>
 * 你可以通过yml、properties、java代码等形式配置本类参数，具体请查阅官方文档: https://sa-token.cc/
 * 
 * @author kong
 *
 */
public class SaTokenConfig implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;

	/** token名称 (同时也是cookie名称) */
	private String tokenName = "satoken";

	/** token的长久有效期(单位:秒) 默认30天, -1代表永久 */
	private long timeout = 60 * 60 * 24 * 30;

	/**
	 * token临时有效期 [指定时间内无操作就视为token过期] (单位: 秒), 默认-1 代表不限制
	 * (例如可以设置为1800代表30分钟内无操作就过期)
	 */
	private long activityTimeout = -1;

	/** 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录) */
	private Boolean isConcurrent = true;

	/** 在多人登录同一账号时，是否共用一个token (为true时所有登录共用一个token, 为false时每次登录新建一个token) */
	private Boolean isShare = true;

	/**
	 * 同一账号最大登录数量，-1代表不限 （只有在 isConcurrent=true, isShare=false 时此配置才有效）
	 */
	private int maxLoginCount = 12;

	/** 是否尝试从请求体里读取token */
	private Boolean isReadBody = true;

	/** 是否尝试从header里读取token */
	private Boolean isReadHeader = true;

	/** 是否尝试从cookie里读取token */
	private Boolean isReadCookie = true;

	/** 是否在登录后将 Token 写入到响应头 */
	private Boolean isWriteHeader = false;
	
	/** token风格(默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik) */
	private String tokenStyle = "uuid";

	/** 默认dao层实现类中，每次清理过期数据间隔的时间 (单位: 秒) ，默认值30秒，设置为-1代表不启动定时清理 */
	private int dataRefreshPeriod = 30;

	/** 获取[token专属session]时是否必须登录 (如果配置为true，会在每次获取[token-session]时校验是否登录) */
	private Boolean tokenSessionCheckLogin = true;

	/** 是否打开自动续签 (如果此值为true, 框架会在每次直接或间接调用getLoginId()时进行一次过期检查与续签操作)  */
	private Boolean autoRenew = true;

	/** token前缀, 格式样例(satoken: Bearer xxxx-xxxx-xxxx-xxxx) */
	private String tokenPrefix;

	/** 是否在初始化配置时打印版本字符画 */
	private Boolean isPrint = true;

	/** 是否打印操作日志 */
	private Boolean isLog = false;

	/** 日志等级（trace、debug、info、warn、error、fatal） */
	private String logLevel = "trace";

	/** 日志等级 int 值（1=trace、2=debug、3=info、4=warn、5=error、6=fatal） */
	private int logLevelInt = 1;


	/**
	 * jwt秘钥 (只有集成 jwt 模块时此参数才会生效)   
	 */
	private String jwtSecretKey;

	/**
	 * Http Basic 认证的账号和密码 
	 */
	private String basic = "";

	/** 配置当前项目的网络访问地址 */
	private String currDomain;

	/**
	 * Same-Token 的有效期 (单位: 秒)
	 */
	private long sameTokenTimeout = 60 * 60 * 24;

	/** 是否校验Same-Token（部分rpc插件有效） */
	private Boolean checkSameToken = false;


	/**
	 * Cookie配置对象 
	 */
	public SaCookieConfig cookie = new SaCookieConfig();
	

	/**
	 * @return token名称 (同时也是cookie名称)
	 */
	public String getTokenName() {
		return tokenName;
	}

	/**
	 * @param tokenName token名称 (同时也是cookie名称)
	 * @return 对象自身
	 */
	public SaTokenConfig setTokenName(String tokenName) {
		this.tokenName = tokenName;
		return this;
	}

	/**
	 * @return token的长久有效期(单位:秒) 默认30天, -1代表永久
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout token的长久有效期(单位:秒) 默认30天, -1代表永久
	 * @return 对象自身
	 */
	public SaTokenConfig setTimeout(long timeout) {
		this.timeout = timeout;
		return this;
	}

	/**
	 * @return token临时有效期 [指定时间内无操作就视为token过期] (单位: 秒), 默认-1 代表不限制
	 *         (例如可以设置为1800代表30分钟内无操作就过期)
	 */
	public long getActivityTimeout() {
		return activityTimeout;
	}

	/**
	 * @param activityTimeout token临时有效期 [指定时间内无操作就视为token过期] (单位: 秒), 默认-1 代表不限制
	 *                        (例如可以设置为1800代表30分钟内无操作就过期)
	 * @return 对象自身
	 */
	public SaTokenConfig setActivityTimeout(long activityTimeout) {
		this.activityTimeout = activityTimeout;
		return this;
	}

	/**
	 * @return 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录) 
	 */
	public Boolean getIsConcurrent() {
		return isConcurrent;
	}

	/**
	 * @param isConcurrent 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录)
	 * @return 对象自身
	 */
	public SaTokenConfig setIsConcurrent(Boolean isConcurrent) {
		this.isConcurrent = isConcurrent;
		return this;
	}

	/**
	 * @return 在多人登录同一账号时，是否共用一个token (为true时所有登录共用一个token, 为false时每次登录新建一个token)
	 */
	public Boolean getIsShare() {
		return isShare;
	}

	/**
	 * @param isShare 在多人登录同一账号时，是否共用一个token (为true时所有登录共用一个token, 为false时每次登录新建一个token)
	 * @return 对象自身
	 */
	public SaTokenConfig setIsShare(Boolean isShare) {
		this.isShare = isShare;
		return this;
	}

	/**
	 * @return 同一账号最大登录数量，-1代表不限 （只有在 isConcurrent=true, isShare=false 时此配置才有效）
	 */
	public int getMaxLoginCount() {
		return maxLoginCount;
	}

	/**
	 * @param maxLoginCount 同一账号最大登录数量，-1代表不限 （只有在 isConcurrent=true, isShare=false 时此配置才有效）
	 * @return 对象自身 
	 */
	public SaTokenConfig setMaxLoginCount(int maxLoginCount) {
		this.maxLoginCount = maxLoginCount;
		return this;
	}

	/**
	 * @return 是否尝试从请求体里读取token
	 */
	public Boolean getIsReadBody() {
		return isReadBody;
	}

	/**
	 * @param isReadBody 是否尝试从请求体里读取token
	 * @return 对象自身
	 */
	public SaTokenConfig setIsReadBody(Boolean isReadBody) {
		this.isReadBody = isReadBody;
		return this;
	}

	/**
	 * @return 是否尝试从header里读取token
	 */
	public Boolean getIsReadHeader() {
		return isReadHeader;
	}

	/**
	 * @param isReadHeader 是否尝试从header里读取token
	 * @return 对象自身
	 */
	public SaTokenConfig setIsReadHeader(Boolean isReadHeader) {
		this.isReadHeader = isReadHeader;
		return this;
	}

	/**
	 * @return 是否尝试从cookie里读取token
	 */
	public Boolean getIsReadCookie() {
		return isReadCookie;
	}

	/**
	 * @param isReadCookie 是否尝试从cookie里读取token
	 * @return 对象自身
	 */
	public SaTokenConfig setIsReadCookie(Boolean isReadCookie) {
		this.isReadCookie = isReadCookie;
		return this;
	}

	/**
	 * @return 是否在登录后将 Token 写入到响应头
	 */
	public Boolean getIsWriteHeader() {
		return isWriteHeader;
	}

	/**
	 * @param isWriteHeader 是否在登录后将 Token 写入到响应头
	 * @return 对象自身
	 */
	public SaTokenConfig setIsWriteHeader(Boolean isWriteHeader) {
		this.isWriteHeader = isWriteHeader;
		return this;
	}

	/**
	 * @return token风格(默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik)
	 */
	public String getTokenStyle() {
		return tokenStyle;
	}

	/**
	 * @param tokenStyle token风格(默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik)
	 * @return 对象自身
	 */
	public SaTokenConfig setTokenStyle(String tokenStyle) {
		this.tokenStyle = tokenStyle;
		return this;
	}

	/**
	 * @return 默认dao层实现类中，每次清理过期数据间隔的时间 (单位: 秒) ，默认值30秒，设置为-1代表不启动定时清理
	 */
	public int getDataRefreshPeriod() {
		return dataRefreshPeriod;
	}

	/**
	 * @param dataRefreshPeriod 默认dao层实现类中，每次清理过期数据间隔的时间 (单位: 秒)
	 *                          ，默认值30秒，设置为-1代表不启动定时清理
	 * @return 对象自身
	 */
	public SaTokenConfig setDataRefreshPeriod(int dataRefreshPeriod) {
		this.dataRefreshPeriod = dataRefreshPeriod;
		return this;
	}

	/**
	 * @return 获取[token专属session]时是否必须登录 (如果配置为true，会在每次获取[token-session]时校验是否登录)
	 */
	public Boolean getTokenSessionCheckLogin() {
		return tokenSessionCheckLogin;
	}

	/**
	 * @param tokenSessionCheckLogin 获取[token专属session]时是否必须登录
	 *                               (如果配置为true，会在每次获取[token-session]时校验是否登录)
	 * @return 对象自身
	 */
	public SaTokenConfig setTokenSessionCheckLogin(Boolean tokenSessionCheckLogin) {
		this.tokenSessionCheckLogin = tokenSessionCheckLogin;
		return this;
	}

	/**
	 * @return 是否打开了自动续签 (如果此值为true, 框架会在每次直接或间接调用getLoginId()时进行一次过期检查与续签操作) 
	 */
	public Boolean getAutoRenew() {
		return autoRenew;
	}

	/**
	 * @param autoRenew 是否打开自动续签 (如果此值为true, 框架会在每次直接或间接调用getLoginId()时进行一次过期检查与续签操作) 
	 * @return 对象自身
	 */
	public SaTokenConfig setAutoRenew(Boolean autoRenew) {
		this.autoRenew = autoRenew;
		return this;
	}

	/**
	 * @return token前缀, 格式样例(satoken: Bearer xxxx-xxxx-xxxx-xxxx)
	 */
	public String getTokenPrefix() {
		return tokenPrefix;
	}

	/**
	 * @param tokenPrefix token前缀, 格式样例(satoken: Bearer xxxx-xxxx-xxxx-xxxx)
	 * @return 对象自身
	 */
	public SaTokenConfig setTokenPrefix(String tokenPrefix) {
		this.tokenPrefix = tokenPrefix;
		return this;
	}
	
	/**
	 * @return 是否在初始化配置时打印版本字符画
	 */
	public Boolean getIsPrint() {
		return isPrint;
	}

	/**
	 * @param isPrint 是否在初始化配置时打印版本字符画
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
	 * @return 日志等级（trace、debug、info、warn、error、fatal）
	 */
	public String getLogLevel() {
		return logLevel;
	}

	/**
	 * @param logLevel 日志等级（trace、debug、info、warn、error、fatal）
	 * @return 对象自身
	 */
	public SaTokenConfig setLogLevel(String logLevel) {
		this.logLevel = logLevel;
		this.logLevelInt = SaFoxUtil.translateLogLevelToInt(logLevel);
		return this;
	}

	/**
	 * @return 日志等级 int 值（1=trace、2=debug、3=info、4=warn、5=error、6=fatal）
	 */
	public int getLogLevelInt() {
		return logLevelInt;
	}

	/**
	 * @param logLevelInt 日志等级 int 值（1=trace、2=debug、3=info、4=warn、5=error、6=fatal）
	 * @return 对象自身
	 */
	public SaTokenConfig setLogLeveInt(int logLevelInt) {
		this.logLevelInt = logLevelInt;
		this.logLevel = SaFoxUtil.translateLogLevelToString(logLevelInt);
		return this;
	}
	
	/**
	 * @return jwt秘钥 (只有集成 jwt 模块时此参数才会生效)    
	 */
	public String getJwtSecretKey() {
		return jwtSecretKey;
	}

	/**
	 * @param jwtSecretKey jwt秘钥 (只有集成 jwt 模块时此参数才会生效)  
	 * @return 对象自身
	 */
	public SaTokenConfig setJwtSecretKey(String jwtSecretKey) {
		this.jwtSecretKey = jwtSecretKey;
		return this;
	}

	/**
	 * @return Http Basic 认证的账号和密码 
	 */
	public String getBasic() {
		return basic;
	}

	/**
	 * @param basic Http Basic 认证的账号和密码 
	 * @return 对象自身
	 */
	public SaTokenConfig setBasic(String basic) {
		this.basic = basic;
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
	
	@Override
	public String toString() {
		return "SaTokenConfig ["
				+ "tokenName=" + tokenName 
				+ ", timeout=" + timeout 
				+ ", activityTimeout=" + activityTimeout
				+ ", isConcurrent=" + isConcurrent 
				+ ", isShare=" + isShare 
				+ ", maxLoginCount=" + maxLoginCount 
				+ ", isReadBody=" + isReadBody
				+ ", isReadHeader=" + isReadHeader 
				+ ", isReadCookie=" + isReadCookie
				+ ", isWriteHeader=" + isWriteHeader
				+ ", tokenStyle=" + tokenStyle
				+ ", dataRefreshPeriod=" + dataRefreshPeriod 
				+ ", tokenSessionCheckLogin=" + tokenSessionCheckLogin
				+ ", autoRenew=" + autoRenew 
				+ ", tokenPrefix=" + tokenPrefix
				+ ", isPrint=" + isPrint 
				+ ", isLog=" + isLog 
				+ ", logLevel=" + logLevel 
				+ ", logLevelInt=" + logLevelInt
				+ ", jwtSecretKey=" + jwtSecretKey 
				+ ", basic=" + basic 
				+ ", currDomain=" + currDomain 
				+ ", sameTokenTimeout=" + sameTokenTimeout
				+ ", checkSameToken=" + checkSameToken 
				+ ", cookie=" + cookie 
				+ "]";
	}

	
	/**
	 * <h1> 本函数设计已过时，未来版本可能移除此函数，请及时更换为 getIsReadHeader() ，使用方式保持不变 </h1>
	 * @return 是否尝试从header里读取token
	 */
	@Deprecated
	public Boolean getIsReadHead() {
		return isReadHeader;
	}

	/**
	 * <h1> 本函数设计已过时，未来版本可能移除此函数，请及时更换为 setIsReadHeader() ，使用方式保持不变 </h1>
	 * @param isReadHead 是否尝试从header里读取token
	 * @return 对象自身
	 */
	@Deprecated
	public SaTokenConfig setIsReadHead(Boolean isReadHead) {
		this.isReadHeader = isReadHead;
		return this;
	}

	/**
	 * <h1> 本函数设计已过时，未来版本可能移除此函数，请及时更换为 getSameTokenTimeout() ，使用方式保持不变 </h1>
	 * @return Id-Token的有效期 (单位: 秒)
	 */
	@Deprecated
	public long getIdTokenTimeout() {
		return sameTokenTimeout;
	}

	/**
	 * <h1> 本函数设计已过时，未来版本可能移除此函数，请及时更换为 setSameTokenTimeout() ，使用方式保持不变 </h1>
	 * @param idTokenTimeout Id-Token的有效期 (单位: 秒)
	 * @return 对象自身
	 */
	@Deprecated
	public SaTokenConfig setIdTokenTimeout(long idTokenTimeout) {
		this.sameTokenTimeout = idTokenTimeout;
		return this;
	}

	/**
	 * <h1> 本函数设计已过时，未来版本可能移除此函数，请及时更换为 getCheckSameToken() ，使用方式保持不变 </h1>
	 * @return 是否校验Id-Token（部分rpc插件有效）
	 */
	@Deprecated
	public Boolean getCheckIdToken() {
		return checkSameToken;
	}

	/**
	 * <h1> 本函数设计已过时，未来版本可能移除此函数，请及时更换为 setCheckSameToken() ，使用方式保持不变 </h1>
	 * @param checkIdToken 是否校验Id-Token（部分rpc插件有效）
	 * @return 对象自身 
	 */
	@Deprecated
	public SaTokenConfig setCheckIdToken(Boolean checkIdToken) {
		this.checkSameToken = checkIdToken;
		return this;
	}

}
