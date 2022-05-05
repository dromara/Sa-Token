package cn.dev33.satoken.jwt.exception;

/**
 * 定义所有 JWT 异常细分状态码 
 * 
 * @author kong
 * @since: 2022-5-1 
 */
public class SaJwtExceptionCode {

	/** 对 jwt 字符串解析失败 */
	public static final int CODE_40101 = 40101;

	/** 此 jwt 的签名无效 */
	public static final int CODE_40102 = 40102;

	/** 此 jwt 的 loginType 字段不符合预期 */
	public static final int CODE_40103 = 40103;

	/** 此 jwt 已超时 */
	public static final int CODE_40104 = 40104;

}
