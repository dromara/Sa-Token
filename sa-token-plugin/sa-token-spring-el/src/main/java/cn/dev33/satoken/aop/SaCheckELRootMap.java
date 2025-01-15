/*
 * Copyright 2020-2099 sa-token.cc
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

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Sa-Token 注解鉴权 EL 表达式解析器的根数据对象
 *
 * @author click33
 * @since 1.40.0
 */
public class SaCheckELRootMap extends HashMap<String, Object> {

    /**
     * KEY标记：被切入的函数
     */
    public static final String KEY_METHOD = "method";

    /**
     * KEY标记：被切入的函数参数
     */
    public static final String KEY_ARGS = "args";

    /**
     * KEY标记：被切入的目标对象
     */
    public static final String KEY_TARGET = "target";

    /**
     * KEY标记：注解所在类对象引用
     */
    public static final String KEY_THIS = "this";

    /**
     * KEY标记：全局默认 StpLogic 对象
     */
    public static final String KEY_STP = "stp";

    /**
     * KEY标记：本次切入的 JoinPoint 对象
     */
    public static final String KEY_JOIN_POINT = "joinPoint";

    public SaCheckELRootMap(Method method, Object[] args, Object target) {
        this.put(KEY_METHOD, method);
        this.put(KEY_ARGS, args);
        this.put(KEY_TARGET, target);
    }

    /**
     * 获取 被切入的函数
     *
     * @return method 被切入的函数
     */
    public Method getMethod() {
        return (Method) this.get(KEY_METHOD);
    }

    /**
     * 获取 被切入的函数参数
     *
     * @return args 被切入的函数参数
     */
    public Object[] getArgs() {
        return (Object[]) this.get(KEY_ARGS);
    }

    /**
     * 获取 被切入的目标对象
     *
     * @return target 被切入的目标对象
     */
    public Object getTarget() {
        return this.get(KEY_TARGET);
    }

    /**
     * 获取 注解所在类对象引用
     *
     * @return this 注解所在类对象引用
     */
    public Object getThis() {
        return this.get(KEY_THIS);
    }

    /**
     * 获取本次切入的 JoinPoint 对象
     */
    public Object getJoinPoint() {
        return this.get(KEY_JOIN_POINT);
    }

    /**
     * 断言函数, 表达式执行结果为true才能通过
     *
     * @param flag 执行结果
     */
    public void NEED(boolean flag) {
        NEED(flag, SaErrorCode.CODE_UNDEFINED, "未通过 EL 表达式校验");
    }

    /**
     * 断言函数, 表达式执行结果为true才能通过，并在未通过时抛出 SaTokenException 异常，异常描述信息为 errorMessage
     *
     * @param flag 执行结果
     */
    public void NEED(boolean flag, String errorMessage) {
        NEED(flag, SaErrorCode.CODE_UNDEFINED, errorMessage);
    }

    /**
     * 断言函数, 表达式执行结果为true才能通过，并在未通过时抛出 SaTokenException 异常，异常码为 errorCode，异常描述信息为 errorMessage
     *
     * @param flag 执行结果
     */
    public void NEED(boolean flag, int errorCode, String errorMessage) {
        if(!flag) {
            throw new SaTokenException(errorCode, errorMessage);
        }
    }


}
