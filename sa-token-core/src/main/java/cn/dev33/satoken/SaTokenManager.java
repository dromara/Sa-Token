package cn.dev33.satoken;

import cn.dev33.satoken.action.SaTokenAction;
import cn.dev33.satoken.action.SaTokenActionDefaultImpl;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.config.SaTokenConfigFactory;
import cn.dev33.satoken.cookie.SaTokenCookie;
import cn.dev33.satoken.cookie.SaTokenCookieDefaultImpl;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.servlet.SaTokenServlet;
import cn.dev33.satoken.servlet.SaTokenServletDefaultImpl;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpInterfaceDefaultImpl;
import cn.dev33.satoken.util.SaTokenInsideUtil;

/**
 * 管理sa-token所有接口对象 
 * @author kong
 *
 */
public class SaTokenManager {

	
	/**
	 * 配置文件 Bean 
	 */
	private static SaTokenConfig config;	
	public static SaTokenConfig getConfig() {
		if (config == null) {
			initConfig();
		}
		return config;
	}
	public static void setConfig(SaTokenConfig config) {
		SaTokenManager.config = config;
		if(config.getIsV()) {
			SaTokenInsideUtil.printSaToken();
		}
	}
	public synchronized static void initConfig() {
		if (config == null) {
			setConfig(SaTokenConfigFactory.createConfig());
		}
	}
	
	/**
	 * 持久化 Bean 
	 */
	public static SaTokenDao saTokenDao;
	public static SaTokenDao getSaTokenDao() {
		if (saTokenDao == null) {
			initSaTokenDao();
		}
		return saTokenDao;
	}
	public static void setSaTokenDao(SaTokenDao saTokenDao) {
		if(SaTokenManager.saTokenDao != null && (SaTokenManager.saTokenDao instanceof SaTokenDaoDefaultImpl)) {
			((SaTokenDaoDefaultImpl)SaTokenManager.saTokenDao).endRefreshTimer();
		}
		SaTokenManager.saTokenDao = saTokenDao;
	}
	public synchronized static void initSaTokenDao() {
		if (saTokenDao == null) {
			setSaTokenDao(new SaTokenDaoDefaultImpl());
		}
	}
	
	/**
	 * 权限认证 Bean 
	 */
	public static StpInterface stpInterface;
	public static StpInterface getStpInterface() {
		if (stpInterface == null) {
			initStpInterface();
		}
		return stpInterface;
	}
	public static void setStpInterface(StpInterface stpInterface) {
		SaTokenManager.stpInterface = stpInterface;
	}
	public synchronized static void initStpInterface() {
		if (stpInterface == null) {
			setStpInterface(new StpInterfaceDefaultImpl());
		}
	}
	
	/**
	 * 框架行为 Bean 
	 */
	public static SaTokenAction saTokenAction;
	public static SaTokenAction getSaTokenAction() {
		if (saTokenAction == null) {
			initSaTokenAction();
		}
		return saTokenAction;
	}
	public static void setSaTokenAction(SaTokenAction saTokenAction) {
		SaTokenManager.saTokenAction = saTokenAction;
	}
	public synchronized static void initSaTokenAction() {
		if (saTokenAction == null) {
			setSaTokenAction(new SaTokenActionDefaultImpl());
		}
	}

	/**
	 * Cookie操作 Bean 
	 */
	public static SaTokenCookie saTokenCookie;
	public static SaTokenCookie getSaTokenCookie() {
		if (saTokenCookie == null) {
			initSaTokenCookie();
		}
		return saTokenCookie;
	}
	public static void setSaTokenCookie(SaTokenCookie saTokenCookie) {
		SaTokenManager.saTokenCookie = saTokenCookie;
	}
	public synchronized static void initSaTokenCookie() {
		if (saTokenCookie == null) {
			setSaTokenCookie(new SaTokenCookieDefaultImpl());
		}
	}
	
	/**
	 * Servlet操作 Bean 
	 */
	public static SaTokenServlet saTokenServlet;
	public static SaTokenServlet getSaTokenServlet() {
		if (saTokenServlet == null) {
			initSaTokenServlet();
		}
		return saTokenServlet;
	}
	public static void setSaTokenServlet(SaTokenServlet saTokenServlet) {
		SaTokenManager.saTokenServlet = saTokenServlet;
	}
	public synchronized static void initSaTokenServlet() {
		if (saTokenServlet == null) {
			setSaTokenServlet(new SaTokenServletDefaultImpl());
		}
	}
	
	
	
	
	
	
	
	
}
