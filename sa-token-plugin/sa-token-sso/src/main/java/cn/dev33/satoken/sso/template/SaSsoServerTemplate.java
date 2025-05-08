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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaSignConfig;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.sign.SaSignTemplate;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientModel;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.message.handle.server.SaSsoMessageCheckTicketHandle;
import cn.dev33.satoken.sso.message.handle.server.SaSsoMessageSignoutHandle;
import cn.dev33.satoken.sso.model.SaSsoClientInfo;
import cn.dev33.satoken.sso.model.TicketModel;
import cn.dev33.satoken.sso.strategy.SaSsoServerStrategy;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

import java.util.*;

/**
 * SSO 模板方法类 （Server端）
 *
 * @author click33
 * @since 1.38.0
 */
public class SaSsoServerTemplate extends SaSsoTemplate {

    /**
     * Server 相关策略
     */
    public SaSsoServerStrategy strategy = new SaSsoServerStrategy();

    public SaSsoServerTemplate() {
        super.messageHolder.addHandle(new SaSsoMessageCheckTicketHandle());
        super.messageHolder.addHandle(new SaSsoMessageSignoutHandle());
    }

    // ---------------------- Ticket 操作 ----------------------

    // 增删改

    /**
     * 保存 Ticket
     * @param ticketModel /
     */
    public void saveTicket(TicketModel ticketModel) {
        long ticketTimeout = getServerConfig().getTicketTimeout();
        SaManager.getSaTokenDao().setObject(splicingTicketModelSaveKey(ticketModel.getTicket()), ticketModel, ticketTimeout);
    }

    /**
     * 删除 Ticket
     * @param ticket Ticket码
     */
    public void deleteTicket(String ticket) {
        if(ticket == null) {
            return;
        }
        SaManager.getSaTokenDao().deleteObject(splicingTicketModelSaveKey(ticket));
    }

    /**
     * 根据参数创建一个 ticket 码
     *
     * @param client 客户端标识
     * @param loginId 账号 id
     * @param tokenValue 会话 Token
     * @return Ticket码
     */
    public TicketModel createTicket(String client, Object loginId, String tokenValue) {
        TicketModel ticketModel = new TicketModel();
        ticketModel.setTicket(randomTicket(loginId));
        ticketModel.setClient(client);
        ticketModel.setLoginId(loginId);
        ticketModel.setTokenValue(tokenValue);
        return ticketModel;
    }

    /**
     * 根据参数创建一个 ticket 码，并保存
     *
     * @param client 客户端标识
     * @param loginId 账号 id
     * @param tokenValue 会话 Token
     * @return Ticket码
     */
    public String createTicketAndSave(String client, Object loginId, String tokenValue) {
        // 创建
        TicketModel ticketModel = createTicket(client, loginId, tokenValue);

        // 保存
        saveTicket(ticketModel);
        saveTicketIndex(client, loginId, ticketModel.getTicket());

        // 返回
        return ticketModel.getTicket();
    }

    /**
     * 随机一个 Ticket 码
     * @param loginId 账号id
     * @return Ticket 码
     */
    public String randomTicket(Object loginId) {
        return SaFoxUtil.getRandomString(64);
    }

    // 查

    /**
     * 查询 ticket ，如果 ticket 无效则返回 null
     *
     * @param ticket Ticket码
     * @return 账号id
     */
    public TicketModel getTicket(String ticket) {
        if(SaFoxUtil.isEmpty(ticket)) {
            return null;
        }
        return SaManager.getSaTokenDao().getObject(splicingTicketModelSaveKey(ticket), TicketModel.class);
    }

    /**
     * 查询 ticket 指向的 loginId，如果 ticket 码无效则返回 null
     * @param ticket Ticket码
     * @return 账号id
     */
    public Object getLoginId(String ticket) {
        TicketModel ticketModel = getTicket(ticket);
        if(ticketModel == null) {
            return null;
        }
        return ticketModel.getLoginId();
    }

