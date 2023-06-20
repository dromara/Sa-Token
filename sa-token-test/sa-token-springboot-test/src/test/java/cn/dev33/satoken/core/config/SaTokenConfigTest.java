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
package cn.dev33.satoken.core.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.config.SaCookieConfig;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.config.SaTokenConfigFactory;

/**
 * 配置类测试 
 * 
 * @author click33
 * @since 2022-9-4
 */
public class SaTokenConfigTest {

	// 基本 get set 测试
	@Test
	public void testProp() {
		SaTokenConfig config = new SaTokenConfig();
		
		config.setTokenName("nav-token");
		Assertions.assertEquals(config.getTokenName(), "nav-token");
		
		config.setTimeout(100204);
		Assertions.assertEquals(config.getTimeout(), 100204);
		
		config.setActiveTimeout(1804);
		Assertions.assertEquals(config.getActiveTimeout(), 1804);

		config.setIsConcurrent(false);
		Assertions.assertEquals(config.getIsConcurrent(), false);

		config.setIsShare(false);
		Assertions.assertEquals(config.getIsShare(), false);

		config.setMaxLoginCount(11);
		Assertions.assertEquals(config.getMaxLoginCount(), 11);

		config.setIsReadBody(false);
		Assertions.assertEquals(config.getIsReadBody(), false);

		config.setIsReadHeader(false);
		Assertions.assertEquals(config.getIsReadHeader(), false);

		config.setIsReadCookie(false);
		Assertions.assertEquals(config.getIsReadCookie(), false);

		config.setTokenStyle("tik");
		Assertions.assertEquals(config.getTokenStyle(), "tik");

		config.setDataRefreshPeriod(111);
		Assertions.assertEquals(config.getDataRefreshPeriod(), 111);

		config.setTokenSessionCheckLogin(false);
		Assertions.assertEquals(config.getTokenSessionCheckLogin(), false);

		config.setAutoRenew(false);
		Assertions.assertEquals(config.getAutoRenew(), false);

		config.setTokenPrefix("token");
		Assertions.assertEquals(config.getTokenPrefix(), "token");

		config.setIsPrint(false);
		Assertions.assertEquals(config.getIsPrint(), false);

		config.setIsLog(false);
		Assertions.assertEquals(config.getIsLog(), false);

		config.setJwtSecretKey("NgdfaXasARggr");
		Assertions.assertEquals(config.getJwtSecretKey(), "NgdfaXasARggr");

		config.setSameTokenTimeout(1004);
		Assertions.assertEquals(config.getSameTokenTimeout(), 1004);

		config.setBasic("sa:123456");
		Assertions.assertEquals(config.getBasic(), "sa:123456");

		config.setCurrDomain("http://127.0.0.1:8084");
		Assertions.assertEquals(config.getCurrDomain(), "http://127.0.0.1:8084");

		config.setCheckSameToken(false);
		Assertions.assertEquals(config.getCheckSameToken(), false);

		SaCookieConfig scc = new SaCookieConfig();
		config.setCookie(scc);
		Assertions.assertEquals(config.getCookie(), scc);
		
		config.toString();
	}

	// 从文件读取 
	@Test
	public void testSaTokenConfigFactory() {
		SaTokenConfig config = SaTokenConfigFactory.createConfig("sa-token2.properties");
		Assertions.assertEquals(config.getTokenName(), "use-token");
		Assertions.assertEquals(config.getTimeout(), 9000);
		Assertions.assertEquals(config.getActiveTimeout(), 240);
		Assertions.assertEquals(config.getIsConcurrent(), false);
		Assertions.assertEquals(config.getIsShare(), false);
		Assertions.assertEquals(config.getIsLog(), true);
	}

	// 测试 SaCookieConfig 
	@Test
	public void testSaCookieConfig() {
		SaCookieConfig config = new SaCookieConfig();
		
		config.setDomain("stp.cn");
		Assertions.assertEquals(config.getDomain(), "stp.cn");
		
		config.setPath("/pro/");
		Assertions.assertEquals(config.getPath(), "/pro/");
		
		config.setSecure(true);
		Assertions.assertEquals(config.getSecure(), true);

		config.setHttpOnly(false);
		Assertions.assertEquals(config.getHttpOnly(), false);

		config.setSameSite("lax");
		Assertions.assertEquals(config.getSameSite(), "lax");

	}
	
}
