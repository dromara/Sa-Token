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

    /** 此账号会话剩余有效期 */
    public long remainSessionTimeout;

    /** 从 sso-server 返回的所有参数 */
    public SaResult result;

    public SaCheckTicketResult(Object loginId, long remainSessionTimeout, SaResult result) {
        this.loginId = loginId;
        this.remainSessionTimeout = remainSessionTimeout;
        this.result = result;
    }
    @Override
    public String toString() {
        return "CheckTicketResult{" +
                "loginId=" + loginId +
                ", remainSessionTimeout=" + remainSessionTimeout +
                ", result=" + result +
                '}';
    }

}