# 数据结构


## 1、登录会话

### 1.1、token -> loginId 映射

``` js
// ttl = 此 token 的 timeout 有效期
{tokenName}:{loginType}:token:{tokenValue}  -->  {loginId}
```

<details>
<summary>详细</summary>

示例：
``` js
satoken:login:token:47ab0105-2be1-400c-b517-82f81a0cfcf8  -->  10001
```

异常 value 格式

``` js
-1       未能从请求中读取到有效 token
-2       已读取到 token，但是 token 无效
-3       已读取到 token，但是 token 已经过期 (详)
-4       已读取到 token，但是 token 已被顶下线
-5       已读取到 token，但是 token 已被踢下线
-6       已读取到 token，但是 token 已被冻结
-7       未按照指定前缀提交 token
```

</details>


### 1.2、active-timeout

``` js
// ttl = 对应 token 的 timeout 有效期值
{tokenName}:{loginType}:last-active:{tokenValue}  -->  {13位时间戳}
```

<details>
<summary>详细</summary>

示例：
``` js
satoken:login:last-active:06d1f12b-614e-4c00-8d8e-c07fef5f4aa9   -->   1722334954193
```

value 格式分两种：
```
1722334954193          // 单值时：此 token 最后访问日期
1722334954193, 1200    // 双值时：此 token 最后访问日期，此 token 指定的动态 active-timeout 值 
```

注意：判断一个 token 是否 active-timeout 过期，与 ttl 无关，而是利 value 值计算：
``` js
当前时间 - token 最后访问时间 > active-timeout   （true=token 已冻结，false=token 未冻结 ）
```

</details>



### 1.3、SaSession

``` js
{tokenName}:{loginType}:session:{loginId}  -->  {SaSession 对象}        // Account-Session
{tokenName}:{loginType}:token-session:{loginId}  -->  {SaSession 对象}  // Token-Session
{tokenName}:custom:session:{sessionId}  -->  {SaSession 对象}           // Custom-Session
```

<details>
<summary>详细</summary>

key 示例 
``` js
// Account-Session
satoken:login:session:1000001

// Token-Session
satoken:login:session:47ab0105-2be1-400c-b517-82f81a0cfcf8

// Custom-Session
satoken:custom:session:role-1001
```

value 格式 

``` js
{
  "@class": "cn.dev33.satoken.dao.SaSessionForJacksonCustomized",    // java calss 信息
  "id": "satoken:login:session:10001",    // sessionId
  "type": "Account-Session",    // session类型：Account-Session / Token-Session / Custom-Session
  "loginType": "login",     // 账号类型 
  "loginId": [    // 对应登录id 值（Account-Session才会有值）
    "java.lang.Long",
    10001
  ],    
  "token": null,    // 对应 token 值 （Token-Session才会有值）
  "createTime": 1722334954145,    // 此 session 创建时间，13位时间戳 
  "dataMap": {    // 此 session 挂载数据 
    "@class": "java.util.concurrent.ConcurrentHashMap", 
    "name": "张三"    // 此 session 挂载数据 详情
	// 更多值 ...
  },
  "terminalList": [		// 已登录终端信息列表（Account-Session才会有值）
    "java.util.Vector",
    [
      {
        "@class": "cn.dev33.satoken.session.SaTerminalInfo",
        "index": 1,
        "tokenValue": "2551663f-bb98-47d7-9af3-e2e6a28dadce",   // 客户端 token 值
        "deviceType": "DEF",  // 登录设备类型 
        "deviceId": "xxxxxxxxx",  // 登录设备id 
        "extraData": {
			// 扩展信息列表 （手动自定义值）
			"@class": "java.util.LinkedHashMap",
			"deviceSimpleTitle": "XiaoMi 15 Ultra",
			"loginAddress": "浙江省杭州市西湖区",
			"loginIp": "127.0.0.1",
			"loginTime": "2025-03-08 15:00:02"
        },
        "createTime": 1741406340845 // 登录时间 
      }
    ]
  ]
}
```

</details>


### 1.4、二级认证
``` js
{tokenName}:{loginType}:safe:{service}:{tokenValue}  -->  SAFE_AUTH_SAVE_VALUE
```
value 为常亮值：`SAFE_AUTH_SAVE_VALUE`


### 1.5、账号服务封禁
``` js
{tokenName}:{loginType}:disable:{service}:{loginId}  -->  {level}
```
value 为封禁等级，int类型 


### 1.6、其它
SaApplication 全局变量
``` js
{tokenName}:var:{变量名}
```

本次请求新创建 token，在 SaStorage 存储 key 
``` js
JUST_CREATED_  -->  {token}
```

本次请求新创建 token，在 SaStorage 存储 key  （无前缀方式）
``` js
JUST_CREATED_NOT_PREFIX_  -->  {token}
```

临时身份切换，使用的key
``` js
SWITCH_TO_SAVE_KEY_{loginType}  -->  {loginId}
```


## 2、SSO 单点登录

### 2.1、ticket -> loginId 映射
``` js
// ttl = 此 ticket 有效期，下同理 
{tokenName}:ticket:{ticket}  -->  {loginId}
```

