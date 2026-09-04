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
package cn.dev33.satoken.core.util;

import cn.dev33.satoken.util.SaSugar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SaSugar 测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaSugarTest {

	/** get 与 exe 应执行传入 Lambda */
	@Test
	void invokesLambda() {
		AtomicBoolean executed = new AtomicBoolean();

		Assertions.assertEquals("value", SaSugar.get(() -> "value"));
		SaSugar.exe(() -> executed.set(true));

		Assertions.assertTrue(executed.get());
	}

	/** 默认构造函数应可正常创建实例 */
	@Test
	void defaultConstructor() {
		Assertions.assertDoesNotThrow(SaSugar::new);
	}

}
