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
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.sign.SaSignTemplate;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientModel;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.model.SaSsoClientInfo;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.*;

/**
 * Sa-Token SSO 模板方法类 （Server端）
 *
 * @author click33
 * @since 1.38.0
 */
public class SaSsoServerTemplate extends SaSsoTemplate {

    /**
     * 获取底层使用的SsoServer配置对象
     * @return /
     */
    public SaSsoServerConfig getServerConfig() {
        return SaSsoManager.getServerConfig();
    }

    // ---------------------- Ticket 操作 ----------------------

    /**
     * 保存 Ticket 关联的 loginId
     * @param ticket ticket码
     * @param loginId 账号id
     */
    public void saveTicket(String ticket, Object loginId) {
        // 保存 ticket -> loginId 的关系
        long ticketTimeout = getServerConfig().getTicketTimeout();
        SaManager.getSaTokenDao().set(splicingTicketSaveKey(ticket), String.valueOf(loginId), ticketTimeout);
    }

    /**
     * 保存 Ticket 索引 （id 反查 ticket）
     * @param ticket ticket码
     * @param loginId 账号id
     */
    public void saveTicketIndex(String ticket, Object loginId) {
        long ticketTimeout = getServerConfig().getTicketTimeout();
        SaManager.getSaTokenDao().set(splicingTicketIndexKey(loginId), String.valueOf(ticket), ticketTimeout);
    }

    /**
     * 保存 Ticket 关联的 client
     * @param ticket ticket码
     * @param client 客户端标识
     */
    public void saveTicketToClient(String ticket, String client) {
        if(SaFoxUtil.isEmpty(client)) {
            return;
        }
        long ticketTimeout = getServerConfig().getTicketTimeout();
        SaManager.getSaTokenDao().set(splicingTicketToClientSaveKey(ticket), client, ticketTimeout);
    }

    /**
     * 删除 Ticket
     * @param ticket Ticket码
     */
    public void deleteTicket(String ticket) {
        if(ticket == null) {
            return;
        }
        SaManager.getSaTokenDao().delete(splicingTicketSaveKey(ticket));
    }

    /**
     * 删除 Ticket索引
     * @param loginId 账号id
     */
    public void deleteTicketIndex(Object loginId) {
        if(loginId == null) {
            return;
        }
        SaManager.getSaTokenDao().delete(splicingTicketIndexKey(loginId));
    }

    /**
     * 删除 Ticket 关联的 client
     * @param ticket Ticket码
     */
    public void deleteTicketToClient(String ticket) {
        if(ticket == null) {
            return;
        }
        SaManager.getSaTokenDao().delete(splicingTicketToClientSaveKey(ticket));
    }

