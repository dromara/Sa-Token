package cn.dev33.satoken.listener;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.SaLoginModel;

/**
 * Sa-Token 侦听器实现：Log 打印 
 * 
 * @author kong
 * @since 2022-11-2
 */
public class SaTokenListenerForLog implements SaTokenListener {

	/**
	 * 每次登录时触发 
	 */
	@Override
	public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
		SaManager.getLogInput().doLogin(loginType, loginId, tokenValue, loginModel);
	}

	/**
	 * 每次注销时触发 
	 */
	@Override
	public void doLogout(String loginType, Object loginId, String tokenValue) {
		SaManager.getLogInput().doLogout(loginType, loginId, tokenValue);
	}

	/**
	 * 每次被踢下线时触发
	 */
	@Override
	public void doKickout(String loginType, Object loginId, String tokenValue) {
		SaManager.getLogInput().doKickout(loginType, loginId, tokenValue);
	}

	/**
	 * 每次被顶下线时触发
	 */
	@Override
	public void doReplaced(String loginType, Object loginId, String tokenValue) {
		SaManager.getLogInput().doReplaced(loginType, loginId, tokenValue);
	}

	/**
	 * 每次被封禁时触发
	 */
	@Override
	public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
		SaManager.getLogInput().doDisable(loginType, loginId, service, level, disableTime);
	}

	/**
	 * 每次被解封时触发
	 */
	@Override
	public void doUntieDisable(String loginType, Object loginId, String service) {
		SaManager.getLogInput().doUntieDisable(loginType, loginId, service);
	}
	
	/**
	 * 每次打开二级认证时触发
	 */
	@Override
	public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
		SaManager.getLogInput().doOpenSafe(loginType, tokenValue, service, safeTime);
	}

	/**
	 * 每次关闭二级认证时触发
	 */
	@Override
	public void doCloseSafe(String loginType, String tokenValue, String service) {
		SaManager.getLogInput().doCloseSafe(loginType, tokenValue, service);
	}

	/**
	 * 每次创建Session时触发
	 */
	@Override
	public void doCreateSession(String id) {
		SaManager.getLogInput().doCreateSession(id);
	}

	/**
	 * 每次注销Session时触发
	 */
	@Override
	public void doLogoutSession(String id) {
		SaManager.getLogInput().doLogoutSession(id);
	}

	/**
	 * 每次Token续期时触发
	 */
	@Override
	public void doRenewTimeout(String tokenValue, Object loginId, long timeout) {
		SaManager.getLogInput().doRenewTimeout(tokenValue, loginId, timeout);
	}

	
}
