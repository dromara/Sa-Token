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


import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.message.handle.SaSsoMessageHandle;
import cn.dev33.satoken.sso.model.TicketModel;
import cn.dev33.satoken.sso.name.ParamName;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.sso.template.SaSsoTemplate;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.util.SaResult;

/**
 * SSO 消息处理器 - sso-server 端：处理校验 ticket 的请求
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

        // 1、获取对象
        SaSsoServerTemplate ssoServerTemplate = (SaSsoServerTemplate) ssoTemplate;
        ParamName paramName = ssoServerTemplate.paramName;
        StpLogic stpLogic = ssoServerTemplate.getStpLogicOrGlobal();
        String client = message.getString(paramName.client);
        String ticket = message.getValueNotNull(paramName.ticket).toString();
        String sloCallback = message.getString(paramName.ssoLogoutCall);

        // 2、校验ticket，获取 loginId
        TicketModel ticketModel = ssoServerTemplate.checkTicketParamAndDelete(ticket, client);
        Object loginId = ticketModel.getLoginId();

        // 3、注册此客户端的登录信息
        ssoServerTemplate.registerSloCallbackUrl(loginId, client, sloCallback);

        // 4、给 client 端响应结果
        SaResult result = SaResult.ok();
        result.setData(loginId); // 兼容历史版本
        result.set(paramName.loginId, loginId);
        result.set(paramName.tokenValue, ticketModel.getTokenValue());
        result.set(paramName.deviceId, stpLogic.getLoginDeviceIdByToken(ticketModel.getTokenValue()));
        result.set(paramName.remainTokenTimeout, stpLogic.getTokenTimeout(ticketModel.getTokenValue()));
        result.set(paramName.remainSessionTimeout, stpLogic.getSessionTimeoutByLoginId(loginId));
        result = ssoServerTemplate.strategy.checkTicketAppendData.apply(loginId, result);
        return result;
    }

}
