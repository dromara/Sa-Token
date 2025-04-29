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
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.message.handle.SaSsoMessageHandle;
import cn.dev33.satoken.sso.name.ParamName;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.sso.template.SaSsoTemplate;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token SSO 消息 处理器 - sso-server 端：处理校验 ticket 的请求
 *
 * @author click33
 * @since 1.43.0
 */
public class SaSsoMessageCheckTicketHandle implements SaSsoMessageHandle {

    /**
     * 获取所要处理的消息类型
     *
     * @return /
     */
    public String getHandlerType() {
        return SaSsoConsts.MESSAGE_CHECK_TICKET;
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

        // 1、获取参数
        SaRequest req = SaHolder.getRequest();
        SaSsoServerConfig ssoServerConfig = ssoServerTemplate.getServerConfig();
        String client = req.getParam(paramName.client);
        String ticket = req.getParamNotNull(paramName.ticket);
        String sloCallback = req.getParam(paramName.ssoLogoutCall);

        // 2、校验提供的client是否为非法字符
        if(SaSsoConsts.CLIENT_WILDCARD.equals(client)) {
            return SaResult.error("无效 client 标识：" + client);
        }

        // 3、校验签名
//        if(ssoServerConfig.getIsCheckSign()) {
//            ssoServerTemplate.getSignTemplate(client).checkRequest(req, paramName.client, paramName.ticket, paramName.ssoLogoutCall);
//        } else {
//            SaSsoManager.printNoCheckSignWarningByRuntime();
//        }

        // 4、校验ticket，获取 loginId
        Object loginId = ssoServerTemplate.checkTicket(ticket, client);
        if(SaFoxUtil.isEmpty(loginId)) {
            return SaResult.error("无效ticket：" + ticket);
        }

        // 5、注册此客户端的单点注销回调URL
        ssoServerTemplate.registerSloCallbackUrl(loginId, client, sloCallback);

        // 6、给 client 端响应结果
        long remainSessionTimeout = ssoServerTemplate.getStpLogic().getSessionTimeoutByLoginId(loginId);
        SaResult result = SaResult.data(loginId).set(paramName.remainSessionTimeout, remainSessionTimeout);
        result = ssoServerConfig.checkTicketAppendData.apply(loginId, result);
        return result;
    }

}
