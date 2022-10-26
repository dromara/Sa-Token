package cn.dev33.satoken.sso.name;

/**
 * SSO 模块所有 API 路由名称定义 
 * 
 * @author kong
 * @since 2022-10-25
 */
public class ApiName {

	/** SSO-Server端：授权地址 */ 
	public String ssoAuth = "/sso/auth";

	/** SSO-Server端：RestAPI 登录接口 */ 
	public String ssoDoLogin = "/sso/doLogin";

	/** SSO-Server端：校验ticket 获取账号id */ 
	public String ssoCheckTicket = "/sso/checkTicket";

	/** SSO-Server端：获取userinfo  */ 
	public String ssoUserinfo = "/sso/userinfo";

	/** SSO-Server端：单点注销地址 */ 
	public String ssoSignout = "/sso/signout";

	/** SSO-Client端：登录地址 */ 
	public String ssoLogin = "/sso/login";

	/** SSO-Client端：单点注销地址 */ 
	public String ssoLogout = "/sso/logout";

	/** SSO-Client端：单点注销的回调 */ 
	public String ssoLogoutCall = "/sso/logoutCall";
	
	/**
	 * 批量修改path，新增固定前缀 
	 * @param prefix 示例值：/sso-user、/sso-admin
	 * @return 对象自身 
	 */
	public ApiName addPrefix(String prefix) {
		this.ssoAuth = prefix + this.ssoAuth;
		this.ssoDoLogin = prefix + this.ssoDoLogin;
		this.ssoCheckTicket = prefix + this.ssoCheckTicket;
		this.ssoUserinfo = prefix + this.ssoUserinfo;
		this.ssoSignout  = prefix + this.ssoSignout;
		this.ssoLogin = prefix + this.ssoLogin;
		this.ssoLogout = prefix + this.ssoLogout;
		this.ssoLogoutCall = prefix + this.ssoLogoutCall;
		return this;
	}
	
	/**
	 * 批量修改path，替换掉 /sso 固定前缀 
	 * @param prefix 示例值：/sso-user、/sso-admin
	 * @return 对象自身 
	 */
	public ApiName replacePrefix(String prefix) {
		String oldPrefix = "/sso";
		this.ssoAuth = this.ssoAuth.replaceFirst(oldPrefix, prefix);
		this.ssoDoLogin = this.ssoDoLogin.replaceFirst(oldPrefix, prefix);
		this.ssoCheckTicket = this.ssoCheckTicket.replaceFirst(oldPrefix, prefix);
		this.ssoUserinfo = this.ssoUserinfo.replaceFirst(oldPrefix, prefix);
		this.ssoSignout = this.ssoSignout.replaceFirst(oldPrefix, prefix);
		this.ssoLogin = this.ssoLogin.replaceFirst(oldPrefix, prefix);
		this.ssoLogout = this.ssoLogout.replaceFirst(oldPrefix, prefix);
		this.ssoLogoutCall = this.ssoLogoutCall.replaceFirst(oldPrefix, prefix);
		return this;
	}

	
	@Override
	public String toString() {
		return "ApiName [ssoAuth=" + ssoAuth + ", ssoDoLogin=" + ssoDoLogin + ", ssoCheckTicket=" + ssoCheckTicket
				+ ", ssoUserinfo=" + ssoUserinfo + ", ssoSignout=" + ssoSignout + ", ssoLogin=" + ssoLogin
				+ ", ssoLogout=" + ssoLogout + ", ssoLogoutCall=" + ssoLogoutCall + "]";
	}

	
}
