/*
 * Copyright 2020-2099 sa-token.com
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
package cn.dev33.satoken.sso.template;

import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.util.SaResult;

import java.util.Map;

/**
 * SSO 模板方法类 （Client端）
 *
 * @author click33
 * @since 1.38.0
 */
public class SaSsoClientUtil {

    private SaSsoClientUtil() {
    }

    /**
     * 返回底层使用的 SaSsoClientTemplate 对象
     * @return /
     */
    public static SaSsoClientTemplate getSsoTemplate() {
        return SaSsoClientProcessor.instance.ssoClientTemplate;
    }


    // ------------------- getData 相关 -------------------

    /**
     * 根据配置的 getData 地址，查询数据
     *
     * @param paramMap 查询参数
     * @return 查询结果
     */
    public static Object getData(Map<String, Object> paramMap) {
        return SaSsoClientProcessor.instance.ssoClientTemplate.getData(paramMap);
    }

    /**
     * 根据自定义 path 地址，查询数据 （此方法需要配置 sa-token.sso.server-url 地址）
     *
     * @param path 自定义 path
     * @param paramMap 查询参数
     * @return 查询结果
     */
    public static Object getData(String path, Map<String, Object> paramMap) {
        return SaSsoClientProcessor.instance.ssoClientTemplate.getData(path, paramMap);
    }


    // ---------------------- 构建交互地址 ----------------------

    /**
     * 构建URL：Server端 单点登录授权地址，
     * <br/> 形如：http://sso-server.com/sso/auth?redirectUrl=http://sso-client.com/sso/login?back=http://sso-client.com
     * <br/> 注意：如果 clientLoginUrl 是编码状态，则无法识别 back 参数，请调用者在编码前自行拼接 back 参数
     * @param clientLoginUrl Client端登录地址
     * @param back 回调路径
     * @return [SSO-Server端-认证地址 ]
     */
    public static String buildServerAuthUrl(String clientLoginUrl, String back) {
        return SaSsoClientProcessor.instance.ssoClientTemplate.buildServerAuthUrl(clientLoginUrl, back);
    }


    // ------------------- 消息推送 -------------------

    /**
     * 向 sso-server 推送消息
     *
     * @param message /
     * @return /
     */
    public static String pushMessage(SaSsoMessage message) {
        return SaSsoClientProcessor.instance.ssoClientTemplate.pushMessage(message);
    }

    /**
     * 向 sso-server 推送消息，并将返回值转为 SaResult
     *
     * @param message /
     * @return /
     */
    public static SaResult pushMessageAsSaResult(SaSsoMessage message) {
        return SaSsoClientProcessor.instance.ssoClientTemplate.pushMessageAsSaResult(message);
    }

    /**
     * 构建消息：校验 ticket
     *
     * @param ticket ticket码
     * @param ssoLogoutCallUrl 单点注销时的回调URL
     * @return 构建完毕的URL
     */
    public static SaSsoMessage buildCheckTicketMessage(String ticket, String ssoLogoutCallUrl) {
        return SaSsoClientProcessor.instance.ssoClientTemplate.buildCheckTicketMessage(ticket, ssoLogoutCallUrl);
    }

    /**
     * 构建消息：单点注销
     *
     * @param loginId 要注销的账号 id
     * @param logoutParameter 单点注销
     * @return 单点注销URL
     */
    public static SaSsoMessage buildSignoutMessage(Object loginId, SaLogoutParameter logoutParameter) {
        return SaSsoClientProcessor.instance.ssoClientTemplate.buildSignoutMessage(loginId, logoutParameter);
    }


    // ------------------- 单点注销 -------------------

    /**
     * 指定账号单点注销（向认证中心推送，并由认证中心通知各端下线）
     *
     * @param loginId 指定账号（本地 loginId，会按策略转换为认证中心 id）
     */
    public static void ssoLogout(Object loginId) {
        SaSsoClientProcessor.instance.ssoClientTemplate.ssoLogout(loginId);
    }

    /**
     * 指定账号单点注销（向认证中心推送，并由认证中心通知各端下线）
     *
     * @param loginId 指定账号（本地 loginId，会按策略转换为认证中心 id）
     * @param logoutParameter 注销参数
     */
    public static void ssoLogout(Object loginId, SaLogoutParameter logoutParameter) {
        SaSsoClientProcessor.instance.ssoClientTemplate.ssoLogout(loginId, logoutParameter);
    }

}
