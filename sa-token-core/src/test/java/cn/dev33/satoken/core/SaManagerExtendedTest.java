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
package cn.dev33.satoken.core;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContextForThreadLocal;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.secure.totp.SaTotpTemplate;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaManager 补充测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaManagerExtendedTest {

	/** removeStpLogic 后应无法再获取对应 StpLogic */
	@Test
	void removeStpLogic() {
		StpLogic logic = new StpLogic("temp");
		SaManager.putStpLogic(logic);
		Assertions.assertSame(logic, SaManager.getStpLogic("temp", false));
		SaManager.removeStpLogic("temp");
		Assertions.assertThrows(SaTokenException.class, () -> SaManager.getStpLogic("temp", false));
	}

	/** 缺失的 loginType 在宽松模式下应自动创建 StpLogic */
	@Test
	void getStpLogic_autoCreateWhenMissing() {
		StpLogic logic = SaManager.getStpLogic("auto-create");
		Assertions.assertNotNull(logic);
		Assertions.assertEquals("auto-create", logic.getLoginType());
		SaManager.removeStpLogic("auto-create");
	}

	/** loginType 为 null 或空字符串时应返回默认 StpLogic */
	@Test
	void getStpLogic_emptyLoginTypeReturnsDefault() {
		Assertions.assertSame(StpUtil.stpLogic, SaManager.getStpLogic(null));
		Assertions.assertSame(StpUtil.stpLogic, SaManager.getStpLogic(""));
	}

	/** setSaTotpTemplate / getSaTotpTemplate 应读写同一实例 */
	@Test
	void setAndGetSaTotpTemplate() {
		SaTotpTemplate template = new SaTotpTemplate();
		SaManager.setSaTotpTemplate(template);
		Assertions.assertSame(template, SaManager.getSaTotpTemplate());
	}

	/** setSaTokenContext 后应能获取同一上下文实现 */
	@Test
	void setSaTokenContext() {
		SaTokenContextForThreadLocal context = new SaTokenContextForThreadLocal();
		SaManager.setSaTokenContext(context);
		Assertions.assertSame(context, SaManager.getSaTokenContext());
	}

	/** 开启日志且未指定 isColorLog 时，setConfig 应自动推断彩色日志开关 */
	@Test
	void setConfig_autoColorLogWhenEnabled() {
		SaTokenConfig config = new SaTokenConfig();
		config.setIsPrint(false);
		config.setIsLog(true);
		config.setIsColorLog(null);
		SaManager.setConfig(config);
		Assertions.assertNotNull(SaManager.getConfig().getIsColorLog());
	}

	/** 严格模式下获取不存在的 StpLogic 应抛出 CODE_10002 异常 */
	@Test
	void getStpLogicStrictModeThrows() {
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class,
				() -> SaManager.getStpLogic("missing-type", false));
		Assertions.assertEquals(SaErrorCode.CODE_10002, ex.getCode());
	}

}
