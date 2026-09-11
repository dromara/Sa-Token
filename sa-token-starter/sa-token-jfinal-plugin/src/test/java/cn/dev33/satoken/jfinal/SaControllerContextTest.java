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
package cn.dev33.satoken.jfinal;

import com.jfinal.core.Controller;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

/**
 * {@link SaControllerContext} 线程绑定测试
 */
public class SaControllerContextTest {

    /** 每个用例结束后把 ThreadLocal 清掉，别脏到别的测试 */
    @AfterEach
    public void tearDown() {
        SaControllerContext.release();
    }

    /** 应测尽测：测试 SaControllerContext 无参构造 */
    @Test
    public void constructor_canNew() {
        Assertions.assertNotNull(new SaControllerContext());
    }

    /** hold 之后 get 应该拿到同一个 Controller */
    @Test
    public void hold_thenGet() {
        Controller controller = mock(Controller.class);
        SaControllerContext.hold(controller);
        Assertions.assertSame(controller, SaControllerContext.get());
    }

    /** 没 hold 的时候 get 应该是 null */
    @Test
    public void get_withoutHold_shouldBeNull() {
        Assertions.assertNull(SaControllerContext.get());
    }

    /** release 之后当前线程就不该再拿得到 Controller */
    @Test
    public void release_shouldClear() {
        SaControllerContext.hold(mock(Controller.class));
        SaControllerContext.release();
        Assertions.assertNull(SaControllerContext.get());
    }
}
