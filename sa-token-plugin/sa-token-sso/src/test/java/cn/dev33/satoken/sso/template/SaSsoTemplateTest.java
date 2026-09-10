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
package cn.dev33.satoken.sso.template;

import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.name.ApiName;
import cn.dev33.satoken.sso.name.ParamName;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.sso.support.SsoTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 公共模板：换 paramName/apiName/stpLogic，找不到消息处理器
 */
@SsoTest
public class SaSsoTemplateTest {

	/** setParamName / setApiName 应该连缀写回 */
	@Test
	public void setParamNameAndApiName_returnsSelf() {
		SaSsoTemplate t = new SaSsoTemplate();
		ParamName pn = new ParamName();
		pn.back = "returnUrl";
		ApiName an = new ApiName();
		Assertions.assertSame(t, t.setParamName(pn));
		Assertions.assertSame(t, t.setApiName(an));
		Assertions.assertSame(pn, t.paramName);
		Assertions.assertSame(an, t.apiName);
	}

	/** 没配 StpLogic 时 getStpLogicOrGlobal 应该走全局，配了就用自己的 */
	@Test
	public void setStpLogic_overridesGlobal() {
		SaSsoTemplate t = new SaSsoTemplate();
		Assertions.assertNull(t.getStpLogic());
		Assertions.assertSame(StpUtil.stpLogic, t.getStpLogicOrGlobal());
		StpLogic custom = new StpLogic("sso-tpl");
		Assertions.assertSame(t, t.setStpLogic(custom));
		Assertions.assertSame(custom, t.getStpLogic());
		Assertions.assertSame(custom, t.getStpLogicOrGlobal());
	}

	/** handleMessage 找不到处理器时应该抛 30021 */
	@Test
	public void handleMessage_missingHandlerThrows30021() {
		SaSsoTemplate t = new SaSsoTemplate();
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class,
				() -> t.handleMessage(new SaSsoMessage("no-such-type")));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30021, ex.getCode());
	}

}
