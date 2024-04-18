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
package cn.dev33.satoken.httpauth.digest;

/**
 * Sa-Token Http Digest 认证 - 参数实体类
 *
 * @author click33
 * @since 1.38.0
 */
public class SaHttpDigestModel {

    /**
     * 默认的 Realm 领域名称
     */
    public static final String DEFAULT_REALM = "Sa-Token";

    /**
     * 默认的 qop 值
     */
    public static final String DEFAULT_QOP = "auth";


    /**
     * 用户名
     */
    public String username;

    /**
     * 密码
     */
    public String password;

    /**
     * 领域
     */
    public String realm = DEFAULT_REALM;

    /**
     * 随机数
     */
    public String nonce;

    /**
     * 请求 uri
     */
    public String uri;

    /**
     * 请求方法
     */
    public String method;

    /**
     * 保护质量（auth=默认的，auth-int=增加报文完整性检测），可以为空，但不推荐
     */
    public String qop;

    /**
     * nonce计数器，是一个16进制的数值，表示同一nonce下客户端发送出请求的数量
     */
    public String nc;

    /**
     * 客户端随机数，由客户端提供
     */
    public String cnonce;

    /**
     * opaque
     */
    public String opaque;

    /**
     * 请求摘要，最终计算的摘要结果
     */
    public String response;

    // ------------------- 构造函数 -------------------

    public SaHttpDigestModel() {
    }
    public SaHttpDigestModel(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public SaHttpDigestModel(String username, String password, String realm) {
        this.username = username;
        this.password = password;
        this.realm = realm;
    }


    // ------------------- get/set -------------------

    /**
     * 获取 用户名
     *
     * @return username 用户名
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * 设置 用户名
     *
     * @param username 用户名
     * @return /
     */
    public SaHttpDigestModel setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * 获取 领域
     *
     * @return realm 领域
     */
    public String getRealm() {
        return this.realm;
    }

    /**
     * 设置 领域
     *
     * @param realm 领域
     * @return /
     */
    public SaHttpDigestModel setRealm(String realm) {
        this.realm = realm;
        return this;
    }

    /**
     * 获取 密码
     *
     * @return password 密码
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * 设置 密码
     *
     * @param password 密码
     * @return /
     */
    public SaHttpDigestModel setPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * 获取 随机数
     *
     * @return nonce 随机数
     */
    public String getNonce() {
        return this.nonce;
    }

    /**
     * 设置 随机数
     *
     * @param nonce 随机数
     * @return /
     */
    public SaHttpDigestModel setNonce(String nonce) {
        this.nonce = nonce;
        return this;
    }

    /**
     * 获取 请求 uri
     *
     * @return uri 请求 uri
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * 设置 请求 uri
     *
     * @param uri 请求 uri
     * @return /
     */
    public SaHttpDigestModel setUri(String uri) {
        this.uri = uri;
        return this;
    }

    /**
     * 获取 请求方法
     *
     * @return method 请求方法
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * 设置 请求方法
     *
     * @param method 请求方法
     * @return /
     */
    public SaHttpDigestModel setMethod(String method) {
        this.method = method;
        return this;
    }

    /**
     * 获取 保护质量（auth=默认的，auth-int=增加报文完整性检测），可以为空，但不推荐
     *
     * @return qop 保护质量（auth=默认的，auth-int=增加报文完整性检测），可以为空，但不推荐
     */
    public String getQop() {
        return this.qop;
    }

    /**
     * 设置 保护质量（auth=默认的，auth-int=增加报文完整性检测），可以为空，但不推荐
     *
     * @param qop 保护质量（auth=默认的，auth-int=增加报文完整性检测），可以为空，但不推荐
     * @return /
     */
    public SaHttpDigestModel setQop(String qop) {
        this.qop = qop;
        return this;
    }

    /**
     * 获取 nonce计数器，是一个16进制的数值，表示同一nonce下客户端发送出请求的数量
     *
     * @return nc nonce计数器，是一个16进制的数值，表示同一nonce下客户端发送出请求的数量
     */
    public String getNc() {
        return this.nc;
    }

    /**
     * 设置 nonce计数器，是一个16进制的数值，表示同一nonce下客户端发送出请求的数量
     *
     * @param nc nonce计数器，是一个16进制的数值，表示同一nonce下客户端发送出请求的数量
     * @return /
     */
    public SaHttpDigestModel setNc(String nc) {
        this.nc = nc;
        return this;
    }

    /**
     * 获取 客户端随机数，由客户端提供
     *
     * @return cnonce 客户端随机数，由客户端提供
     */
    public String getCnonce() {
        return this.cnonce;
    }

    /**
     * 设置 客户端随机数，由客户端提供
     *
     * @param cnonce 客户端随机数，由客户端提供
     * @return /
     */
    public SaHttpDigestModel setCnonce(String cnonce) {
        this.cnonce = cnonce;
        return this;
    }

    /**
     * 获取 opaque
     *
     * @return opaque opaque
     */
    public String getOpaque() {
        return this.opaque;
    }

    /**
     * 设置 opaque
     *
     * @param opaque opaque
     * @return /
     */
    public SaHttpDigestModel setOpaque(String opaque) {
        this.opaque = opaque;
        return this;
    }

    /**
     * 获取 请求摘要，最终计算的摘要结果
     *
     * @return response 请求摘要，最终计算的摘要结果
     */
    public String getResponse() {
        return this.response;
    }

    /**
     * 设置 请求摘要，最终计算的摘要结果
     *
     * @param response 请求摘要，最终计算的摘要结果
     * @return /
     */
    public SaHttpDigestModel setResponse(String response) {
        this.response = response;
        return this;
    }

    @Override
    public String toString() {
        return "SaHttpDigestModel[" +
                "username=" + username +
                ", password=" + password +
                ", realm=" + realm +
                ", nonce=" + nonce +
                ", uri=" + uri +
                ", method=" + method +
                ", qop=" + qop +
                ", nc=" + nc +
                ", cnonce=" + cnonce +
                ", opaque=" + opaque +
                ", response=" + response +
                "]";
    }

}