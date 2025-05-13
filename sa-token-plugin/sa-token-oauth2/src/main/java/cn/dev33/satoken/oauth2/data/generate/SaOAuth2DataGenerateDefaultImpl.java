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
package cn.dev33.satoken.oauth2.data.generate;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.dao.SaOAuth2Dao;
import cn.dev33.satoken.oauth2.data.convert.SaOAuth2DataConverter;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.RefreshTokenModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2AuthorizationCodeException;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.exception.SaOAuth2RefreshTokenException;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaTtlMethods;

import java.util.List;
import java.util.function.Consumer;

/**
 * Sa-Token OAuth2 数据构建器，默认实现类
 *
 * @author click33
 * @since 1.39.0
 */
public class SaOAuth2DataGenerateDefaultImpl implements SaOAuth2DataGenerate, SaTtlMethods {

    /**
     * 构建 Model：Code授权码
     * @param ra 请求参数Model
     * @return 授权码Model
     */
    @Override
    public CodeModel generateCode(RequestAuthModel ra) {

        SaOAuth2Dao dao = SaOAuth2Manager.getDao();

        // 删除旧 Code
        dao.deleteCode(dao.getCodeValue(ra.clientId, ra.loginId));

        // 生成新 Code
        CodeModel cm = SaOAuth2Manager.getDataConverter().convertRequestAuthToCode(ra);

        // 保存新 Code
        dao.saveCode(cm);
        dao.saveCodeIndex(cm);

        // 保存 code -> nonce
        dao.saveCodeNonceIndex(cm);

        // 返回
        return cm;
    }

    /**
     * 构建 Model：Access-Token (根据 code 授权码)
     * @param code 授权码
     * @return AccessToken Model
     */
    @Override
    public AccessTokenModel generateAccessToken(String code) {

        SaOAuth2Dao dao = SaOAuth2Manager.getDao();
        SaOAuth2DataConverter dataConverter = SaOAuth2Manager.getDataConverter();

        // 1、先校验
        CodeModel cm = dao.getCode(code);
        SaOAuth2AuthorizationCodeException.throwBy(cm == null, "无效 code: " + code, code, SaOAuth2ErrorCode.CODE_30110);

        // 2、开发者自定义的授权前置检查
        SaOAuth2Strategy.instance.userAuthorizeClientCheck.run(cm.loginId, cm.clientId);

        // 3、生成新 Access-Token
        SaClientModel clientModel = SaOAuth2Manager.getDataLoader().getClientModelNotNull(cm.clientId);
        AccessTokenModel at = dataConverter.convertCodeToAccessToken(cm, clientModel.getAccessTokenTimeout());
        SaOAuth2Strategy.instance.workAccessTokenByScope.accept(at);

        // 4、生成新 Refresh-Token
        RefreshTokenModel rt = dataConverter.convertAccessTokenToRefreshToken(at, clientModel.getRefreshTokenTimeout());
        at.refreshToken = rt.refreshToken;
        at.refreshExpiresTime = rt.expiresTime;

        // 5、保存 Access-Token、Refresh-Token
        dao.saveAccessToken(at);
        dao.saveAccessTokenIndex_AndAdjust(at, clientModel.getMaxAccessTokenCount());
        dao.saveRefreshToken(rt);
        dao.saveRefreshTokenIndex_AndAdjust(rt, clientModel.getMaxRefreshTokenCount());

        // 6、删除 Code (一个 code 只可以使用一次)
        dao.deleteCode(code);
        dao.deleteCodeIndex(cm.clientId, cm.loginId);

        // 7、返回 Access-Token
        return at;
    }

    /**
     * 刷新 Model：根据 Refresh-Token 生成一个新的 Access-Token
     * @param refreshToken Refresh-Token值
     * @return 新的 Access-Token
     */
    @Override
    public AccessTokenModel refreshAccessToken(String refreshToken) {

        SaOAuth2Dao dao = SaOAuth2Manager.getDao();

        // 1、获取 Refresh-Token 信息
        RefreshTokenModel rt = dao.getRefreshToken(refreshToken);
        SaOAuth2RefreshTokenException.throwBy(rt == null, "无效 refresh_token: " + refreshToken, refreshToken, SaOAuth2ErrorCode.CODE_30111);

        // 2、开发者自定义的授权前置检查
        SaOAuth2Strategy.instance.userAuthorizeClientCheck.run(rt.loginId, rt.clientId);

        // 3、如果配置了 isNewRefresh=true，则生成一个新的 Refresh-Token
        SaClientModel clientModel = SaOAuth2Manager.getDataLoader().getClientModelNotNull(rt.clientId);
        if(clientModel.getIsNewRefresh()) {
            rt = SaOAuth2Manager.getDataConverter().convertRefreshTokenToRefreshToken(rt, clientModel.getRefreshTokenTimeout());
            dao.saveRefreshToken(rt);
            dao.saveRefreshTokenIndex_AndAdjust(rt, clientModel.getMaxRefreshTokenCount());
        }

        // 4、生成新 Access-Token
        AccessTokenModel at = SaOAuth2Manager.getDataConverter().convertRefreshTokenToAccessToken(rt, clientModel.getAccessTokenTimeout());
        SaOAuth2Strategy.instance.refreshAccessTokenWorkByScope.accept(at);

        // 5、保存新 Access-Token
        dao.saveAccessToken(at);
        dao.saveAccessTokenIndex_AndAdjust(at, clientModel.getMaxAccessTokenCount());

        // 6、返回新 Access-Token
        return at;
    }

