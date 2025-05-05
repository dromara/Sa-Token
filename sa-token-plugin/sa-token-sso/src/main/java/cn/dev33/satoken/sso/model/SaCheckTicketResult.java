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

import cn.dev33.satoken.util.SaResult;

import java.io.Serializable;

/**
 * 校验 ticket 返回 loginId 等结果的参数封装
 *
 * @author click33
 * @since 1.38.0
 */
public class SaCheckTicketResult implements Serializable {

    private static final long serialVersionUID = 1406115065849845073L;

    /** 账号id */
    public Object loginId;

    /** 在 sso-server 端的 token 值 */
    public String tokenValue;

    /** 登录设备 id */
    public String deviceId;

    /** 此账号 token 剩余有效期 */
    public Long remainTokenTimeout;

    /** 此账号会话剩余有效期 */
    public Long remainSessionTimeout;

    /** 此账号在认证中心的 loginId */
    public Object centerId;

    /** 从 sso-server 返回的原生所有参数 */
    public SaResult result;

    @Override
    public String toString() {
        return "SaCheckTicketResult{" +
                "loginId=" + loginId +
                ", tokenValue='" + tokenValue + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", remainTokenTimeout=" + remainTokenTimeout +
                ", remainSessionTimeout=" + remainSessionTimeout +
                ", centerId=" + centerId +
                ", result=" + result +
                '}';
    }

}