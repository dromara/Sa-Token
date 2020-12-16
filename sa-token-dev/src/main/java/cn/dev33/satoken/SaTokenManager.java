package cn.dev33.satoken;

import cn.dev33.satoken.action.SaTokenAction;
import cn.dev33.satoken.action.SaTokenActionDefaultImpl;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.config.SaTokenConfigFactory;
import cn.dev33.satoken.cookie.SaCookieOper;
import cn.dev33.satoken.cookie.SaCookieOperDefaultImpl;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpInterfaceDefaultImpl;
import cn.dev33.satoken.util.SaTokenInsideUtil;

/**
 * 管理sa-token所有对象 
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
	public static SaTokenDao dao;
	public static SaTokenDao getDao() {
		if (dao == null) {
			initDao();
		}
		return dao;
	}
	public static void setDao(SaTokenDao dao) {
		SaTokenManager.dao = dao;
	}
	public synchronized static void initDao() {
		if (dao == null) {
			setDao(new SaTokenDaoDefaultImpl());
		}
	}
	
	/**
	 * 权限认证 Bean 
	 */
	public static StpInterface stp;
	public static StpInterface getStp() {
		if (stp == null) {
			initStp();
		}
		return stp;
	}
	public static void setStp(StpInterface stp) {
		SaTokenManager.stp = stp;
	}
	public synchronized static void initStp() {
		if (stp == null) {
			setStp(new StpInterfaceDefaultImpl());
		}
	}
	
	/**
	 * sa-token行为 Bean 
	 */
	public static SaTokenAction sta;
	public static SaTokenAction getSta() {
		if (sta == null) {
			initSta();
		}
		return sta;
	}
	public static void setSta(SaTokenAction sta) {
		SaTokenManager.sta = sta;
	}
	public synchronized static void initSta() {
		if (sta == null) {
			setSta(new SaTokenActionDefaultImpl());
		}
	}

	/**
	 * sa-token cookie操作 Bean 
	 */
	public static SaCookieOper saCookieOper;
	public static SaCookieOper getSaCookieOper() {
		if (saCookieOper == null) {
			initSaCookieOper();
		}
		return saCookieOper;
	}
	public static void setSaCookieOper(SaCookieOper saCookieOper) {
		SaTokenManager.saCookieOper = saCookieOper;
	}
	public synchronized static void initSaCookieOper() {
		if (saCookieOper == null) {
			setSaCookieOper(new SaCookieOperDefaultImpl());
		}
	}
	
	
	
	
	
	
	
	
}
