package com.pj.oauth2.h5;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.generate.SaOAuth2DataGenerate;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sa-Token OAuth2 Server端 控制器 (前后端分离情形下所需要的接口)
 */
@RestController
public class SaOAuth2ServerH5Controller {

    /**
     * 获取最终授权重定向地址，形如：http://xxx.com/xxx?code=xxxxx
     *
     * <p> 情况1：客户端未登录，返回 code=401，提示用户登录 <p/>
     * <p> 情况2：请求的 scope 需要客户端手动确认授权，返回 code=411，提示用户手动确认 <p/>
     * <p> 情况3：已登录且请求的 scope 已确认授权，返回 code=200，redirect_uri=最终重定向 url 地址(携带code码参数) <p/>
     *
     * @return /
     */
    @PostMapping("/oauth2/getRedirectUri")
    public Object getRedirectUri() {

        // 获取变量
        SaRequest req = SaHolder.getRequest();
        SaOAuth2ServerConfig cfg = SaOAuth2Manager.getServerConfig();
        SaOAuth2DataGenerate dataGenerate = SaOAuth2Manager.getDataGenerate();
        SaOAuth2Template oauth2Template = SaOAuth2Manager.getTemplate();
        String responseType = req.getParamNotNull(SaOAuth2Consts.Param.response_type);

        // 1、先判断是否开启了指定的授权模式
        SaOAuth2ServerProcessor.instance.checkAuthorizeResponseType(responseType, req, cfg);

        // 2、如果尚未登录, 则先去登录
        long loginId = SaOAuth2Manager.getStpLogic().getLoginId(0L);
        if(loginId == 0L) {
            return SaResult.get(401, "need login", null);
        }

        // 3、构建请求 Model
        RequestAuthModel ra = SaOAuth2Manager.getDataResolver().readRequestAuthModel(req, loginId);

        // 4、校验：重定向域名是否合法
        oauth2Template.checkRedirectUri(ra.clientId, ra.redirectUri);

        // 5、校验：此次申请的Scope，该Client是否已经签约
        oauth2Template.checkContractScope(ra.clientId, ra.scopes);

        // 6、判断：如果此次申请的Scope，该用户尚未授权，则转到授权页面
        boolean isNeedCarefulConfirm = oauth2Template.isNeedCarefulConfirm(ra.loginId, ra.clientId, ra.scopes);
        if(isNeedCarefulConfirm) {
            SaClientModel cm = oauth2Template.checkClientModel(ra.clientId);
            if( ! cm.getIsAutoConfirm()) {
                // code=411，需要用户手动确认授权
                return SaResult.get(411, "need confirm", null);
            }
        }

        // 7、判断授权类型，重定向到不同地址
        // 		如果是 授权码式，则：开始重定向授权，下放code
        if(SaOAuth2Consts.ResponseType.code.equals(ra.responseType)) {
            CodeModel codeModel = dataGenerate.generateCode(ra);
            String redirectUri = dataGenerate.buildRedirectUri(ra.redirectUri, codeModel.code, ra.state);
            return SaResult.ok().set("redirect_uri", redirectUri);
        }

        // 		如果是 隐藏式，则：开始重定向授权，下放 token
        if(SaOAuth2Consts.ResponseType.token.equals(ra.responseType)) {
            AccessTokenModel at = dataGenerate.generateAccessToken(ra, false, null);
            String redirectUri = dataGenerate.buildImplicitRedirectUri(ra.redirectUri, at.accessToken, ra.state);
            return SaResult.ok().set("redirect_uri", redirectUri);
        }

        // 默认返回
        throw new SaOAuth2Exception("无效 response_type: " + ra.responseType).setCode(SaOAuth2ErrorCode.CODE_30125);
    }

}
