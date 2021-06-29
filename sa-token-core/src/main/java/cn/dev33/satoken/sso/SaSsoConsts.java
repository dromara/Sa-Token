package cn.dev33.satoken.sso;

/**
 * Sa-Token-SSO模块相关常量
 * @author kong
 *
 */
public class SaSsoConsts {

	/** redirect参数名称 */
	public static final String REDIRECT_NAME = "redirect";
	
	/** ticket参数名称 */
	public static final String TICKET_NAME = "ticket";

	/** back参数名称 */
	public static final String BACK_NAME = "back";

	/** loginId参数名称 */
	public static final String LOGIN_ID_NAME = "loginId";

	/** secretkey参数名称 */
	public static final String SECRETKEY = "secretkey";
	
	/** Client端单点注销时-回调URL 参数名称 */
	public static final String SLO_CALLBACK_NAME = "sloCallback";

	/** Client端单点注销回调URL的Set集合，存储在Session中使用的key */
	public static final String SLO_CALLBACK_SET_KEY = "SLO_CALLBACK_SET_KEY_";
	
}
