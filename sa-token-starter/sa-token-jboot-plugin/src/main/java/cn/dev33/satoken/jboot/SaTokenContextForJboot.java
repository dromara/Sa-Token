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
package cn.dev33.satoken.jboot;

import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.servlet.model.SaStorageForServlet;
import io.jboot.web.controller.JbootControllerContext;

/**
 * Sa-Token 上线文处理器 [Jboot 版本实现]
 */
public class SaTokenContextForJboot implements SaTokenContext {
    /**
     * 获取当前请求的Request对象
     */
    @Override
    public SaRequest getRequest() {
        return new SaRequestForServlet(JbootControllerContext.get().getRequest());
    }

    /**
     * 获取当前请求的Response对象
     */
    @Override
    public SaResponse getResponse() {
        return new SaResponseForServlet(JbootControllerContext.get().getResponse());
    }

    /**
     * 获取当前请求的 [存储器] 对象
     */
    @Override
    public SaStorage getStorage() {
        return new SaStorageForServlet(JbootControllerContext.get().getRequest());
    }

    /**
     * 校验指定路由匹配符是否可以匹配成功指定路径
     */
    @Override
    public boolean matchPath(String pattern, String path) {
        return PathAnalyzer.get(pattern).matches(path);
    }

    @Override
    public boolean isValid() {
        return SaTokenContext.super.isValid();
    }
}
