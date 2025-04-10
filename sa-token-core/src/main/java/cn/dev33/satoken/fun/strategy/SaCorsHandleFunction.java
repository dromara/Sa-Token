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
package cn.dev33.satoken.fun.strategy;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;

/**
 * CORS 跨域策略处理函数
 *
 * @author click33
 * @since 1.42.0
 */
@FunctionalInterface
public interface SaCorsHandleFunction {

    /**
     * CORS 策略处理函数
     *
     * @param req 请求包装对象
     * @param res 响应包装对象
     * @param sto 数据读写对象
     */
    void execute(
            SaRequest req,
            SaResponse res,
            SaStorage sto
    );

}