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
package cn.dev33.satoken.jfinal;

import cn.dev33.satoken.context.SaTokenContextForReadOnly;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.servlet.model.SaStorageForServlet;
import cn.dev33.satoken.strategy.SaStrategy;

/**
 * Sa-Token 上线文处理器 [Jfinal 版本实现]
 */
public class SaTokenContextForJfinal implements SaTokenContextForReadOnly {

    public SaTokenContextForJfinal() {
        // 重写路由匹配算法
        SaStrategy.instance.routeMatcher = (pattern, path) -> {
            return PathAnalyzer.get(pattern).matches(path);
        };
    }

    /**
     * 获取当前请求的Request对象
     */
    @Override
    public SaRequest getRequest() {
        return new SaRequestForServlet(SaControllerContext.get().getRequest());
    }

    /**
     * 获取当前请求的Response对象
     */
    @Override
    public SaResponse getResponse() {
        return new SaResponseForServlet(SaControllerContext.get().getResponse());
    }

    /**
     * 获取当前请求的 [存储器] 对象
     */
    @Override
    public SaStorage getStorage() {
        return new SaStorageForServlet(SaControllerContext.get().getRequest());
    }

    @Override
    public boolean isValid() {
        return SaControllerContext.get() != null;
    }

}
