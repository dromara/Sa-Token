# SSO-Server 认证中心开放接口

如果你的 SSO-Server 端和 SSO-Client 端都使用 Sa-Token-SSO 搭建，那么你可以直接跳过本章，开始 [SSO模式一 共享Cookie同步会话](/sso/sso-type1) 的学习。

如果你仅在 SSO-Server 端使用 Sa-Token-SSO 搭建，而 SSO-Client 端使用其它框架的话，那么你就需要手动调用 http 请求来对接 SSO-Server 认证中心，
下面的 API 列表将给你的对接步骤做一份参考。

--- 


## 一、SSO-Server 认证中心接口 


### 1、单点登录授权地址
``` url
http://{host}:{port}/sso/auth
```

接收参数：

| 参数			| 是否必填	| 说明																|
| :--------		| :--------	| :--------															|
| redirect		| 是		| 登录成功后的重定向地址，一般填写 location.href（从哪来回哪去）							|
| mode			| 否		| 授权模式，取值 [simple, ticket]，simple=登录后直接重定向，ticket=带着ticket参数重定向，默认值为ticket			|
| client		| 否		| 客户端标识，可不填，代表是一个匿名应用，若填写了，则校验 ticket 时也必须是这个 client 才可以校验成功			|

访问接口后有两种情况：
- 情况一：当前会话在 SSO 认证中心未登录，会进入登录页开始登录。
- 情况二：当前会话在 SSO 认证中心已登录，会被重定向至 `redirect` 地址，并携带 `ticket` 参数。

`ticket` 码具有以下特点：
1. 每次授权产生的 `ticket` 码都不一样。
2. `ticket` 码用完即废，不能二次使用。
3. 一个 `ticket` 的有效期默认为五分钟，超时自动作废。
4. 每次授权产生新 `ticket` 码，会导致旧 `ticket` 码立即作废，即使旧 `ticket` 码尚未使用。

### 2、RestAPI 登录接口
``` url
http://{host}:{port}/sso/doLogin
```

接收参数：

| 参数			| 是否必填	| 说明													|
| :--------		| :--------	| :--------												|
| name			| 是		| 用户名  |
| pwd			| 是		| 密码	 |

此接口属于 RestAPI (使用ajax访问)，会进入后端配置的 `ssoServer.doLoginHandle` 函数中，此函数的返回值即是此接口的响应值。

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
| client		| 否		| 客户端标识，可不填，代表是一个匿名应用，若填写了，则必须填写的和 `/sso/auth` 登录时填写的一致才可以校验成功			|
| timestamp		| 是		| 当前时间戳，13位									|
| nonce			| 是		| 随机字符串										|
| sign			| 是		| 签名，生成算法：`md5( [client={client值}&]nonce={随机字符串}&[ssoLogoutCall={单点注销回调地址}&]ticket={ticket值}&timestamp={13位时间戳}&key={secretkey秘钥} )`	 注：[]内容代表可选 				|

返回值场景：
- 校验成功时：

``` js
{
    "code": 200,
    "msg": "ok",
    "data": "10001",	// 此 ticket 指向的 loginId
	"remainSessionTimeout": 7200, // 此账号在 sso-server 端的会话剩余有效期（单位：s）
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
http://{host}:{port}/sso/signout
```

此接口有两种调用方式

##### 4.1、方式一：在 Client 的前端页面引导用户直接跳转，并带有 back 参数 
例如：

``` url
http://{host}:{port}/sso/signout?back=xxx
```
用户注销成功后将返回 back 地址 

##### 4.2、方式二：在 Client 的后端通过 http 工具来调用

接受参数：

| 参数			| 是否必填	| 说明											|
| :--------		| :--------	| :--------										|
| loginId		| 是		| 要注销的账号 id			 					|
| timestamp		| 是		| 当前时间戳，13位									|
| nonce			| 是		| 随机字符串										|
| sign			| 是		| 签名，生成算法：`md5( loginId={账号id}&nonce={随机字符串}&timestamp={13位时间戳}&key={secretkey秘钥} )`							|
| client		| 否		| 客户端标识，可不填，一般在帮助 “sso-server 端不同client不同秘钥” 的场景下找到对应秘钥时，才填写		|

例如：
``` url
http://{host}:{port}/sso/signout?loginId={value}&timestamp={value}&nonce={value}&sign={value}
```

将返回 json 数据结果，形如：

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
    "msg": "签名无效：xxx",	// 失败原因 
    "data": null
}
```


<br>

SSO 认证中心只有这四个接口，接下来让我一起来看一下 Client 端的对接流程：[SSO模式一 共享Cookie同步会话](/sso/sso-type1) 



---

## 二、SSO-Client 应用端开放接口 

### 1、登录地址
``` url
http://{host}:{port}/sso/login
```

接收参数：

| 参数			| 是否必填	| 说明																|
| :--------		| :--------	| :--------															|
| back			| 是		| 登录成功后的重定向地址，一般填写 location.href（从哪来回哪去）			|
| ticket		| 否		| 授权 ticket 码			|

此接口有两种访问方式：
- 方式一：我们需要登录操作，所以带着 back 参数主动访问此接口，框架会拼接好参数后再次将用户重定向至认证中心。
- 方式二：用户在认证中心登录成功后，带着 ticket 参数重定向而来，此为框架自动处理的逻辑，开发者无需关心。


### 2、注销地址
``` url
http://{host}:{port}/sso/logout
```

接收参数：

| 参数			| 是否必填	| 说明																|
| :--------		| :--------	| :--------															|
| back			| 否		| 注销成功后的重定向地址，一般填写 location.href（从哪来回哪去），也可以填写 self 字符串，含义同上			|

此接口有两种访问方式：
- 方式一：直接 `location.href` 网页跳转，此时可携带 back 参数。
- 方式二：使用 Ajax 异步调用（此方式不可携带 back 参数，但是需要提交会话 Token ），注销成功将返回以下内容：

``` js
{
    "code": 200,    // 200表示请求成功，非200标识请求失败
    "msg": "单点注销成功",
    "data": null
}
```


### 3、单点注销回调接口
此接口仅配置模式三 `(isHttp=true)` 时打开，且为框架回调，开发者无需关心

``` url
http://{host}:{port}/sso/logoutCall
```

接受参数：

| 参数			| 是否必填	| 说明											|
| :--------		| :--------	| :--------										|
| loginId		| 是		| 要注销的账号 id			 					|
| timestamp		| 是		| 当前时间戳，13位								|
| nonce			| 是		| 随机字符串										|
| sign			| 是		| 签名，生成算法：`md5( loginId={账号id}&nonce={随机字符串}&timestamp={13位时间戳}&key={secretkey秘钥} )` |
| client		| 否		| 客户端标识，如果你在登录时向 sso-server 端传递了 client 值，那么在此处 sso-server 也会给你回传过来，否则此参数无值。如果此参数有值，则此参数也要参与签名，放在 loginId 参数前面（字典顺序）		|
| autoLogout	| 否		| 是否为“登录client超过最大数量”引起的自动注销（true=超限系统自动注销，false=用户主动发起注销）。如果此参数有值，则此参数也要参与签名，放在 client 参数前面（字典顺序）		|


返回数据：

``` js
{
    "code": 200,    // 200表示请求成功，非200标识请求失败
    "msg": "单点注销回调成功",
    "data": null
}
```














