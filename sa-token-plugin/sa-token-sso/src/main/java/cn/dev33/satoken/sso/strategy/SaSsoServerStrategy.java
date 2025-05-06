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
package cn.dev33.satoken.sso.strategy;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.fun.SaParamFunction;
import cn.dev33.satoken.sso.function.CheckTicketAppendDataFunction;
import cn.dev33.satoken.sso.function.DoLoginHandleFunction;
import cn.dev33.satoken.sso.function.NotLoginViewFunction;
import cn.dev33.satoken.sso.function.SendRequestFunction;
import cn.dev33.satoken.util.SaResult;

import java.util.Map;

/**
 * Sa-Token SSO Server 相关策略
 *
 * @author click33
 * @since 1.43.0
 */
public class SaSsoServerStrategy {

    /**
     * 发送 Http 请求的处理函数
     */
    public SendRequestFunction sendRequest = url -> {
        return SaManager.getSaHttpTemplate().get(url);
    };

    /**
     * 使用异步模式执行一个任务
     */
    public SaParamFunction<SaFunction> asyncRun = fun -> {
        new Thread(() -> {
            fun.run();
        }).start();
    };

    /**
     * 未登录时返回的 View
     */
    public NotLoginViewFunction notLoginView = () -> {
        return "当前会话在 SSO-Server 认证中心尚未登录（当前未配置登录视图）";
    };

    /**
     * SSO-Server端：登录函数
     */
    public DoLoginHandleFunction doLoginHandle = (name, pwd) -> {
        return SaResult.error();
    };

    /**
     * SSO-Server端：在授权重定向之前的通知
     */
    public SaParamFunction<String> jumpToRedirectUrlNotice = (redirectUrl) -> {

    };

    /**
     * SSO-Server端：在校验 ticket 后，给 sso-client 端追加返回信息的函数
     */
    public CheckTicketAppendDataFunction checkTicketAppendData = (loginId, result) -> {
        return result;
    };

    /**
     * 发送 Http 请求，并将响应结果转换为 SaResult
     *
     * @param url 请求地址
     * @return 返回的结果
     */
    public SaResult requestAsSaResult(String url) {
        String body = sendRequest.apply(url);
        Map<String, Object> map = SaManager.getSaJsonTemplate().jsonToMap(body);
        return new SaResult(map);
    }

}
