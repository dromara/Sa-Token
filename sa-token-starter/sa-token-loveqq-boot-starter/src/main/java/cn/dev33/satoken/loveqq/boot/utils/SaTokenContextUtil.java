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
package cn.dev33.satoken.loveqq.boot.utils;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaTokenContextForThreadLocalStaff;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.context.model.SaTokenContextModelBox;
import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.fun.SaRetGenericFunction;
import cn.dev33.satoken.loveqq.boot.model.LoveqqSaRequest;
import cn.dev33.satoken.loveqq.boot.model.LoveqqSaResponse;
import cn.dev33.satoken.loveqq.boot.model.LoveqqSaStorage;
import com.kfyty.loveqq.framework.web.core.http.ServerRequest;
import com.kfyty.loveqq.framework.web.core.http.ServerResponse;

/**
 * SaTokenContext 上下文读写工具类
 *
 * @author click33
 * @since 1.42.0
 */
public class SaTokenContextUtil {
    /**
     * 写入当前上下文
     * 并返回当前的上下文，以支持 loveqq-framework 的 servlet/reactor 的统一配置
     *
     * @param request  /
     * @param response /
     */
    public static SaTokenContextModelBox setContext(ServerRequest request, ServerResponse response) {
        SaTokenContextModelBox prev = SaTokenContextForThreadLocalStaff.getModelBoxOrNull();
        SaRequest req = new LoveqqSaRequest(request);
        SaResponse res = new LoveqqSaResponse(response);
        SaStorage stg = new LoveqqSaStorage(request);
        SaManager.getSaTokenContext().setContext(req, res, stg);
        return prev;
    }

    /**
     * 写入上下文对象, 并在执行函数后将其清除
     *
     * @param request  /
     * @param response /
     * @param fun      /
     */
    public static void setContext(ServerRequest request, ServerResponse response, SaFunction fun) {
        SaTokenContextModelBox prev = setContext(request, response);
        try {
            fun.run();
        } finally {
            clearContext(prev);
        }
    }

    /**
     * 写入上下文对象, 并在执行函数后将其清除
     *
     * @param request  /
     * @param response /
     * @param fun      /
     * @param <T>      /
     * @return /
     */
    public static <T> T setContext(ServerRequest request, ServerResponse response, SaRetGenericFunction<T> fun) {
        SaTokenContextModelBox prev = setContext(request, response);
        try {
            return fun.run();
        } finally {
            clearContext(prev);
        }
    }

    /**
     * 清除当前上下文
     * 并恢复之前的上下文，以支持 loveqq-framework 的 servlet/reactor 的统一配置
     */
    public static void clearContext(SaTokenContextModelBox prev) {
        if (prev == null) {
            SaManager.getSaTokenContext().clearContext();
        } else {
            SaManager.getSaTokenContext().setContext(prev.getRequest(), prev.getResponse(), prev.getStorage());
        }
    }

    /**
     * 获取当前 ModelBox
     *
     * @return /
     */
    public static SaTokenContextModelBox getModelBox() {
        return SaManager.getSaTokenContext().getModelBox();
    }

    /**
     * 获取当前 Request
     *
     * @return /
     */
    public static ServerRequest getRequest() {
        return (ServerRequest) getModelBox().getRequest().getSource();
    }

    /**
     * 获取当前 Response
     *
     * @return /
     */
    public static ServerResponse getResponse() {
        return (ServerResponse) getModelBox().getResponse().getSource();
    }
}
