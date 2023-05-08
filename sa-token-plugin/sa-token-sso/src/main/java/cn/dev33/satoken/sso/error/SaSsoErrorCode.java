package cn.dev33.satoken.sso.error;

/**
 * 定义 sa-token-sso 所有异常细分状态码 
 * 
 * @author click33
 * @since: 2022-10-31
 */
public interface SaSsoErrorCode {

	/** redirect 重定向 url 是一个无效地址 */
	public static final int CODE_30001 = 30001;

	/** redirect 重定向 url 不在 allowUrl 允许的范围内 */
	public static final int CODE_30002 = 30002;

	/** 接口调用方提供的 secretkey 秘钥无效 */
	public static final int CODE_30003 = 30003;

	/** 提供的 ticket 是无效的 */
	public static final int CODE_30004 = 30004;

	/** 在模式三下，sso-client 调用 sso-server 端 校验ticket接口 时，得到的响应是校验失败 */
	public static final int CODE_30005 = 30005;

	/** 在模式三下，sso-client 调用 sso-server 端 单点注销接口 时，得到的响应是注销失败 */
	public static final int CODE_30006 = 30006;

	/** http 请求调用 提供的 timestamp 与当前时间的差距超出允许的范围 */
	public static final int CODE_30007 = 30007;

	/** http 请求调用 提供的 sign 无效 */
	public static final int CODE_30008 = 30008;

	/** 本地系统没有配置 secretkey 字段 */
	public static final int CODE_30009 = 30009;

	/** 本地系统没有配置 http 请求处理器 */
	public static final int CODE_30010 = 30010;

	/** 该 ticket 不属于当前 client */
	public static final int CODE_30011 = 30011;

	/** 当前缺少配置 server-url 地址 */
	public static final int CODE_30012 = 30012;

}
