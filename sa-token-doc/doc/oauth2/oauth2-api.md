# Sa-Token-OAuth2 Server端 API列表
基于官方仓库的搭建示例，`OAuth2-Server`端会暴露出以下API，`OAuth2-Client`端可据此文档进行对接  

--- 

## 1、模式一：授权码（Authorization Code）

### 1.1、获取授权码

根据以下格式构建URL，引导用户访问 （复制时请注意删减掉相应空格和换行符）
``` url
http://sa-oauth-server.com:8001/oauth2/authorize
	?response_type=code
	&client_id={value}
	&redirect_uri={value}
	&scope={value}
	$state={value}
```

参数详解：

| 参数			| 是否必填	| 说明													|
| :--------		| :--------	| :--------												|
| response_type	| 是		| 返回类型，这里请填写：code							|
| client_id		| 是		| 应用id												|
| redirect_uri	| 是		| 用户确认授权后，重定向的url地址							|
| scope			| 否		| 具体请求的权限，多个用逗号隔开						|
| state			| 否		| 随机值，此参数会在重定向时追加到url末尾，不填不追加	|

注意点：
1. 如果用户在Server端尚未登录：会被转发到登录视图，你可以参照文档或官方示例自定义登录页面
2. 如果scope参数为空，或者请求的权限用户近期已确认过，则无需用户再次确认，达到静默授权的效果，否则需要用户手动确认，服务器才可以下放code授权码 

用户确认授权之后，会被重定向至`redirect_uri`，并追加code参数与state参数，形如：
``` url
redirect_uri?code={code}&state={state}
```

Code授权码具有以下特点：
1. 每次授权产生的Code码都不一样
2. Code码用完即废，不能二次使用
3. 一个Code的有效期默认为五分钟，超时自动作废
4. 每次授权产生新Code码，会导致旧Code码立即作废，即使旧Code码尚未使用 


### 1.2、根据授权码获取Access-Token
获得Code码后，我们可以通过以下接口，获取到用户的`Access-Token`、`Refresh-Token`、`openid`等关键信息

``` url
http://sa-oauth-server.com:8001/oauth2/token
	?grant_type=authorization_code
	&client_id={value}
	&client_secret={value}
	&code={value}
```

参数详解：

| 参数			| 是否必填	| 说明													|
| :--------		| :--------	| :--------												|
| grant_type	| 是		| 授权类型，这里请填写：authorization_code				|
| client_id		| 是		| 应用id												|
| client_secret	| 是		| 应用秘钥												|
| code			| 是		| 步骤1.1中获取到的授权码								|

接口返回示例：

``` js
{
    "code": 200,	// 200表示请求成功，非200标识请求失败, 以下不再赘述 
    "msg": "ok",
    "data": {
        "access_token": "7Ngo1Igg6rieWwAmWMe4cxT7j8o46mjyuabuwLETuAoN6JpPzPO2i3PVpEVJ",     // Access-Token值
        "refresh_token": "ZMG7QbuCVtCIn1FAJuDbgEjsoXt5Kqzii9zsPeyahAmoir893ARA4rbmeR66",    // Refresh-Token值
        "expires_in": 7199,                 // Access-Token剩余有效期，单位秒  
        "refresh_expires_in": 2591999,      // Refresh-Token剩余有效期，单位秒  
        "client_id": "1001",                // 应用id
        "scope": "userinfo",                // 此令牌包含的权限
        "openid": "gr_SwoIN0MC1ewxHX_vfCW3BothWDZMMtx__"     // openid 
    }
}
```


### 1.3、根据 Refresh-Token 刷新 Access-Token （如果需要的话）
Access-Token的有效期较短，如果每次过期都需要重新授权的话，会比较影响用户体验，因此我们可以在后台通过`Refresh-Token` 刷新 `Access-Token`

``` url
http://sa-oauth-server.com:8001/oauth2/refresh
	?grant_type=refresh_token
	&client_id={value}
	&client_secret={value}
	&refresh_token={value}
```

参数详解：

| 参数			| 是否必填	| 说明													|
| :--------		| :--------	| :--------												|
| grant_type	| 是		| 授权类型，这里请填写：refresh_token				|
| client_id		| 是		| 应用id												|
| client_secret	| 是		| 应用秘钥												|
| refresh_token | 是		| 步骤1.2中获取到的`Refresh-Token`值							|

接口返回值同章节1.2，此处不再赘述 


### 1.4、回收 Access-Token （如果需要的话）
在Access-Token过期前主动将其回收 

``` url
http://sa-oauth-server.com:8001/oauth2/revoke
	?client_id={value}
	&client_secret={value}
	&access_token={value}
```

参数详解：

