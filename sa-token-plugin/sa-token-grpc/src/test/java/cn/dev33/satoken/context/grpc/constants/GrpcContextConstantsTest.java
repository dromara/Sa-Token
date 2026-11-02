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
package cn.dev33.satoken.context.grpc.constants;

import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Metadata Key 应该和 Same-Token / JUST_CREATED 常量对齐。
 */
public class GrpcContextConstantsTest {

	/** 应测尽测：测试 GrpcContextConstants 无参构造 */
	@Test
	public void ctor_canNew() {
		new GrpcContextConstants();
	}

	/** 两个 Key 的名字会被 gRPC 收成小写 */
	@Test
	public void keys_matchSaTokenNamesLowerCase() {
		Assertions.assertEquals(SaSameUtil.SAME_TOKEN.toLowerCase(), GrpcContextConstants.SA_SAME_TOKEN.name());
		Assertions.assertEquals(SaTokenConsts.JUST_CREATED_NOT_PREFIX.toLowerCase(),
				GrpcContextConstants.SA_JUST_CREATED_NOT_PREFIX.name());
	}

}
