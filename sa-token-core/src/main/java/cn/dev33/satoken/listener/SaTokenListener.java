package cn.dev33.satoken.listener;

import cn.dev33.satoken.stp.SaLoginModel;

/**
 * Sa-Token的侦听器
 * <p> 你可以通过实现此接口在用户登陆、退出等关键性操作时进行一些AOP操作 
 * @author kong
 *
 */
public interface SaTokenListener {

	/**
	 * 每次登录时触发 
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param loginModel 登录参数
	 */
	public void doLogin(String loginType, Object loginId, SaLoginModel loginModel);
			
	/**
	 * 每次注销时触发 
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param tokenValue token值
	 */
	public void doLogout(String loginType, Object loginId, String tokenValue);
	
	/**
	 * 每次被踢下线时触发
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param tokenValue token值
	 * @param device 设备标识 
	 */
	public void doLogoutByLoginId(String loginType, Object loginId, String tokenValue, String device);

	/**
	 * 每次被顶下线时触发
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param tokenValue token值
	 * @param device 设备标识 
	 */
	public void doReplaced(String loginType, Object loginId, String tokenValue, String device);

	/**
	 * 每次被封禁时触发
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param disableTime 封禁时长，单位: 秒
	 */
	public void doDisable(String loginType, Object loginId, long disableTime);
	
	/**
	 * 每次被解封时触发
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 */
	public void doUntieDisable(String loginType, Object loginId);
	
	/**
	 * 每次创建Session时触发
	 * @param id SessionId
	 */
	public void doCreateSession(String id);
	
	/**
	 * 每次注销Session时触发
	 * @param id SessionId
	 */
	public void doLogoutSession(String id);
	
}
