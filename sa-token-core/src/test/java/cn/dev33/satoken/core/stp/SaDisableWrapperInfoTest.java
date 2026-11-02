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

import cn.dev33.satoken.model.wrapperInfo.SaDisableWrapperInfo;
import cn.dev33.satoken.util.SaTokenConsts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaDisableWrapperInfo 测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaDisableWrapperInfoTest {

	/** createDisabled 应创建带封禁时长与等级的禁用信息 */
	@Test
	void createDisabled() {
		SaDisableWrapperInfo info = SaDisableWrapperInfo.createDisabled(3600, 2);
		Assertions.assertTrue(info.isDisable);
		Assertions.assertEquals(3600, info.disableTime);
		Assertions.assertEquals(2, info.disableLevel);
		Assertions.assertTrue(info.getIsDisable());
		Assertions.assertEquals(3600, info.getDisableTime());
		Assertions.assertEquals(2, info.getDisableLevel());
	}

	/** createNotDisabled 应创建未封禁且等级为 NOT_DISABLE_LEVEL 的信息 */
	@Test
	void createNotDisabled() {
		SaDisableWrapperInfo info = SaDisableWrapperInfo.createNotDisabled();
		Assertions.assertFalse(info.isDisable);
		Assertions.assertEquals(0, info.disableTime);
		Assertions.assertEquals(SaTokenConsts.NOT_DISABLE_LEVEL, info.disableLevel);
	}

	/** createNotDisabled(cacheTime) 应保留缓存时长但不标记封禁 */
	@Test
	void createNotDisabledWithCacheTime() {
		SaDisableWrapperInfo info = SaDisableWrapperInfo.createNotDisabled(30);
		Assertions.assertFalse(info.isDisable);
		Assertions.assertEquals(30, info.disableTime);
		Assertions.assertEquals(SaTokenConsts.NOT_DISABLE_LEVEL, info.disableLevel);
	}

	/** 构造函数与链式 setter 应正确更新各字段 */
	@Test
	void constructorAndChainSetters() {
		SaDisableWrapperInfo info = new SaDisableWrapperInfo(true, -1, 3);
		Assertions.assertSame(info, info.setIsDisable(false).setDisableTime(0).setDisableLevel(0));
		Assertions.assertFalse(info.getIsDisable());
		Assertions.assertEquals(0, info.getDisableTime());
		Assertions.assertEquals(0, info.getDisableLevel());
		Assertions.assertTrue(info.toString().contains("SaDisableWrapperInfo"));
	}

}