    /**
     * 查询 ticket 指向的 loginId，如果 ticket 码无效则返回 null
     * @param ticket Ticket码
     * @return 账号id
     */
    public Object getLoginId(String ticket) {
        if(SaFoxUtil.isEmpty(ticket)) {
            return null;
        }
        return SaManager.getSaTokenDao().get(splicingTicketSaveKey(ticket));
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

    /**
     * 查询 指定 loginId 其所属的 ticket 值
     * @param loginId 账号id
     * @return Ticket值
     */
    public String getTicketValue(Object loginId) {
        if(loginId == null) {
            return null;
        }
        return SaManager.getSaTokenDao().get(splicingTicketIndexKey(loginId));
    }

    /**
     * 查询 ticket 关联的 client，如果 ticket 码无效则返回 null
     * @param ticket Ticket码
     * @return 账号id
     */
    public String getTicketToClient(String ticket) {
        if(SaFoxUtil.isEmpty(ticket)) {
            return null;
        }
        return SaManager.getSaTokenDao().get(splicingTicketToClientSaveKey(ticket));
    }

    //

    /**
     * 根据 账号id 创建一个 Ticket码
     * @param loginId 账号id
     * @param client 客户端标识
     * @return Ticket码
     */
    public String createTicket(Object loginId, String client) {
        // 创建 Ticket
        String ticket = randomTicket(loginId);

        // 保存 Ticket
        saveTicket(ticket, loginId);
        saveTicketIndex(ticket, loginId);
        saveTicketToClient(ticket, client);

        // 返回 Ticket
        return ticket;
    }

    /**
     * 校验 Ticket 码，获取账号id，如果此ticket是有效的，则立即删除
     * @param ticket Ticket码
     * @return 账号id
     */
    public Object checkTicket(String ticket) {
        return checkTicket(ticket, SaSsoConsts.CLIENT_WILDCARD);
    }

    /**
     * 校验 Ticket 码，获取账号id，如果此ticket是有效的，则立即删除
     * @param ticket Ticket码
     * @param client client 标识
     * @return 账号id
     */
    public Object checkTicket(String ticket, String client) {
        // 读取 loginId
        String loginId = SaManager.getSaTokenDao().get(splicingTicketSaveKey(ticket));

        if(loginId != null) {

            // 解析出这个 ticket 关联的 Client
            String ticketClient = getTicketToClient(ticket);

            // 校验 client 参数是否正确，即：创建 ticket 的 client 和当前校验 ticket 的 client 是否一致
            if(SaSsoConsts.CLIENT_WILDCARD.equals(client)) {
                // 如果提供的是通配符，直接越过 client 校验
            } else if (SaFoxUtil.isEmpty(client) && SaFoxUtil.isEmpty(ticketClient)) {
                // 如果提供的和期望的两者均为空，则通过校验
            } else {
                // 开始详细比对
                if(SaFoxUtil.notEquals(client, ticketClient)) {
                    throw new SaSsoException("该 ticket 不属于 client=" + client + ", ticket 值: " + ticket)
                            .setCode(SaSsoErrorCode.CODE_30011);
                }
            }

            // 删除 ticket 信息，使其只有一次性有效
            deleteTicket(ticket);
            deleteTicketIndex(loginId);
            deleteTicketToClient(ticket);
        }

        //
        return loginId;
    }

    /**
     * 随机一个 Ticket码
     * @param loginId 账号id
     * @return Ticket码
     */
    public String randomTicket(Object loginId) {
        return SaFoxUtil.getRandomString(64);
    }

    /**
     * 获取：所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket)
     *
     * @param client /
     * @return /
     */
//    public String getAllowUrl(String client) {
//        if(SaFoxUtil.isEmpty(client)) {
//            return getServerConfig().getAllowUrl();
//        } else {
//            return getClientNotNull(client).getAllowUrl();
//        }
//    }

    /**
     * 获取应用信息，无效 client 则抛出异常
     *
     * @param client /
     * @return /
     */
    public SaSsoClientModel getClientNotNull(String client) {
        if(SaFoxUtil.isEmpty(client)) {
            if(getServerConfig().getAllowAnonClient()) {
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
     * 获取匿名 client 信息
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
     * 获取应用信息，无效 client 返回 null
     *
     * @param client /
     * @return /
     */
    public SaSsoClientModel getClient(String client) {
        return getServerConfig().getClients().get(client);
    }

    /**
     * 校验重定向url合法性
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

    // ------------------- SSO -------------------

    /**
     * 指定账号单点注销
     * @param loginId 指定账号
     */
    public void ssoLogout(Object loginId) {

        // 如果这个账号尚未登录，则无操作
        SaSession session = getStpLogic().getSessionByLoginId(loginId, false);
        if(session == null) {
            return;
        }

        // step.1 遍历通知 Client 端注销会话
        List<SaSsoClientInfo> scmList = session.get(SaSsoConsts.SSO_CLIENT_MODEL_LIST_KEY_, ArrayList::new);
        scmList.forEach(scm -> {
            notifyClientLogout(loginId, scm, false);
        });

        // step.2 Server端注销
        getStpLogic().logout(loginId);
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
     * 为指定账号id注册单点注销回调信息（模式三）
     * @param loginId 账号id
     * @param client 指定客户端标识，可为null
     * @param sloCallbackUrl 单点注销时的回调URL
     */
    public void registerSloCallbackUrl(Object loginId, String client, String sloCallbackUrl) {
        // 如果提供的参数是空值，则直接返回，不进行任何操作
        if(SaFoxUtil.isEmpty(loginId)) {
            return;
        }

        SaSession session = getStpLogic().getSessionByLoginId(loginId);

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
                    notifyClientLogout(loginId, removeScm, true);
                } else {
                    break;
                }
            }
        }

        // 存入持久库
        session.set(SaSsoConsts.SSO_CLIENT_MODEL_LIST_KEY_, scmList);
    }

    /**
     * 通知指定账号的指定客户端注销
     * @param loginId 指定账号
     * @param scm 客户端信息对象
     * @param autoLogout 是否为超过 maxRegClient 的自动注销
     */
    public void notifyClientLogout(Object loginId, SaSsoClientInfo scm, boolean autoLogout) {

        // 如果给个null值，不进行任何操作
        if(scm == null || scm.mode != SaSsoConsts.SSO_MODE_3) {
            return;
        }

        // url
        String sloCallUrl = scm.getSloCallbackUrl();
        if(SaFoxUtil.isEmpty(sloCallUrl)) {
            return;
        }

        // 参数
        Map<String, Object> paramsMap = new TreeMap<>();
        paramsMap.put(paramName.client, scm.getClient());
        paramsMap.put(paramName.loginId, loginId);
        paramsMap.put(paramName.autoLogout, autoLogout);
        String signParamsStr = getSignTemplate(scm.getClient()).addSignParamsAndJoin(paramsMap);

        // 拼接
        String finalUrl = SaFoxUtil.joinParam(sloCallUrl, signParamsStr);

        // 发起请求
        getServerConfig().sendHttp.apply(finalUrl);
    }

    // ---------------------- 构建URL ----------------------

    /**
     * 构建URL：Server端向Client下放ticket的地址
     * @param loginId 账号id
     * @param client 客户端标识
     * @param redirect Client端提供的重定向地址
     * @return see note
     */
    public String buildRedirectUrl(Object loginId, String client, String redirect) {

        // 校验 重定向地址 是否合法
        checkRedirectUrl(client, redirect);

        // 删掉 旧Ticket
        deleteTicket(getTicketValue(loginId));

        // 创建 新Ticket
        String ticket = createTicket(loginId, client);

        // 构建 授权重定向地址 （Server端 根据此地址向 Client端 下放Ticket）
        return SaFoxUtil.joinParam(encodeBackParam(redirect), paramName.ticket, ticket);
    }

    /**
     * 对url中的back参数进行URL编码, 解决超链接重定向后参数丢失的bug
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


    // ---------------------- 重构 ----------------------


    /**
     * 校验 Ticket 码，获取账号id，如果此ticket是有效的，则立即删除
     * @param ticket Ticket码
     * @param client client 标识
     */
    public void checkTicketVerifySign(SaRequest req, String ticket, String client) {
        SaSignTemplate signTemplate = getSignTemplate(client);
        signTemplate.checkRequest(req, paramName.client, paramName.ticket, paramName.ssoLogoutCall);
    }


    /**
     * 获取底层使用的 API 签名对象
     * @param client 指定客户端标识，填 null 代表获取默认的
     * @return /
     */
    @Override
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
     * 拼接key：Ticket 查 账号Id
     * @param ticket ticket值
     * @return key
     */
    public String splicingTicketSaveKey(String ticket) {
        return getStpLogic().getConfigOrGlobal().getTokenName() + ":ticket:" + ticket;
    }

    /**
     * 拼接key：Ticket 查 所属的 client
     * @param ticket ticket值
     * @return key
     */
    public String splicingTicketToClientSaveKey(String ticket) {
        return getStpLogic().getConfigOrGlobal().getTokenName() + ":ticket-client:" + ticket;
    }

    /**
     * 拼接key：账号Id 反查 Ticket
     * @param id 账号id
     * @return key
     */
    public String splicingTicketIndexKey(Object id) {
        return getStpLogic().getConfigOrGlobal().getTokenName() + ":id-ticket:" + id;
    }

}
