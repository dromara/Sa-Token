# Sa-Token-OAuth2 Server端 二次开发用到的所有函数说明 

官方示例只提供了基本的授权流程，以及 userinfo 资源的开放，如果您需要开放更多的接口，则二次开发时可能用到以下相关 API 方法 

--- 

### Client 信息相关

``` java
// 获取 ClientModel，根据 clientId
SaOAuth2Util.getClientModel(clientId);

// 校验 clientId 信息并返回 ClientModel，如果找不到对应 Client 信息则抛出异常
SaOAuth2Util.checkClientModel(clientId);

// 校验：clientId 与 clientSecret 是否正确
SaOAuth2Util.checkClientSecret(clientId, clientSecret);

// 校验：clientId 与 clientSecret 是否正确，并且是否签约了指定 scopes
SaOAuth2Util.checkClientSecretAndScope(clientId, clientSecret, scopes);

// 判断：该 Client 是否签约了指定的 Scope，返回 true 或 false
SaOAuth2Util.isContractScope(clientId, scopes);

// 校验：该 Client 是否签约了指定的 Scope，如果没有则抛出异常
SaOAuth2Util.checkContractScope(clientId, scopes);

// 校验：该 Client 是否签约了指定的 Scope，如果没有则抛出异常
SaOAuth2Util.checkContractScope(clientModel, scopes);

// 校验：该 Client 使用指定 url 作为回调地址，是否合法
SaOAuth2Util.checkRedirectUri(clientId, url);

// 判断：指定 loginId 是否对一个 Client 授权给了指定 Scope
SaOAuth2Util.isGrantScope(loginId, clientId, scopes);
```


### Access-Token 相关
``` java
// 获取 AccessTokenModel，无效的 AccessToken 会返回 null
SaOAuth2Util.getAccessToken(accessToken);

// 校验 Access-Token，成功返回 AccessTokenModel，失败则抛出异常
SaOAuth2Util.checkAccessToken(accessToken);

// 获取 Access-Token，根据索引： clientId、loginId
SaOAuth2Util.getAccessTokenValue(clientId, loginId);

// 判断：指定 Access-Token 是否具有指定 Scope 列表，返回 true 或 false
SaOAuth2Util.hasAccessTokenScope(accessToken, ...scopes);

// 校验：指定 Access-Token 是否具有指定 Scope 列表，如果不具备则抛出异常
SaOAuth2Util.checkAccessTokenScope(accessToken, ...scopes);

// 获取 Access-Token 所代表的LoginId
SaOAuth2Util.getLoginIdByAccessToken(accessToken);

// 获取 Access-Token 所代表的 clientId
SaOAuth2Util.getClientIdByAccessToken(accessToken);

// 回收 Access-Token
SaOAuth2Util.revokeAccessToken(accessToken);

// 回收 Access-Token，根据索引： clientId、loginId
SaOAuth2Util.revokeAccessTokenByIndex(clientId, loginId);
```


### Refresh-Token 相关
``` java
// 获取 RefreshTokenModel，无效的 RefreshToken 会返回 null
SaOAuth2Util.getRefreshToken(refreshToken);

// 校验 Refresh-Token，成功返回 RefreshTokenModel，失败则抛出异常
SaOAuth2Util.checkRefreshToken(refreshToken);

// 获取 Refresh-Token，根据索引： clientId、loginId
SaOAuth2Util.getRefreshTokenValue(clientId, Object loginId);

// 根据 RefreshToken 刷新出一个 AccessToken
SaOAuth2Util.refreshAccessToken(refreshToken);
```


### Client-Token 相关

``` java
// 获取 ClientTokenModel，无效的 ClientToken 会返回 null
SaOAuth2Util.getClientToken(clientToken);

// 校验 Client-Token，成功返回 ClientTokenModel，失败则抛出异常
SaOAuth2Util.checkClientToken(clientToken);

// 获取 ClientToken，根据索引： clientId
SaOAuth2Util.getClientTokenValue(clientId);

// 判断：指定 Client-Token 是否具有指定 Scope 列表，返回 true 或 false
SaOAuth2Util.hasClientTokenScope(clientToken, ...scopes);

// 校验：指定 Client-Token 是否具有指定 Scope 列表，如果不具备则抛出异常
SaOAuth2Util.checkClientTokenScope(clientToken, ...scopes);

// 回收 ClientToken
SaOAuth2Util.revokeClientToken(clientToken);

// 回收 ClientToken，根据索引： clientId
SaOAuth2Util.revokeClientTokenByIndex(clientId);

// 回收 Lower-ClientToken，根据索引： clientId
SaOAuth2Util.revokeLowerClientTokenByIndex(clientId);
```

--- 

详情请参考源码：[码云：SaOAuth2Util.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-plugin/sa-token-oauth2/src/main/java/cn/dev33/satoken/oauth2/template/SaOAuth2Util.java)


