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
package cn.dev33.satoken.core.fun;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.fun.IsRunFunction;

/**
 * IsRunFunction 测试
 *
 * @author click33
 * @since 1.46.0
 */
public class IsRunFunctionTest {

	/** isRun 为 true 时应执行 exe 分支 */
	@Test
	public void exeRunsWhenTrue() {
		class TempClass {
			int count = 1;
		}
		TempClass obj = new TempClass();

		IsRunFunction fun = new IsRunFunction(true);
		fun.exe(() -> obj.count = 2).noExe(() -> obj.count = 3);

		Assertions.assertEquals(2, obj.count);
	}

	/** isRun 为 false 时应执行 noExe 分支 */
	@Test
	public void noExeRunsWhenFalse() {
		class TempClass {
			int count = 1;
		}
		TempClass obj = new TempClass();

		IsRunFunction fun = new IsRunFunction(false);
		fun.exe(() -> obj.count = 2).noExe(() -> obj.count = 3);

		Assertions.assertEquals(3, obj.count);
	}

}
