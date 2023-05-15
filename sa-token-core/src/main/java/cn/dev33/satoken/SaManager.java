/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.config.SaTokenConfigFactory;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.context.SaTokenContextDefaultImpl;
import cn.dev33.satoken.context.second.SaTokenSecondContext;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.json.SaJsonTemplateDefaultImpl;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.log.SaLog;
import cn.dev33.satoken.log.SaLogForConsole;
import cn.dev33.satoken.same.SaSameTemplate;
import cn.dev33.satoken.sign.SaSignTemplate;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpInterfaceDefaultImpl;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempDefaultImpl;
import cn.dev33.satoken.temp.SaTempInterface;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 管理 Sa-Token 所有全局组件，可通过此类快速获取、写入各种全局组件对象
 *
 * @author click33
 * @since <= 1.34.0
 */
public class SaManager {

	/**
	 * 全局配置对象
	 */
	public volatile static SaTokenConfig config;	
	public static void setConfig(SaTokenConfig config) {
		setConfigMethod(config);
		
		// 打印 banner 
		if(config !=null && config.getIsPrint()) {
			SaFoxUtil.printSaToken();
		}

		// 如果此 config 对象没有配置 isColorLog 的值，则框架为它自动判断一下
		if(config != null && config.getIsLog() != null && config.getIsLog() && config.getIsColorLog() == null) {
			config.setIsColorLog(SaFoxUtil.isCanColorLog());
		}

		// $$ 全局事件 
		SaTokenEventCenter.doSetConfig(config);
		
		// 调用一次 StpUtil 中的方法，保证其可以尽早的初始化 StpLogic
		StpUtil.getLoginType();
	}
	private static void setConfigMethod(SaTokenConfig config) {
		SaManager.config = config;
	}

	/**
	 * 获取 Sa-Token 的全局配置信息
	 * @return
	 */
	public static SaTokenConfig getConfig() {
		if (config == null) {
			synchronized (SaManager.class) {
				if (config == null) {
					setConfigMethod(SaTokenConfigFactory.createConfig());
				}
			}
		}
		return config;
	}
	
	/**
	 * 持久化组件
	 */
	private volatile static SaTokenDao saTokenDao;
	public static void setSaTokenDao(SaTokenDao saTokenDao) {
		setSaTokenDaoMethod(saTokenDao);
		SaTokenEventCenter.doRegisterComponent("SaTokenDao", saTokenDao);
	}
	private static void setSaTokenDaoMethod(SaTokenDao saTokenDao) {
		if((SaManager.saTokenDao instanceof SaTokenDaoDefaultImpl)) {
			((SaTokenDaoDefaultImpl)SaManager.saTokenDao).endRefreshThread();
		}
		SaManager.saTokenDao = saTokenDao;
	}
	public static SaTokenDao getSaTokenDao() {
		if (saTokenDao == null) {
			synchronized (SaManager.class) {
				if (saTokenDao == null) {
					setSaTokenDaoMethod(new SaTokenDaoDefaultImpl());
				}
			}
		}
		return saTokenDao;
	}
	
	/**
	 * 权限数据源组件
	 */
	private volatile static StpInterface stpInterface;
	public static void setStpInterface(StpInterface stpInterface) {
		SaManager.stpInterface = stpInterface;
		SaTokenEventCenter.doRegisterComponent("StpInterface", stpInterface);
	}
	public static StpInterface getStpInterface() {
		if (stpInterface == null) {
			synchronized (SaManager.class) {
				if (stpInterface == null) {
					SaManager.stpInterface = new StpInterfaceDefaultImpl();
				}
			}
		}
		return stpInterface;
	}
	
	/**
	 * 一级上下文 SaTokenContextContext
	 */
	private volatile static SaTokenContext saTokenContext;
	public static void setSaTokenContext(SaTokenContext saTokenContext) {
		SaManager.saTokenContext = saTokenContext;
		SaTokenEventCenter.doRegisterComponent("SaTokenContext", saTokenContext);
	}
	public static SaTokenContext getSaTokenContext() {
		return saTokenContext;
	}
	
	/**
	 * 二级上下文 SaTokenSecondContext
	 */
	private volatile static SaTokenSecondContext saTokenSecondContext;
	public static void setSaTokenSecondContext(SaTokenSecondContext saTokenSecondContext) {
		SaManager.saTokenSecondContext = saTokenSecondContext;
		SaTokenEventCenter.doRegisterComponent("SaTokenSecondContext", saTokenSecondContext);
	}
	public static SaTokenSecondContext getSaTokenSecondContext() {
		return saTokenSecondContext;
	}
	
	/**
	 * 获取一个可用的 SaTokenContext （按照一级上下文、二级上下文、默认上下文的顺序来判断）
	 * @return / 
	 */
	public static SaTokenContext getSaTokenContextOrSecond() {
		
		// s1. 一级Context可用时返回一级Context
		if(saTokenContext != null) {
			if(saTokenSecondContext == null || saTokenContext.isValid()) {
				// 因为 isValid 是一个耗时操作，所以此处假定：二级Context为null的情况下无需验证一级Context有效性 
				// 这样可以提升6倍左右的上下文获取速度 
				return saTokenContext;
			}
		}
		
		// s2. 一级Context不可用时判断二级Context是否可用 
		if(saTokenSecondContext != null && saTokenSecondContext.isValid()) {
			return saTokenSecondContext;
		}
		
		// s3. 都不行，就返回默认的 Context 
		return SaTokenContextDefaultImpl.defaultContext; 
	}

