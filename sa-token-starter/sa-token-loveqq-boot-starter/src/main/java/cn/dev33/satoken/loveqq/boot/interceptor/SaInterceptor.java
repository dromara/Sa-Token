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
package cn.dev33.satoken.loveqq.boot.interceptor;

import cn.dev33.satoken.context.model.SaTokenContextModelBox;
import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.fun.SaParamFunction;
import cn.dev33.satoken.loveqq.boot.utils.SaTokenContextUtil;
import cn.dev33.satoken.loveqq.boot.utils.SaTokenOperateUtil;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import com.kfyty.loveqq.framework.web.core.http.ServerRequest;
import com.kfyty.loveqq.framework.web.core.http.ServerResponse;
import com.kfyty.loveqq.framework.web.core.interceptor.HandlerInterceptor;
import com.kfyty.loveqq.framework.web.core.mapping.MethodMapping;

import java.lang.reflect.Method;

/**
 * Sa-Token 综合拦截器，提供注解鉴权和路由拦截鉴权能力
 *
 * @author click33
 * @since 1.31.0
 */
public class SaInterceptor implements HandlerInterceptor {
    /**
     * 是否打开注解鉴权，配置为 true 时注解鉴权才会生效，配置为 false 时，即使写了注解也不会进行鉴权
     */
    public boolean isAnnotation = true;

    /**
     * 认证前置函数：在注解鉴权之前执行
     * <p> 参数：路由处理函数指针
     */
    public SaParamFunction<Object> beforeAuth = handler -> {
    };

    /**
     * 认证函数：每次请求执行
     * <p> 参数：路由处理函数指针
     */
    public SaParamFunction<Object> auth = handler -> {
    };

    /**
     * 创建一个 Sa-Token 综合拦截器，默认带有注解鉴权能力
     */
    public SaInterceptor() {
    }

    /**
     * 创建一个 Sa-Token 综合拦截器，默认带有注解鉴权能力
     *
     * @param auth 认证函数，每次请求执行
     */
    public SaInterceptor(SaParamFunction<Object> auth) {
        this.auth = auth;
    }

    /**
     * 设置是否打开注解鉴权：配置为 true 时注解鉴权才会生效，配置为 false 时，即使写了注解也不会进行鉴权
     *
     * @param isAnnotation /
     * @return 对象自身
     */
    public SaInterceptor isAnnotation(boolean isAnnotation) {
        this.isAnnotation = isAnnotation;
        return this;
    }

    /**
     * 写入 [ 认证前置函数 ]: 在注解鉴权之前执行
     *
     * @param beforeAuth /
     * @return 对象自身
     */
    public SaInterceptor setBeforeAuth(SaParamFunction<Object> beforeAuth) {
        this.beforeAuth = beforeAuth;
        return this;
    }

    /**
     * 写入 [ 认证函数 ]: 每次请求执行
     *
     * @param auth /
     * @return 对象自身
     */
    public SaInterceptor setAuth(SaParamFunction<Object> auth) {
        this.auth = auth;
        return this;
    }


    // ----------------- 验证方法 -----------------

    /**
     * 每次请求之前触发的方法
     */
    @Override
    public boolean preHandle(ServerRequest request, ServerResponse response, MethodMapping handler) {
        SaTokenContextModelBox prev = SaTokenContextUtil.setContext(request, response);
        try {
            // 前置函数：在注解鉴权之前执行
            beforeAuth.run(handler);

            // 这里必须确保 handler 是 HandlerMethod 类型时，才能进行注解鉴权
            if (isAnnotation) {
                Method method = handler.getMappingMethod();
                SaAnnotationStrategy.instance.checkMethodAnnotation.accept(method);
            }

            // Auth 路由拦截鉴权校验
            auth.run(handler);
        } catch (StopMatchException e) {
            // StopMatchException 异常代表：停止匹配，进入Controller
        } catch (BackResultException e) {
            SaTokenOperateUtil.writeResult(response, e.getMessage());
            return false;
        } finally {
            SaTokenContextUtil.clearContext(prev);
        }

        // 通过验证
        return true;
    }
}
