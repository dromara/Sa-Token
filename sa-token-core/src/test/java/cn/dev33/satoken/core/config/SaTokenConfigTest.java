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
	/** SaTokenConfig 基本属性 getter/setter 应正常工作 */
	@Test
	public void testProp() {
		SaTokenConfig config = new SaTokenConfig();
		
		config.setTokenName("nav-token");
		Assertions.assertEquals("nav-token", config.getTokenName());

		config.setTimeout(100204);
		Assertions.assertEquals(100204, config.getTimeout());

		config.setActiveTimeout(1804);
		Assertions.assertEquals(1804, config.getActiveTimeout());

		config.setIsConcurrent(false);
		Assertions.assertEquals(false, config.getIsConcurrent());

		config.setIsShare(false);
		Assertions.assertEquals(false, config.getIsShare());

		config.setMaxLoginCount(11);
		Assertions.assertEquals(11, config.getMaxLoginCount());

		config.setIsReadBody(false);
		Assertions.assertEquals(false, config.getIsReadBody());

		config.setIsReadHeader(false);
		Assertions.assertEquals(false, config.getIsReadHeader());

		config.setIsReadCookie(false);
		Assertions.assertEquals(false, config.getIsReadCookie());

		config.setTokenStyle("tik");
		Assertions.assertEquals("tik", config.getTokenStyle());

		config.setDataRefreshPeriod(111);
		Assertions.assertEquals(111, config.getDataRefreshPeriod());

		config.setTokenSessionCheckLogin(false);
		Assertions.assertEquals(false, config.getTokenSessionCheckLogin());

		config.setAutoRenew(false);
		Assertions.assertEquals(false, config.getAutoRenew());

		config.setTokenPrefix("token");
		Assertions.assertEquals("token", config.getTokenPrefix());

		config.setIsPrint(false);
		Assertions.assertEquals(false, config.getIsPrint());

		config.setIsLog(false);
		Assertions.assertEquals(false, config.getIsLog());

		config.setJwtSecretKey("NgdfaXasARggr");
		Assertions.assertEquals("NgdfaXasARggr", config.getJwtSecretKey());

		config.setSameTokenTimeout(1004);
		Assertions.assertEquals(1004, config.getSameTokenTimeout());

		config.setHttpBasic("sa:123456");
		Assertions.assertEquals("sa:123456", config.getHttpBasic());

		config.setCurrDomain("http://127.0.0.1:8084");
		Assertions.assertEquals("http://127.0.0.1:8084", config.getCurrDomain());

		config.setCheckSameToken(false);
		Assertions.assertEquals(false, config.getCheckSameToken());

		config.setAllowLoginIdColon(true);
		Assertions.assertEquals(true, config.getAllowLoginIdColon());

		SaCookieConfig scc = new SaCookieConfig();
		config.setCookie(scc);
		Assertions.assertEquals(scc, config.getCookie());
		
		config.toString();
	}

	// 从文件读取 
	/** 应从 properties 文件创建 SaTokenConfig */
	@Test
	public void testSaTokenConfigFactory() {
		SaTokenConfig config = SaTokenConfigFactory.createConfig("sa-token2.properties");
		Assertions.assertEquals("use-token", config.getTokenName());
		Assertions.assertEquals(9000, config.getTimeout());
		Assertions.assertEquals(240, config.getActiveTimeout());
		Assertions.assertEquals(false, config.getIsConcurrent());
		Assertions.assertEquals(false, config.getIsShare());
		Assertions.assertEquals(true, config.getIsLog());
	}

	// 测试 SaCookieConfig 
	/** SaCookieConfig 基本属性 getter/setter 应正常工作 */
	@Test
	public void testSaCookieConfig() {
		SaCookieConfig config = new SaCookieConfig();
		
		config.setDomain("stp.cn");
		Assertions.assertEquals("stp.cn", config.getDomain());

		config.setPath("/pro/");
		Assertions.assertEquals("/pro/", config.getPath());

		config.setSecure(true);
		Assertions.assertEquals(true, config.getSecure());

		config.setHttpOnly(false);
		Assertions.assertEquals(false, config.getHttpOnly());

		config.setSameSite("lax");
		Assertions.assertEquals("lax", config.getSameSite());

	}
	
}
