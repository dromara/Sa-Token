# Sa-Token SSO Server端 二次开发用到的所有函数说明 

本篇展示一下 SSO 模块常用的工具类、方法

## Sso-Server 工具类

### Ticket 操作
``` java
// 增删改

// 删除 Ticket
SaSsoServerUtil.deleteTicket(String ticket);

// 根据参数创建一个 ticket 码，并保存
SaSsoServerUtil.createTicketAndSave(String client, Object loginId, String tokenValue);

// 查

// 查询 ticket ，如果 ticket 无效则返回 null
SaSsoServerUtil.getTicket(String ticket);

// 查询 ticket 指向的 loginId，如果 ticket 码无效则返回 null
SaSsoServerUtil.getLoginId(String ticket);

// 查询 ticket 指向的 loginId，并转换为指定类型
SaSsoServerUtil.getLoginId(String ticket, Class<T> cs);

// 校验

// 校验 Ticket，无效 ticket 会抛出异常
SaSsoServerUtil.checkTicket(String ticket);

// 校验 Ticket 码，无效 ticket 会抛出异常，如果此ticket是有效的，则立即删除
SaSsoServerUtil.checkTicketParamAndDelete(String ticket);

// 校验 Ticket，无效 ticket 会抛出异常，如果此ticket是有效的，则立即删除
SaSsoServerUtil.checkTicketParamAndDelete(String ticket, String client);

// ticket 索引

// 查询 指定 client、loginId 其所属的 ticket 值
SaSsoServerUtil.getTicketValue(String client, Object loginId);
```



### Client 信息获取 
``` java
// 获取所有 Client
SaSsoServerUtil.getClients();

// 获取应用信息，无效 client 返回 null
SaSsoServerUtil.getClient(String client);

// 获取应用信息，无效 client 则抛出异常
SaSsoServerUtil.getClientNotNull(String client);

// 获取匿名 client 信息
SaSsoServerUtil.getAnonClient();

// 获取所有需要接收消息推送的 Client
SaSsoServerUtil.getNeedPushClients();
```


### 重定向 URL 构建与校验
``` java
// 构建 URL：sso-server 端向 sso-client 下放 ticket 的地址
SaSsoServerUtil.buildRedirectUrl(String client, String redirect, Object loginId, String tokenValue);

// 校验重定向 url 合法性
SaSsoServerUtil.checkRedirectUrl(String client, String url);
```


### 单点注销
``` java
// 指定账号单点注销
SaSsoServerUtil.ssoLogout(Object loginId);

// 指定账号单点注销
SaSsoServerUtil.ssoLogout(Object loginId, SaLogoutParameter logoutParameter, String ignoreClient);
```





### 消息推送 
``` java
// 向指定 Client 推送消息
SaSsoServerUtil.pushMessage(SaSsoClientModel clientModel, SaSsoMessage message);

// 向指定 client 推送消息，并将返回值转为 SaResult
SaSsoServerUtil.pushMessageAsSaResult(SaSsoClientModel clientModel, SaSsoMessage message);

// 向指定 Client 推送消息
SaSsoServerUtil.pushMessage(String client, SaSsoMessage message);

// 向指定 client 推送消息，并将返回值转为 SaResult
SaSsoServerUtil.pushMessageAsSaResult(String client, SaSsoMessage message);

// 向所有 Client 推送消息
SaSsoServerUtil.pushToAllClient(SaSsoMessage message);

// 向所有 Client 推送消息，并忽略掉某个 client
SaSsoServerUtil.pushToAllClient(SaSsoMessage message, String ignoreClient);
```


详情请参考源码：[码云：SaSsoServerUtil.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-plugin/sa-token-sso/src/main/java/cn/dev33/satoken/sso/template/SaSsoServerUtil.java)



## Sso-Client 工具类


### 构建交互地址 
``` java
// 构建URL：Server端 单点登录授权地址 
SaSsoClientUtil.buildServerAuthUrl(String clientLoginUrl, String back);
```


### 消息推送 

``` java
// 向 sso-server 推送消息
SaSsoClientUtil.pushMessage(SaSsoMessage message);

// 向 sso-server 推送消息，并将返回值转为 SaResult
SaSsoClientUtil.pushMessageAsSaResult(SaSsoMessage message);

// 构建消息：校验 ticket
SaSsoClientUtil.buildCheckTicketMessage(String ticket, String ssoLogoutCallUrl);

// 构建消息：单点注销
SaSsoClientUtil.buildSignoutMessage(Object loginId, SaLogoutParameter logoutParameter);
```

详情请参考源码：[码云：SaSsoClientUtil.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-plugin/sa-token-sso/src/main/java/cn/dev33/satoken/sso/template/SaSsoClientUtil.java)




## Sso-Server 所有可重写策略 

``` java
// 发送 Http 请求的处理函数
SaSsoServerProcessor.instance.ssoServerTemplate.strategy.sendRequest = url -> {
	// ...
}

// 使用异步模式执行一个任务
SaSsoServerProcessor.instance.ssoServerTemplate.strategy.asyncRun = fun -> {
	// ...
}

// 未登录时返回的 View
SaSsoServerProcessor.instance.ssoServerTemplate.strategy.notLoginView = () -> {
	// ...
}

// SSO-Server端：登录函数
SaSsoServerProcessor.instance.ssoServerTemplate.strategy.doLoginHandle = (name, pwd) -> {
	// ...
}

//SSO-Server端：在授权重定向之前的通知
SaSsoServerProcessor.instance.ssoServerTemplate.strategy.jumpToRedirectUrlNotice = (redirectUrl) -> {
	// ...
}

// SSO-Server端：在校验 ticket 后，给 sso-client 端追加返回信息的函数
SaSsoServerProcessor.instance.ssoServerTemplate.strategy.checkTicketAppendData = (loginId, result) -> {
	// ...
}
```



## Sso-Client 所有可重写策略

``` java
// 发送 Http 请求的处理函数
SaSsoClientProcessor.instance.ssoClientTemplate.strategy.sendRequest = url -> {
	// ...
}

// 自定义校验 ticket 返回值的处理逻辑 （每次从认证中心获取校验 ticket 的结果后调用）
SaSsoClientProcessor.instance.ssoClientTemplate.strategy.ticketResultHandle = (ctr, back) -> {
	// ...
}

// 转换：认证中心 centerId > 本地 loginId
SaSsoClientProcessor.instance.ssoClientTemplate.strategy.convertCenterIdToLoginId = (centerId) -> {
	// ...
}

// 转换：本地 loginId > 认证中心 centerId
SaSsoClientProcessor.instance.ssoClientTemplate.strategy.convertLoginIdToCenterId = (loginId) -> {
	// ...
}
```
