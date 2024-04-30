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
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

import java.util.Map;
import java.util.TreeMap;

/**
 * Sa-Token SSO 模板方法类 （Client端）
 *
 * @author click33
 * @since 1.38.0
 */
public class SaSsoClientTemplate extends SaSsoTemplate {

    /**
     * 获取底层使用的SsoClient配置对象
     * @return /
     */
    public SaSsoClientConfig getClientConfig() {
        return SaSsoManager.getClientConfig();
    }

    // ------------------- SSO 模式三相关 -------------------

    /**
     * 根据配置的 getData 地址，查询数据
     * @param paramMap 查询参数
     * @return 查询结果
     */
    public Object getData(Map<String, Object> paramMap) {
        String getDataUrl = getClientConfig().splicingGetDataUrl();
        return getData(getDataUrl, paramMap);
    }

    /**
     * 根据自定义 path 地址，查询数据 （此方法需要配置 sa-token.sso.server-url 地址）
     * @param path 自定义 path
     * @param paramMap 查询参数
     * @return 查询结果
     */
    public Object getData(String path, Map<String, Object> paramMap) {
        String url = buildCustomPathUrl(path, paramMap);
        return getClientConfig().sendHttp.apply(url);
    }

    // ---------------------- 构建URL ----------------------

    /**
     * 构建URL：Server端 单点登录地址
     * @param clientLoginUrl Client端登录地址
     * @param back 回调路径
     * @return [SSO-Server端-认证地址 ]
     */
    public String buildServerAuthUrl(String clientLoginUrl, String back) {
        SaSsoClientConfig ssoConfig = getClientConfig();

        // 服务端认证地址
        String serverUrl = ssoConfig.splicingAuthUrl();

        // 拼接客户端标识
        String client = ssoConfig.getClient();
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
        if( ! clientLoginUrl.contains(paramName.back + "=" + back) ) {
            clientLoginUrl = SaFoxUtil.joinParam(clientLoginUrl, paramName.back, back);
        }

        // 返回
        return SaFoxUtil.joinParam(serverUrl, paramName.redirect, clientLoginUrl);
    }

    /**
     * 构建URL：校验ticket的URL
     * <p> 在模式三下，Client端拿到Ticket后根据此地址向Server端发送请求，获取账号id
     * @param ticket ticket码
     * @param ssoLogoutCallUrl 单点注销时的回调URL
     * @return 构建完毕的URL
     */
    public String buildCheckTicketUrl(String ticket, String ssoLogoutCallUrl) {

        SaSsoClientConfig ssoConfig = getClientConfig();

        // 1、url
        String url = ssoConfig.splicingCheckTicketUrl();

        // 2、参数：client、ticket、ssoLogoutCall
        Map<String, Object> paramMap = new TreeMap<>();
        paramMap.put(paramName.ticket, ticket);
        paramMap.put(paramName.client, ssoConfig.getClient());
        paramMap.put(paramName.ssoLogoutCall, ssoLogoutCallUrl);

        // 追加签名参数，并序列化为kv字符串
        String signParamStr = getSignTemplate(ssoConfig.getClient()).addSignParamsAndJoin(paramMap);

        // 3、拼接
        return SaFoxUtil.joinParam(url, signParamStr);
    }

    /**
     * 构建URL：单点注销URL
     * @param loginId 要注销的账号id
     * @return 单点注销URL
     */
    public String buildSloUrl(Object loginId) {
        // 获取所需对象
        SaSsoClientConfig ssoConfig = getClientConfig();
        String url = ssoConfig.splicingSloUrl();
        String currClient = ssoConfig.getClient();

        // 组织请求参数
        Map<String, Object> paramMap = new TreeMap<>();
        paramMap.put(paramName.loginId, loginId);
        paramMap.put(paramName.client, currClient);

        // 追加签名参数，并序列化为kv字符串
        String signParamsStr = getSignTemplate(currClient).addSignParamsAndJoin(paramMap);

        //  拼接到 url 上
        return SaFoxUtil.joinParam(url, signParamsStr);
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
            SaSsoException.notEmpty(serverUrl, "请先配置 sa-token.sso.server-url 地址", SaSsoErrorCode.CODE_30012);
            url = SaFoxUtil.spliceTwoUrl(serverUrl, path);
        }

        // 构建参数字符串
        paramMap.put(paramName.client, ssoConfig.getClient());
        String signParamsStr = getSignTemplate(ssoConfig.getClient()).addSignParamsAndJoin(paramMap);

        // 拼接
        return SaFoxUtil.joinParam(url, signParamsStr);
    }


    // ------------------- 发起请求 -------------------

    /**
     * 发出请求，并返回 SaResult 结果
     * @param url 请求地址
     * @return 返回的结果
     */
    public SaResult request(String url) {
        String body = getClientConfig().sendHttp.apply(url);
        Map<String, Object> map = SaManager.getSaJsonTemplate().parseJsonToMap(body);
        return new SaResult(map);
    }

}
