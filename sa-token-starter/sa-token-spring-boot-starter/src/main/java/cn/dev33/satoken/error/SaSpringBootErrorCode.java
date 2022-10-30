package cn.dev33.satoken.error;

/**
 * 定义 sa-token-spring-boot-starter 所有异常细分状态码 
 * 
 * @author kong
 * @since: 2022-10-30 
 */
public interface SaSpringBootErrorCode {
	
	/** 企图在非 Web 上下文获取 Request、Response 等对象 */
	public static final int CODE_20101 = 20101;

	/** 对象转 JSON 字符串失败 */
	public static final int CODE_20103 = 20103;

	/** JSON 字符串转 Map 失败 */
	public static final int CODE_20104 = 20104;

	/** 默认的 Filter 异常处理函数 */
	public static final int CODE_20105 = 20105;

}
