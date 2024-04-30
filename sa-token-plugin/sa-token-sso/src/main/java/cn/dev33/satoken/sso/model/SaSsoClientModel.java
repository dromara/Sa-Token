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
     * 客户端标识
     */
     public String client;

    /**
     * 单点注销回调url
     */
    public String ssoLogoutCall;

    /**
     * 此 client 注册信息的时间，13位时间戳
     */
    public Long regTime;

    public SaSsoClientModel() {
    }

    public SaSsoClientModel(String client, String ssoLogoutCall) {
        this.client = client;
        this.ssoLogoutCall = ssoLogoutCall;
        this.regTime = System.currentTimeMillis();
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
     */
    public void setClient(String client) {
        this.client = client;
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
     */
    public void setSsoLogoutCall(String ssoLogoutCall) {
        this.ssoLogoutCall = ssoLogoutCall;
    }

    /**
     * 获取 此 client 注册信息的时间，13位时间戳
     *
     * @return regTime 此 client 注册信息的时间，13位时间戳
     */
    public Long getRegTime() {
        return this.regTime;
    }

    /**
     * 设置 此 client 注册信息的时间，13位时间戳
     *
     * @param regTime 此 client 注册信息的时间，13位时间戳
     */
    public void setRegTime(Long regTime) {
        this.regTime = regTime;
    }

    @Override
    public String toString() {
        return "SaSsoClientModel{" +
                "client='" + client + '\'' +
                ", ssoLogoutCall='" + ssoLogoutCall + '\'' +
                ", regTime='" + regTime + '\'' +
                '}';
    }

}