### 2.2、ticket -> client 映射
``` js
{tokenName}:ticket-client:{ticket}  -->  {client}
```

### 2.3、loginId -> ticket 映射（client + loginId 反查 ticket）
``` js
{tokenName}:ticket-index:{client}:{loginId}  -->  {ticket}
```



## 3、OAuth2 统一认证 

### 3.1、Code 授权码
``` js
{tokenName}:oauth2:code:{code}  -->  {CodeModel 对象}
```

<details>
<summary>详细</summary>

value 示例：

``` js
{
	"@class": "cn.dev33.satoken.oauth2.model.CodeModel",    // java class 信息
	"code": "AbRVp2HrgyklE0BXYWszskGJWAGY7xhGu6Zaco4zJECzGYagCCFWj0jOlHza",    // code值
	"scope": "",    // 所申请权限列表，多个用逗号隔开
	"loginId": "10001",    // 对应的loginId
	"redirectUri": "",    // 重定向地址
}
```

</details>

clientId + loginId 反查 code
``` js
{tokenName}:oauth2:code-index:{clientId}:{loginId}  -->  {code 值}
```



### 3.2、Access-Token 资源令牌
``` js
{tokenName}:oauth2:access-token:{accessToken}  -->  {AccessTokenModel 对象}
```

<details>
<summary>详细</summary>

value 示例：

``` js
{
  "@class": "cn.dev33.satoken.oauth2.data.model.AccessTokenModel",    // java class 信息
  "accessToken": "Pu3t55dJIgvkmVoHz50FqaVQOJ6Flggjr2eHTiS74Ooai8e3nNyYPq78K80P",    // 资源令牌值
  "refreshToken": "baGyl6PHK304tPojnpxd1SpW12oJcOGv7gFaDAAkjLWbJG1J1WLUIGobsw7m",    // 刷新令牌值
  "expiresTime": 1738280553695,    // 资源令牌到期时间
  "refreshExpiresTime": 1740865353760,    // 刷新令牌到期时间
  "clientId": "1001",    // 对应的应用id
  "loginId": "10001",    // 对应的loginId
  "scopes": [   // 所具有的权限列表 
    "java.util.ArrayList",
    [
      "userinfo",
      "userid",
      "openid",
      "unionid",
      "oidc"
    ]
  ],
  "tokenType": "bearer",   // tokenType 
  "grantType": "authorization_code",   // 授权方式 
  "extraData": {   // 扩展数据  
    "@class": "java.util.LinkedHashMap",
    "userid": "10001",
    "openid": "ded91dc189a437dd1bac2274be167d50",
    "unionid": "11d48faa74c4e5f19355ccc53c1c5c7a",
    "id_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vc2Etb2F1dGgtc2VydmVyLmNvbTo4MDAwIiwic3ViIjoiMTAwMDEiLCJhdWQiOiIxMDAxIiwiZXhwIjoxNzM4MjczOTUzLCJpYXQiOjE3MzgyNzMzNTMsImF1dGhfdGltZSI6MTczODI3MzM0Miwibm9uY2UiOiJZQTlPQjJzYkpGanZkUlFjN0E3V1pnTUFhTDFVRjE5OSIsImF6cCI6IjEwMDEifQ.pvoj6CR7tdhOblvYJoGUfvam9egSiL5Uw3tflLLMb5g"
  },
  "createTime": 1738273353694,   // 创建时间   
  "expiresIn": 7199    // 资源令牌剩余有效时间，单位秒
  "refreshExpiresIn": 2592000,    // 刷新令牌剩余有效时间，单位秒
}

```

</details>

clientId + loginId 反查 Access-Token
``` js
{tokenName}:oauth2:access-token-index:{clientId}:{loginId}  -->  {access_token 值}
```


### 3.3、Refresh-Token 资源令牌
``` js
{tokenName}:oauth2:refresh-token:{refreshToken}  -->  {RefreshTokenModel 对象}
```

<details>
<summary>详细</summary>

value 示例：

``` js
{
  "@class": "cn.dev33.satoken.oauth2.data.model.RefreshTokenModel",    // java class 信息
  "refreshToken": "baGyl6PHK304tPojnpxd1SpW12oJcOGv7gFaDAAkjLWbJG1J1WLUIGobsw7m",    // 刷新令牌值
  "expiresTime": 1740865353760,   // 刷新令牌到期时间
  "clientId": "1001",    // 对应的应用id
  "loginId": "10001",    // 对应的loginId
  "scopes": [    // 所具有的权限列表 
    "java.util.ArrayList",
    [
      "userinfo",
      "userid",
      "openid",
      "unionid",
      "oidc"
    ]
  ],
  "extraData": {   // 扩展数据  
    "@class": "java.util.LinkedHashMap",
    "userid": "10001",
    "openid": "ded91dc189a437dd1bac2274be167d50",
    "unionid": "11d48faa74c4e5f19355ccc53c1c5c7a",
    "id_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vc2Etb2F1dGgtc2VydmVyLmNvbTo4MDAwIiwic3ViIjoiMTAwMDEiLCJhdWQiOiIxMDAxIiwiZXhwIjoxNzM4MjczOTUzLCJpYXQiOjE3MzgyNzMzNTMsImF1dGhfdGltZSI6MTczODI3MzM0Miwibm9uY2UiOiJZQTlPQjJzYkpGanZkUlFjN0E3V1pnTUFhTDFVRjE5OSIsImF6cCI6IjEwMDEifQ.pvoj6CR7tdhOblvYJoGUfvam9egSiL5Uw3tflLLMb5g"
  },
  "createTime": 1738273353760,   // 创建时间   
  "expiresIn": 2591999    // 刷新令牌剩余有效时间，单位秒
}
```

