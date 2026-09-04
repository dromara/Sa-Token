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

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaLoginModel 兼容类测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaLoginModelTest {

	/** SaLoginModel 应继承 SaLoginParameter 并可实例化 */
	@Test
	void instance_extendsSaLoginParameter() {
		SaLoginModel model = new SaLoginModel();
		Assertions.assertInstanceOf(SaLoginParameter.class, model);
		model.setTimeout(1800);
		Assertions.assertEquals(1800L, model.getTimeout());
	}

}
