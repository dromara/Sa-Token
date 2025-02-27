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

/**
 * 防火墙策略校验钩子函数：请求 path 危险字符校验
 *
 * @author click33
 * @since 1.41.0
 */
public class SaFirewallCheckHookForPathDangerCharacter implements SaFirewallCheckHook {

    /**
     * 默认实例
     */
    public static SaFirewallCheckHookForPathDangerCharacter instance = new SaFirewallCheckHookForPathDangerCharacter();

    /**
     * 请求 path 不允许出现的危险字符
     */
    public String[] dangerCharacter = {
            "//",           // //
            "\\",			// \
            "%2e", "%2E",	// .
            "%2f", "%2F",	// /
            "%5c", "%5C",	// \
            ";", "%3b", "%3B",	// ;    // 参考资料：https://mp.weixin.qq.com/s/77CIDZbgBwRunJeluofPTA
            "%25",			// 空格
            "\0", "%00",	// 空字符
            "\n", "%0a", "%0A",	// 换行符
            "\r", "%0d", "%0D",	// 回车符
            "\u2028",     // 行分隔符
            "\u2029"    // 段分隔符
    };

    /**
     * 重载配置
     * @param character 危险字符列表
     */
    public void resetConfig(String... character) {
        this.dangerCharacter = character;
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
        String requestPath = req.getRequestPath();
        for (String item : dangerCharacter) {
            if (requestPath.contains(item)) {
                throw new RequestPathInvalidException("非法请求：" + requestPath, requestPath);
            }
        }
    }

}
