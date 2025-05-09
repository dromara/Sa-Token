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
package cn.dev33.satoken.sso.template;

import cn.dev33.satoken.sso.config.SaSsoClientModel;
import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.model.TicketModel;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.util.SaResult;

import java.util.List;

/**
 * SSO 工具类 （Server端）
 *
 * @author click33
 * @since 1.43.0
 */
public class SaSsoServerUtil {

    private SaSsoServerUtil() {
    }

    /**
     * 返回底层使用的 SaSsoServerTemplate 对象
     * @return /
     */
    public static SaSsoServerTemplate getSsoTemplate() {
        return SaSsoServerProcessor.instance.ssoServerTemplate;
    }


    // ---------------------- Ticket 操作 ----------------------

    // 增删改

    /**
     * 删除 Ticket
     * @param ticket Ticket码
     */
    public static void deleteTicket(String ticket) {
        SaSsoServerProcessor.instance.ssoServerTemplate.deleteTicket(ticket);
    }

    /**
     * 根据参数创建一个 ticket 码，并保存
     *
     * @param client 客户端标识
     * @param loginId 账号 id
     * @param tokenValue 会话 Token
     * @return Ticket码
     */
    public static String createTicketAndSave(String client, Object loginId, String tokenValue) {
        return SaSsoServerProcessor.instance.ssoServerTemplate.createTicketAndSave(client, loginId, tokenValue);
    }

    // 查

    /**
     * 查询 ticket ，如果 ticket 无效则返回 null
     *
     * @param ticket Ticket码
     * @return 账号id
     */
    public static TicketModel getTicket(String ticket) {
        return SaSsoServerProcessor.instance.ssoServerTemplate.getTicket(ticket);
    }

    /**
     * 查询 ticket 指向的 loginId，如果 ticket 码无效则返回 null
     * @param ticket Ticket码
     * @return 账号id
     */
    public static Object getLoginId(String ticket) {
        return SaSsoServerProcessor.instance.ssoServerTemplate.getLoginId(ticket);
    }

    /**
     * 查询 ticket 指向的 loginId，并转换为指定类型
     * @param <T> 要转换的类型
     * @param ticket Ticket码
     * @param cs 要转换的类型
     * @return 账号id
     */
    public static <T> T getLoginId(String ticket, Class<T> cs) {
        return SaSsoServerProcessor.instance.ssoServerTemplate.getLoginId(ticket, cs);
    }

    // 校验

    /**
     * 校验 Ticket，无效 ticket 会抛出异常
     *
     * @param ticket Ticket码
     * @return /
     */
    public static TicketModel checkTicket(String ticket) {
        return SaSsoServerProcessor.instance.ssoServerTemplate.checkTicket(ticket);
    }

    /**
     * 校验 Ticket 码，无效 ticket 会抛出异常，如果此ticket是有效的，则立即删除
     * @param ticket Ticket码
     * @return 账号id
     */
    public static TicketModel checkTicketParamAndDelete(String ticket) {
        return SaSsoServerProcessor.instance.ssoServerTemplate.checkTicketParamAndDelete(ticket);
    }

    /**
     * 校验 Ticket，无效 ticket 会抛出异常，如果此ticket是有效的，则立即删除
     *
     * @param ticket Ticket码
     * @param client client 标识
     * @return /
     */
    public static TicketModel checkTicketParamAndDelete(String ticket, String client) {
        return SaSsoServerProcessor.instance.ssoServerTemplate.checkTicketParamAndDelete(ticket, client);
    }

    // ticket 索引

    /**
     * 查询 指定 client、loginId 其所属的 ticket 值
     *
     * @param client 应用
     * @param loginId 账号id
     * @return Ticket值
     */
    public static String getTicketValue(String client, Object loginId) {
        return SaSsoServerProcessor.instance.ssoServerTemplate.getTicketValue(client, loginId);
    }


    // ---------------------- Client 信息获取 ----------------------

    /**
     * 获取所有 Client
     *
     * @return /
     */
    public static List<SaSsoClientModel> getClients() {
        return SaSsoServerProcessor.instance.ssoServerTemplate.getClients();
    }

    /**
     * 获取应用信息，无效 client 返回 null
     *
     * @param client /
     * @return /
     */
    public static SaSsoClientModel getClient(String client) {
        return SaSsoServerProcessor.instance.ssoServerTemplate.getClient(client);
    }