    /**
     * 构建 Model：Access-Token (根据 RequestAuthModel 构建，用于隐藏式 and 密码式)
     * @param ra 请求参数Model
     * @param isCreateRt 是否生成对应的Refresh-Token
     * @param appendWork 对生成的 AccessTokenModel 进行追加操作
     *
     * @return Access-Token Model
     */
    @Override
    public AccessTokenModel generateAccessToken(RequestAuthModel ra, boolean isCreateRt, Consumer<AccessTokenModel> appendWork) {

        SaOAuth2Dao dao = SaOAuth2Manager.getDao();
        SaOAuth2DataConverter dataConverter = SaOAuth2Manager.getDataConverter();

        // 1、开发者自定义的授权前置检查
        SaOAuth2Strategy.instance.userAuthorizeClientCheck.run(ra.loginId, ra.clientId);

        // 2、生成 Access-Token
        SaClientModel clientModel = SaOAuth2Manager.getDataLoader().getClientModelNotNull(ra.clientId);
        AccessTokenModel at = dataConverter.convertRequestAuthToAccessToken(ra, clientModel.getAccessTokenTimeout());
        if(appendWork != null) {
            appendWork.accept(at);
        }
        SaOAuth2Strategy.instance.workAccessTokenByScope.accept(at);

        // 3、生成 & 保存 Refresh-Token
        if(isCreateRt) {
            RefreshTokenModel rt = dataConverter.convertAccessTokenToRefreshToken(at, clientModel.getRefreshTokenTimeout());
            at.refreshToken = rt.refreshToken;
            at.refreshExpiresTime = rt.expiresTime;

            dao.saveRefreshToken(rt);
            dao.saveRefreshTokenIndex_AndAdjust(rt, clientModel.getMaxRefreshTokenCount());
        }

        // 4、保存 Access-Token
        dao.saveAccessToken(at);
        dao.saveAccessTokenIndex_AndAdjust(at, clientModel.getMaxAccessTokenCount());

        // 5、返回 Access-Token
        return at;
    }

    /**
     * 构建 Model：Client-Token
     * @param clientId 应用id
     * @param scopes 授权范围
     * @return Client-Token Model
     */
    @Override
    public ClientTokenModel generateClientToken(String clientId, List<String> scopes) {

        SaOAuth2Dao dao = SaOAuth2Manager.getDao();

        // 1、如果配置了 Lower-Client-Token 的 ttl ，则需要更新一下
        SaClientModel clientModel = SaOAuth2Manager.getDataLoader().getClientModelNotNull(clientId);

        // 2、生成 Client-Token
        ClientTokenModel ct = SaOAuth2Manager.getDataConverter().convertSaClientToClientToken(clientModel, scopes);
        SaOAuth2Strategy.instance.workClientTokenByScope.accept(ct);

        // 3、保存 Client-Token
        dao.saveClientToken(ct);
        dao.saveClientTokenIndex_AndAdjust(ct, clientModel.getMaxClientTokenCount());

        // 4、返回
        return ct;
    }

    /**
     * 构建 URL：下放Code URL (Authorization Code 授权码)
     * @param redirectUri 下放地址
     * @param code code参数
     * @param state state参数
     * @return 构建完毕的URL
     */
    @Override
    public String buildRedirectUri(String redirectUri, String code, String state) {
        String url = SaFoxUtil.joinParam(redirectUri, SaOAuth2Consts.Param.code, code);
        if( ! SaFoxUtil.isEmpty(state)) {
            checkState(state);
            url = SaFoxUtil.joinParam(url, SaOAuth2Consts.Param.state, state);
        }
        return url;
    }

    /**
     * 构建 URL：下放Access-Token URL （implicit 隐藏式）
     * @param redirectUri 下放地址
     * @param token token
     * @param state state参数
     * @return 构建完毕的URL
     */
    @Override
    public String buildImplicitRedirectUri(String redirectUri, String token, String state) {
        String url = SaFoxUtil.joinSharpParam(redirectUri, SaOAuth2Consts.Param.token, token);
        if( ! SaFoxUtil.isEmpty(state)) {
            checkState(state);
            url = SaFoxUtil.joinSharpParam(url, SaOAuth2Consts.Param.state, state);
        }
        return url;
    }

    /**
     * 检查 state 是否被重复使用
     * @param state /
     */
    @Override
    public void checkState(String state) {
        String value = SaOAuth2Manager.getDao().getState(state);
        if(SaFoxUtil.isNotEmpty(value)) {
            throw new SaOAuth2Exception("多次请求的 state 不可重复: " + state).setCode(SaOAuth2ErrorCode.CODE_30127);
        }
        SaOAuth2Manager.getDao().saveState(state);
    }

}

