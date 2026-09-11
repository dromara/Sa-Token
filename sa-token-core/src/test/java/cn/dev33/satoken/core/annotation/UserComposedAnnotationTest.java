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
package cn.dev33.satoken.core.annotation;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.fun.strategy.SaGetAnnotationFunction;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.test.fixture.MergedAnnotationLookup;
import cn.dev33.satoken.test.fixture.RecordingStpInterface;
import cn.dev33.satoken.test.fixture.UserAuthApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 用户组合鉴权注解应走策略扫描，行为和直接写内置注解一样。
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class UserComposedAnnotationTest {

	private SaGetAnnotationFunction savedGetAnnotation;

	/** 每个用例开始前挂上用户项目那种元注解查找，并给账号准备权限 */
	@BeforeEach
	void setUp() {
		savedGetAnnotation = SaAnnotationStrategy.instance.getAnnotation;
		SaAnnotationStrategy.instance.getAnnotation = MergedAnnotationLookup::get;
		SaManager.setStpInterface(new RecordingStpInterface(
				Arrays.asList("user:add", "user:view"),
				Arrays.asList("user")));
	}

	/** 每个用例结束后把 getAnnotation 策略还原 */
	@AfterEach
	void tearDown() {
		SaAnnotationStrategy.instance.getAnnotation = savedGetAnnotation;
	}

	/** 有 user:add 时，组合注解和内置注解都应该放行 */
	@Test
	void composedAnnotation_sameAsBuiltin_whenHasPermission() throws Exception {
		Method composed = UserAuthApi.class.getMethod("addUser");
		Method builtin = UserAuthApi.class.getMethod("addUserBuiltin");
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			Assertions.assertDoesNotThrow(() -> checkMethodAnnotation(composed));
			Assertions.assertDoesNotThrow(() -> checkMethodAnnotation(builtin));
		});
	}

	/** 没有 user:delete 时，组合注解路径和内置注解都应该抛 NotPermissionException */
	@Test
	void composedAnnotation_sameAsBuiltin_whenMissingPermission() throws Exception {
		Method composed = UserAuthApi.class.getMethod("addUser");
		Method deleteUser = UserAuthApi.class.getMethod("deleteUser");
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			Assertions.assertDoesNotThrow(() -> checkMethodAnnotation(composed));
			Assertions.assertThrows(NotPermissionException.class, () -> checkMethodAnnotation(deleteUser));
		});
	}

	/** 同一条 user:add / user:delete，手写 StpUtil 和注解扫描的放行、失败类型应该一致 */
	@Test
	void userAdd_annotation_sameAsStpUtil() throws Exception {
		Method addUser = UserAuthApi.class.getMethod("addUser");
		Method deleteUser = UserAuthApi.class.getMethod("deleteUser");
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			Assertions.assertDoesNotThrow(() -> StpUtil.checkPermission("user:add"));
			Assertions.assertDoesNotThrow(() -> checkMethodAnnotation(addUser));
			NotPermissionException fromApi = Assertions.assertThrows(NotPermissionException.class,
					() -> StpUtil.checkPermission("user:delete"));
			NotPermissionException fromAnnotation = Assertions.assertThrows(NotPermissionException.class,
					() -> checkMethodAnnotation(deleteUser));
			Assertions.assertEquals(fromApi.getPermission(), fromAnnotation.getPermission());
			Assertions.assertEquals(fromApi.getLoginType(), fromAnnotation.getLoginType());
		});
	}

	/** 默认 getAnnotation 看不到组合注解，用户不重写策略时 @RequireUserAdd 不会触发权限校验 */
	@Test
	void defaultLookup_ignoresComposedAnnotation() throws Exception {
		SaAnnotationStrategy.instance.getAnnotation = savedGetAnnotation;
		Method composed = UserAuthApi.class.getMethod("addUser");
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			Assertions.assertDoesNotThrow(() -> checkMethodAnnotation(composed));
		});
	}

	/** 按当前策略扫描这个方法上的鉴权注解 */
	private void checkMethodAnnotation(Method method) {
		SaAnnotationStrategy.instance.checkMethodAnnotation.accept(method);
	}

}
