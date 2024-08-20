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
package cn.dev33.satoken.annotation.handler;

import cn.dev33.satoken.annotation.SaCheckHttpDigest;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestUtil;
import cn.dev33.satoken.util.SaFoxUtil;

import java.lang.reflect.Method;

/**
 * 注解 SaCheckHttpDigest 的处理器
 *
 * @author click33
 * @since 2024/8/2
 */
public class SaCheckHttpDigestHandler implements SaAnnotationHandlerInterface<SaCheckHttpDigest> {

    @Override
    public Class<SaCheckHttpDigest> getHandlerAnnotationClass() {
        return SaCheckHttpDigest.class;
    }

    @Override
    public void checkMethod(SaCheckHttpDigest at, Method method) {
        _checkMethod(at.username(), at.password(), at.realm(), at.value());
    }

    public static void _checkMethod(String username, String password, String realm, String value) {
        // 如果配置了 value，则以 value 优先
        if(SaFoxUtil.isNotEmpty(value)){
            String[] arr = value.split(":");
            if(arr.length != 2){
                throw new SaTokenException("注解参数配置错误，格式应如：username:password");
            }
            SaHttpDigestUtil.check(arr[0], arr[1]);
            return;
        }

        // 如果配置了 username，则分别获取参数
        if(SaFoxUtil.isNotEmpty(username)){
            SaHttpDigestUtil.check(username, password, realm);
            return;
        }

        // 都没有配置，则根据全局配置参数进行校验
        SaHttpDigestUtil.check();
    }

}