package cn.dev33.satoken.listener;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Sa-Token 侦听器实现：控制台 log 打印 
 * @author kong
 *
 */
public class SaTokenListenerForConsolePrint implements SaTokenListener {

	/**
	 * 每次登录时触发 
	 */
	@Override
	public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
		println("账号[" + loginId + "]登录成功");
	}

	/**
	 * 每次注销时触发 
	 */
	@Override
	public void doLogout(String loginType, Object loginId, String tokenValue) {
		println("账号[" + loginId + "]注销成功 (Token=" + tokenValue + ")");
	}

	/**
	 * 每次被踢下线时触发
	 */
	@Override
	public void doKickout(String loginType, Object loginId, String tokenValue) {
		println("账号[" + loginId + "]被踢下线 (Token=" + tokenValue + ")");
	}

	/**
	 * 每次被顶下线时触发
	 */
	@Override
	public void doReplaced(String loginType, Object loginId, String tokenValue) {
		println("账号[" + loginId + "]被顶下线 (Token=" + tokenValue + ")");
	}

	/**
	 * 每次被封禁时触发
	 */
	@Override
	public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
		Instant instant = Instant.ofEpochMilli(System.currentTimeMillis() + disableTime * 1000);
		ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
		println("账号[" + loginId + "] " + service + " 服务被封禁，封禁等级=" + level + " (解封时间: " + SaFoxUtil.formatDate(zonedDateTime) + ")");
	}

	/**
	 * 每次被解封时触发
	 */
	@Override
	public void doUntieDisable(String loginType, Object loginId, String service) {
		println("账号[" + loginId + "] " + service + " 服务被解除封禁");
	}

	/**
	 * 每次创建Session时触发
	 */
	@Override
	public void doCreateSession(String id) {
		println("Session[" + id + "]创建成功");
	}

	/**
	 * 每次注销Session时触发
	 */
	@Override
	public void doLogoutSession(String id) {
		println("Session[" + id + "]注销成功");
	}

	/**
	 * 每次Token续期时触发
	 */
	@Override
	public void doRenewTimeout(String tokenValue, Object loginId, long timeout) {
		println("帐号[" + loginId + "]，Token=" + tokenValue + " 续期timeout成功!");
	}

	/**
	 * 日志输出的前缀
	 */
	public static final String LOG_PREFIX = "SaLog -->: ";
	
	/**
	 * 打印指定字符串
	 * @param str 字符串
	 */
	public void println(String str) {
		if(SaManager.getConfig().getIsLog()) {
			System.out.println(LOG_PREFIX + str);
		}
	}

}
