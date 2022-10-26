package cn.dev33.satoken.sso.name;

/**
 * SSO 模块所有参数名称定义 
 * 
 * @author kong
 * @since 2022-10-25
 */
public class ParamName {

	/** redirect参数名称 */
	public String redirect = "redirect";
	
	/** ticket参数名称 */
	public String ticket = "ticket";

	/** back参数名称 */
	public String back = "back";

	/** mode参数名称 */
	public String mode = "mode";
	
	/** loginId参数名称 */
	public String loginId = "loginId";

	/** secretkey参数名称 */
	public String secretkey = "secretkey";
	
	/** Client端单点注销时-回调URL 参数名称 */
	public String ssoLogoutCall = "ssoLogoutCall";

	public String name = "name";
	public String pwd = "pwd";
	
	public String timestamp = "timestamp";
	public String nonce = "nonce";
	public String sign = "sign";

}