    /**
     * 获取应用信息，无效 client 则抛出异常
     *
     * @param client /
     * @return /
     */
    public static SaSsoClientModel getClientNotNull(String client) {
        return SaSsoServerProcessor.instance.ssoServerTemplate.getClientNotNull(client);
    }

    /**
     * 获取匿名 client 信息
     *
     * @return /
     */
    public static SaSsoClientModel getAnonClient() {
        return SaSsoServerProcessor.instance.ssoServerTemplate.getAnonClient();
    }

    /**
     * 获取所有需要接收消息推送的 Client
     *
     * @return /
     */
    public static List<SaSsoClientModel> getNeedPushClients() {
        return SaSsoServerProcessor.instance.ssoServerTemplate.getNeedPushClients();
    }


    // ------------------- 重定向 URL 构建与校验 -------------------

    /**
     * 构建 URL：sso-server 端向 sso-client 下放 ticket 的地址
     *
     * @param client 客户端标识
     * @param redirect sso-client 端的重定向地址
     * @param loginId 账号 id
     * @param tokenValue 会话 token
     * @return /
     */
    public static String buildRedirectUrl(String client, String redirect, Object loginId, String tokenValue) {
        return SaSsoServerProcessor.instance.ssoServerTemplate.buildRedirectUrl(client, redirect, loginId, tokenValue);
    }

    /**
     * 校验重定向 url 合法性
     *
     * @param client 应用标识
     * @param url 下放ticket的url地址
     */
    public static void checkRedirectUrl(String client, String url) {
        SaSsoServerProcessor.instance.ssoServerTemplate.checkRedirectUrl(client, url);
    }


    // ------------------- 单点注销 -------------------

    /**
     * 指定账号单点注销
     *
     * @param loginId 指定账号
     */
    public static void ssoLogout(Object loginId) {
        SaSsoServerProcessor.instance.ssoServerTemplate.ssoLogout(loginId);
    }

    /**
     * 指定账号单点注销
     *
     * @param loginId 指定账号
     * @param logoutParameter 注销参数
     * @param ignoreClient 要被忽略掉的 client，填 null 代表不忽略
     */
    public static void ssoLogout(Object loginId, SaLogoutParameter logoutParameter, String ignoreClient) {
        SaSsoServerProcessor.instance.ssoServerTemplate.ssoLogout(loginId, logoutParameter, ignoreClient);
    }


    // ------------------- 消息推送 -------------------

    /**
     * 向指定 Client 推送消息
     * @param clientModel /
     * @param message /
     * @return /
     */
    public static String pushMessage(SaSsoClientModel clientModel, SaSsoMessage message) {
        return SaSsoServerProcessor.instance.ssoServerTemplate.pushMessage(clientModel, message);
    }

    /**
     * 向指定 client 推送消息，并将返回值转为 SaResult
     *
     * @param clientModel /
     * @param message /
     * @return /
     */
    public static SaResult pushMessageAsSaResult(SaSsoClientModel clientModel, SaSsoMessage message) {
        return SaSsoServerProcessor.instance.ssoServerTemplate.pushMessageAsSaResult(clientModel, message);
    }

    /**
     * 向指定 Client 推送消息
     * @param client /
     * @param message /
     * @return /
     */
    public static String pushMessage(String client, SaSsoMessage message) {
        return SaSsoServerProcessor.instance.ssoServerTemplate.pushMessage(client, message);
    }

    /**
     * 向指定 client 推送消息，并将返回值转为 SaResult
     *
     * @param client /
     * @param message /
     * @return /
     */
    public static SaResult pushMessageAsSaResult(String client, SaSsoMessage message) {
        return SaSsoServerProcessor.instance.ssoServerTemplate.pushMessageAsSaResult(client, message);
    }

    /**
     * 向所有 Client 推送消息
     *
     * @param message /
     */
    public static void pushToAllClient(SaSsoMessage message) {
        SaSsoServerProcessor.instance.ssoServerTemplate.pushToAllClient(message);
    }

    /**
     * 向所有 Client 推送消息，并忽略掉某个 client
     *
     * @param ignoreClient 要被忽略掉的 client，填 null 代表不忽略
     * @param message /
     */
    public static void pushToAllClient(SaSsoMessage message, String ignoreClient) {
        SaSsoServerProcessor.instance.ssoServerTemplate.pushToAllClient(message, ignoreClient);
    }

}
