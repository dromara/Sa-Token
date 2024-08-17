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
package cn.dev33.satoken.oauth2;

import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.dao.SaOAuth2Dao;
import cn.dev33.satoken.oauth2.dao.SaOAuth2DaoDefaultImpl;
import cn.dev33.satoken.oauth2.data.convert.SaOAuth2DataConverter;
import cn.dev33.satoken.oauth2.data.convert.SaOAuth2DataConverterDefaultImpl;
import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoaderDefaultImpl;
import cn.dev33.satoken.oauth2.data.resolver.SaOAuth2DataResolver;
import cn.dev33.satoken.oauth2.data.resolver.SaOAuth2DataResolverDefaultImpl;

/**
 * Sa-Token-OAuth2 模块 总控类
 * 
 * @author click33
 * @since 1.19.0
 */
public class SaOAuth2Manager {

	/**
	 * OAuth2 配置 Bean 
	 */
	private static volatile SaOAuth2Config config;
	public static SaOAuth2Config getConfig() {
		if (config == null) {
			// 初始化默认值
			synchronized (SaOAuth2Manager.class) {
				if (config == null) {
					setConfig(new SaOAuth2Config());
				}
			}
		}
		return config;
	}
	public static void setConfig(SaOAuth2Config config) {
		SaOAuth2Manager.config = config;
	}

	/**
	 * OAuth2 数据加载器 Bean
	 */
	private static volatile SaOAuth2DataLoader dataLoader;
	public static SaOAuth2DataLoader getDataLoader() {
		if (dataLoader == null) {
			synchronized (SaOAuth2Manager.class) {
				if (dataLoader == null) {
					setDataLoader(new SaOAuth2DataLoaderDefaultImpl());
				}
			}
		}
		return dataLoader;
	}
	public static void setDataLoader(SaOAuth2DataLoader dataLoader) {
		SaOAuth2Manager.dataLoader = dataLoader;
	}

	/**
	 * OAuth2 数据解析器 Bean
	 */
	private static volatile SaOAuth2DataResolver dataResolver;
	public static SaOAuth2DataResolver getDataResolver() {
		if (dataResolver == null) {
			synchronized (SaOAuth2Manager.class) {
				if (dataResolver == null) {
					setDataResolver(new SaOAuth2DataResolverDefaultImpl());
				}
			}
		}
		return dataResolver;
	}
	public static void setDataResolver(SaOAuth2DataResolver dataResolver) {
		SaOAuth2Manager.dataResolver = dataResolver;
	}

	/**
	 * OAuth2 数据格式转换器
	 */
	private static volatile SaOAuth2DataConverter dataConverter;
	public static SaOAuth2DataConverter getDataConverter() {
		if (dataConverter == null) {
			synchronized (SaOAuth2Manager.class) {
				if (dataConverter == null) {
					setDataConverter(new SaOAuth2DataConverterDefaultImpl());
				}
			}
		}
		return dataConverter;
	}
	public static void setDataConverter(SaOAuth2DataConverter dataConverter) {
		SaOAuth2Manager.dataConverter = dataConverter;
	}

	/**
	 * OAuth2 数据持久 Bean
	 */
	private static volatile SaOAuth2Dao dao;
	public static SaOAuth2Dao getDao() {
		if (dao == null) {
			synchronized (SaOAuth2Manager.class) {
				if (dao == null) {
					setDao(new SaOAuth2DaoDefaultImpl());
				}
			}
		}
		return dao;
	}
	public static void setDao(SaOAuth2Dao dao) {
		SaOAuth2Manager.dao = dao;
	}

}