    /**
     * 查询 ticket 指向的 loginId，并转换为指定类型
     * @param <T> 要转换的类型
     * @param ticket Ticket码
     * @param cs 要转换的类型
     * @return 账号id
     */
    public <T> T getLoginId(String ticket, Class<T> cs) {
        return SaFoxUtil.getValueByType(getLoginId(ticket), cs);
    }

    // 校验

    /**
     * 校验 Ticket，无效 ticket 会抛出异常
     *
     * @param ticket Ticket码
     * @return /
     */
    public TicketModel checkTicket(String ticket) {
        TicketModel ticketModel = getTicket(ticket);
        if(ticketModel == null) {
            throw new SaSsoException("无效 ticket : " + ticket).setCode(SaSsoErrorCode.CODE_30004);
        }
        return ticketModel;
    }

    /**
     * 校验 Ticket 码，无效 ticket 会抛出异常，如果此ticket是有效的，则立即删除
     * @param ticket Ticket码
     * @return 账号id
     */
    public TicketModel checkTicketParamAndDelete(String ticket) {
        return checkTicketParamAndDelete(ticket, SaSsoConsts.CLIENT_WILDCARD);
    }

    /**
     * 校验 Ticket，无效 ticket 会抛出异常，如果此ticket是有效的，则立即删除
     *
     * @param ticket Ticket码
     * @param client client 标识
     * @return /
     */
    public TicketModel checkTicketParamAndDelete(String ticket, String client) {
        TicketModel ticketModel = checkTicket(ticket);

        // 校验 client 参数是否正确，即：创建 ticket 的 client 和当前校验 ticket 的 client 是否一致
        String ticketClient = ticketModel.getClient();
        if(SaSsoConsts.CLIENT_WILDCARD.equals(client)) {
            // 如果提供的是通配符，直接越过 client 校验
        } else if (SaFoxUtil.isEmpty(client) && SaFoxUtil.isEmpty(ticketClient)) {
            // 如果提供的和期望的两者均为空，则通过校验
        } else {
            // 开始详细比对
            if(SaFoxUtil.notEquals(client, ticketClient)) {
                throw new SaSsoException("该 ticket 不属于 client=" + client + ", ticket 值: " + ticket).setCode(SaSsoErrorCode.CODE_30011);
            }
        }

        // 删除 ticket 信息，使其只有一次性有效
        deleteTicket(ticket);
        deleteTicketIndex(client, ticketModel.getLoginId());

        //
        return ticketModel;
    }

    // ticket 索引

    /**
     * 保存 Ticket 索引 （id 反查 ticket）
     *
     * @param client 应用端
     * @param ticket ticket码
     * @param loginId 账号id
     */
    public void saveTicketIndex(String client, Object loginId, String ticket) {
        long ticketTimeout = getServerConfig().getTicketTimeout();
        SaManager.getSaTokenDao().set(splicingTicketIndexKey(client, loginId), String.valueOf(ticket), ticketTimeout);
    }

    /**
     * 删除 Ticket 索引
     *
     * @param client 应用标识
     * @param loginId 账号id
     */
    public void deleteTicketIndex(String client, Object loginId) {
        if(loginId == null) {
            return;
        }
        SaManager.getSaTokenDao().delete(splicingTicketIndexKey(client, loginId));
    }

    /**
     * 查询 指定 client、loginId 其所属的 ticket 值
     *
     * @param client 应用
     * @param loginId 账号id
     * @return Ticket值
     */
    public String getTicketValue(String client, Object loginId) {
        if(loginId == null) {
            return null;
        }
        return SaManager.getSaTokenDao().get(splicingTicketIndexKey(client, loginId));
    }


    // ---------------------- Client 信息获取 ----------------------

