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

import cn.dev33.satoken.exception.RequestPathInvalidException;

/**
 * 函数式接口：当请求 path 校验不通过时处理方案的算法
 *
 * @author click33
 * @since 1.37.0
 */
@FunctionalInterface
public interface SaRequestPathInvalidHandleFunction {

    /**
     * 执行函数
     * @param e 请求 path 无效的异常对象
     * @param extArg1 扩展参数1
     * @param extArg2 扩展参数2
     */
    void run(RequestPathInvalidException e, Object extArg1, Object extArg2);

}