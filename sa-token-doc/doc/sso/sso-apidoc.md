# SSO-Server 认证中心开放接口

如果你的 SSO-Server 端和 SSO-Client 端都使用 Sa-Token-SSO 搭建，那么你可以直接跳过本章，开始 [SSO模式一 共享Cookie同步会话](/sso/sso-type1) 的学习。

如果你仅在 SSO-Server 端使用 Sa-Token-SSO 搭建，而 SSO-Client 端使用其它框架的话，那么你就需要手动调用 http 请求来对接 SSO-Server 认证中心，
下面的 API 列表将给你的对接步骤做一份参考。

--- 


### 1、单点登录授权地址
``` url
http://{host}:{port}/sso/auth
```

接收参数：

| 参数			| 是否必填	| 说明																|
| :--------		| :--------	| :--------															|
| redirect		| 是		| 登录成功后的重定向地址，一般填写 location.href（从哪来回哪去）							|
| mode			| 否		| 授权模式，取值 [simple, ticket]，simple=登录后直接重定向，ticket=带着ticket参数重定向，默认值为ticket			|

访问接口后有两种情况：
- 情况一：当前会话在 SSO 认证中心未登录，会进入登录页开始登录。
- 情况二：当前会话在 SSO 认证中心已登录，会被重定向至 `redirect` 地址，并携带 `ticket` 参数。


### 2、RestAPI 登录接口
``` url
http://{host}:{port}/sso/doLogin
```

接收参数：

| 参数			| 是否必填	| 说明													|
| :--------		| :--------	| :--------												|
| name			| 是		| 用户名  |
| pwd			| 是		| 密码	 |

此接口属于 RestAPI (使用ajax访问)，会进入后端配置的 `sso.setDoLoginHandle` 函数中，此函数的返回值即是此接口的响应值。

另外需要注意：此接口并非只能携带 name、pwd 参数，因为你可以在方法里通过 `SaHolder.getRequest().getParam("xxx")` 来获取前端提交的其它参数。 


### 3、Ticket 校验接口
此接口仅配置模式三 `(isHttp=true)` 时打开 

``` url
http://{host}:{port}/sso/checkTicket
```

接收参数：

| 参数			| 是否必填	| 说明													|
| :--------		| :--------	| :--------												|
| ticket		| 是		| 在步骤 1 中授权重定向时的 ticket 参数 						|
| ssoLogoutCall	| 否		| 单点注销时的回调通知地址，只在SSO模式三单点注销时需要携带此参数|

返回值场景：
- 校验成功时：

``` js
{
    "code": 200,
    "msg": "ok",
    "data": "10001"	// 此 ticket 指向的 loginId
}
```

- 校验失败时：

``` js
{
    "code": 500,
    "msg": "无效ticket：vESj0MtqrtSoucz4DDHJnsqU3u7AKFzbj0KH57EfJvuhkX1uAH23DuNrMYSjTnEq",
    "data": null
}
```


### 4、单点注销接口
``` url
http://{host}:{port}/sso/logout         
```

接受参数：

| 参数			| 是否必填	| 说明													|
| :--------		| :--------	| :--------												|
| loginId		| 否		| 要注销的账号id			 						|
| secretkey		| 否		| 接口通信秘钥									|
| back			| 否		| 注销成功后的重定向地址							|


此接口有两种调用方式

##### 方式一：在 Client 的前端页面引导用户直接跳转，并带有 back 参数 
例如：`http://{host}:{port}/sso/logout?back=xxx`，代表用户注销成功后返回back地址 

##### 方式二：在 Client 的后端通过 http 工具来调用
例如：`http://{host}:{port}/sso/logout?loginId={value}&secretkey={value}`，代表注销 账号=loginId 的账号，返回json数据结果，形如：

``` js
{
    "code": 200,    // 200表示请求成功，非200标识请求失败
    "msg": "单点注销成功",
    "data": null
}
```

如果单点注销失败，将返回：

``` js
{
    "code": 500,    // 200表示请求成功，非200标识请求失败
    "msg": "无效秘钥：xxx",	// 失败原因 
    "data": null
}
```


<br>

SSO 认证中心只有这四个接口，接下来让我一起来看一下 Client 端的对接流程：[SSO模式一 共享Cookie同步会话](/sso/sso-type1) 











