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
package cn.dev33.satoken.sso.config;


import cn.dev33.satoken.util.SaFoxUtil;

import java.io.Serializable;

/**
 * Sa-Token SSO Client 端 配置类
 *
 * @author click33
 * @since 1.30.0
 */
public class SaSsoClientConfig implements Serializable {

    private static final long serialVersionUID = -6541180061782004705L;

    /**
     * 指定当前系统集成 SSO 时使用的模式（约定型配置项，不对代码逻辑产生任何影响）
     */
    public String mode = "";

    /**
     * 当前 Client 标识（非必填，不填时代表当前应用是一个匿名应用）
     */
    public String client;

    /**
     * 配置 SSO Server 端主机总地址
     */
    public String serverUrl;

    /**
     * 单独配置 Server 端：单点登录授权地址
     */
    public String authUrl = "/sso/auth";

    /**
     * 单独配置 Server 端：单点注销地址
     */
    public String signoutUrl = "/sso/signout";

    /**
     * 单独配置 Server 端：推送消息地址
     */
    public String pushUrl = "/sso/pushS";

    /**
     * 单独配置 Server 端：查询数据 getData 地址
     */
    public String getDataUrl = "/sso/getData";

    /**
     * 配置当前 Client 端的登录地址（为空时自动获取）
     */
    public String currSsoLogin;

    /**
     * 配置当前 Client 端的单点注销回调URL （为空时自动获取）
     */
    public String currSsoLogoutCall;

    /**
     * 是否打开模式三（此值为 true 时将使用 http 请求校验 ticket 值）
     */
    public Boolean isHttp = false;

    /**
     * 是否打开单点注销功能 (为 true 时，开放 /sso/logout 接口，以及接收单点注销回调消息推送)
     */
    public Boolean isSlo = true;

    /**
     * 是否注册单点登录注销回调 (为 true 时，登录时附带单点登录回调地址，并且开放 /sso/logoutCall 地址)
     */
    public Boolean regLogoutCall = false;

    /**
     * API 调用签名秘钥
     */
    public String secretKey;

    /**
     * 是否校验参数签名（为 false 时暂时关闭参数签名校验，此为方便本地调试用的一个配置项，生产环境请务必为true）
     */
    public Boolean isCheckSign = true;


    // 额外添加的一些函数

