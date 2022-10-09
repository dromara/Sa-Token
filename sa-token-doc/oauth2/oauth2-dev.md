# Sa-Token-OAuth2 Server端 二次开发用到的所有函数说明 

官方示例只提供了基本的授权流程，以及userinfo资源的开放，如果您需要开放更多的接口，则二次开发时用到以下相关API方法 

--- 

## Sa-OAuth2 模块常用方法

``` java
// 根据 id 获取 Client 信息, 如果 Client 为空，则抛出异常 
SaOAuth2Util.checkClientModel(clientId);

// 获取 Access-Token，如果Access-Token为空则抛出异常 
SaOAuth2Util.checkAccessToken(accessToken);

// 获取 Client-Token，如果Client-Token为空则抛出异常
SaOAuth2Util.checkClientToken(clientToken);

// 获取 Access-Token 所代表的LoginId
SaOAuth2Util.getLoginIdByAccessToken(accessToken);

// 校验：指定 Access-Token 是否具有指定 Scope
SaOAuth2Util.checkScope(accessToken, scopes);

// 根据 code码 生成 Access-Token 
SaOAuth2Util.generateAccessToken(code);

// 根据 Refresh-Token 生成一个新的 Access-Token
SaOAuth2Util.refreshAccessToken(refreshToken);

// 构建 Client-Token 
SaOAuth2Util.generateClientToken(clientId, scope);

// 校验 Client-Token 是否含有指定 Scope 
SaOAuth2Util.checkClientTokenScope(clientToken, scopes);

// 回收 Access-Token 
SaOAuth2Util.revokeAccessToken(accessToken);

// 持久化：用户授权记录 
SaOAuth2Util.saveGrantScope(clientId, loginId, scope);

// 获取：Refresh-Token Model
SaOAuth2Util.getRefreshToken(refreshToken);
```

详情请参考源码：[码云：SaOAuth2Util.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-plugin/sa-token-oauth2/src/main/java/cn/dev33/satoken/oauth2/logic/SaOAuth2Util.java)


