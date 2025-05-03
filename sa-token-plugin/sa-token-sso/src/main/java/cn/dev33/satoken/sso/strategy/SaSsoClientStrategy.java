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
package cn.dev33.satoken.sso.strategy;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.sso.function.SendHttpFunction;
import cn.dev33.satoken.sso.function.TicketResultHandleFunction;

/**
 * Sa-Token SSO Client 相关策略
 *
 * @author click33
 * @since 1.43.0
 */
public class SaSsoClientStrategy {

    /**
     * 发送 Http 请求的处理函数
     */
    public SendHttpFunction sendHttp = url -> {
        return SaManager.getSaHttpTemplate().get(url);
    };

    /**
     * SSO-Client端：自定义校验 ticket 返回值的处理逻辑 （每次从认证中心获取校验 ticket 的结果后调用）
     * <p> 参数：loginId, back
     * <p> 返回值：返回给前端的值
     */
    public TicketResultHandleFunction ticketResultHandle = null;

}