    /**
     * @return 获取拼接 url：Server 端单点登录授权地址
     */
    public String splicingAuthUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getAuthUrl());
    }

    /**
     * @return 获取拼接 url：Server 端查询数据 getData 地址
     */
    public String splicingGetDataUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getGetDataUrl());
    }

    /**
     * @return 获取拼接 url：Server 端单点注销地址
     */
    public String splicingSignoutUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getSignoutUrl());
    }

    /**
     * @return 获取拼接 url：单独配置 Server 端推送消息地址
     */
    public String splicingPushUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getPushUrl());
    }


    // get set

    /**
     * 获取 指定当前系统集成 SSO 时使用的模式（约定型配置项，不对代码逻辑产生任何影响）
     *
     * @return /
     */
    public String getMode() {
        return this.mode;
    }

    /**
     * 设置 指定当前系统集成 SSO 时使用的模式（约定型配置项，不对代码逻辑产生任何影响）
     *
     * @param mode /
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * @return 是否打开单点注销功能  (为 true 时，开放 /sso/logout 接口，以及接收单点注销回调消息推送)
     */
    public Boolean getIsSlo() {
        return isSlo;
    }

    /**
     * @param isSlo 是否打开单点注销功能 (为 true 时，开放 /sso/logout 接口，以及接收单点注销回调消息推送)
     * @return 对象自身
     */
    public SaSsoClientConfig setIsSlo(Boolean isSlo) {
        this.isSlo = isSlo;
        return this;
    }

    /**
     * @return isHttp 是否打开模式三（此值为 true 时将使用 http 请求校验 ticket 值）
     */
    public Boolean getIsHttp() {
        return isHttp;
    }

    /**
     * @param isHttp 是否打开模式三（此值为 true 时将使用 http 请求校验 ticket 值）
     * @return 对象自身
     */
    public SaSsoClientConfig setIsHttp(Boolean isHttp) {
        this.isHttp = isHttp;
        return this;
    }

    /**
     * 当前 Client 标识（非必填，不填时代表当前应用是一个匿名应用）
     *
     * @return /
     */
    public String getClient() {
        return client;
    }

    /**
     * 当前 Client 标识（非必填，不填时代表当前应用是一个匿名应用）
     *
     * @param client /
     */
    public SaSsoClientConfig setClient(String client) {
        this.client = client;
        return this;
    }

    /**
     * @return 单独配置 Server 端：单点登录授权地址
     */
    public String getAuthUrl() {
        return authUrl;
    }

    /**
     * @param authUrl 单独配置 Server 端：单点登录授权地址
     * @return 对象自身
     */
    public SaSsoClientConfig setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
        return this;
    }

    /**
     * @return 单独配置 Server 端：查询数据 getData 地址
     */
    public String getGetDataUrl() {
        return getDataUrl;
    }

    /**
     * @param getDataUrl 单独配置 Server 端：查询数据 getData 地址
     * @return 对象自身
     */
    public SaSsoClientConfig setGetDataUrl(String getDataUrl) {
        this.getDataUrl = getDataUrl;
        return this;
    }

    /**
     * @return 单独配置 Server 端：单点注销地址
     */
    public String getSignoutUrl() {
        return signoutUrl;
    }

    /**
     * @param signoutUrl 单独配置 Server 端：单点注销地址
     * @return 对象自身
     */
    public SaSsoClientConfig setSignoutUrl(String signoutUrl) {
        this.signoutUrl = signoutUrl;
        return this;
    }

    /**
     * 获取 单独配置 Server 端：推送消息地址
     *
     * @return /
     */
    public String getPushUrl() {
        return this.pushUrl;
    }

    /**
     * 设置 单独配置 Server 端：推送消息地址
     *
     * @param pushUrl /
     * @return 对象自身
     */
    public SaSsoClientConfig setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
        return this;
    }

    /**
     * @return 配置当前 Client 端的登录地址（为空时自动获取）
     */
    public String getCurrSsoLogin() {
        return currSsoLogin;
    }

    /**
     * @param currSsoLogin 配置当前 Client 端的登录地址（为空时自动获取）
     * @return 对象自身
     */
    public SaSsoClientConfig setCurrSsoLogin(String currSsoLogin) {
        this.currSsoLogin = currSsoLogin;
        return this;
    }

    /**
     * @return 配置当前 Client 端的单点注销回调URL （为空时自动获取）
     */
    public String getCurrSsoLogoutCall() {
        return currSsoLogoutCall;
    }

    /**
     * @param currSsoLogoutCall 配置当前 Client 端的单点注销回调URL （为空时自动获取）
     * @return 对象自身
     */
    public SaSsoClientConfig setCurrSsoLogoutCall(String currSsoLogoutCall) {
        this.currSsoLogoutCall = currSsoLogoutCall;
        return this;
    }

    /**
     * 配置 SSO Server 端主机总地址
     *
     * @return /
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * 配置 SSO Server 端主机总地址
     *
     * @param serverUrl /
     * @return 对象自身
     */
    public SaSsoClientConfig setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    /**
     * 获取 API 调用签名秘钥
     *
     * @return /
     */
    public String getSecretKey() {
        return this.secretKey;
    }

    /**
     * 设置 API 调用签名秘钥
     *
     * @param secretKey /
     * @return 对象自身
     */
    public SaSsoClientConfig setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    /**
     * 获取 是否校验参数签名（为 false 时暂时关闭参数签名校验，此为方便本地调试用的一个配置项，生产环境请务必为true）
     *
     * @return isCheckSign 是否校验参数签名（方便本地调试用的一个配置项，生产环境请务必为true）
     */
    public Boolean getIsCheckSign() {
        return this.isCheckSign;
    }

    /**
     * 设置 是否校验参数签名（为 false 时暂时关闭参数签名校验，此为方便本地调试用的一个配置项，生产环境请务必为true）
     *
     * @param isCheckSign 是否校验参数签名（方便本地调试用的一个配置项，生产环境请务必为true）
     */
    public SaSsoClientConfig setIsCheckSign(Boolean isCheckSign) {
        this.isCheckSign = isCheckSign;
        return this;
    }

    /**
     * 获取 是否注册单点登录注销回调 (为 true 时，登录时附带单点登录回调地址，并且开放 /sso/logoutCall 地址)
     *
     * @return /
     */
    public Boolean getRegLogoutCall() {
        return this.regLogoutCall;
    }

    /**
     * 设置 是否注册单点登录注销回调 (为 true 时，登录时附带单点登录回调地址，并且开放 /sso/logoutCall 地址)
     *
     * @param regLogoutCall /
     * @return /
     */
    public SaSsoClientConfig setRegLogoutCall(Boolean regLogoutCall) {
        this.regLogoutCall = regLogoutCall;
        return this;
    }

    @Override
    public String toString() {
        return "SaSsoClientConfig ["
                + "mode=" + mode
                + ", client=" + client
                + ", serverUrl=" + serverUrl
                + ", authUrl=" + authUrl
                + ", signoutUrl=" + signoutUrl
                + ", pushUrl=" + pushUrl
                + ", getDataUrl=" + getDataUrl
                + ", currSsoLogin=" + currSsoLogin
                + ", currSsoLogoutCall=" + currSsoLogoutCall
                + ", isHttp=" + isHttp
                + ", isSlo=" + isSlo
                + ", regLogoutCall=" + regLogoutCall
                + ", secretKey=" + secretKey
                + ", isCheckSign=" + isCheckSign
                + "]";
    }

}
