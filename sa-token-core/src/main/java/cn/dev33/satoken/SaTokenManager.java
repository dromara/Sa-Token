package cn.dev33.satoken;

import java.util.HashMap;
import java.util.Map;

import cn.dev33.satoken.action.SaTokenAction;
import cn.dev33.satoken.action.SaTokenActionDefaultImpl;
import cn.dev33.satoken.aop.SaTokenListener;
import cn.dev33.satoken.aop.SaTokenListenerDefaultImpl;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.config.SaTokenConfigFactory;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.context.SaTokenContextDefaultImpl;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpInterfaceDefaultImpl;
import cn.dev33.satoken.stp.StpLogic;
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
	public static void setConfig(SaTokenConfig config) {
		SaTokenManager.config = config;
		if(config.getIsV()) {
			SaTokenInsideUtil.printSaToken();
		}
	}
	public static SaTokenConfig getConfig() {
		if (config == null) {
			// 如果对象为空，则使用框架默认方式初始化 
			synchronized (SaTokenManager.class) {
				if (config == null) {
					setConfig(SaTokenConfigFactory.createConfig());
				}
			}
		}
		return config;
	}
	
	/**
	 * 持久化 Bean 
	 */
	private static SaTokenDao saTokenDao;
	public static void setSaTokenDao(SaTokenDao saTokenDao) {
		if(SaTokenManager.saTokenDao != null && (SaTokenManager.saTokenDao instanceof SaTokenDaoDefaultImpl)) {
			((SaTokenDaoDefaultImpl)SaTokenManager.saTokenDao).endRefreshThread();
		}
		SaTokenManager.saTokenDao = saTokenDao;
	}
	public static SaTokenDao getSaTokenDao() {
		if (saTokenDao == null) {
			// 如果对象为空，则使用框架默认方式初始化 
			synchronized (SaTokenManager.class) {
				if (saTokenDao == null) {
					setSaTokenDao(new SaTokenDaoDefaultImpl());
				}
			}
		}
		return saTokenDao;
	}
	
	/**
	 * 权限认证 Bean 
	 */
	private static StpInterface stpInterface;
	public static void setStpInterface(StpInterface stpInterface) {
		SaTokenManager.stpInterface = stpInterface;
	}
	public static StpInterface getStpInterface() {
		if (stpInterface == null) {
			// 如果对象为空，则使用框架默认方式初始化 
			synchronized (SaTokenManager.class) {
				if (stpInterface == null) {
					setStpInterface(new StpInterfaceDefaultImpl());
				}
			}
		}
		return stpInterface;
	}
	
	/**
	 * 框架行为 Bean 
	 */
	private static SaTokenAction saTokenAction;
	public static void setSaTokenAction(SaTokenAction saTokenAction) {
		SaTokenManager.saTokenAction = saTokenAction;
	}
	public static SaTokenAction getSaTokenAction() {
		if (saTokenAction == null) {
			// 如果对象为空，则使用框架默认方式初始化 
			synchronized (SaTokenManager.class) {
				if (saTokenAction == null) {
					setSaTokenAction(new SaTokenActionDefaultImpl());
				}
			}
		}
		return saTokenAction;
	}
	
	/**
	 * 容器操作 Bean  
	 */
	private static SaTokenContext saTokenContext;
	public static void setSaTokenContext(SaTokenContext saTokenContext) {
		SaTokenManager.saTokenContext = saTokenContext;
	}
	public static SaTokenContext getSaTokenContext() {
		if (saTokenContext == null) {
			// 如果对象为空，则使用框架默认方式初始化 
			synchronized (SaTokenManager.class) {
				if (saTokenContext == null) {
					setSaTokenContext(new SaTokenContextDefaultImpl());
				}
			}
		}
		return saTokenContext;
	}

	/**
	 * 监听器 Bean  
	 */
	private static SaTokenListener saTokenListener;
	public static void setSaTokenListener(SaTokenListener saTokenListener) {
		SaTokenManager.saTokenListener = saTokenListener;
	}
	public static SaTokenListener getSaTokenListener() {
		if (saTokenListener == null) {
			synchronized (SaTokenManager.class) {
				if (saTokenListener == null) {
					setSaTokenListener(new SaTokenListenerDefaultImpl());
				}
			}
		}
		return saTokenListener;
	}

	/**
	 * StpLogic集合, 记录框架所有成功初始化的StpLogic 
	 */
	public static Map<String, StpLogic> stpLogicMap = new HashMap<String, StpLogic>();
	
	/**
	 * 向集合中 put 一个 StpLogic 
	 * @param stpLogic StpLogic
	 */
	public static void putStpLogic(StpLogic stpLogic) {
		stpLogicMap.put(stpLogic.getLoginKey(), stpLogic);
	}

	/**
	 * 根据 LoginKey 获取对应的StpLogic，如果不存在则返回null 
	 * @param loginKey 对应的LoginKey 
	 * @return 对应的StpLogic
	 */
	public static StpLogic getStpLogic(String loginKey) {
		return stpLogicMap.get(loginKey);
	}
	
	
}
