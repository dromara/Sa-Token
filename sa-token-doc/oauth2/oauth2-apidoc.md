# Sa-Token-OAuth2 Server端 API列表
基于官方仓库的搭建示例，`OAuth2-Server`端会暴露出以下API，`OAuth2-Client`端可据此文档进行对接  

--- 

## 1、模式一：授权码（Authorization Code）

### 1.1、获取授权码

根据以下格式构建URL，引导用户访问 （复制时请注意删减掉相应空格和换行符）
``` url
http://{host}:{port}/oauth2/authorize
	?response_type=code
	&client_id={client_id}
	&redirect_uri={redirect_uri}
	&scope={scope}
	&state={state}
```

参数详解：

| 参数			| 是否必填	| 说明													|
| :--------		| :--------	| :--------												|
| response_type	| 是		| 返回类型，这里请填写：`code`							|
| client_id		| 是		| 应用 id												|
| redirect_uri	| 是		| 用户确认授权后，重定向的 url 地址							|
| scope			| 否		| 具体请求的权限，多个用逗号(或空格)隔开						|
| state			| 否		| 随机值，此参数会在重定向时追加到url末尾，不填不追加，如果填写则每次填写的值不可以重复	|

注意点：
1. 如果用户在 `OAuth-Server` 端尚未登录：会被转发到登录视图，你可以参照文档或官方示例自定义登录页面。
2. 如果 `scope` 参数为空，或者请求的 `scope` 用户近期已确认授权过，则无需用户再次确认，达到静默授权的效果，否则需要用户手动确认，服务器才可以下放 `code` 授权码。

用户确认授权之后，会被重定向至`redirect_uri`，并追加 `code` 参数与 `state` 参数，形如：
``` url
redirect_uri?code={code}&state={state}
```

`Code` 授权码具有以下特点：
1. 每次授权产生的 `Code` 码都不一样。
2. `Code` 码用完即废，不能二次使用。
3. 一个 `Code` 的有效期默认为五分钟，超时自动作废。
4. 每次授权产生新 `Code` 码，会导致旧 `Code` 码立即作废，即使旧 `Code` 码尚未使用。


<details>
<summary>RestAPI 登录接口：/oauth2/doLogin</summary>

如果用户在 OAuth-Server 端尚未登录，则会被阻塞在登录界面，开始登录，需要在页面上调用`/oauth2/doLogin`完成登录（此接口非 OAuth2 标准协议接口）

``` url
http://{host}:{port}/oauth2/doLogin
	?name={name}
	&pwd={pwd}
```
参数详解：

| 参数			| 是否必填	| 说明													|
| :--------		| :--------	| :--------												|
| name			| 否		| 账号													|
| pwd			| 否		| 密码													|

访问此接口将进入自定义的 `cfg.doLoginHandle` 函数开始登录，你只要在此函数内调用 `StpUtil.login(xxx)` 即代表登录成功。

另外需要注意：此接口并非只能携带 `name`、`pwd` 参数，因为你可以在方法里通过 `SaHolder.getRequest().getParam("xxx")` 来获取前端提交的其它参数。

</details>


<details>
<summary>RestAPI 确认授权接口：/oauth2/doConfirm</summary>

如果 oauth-client 端申请的 scope 在 OAuth-Server 端需要用户手动确认授权，则会被阻塞在授权界面，
需要在页面上调用`/oauth2/doConfirm`完成授权（此接口非 OAuth2 标准协议接口）

``` url
http://{host}:{port}/oauth2/doConfirm
    ?client_id={value}
    &scope={value}
    &build_redirect_uri={true|false}
    &response_type={value}
    &redirect_uri={value}
    &state={value}
```
参数详解：

| 参数					| 是否必填	| 说明													|
| :--------				| :--------	| :--------												|
| client_id				| 是		| 应用 id												|
| scope					| 是		| 具体确认的权限，多个用逗号(或空格)隔开					|
| build_redirect_uri	| 否		| 是否立即构建 `redirect_uri` 授权地址，取值：true | false		|
| response_type			| 否		| 取 url 上的 `response_type` 参数来提交					|
| redirect_uri			| 否		| 取 url 上的 `redirect_uri` 参数来提交					|
| state					| 否		| 取 url 上的 `state` 参数来提交							|

此接口有两种调用方式，一种只提供 `client_id`、`scope` 两个参数，此时返回结果代表是否确认授权成功：
``` js
{
    code: 200, 
    msg: 'ok', 
    data: null,
}
```