| 参数			| 是否必填	| 说明													|
| :--------		| :--------	| :--------												|
| client_id		| 是		| 应用id												|
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
注：此接口为官方仓库模拟接口，正式项目中大家可以根据此样例，自定义需要的接口及参数 

``` url
http://sa-oauth-server.com:8001/oauth2/userinfo?access_token={value}
```

返回值样例：
``` js
{
    "code": 200,
    "msg": "ok",
    "data": {
        "nickname": "shengzhang_",         // 账号昵称
        "avatar": "http://xxx.com/1.jpg",  // 头像地址
        "age": "18",                       // 年龄
        "sex": "男",                       // 性别
        "address": "山东省 青岛市 城阳区"   // 所在城市 
    }
}
```


## 2、模式二：隐藏式（Implicit）

根据以下格式构建URL，引导用户访问：
``` url
http://sa-oauth-server.com:8001/oauth2/authorize
	?response_type=token
	&client_id={value}
	&redirect_uri={value}
	&scope={value}
	$state={value}
```

参数详解：

| 参数			| 是否必填	| 说明													|
| :--------		| :--------	| :--------												|
| response_type	| 是		| 返回类型，这里请填写：token							|
| client_id		| 是		| 应用id												|
| redirect_uri	| 是		| 用户确认授权后，重定向的url地址							|
| scope			| 否		| 具体请求的权限，多个用逗号隔开							|
| state			| 否		| 随机值，此参数会在重定向时追加到url末尾，不填不追加		|

此模式会越过授权码的步骤，直接返回Access-Token到前端页面，形如：
``` url
redirect_uri#token=xxxx-xxxx-xxxx-xxxx
```

## 3、模式三：密码式（Password）
首先在Client端构建表单，让用户输入Server端的账号和密码，然后在Client端访问接口 
``` url
http://sa-oauth-server.com:8001/oauth2/token
	?grant_type=password
	&client_id={value}
	&username={value}
	&password={value}
```

参数详解：

| 参数		| 是否必填	| 说明							|
| :--------	| :--------	| :--------						|
| grant_type| 是		| 返回类型，这里请填写：password|
| client_id	| 是		| 应用id						|
| username	| 是		| 用户的Server端账号			|
| password	| 是		| 用户的Server端密码			|
| scope		| 否		| 具体请求的权限，多个用逗号隔开						|

接口返回示例：

``` js
{
    "code": 200,	// 200表示请求成功，非200标识请求失败, 以下不再赘述 
    "msg": "ok",
    "data": {
        "access_token": "7Ngo1Igg6rieWwAmWMe4cxT7j8o46mjyuabuwLETuAoN6JpPzPO2i3PVpEVJ",     // Access-Token值
        "refresh_token": "ZMG7QbuCVtCIn1FAJuDbgEjsoXt5Kqzii9zsPeyahAmoir893ARA4rbmeR66",    // Refresh-Token值
        "expires_in": 7199,                 // Access-Token剩余有效期，单位秒  
        "refresh_expires_in": 2591999,      // Refresh-Token剩余有效期，单位秒  
        "client_id": "1001",                // 应用id
        "scope": "",                        // 此令牌包含的权限
        "openid": "gr_SwoIN0MC1ewxHX_vfCW3BothWDZMMtx__"     // openid 
    }
}
```


## 4、模式四：凭证式（Client Credentials）
以上三种模式获取的都是用户的 `Access-Token`，代表用户对第三方应用的授权，
在OAuth2.0中还有一种针对 Client级别的授权， 即：`Client-Token`，代表应用自身的资源授权

在Client端的后台访问以下接口：

``` url
http://sa-oauth-server.com:8001/oauth2/client_token
	?grant_type=client_credentials
	&client_id={value}
	&client_secret={value}
```

参数详解：

| 参数			| 是否必填	| 说明									|
| :--------		| :--------	| :--------								|
| grant_type	| 是		| 返回类型，这里请填写：client_credentials|
| client_id		| 是		| 应用id								|
| client_secret	| 是		| 应用秘钥								|
| scope			| 否		| 申请权限								|

接口返回值样例：
``` js
{
    "code": 200,
    "msg": "ok",
    "data": {
        "client_token": "HmzPtaNuIqGrOdudWLzKJRSfPadN497qEJtanYwE7ZvHQWDy0jeoZJuDIiqO",	// Client-Token 值
        "expires_in": 7199,     // Token剩余有效时间，单位秒 
        "client_id": "1001",    // 应用id
        "scope": null           // 包含权限 
    }
}
```

注：`Client-Token`具有延迟作废特性，即：在每次获取最新`Client-Token`的时候，旧`Client-Token`不会立即过期，而是作为`Past-Token`再次储存起来，
资源请求方只要携带其中之一便可通过Token校验，这种特性保证了在大量并发请求时不会出现“新旧Token交替造成的授权失效”， 保证了服务的高可用

