package cn.dev33.satoken.reactor.error;

/**
 * 定义 sa-token-reactor3-spring-boot-starter 所有异常细分状态码
 * 
 * @author click33
 * @since <= 1.34.0
 */
public interface SaReactorSpringBootErrorCode {
	
	/** 对象转 JSON 字符串失败 */
	int CODE_20203 = 20203;

	/** JSON 字符串转 Map 失败 */
	int CODE_20204 = 20204;

	/** 默认的 Filter 异常处理函数 */
	int CODE_20205 = 20205;

}