一种是指定 `build_redirect_uri: true`，并同时提供 `client_id`、`scope`、`response_type`、`redirect_uri`、`state` 全部参数，
此时返回结果包括最终的 code 授权地址：
``` js
{
    code: 200, 
    msg: 'ok', 
    data: null,
	redirect_uri: 'http://sa-oauth-client.com:8002/?code=n12TTc1M9REfJVqKm0wewDz0tNZDBhE1A90irOJmxD0zb92pdhUK8NghJfuC'
}
```

前端在 ajax 回调函数中直接使用 `location.href=res.redirect_uri` 跳转即可，无需再重复访问 `/oauth2/authorize` 接口。

</details>



### 1.2、根据授权码获取 Access-Token
获得 `Code` 码后，我们可以通过以下接口，获取到用户的 `Access-Token`、`Refresh-Token` 等信息。

``` url
http://{host}:{port}/oauth2/token
	?grant_type=authorization_code
	&client_id={client_id}
	&client_secret={client_secret}
	&code={code}
```

参数详解：

| 参数			| 是否必填	| 说明													|
| :--------		| :--------	| :--------												|
| grant_type	| 是		| 授权类型，这里请填写：`authorization_code`				|
| client_id		| 是		| 应用 id												|
| client_secret	| 是		| 应用秘钥												|
| code			| 是		| 步骤 1.1 中获取到的授权码								|

也可以通过 `Basic Authorization` 方式提交 `client` 信息，格式为在请求 `header` 头添加 `Authorization` 参数：
``` js
header['Authorization'] = base64(`${client_id}:${client_secret}`);
```

接口返回示例：

``` js
{
    "code": 200,    // 200表示请求成功，非200标识请求失败, 以下不再赘述 
    "msg": "ok",
    "data": null,
    "token_type": "bearer",
    "access_token": "Gly7mnnXSdCxkOqmOwcA5SbG6ZtPmJVX7ZgSn1pidhRmnenBEgxbWJS8VWxA",     // Access-Token值
    "refresh_token": "EuYNwpxdc18MpaZLPyhFeyAyzr2IOWEr4q3QUGgPWqdJujQqvohjQEDJpwOm",    // Refresh-Token值
    "expires_in": 7199,                  // Access-Token剩余有效期，单位秒  
    "refresh_expires_in": 2591999,       // Refresh-Token剩余有效期，单位秒  
    "client_id": "1001",                 // 应用 id
    "scope": "userinfo"                  // 此令牌包含的权限
}
```


### 1.3、根据 Refresh-Token 刷新 Access-Token （如果需要的话）
Access-Token的有效期较短，如果每次过期都需要重新授权的话，会比较影响用户体验，因此我们可以在后台通过`Refresh-Token` 刷新 `Access-Token`

``` url
http://{host}:{port}/oauth2/refresh
	?grant_type=refresh_token
	&client_id={client_id}
	&client_secret={client_secret}
	&refresh_token={refresh_token}
```

参数详解：

| 参数			| 是否必填	| 说明													|
| :--------		| :--------	| :--------												|
| grant_type	| 是		| 授权类型，这里请填写：`refresh_token`				|
| client_id		| 是		| 应用 id												|
| client_secret	| 是		| 应用秘钥												|
| refresh_token | 是		| 步骤1.2中获取到的 `Refresh-Token` 值							|

接口返回值同章节1.2，此处不再赘述 


### 1.4、回收 Access-Token （如果需要的话）
在A ccess-Token 过期之前主动将其回收 

``` url
http://{host}:{port}/oauth2/revoke
	?client_id={client_id}
	&client_secret={client_secret}
	&access_token={access_token}
```

参数详解：

| 参数			| 是否必填	| 说明													|
| :--------		| :--------	| :--------												|
| client_id		| 是		| 应用 id												|
| client_secret	| 是		| 应用秘钥												|
| access_token  | 是		| 步骤1.2中获取到的`Access-Token`值						|

返回值样例：
``` js
{
    "code": 200,
    "msg": "ok",
    "data": null
}
```


### 1.5、根据 Access-Token 获取相应用户的账号信息  
注：此接口非 OAuth2 标准协议接口，为官方仓库 demo 模拟接口，正式项目中大家可以根据此样例，自定义需要的接口及参数 

``` url
http://{host}:{port}/oauth2/userinfo?access_token={access_token}
```

返回值样例：
``` js
{
    "code": 200,
    "msg": "ok",
	"nickname": "shengzhang_",         // 账号昵称
	"avatar": "http://xxx.com/1.jpg",  // 头像地址
	"age": "18",                       // 年龄
	"sex": "男",                       // 性别
	"address": "山东省 青岛市 城阳区"   // 所在城市 
}
```

除了直接在 url 中以 query 参数方式提交 `access_token`，你也可以在 `Authorization` 请求头以 `Bearer Token` 方式提交：
``` js
header['Authorization'] = 'Bearer access_token';
```


