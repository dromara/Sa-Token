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
package cn.dev33.satoken.loveqq.boot.support;

import com.kfyty.loveqq.framework.core.support.AntPathMatcher;
import com.kfyty.loveqq.framework.core.support.PatternMatcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaPathMatcherHolder} 路由匹配器读写测试
 */
public class SaPathMatcherHolderTest {

	/** 每个用例结束后把静态匹配器清掉，避免污染别的用例 */
	@AfterEach
	public void tearDown() {
		SaPathMatcherHolder.setPathMatcher(null);
	}

	/** 没手动设置时应该懒加载出一个 AntPathMatcher */
	@Test
	public void getPathMatcher_lazyDefault() {
		SaPathMatcherHolder.pathMatcher = null;
		PatternMatcher matcher = SaPathMatcherHolder.getPathMatcher();
		Assertions.assertNotNull(matcher);
		Assertions.assertSame(matcher, SaPathMatcherHolder.getPathMatcher());
	}

	/** setPathMatcher 之后 get 应该拿到同一份 */
	@Test
	public void setPathMatcher_shouldHold() {
		AntPathMatcher matcher = new AntPathMatcher();
		SaPathMatcherHolder.setPathMatcher(matcher);
		Assertions.assertSame(matcher, SaPathMatcherHolder.getPathMatcher());
	}

	/** match 应该按 Ant 风格判断路径 */
	@Test
	public void match_antStyle() {
		SaPathMatcherHolder.setPathMatcher(new AntPathMatcher());
		Assertions.assertTrue(SaPathMatcherHolder.match("/user/**", "/user/1"));
		Assertions.assertFalse(SaPathMatcherHolder.match("/user/**", "/admin/1"));
	}

}
