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
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.util.SaFoxUtil;

import java.io.Serializable;
import java.util.List;

/**
 * Sa-Token SSO 客户端信息配置 （在 Server 端配置允许接入的 Client 信息）
 *
 * @author click33
 * @since 1.43.0
 */
public class SaSsoClientModel implements Serializable {

    private static final long serialVersionUID = -6541180061782004705L;

    /**
     * Client 名称标识
     */
    public String client;

    /**
     * 所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的 URL 将禁止下放 ticket )
     */
    public String allowUrl = "";

    /**
     * 是否接收推送消息
     */
    public Boolean isPush = false;

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
     * 此 Client 端推送消息的地址 (如不配置，默认根据 serverUrl + '/sso/pushC' 进行拼接)
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
    public String splicingPushUrl() {
        String _pushUrl = SaFoxUtil.spliceTwoUrl(getServerUrl(), getPushUrl());
        if ( ! SaFoxUtil.isUrl(_pushUrl)) {
            throw new SaSsoException("应用 [" + getClient() + "] 推送地址无效：" + _pushUrl).setCode(SaSsoErrorCode.CODE_30023);
        }
        return _pushUrl;
    }


    // get set

    /**
     * @return Client 名称标识
     */
    public String getClient() {
        return client;
    }

    /**
     * @param client Client 名称标识
     */
    public SaSsoClientModel setClient(String client) {
        this.client = client;
        return this;
    }

    /**
     * @return 所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的 URL 将禁止下放 ticket )
     */
    public String getAllowUrl() {
        return allowUrl;
    }

    /**
     * @param allowUrl 所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的 URL 将禁止下放 ticket )
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
    public Boolean getIsPush() {
        return isPush;
    }

    /**
     * @param isPush 是否打开模式三
     * @return 对象自身
     */
    public SaSsoClientModel setIsPush(Boolean isPush) {
        this.isPush = isPush;
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
     * 获取 此 Client 端推送消息的地址 (如不配置，默认根据 serverUrl + '/sso/pushC' 进行拼接)
     *
     * @return /
     */
    public String getPushUrl() {
        return this.pushUrl;
    }

    /**
     * 设置 此 Client 端推送消息的地址 (如不配置，默认根据 serverUrl + '/sso/pushC' 进行拼接)
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
                + ", isPush=" + isPush
                + ", secretKey=" + secretKey
                + ", serverUrl=" + serverUrl
                + ", pushUrl=" + pushUrl
                + "]";
    }

}
