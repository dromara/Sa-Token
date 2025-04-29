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
package cn.dev33.satoken.sso.message.handle.server;


import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.message.handle.SaSsoMessageHandle;
import cn.dev33.satoken.sso.name.ParamName;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.sso.template.SaSsoTemplate;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token SSO 消息 处理器 - sso-server 端：处理 单点注销 的请求
 *
 * @author click33
 * @since 1.43.0
 */
public class SaSsoMessageSignoutHandle implements SaSsoMessageHandle {

    /**
     * 获取所要处理的消息类型
     *
     * @return /
     */
    public String getHandlerType() {
        return SaSsoConsts.MESSAGE_SIGNOUT;
    }

    /**
     * 执行方法
     *
     * @param ssoTemplate /
     * @param message /
     * @return /
     */
    public Object handle(SaSsoTemplate ssoTemplate, SaSsoMessage message) {
        SaSsoServerTemplate ssoServerTemplate = (SaSsoServerTemplate) ssoTemplate;
        ParamName paramName = ssoServerTemplate.paramName;

        if( ! ssoServerTemplate.getServerConfig().getIsSlo()) {
            return SaResult.error("当前 sso-server 端未开启单点注销功能");
        }

        // 获取参数
        SaRequest req = SaHolder.getRequest();
        String loginId = req.getParam(paramName.loginId);

        // step.2 单点注销
        ssoServerTemplate.ssoLogout(loginId);

        // 响应
        return SaResult.ok();
    }

}
