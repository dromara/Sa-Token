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

import cn.dev33.satoken.stp.StpLogic;

import java.util.function.Function;

/**
 * 函数式接口：创建 StpLogic 的算法
 *
 * <p>  参数：账号体系标识  </p>
 * <p>  返回：创建好的 StpLogic 对象  </p>
 *
 * @author click33
 * @since 1.35.0.RC
 */
@FunctionalInterface
public interface SaCreateStpLogicFunction extends Function<String, StpLogic> {

}