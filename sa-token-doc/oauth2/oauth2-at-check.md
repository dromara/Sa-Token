# Sa-Token OAuth2 模块相关注解

sa-token-oauth2 模块扩展了三个注解用于相关数据校验：
- `@SaCheckAccessToken`：指定请求中必须包含有效的 `access_token` ，并且包含指定的 `scope`。
- `@SaCheckClientToken`：指定请求中必须包含有效的 `client_token` ，并且包含指定的 `scope`。
- `@SaCheckClientIdSecret`：指定请求中必须包含有效的 `client_id` 和 `client_secret` 信息。

和 Sa-Token-Code 模块的注解一样，你必须先注册框架的内置拦截器，才可以使用这些注解，详细参考：[注解鉴权](/use/at-check) 。

--- 


### 1、@SaCheckAccessToken 示例

``` java
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

}
```


### 2、@SaCheckClientToken 示例

``` java
@RestController
@RequestMapping("/test")
public class TestController {

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

}
```


### 3、@SaCheckClientIdSecret 示例
``` java
@RestController
@RequestMapping("/test")
public class TestController {

    // 测试：携带有效的 client_id 和 client_secret 信息，才可以进入请求
    // 你可以在请求参数中携带 client_id 和 client_secret 参数，或者从请求头以 Authorization: Basic base64(client_id:client_secret) 的形式携带
    @SaCheckClientIdSecret
    @RequestMapping("/checkClientIdSecret")
    public SaResult checkClientIdSecret() {
        return SaResult.ok("访问成功");
    }

}
```
