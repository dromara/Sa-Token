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

// 删除：指定 loginId 针对指定 Client 的授权信息
SaOAuth2Util.deleteGrantScope(loginId, clientId);
```


### Code 相关
``` java
// 获取 CodeModel，无效的 code 会返回 null
SaOAuth2Util.getCode(code);

// 校验 Code，成功返回 CodeModel，失败则抛出异常
SaOAuth2Util.checkCode(code);

// 获取 Code，根据索引： clientId、loginId
SaOAuth2Util.getCodeValue(clientId, loginId);
```


### Access-Token 相关
``` java
// 获取 AccessTokenModel，无效的 AccessToken 会返回 null
SaOAuth2Util.getAccessToken(accessToken);

// 校验 Access-Token，成功返回 AccessTokenModel，失败则抛出异常
SaOAuth2Util.checkAccessToken(accessToken);

// 获取 Access-Token 列表：此应用下 对 某个用户 签发的所有 Access-token
SaOAuth2Util.getAccessTokenValueList(clientId, loginId);

// 判断：指定 Access-Token 是否具有指定 Scope 列表，返回 true 或 false
SaOAuth2Util.hasAccessTokenScope(accessToken, ...scopes);

// 校验：指定 Access-Token 是否具有指定 Scope 列表，如果不具备则抛出异常
SaOAuth2Util.checkAccessTokenScope(accessToken, ...scopes);

// 获取 Access-Token 所代表的LoginId
SaOAuth2Util.getLoginIdByAccessToken(accessToken);

// 获取 Access-Token 所代表的 clientId
SaOAuth2Util.getClientIdByAccessToken(accessToken);

// 回收一个 Access-Token
SaOAuth2Util.revokeAccessToken(accessToken);

// 回收全部 Access-Token：指定应用下 指定用户 的全部 Access-Token
SaOAuth2Util.revokeAccessTokenByIndex(clientId, loginId);
```


### Refresh-Token 相关
``` java
// 获取 RefreshTokenModel，无效的 RefreshToken 会返回 null
SaOAuth2Util.getRefreshToken(refreshToken);

// 校验 Refresh-Token，成功返回 RefreshTokenModel，失败则抛出异常
SaOAuth2Util.checkRefreshToken(refreshToken);

// 获取 Refresh-Token 列表：此应用下 对 某个用户 签发的所有 Refresh-Token
SaOAuth2Util.getRefreshTokenValueList(clientId, loginId);

// 回收一个 Refresh-Token
SaOAuth2Util.revokeRefreshToken(refreshToken);

// 回收全部 Refresh-Token：指定应用下 指定用户 的全部 Refresh-Token
SaOAuth2Util.revokeRefreshTokenByIndex(clientId, loginId);

// 根据 RefreshToken 刷新出一个 AccessToken
SaOAuth2Util.refreshAccessToken(refreshToken);
```


### Client-Token 相关

``` java
// 获取 ClientTokenModel，无效的 ClientToken 会返回 null
SaOAuth2Util.getClientToken(clientToken);

// 校验 Client-Token，成功返回 ClientTokenModel，失败则抛出异常
SaOAuth2Util.checkClientToken(clientToken);

// 获取 Client-Token 列表：此应用下 对 某个用户 签发的所有 Client-token
SaOAuth2Util.getClientTokenValueList(clientId);

// 判断：指定 Client-Token 是否具有指定 Scope 列表，返回 true 或 false
SaOAuth2Util.hasClientTokenScope(clientToken, ...scopes);

// 校验：指定 Client-Token 是否具有指定 Scope 列表，如果不具备则抛出异常
SaOAuth2Util.checkClientTokenScope(clientToken, ...scopes);

// 回收一个 ClientToken
SaOAuth2Util.revokeClientToken(clientToken);

// 回收全部 Client-Token：指定应用下的全部 Client-Token
SaOAuth2Util.revokeClientTokenByIndex(clientId);
```


### 请求查询

``` java
// 数据读取：从当前请求对象中读取 access_token，并查询到 AccessTokenModel 信息，无效 access_token 抛出异常
// 1、请求参数 access_token，2、请求头 Authorization Bearer access_token
SaOAuth2Util.currentAccessToken();

// 数据读取：从当前请求对象中读取 client_token，并查询到 ClientTokenModel 信息，无效 client_token 抛出异常
// 1、请求参数 client_token，2、请求头 Authorization Bearer client_token
SaOAuth2Util.currentClientToken();
```



详情请参考源码：[码云：SaOAuth2Util.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-plugin/sa-token-oauth2/src/main/java/cn/dev33/satoken/oauth2/template/SaOAuth2Util.java)


### OAuth2-Server 所有可重写策略


#### 权限处理器
``` java
// 根据 scope 信息对一个 AccessTokenModel 进行加工处理
SaOAuth2Strategy.instance.workAccessTokenByScope = at -> {
	// ... 
}

// 当使用 RefreshToken 刷新 AccessToken 时，根据 scope 信息对一个 AccessTokenModel 进行加工处理
SaOAuth2Strategy.instance.refreshAccessTokenWorkByScope = at -> {
	// ... 
}

// 根据 scope 信息对一个 ClientTokenModel 进行加工处理
SaOAuth2Strategy.instance.workClientTokenByScope = at -> {
	// ... 
}
```


#### grant_type 处理器
``` java
// 根据 grantType 构造一个 AccessTokenModel
SaOAuth2Strategy.instance.grantTypeAuth = req -> {
	// ... 
}
```


#### 凭证创建
``` java
// 创建一个 code value
SaOAuth2Strategy.instance.createCodeValue = (clientId, loginId, scopes) -> {
	// ... 
}

// 创建一个 AccessToken value
SaOAuth2Strategy.instance.createAccessToken = (clientId, loginId, scopes) -> {
	// ... 
}

// 创建一个 RefreshToken value
SaOAuth2Strategy.instance.createRefreshToken = (clientId, loginId, scopes) -> {
	// ... 
}

// 创建一个 ClientToken value
SaOAuth2Strategy.instance.createClientToken = (clientId, scopes) -> {
	// ... 
}
```


#### 认证流程回调
``` java
// OAuth-Server端：未登录时返回的View
SaOAuth2Strategy.instance.notLoginView = () -> {
	// ... 
}

// OAuth-Server端：确认授权时返回的View
SaOAuth2Strategy.instance.confirmView = (clientId, scopes) -> {
	// ... 
}

// OAuth-Server端：登录函数
SaOAuth2Strategy.instance.doLoginHandle = (name, pwd) -> {
	// ... 
}

// OAuth-Server端：用户在授权指定 client 前的检查，如果检查不通过，请直接抛出异常
SaOAuth2Strategy.instance.userAuthorizeClientCheck = (loginId, clientId) -> {
	// ... 
}
```


#### 其它
``` java
// 在创建 SaClientModel 时，设置其默认字段
SaOAuth2Strategy.instance.setSaClientModelDefaultFields = (clientModel) -> {
	// ... 
}
```

