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
package cn.dev33.satoken.sso.model;


/**
 * Sa-Token SSO Model
 *
 * @author click33
 * @since 1.38.0
 */
public class SaSsoClientModel {

    /**
     * 此 client 登录模式（1=模式一，2=模式二，3=模式三）
     */
    public int mode;

    /**
     * 客户端标识
     */
    public String client;

    /**
     * 此次登录 token 值
     */
    public String tokenValue;

    /**
     * 单点注销回调url
     */
    public String ssoLogoutCall;

    /**
     * 此 client 注册信息的时间，13位时间戳
     */
    public long regTime;

    /**
     * 此账号有记录以来为第几次登录，默认从0开始递增
     */
    public int index;

    public SaSsoClientModel() {
    }

    public SaSsoClientModel(String client, String ssoLogoutCall) {
        this.client = client;
        this.ssoLogoutCall = ssoLogoutCall;
        this.regTime = System.currentTimeMillis();
    }


    /**
     * 获取 此 client 登录模式（1=模式一，2=模式二，3=模式三）
     *
     * @return mode 此 client 登录模式（1=模式一，2=模式二，3=模式三）
     */
    public int getMode() {
        return this.mode;
    }

    /**
     * 设置 此 client 登录模式（1=模式一，2=模式二，3=模式三）
     *
     * @param mode 此 client 登录模式（1=模式一，2=模式二，3=模式三）
     * @return /
     */
    public SaSsoClientModel setMode(int mode) {
        this.mode = mode;
        return this;
    }

    /**
     * 获取 客户端标识
     *
     * @return client 客户端标识
     */
    public String getClient() {
        return this.client;
    }

    /**
     * 设置 客户端标识
     *
     * @param client 客户端标识
     * @return /
     */
    public SaSsoClientModel setClient(String client) {
        this.client = client;
        return this;
    }

    /**
     * 获取 此次登录 token 值
     *
     * @return tokenValue 此次登录 token 值
     */
    public String getTokenValue() {
        return this.tokenValue;
    }

    /**
     * 设置 此次登录 token 值
     *
     * @param tokenValue 此次登录 token 值
     * @return /
     */
    public SaSsoClientModel setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
        return this;
    }

    /**
     * 获取 单点注销回调url
     *
     * @return ssoLogoutCall 单点注销回调url
     */
    public String getSsoLogoutCall() {
        return this.ssoLogoutCall;
    }

    /**
     * 设置 单点注销回调url
     *
     * @param ssoLogoutCall 单点注销回调url
     * @return /
     */
    public SaSsoClientModel setSsoLogoutCall(String ssoLogoutCall) {
        this.ssoLogoutCall = ssoLogoutCall;
        return this;
    }

    /**
     * 获取 此 client 注册信息的时间，13位时间戳
     *
     * @return regTime 此 client 注册信息的时间，13位时间戳
     */
    public long getRegTime() {
        return this.regTime;
    }

    /**
     * 设置 此 client 注册信息的时间，13位时间戳
     *
     * @param regTime 此 client 注册信息的时间，13位时间戳
     * @return /
     */
    public SaSsoClientModel setRegTime(long regTime) {
        this.regTime = regTime;
        return this;
    }

    /**
     * 获取 此账号有记录以来为第几次登录，默认从0开始递增
     *
     * @return regTime 此账号有记录以来为第几次登录，默认从0开始递增
     */
    public long getIndex() {
        return this.index;
    }

    /**
     * 设置 此账号有记录以来为第几次登录，默认从0开始递增
     *
     * @param index 此账号有记录以来为第几次登录，默认从0开始递增
     * @return /
     */
    public SaSsoClientModel setIndex(int index) {
        this.index = index;
        return this;
    }

    @Override
    public String toString() {
        return "SaSsoClientModel{" +
                "mode=" + mode +
                ", client='" + client + '\'' +
                ", tokenValue='" + tokenValue + '\'' +
                ", ssoLogoutCall='" + ssoLogoutCall + '\'' +
                ", regTime=" + regTime +
                ", index=" + index +
                '}';
    }

}