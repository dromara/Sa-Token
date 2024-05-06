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


import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.function.SendHttpFunction;
import cn.dev33.satoken.sso.function.TicketResultHandleFunction;
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
     * 当前 Client 名称标识，用于和 ticket 码的互相锁定
     */
    public String client;

    /**
     * 配置 Server 端主机总地址，拼接在 authUrl、checkTicketUrl、getDataUrl、sloUrl 属性前面，用以简化各种 url 配置
     */
    public String serverUrl;

    /**
     * 单独配置 Server 端单点登录授权地址
     */
    public String authUrl = "/sso/auth";

    /**
     * 单独配置 Server 端的 ticket 校验地址
     */
    public String checkTicketUrl = "/sso/checkTicket";

    /**
     * 单独配置 Server 端查询数据 getData 地址
     */
    public String getDataUrl = "/sso/getData";

    /**
     * 单独配置 Server 端单点注销地址
     */
    public String sloUrl = "/sso/signout";

    /**
     * 配置当前 Client 端的登录地址（为空时自动获取）
     */
    public String currSsoLogin;

    /**
     * 配置当前 Client 端的单点注销回调URL （为空时自动获取）
     */
    public String currSsoLogoutCall;

    /**
     * 是否打开单点注销功能
     */
    public Boolean isSlo = true;

    /**
     * 是否打开模式三（此值为 true 时将使用 http 请求：校验ticket值、单点注销、拉取数据getData）
     */
    public Boolean isHttp = false;

    /**
     * 是否校验参数签名（方便本地调试用的一个配置项，生产环境请务必为true）
     */
    public Boolean isCheckSign = true;


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
     * @return 配置的 Server 端的 ticket 校验地址
     */
    public String getCheckTicketUrl() {
        return checkTicketUrl;
    }

    /**
     * @param checkTicketUrl 配置 Server 端的 ticket 校验地址
     * @return 对象自身
     */
    public SaSsoClientConfig setCheckTicketUrl(String checkTicketUrl) {
        this.checkTicketUrl = checkTicketUrl;
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

    @Override
    public String toString() {
        return "SaSsoClientConfig ["
                + "mode=" + mode
                + ", client=" + client
                + ", serverUrl=" + serverUrl
                + ", authUrl=" + authUrl
                + ", checkTicketUrl=" + checkTicketUrl
                + ", getDataUrl=" + getDataUrl
                + ", sloUrl=" + sloUrl
                + ", currSsoLogin=" + currSsoLogin
                + ", currSsoLogoutCall=" + currSsoLogoutCall
                + ", isSlo=" + isSlo
                + ", isHttp=" + isHttp
                + ", isCheckSign=" + isCheckSign
                + "]";
    }

    // 额外添加的一些函数

    /**
     * @return 获取拼接url：Server 端单点登录授权地址
     */
    public String splicingAuthUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getAuthUrl());
    }

    /**
     * @return 获取拼接url：Server 端的 ticket 校验地址
     */
    public String splicingCheckTicketUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getCheckTicketUrl());
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


    // -------------------- 所有回调函数 --------------------

    /**
     * SSO-Client端：自定义校验Ticket返回值的处理逻辑 （每次从认证中心获取校验Ticket的结果后调用）
     * <p> 参数：loginId, back
     * <p> 返回值：返回给前端的值
     */
    public TicketResultHandleFunction ticketResultHandle = null;

    /**
     * SSO-Client端：发送Http请求的处理函数
     */
    public SendHttpFunction sendHttp = url -> {
        throw new SaSsoException("请配置 Http 请求处理器").setCode(SaSsoErrorCode.CODE_30010);
    };

}
