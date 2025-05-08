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
package cn.dev33.satoken.sso.template;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaSignConfig;
import cn.dev33.satoken.sign.SaSignTemplate;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.message.handle.client.SaSsoMessageLogoutCallHandle;
import cn.dev33.satoken.sso.strategy.SaSsoClientStrategy;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

import java.util.Map;

/**
 * SSO 模板方法类 （Client端）
 *
 * @author click33
 * @since 1.38.0
 */
public class SaSsoClientTemplate extends SaSsoTemplate {

    /**
     * Client 相关策略
     */
    public SaSsoClientStrategy strategy = new SaSsoClientStrategy();

    public SaSsoClientTemplate() {
        super.messageHolder.addHandle(new SaSsoMessageLogoutCallHandle());
    }


    // ------------------- getData 相关 -------------------

    /**
     * 根据配置的 getData 地址，查询数据
     *
     * @param paramMap 查询参数
     * @return 查询结果
     */
    public Object getData(Map<String, Object> paramMap) {
        String getDataUrl = getClientConfig().splicingGetDataUrl();
        return getData(getDataUrl, paramMap);
    }

    /**
     * 根据自定义 path 地址，查询数据 （此方法需要配置 sa-token.sso.server-url 地址）
     *
     * @param path 自定义 path
     * @param paramMap 查询参数
     * @return 查询结果
     */
    public Object getData(String path, Map<String, Object> paramMap) {
        String url = buildCustomPathUrl(path, paramMap);
        return strategy.sendHttp.apply(url);
    }

    /**
     * 构建URL：Server端 getData 地址，带签名等参数
     * @param paramMap 查询参数
     * @return /
     */
    public String buildGetDataUrl(Map<String, Object> paramMap) {
        String getDataUrl = getClientConfig().getGetDataUrl();
        return buildCustomPathUrl(getDataUrl, paramMap);
    }

    /**
     * 构建URL：Server 端自定义 path 地址，带签名等参数 （此方法需要配置 sa-token.sso.server-url 地址）
     * @param paramMap 请求参数
     * @return /
     */
    public String buildCustomPathUrl(String path, Map<String, Object> paramMap) {
        SaSsoClientConfig ssoConfig = getClientConfig();

        // 构建 url
        // 如果 path 不是以 http 开头，那么就拼接上 serverUrl
        String url = path;
        if( ! url.startsWith("http") ) {
            String serverUrl = ssoConfig.getServerUrl();
            SaSsoException.notEmpty(serverUrl, "请先配置 sa-token.sso-client.server-url 地址", SaSsoErrorCode.CODE_30012);
            url = SaFoxUtil.spliceTwoUrl(serverUrl, path);
        }

        // 构建参数字符串
        paramMap.put(paramName.client, getClient());
        String signParamsStr = getSignTemplate().addSignParamsAndJoin(paramMap);

        // 拼接
        return SaFoxUtil.joinParam(url, signParamsStr);
    }


    // ---------------------- 构建交互地址 ----------------------

    /**
     * 构建URL：Server端 单点登录授权地址，
     * <br/> 形如：http://sso-server.com/sso/auth?redirectUrl=http://sso-client.com/sso/login?back=http://sso-client.com
     * @param clientLoginUrl Client端登录地址
     * @param back 回调路径
     * @return [SSO-Server端-认证地址 ]
     */
    public String buildServerAuthUrl(String clientLoginUrl, String back) {
        SaSsoClientConfig ssoConfig = getClientConfig();

        // 服务端认证地址
        String serverUrl = ssoConfig.splicingAuthUrl();

        // 拼接客户端标识
        String client = getClient();
        if(SaFoxUtil.isNotEmpty(client)) {
            serverUrl = SaFoxUtil.joinParam(serverUrl, paramName.client, client);
        }

        // 对back地址编码
        back = (back == null ? "" : back);
        back = SaFoxUtil.encodeUrl(back);

        // 开始拼接 sso 统一认证地址，形如：serverAuthUrl = http://xxx.com?redirectUrl=xxx.com?back=xxx.com

        /*
         * 部分 Servlet 版本 request.getRequestURL() 返回的 url 带有 query 参数，形如：http://domain.com?id=1，
         * 如果不加判断会造成最终生成的 serverAuthUrl 带有双 back 参数 ，这个 if 判断正是为了解决此问题
         */
        if( ! clientLoginUrl.contains(paramName.back + "=") ) {
            clientLoginUrl = SaFoxUtil.joinParam(clientLoginUrl, paramName.back, back);
        }

        // 返回
        return SaFoxUtil.joinParam(serverUrl, paramName.redirect, clientLoginUrl);
    }


