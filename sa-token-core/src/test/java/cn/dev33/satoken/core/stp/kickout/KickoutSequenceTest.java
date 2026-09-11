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
package cn.dev33.satoken.core.stp.kickout;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.test.fixture.RecordingSaTokenListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 踢人场景：夹具、步骤串、公开 API 都放在这个包里，不跟别的能力混扫。
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class KickoutSequenceTest {

	private List<SaTokenListener> savedListeners;

	/** 每个用例开始前把监听器列表换成空的，避免掺进别的侦听器 */
	@BeforeEach
	void setUp() {
		savedListeners = new ArrayList<>(SaTokenEventCenter.getListenerList());
		SaTokenEventCenter.setListenerList(new ArrayList<>());
	}

	/** 每个用例结束后把监听器列表还原 */
	@AfterEach
	void tearDown() {
		SaTokenEventCenter.setListenerList(savedListeners);
	}

	/** kickout 后旧 Token 应标成 KICK_OUT，事件串是 login==>kickout，下次带着旧 Token 请求要失败 */
	@Test
	void kickout_oldTokenFails_andStepStr() {
		RecordingSaTokenListener listener = new RecordingSaTokenListener();
		SaTokenEventCenter.registerListener(listener);
		SaTokenDao dao = SaManager.getSaTokenDao();
		String tokenName = StpUtil.getTokenName();

		String t1 = SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10002);
			Assertions.assertEquals("login", listener.stepStr());
			return StpUtil.getTokenValue();
		});

		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.kickout(10002);
			Assertions.assertEquals("login==>kickout", listener.stepStr());
			Assertions.assertEquals(NotLoginException.KICK_OUT,
					dao.get(StpUtil.getStpLogic().splicingKeyTokenValue(t1)));
		});

		SaTokenContextMockUtil.setMockContext(() -> {
			((SaRequestForMock) SaHolder.getRequest()).headerMap.put(tokenName, t1);
			NotLoginException e = Assertions.assertThrows(NotLoginException.class, StpUtil::checkLogin);
			Assertions.assertEquals(NotLoginException.KICK_OUT, e.getType());
		});
	}

}
