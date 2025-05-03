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
 * Sa-Token SSO 单点登录模块 配置类  （Client端）
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
     * 当前 Client 标识
     */
    public String client;

    /**
     * 配置 Server 端主机总地址
     */
    public String serverUrl;

    /**
     * 单独配置 Server 端单点登录授权地址
     */
    public String authUrl = "/sso/auth";

    /**
     * 单独配置 Server 端查询数据 getData 地址
     */
    public String getDataUrl = "/sso/getData";

    /**
     * 单独配置 Server 端单点注销地址
     */
    public String sloUrl = "/sso/signout";

    /**
     * 单独配置 Server 端推送消息地址
     */
    public String pushUrl = "/sso/pushS";

    /**
     * 配置当前 Client 端的登录地址（为空时自动获取）
     */
    public String currSsoLogin;

    /**
     * 配置当前 Client 端的单点注销回调URL （为空时自动获取）
     */
    public String currSsoLogoutCall;

    /**
     * 是否打开单点注销功能 (为 true 时，接收单点注销回调消息推送)
     */
    public Boolean isSlo = true;

    /**
     * 是否注册单点登录注销回调 (为 true 时，登录时附带单点登录回调地址，并且开放 /sso/logoutCall 地址)
     */
    public Boolean regLogoutCall = false;

    /**
     * 是否打开模式三（此值为 true 时将使用 http 请求校验 ticket 值）
     */
    public Boolean isHttp = false;

    /**
     * API 调用签名秘钥
     */
    public String secretKey;

    /**
     * 是否校验参数签名（方便本地调试用的一个配置项，生产环境请务必为true）
     */
    public Boolean isCheckSign = true;


    // 额外添加的一些函数

    /**
     * @return 获取拼接url：Server 端单点登录授权地址
     */
    public String splicingAuthUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getAuthUrl());
    }

    /**
     * @return 获取拼接url：Server 端查询数据 getData 地址
     */
    public String splicingGetDataUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getGetDataUrl());
    }

    /**
     * @return 获取拼接url：Server 端单点注销地址
     */
    public String splicingSloUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getSloUrl());
    }

    /**
     * @return 获取拼接url：单独配置 Server 端推送消息地址
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
     * @return 是否打开单点注销功能
     */
    public Boolean getIsSlo() {
        return isSlo;
    }

    /**
     * @param isSlo 是否打开单点注销功能
     * @return 对象自身
     */
    public SaSsoClientConfig setIsSlo(Boolean isSlo) {
        this.isSlo = isSlo;
        return this;
    }

    /**
     * @return isHttp 是否打开模式三（此值为 true 时将使用 http 请求：校验ticket值、单点注销、拉取数据getData）
     */
    public Boolean getIsHttp() {
        return isHttp;
    }

    /**
     * @param isHttp 是否打开模式三（此值为 true 时将使用 http 请求：校验ticket值、单点注销、拉取数据getData）
     * @return 对象自身
     */
    public SaSsoClientConfig setIsHttp(Boolean isHttp) {
        this.isHttp = isHttp;
        return this;
    }

    /**
     * @return 当前 Client 名称标识，用于和 ticket 码的互相锁定
     */
    public String getClient() {
        return client;
    }

    /**
     * @param client 当前 Client 名称标识，用于和 ticket 码的互相锁定
     */
    public SaSsoClientConfig setClient(String client) {
        this.client = client;
        return this;
    }

    /**
     * @return 配置的 Server 端单点登录授权地址
     */
    public String getAuthUrl() {
        return authUrl;
    }

    /**
     * @param authUrl 配置 Server 端单点登录授权地址
     * @return 对象自身
     */
    public SaSsoClientConfig setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
        return this;
    }

    /**
     * @return Server 端查询数据 getData 地址
     */
    public String getGetDataUrl() {
        return getDataUrl;
    }

    /**
     * @param getDataUrl 配置 Server 端查询数据 getData 地址
     * @return 对象自身
     */
    public SaSsoClientConfig setGetDataUrl(String getDataUrl) {
        this.getDataUrl = getDataUrl;
        return this;
    }

    /**
     * @return 配置 Server 端单点注销地址
     */
    public String getSloUrl() {
        return sloUrl;
    }

    /**
     * @param sloUrl 配置 Server 端单点注销地址
     * @return 对象自身
     */
    public SaSsoClientConfig setSloUrl(String sloUrl) {
        this.sloUrl = sloUrl;
        return this;
    }

    /**
     * 获取 单独配置 Server 端推送消息地址
     *
     * @return /
     */
    public String getPushUrl() {
        return this.pushUrl;
    }

    /**
     * 设置 单独配置 Server 端推送消息地址
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
     * @return 配置的 Server 端主机总地址，拼接在 authUrl、checkTicketUrl、getDataUrl、sloUrl 属性前面，用以简化各种 url 配置
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * @param serverUrl 配置 Server 端主机总地址，拼接在 authUrl、checkTicketUrl、getDataUrl、sloUrl 属性前面，用以简化各种 url 配置
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
     * 获取 是否校验参数签名（方便本地调试用的一个配置项，生产环境请务必为true）
     *
     * @return isCheckSign 是否校验参数签名（方便本地调试用的一个配置项，生产环境请务必为true）
     */
    public Boolean getIsCheckSign() {
        return this.isCheckSign;
    }

    /**
     * 设置 是否校验参数签名（方便本地调试用的一个配置项，生产环境请务必为true）
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
                + ", getDataUrl=" + getDataUrl
                + ", sloUrl=" + sloUrl
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
