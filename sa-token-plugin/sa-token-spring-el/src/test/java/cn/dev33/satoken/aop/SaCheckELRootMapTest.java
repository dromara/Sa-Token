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
package cn.dev33.satoken.aop;

import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaTokenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * {@link SaCheckELRootMap} 根数据对象测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaCheckELRootMapTest {

    /** 构造函数应该填充 method、args、target，且各 getter 应该能读回 */
    @Test
    public void constructorAndGetters() {
        Method method = Object.class.getMethods()[0];
        Object[] args = {"a", 1};
        Object target = new Object();

        SaCheckELRootMap rootMap = new SaCheckELRootMap(method, args, target);

        Assertions.assertSame(method, rootMap.getMethod());
        Assertions.assertSame(args, rootMap.getArgs());
        Assertions.assertSame(target, rootMap.getTarget());
    }

    /** put 进去的 this 和 joinPoint 应该能通过对应 getter 读回 */
    @Test
    public void getThisAndGetJoinPoint() {
        SaCheckELRootMap rootMap = new SaCheckELRootMap(Object.class.getMethods()[0], new Object[0], null);
        Object target = new Object();

        rootMap.put(SaCheckELRootMap.KEY_THIS, target);
        rootMap.put(SaCheckELRootMap.KEY_JOIN_POINT, "joinPoint");

        Assertions.assertSame(target, rootMap.getThis());
        Assertions.assertEquals("joinPoint", rootMap.getJoinPoint());
    }

    /** NEED(true) 时应该不抛异常 */
    @Test
    public void need_true_pass() {
        SaCheckELRootMap rootMap = new SaCheckELRootMap(Object.class.getMethods()[0], new Object[0], null);
        Assertions.assertDoesNotThrow(() -> rootMap.NEED(true));
        Assertions.assertDoesNotThrow(() -> rootMap.NEED(true, "自定义消息"));
        Assertions.assertDoesNotThrow(() -> rootMap.NEED(true, 30206, "自定义消息"));
    }

    /** NEED(false) 时应该抛出默认消息的 SaTokenException */
    @Test
    public void need_false_defaultMessage() {
        SaCheckELRootMap rootMap = new SaCheckELRootMap(Object.class.getMethods()[0], new Object[0], null);
        SaTokenException e = Assertions.assertThrows(SaTokenException.class, () -> rootMap.NEED(false));
        Assertions.assertEquals("未通过 EL 表达式校验", e.getMessage());
        Assertions.assertEquals(SaErrorCode.CODE_UNDEFINED, e.getCode());
    }

    /** NEED(false, errorMessage) 时应该抛出携带自定义消息的 SaTokenException */
    @Test
    public void need_false_customMessage() {
        SaCheckELRootMap rootMap = new SaCheckELRootMap(Object.class.getMethods()[0], new Object[0], null);
        SaTokenException e = Assertions.assertThrows(SaTokenException.class,
                () -> rootMap.NEED(false, "自定义校验失败"));
        Assertions.assertEquals("自定义校验失败", e.getMessage());
    }

    /** NEED(false, errorCode, errorMessage) 时应该抛出携带自定义错误码和消息的 SaTokenException */
    @Test
    public void need_false_customCodeAndMessage() {
        SaCheckELRootMap rootMap = new SaCheckELRootMap(Object.class.getMethods()[0], new Object[0], null);
        SaTokenException e = Assertions.assertThrows(SaTokenException.class,
                () -> rootMap.NEED(false, 30206, "自定义校验失败"));
        Assertions.assertEquals(30206, e.getCode());
        Assertions.assertEquals("自定义校验失败", e.getMessage());
    }

}
