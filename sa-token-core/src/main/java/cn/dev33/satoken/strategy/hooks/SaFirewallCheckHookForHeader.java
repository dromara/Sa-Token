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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 防火墙策略校验钩子函数：请求头检测
 *
 * @author click33
 * @since 1.41.0
 */
public class SaFirewallCheckHookForHeader implements SaFirewallCheckHook {

    /**
     * 默认实例
     */
    public static SaFirewallCheckHookForHeader instance = new SaFirewallCheckHookForHeader();

    /**
     * 不允许的请求头列表
     */
    public List<String> notAllowHeaderNames = new ArrayList<>();

    public SaFirewallCheckHookForHeader() {
    }

    /**
     * 配置
     * @param notAllowHeaderNames 不允许的请求头列表 (先清空原来的，再添加上新的)
     */
    public void resetConfig(String... notAllowHeaderNames) {
        this.notAllowHeaderNames.clear();
        this.notAllowHeaderNames.addAll(Arrays.asList(notAllowHeaderNames));
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
        for (String headerName : notAllowHeaderNames) {
            if(req.getHeader(headerName) != null) {
                throw new FirewallCheckException("非法请求头：" + headerName);
            }
        }
    }

}