    /**
     * 获取所有 Client
     *
     * @return /
     */
    public List<SaSsoClientModel> getClients() {
        return new ArrayList<>(getServerConfig().getClients().values());
    }

    /**
     * 获取应用信息，无效 client 返回 null
     *
     * @param client /
     * @return /
     */
    public SaSsoClientModel getClient(String client) {
        return getServerConfig().getClients().get(client);
    }

    /**
     * 获取应用信息，无效 client 则抛出异常
     *
     * @param client /
     * @return /
     */
    public SaSsoClientModel getClientNotNull(String client) {
        if(SaFoxUtil.isEmpty(client)) {
            if(getConfigOfAllowAnonClient()) {
                return getAnonClient();
            } else {
                throw new SaSsoException("client 标识不可为空");
            }
        } else {
            SaSsoClientModel scm = getClient(client);
            if(scm == null) {
                throw new SaSsoException("未能获取应用信息，client=" + client).setCode(SaSsoErrorCode.CODE_30013);
            }
            return scm;
        }
    }

    /**
     * 获取配置项：是否允许匿名 client 接入
     *
     * @return /
     */
    public boolean getConfigOfAllowAnonClient() {
        return getServerConfig().getAllowAnonClient();
    }

    /**
     * 获取匿名 client 配置信息
     *
     * @return /
     */
    public SaSsoClientModel getAnonClient() {
        SaSsoServerConfig serverConfig = getServerConfig();
        SaSsoClientModel scm = new SaSsoClientModel();
        scm.setAllowUrl(serverConfig.getAllowUrl());
        scm.setIsSlo(serverConfig.getIsSlo());
        scm.setSecretKey(serverConfig.getSecretKey());
        if(SaFoxUtil.isEmpty(scm.getSecretKey())) {
            scm.setSecretKey(SaManager.getSaSignTemplate().getSignConfigOrGlobal().getSecretKey());
        }
        return scm;
    }

