package com.pj.test;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.template.SaOAuth2Util;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 测试 OAuth2 相关 token 增删查
 *
 * @author click33
 * @since 2024/8/25
 */
@RestController
@RequestMapping("/test")
public class Test2Controller {

    // 测试：查询全部 Access-Token   --- http://localhost:8000/test/getAccessTokenValueList?clientId=1001&loginId=10001
    @RequestMapping("/getAccessTokenValueList")
    public SaResult getAccessTokenValueList(String clientId, long loginId) {
        List<String> accessTokenValueList = SaOAuth2Util.getAccessTokenValueList(clientId, loginId);
        return SaResult.data(accessTokenValueList);
    }

    // 测试：查询全部 Access-Token, 带过期时间   --- http://localhost:8000/test/getAccessTokenIndexMap?clientId=1001&loginId=10001
    @RequestMapping("/getAccessTokenIndexMap")
    public SaResult getAccessTokenIndexMap(String clientId, long loginId) {
        Map<String, Long> accessTokenIndexMap = SaOAuth2Manager.getDao().getAccessTokenIndexMap_FromAdjustAfter(clientId, loginId);
        return SaResult.data(accessTokenIndexMap);
    }

    // 测试：回收指定 Access-Token   --- http://localhost:8000/test/revokeAccessToken?access_token=xxxxxxxxxx
    @RequestMapping("/revokeAccessToken")
    public SaResult revokeAccessToken(String access_token) {
        SaOAuth2Util.revokeAccessToken(access_token);
        return SaResult.ok();
    }

    // 测试：回收全部 Access-Token   --- http://localhost:8000/test/revokeAccessTokenByIndex?clientId=1001&loginId=10001
    @RequestMapping("/revokeAccessTokenByIndex")
    public SaResult revokeAccessTokenByIndex(String clientId, long loginId) {
        SaOAuth2Util.revokeAccessTokenByIndex(clientId, loginId);
        return SaResult.ok();
    }


    // 测试：查询全部 Refresh-Token   --- http://localhost:8000/test/getRefreshTokenValueList?clientId=1001&loginId=10001
    @RequestMapping("/getRefreshTokenValueList")
    public SaResult getRefreshTokenValueList(String clientId, long loginId) {
        List<String> refreshTokenValueList = SaOAuth2Util.getRefreshTokenValueList(clientId, loginId);
        return SaResult.data(refreshTokenValueList);
    }

    // 测试：查询全部 Refresh-Token, 带过期时间   --- http://localhost:8000/test/getRefreshTokenIndexMap?clientId=1001&loginId=10001
    @RequestMapping("/getRefreshTokenIndexMap")
    public SaResult getRefreshTokenIndexMap(String clientId, long loginId) {
        Map<String, Long> refreshTokenIndexMap = SaOAuth2Manager.getDao().getRefreshTokenIndexMap_FromAdjustAfter(clientId, loginId);
        return SaResult.data(refreshTokenIndexMap);
    }

    // 测试：回收指定 Refresh-Token   --- http://localhost:8000/test/revokeRefreshToken?refresh_token=xxxxxxxxxx
    @RequestMapping("/revokeRefreshToken")
    public SaResult revokeRefreshToken(String refresh_token) {
        SaOAuth2Util.revokeRefreshToken(refresh_token);
        return SaResult.ok();
    }

    // 测试：回收全部 Refresh-Token   --- http://localhost:8000/test/revokeRefreshTokenByIndex?clientId=1001&loginId=10001
    @RequestMapping("/revokeRefreshTokenByIndex")
    public SaResult revokeRefreshTokenByIndex(String clientId, long loginId) {
        SaOAuth2Util.revokeRefreshTokenByIndex(clientId, loginId);
        return SaResult.ok();
    }


    // 测试：查询全部 Client-Token   --- http://localhost:8000/test/getClientTokenValueList?clientId=1001
    @RequestMapping("/getClientTokenValueList")
    public SaResult getClientTokenValueList(String clientId) {
        List<String> clientTokenValueList = SaOAuth2Util.getClientTokenValueList(clientId);
        return SaResult.data(clientTokenValueList);
    }

    // 测试：查询全部 Client-Token, 带过期时间   --- http://localhost:8000/test/getClientTokenIndexMap?clientId=1001&loginId=10001
    @RequestMapping("/getClientTokenIndexMap")
    public SaResult getClientTokenIndexMap(String clientId, long loginId) {
        Map<String, Long> rlientTokenIndexMap = SaOAuth2Manager.getDao().getClientTokenIndexMap_FromAdjustAfter(clientId, loginId);
        return SaResult.data(rlientTokenIndexMap);
    }

    // 测试：回收指定 Client-Token   --- http://localhost:8000/test/revokeClientToken?client_token=xxxxxxxxxxx
    @RequestMapping("/revokeClientToken")
    public SaResult revokeClientToken(String client_token) {
        SaOAuth2Util.revokeClientToken(client_token);
        return SaResult.ok();
    }

    // 测试：回收全部 Client-Token   --- http://localhost:8000/test/revokeClientTokenByIndex?clientId=1001
    @RequestMapping("/revokeClientTokenByIndex")
    public SaResult revokeClientTokenByIndex(String clientId) {
        SaOAuth2Util.revokeClientTokenByIndex(clientId);
        return SaResult.ok();
    }

}