## 2、模式二：隐藏式（Implicit）

根据以下格式构建URL，引导用户访问：
``` url
http://{host}:{port}/oauth2/authorize
	?response_type=token
	&client_id={client_id}
	&redirect_uri={redirect_uri}
	&scope={scope}
	&state={state}
```

参数详解：

| 参数			| 是否必填	| 说明													|
| :--------		| :--------	| :--------												|
| response_type	| 是		| 返回类型，这里请填写：`token`							|
| client_id		| 是		| 应用 id												|
| redirect_uri	| 是		| 用户确认授权后，重定向的url地址							|
| scope			| 否		| 具体请求的权限，多个用逗号(或空格)隔开							|
| state			| 否		| 随机值，此参数会在重定向时追加到url末尾，不填不追加，如果填写则每次填写的值不可以重复			|

此模式会越过授权码的步骤，直接返回 `Access-Token` 到前端页面，形如：
``` url
redirect_uri#token=xxxx-xxxx-xxxx-xxxx
```
注意 token 是以 `#` 锚参数的形式拼接到 url 上的。


## 3、模式三：密码式（Password）
首先在Client端构建表单，让用户输入 Server 端的账号和密码，然后在 Client 端访问接口 
``` url
http://{host}:{port}/oauth2/token
	?grant_type=password
	&client_id={client_id}
	&client_secret={client_secret}
	&username={username}
	&password={password}
	&scope={scope}
```

参数详解：

| 参数			| 是否必填	| 说明							|
| :--------		| :--------	| :--------						|
| grant_type	| 是		| 返回类型，这里请填写：`password`|
| client_id		| 是		| 应用 id						|
| client_secret	| 是		| 应用秘钥												|
| username		| 是		| 用户的 `OAuth2-Server` 端账号			|
| password		| 是		| 用户的 `OAuth2-Server` 端密码			|
| scope			| 否		| 具体请求的权限，多个用逗号(或空格)隔开						|

接口返回示例：

``` js
{
    "code": 200,	// 200表示请求成功，非200标识请求失败, 以下不再赘述 
    "msg": "ok",
	"access_token": "7Ngo1Igg6rieWwAmWMe4cxT7j8o46mjyuabuwLETuAoN6JpPzPO2i3PVpEVJ",     // Access-Token 值
	"refresh_token": "ZMG7QbuCVtCIn1FAJuDbgEjsoXt5Kqzii9zsPeyahAmoir893ARA4rbmeR66",    // Refresh-Token 值
	"expires_in": 7199,                 // Access-Token 剩余有效期，单位秒  
	"refresh_expires_in": 2591999,      // Refresh-Token 剩余有效期，单位秒  
	"client_id": "1001",                // 应用 id
	"scope": "",                        // 此令牌包含的权限
}
```

> [!WARNING| label:重写认证处理器] 
> 在正式项目中，password 认证模式需要重写 `PasswordGrantTypeHandler` 处理器，在后面的 [自定义 grant_type](/oauth2/oauth2-custom-grant_type) 章节我们会详细介绍


## 4、模式四：凭证式（Client Credentials）
以上三种模式获取的都是用户的 `Access-Token`，代表用户对第三方应用的授权，
在OAuth2.0中还有一种针对 Client级别的授权， 即：`Client-Token`，代表应用自身的资源授权

在 Client 端的后台访问以下接口：

``` url
http://{host}:{port}/oauth2/client_token
	?grant_type=client_credentials
	&client_id={client_id}
	&client_secret={client_secret}
	&scope={scope}
```

参数详解：

| 参数			| 是否必填	| 说明									|
| :--------		| :--------	| :--------								|
| grant_type	| 是		| 返回类型，这里请填写：`client_credentials`|
| client_id		| 是		| 应用 id								|
| client_secret	| 是		| 应用秘钥								|
| scope			| 否		| 具体请求的权限，多个用逗号(或空格)隔开	|

接口返回值样例：
``` js
{
    "code": 200,
    "msg": "ok",
	"client_token": "HmzPtaNuIqGrOdudWLzKJRSfPadN497qEJtanYwE7ZvHQWDy0jeoZJuDIiqO",	// Client-Token 值
	"expires_in": 7199,     // Token剩余有效时间，单位秒 
	"client_id": "1001",    // 应用 id
	"scope": null           // 包含权限 
}
```

注：`Client-Token`具有延迟作废特性，即：在每次获取最新`Client-Token`的时候，旧`Client-Token`不会立即过期，而是作为`Lower-Client-Token`再次储存起来，
资源请求方只要携带其中之一便可通过Token校验，这种特性保证了在大量并发请求时不会出现“新旧Token交替造成的授权失效”， 保证了服务的高可用。

