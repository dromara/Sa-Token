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
import cn.dev33.satoken.exception.RequestPathInvalidException;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * 防火墙策略校验钩子函数：请求 path 禁止字符校验
 *
 * @author click33
 * @since 1.41.0
 */
public class SaFirewallCheckHookForPathBannedCharacter implements SaFirewallCheckHook {

    /**
     * 默认实例
     */
    public static SaFirewallCheckHookForPathBannedCharacter instance = new SaFirewallCheckHookForPathBannedCharacter();

    /**
     * 执行的方法
     *
     * @param req 请求对象
     * @param res 响应对象
     * @param extArg 预留扩展参数
     */
    @Override
    public void execute(SaRequest req, SaResponse res, Object extArg) {
        // 非可打印 ASCII 字符检查
        String requestPath = req.getRequestPath();
        if(SaFoxUtil.hasNonPrintableASCII(requestPath)) {
            throw new RequestPathInvalidException("请求 path 包含禁止字符：" + requestPath, requestPath);
        }

    }

}
