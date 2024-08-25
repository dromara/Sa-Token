package com.pj.test;

import cn.dev33.satoken.oauth2.annotation.SaCheckAccessToken;
import cn.dev33.satoken.oauth2.annotation.SaCheckClientIdSecret;
import cn.dev33.satoken.oauth2.annotation.SaCheckClientToken;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuth2 相关注解测试 Controller
 *
 * @author click33
 * @since 2024/8/25
 */
@RestController
@RequestMapping("/test")
public class TestController {

    // 测试：携带有效的 access_token 才可以进入请求
    // 你可以在请求参数中携带 access_token 参数，或者从请求头以 Authorization: bearer xxx 的形式携带
    @SaCheckAccessToken
    @RequestMapping("/checkAccessToken")
    public SaResult checkAccessToken() {
        return SaResult.ok("访问成功");
    }

    // 测试：携带有效的 access_token ，并且具备指定 scope 才可以进入请求
    @SaCheckAccessToken(scope = "userinfo")
    @RequestMapping("/checkAccessTokenScope")
    public SaResult checkAccessTokenScope() {
        return SaResult.ok("访问成功");
    }

    // 测试：携带有效的 access_token ，并且具备指定 scope 列表才可以进入请求
    @SaCheckAccessToken(scope = {"openid", "userinfo"})
    @RequestMapping("/checkAccessTokenScopeList")
    public SaResult checkAccessTokenScopeList() {
        return SaResult.ok("访问成功");
    }

    // 测试：携带有效的 client_token 才可以进入请求
    // 你可以在请求参数中携带 client_token 参数，或者从请求头以 Authorization: bearer xxx 的形式携带
    @SaCheckClientToken
    @RequestMapping("/checkClientToken")
    public SaResult checkClientToken() {
        return SaResult.ok("访问成功");
    }

    // 测试：携带有效的 client_token ，并且具备指定 scope 才可以进入请求
    @SaCheckClientToken(scope = "userinfo")
    @RequestMapping("/checkClientTokenScope")
    public SaResult checkClientTokenScope() {
        return SaResult.ok("访问成功");
    }

    // 测试：携带有效的 client_token ，并且具备指定 scope 列表才可以进入请求
    @SaCheckClientToken(scope = {"openid", "userinfo"})
    @RequestMapping("/checkClientTokenScopeList")
    public SaResult checkClientTokenScopeList() {
        return SaResult.ok("访问成功");
    }

    // 测试：携带有效的 client_id 和 client_secret 信息，才可以进入请求
    // 你可以在请求参数中携带 client_id 和 client_secret 参数，或者从请求头以 Authorization: Basic base64(client_id:client_secret) 的形式携带
    @SaCheckClientIdSecret
    @RequestMapping("/checkClientIdSecret")
    public SaResult checkClientIdSecret() {
        return SaResult.ok("访问成功");
    }

}