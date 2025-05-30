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


import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.util.SaFoxUtil;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sa-Token SSO Server 端 配置类
 *
 * @author click33
 * @since 1.38.0
 */
public class SaSsoServerConfig implements Serializable {

    private static final long serialVersionUID = -6541180061782004705L;


    // ----------------- Server端相关配置

    /**
     * 指定当前系统集成 SSO 时使用的模式（约定型配置项，不对代码逻辑产生任何影响）
     */
    public String mode = "";

    /**
     * ticket 有效期 (单位: 秒)
     */
    public long ticketTimeout = 60 * 5;

    /**
     * 主页路由：在 /sso/auth 登录页不指定 redirect 参数时，默认跳转的地址
     */
    public String homeRoute;

    /**
     * 是否打开单点注销功能 (为 true 时接收 client 端推送的单点注销消息)
     */
    public Boolean isSlo = true;

    /**
     * 是否在每次下发 ticket 时，自动续期 token 的有效期（根据全局 timeout 值）
     */
    public Boolean autoRenewTimeout = false;

    /**
     * 在 Account-Session 上记录 Client 信息的最高数量（-1=无限），超过此值将进行自动清退处理，先进先出
     */
    public int maxRegClient = 32;

    /**
     * 是否校验参数签名（方便本地调试用的一个配置项，生产环境请务必为true）
     */
    public Boolean isCheckSign = true;

    /**
     * Client 信息配置列表
     */
    public Map<String, SaSsoClientModel> clients = new LinkedHashMap<>();

    // 匿名 Client 相关配置

    /**
     * 是否允许匿名 Client 接入
     */
    public Boolean allowAnonClient = false;

    /**
     * 所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket) (匿名 client 使用)
     */
    public String allowUrl = "";

    /**
     * API 调用签名秘钥 (全局默认 + 匿名 client 使用)
     */
    public String secretKey;


    // 额外方法

    /**
     * 以数组形式写入允许的授权回调地址 (不在此列表中的URL将禁止下放ticket) (匿名 client 使用)
     * @param url 所有集合
     * @return 对象自身
     */
    public SaSsoServerConfig setAllow(String ...url) {
        this.setAllowUrl(SaFoxUtil.arrayJoin(url));
        return this;
    }

    /**
     * 添加一个应用
     * @param client /
     * @return 对象自身
     */
    public SaSsoServerConfig addClient(SaSsoClientModel client) {
        this.clients.put(client.getClient(), client);
        return this;
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
     * @return ticket 有效期 (单位: 秒)
     */
    public long getTicketTimeout() {
        return ticketTimeout;
    }

    /**
     * @param ticketTimeout ticket 有效期 (单位: 秒)
     * @return 对象自身
     */
    public SaSsoServerConfig setTicketTimeout(long ticketTimeout) {
        this.ticketTimeout = ticketTimeout;
        return this;
    }

    /**
     * @return 所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket) (匿名 client 使用)
     */
    public String getAllowUrl() {
        return allowUrl;
    }

    /**
     * @param allowUrl 所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket) (匿名 client 使用)
     * @return 对象自身
     */
    public SaSsoServerConfig setAllowUrl(String allowUrl) {
        // 提前校验一下配置的 allowUrl 是否合法，让开发者尽早发现错误
        if(SaFoxUtil.isNotEmpty(allowUrl)) {
            List<String> allowUrlList = SaFoxUtil.convertStringToList(allowUrl);
            SaSsoServerTemplate.checkAllowUrlListStaticMethod(allowUrlList);
        }
        this.allowUrl = allowUrl;
        return this;
    }

    /**
     * @return 主页路由：在 /sso/auth 登录页不指定 redirect 参数时，默认跳转的地址
     */
    public String getHomeRoute() {
        return homeRoute;
    }

    /**
     * @param homeRoute 主页路由：在 /sso/auth 登录页不指定 redirect 参数时，默认跳转的地址
     * @return 对象自身
     */
    public SaSsoServerConfig setHomeRoute(String homeRoute) {
        this.homeRoute = homeRoute;
        return this;
    }

    /**
     * @return 是否打开单点注销功能 (为 true 时接收 client 端推送的单点注销消息)
     */
    public Boolean getIsSlo() {
        return isSlo;
    }

    /**
     * @param isSlo 是否打开单点注销功能 (为 true 时接收 client 端推送的单点注销消息)
     * @return 对象自身
     */
    public SaSsoServerConfig setIsSlo(Boolean isSlo) {
        this.isSlo = isSlo;
        return this;
    }

    /**
     * @return 是否在每次下发 ticket 时，自动续期 token 的有效期（根据全局 timeout 值）
     */
    public Boolean getAutoRenewTimeout() {
        return autoRenewTimeout;
    }

    /**
     * @param autoRenewTimeout 是否在每次下发 ticket 时，自动续期 token 的有效期（根据全局 timeout 值）
     * @return 对象自身
     */
    public SaSsoServerConfig setAutoRenewTimeout(Boolean autoRenewTimeout) {
        this.autoRenewTimeout = autoRenewTimeout;
        return this;
    }

    /**
     * @return maxLoginClient 在 Account-Session 上记录 Client 信息的最高数量（-1=无限），超过此值将进行自动清退处理，先进先出
     */
    public int getMaxRegClient() {
        return maxRegClient;
    }

    /**
     * @param maxRegClient 在 Account-Session 上记录 Client 信息的最高数量（-1=无限），超过此值将进行自动清退处理，先进先出
     * @return 对象自身
     */
    public SaSsoServerConfig setMaxRegClient(int maxRegClient) {
        this.maxRegClient = maxRegClient;
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
    public SaSsoServerConfig setIsCheckSign(Boolean isCheckSign) {
        this.isCheckSign = isCheckSign;
        return this;
    }

    /**
     * 获取 是否允许匿名 Client 接入
     *
     * @return /
     */
    public Boolean getAllowAnonClient() {
        return this.allowAnonClient;
    }

    /**
     * 设置 是否允许匿名 Client 接入
     *
     * @param allowAnonClient /
     */
    public SaSsoServerConfig setAllowAnonClient(Boolean allowAnonClient) {
        this.allowAnonClient = allowAnonClient;
        return this;
    }

    /**
     * 获取 API 调用签名秘钥 (全局默认 + 匿名 client 使用)
     *
     * @return /
     */
    public String getSecretKey() {
        return this.secretKey;
    }

    /**
     * 设置 API 调用签名秘钥 (全局默认 + 匿名 client 使用)
     *
     * @param secretKey /
     * @return 对象自身
     */
    public SaSsoServerConfig setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    /**
     * 获取 Client 信息配置列表
     *
     * @return clients Client 信息配置列表
     */
    public Map<String, SaSsoClientModel> getClients() {
        return this.clients;
    }

    /**
     * 设置 Client 信息配置列表
     *
     * @param clients Client 信息配置列表
     * @return 对象自身
     */
    public SaSsoServerConfig setClients(Map<String, SaSsoClientModel> clients) {
        this.clients = clients;
        return this;
    }

    @Override
    public String toString() {
        return "SaSsoServerConfig ["
                + "mode=" + mode
                + ", ticketTimeout=" + ticketTimeout
                + ", allowUrl=" + allowUrl
                + ", homeRoute=" + homeRoute
                + ", isSlo=" + isSlo
                + ", autoRenewTimeout=" + autoRenewTimeout
                + ", maxRegClient=" + maxRegClient
                + ", isCheckSign=" + isCheckSign
                + ", allowAnonClient=" + allowAnonClient
                + ", secretKey=" + secretKey
                + ", clients=" + clients
                + "]";
    }

}