    // ------------------- 消息推送 -------------------

    /**
     * 向 sso-server 推送消息
     *
     * @param message /
     * @return /
     */
    public String pushMessage(SaSsoMessage message) {
        SaSsoClientConfig ssoConfig = getClientConfig();

        // 拼接 push-url 地址
        String pushUrl = ssoConfig.splicingPushUrl();
        SaSsoException.notTrue(! SaFoxUtil.isUrl(pushUrl), "无效 push-url 地址：" + pushUrl, SaSsoErrorCode.CODE_30023);

        // 组织参数
        message.set(paramName.client, getClient());
        message.checkType();
        String paramsStr = getSignTemplate().addSignParamsAndJoin(message);

        // 发起请求
        String finalUrl = SaFoxUtil.joinParam(pushUrl, paramsStr);
        return strategy.sendHttp.apply(finalUrl);
    }

    /**
     * 向 sso-server 推送消息，并将返回值转为 SaResult
     *
     * @param message /
     * @return /
     */
    public SaResult pushMessageAsSaResult(SaSsoMessage message) {
        String res = pushMessage(message);
        Map<String, Object> map = SaManager.getSaJsonTemplate().jsonToMap(res);
        return new SaResult(map);
    }

    /**
     * 构建消息：校验 ticket
     *
     * @param ticket ticket码
     * @param ssoLogoutCallUrl 单点注销时的回调URL
     * @return 构建完毕的URL
     */
    public SaSsoMessage buildCheckTicketMessage(String ticket, String ssoLogoutCallUrl) {
        SaSsoClientConfig ssoConfig = getClientConfig();
        SaSsoMessage message = new SaSsoMessage();
        message.setType(SaSsoConsts.MESSAGE_CHECK_TICKET);
        message.set(paramName.client, getClient());
        message.set(paramName.ticket, ticket);
        message.set(paramName.ssoLogoutCall, ssoLogoutCallUrl);
        return message;
    }

    /**
     * 构建消息：单点注销
     *
     * @param loginId 要注销的账号 id
     * @param logoutParameter 单点注销
     * @return 单点注销URL
     */
    public SaSsoMessage buildSignoutMessage(Object loginId, SaLogoutParameter logoutParameter) {
        SaSsoMessage message = new SaSsoMessage();
        message.setType(SaSsoConsts.MESSAGE_SIGNOUT);
        message.set(paramName.client, getClient());
        message.set(paramName.loginId, loginId);
        message.set(paramName.deviceId, logoutParameter.getDeviceId());
        return message;
    }


    // ------------------- Bean 对象获取 -------------------

    /**
     * 获取底层使用的SsoClient配置对象
     * @return /
     */
    public SaSsoClientConfig getClientConfig() {
        return SaSsoManager.getClientConfig();
    }

    /**
     * 获取当前项目 client 标识
     * @return /
     */
    public String getClient() {
        return getClientConfig().getClient();
    }

    /**
     * 获取底层使用的 API 签名对象
     *
     * @return /
     */
    public SaSignTemplate getSignTemplate() {
        SaSignConfig signConfig = SaManager.getSaSignTemplate().getSignConfigOrGlobal().copy();

        // 使用 secretKey 的优先级：SSO 模块全局配置 > sign 模块默认配置
        String secretKey = getClientConfig().getSecretKey();
        if(SaFoxUtil.isEmpty(secretKey)) {
            secretKey = signConfig.getSecretKey();
        }
        signConfig.setSecretKey(secretKey);

        return new SaSignTemplate(signConfig);
    }

}
