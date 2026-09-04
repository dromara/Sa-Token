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
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaTokenConsts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * StpLogic 账号封禁
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicDisableTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** 默认服务封禁/解封/checkDisable/getDisableTime 全流程应正常 */
	@Test
	void disable_untieDisable_checkDisable_getDisableTime() {
		SaTokenDao dao = SaManager.getSaTokenDao();

		stpLogic.disable(10007, 200);
		Assertions.assertTrue(stpLogic.isDisable(10007));
		Assertions.assertEquals(String.valueOf(SaTokenConsts.DEFAULT_DISABLE_LEVEL),
				dao.get(stpLogic.splicingKeyDisable(10007, SaTokenConsts.DEFAULT_DISABLE_SERVICE)));

		Assertions.assertThrows(DisableServiceException.class, () -> stpLogic.checkDisable(10007));

		long disableTime = stpLogic.getDisableTime(10007);
		Assertions.assertTrue(disableTime <= 200 && disableTime >= 199);

		stpLogic.untieDisable(10007);
		Assertions.assertFalse(stpLogic.isDisable(10007));
		Assertions.assertNull(dao.get(stpLogic.splicingKeyDisable(10007, SaTokenConsts.DEFAULT_DISABLE_SERVICE)));
		Assertions.assertDoesNotThrow(() -> stpLogic.checkDisable(10007));
	}

	/** 指定 service 的封禁与解封应互不影响其他 service */
	@Test
	void disable_withService_untieDisable_checkDisable_getDisableTime() {
		SaTokenDao dao = SaManager.getSaTokenDao();

		stpLogic.disable(10008, "comment", 200);
		Assertions.assertTrue(stpLogic.isDisable(10008, "comment"));
		Assertions.assertEquals(String.valueOf(SaTokenConsts.DEFAULT_DISABLE_LEVEL),
				dao.get(stpLogic.splicingKeyDisable(10008, "comment")));
		Assertions.assertNull(dao.get(stpLogic.splicingKeyDisable(10008, SaTokenConsts.DEFAULT_DISABLE_SERVICE)));

		Assertions.assertThrows(DisableServiceException.class, () -> stpLogic.checkDisable(10008, "comment"));
		Assertions.assertThrows(DisableServiceException.class, () -> stpLogic.checkDisable(10008, "comment", "login"));

		long disableTime = stpLogic.getDisableTime(10008, "comment");
		Assertions.assertTrue(disableTime <= 200 && disableTime >= 199);

		stpLogic.untieDisable(10008);
		Assertions.assertTrue(stpLogic.isDisable(10008, "comment"));
		Assertions.assertNotNull(dao.get(stpLogic.splicingKeyDisable(10008, "comment")));

		stpLogic.untieDisable(10008, "comment");
		Assertions.assertFalse(stpLogic.isDisable(10008, "comment"));
		Assertions.assertNull(dao.get(stpLogic.splicingKeyDisable(10008, "comment")));
		Assertions.assertDoesNotThrow(() -> stpLogic.checkDisable(10007, "comment"));
	}

	/** 按等级封禁后 isDisableLevel/checkDisableLevel/getDisableLevel 应正确 */
	@Test
	void disableLevel_getDisableLevel_checkDisableLevel() {
		SaTokenDao dao = SaManager.getSaTokenDao();

		stpLogic.disableLevel(10009, 5, 200);
		Assertions.assertTrue(stpLogic.isDisableLevel(10009, 3));
		Assertions.assertTrue(stpLogic.isDisableLevel(10009, 5));
		Assertions.assertFalse(stpLogic.isDisableLevel(10009, 7));
		Assertions.assertFalse(stpLogic.isDisableLevel(20009, 3));
		Assertions.assertEquals(String.valueOf(5),
				dao.get(stpLogic.splicingKeyDisable(10009, SaTokenConsts.DEFAULT_DISABLE_SERVICE)));

		Assertions.assertThrows(DisableServiceException.class, () -> stpLogic.checkDisableLevel(10009, 3));
		Assertions.assertThrows(DisableServiceException.class, () -> stpLogic.checkDisableLevel(10009, 5));
		Assertions.assertDoesNotThrow(() -> stpLogic.checkDisableLevel(10009, 7));
		Assertions.assertDoesNotThrow(() -> stpLogic.checkDisableLevel(20009, 3));

		Assertions.assertEquals(5, stpLogic.getDisableLevel(10009));
		Assertions.assertEquals(SaTokenConsts.NOT_DISABLE_LEVEL, stpLogic.getDisableLevel(20009));

		stpLogic.untieDisable(10009);
		Assertions.assertFalse(stpLogic.isDisable(10009));
		Assertions.assertFalse(stpLogic.isDisableLevel(10009, 5));
		Assertions.assertNull(dao.get(stpLogic.splicingKeyDisable(10009, SaTokenConsts.DEFAULT_DISABLE_SERVICE)));
	}

	/** 指定 service 的等级封禁与 getDisableLevel 应正确隔离 */
	@Test
	void disableLevel_withService_getDisableLevel() {
		SaTokenDao dao = SaManager.getSaTokenDao();

		stpLogic.disableLevel(10010, "shop", 5, 200);
		Assertions.assertTrue(stpLogic.isDisableLevel(10010, "shop", 3));
		Assertions.assertTrue(stpLogic.isDisableLevel(10010, "shop", 5));
		Assertions.assertFalse(stpLogic.isDisableLevel(10010, "shop", 7));
		Assertions.assertFalse(stpLogic.isDisableLevel(20010, "shop", 3));
		Assertions.assertFalse(stpLogic.isDisableLevel(10010, "shop2", 5));
		Assertions.assertEquals(String.valueOf(5), dao.get(stpLogic.splicingKeyDisable(10010, "shop")));

		Assertions.assertThrows(DisableServiceException.class, () -> stpLogic.checkDisableLevel(10010, "shop", 3));
		Assertions.assertDoesNotThrow(() -> stpLogic.checkDisableLevel(10010, "shop", 7));

		Assertions.assertEquals(5, stpLogic.getDisableLevel(10010, "shop"));
		Assertions.assertEquals(SaTokenConsts.NOT_DISABLE_LEVEL, stpLogic.getDisableLevel(10010, "shop2"));
		Assertions.assertEquals(SaTokenConsts.NOT_DISABLE_LEVEL, stpLogic.getDisableLevel(20010, "shop"));

		stpLogic.untieDisable(10010, "shop");
		Assertions.assertFalse(stpLogic.isDisable(10010, "shop"));
		Assertions.assertNull(dao.get(stpLogic.splicingKeyDisable(10010, "shop")));
	}

}