</details>

clientId + loginId 反查 Refresh-Token
``` js
{tokenName}:oauth2:refresh-token-index:{clientId}:{loginId}  -->  {refresh_token 值}
```


### 3.4、Client-Token 应用令牌
``` js
{tokenName}:oauth2:client-token:{clientToken}  -->  {ClientTokenModel 对象}
```

<details>
<summary>详细</summary>

value 示例：

``` js
{
  "@class": "cn.dev33.satoken.oauth2.data.model.ClientTokenModel",    // java class 信息
  "clientToken": "lIpS3fKEACKMFauEWVpR7Zmzh7SoFetPVuB9aDzISnqzHKu8R3OwpWFy5nLv",    // 应用令牌值 
  "expiresTime": 1738280930646,    // 应用令牌到期时间
  "clientId": "1001",    // 对应的应用id
  "scopes": [    // 所具有的权限列表 
    "java.util.ArrayList",
    [
      "userinfo",
      "userid",
      "openid",
      "unionid",
      "oidc"
    ]
  ],
  "tokenType": "bearer",   // tokenType   
  "grantType": "client_credentials",   // 授权类型    
  "extraData": {   // 扩展数据
    "@class": "java.util.LinkedHashMap"
  },
  "createTime": 1738273730646,   // 创建时间   
  "expiresIn": 7199    // 应用令牌剩余有效时间，单位秒
}
```

</details>

clientId 反查 Client-Token
``` js
{tokenName}:oauth2:client-token-index:{clientId}  -->  {client_token 值}
```

Lower-Client-Token 次级应用令牌索引
``` js
{tokenName}:oauth2:lower-client-token-index:{clientId}  -->  {client_token 值}
```

### 3.5、用户授权记录
``` js
{tokenName}:oauth2:grant-scope:{clientId}:{loginId}  -->  {scope列表}
```
值为 scope 列表，多个用逗号隔开，例如：`userinfo,openid,userid`。




## 4、插件

### 4.1、临时 token 会话 

temp-token -> value

``` js
// namespace 默认值为 "temp-token"
{tokenName}:{namespace}:{temp-token}  -->  {value}
```

value 反查 temp-token

``` js
{tokenName}:raw-session:{namespace}:{value}  -->  {Raw#SaSession 对象}
```

- 在 SaSession 以 `__HD_TEMP_TOKEN_MAP` 为 key 存储 temp-token 索引列表。值类型为 Map。
- 其中：Map 的 key = temp-token 值，Map 的 value = 此 temp-token 到期时间戳。



### 4.2、 Same-Token 

Same-Token 
``` js
{tokenName}:var:same-token  -->  {same-token 值}
```

Past-Same-Token 
``` js
{tokenName}:var:past-same-token  -->  {same-token 值}
```


### 4.3、Sign 签名

随机字符串
``` js
// nonce 值 默认为 32位随机字符
{tokenName}:sign:nonce:{nonce}  -->  {nonce 值}
```

### 4.4、API Key

``` js
// namespace 默认值为 "apikey" (全小写), ttl = 此 API Key 剩余有效期 
{tokenName}:{namespace}:{apikey}  -->  {ApiKeyModel 对象}
```

<details>
<summary>详细</summary>

key 示例：

``` js
satoken:apikey:AK-XCoJLP2E7Q9GXyPiiZWMM8Sqi6Fm0JoFC41R
```

value 示例：
``` js
{
	"@class": "cn.dev33.satoken.apikey.model.ApiKeyModel",   // java class 信息
	"title": "test",   // API Key 名称 
	"intro": null,   // 用途介绍
	"apiKey": "AK-XCoJLP2E7Q9GXyPiiZWMM8Sqi6Fm0JoFC41R",   // API Key 值
	"loginId": "10001",   // 所属用户 id
	"createTime": 1766509019137,   // 创建时间戳
	"expiresTime": 1769101019136,   // 到期时间戳
	"isValid": true,   // 是否有效
	"scopes": [   // 含有权限
		"java.util.ArrayList",
		[
		  "userinfo",
		  "user-update"
		]
	],
	"extraData": null   // 扩展数据：Map<String, Object> 类型 
}
```

</details>


value 反查 API Key 

``` js
{tokenName}:raw-session:{namespace}:{value}  -->  {Raw#SaSession 对象}
```

- 在 SaSession 以 `__HD_API_KEY_LIST` 为 key 存储 API Key 索引列表。值类型为 List (API Key 列表)。








