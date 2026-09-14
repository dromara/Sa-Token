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
package cn.dev33.satoken.core.stp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaCookieConfig;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaResponseForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaTokenConsts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * StpLogic Token 值读取与写入：前缀模式、Header/Cookie/Body/Storage 多来源
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicTokenValueTest {

	private StpLogic stpLogic;

	/** 每个用例开始前准备测试现场 */
	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** Bearer 前缀模式下 getTokenValueNotCut 应含前缀而 getTokenValue 返回裸 Token */
	@Test
	void tokenPrefix_bearer_getTokenValueAndNotCut() {
		SaTokenConfig config = SaManager.getConfig();
		config.setTokenPrefix("Bearer");
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(50008);
			String rawToken = stpLogic.getTokenValue();
			String prefixed = stpLogic.getTokenValueNotCut();
			Assertions.assertTrue(prefixed.startsWith("Bearer" + SaTokenConsts.TOKEN_CONNECTOR_CHAT));
			Assertions.assertEquals(rawToken, stpLogic.getTokenValue());
			Assertions.assertEquals(rawToken, stpLogic.getTokenValue(false));
		});
	}

	/** Header 无前缀且 isCut=false 时 getTokenValue(true) 应抛出 NO_PREFIX */
	@Test
	void getTokenValue_noPrefixThrowException() {
		SaTokenConfig config = SaManager.getConfig();
		config.setTokenPrefix("Bearer");
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			String token = stpLogic.createLoginSession(50009);
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put(stpLogic.getTokenName(), token);

			Assertions.assertNull(stpLogic.getTokenValue(false));
			NotLoginException noPrefix = Assertions.assertThrows(NotLoginException.class, () -> stpLogic.getTokenValue(true));
			Assertions.assertEquals(NotLoginException.NO_PREFIX, noPrefix.getType());
		});
	}

	/** Cookie 自动补前缀模式下 getTokenValue 应从 Cookie 读取裸 Token */
	@Test
	void getTokenValue_readsFromCookieWithAutoFillPrefix() {
		SaTokenConfig config = SaManager.getConfig();
		config.setTokenPrefix("Bearer");
		config.setIsReadCookie(true);
		config.setIsReadHeader(false);
		config.setIsReadBody(false);
		config.setCookieAutoFillPrefix(true);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			String token = stpLogic.createLoginSession(50018);
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.cookieMap.put(stpLogic.getTokenName(), token);

			Assertions.assertEquals(token, stpLogic.getTokenValue());
			Assertions.assertEquals("Bearer" + SaTokenConsts.TOKEN_CONNECTOR_CHAT + token,
					stpLogic.getTokenValueNotCut());
		});
	}

	/** 仅读 Body 配置下 getTokenValue 应从请求参数读取 Token */
	@Test
	void getTokenValue_readsFromBodyOnly() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsReadBody(true);
		config.setIsReadHeader(false);
		config.setIsReadCookie(false);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			String token = stpLogic.createLoginSession(60002);
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.parameterMap.put(stpLogic.getTokenName(), token);

			Assertions.assertEquals(token, stpLogic.getTokenValue());
			Assertions.assertTrue(stpLogic.isLogin());
		});
	}

	/** 仅读 Storage 配置下 getTokenValue 应从 Storage 读取 Token */
	@Test
	void getTokenValue_readsFromStorageOnly() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsReadBody(false);
		config.setIsReadHeader(false);
		config.setIsReadCookie(false);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			String token = stpLogic.createLoginSession(60003);
			stpLogic.setTokenValueToStorage(token);

			Assertions.assertEquals(token, stpLogic.getTokenValue());
			Assertions.assertEquals("60003", stpLogic.getLoginIdAsString());
		});
	}

	/** 空 Token 调用 setTokenValue 应不抛异常 */
	@Test
	void setTokenValue_emptyToken_isNoOp() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertDoesNotThrow(() -> stpLogic.setTokenValue(""));
			Assertions.assertDoesNotThrow(() -> stpLogic.setTokenValue(null));
			Assertions.assertDoesNotThrow(() -> stpLogic.setTokenValue("", 60));
		});
	}

	/** 关闭读 Cookie 时 setTokenValue 应仅写入 Storage */
	@Test
	void setTokenValue_skipsCookieWhenReadCookieDisabled() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsReadCookie(false);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.setTokenValue("storage-only-token", 120);
			Assertions.assertEquals("storage-only-token", stpLogic.getTokenValueNotCut());
		});
	}

	/** Token 前缀模式下 Storage 应存带前缀的值 */
	@Test
	void setTokenValue_withTokenPrefix_writesPrefixedToStorage() {
		SaTokenConfig config = SaManager.getConfig();
		config.setTokenPrefix("Bearer");
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.setTokenValue("prefixed-token");
			String stored = String.valueOf(SaHolder.getStorage().get(stpLogic.splicingKeyJustCreatedSave()));
			Assertions.assertTrue(stored.startsWith("Bearer" + SaTokenConsts.TOKEN_CONNECTOR_CHAT));
			Assertions.assertEquals("prefixed-token", stpLogic.getTokenValue());
		});
	}

	/** 自定义 Cookie 配置下 setTokenValue 应写入 Cookie */
	@Test
	void setTokenValueToCookie_withCustomCookieConfig() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaCookieConfig cookie = new SaCookieConfig().setDomain("test.local").setPath("/api");
			stpLogic.setTokenValue("cookie-token", new SaLoginParameter().setCookie(cookie).setTimeout(300));
			Assertions.assertEquals("cookie-token", stpLogic.getTokenValueNotCut());
		});
	}

	/** isWriteHeader=true 时登录应将 Token 写入响应 Header */
	@Test
	void setTokenValueToResponseHeader_whenIsWriteHeaderTrue() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaLoginParameter param = new SaLoginParameter().setIsWriteHeader(true);
			stpLogic.login(90004, param);
			String token = stpLogic.getTokenValue();

			SaResponseForMock response = (SaResponseForMock) SaHolder.getResponse();
			Assertions.assertEquals(token, response.headerMap.get(stpLogic.getTokenName()));
			Assertions.assertEquals(stpLogic.getTokenName(),
					response.headerMap.get(SaResponse.ACCESS_CONTROL_EXPOSE_HEADERS));

			stpLogic.setTokenValueToResponseHeader("header-token");
			Assertions.assertEquals("header-token", response.headerMap.get(stpLogic.getTokenName()));
		});
	}

	/** 带 SaLoginParameter 的 setTokenValue 应将 Token 写入上下文 */
	@Test
	void setTokenValue_withLoginParameter() {
		SaTokenContextMockUtil.setMockContext(() -> {
			String token = stpLogic.createLoginSession(40007, new SaLoginParameter());
			SaLoginParameter param = new SaLoginParameter().setToken(token);
			stpLogic.setTokenValue(token, param);
			Assertions.assertEquals(token, stpLogic.getTokenValue());
		});
	}

}
