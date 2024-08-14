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
package cn.dev33.satoken.spring;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.handler.SaAnnotationAbstractHandler;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.context.second.SaTokenSecondContextCreator;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicTemplate;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestTemplate;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestUtil;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.log.SaLog;
import cn.dev33.satoken.same.SaSameTemplate;
import cn.dev33.satoken.sign.SaSignTemplate;
import cn.dev33.satoken.spring.pathmatch.SaPathMatcherHolder;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import cn.dev33.satoken.temp.SaTempInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.PathMatcher;

import java.util.List;

/**
 * 注入 Sa-Token 所需要的 Bean
 * 
 * @author click33
 * @since 1.34.0
 */
public class SaBeanInject {

	/**
	 * 组件注入 
	 * <p> 为确保 Log 组件正常打印，必须将 SaLog 和 SaTokenConfig 率先初始化 </p> 
	 * 
	 * @param log log 对象
	 * @param saTokenConfig 配置对象
	 */
	public SaBeanInject(
			@Autowired(required = false) SaLog log, 
			@Autowired(required = false) SaTokenConfig saTokenConfig
			){
		if(log != null) {
			SaManager.setLog(log);
		}
		if(saTokenConfig != null) {
			SaManager.setConfig(saTokenConfig);
		}
	}
	
	/**
	 * 注入持久化Bean
	 * 
	 * @param saTokenDao SaTokenDao对象 
	 */
	@Autowired(required = false)
	public void setSaTokenDao(SaTokenDao saTokenDao) {
		SaManager.setSaTokenDao(saTokenDao);
	}

	/**
	 * 注入权限认证Bean
	 * 
	 * @param stpInterface StpInterface对象 
	 */
	@Autowired(required = false)
	public void setStpInterface(StpInterface stpInterface) {
		SaManager.setStpInterface(stpInterface);
	}

	/**
	 * 注入上下文Bean
	 * 
	 * @param saTokenContext SaTokenContext对象 
	 */
	@Autowired(required = false)
	public void setSaTokenContext(SaTokenContext saTokenContext) {
		SaManager.setSaTokenContext(saTokenContext);
	}

	/**
	 * 注入二级上下文Bean
	 * 
	 * @param saTokenSecondContextCreator 二级上下文创建器 
	 */
	@Autowired(required = false)
	public void setSaTokenContext(SaTokenSecondContextCreator saTokenSecondContextCreator) {
		SaManager.setSaTokenSecondContext(saTokenSecondContextCreator.create());
	}

	/**
	 * 注入侦听器Bean
	 * 
	 * @param listenerList 侦听器集合 
	 */
	@Autowired(required = false)
	public void setSaTokenListener(List<SaTokenListener> listenerList) {
		SaTokenEventCenter.registerListenerList(listenerList);
	}

	/**
	 * 注入自定义注解处理器
	 *
	 * @param handlerList 自定义注解处理器集合
	 */
	@Autowired(required = false)
	public void setSaAnnotationHandler(List<SaAnnotationAbstractHandler<?>> handlerList) {
		for (SaAnnotationAbstractHandler<?> handler : handlerList) {
			SaAnnotationStrategy.instance.registerAnnotationHandler(handler);
		}
	}

	/**
	 * 注入临时令牌验证模块 Bean
	 * 
	 * @param saTemp saTemp对象 
	 */
	@Autowired(required = false)
	public void setSaTemp(SaTempInterface saTemp) {
		SaManager.setSaTemp(saTemp);
	}

	/**
	 * 注入 Same-Token 模块 Bean
	 * 
	 * @param saSameTemplate saSameTemplate对象 
	 */
	@Autowired(required = false)
	public void setSaIdTemplate(SaSameTemplate saSameTemplate) {
		SaManager.setSaSameTemplate(saSameTemplate);
	}

	/**
	 * 注入 Sa-Token Http Basic 认证模块 
	 * 
	 * @param saBasicTemplate saBasicTemplate对象 
	 */
	@Autowired(required = false)
	public void setSaHttpBasicTemplate(SaHttpBasicTemplate saBasicTemplate) {
		SaHttpBasicUtil.saHttpBasicTemplate = saBasicTemplate;
	}

	/**
	 * 注入 Sa-Token Http Digest 认证模块
	 *
	 * @param saHttpDigestTemplate saHttpDigestTemplate 对象
	 */
	@Autowired(required = false)
	public void setSaHttpDigestTemplate(SaHttpDigestTemplate saHttpDigestTemplate) {
		SaHttpDigestUtil.saHttpDigestTemplate = saHttpDigestTemplate;
	}

	/**
	 * 注入自定义的 JSON 转换器 Bean 
	 * 
	 * @param saJsonTemplate JSON 转换器 
	 */
	@Autowired(required = false)
	public void setSaJsonTemplate(SaJsonTemplate saJsonTemplate) {
		SaManager.setSaJsonTemplate(saJsonTemplate);
	}

	/**
	 * 注入自定义的 参数签名 Bean 
	 * 
	 * @param saSignTemplate 参数签名 Bean 
	 */
	@Autowired(required = false)
	public void setSaSignTemplate(SaSignTemplate saSignTemplate) {
		SaManager.setSaSignTemplate(saSignTemplate);
	}

	/**
	 * 注入自定义的 StpLogic 
	 * @param stpLogic / 
	 */
	@Autowired(required = false)
	public void setStpLogic(StpLogic stpLogic) {
		StpUtil.setStpLogic(stpLogic);
	}
	
	/**
	 * 利用自动注入特性，获取Spring框架内部使用的路由匹配器
	 * 
	 * @param pathMatcher 要设置的 pathMatcher
	 */
	@Autowired(required = false)
	@Qualifier("mvcPathMatcher")
	public void setPathMatcher(PathMatcher pathMatcher) {
		SaPathMatcherHolder.setPathMatcher(pathMatcher);
	}

}
