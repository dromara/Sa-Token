package cn.dev33.satoken.sso.exception;

/**
 * 定义所有 SSO 异常细分状态码 
 * 
 * @author kong
 * @date: 2022-4-25
 */
public class SaSsoExceptionCode {

	/** redirect 重定向 url 是一个无效地址 */
	public static final int CODE_20001 = 20001;

	/** redirect 重定向 url 不在 allowUrl 允许的范围内 */
	public static final int CODE_20002 = 20002;

	/** 接口调用方提供的 secretkey 秘钥无效 */
	public static final int CODE_20003 = 20003;

	/** 提供的 ticket 是无效的 */
	public static final int CODE_20004 = 20004;
	
	
	
}
