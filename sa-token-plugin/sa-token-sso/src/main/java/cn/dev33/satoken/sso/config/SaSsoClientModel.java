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
import java.util.List;

/**
 * Sa-Token SSO 客户端信息配置
 *
 * @author click33
 * @since 1.42.0
 */
public class SaSsoClientModel implements Serializable {

    private static final long serialVersionUID = -6541180061782004705L;

    /**
     * 当前 Client 名称标识，用于和 ticket 码的互相锁定
     */
    public String client;

    /**
     * 所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket)
     */
    public String allowUrl = "*";

    /**
     * 是否打开模式三（此值为 true 时使用 http 调用方式进行消息通知）
     */
    public Boolean isHttp = false;

    /**
     * 是否打开单点注销功能
     */
    public Boolean isSlo = true;

    /**
     * API 调用签名秘钥
     */
    public String secretKey;

    /**
     * 此 Client 端主机总地址
     */
    public String serverUrl;

    /**
     * 此 Client 端推送消息的地址
     */
    public String pushUrl = "/sso/pushC";


    // 额外添加的一些函数

    /**
     * 以数组形式写入允许的授权回调地址
     * @param url 所有集合
     * @return 对象自身
     */
    public SaSsoClientModel setAllow(String ...url) {
        this.setAllowUrl(SaFoxUtil.arrayJoin(url));
        return this;
    }

    /**
     * 获取拼接 url：此 Client 端推送消息的地址
     *
     * @return /
     */
    public String splicingNoticeUrl() {
        return SaFoxUtil.spliceTwoUrl(getServerUrl(), getPushUrl());
    }

    /**
     * 判断是否配置了有效地推送地址
     *
     * @return /
     */
    public boolean isValidNoticeUrl() {
        return SaFoxUtil.isUrl(splicingNoticeUrl());
    }


    // get set

    /**
     * @return 当前 Client 名称标识
     */
    public String getClient() {
        return client;
    }

    /**
     * @param client 当前 Client 名称标识
     */
    public SaSsoClientModel setClient(String client) {
        this.client = client;
        return this;
    }

    /**
     * @return 所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket)
     */
    public String getAllowUrl() {
        return allowUrl;
    }

    /**
     * @param allowUrl 所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket)
     * @return 对象自身
     */
    public SaSsoClientModel setAllowUrl(String allowUrl) {
        // 提前校验一下配置的 allowUrl 是否合法，让开发者尽早发现错误
        if(SaFoxUtil.isNotEmpty(allowUrl)) {
            List<String> allowUrlList = SaFoxUtil.convertStringToList(allowUrl);
            SaSsoServerTemplate.checkAllowUrlListStaticMethod(allowUrlList);
        }
        this.allowUrl = allowUrl;
        return this;
    }

    /**
     * @return isHttp 是否打开模式三
     */
    public Boolean getIsHttp() {
        return isHttp;
    }

    /**
     * @param isHttp 是否打开模式三
     * @return 对象自身
     */
    public SaSsoClientModel setIsHttp(Boolean isHttp) {
        this.isHttp = isHttp;
        return this;
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
    public SaSsoClientModel setIsSlo(Boolean isSlo) {
        this.isSlo = isSlo;
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
    public SaSsoClientModel setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    /**
     * 获取 此 Client 端主机总地址
     *
     * @return serverUrl 此 Client 端主机总地址
     */
    public String getServerUrl() {
        return this.serverUrl;
    }

    /**
     * 设置 此 Client 端主机总地址
     *
     * @param serverUrl 此 Client 端主机总地址
     * @return 对象自身
     */
    public SaSsoClientModel setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    /**
     * 获取 此 Client 端推送消息的地址
     *
     * @return noticeUrl 此 Client 端推送消息的地址
     */
    public String getPushUrl() {
        return this.pushUrl;
    }

    /**
     * 设置 此 Client 端推送消息的地址
     *
     * @param pushUrl 此 Client 端推送消息的地址
     * @return 对象自身
     */
    public SaSsoClientModel setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
        return this;
    }

    @Override
    public String toString() {
        return "SaSsoClientModel ["
                + "client=" + client
                + ", allowUrl=" + allowUrl
                + ", isSlo=" + isSlo
                + ", isHttp=" + isHttp
                + ", secretKey=" + secretKey
                + ", serverUrl=" + serverUrl
                + ", pushUrl=" + pushUrl
                + "]";
    }

}
