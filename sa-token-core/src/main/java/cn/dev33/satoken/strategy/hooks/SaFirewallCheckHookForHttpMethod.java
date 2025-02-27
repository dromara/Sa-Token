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
package cn.dev33.satoken.strategy.hooks;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.exception.FirewallCheckException;
import cn.dev33.satoken.router.SaHttpMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 防火墙策略校验钩子函数：请求 Method 检测
 *
 * @author click33
 * @since 1.41.0
 */
public class SaFirewallCheckHookForHttpMethod implements SaFirewallCheckHook {

    /**
     * 默认实例
     */
    public static SaFirewallCheckHookForHttpMethod instance = new SaFirewallCheckHookForHttpMethod();

    /**
     * 是否校验 请求Method，默认开启
     */
    public boolean isCheckMethod = true;

    /**
     * 允许的 HttpMethod 列表
     */
    public List<String> allowMethods = new ArrayList<>();

    public SaFirewallCheckHookForHttpMethod() {
        // 默认允许的 HttpMethod 列表
        allowMethods.add(SaHttpMethod.GET.name());
        allowMethods.add(SaHttpMethod.POST.name());
        allowMethods.add(SaHttpMethod.PUT.name());
        allowMethods.add(SaHttpMethod.DELETE.name());
        allowMethods.add(SaHttpMethod.HEAD.name());
        allowMethods.add(SaHttpMethod.OPTIONS.name());
        allowMethods.add(SaHttpMethod.PATCH.name());
        allowMethods.add(SaHttpMethod.TRACE.name());
        allowMethods.add(SaHttpMethod.CONNECT.name());
    }

    /**
     * 配置
     * @param isCheckMethod 是否校验 Method
     * @param methods 允许的 HttpMethod 列表 (先清空原来的，再添加上新的)
     */
    public void resetConfig(boolean isCheckMethod, String... methods) {
        this.isCheckMethod = isCheckMethod;
        this.allowMethods.clear();
        this.allowMethods.addAll(Arrays.asList(methods));
    }

    /**
     * 执行的方法
     *
     * @param req 请求对象
     * @param res 响应对象
     * @param extArg 预留扩展参数
     */
    @Override
    public void execute(SaRequest req, SaResponse res, Object extArg) {
        if(isCheckMethod) {
            String method = req.getMethod();
            if( ! allowMethods.contains(method) ) {
                throw new FirewallCheckException("非法请求 Method：" + method);
            }
        }
    }

}