	/**
	 * 临时 token 认证模块
	 */
	private volatile static SaTempInterface saTemp;
	public static void setSaTemp(SaTempInterface saTemp) {
		SaManager.saTemp = saTemp;
		SaTokenEventCenter.doRegisterComponent("SaTempInterface", saTemp);
	}
	public static SaTempInterface getSaTemp() {
		if (saTemp == null) {
			synchronized (SaManager.class) {
				if (saTemp == null) {
					SaManager.saTemp = new SaTempDefaultImpl();
				}
			}
		}
		return saTemp;
	}

	/**
	 * JSON 转换器
	 */
	private volatile static SaJsonTemplate saJsonTemplate;
	public static void setSaJsonTemplate(SaJsonTemplate saJsonTemplate) {
		SaManager.saJsonTemplate = saJsonTemplate;
		SaTokenEventCenter.doRegisterComponent("SaJsonTemplate", saJsonTemplate);
	}
	public static SaJsonTemplate getSaJsonTemplate() {
		if (saJsonTemplate == null) {
			synchronized (SaManager.class) {
				if (saJsonTemplate == null) {
					SaManager.saJsonTemplate = new SaJsonTemplateDefaultImpl();
				}
			}
		}
		return saJsonTemplate;
	}

	/**
	 * API 参数签名
	 */
	private volatile static SaSignTemplate saSignTemplate;
	public static void setSaSignTemplate(SaSignTemplate saSignTemplate) {
		SaManager.saSignTemplate = saSignTemplate;
		SaTokenEventCenter.doRegisterComponent("SaSignTemplate", saSignTemplate);
	}
	public static SaSignTemplate getSaSignTemplate() {
		if (saSignTemplate == null) {
			synchronized (SaManager.class) {
				if (saSignTemplate == null) {
					SaManager.saSignTemplate = new SaSignTemplate();
				}
			}
		}
		return saSignTemplate;
	}

	/**
	 * Same-Token 同源系统认证模块
	 */
	private volatile static SaSameTemplate saSameTemplate;
	public static void setSaSameTemplate(SaSameTemplate saSameTemplate) {
		SaManager.saSameTemplate = saSameTemplate;
		SaTokenEventCenter.doRegisterComponent("SaSameTemplate", saSameTemplate);
	}
	public static SaSameTemplate getSaSameTemplate() {
		if (saSameTemplate == null) {
			synchronized (SaManager.class) {
				if (saSameTemplate == null) {
					SaManager.saSameTemplate = new SaSameTemplate();
				}
			}
		}
		return saSameTemplate;
	}

	/**
	 * 日志输出器 
	 */
	public volatile static SaLog log = new SaLogForConsole();
	public static void setLog(SaLog log) {
		SaManager.log = log;
		SaTokenEventCenter.doRegisterComponent("SaLog", log);
	}
	public static SaLog getLog() {
		return SaManager.log;
	}
	
	/**
	 * StpLogic 集合, 记录框架所有成功初始化的 StpLogic
	 */
	public static Map<String, StpLogic> stpLogicMap = new LinkedHashMap<String, StpLogic>();
	
	/**
	 * 向全局集合中 put 一个 StpLogic 
	 * @param stpLogic StpLogic
	 */
	public static void putStpLogic(StpLogic stpLogic) {
		stpLogicMap.put(stpLogic.getLoginType(), stpLogic);
	}

	/**
	 * 根据 LoginType 获取对应的StpLogic，如果不存在则新建并返回 
	 * @param loginType 对应的账号类型 
	 * @return 对应的StpLogic
	 */
	public static StpLogic getStpLogic(String loginType) {
		return getStpLogic(loginType, true);
	}
	
	/**
	 * 根据 LoginType 获取对应的StpLogic，如果不存在，isCreate = 是否自动创建并返回
	 * @param loginType 对应的账号类型 
	 * @param isCreate 在 StpLogic 不存在时，true=新建并返回，false=抛出异常
	 * @return 对应的StpLogic
	 */
	public static StpLogic getStpLogic(String loginType, boolean isCreate) {
		// 如果type为空则返回框架默认内置的 
		if(loginType == null || loginType.isEmpty()) {
			return StpUtil.stpLogic;
		}
		
		// 从集合中获取 
		StpLogic stpLogic = stpLogicMap.get(loginType);
		if(stpLogic == null) {
			
			// isCreate=true时，自创建模式：自动创建并返回 
			if(isCreate) {
				synchronized (SaManager.class) {
					stpLogic = stpLogicMap.get(loginType);
					if(stpLogic == null) {
						stpLogic = new StpLogic(loginType);
						// 此处无需手动put，因为 StpLogic 构造方法中会自动put 
						// putStpLogic(stpLogic); 
					}
				}
			} 
			// isCreate=false时，严格校验模式：抛出异常 
			else {
				/*
				 * 此时有两种情况会造成 StpLogic == null 
				 * 1. loginType拼写错误，请改正 （建议使用常量） 
				 * 2. 自定义StpUtil尚未初始化（静态类中的属性至少一次调用后才会初始化），解决方法两种
				 * 		(1) 从main方法里调用一次
				 * 		(2) 在自定义StpUtil类加上类似 @Component 的注解让容器启动时扫描到自动初始化 
				 */
				throw new SaTokenException("未能获取对应StpLogic，type="+ loginType).setCode(SaErrorCode.CODE_10002);
			}
		}
		
		// 返回 
		return stpLogic;
	}
	
}