    /**
     * 获取所有需要接收消息推送的 Client
     *
     * @return /
     */
    public List<SaSsoClientModel> getNeedPushClients() {
        List<SaSsoClientModel> list = new ArrayList<>();
        List<SaSsoClientModel> clients = getClients();
        for(SaSsoClientModel scm : clients) {
            if (scm.getIsPush()) {
                list.add(scm);
            }
        }
        return list;
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
    public String buildRedirectUrl(String client, String redirect, Object loginId, String tokenValue) {

        // 校验 重定向地址 是否合法
        checkRedirectUrl(client, redirect);

        // 删掉 旧Ticket
        deleteTicket(getTicketValue(client, loginId));

        // 创建 新Ticket
        String ticket = createTicketAndSave(client, loginId, tokenValue);

        // 构建 授权重定向地址 （Server端 根据此地址向 Client端 下放 Ticket）
        return SaFoxUtil.joinParam(encodeBackParam(redirect), paramName.ticket, ticket);
    }

    /**
     * 对 url 中的 back 参数进行 URL 编码, 解决超链接重定向后参数丢失的 bug
     *
     * @param url url
     * @return 编码过后的url
     */
    public String encodeBackParam(String url) {

        // 获取back参数所在位置
        int index = url.indexOf("?" + paramName.back + "=");
        if(index == -1) {
            index = url.indexOf("&" + paramName.back + "=");
            if(index == -1) {
                return url;
            }
        }

        // 开始编码
        int length = paramName.back.length() + 2;
        String back = url.substring(index + length);
        back = SaFoxUtil.encodeUrl(back);

        // 放回url中
        url = url.substring(0, index + length) + back;
        return url;
    }

    /**
     * 校验重定向 url 合法性
     *
     * @param client 应用标识
     * @param url 下放ticket的url地址
     */
    public void checkRedirectUrl(String client, String url) {

        // 1、是否是一个有效的url
        if( ! SaFoxUtil.isUrl(url) ) {
            throw new SaSsoException("无效redirect：" + url).setCode(SaSsoErrorCode.CODE_30001);
        }

        // 2、截取掉?后面的部分
        int qIndex = url.indexOf("?");
        if(qIndex != -1) {
            url = url.substring(0, qIndex);
        }

        // 3、不允许出现@字符
        if(url.contains("@")) {
            //  为什么不允许出现 @ 字符呢，因为这有可能导致 redirect 参数绕过 AllowUrl 列表的校验
            //
            //  举个例子 配置文件：
            //       sa-token.sso-server.allow-url=http://sa-sso-client1.com*
            //
            //  开发者原意是为了允许 sa-sso-client1.com 下的所有地址都可以下放ticket
            //
            //  但是如果攻击者精心构建一个url：
            //       http://sa-sso-server.com:9000/sso/auth?redirect=http://sa-sso-client1.com@sa-token.cc
            //
            //  那么这个url就会绕过 allow-url 的校验，ticket 被下发到了第三方服务器地址：
            //       http://sa-token.cc/?ticket=i8vDfbpqBViMe01QoLY1kHROJWYvv9plBtvTZ6kk77KK0e0U4Xj99NPfSZEYjRul
            //
            //  造成了ticket 参数劫持
            //  所以此处需要禁止在 url 中出现 @ 字符
            //
            //  这么一刀切的做法，可能会导致一些特殊的正常url也无法通过校验，例如：
            //       http://sa-sso-server.com:9000/sso/auth?redirect=http://sa-sso-client1.com:9003/@getInfo
            //
            //  但是为了安全起见，这么做还是有必要的
            throw new SaSsoException("无效redirect（不允许出现@字符）：" + url).setCode(SaSsoErrorCode.CODE_30001);
        }

        // 4、判断是否在 [ 允许的地址列表 ] 之中
        String allowUrlString = getClientNotNull(client).getAllowUrl();
        List<String> allowUrlList = Arrays.asList(allowUrlString.replaceAll(" ", "").split(","));
        checkAllowUrlList(allowUrlList);
        if( ! SaStrategy.instance.hasElement.apply(allowUrlList, url) ) {
            throw new SaSsoException("非法redirect：" + url).setCode(SaSsoErrorCode.CODE_30002);
        }

        // 校验通过 √
    }

    /**
     * 校验配置的 AllowUrl 是否合规，如果不合规则抛出异常
     * @param allowUrlList 待校验的 allow-url 地址列表 
     */
    public void checkAllowUrlList(List<String> allowUrlList){
        checkAllowUrlListStaticMethod(allowUrlList);
    }

    /**
     * 校验配置的 AllowUrl 是否合规，如果不合规则抛出异常
     * @param allowUrlList 待校验的 allow-url 地址列表
     */
    public static void checkAllowUrlListStaticMethod(List<String> allowUrlList){
        for (String url : allowUrlList) {
            int index = url.indexOf("*");
            // 如果配置了 * 字符，则必须出现在最后一位，否则属于无效配置项
            if(index != -1 && index != url.length() - 1) {
                //  为什么不允许 * 字符出现在中间位置呢，因为这有可能导致 redirect 参数绕过 allow-url 列表的校验
                //
                //  举个例子 配置文件：
                //      sa-token.sso-server.allow-url=http://*.sa-sso-client1.com
                //
                //  开发者原意是为了允许 sa-sso-client1.com 下的所有子域名都可以下放ticket
                //      例如：http://shop.sa-sso-client1.com
                //
                //  但是如果攻击者精心构建一个url：
                //       http://sa-sso-server.com:9000/sso/auth?redirect=http://sa-token.cc/a.sa-sso-client1.com/sso/login
                //
                //  那么这个 url 就会绕过 allow-url 的校验，ticket 被下发到了第三方服务器地址：
                //       http://sa-token.cc/a.sa-sso-client1.com/sso/login?ticket=v2KKMUFK7dDsMMzXLQ3aWGsyGUjrA0dBB2jeOWrpCnC8b5ScmXXQSv20mIwPK7Cx
                //
                //  造成了 ticket 参数劫持
                //  所以此处需要禁止 allow-url 配置项的中间位置出现 * 字符（出现在末尾是没有问题的）
                //
                //  这么一刀切的做法，可能会导致正常场景下的子域名url也无法通过校验，例如：
                //       http://sa-sso-server.com:9000/sso/auth?redirect=http://shop.sa-sso-client1.com/sso/login
                //
                //  但是为了安全起见，这么做还是有必要的
                throw new SaSsoException("无效的 allow-url 配置（*通配符只允许出现在最后一位）：" + url).setCode(SaSsoErrorCode.CODE_30015);
            }
        }
    }


    // ------------------- 单点注销 -------------------

    /**
     * 为指定账号 id 注册应用接入信息（模式三）
     *
     * @param loginId 账号id
     * @param client 指定客户端标识，可为null
     * @param sloCallbackUrl 单点注销时的回调URL
     */
    public void registerSloCallbackUrl(Object loginId, String client, String sloCallbackUrl) {
        // 如果提供的参数是空值，则直接返回，不进行任何操作
        if(SaFoxUtil.isEmpty(loginId)) {
            return;
        }

        SaSession session = getStpLogicOrGlobal().getSessionByLoginId(loginId);

        // 取出原来的
        List<SaSsoClientInfo> scmList = session.get(SaSsoConsts.SSO_CLIENT_MODEL_LIST_KEY_, ArrayList::new);

        // 将 新登录client 加入到集合中
        SaSsoClientInfo scm = new SaSsoClientInfo(client, sloCallbackUrl, calcNextIndex(scmList));
        scmList.add(scm);

        // 如果登录的client数量超过了限制，则从最早的一个登录开始清退
        int maxRegClient = getServerConfig().maxRegClient;
        if(maxRegClient != -1)  {
            for (;;) {
                if(scmList.size() > maxRegClient) {
                    SaSsoClientInfo removeScm = scmList.remove(0);
                    strategy.asyncRun.run(() -> {
                        notifyClientLogout(loginId, null, removeScm, true, true);
                    });
                } else {
                    break;
                }
            }
        }

        // 存入持久库
        session.set(SaSsoConsts.SSO_CLIENT_MODEL_LIST_KEY_, scmList);
    }

    /**
     * 计算下一个 index 值
     * @param scmList /
     * @return /
     */
    public int calcNextIndex(List<SaSsoClientInfo> scmList) {
        // 如果目前还没有任何登录记录，则直接返回0
        if(scmList == null || scmList.isEmpty()) {
            return 0;
        }
        // 获取目前最大的index值
        int maxIndex = scmList.get(scmList.size() - 1).index;

        // 如果已经是 int 最大值了，则直接返回0
        if(maxIndex == Integer.MAX_VALUE) {
            return 0;
        }

        // 否则返回最大值+1
        maxIndex++;
        return maxIndex;
    }

    /**
     * 指定账号单点注销
     *
     * @param loginId 指定账号
     */
    public void ssoLogout(Object loginId) {
        ssoLogout(loginId, getStpLogicOrGlobal().createSaLogoutParameter());
    }

    /**
     * 指定账号单点注销
     *
     * @param loginId 指定账号
     * @param logoutParameter 注销参数
     */
    public void ssoLogout(Object loginId, SaLogoutParameter logoutParameter) {

        // 1、消息推送：单点注销
        pushToAllClientByLogoutCall(loginId, logoutParameter);

        // 2、SaSession 挂载的 Client 端注销会话
        SaSession session = getStpLogicOrGlobal().getSessionByLoginId(loginId, false);
        if(session == null) {
            return;
        }
        List<SaSsoClientInfo> scmList = session.get(SaSsoConsts.SSO_CLIENT_MODEL_LIST_KEY_, ArrayList::new);
        scmList.forEach(scm -> {
            strategy.asyncRun.run(() -> {
                notifyClientLogout(loginId, logoutParameter.getDeviceId(), scm, false, false);
            });
        });

        // 3、Server 端本身注销
        getStpLogicOrGlobal().logout(loginId, logoutParameter);
    }

    /**
     * 通知指定账号的指定客户端注销
     *
     * @param loginId 指定账号
     * @param deviceId 指定设备 id
     * @param scm 客户端信息对象
     * @param autoLogout 是否为超过 maxRegClient 的自动注销
     * @param isPushWork 如果该 client 没有注册注销回调地址，是否使用 push 消息的方式进行注销回调通知
     *
     * @return /
     */
    public String notifyClientLogout(Object loginId, String deviceId, SaSsoClientInfo scm, boolean autoLogout, boolean isPushWork) {

        // 如果给个null值，不进行任何操作
        if(scm == null || scm.mode != SaSsoConsts.SSO_MODE_3) {
            return null;
        }

        // 如果此 Client 并没有注册 单点注销 回调地址
        String sloCallUrl = scm.getSloCallbackUrl();
        if(SaFoxUtil.isEmpty(sloCallUrl)) {
            // TODO 代码有效性待验证
            if(isPushWork && SaFoxUtil.isNotEmpty(scm.getClient())) {
                SaSsoClientModel client = getClient(scm.getClient());
                return pushToClientByLogoutCall(client, loginId, getStpLogicOrGlobal().createSaLogoutParameter());
            }
            return null;
        }

        // 参数
        Map<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put(paramName.client, scm.getClient());
        paramsMap.put(paramName.loginId, loginId);
        paramsMap.put(paramName.deviceId, deviceId);
        paramsMap.put(paramName.autoLogout, autoLogout);
        String signParamsStr = getSignTemplate(scm.getClient()).addSignParamsAndJoin(paramsMap);

        // 拼接
        String finalUrl = SaFoxUtil.joinParam(sloCallUrl, signParamsStr);

        // 发起请求
        return strategy.sendRequest.apply(finalUrl);
    }


    // ------------------- 消息推送 -------------------

    /**
     * 向指定 Client 推送消息
     * @param clientModel /
     * @param message /
     * @return /
     */
    public String pushMessage(SaSsoClientModel clientModel, SaSsoMessage message) {
        message.checkType();
        String noticeUrl = clientModel.splicingPushUrl();
        String paramsStr = getSignTemplate(clientModel.getClient()).addSignParamsAndJoin(message);
        String finalUrl = SaFoxUtil.joinParam(noticeUrl, paramsStr);
        return strategy.sendRequest.apply(finalUrl);
    }

    /**
     * 向指定 client 推送消息，并将返回值转为 SaResult
     *
     * @param clientModel /
     * @param message /
     * @return /
     */
    public SaResult pushMessageAsSaResult(SaSsoClientModel clientModel, SaSsoMessage message) {
        String res = pushMessage(clientModel, message);
        Map<String, Object> map = SaManager.getSaJsonTemplate().jsonToMap(res);
        return new SaResult(map);
    }

    /**
     * 向指定 Client 推送消息
     * @param client /
     * @param message /
     * @return /
     */
    public String pushMessage(String client, SaSsoMessage message) {
        return pushMessage(getClientNotNull(client), message);
    }

    /**
     * 向指定 client 推送消息，并将返回值转为 SaResult
     *
     * @param client /
     * @param message /
     * @return /
     */
    public SaResult pushMessageAsSaResult(String client, SaSsoMessage message) {
        String res = pushMessage(client, message);
        Map<String, Object> map = SaManager.getSaJsonTemplate().jsonToMap(res);
        return new SaResult(map);
    }

    /**
     * 向所有 Client 推送消息
     *
     * @param message /
     */
    public void pushToAllClient(SaSsoMessage message) {
        pushToAllClient(message, null);
    }

    /**
     * 向所有 Client 推送消息，并忽略掉某个 client
     *
     * @param ignoreClient 要被忽略掉的 client，填 null 代表不忽略
     * @param message /
     */
    public void pushToAllClient(SaSsoMessage message, String ignoreClient) {
        // TODO 待验证
        List<SaSsoClientModel> needPushClients = getNeedPushClients();
        for (SaSsoClientModel client : needPushClients) {
            if(ignoreClient != null && ignoreClient.equals(client.getClient())) {
                continue;
            }
            strategy.asyncRun.run(() -> pushMessage(client, message));
        }
    }

    /**
     * 向所有 Client 推送消息：单点注销回调
     *
     * @param loginId /
     * @param logoutParameter 注销参数
     */
    public void pushToAllClientByLogoutCall(Object loginId, SaLogoutParameter logoutParameter) {
        List<SaSsoClientModel> npClients = getNeedPushClients();
        for (SaSsoClientModel client : npClients) {
            if(client.getIsSlo()) {
                strategy.asyncRun.run(() -> {
                    pushToClientByLogoutCall(client, loginId, logoutParameter);
                });
            }
        }
    }

    /**
     * 向指定 Client 推送消息：单点注销回调
     *
     * @param loginId /
     * @param logoutParameter 注销参数
     * @return /
     */
    public String pushToClientByLogoutCall(SaSsoClientModel client, Object loginId, SaLogoutParameter logoutParameter) {
        SaSsoMessage message = new SaSsoMessage();
        message.setType(SaSsoConsts.MESSAGE_LOGOUT_CALL);
        message.set(paramName.loginId, loginId);
        message.set(paramName.deviceId, logoutParameter.getDeviceId());
        return pushMessage(client, message);
    }


    // ------------------- Bean 获取 -------------------

    /**
     * 获取底层使用的SsoServer配置对象
     * @return /
     */
    public SaSsoServerConfig getServerConfig() {
        return SaSsoManager.getServerConfig();
    }

    /**
     * 获取底层使用的 API 签名对象
     * @param client 指定客户端标识，填 null 代表获取默认的
     * @return /
     */
    public SaSignTemplate getSignTemplate(String client) {
        SaSignConfig signConfig = SaManager.getSaSignTemplate().getSignConfigOrGlobal().copy();
        SaSsoClientModel clientModel = getClientNotNull(client);

        // 使用 secretKey 的优先级：client 单独配置 > SSO 模块全局配置 > sign 模块默认配置
        String secretKey = clientModel.getSecretKey();
        if (SaFoxUtil.isEmpty(secretKey) && SaFoxUtil.isNotEmpty(client)) {
            secretKey = getServerConfig().getSecretKey();
        }
        if(SaFoxUtil.isEmpty(secretKey)) {
            secretKey = signConfig.getSecretKey();
        }
        signConfig.setSecretKey(secretKey);

        return new SaSignTemplate(signConfig);
    }


    // ------------------- 返回相应key -------------------

    /**
     * 拼接key：TicketModel
     * @param ticket ticket值
     * @return key
     */
    public String splicingTicketModelSaveKey(String ticket) {
        return getStpLogicOrGlobal().getConfigOrGlobal().getTokenName() + ":ticket:" + ticket;
    }

    /**
     * 拼接key：Ticket 索引
     *
     * @param client 应用标识
     * @param id 账号id
     * @return key
     */
    public String splicingTicketIndexKey(String client, Object id) {
        if(SaFoxUtil.isEmpty(client) || SaSsoConsts.CLIENT_WILDCARD.equals(client)) {
            client = SaSsoConsts.CLIENT_ANON;
        }
        return getStpLogicOrGlobal().getConfigOrGlobal().getTokenName() + ":ticket-index:" + client + ":" + id;
    }

}
