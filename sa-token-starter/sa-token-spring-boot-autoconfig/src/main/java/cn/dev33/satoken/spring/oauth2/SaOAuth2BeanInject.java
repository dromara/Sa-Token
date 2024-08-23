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
package cn.dev33.satoken.spring.oauth2;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.dao.SaOAuth2Dao;
import cn.dev33.satoken.oauth2.data.convert.SaOAuth2DataConverter;
import cn.dev33.satoken.oauth2.data.generate.SaOAuth2DataGenerate;
import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.data.resolver.SaOAuth2DataResolver;
import cn.dev33.satoken.oauth2.granttype.handler.SaOAuth2GrantTypeHandlerInterface;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.oauth2.scope.handler.SaOAuth2ScopeHandlerInterface;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import java.util.List;


// 小提示：如果你在 idea 中运行源码时出现异常：java: 程序包cn.dev33.satoken.oauth2不存在。
// 在项目根目录进入 cmd，执行 mvn package 即可解决


/**
 * 注入 Sa-Token-OAuth2 所需要的组件
 * 
 * @author click33
 * @since 1.34.0
 */
@ConditionalOnClass(SaOAuth2Manager.class)
public class SaOAuth2BeanInject {

	/**
	 * 注入 OAuth2 配置对象
	 * 
	 * @param saOAuth2Config 配置对象 
	 */
	@Autowired(required = false)
	public void setSaOAuth2Config(SaOAuth2ServerConfig saOAuth2Config) {
		SaOAuth2Manager.setConfig(saOAuth2Config);
	}

	/**
	 * 注入 OAuth2 模板代码类
	 * 
	 * @param saOAuth2Template 模板代码类
	 */
	@Autowired(required = false)
	public void setSaOAuth2Template(SaOAuth2Template saOAuth2Template) {
		SaOAuth2Manager.setTemplate(saOAuth2Template);
	}

	/**
	 * 注入 OAuth2 请求处理器
	 *
	 * @param serverProcessor 请求处理器
	 */
	@Autowired(required = false)
	public void setSaOAuth2Template(SaOAuth2ServerProcessor serverProcessor) {
		SaOAuth2ServerProcessor.instance = serverProcessor;
	}

	/**
	 * 注入 OAuth2 数据加载器
	 *
	 * @param dataLoader /
	 */
	@Autowired(required = false)
	public void setSaOAuth2DataLoader(SaOAuth2DataLoader dataLoader) {
		SaOAuth2Manager.setDataLoader(dataLoader);
	}

	/**
	 * 注入 OAuth2 数据解析器 Bean
	 *
	 * @param dataResolver /
	 */
	@Autowired(required = false)
	public void setSaOAuth2DataResolver(SaOAuth2DataResolver dataResolver) {
		SaOAuth2Manager.setDataResolver(dataResolver);
	}

	/**
	 * 注入 OAuth2 数据格式转换器 Bean
	 *
	 * @param dataConverter /
	 */
	@Autowired(required = false)
	public void setSaOAuth2DataConverter(SaOAuth2DataConverter dataConverter) {
		SaOAuth2Manager.setDataConverter(dataConverter);
	}

	/**
	 * 注入 OAuth2 数据构建器 Bean
	 *
	 * @param dataGenerate /
	 */
	@Autowired(required = false)
	public void setSaOAuth2DataGenerate(SaOAuth2DataGenerate dataGenerate) {
		SaOAuth2Manager.setDataGenerate(dataGenerate);
	}

	/**
	 * 注入 OAuth2 数据持久 Bean
	 *
	 * @param dao /
	 */
	@Autowired(required = false)
	public void setSaOAuth2Dao(SaOAuth2Dao dao) {
		SaOAuth2Manager.setDao(dao);
	}

	/**
	 * 注入自定义 scope 处理器
	 *
	 * @param handlerList 自定义 scope 处理器集合
	 */
	@Autowired(required = false)
	public void setSaOAuth2ScopeHandler(List<SaOAuth2ScopeHandlerInterface> handlerList) {
		for (SaOAuth2ScopeHandlerInterface handler : handlerList) {
			SaOAuth2Strategy.instance.registerScopeHandler(handler);
		}
	}

	/**
	 * 注入自定义 grant_type 处理器
	 *
	 * @param handlerList 自定义 grant_type 处理器集合
	 */
	@Autowired(required = false)
	public void setSaOAuth2GrantTypeHandlerInterface(List<SaOAuth2GrantTypeHandlerInterface> handlerList) {
		for (SaOAuth2GrantTypeHandlerInterface handler : handlerList) {
			SaOAuth2Strategy.instance.registerGrantTypeHandler(handler);
		}
	}

}
