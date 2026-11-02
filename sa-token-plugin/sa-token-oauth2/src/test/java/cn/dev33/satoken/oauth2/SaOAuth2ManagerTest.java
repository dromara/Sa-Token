/*
 * Copyright 2020-2099 sa-token.com
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

import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.dao.SaOAuth2Dao;
import cn.dev33.satoken.oauth2.data.convert.SaOAuth2DataConverter;
import cn.dev33.satoken.oauth2.data.convert.SaOAuth2DataConverterDefaultImpl;
import cn.dev33.satoken.oauth2.data.generate.SaOAuth2DataGenerate;
import cn.dev33.satoken.oauth2.data.generate.SaOAuth2DataGenerateDefaultImpl;
import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoaderDefaultImpl;
import cn.dev33.satoken.oauth2.data.resolver.SaOAuth2DataResolver;
import cn.dev33.satoken.oauth2.data.resolver.SaOAuth2DataResolverDefaultImpl;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.oauth2.support.OAuth2Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * OAuth2 总控：get/set 各 Bean、置空后再 get 会懒加载默认实现
 */
@OAuth2Test
public class SaOAuth2ManagerTest {

	/** 每个用例前先装一套默认配置 */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
	}

	/** 应测尽测：测试 SaOAuth2Manager 无参构造 */
	@Test
	public void ctor_canNew() {
		Assertions.assertNotNull(new SaOAuth2Manager());
	}

	/** get/set 各 Bean 应该能写回去 */
	@Test
	public void getSet_allBeans_roundTrip() {
		SaOAuth2ServerConfig cfg = new SaOAuth2ServerConfig();
		SaOAuth2DataLoader loader = new SaOAuth2DataLoaderDefaultImpl();
		SaOAuth2DataResolver resolver = new SaOAuth2DataResolverDefaultImpl();
		SaOAuth2DataConverter converter = new SaOAuth2DataConverterDefaultImpl();
		SaOAuth2DataGenerate generate = new SaOAuth2DataGenerateDefaultImpl();
		SaOAuth2Dao dao = new SaOAuth2Dao();
		SaOAuth2Template template = new SaOAuth2Template();
		StpLogic stpLogic = StpUtil.stpLogic;

		SaOAuth2Manager.setServerConfig(cfg);
		SaOAuth2Manager.setDataLoader(loader);
		SaOAuth2Manager.setDataResolver(resolver);
		SaOAuth2Manager.setDataConverter(converter);
		SaOAuth2Manager.setDataGenerate(generate);
		SaOAuth2Manager.setDao(dao);
		SaOAuth2Manager.setTemplate(template);
		SaOAuth2Manager.setStpLogic(stpLogic);

		Assertions.assertSame(cfg, SaOAuth2Manager.getServerConfig());
		Assertions.assertSame(loader, SaOAuth2Manager.getDataLoader());
		Assertions.assertSame(resolver, SaOAuth2Manager.getDataResolver());
		Assertions.assertSame(converter, SaOAuth2Manager.getDataConverter());
		Assertions.assertSame(generate, SaOAuth2Manager.getDataGenerate());
		Assertions.assertSame(dao, SaOAuth2Manager.getDao());
		Assertions.assertSame(template, SaOAuth2Manager.getTemplate());
		Assertions.assertSame(stpLogic, SaOAuth2Manager.getStpLogic());
	}

	/** 把 Bean 置空后再 get，应该各自 new 出默认实现 */
	@Test
	public void get_whenNull_lazyCreatesDefault() {
		SaOAuth2Manager.setServerConfig(null);
		SaOAuth2Manager.setDataLoader(null);
		SaOAuth2Manager.setDataResolver(null);
		SaOAuth2Manager.setDataConverter(null);
		SaOAuth2Manager.setDataGenerate(null);
		SaOAuth2Manager.setDao(null);
		SaOAuth2Manager.setTemplate(null);
		SaOAuth2Manager.setStpLogic(null);

		Assertions.assertNotNull(SaOAuth2Manager.getServerConfig());
		Assertions.assertTrue(SaOAuth2Manager.getDataLoader() instanceof SaOAuth2DataLoaderDefaultImpl);
		Assertions.assertTrue(SaOAuth2Manager.getDataResolver() instanceof SaOAuth2DataResolverDefaultImpl);
		Assertions.assertTrue(SaOAuth2Manager.getDataConverter() instanceof SaOAuth2DataConverterDefaultImpl);
		Assertions.assertTrue(SaOAuth2Manager.getDataGenerate() instanceof SaOAuth2DataGenerateDefaultImpl);
		Assertions.assertNotNull(SaOAuth2Manager.getDao());
		Assertions.assertNotNull(SaOAuth2Manager.getTemplate());
		Assertions.assertSame(StpUtil.stpLogic, SaOAuth2Manager.getStpLogic());
	}

}
