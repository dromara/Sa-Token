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
package cn.dev33.satoken.core.strategy.hooks;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.exception.FirewallCheckException;
import cn.dev33.satoken.exception.RequestPathInvalidException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHookForBlackPath;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHookForDirectoryTraversal;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHookForHeader;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHookForHost;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHookForHttpMethod;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHookForParameter;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHookForPathBannedCharacter;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHookForPathDangerCharacter;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHookForWhitePath;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 防火墙 Hook 全量测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaFirewallHooksTest {

	private boolean originalBannedPercentage;
	private boolean originalCheckHost;
	private boolean originalCheckMethod;
	private List<String> originalDangerCharacter;
	private List<String> originalBlackPaths;
	private List<String> originalWhitePaths;
	private List<String> originalAllowHosts;
	private List<String> originalNotAllowHeaderNames;
	private List<String> originalNotAllowParameterNames;
	private List<String> originalAllowMethods;

	@BeforeEach
	void saveHookState() {
		originalBannedPercentage = SaFirewallCheckHookForPathBannedCharacter.instance.bannedPercentage;
		originalCheckHost = SaFirewallCheckHookForHost.instance.isCheckHost;
		originalCheckMethod = SaFirewallCheckHookForHttpMethod.instance.isCheckMethod;
		originalDangerCharacter = new ArrayList<>(SaFirewallCheckHookForPathDangerCharacter.instance.dangerCharacter);
		originalBlackPaths = new ArrayList<>(SaFirewallCheckHookForBlackPath.instance.blackPaths);
		originalWhitePaths = new ArrayList<>(SaFirewallCheckHookForWhitePath.instance.whitePaths);
		originalAllowHosts = new ArrayList<>(SaFirewallCheckHookForHost.instance.allowHosts);
		originalNotAllowHeaderNames = new ArrayList<>(SaFirewallCheckHookForHeader.instance.notAllowHeaderNames);
		originalNotAllowParameterNames = new ArrayList<>(SaFirewallCheckHookForParameter.instance.notAllowParameterNames);
		originalAllowMethods = new ArrayList<>(SaFirewallCheckHookForHttpMethod.instance.allowMethods);
	}

	@AfterEach
	void restoreHookState() {
		SaFirewallCheckHookForPathBannedCharacter.instance.bannedPercentage = originalBannedPercentage;
		SaFirewallCheckHookForHost.instance.isCheckHost = originalCheckHost;
		SaFirewallCheckHookForHttpMethod.instance.isCheckMethod = originalCheckMethod;
		SaFirewallCheckHookForPathDangerCharacter.instance.dangerCharacter = new ArrayList<>(originalDangerCharacter);
		SaFirewallCheckHookForBlackPath.instance.blackPaths.clear();
		SaFirewallCheckHookForBlackPath.instance.blackPaths.addAll(originalBlackPaths);
		SaFirewallCheckHookForWhitePath.instance.whitePaths.clear();
		SaFirewallCheckHookForWhitePath.instance.whitePaths.addAll(originalWhitePaths);
		SaFirewallCheckHookForHost.instance.allowHosts.clear();
		SaFirewallCheckHookForHost.instance.allowHosts.addAll(originalAllowHosts);
		SaFirewallCheckHookForHeader.instance.notAllowHeaderNames.clear();
		SaFirewallCheckHookForHeader.instance.notAllowHeaderNames.addAll(originalNotAllowHeaderNames);
		SaFirewallCheckHookForParameter.instance.notAllowParameterNames.clear();
		SaFirewallCheckHookForParameter.instance.notAllowParameterNames.addAll(originalNotAllowParameterNames);
		SaFirewallCheckHookForHttpMethod.instance.allowMethods.clear();
		SaFirewallCheckHookForHttpMethod.instance.allowMethods.addAll(originalAllowMethods);
	}

	private void withMockRequest(String path, Runnable action) {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = path;
			req.method = "GET";
			req.host = "localhost";
			action.run();
		});
	}

	/** 目录穿越路径应被拦截并携带原始 path */
	@Test
	void directoryTraversalHook_rejectsInvalidPath() {
		withMockRequest("/user/../info", () -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			SaResponse res = SaHolder.getResponse();
			RequestPathInvalidException ex = Assertions.assertThrows(RequestPathInvalidException.class,
					() -> SaFirewallCheckHookForDirectoryTraversal.instance.execute(req, res, null));
			Assertions.assertEquals("/user/../info", ex.getPath());
		});
	}

	/** 正常路径应通过目录穿越检测 */
	@Test
	void directoryTraversalHook_allowsValidPath() {
		withMockRequest("/user/info", () -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			Assertions.assertDoesNotThrow(() -> SaFirewallCheckHookForDirectoryTraversal.instance.execute(req, SaHolder.getResponse(), null));
		});
	}

	/** 包含危险字符的路径应被拦截 */
	@Test
	void pathDangerCharacterHook_rejectsDangerousPath() {
		SaFirewallCheckHookForPathDangerCharacter.instance.resetConfig("//");
		withMockRequest("/user//info", () -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			Assertions.assertThrows(RequestPathInvalidException.class,
					() -> SaFirewallCheckHookForPathDangerCharacter.instance.execute(req, SaHolder.getResponse(), null));
		});
	}

	/** 不含危险字符的路径应通过检测 */
	@Test
	void pathDangerCharacterHook_allowsSafePath() {
		withMockRequest("/user/info", () -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			Assertions.assertDoesNotThrow(() -> SaFirewallCheckHookForPathDangerCharacter.instance.execute(req, SaHolder.getResponse(), null));
		});
	}

	/** 含不可打印 ASCII 字符的路径应被拦截 */
	@Test
	void pathBannedCharacterHook_rejectsNonPrintableAscii() {
		withMockRequest("/user/\u0007info", () -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			Assertions.assertThrows(RequestPathInvalidException.class,
					() -> SaFirewallCheckHookForPathBannedCharacter.instance.execute(req, SaHolder.getResponse(), null));
		});
	}

	/** 开启百分号拦截时，路径中的 % 应被拒绝 */
	@Test
	void pathBannedCharacterHook_rejectsPercentWhenEnabled() {
		SaFirewallCheckHookForPathBannedCharacter.instance.resetConfig(true);
		withMockRequest("/user/%20info", () -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			Assertions.assertThrows(RequestPathInvalidException.class,
					() -> SaFirewallCheckHookForPathBannedCharacter.instance.execute(req, SaHolder.getResponse(), null));
		});
	}

	/** 命中黑名单路径时应抛出 RequestPathInvalidException */
	@Test
	void blackPathHook_rejectsBlacklistedPath() {
		SaFirewallCheckHookForBlackPath.instance.resetConfig("/admin/secret");
		withMockRequest("/admin/secret", () -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			Assertions.assertThrows(RequestPathInvalidException.class,
					() -> SaFirewallCheckHookForBlackPath.instance.execute(req, SaHolder.getResponse(), null));
		});
	}

	/** 命中白名单路径时应抛出 StopMatchException 跳过后续检测 */
	@Test
	void whitePathHook_throwsStopMatchException() {
		SaFirewallCheckHookForWhitePath.instance.resetConfig("/health");
		withMockRequest("/health", () -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			Assertions.assertThrows(StopMatchException.class,
					() -> SaFirewallCheckHookForWhitePath.instance.execute(req, SaHolder.getResponse(), null));
		});
	}

	/** 未在白名单内的 Host 应被拒绝 */
	@Test
	void hostHook_rejectsIllegalHost() {
		SaFirewallCheckHookForHost.instance.resetConfig(true, "allowed.com");
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/api/user";
			req.host = "evil.com";
			Assertions.assertThrows(FirewallCheckException.class,
					() -> SaFirewallCheckHookForHost.instance.execute(req, SaHolder.getResponse(), null));
		});
	}

	/** 配置的 Host 应通过校验 */
	@Test
	void hostHook_allowsConfiguredHost() {
		SaFirewallCheckHookForHost.instance.resetConfig(true, "localhost");
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/api/user";
			req.host = "localhost";
			Assertions.assertDoesNotThrow(() -> SaFirewallCheckHookForHost.instance.execute(req, SaHolder.getResponse(), null));
		});
	}

	/** 携带禁止请求头的请求应被拦截 */
	@Test
	void headerHook_rejectsForbiddenHeader() {
		SaFirewallCheckHookForHeader.instance.resetConfig("X-Forbidden");
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/api/user";
			req.headerMap.put("X-Forbidden", "1");
			Assertions.assertThrows(FirewallCheckException.class,
					() -> SaFirewallCheckHookForHeader.instance.execute(req, SaHolder.getResponse(), null));
		});
	}

	/** 携带禁止参数名的请求应被拦截 */
	@Test
	void parameterHook_rejectsForbiddenParameter() {
		SaFirewallCheckHookForParameter.instance.resetConfig("debug");
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/api/user";
			req.parameterMap.put("debug", "true");
			Assertions.assertThrows(FirewallCheckException.class,
					() -> SaFirewallCheckHookForParameter.instance.execute(req, SaHolder.getResponse(), null));
		});
	}

	/** 不在允许列表中的 HTTP 方法应被拒绝 */
	@Test
	void httpMethodHook_rejectsIllegalMethod() {
		SaFirewallCheckHookForHttpMethod.instance.resetConfig(true, "GET");
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/api/user";
			req.method = "TRACE";
			Assertions.assertThrows(FirewallCheckException.class,
					() -> SaFirewallCheckHookForHttpMethod.instance.execute(req, SaHolder.getResponse(), null));
		});
	}

	/** 配置的 HTTP 方法应通过校验 */
	@Test
	void httpMethodHook_allowsConfiguredMethod() {
		SaFirewallCheckHookForHttpMethod.instance.resetConfig(true, "GET", "POST");
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/api/user";
			req.method = "POST";
			Assertions.assertDoesNotThrow(() -> SaFirewallCheckHookForHttpMethod.instance.execute(req, SaHolder.getResponse(), null));
		});
	}

	/** 关闭方法校验时不应拦截任意 HTTP 方法 */
	@Test
	void httpMethodHook_skipsWhenDisabled() {
		SaFirewallCheckHookForHttpMethod.instance.resetConfig(false);
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = "/api/user";
			req.method = "UNKNOWN";
			Assertions.assertDoesNotThrow(() -> SaFirewallCheckHookForHttpMethod.instance.execute(req, SaHolder.getResponse(), null));
		});
	}

}
