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
import cn.dev33.satoken.strategy.SaStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 防火墙策略校验钩子函数：Host 检测
 *
 * @author click33
 * @since 1.41.0
 */
public class SaFirewallCheckHookForHost implements SaFirewallCheckHook {

    /**
     * 默认实例
     */
    public static SaFirewallCheckHookForHost instance = new SaFirewallCheckHookForHost();

    /**
     * 是否校验 host，默认关闭
     */
    public boolean isCheckHost = false;

    /**
     * 允许的 host 列表，允许通配符
     */
    public List<String> allowHosts = new ArrayList<>();

    /**
     * 重载配置
     * @param isCheckHost 是否校验 host
     * @param allowHosts 允许的 host 列表 (先清空原来的，再添加上新的)
     */
    public void resetConfig(boolean isCheckHost, String... allowHosts) {
        this.isCheckHost = isCheckHost;
        this.allowHosts.clear();
        this.allowHosts.addAll(Arrays.asList(allowHosts));
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
        if(isCheckHost) {
            String host = req.getHost();
            if( ! SaStrategy.instance.hasElement.apply(allowHosts, host) ) {
                throw new FirewallCheckException("非法请求 host：" + host);
            }
        }
    }

}
