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
package cn.dev33.satoken.solon.model;

import cn.dev33.satoken.context.SaTokenContextForReadOnly;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import org.noear.solon.core.handle.Context;

/**
 * <h2> 此为低版本(<1.42.0) 的上下文处理方案，基于 Solon 内部封装 Context.current() 读写上下文，仅做留档，如无必要请勿使用 </h2>
 *
 * @author noear
 * @since 1.4
 */
public class SaContextForSolon implements SaTokenContextForReadOnly {

    /**
     * 获取当前请求的Request对象
     */
    @Override
    public SaRequest getRequest() {
        return new SaRequestForSolon();
    }

    /**
     * 获取当前请求的Response对象
     */
    @Override
    public SaResponse getResponse() {
        return new SaResponseForSolon();
    }

    /**
     * 获取当前请求的 [存储器] 对象
     */
    @Override
    public SaStorage getStorage() {
        return new SaStorageForSolon();
    }

    /**
     * 此上下文是否有效
     * @return /
     */
    public boolean isValid() {
        return Context.current() != null;
    }

}
