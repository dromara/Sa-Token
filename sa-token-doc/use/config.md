# 框架配置

你可以**零配置启动框架**，但同时你也可以通过一定的参数配置，定制性使用框架，`Sa-Token`支持多种方式配置框架信息

--- 

### 1、配置方式

##### 方式1、在 application.yml 配置

<!---------------------------- tabs:start ---------------------------->

<!------------- tab:yaml 风格  ------------->
``` yaml
############## Sa-Token 配置 (文档: https://sa-token.cc) ##############
sa-token: 
	# token 名称（同时也是 cookie 名称）
	token-name: satoken
    # token 有效期（单位：秒） 默认30天，-1 代表永久有效
	timeout: 2592000
    # token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
	active-timeout: -1
    # 是否允许同一账号多地同时登录 （为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
	is-concurrent: true
    # 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token, 为 false 时每次登录新建一个 token）
	is-share: true
    # token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
	token-style: uuid
    # 是否输出操作日志 
	is-log: true
```

<!------------- tab:properties 风格  ------------->
``` properties
############## Sa-Token 配置 (文档: https://sa-token.cc) ##############

# token 名称（同时也是 cookie 名称）
sa-token.token-name=satoken
# token 有效期（单位：秒） 默认30天，-1 代表永久有效
sa-token.timeout=2592000
# token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
sa-token.active-timeout=-1
# 是否允许同一账号多地同时登录 （为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
sa-token.is-concurrent=true
# 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token, 为 false 时每次登录新建一个 token）
sa-token.is-share=true
# token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
sa-token.token-style=uuid
# 是否输出操作日志 
sa-token.is-log=true
```

<!---------------------------- tabs:end ---------------------------->



##### 方式2、通过代码配置

<!------------------------------ tabs:start ------------------------------>
<!------------- tab:模式 1 ------------->
``` java 
/**
 * Sa-Token 配置类
 */
@Configuration
public class SaTokenConfigure {
	// Sa-Token 参数配置，参考文档：https://sa-token.cc
	// 此配置会覆盖 application.yml 中的配置
    @Bean
    @Primary
    public SaTokenConfig getSaTokenConfigPrimary() {
		SaTokenConfig config = new SaTokenConfig();
		config.setTokenName("satoken");             // token 名称（同时也是 cookie 名称）
		config.setTimeout(30 * 24 * 60 * 60);       // token 有效期（单位：秒），默认30天，-1代表永不过期 
		config.setActiveTimeout(-1);              // token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
		config.setIsConcurrent(true);               // 是否允许同一账号多地同时登录（为 true 时允许一起登录，为 false 时新登录挤掉旧登录）
		config.setIsShare(true);                    // 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token，为 false 时每次登录新建一个 token）
		config.setTokenStyle("uuid");               // token 风格
		config.setIsLog(false);                     // 是否输出操作日志 
		return config;
	}
}
```
<!------------- tab:模式 2 ------------->
``` java
/**
 * Sa-Token 配置类
 */
@Configuration
public class SaTokenConfigure {
	// Sa-Token 参数配置，参考文档：https://sa-token.cc
	// 此配置会与 application.yml 中的配置合并 （代码配置优先）
	@Autowired
	public void configSaToken(SaTokenConfig config) {
		config.setTokenName("satoken");             // token 名称（同时也是 cookie 名称）
		config.setTimeout(30 * 24 * 60 * 60);       // token 有效期（单位：秒），默认30天，-1代表永不过期 
		config.setActiveTimeout(-1);              // token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
		config.setIsConcurrent(true);               // 是否允许同一账号多地同时登录（为 true 时允许一起登录，为 false 时新登录挤掉旧登录）
		config.setIsShare(true);                    // 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token，为 false 时每次登录新建一个 token）
		config.setTokenStyle("uuid");               // token 风格
		config.setIsLog(false);                     // 是否输出操作日志 
	}
}
```
<!---------------------------- tabs:end ------------------------------>

两者的区别在于：
- 模式 1 会覆盖 application.yml 中的配置。
- 模式 2 会与 application.yml 中的配置合并（代码配置优先）。


--- 
### 2、核心包所有可配置项

**你不必立刻掌握整个表格，只需要在用到某个功能时再详细查阅它即可**

| 参数名称				| 类型		| 默认值		| 说明																				|
| :--------				| :--------	| :--------	| :--------																			|
| tokenName				| String	| satoken	| Token 名称 （同时也是 Cookie 名称、数据持久化前缀）													|
| timeout				| long		| 2592000	| Token 有效期（单位：秒），默认30天，-1代表永不过期 [参考：token有效期详解](/fun/token-timeout)		|
| activeTimeout			| long		| -1		| Token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结（例如可以设置为1800代表30分钟内无操作就冻结） 	[参考：token有效期详解](/fun/token-timeout)													|
| dynamicActiveTimeout	| Boolean	| false		| 是否启用动态 activeTimeout 功能，如不需要请设置为 false，节省缓存请求次数	|
| isConcurrent			| Boolean	| true		| 是否允许同一账号并发登录 （为 true 时允许一起登录，为 false 时新登录挤掉旧登录）															|
| isShare				| Boolean	| true		| 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token，为 false 时每次登录新建一个 token，login 时提供了 Extra 数据后，即使配置了为 true 也不能复用旧 Token，必须创建新 Token） 	|
| maxLoginCount			| int		| 12		| 同一账号最大登录数量，-1代表不限 （只有在 `isConcurrent=true`，`isShare=false` 时此配置才有效），[详解](/use/config?id=配置项详解：maxlogincount)	|
| maxTryTimes			| int		| 12		| 在每次创建 Token 时的最高循环次数，用于保证 Token 唯一性（-1=不循环重试，直接使用）			|
| isReadBody			| Boolean	| true		| 是否尝试从 请求体 里读取 Token														|
| isReadHeader			| Boolean	| true		| 是否尝试从 header 里读取 Token														|
| isReadCookie			| Boolean	| true		| 是否尝试从 cookie 里读取 Token，此值为 false 后，`StpUtil.login(id)` 登录时也不会再往前端注入Cookie				|
| isWriteHeader			| Boolean	| false		| 是否在登录后将 Token 写入到响应头							|
| tokenStyle			| String	| uuid		| token风格， [参考：自定义Token风格](/up/token-style)										|
| dataRefreshPeriod		| int		| 30		| 默认数据持久组件实现类中，每次清理过期数据间隔的时间 （单位: 秒） ，默认值30秒，设置为-1代表不启动定时清理 		|
| tokenSessionCheckLogin	| Boolean	| true	| 获取 `Token-Session` 时是否必须登录 （如果配置为true，会在每次获取 `Token-Session` 时校验是否登录），[详解](/use/config?id=配置项详解：tokenSessionCheckLogin)		|
| autoRenew				| Boolean	| true		| 是否打开自动续签 （如果此值为true，框架会在每次直接或间接调用 `getLoginId()` 时进行一次过期检查与续签操作），[参考：token有效期详解](/fun/token-timeout)		|
| tokenPrefix			| String	| null		| token前缀，例如填写 `Bearer` 实际传参 `satoken: Bearer xxxx-xxxx-xxxx-xxxx` 	[参考：自定义Token前缀](/up/token-prefix) 			|
| isPrint				| Boolean	| true		| 是否在初始化配置时打印版本字符画													|
| isLog					| Boolean	| false		| 是否打印操作日志																	|
| logLevel				| String	| trace		| 日志等级（trace、debug、info、warn、error、fatal），此值与 logLevelInt 联动				|
| logLevelInt			| int		| 1			| 日志等级 int 值（1=trace、2=debug、3=info、4=warn、5=error、6=fatal），此值与 logLevel 联动		|
| isColorLog			| Boolean	| null		| 是否打印彩色日志，true=打印彩色日志，false=打印黑白日志，null=框架根据运行终端自行判断是否打印彩色日志 		|
| jwtSecretKey			| String	| null		| jwt秘钥 （只有集成 `sa-token-temp-jwt` 模块时此参数才会生效），[参考：和 jwt 集成](/plugin/jwt-extend)	|
| sameTokenTimeout		| long		| 86400		| Same-Token的有效期 （单位: 秒），[参考：内部服务外网隔离](/micro/same-token)					|
| basic					| String	| ""		| Http Basic 认证的账号和密码 [参考：Http Basic 认证](/up/basic-auth)						|
| currDomain			| String	| null		| 配置当前项目的网络访问地址													|
| checkSameToken			| Boolean		| false		| 是否校验Same-Token（部分rpc插件有效）															|
| cookie				| Object	| new SaCookieConfig()	| Cookie 配置对象															|
| sign					| Object	| new SaSignConfig()	| API 签名配置对象															|

Cookie相关配置：

| 参数名称		| 类型		| 默认值		| 说明																			|
| :--------		| :--------	| :--------	| :--------																		|
| domain		| String	| null		| 作用域（写入Cookie时显式指定的作用域, 常用于单点登录二级域名共享Cookie的场景）			|
| path			| String	| /		| 路径，默认写在域名根路径下			|
| secure		| Boolean	| false		| 是否只在 https 协议下有效												|
| httpOnly		| Boolean	| false		| 是否禁止 js 操作 Cookie 	|
| sameSite		| String	| Lax		| 第三方限制级别（Strict=完全禁止，Lax=部分允许，None=不限制）		|
| extraAttrs	| String	| new LinkedHashMap()		| 额外扩展属性		|


Cookie 配置示例：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
# Sa-Token 配置
sa-token: 
    # Cookie 相关配置 
    cookie: 
		# 基础属性 
        domain: stp.com
        path: /
        secure: false
		httpOnly: true
		sameSite: Lax
        # 额外扩展属性
        extraAttrs:
            # Cookie 优先级
            Priority: Medium
            # Cookie 独立分区
            Partitioned: ""
            # 可以是任意键值对
            # abc: def
```
<!------------- tab:properties 风格  ------------->
``` properties
# Cookie 相关配置 
# ---- 基础属性
sa-token.cookie.domain=stp.com
sa-token.cookie.path=/
sa-token.cookie.secure=false
sa-token.cookie.httpOnly=true
sa-token.cookie.sameSite=Lax
# ---- 额外扩展属性
# Cookie 优先级
sa-token.cookie.extraAttrs.Priority=Medium
# Cookie 独立分区
sa-token.cookie.extraAttrs.Partitioned=""
# 可以是任意键值对
# sa-token.cookie.extraAttrs.abc=def
```
<!---------------------------- tabs:end ---------------------------->

Sign 参数签名相关配置：

| 参数名称				| 类型		| 默认值		| 说明																	|
| :--------				| :--------	| :--------	| :--------																|
| secretKey				| String	| null		| API 调用签名秘钥														|
| timestampDisparity	| long		| 900000		| 接口调用时的时间戳允许的差距（单位：ms），-1 代表不校验差距，默认15分钟		|

示例：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
# Sa-Token 配置
sa-token: 
    # 参数签名配置 
    sign: 
		# API 接口调用签名秘钥
        secret-key: kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
```
<!------------- tab:properties 风格  ------------->
``` properties
# API 接口调用签名秘钥
sa-token.sign.secret-key=kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
```
<!---------------------------- tabs:end ---------------------------->




### 3、单点登录相关配置 

**SSO-Server 端配置：**

| 参数名称		| 类型		| 默认值		| 说明																			|
| :--------		| :--------	| :--------	| :--------																		|
| mode			| String	| 			| 指定当前系统集成 SSO 时使用的模式（约定型配置项，不对代码逻辑产生任何影响）			|
| ticketTimeout	| long		| 300		| ticket 有效期 （单位: 秒）														|
| allowUrl		| String	| *			| 所有允许的授权回调地址，多个用逗号隔开（不在此列表中的URL将禁止下放ticket），参考：[SSO整合：配置域名校验](/sso/sso-check-domain)	|
| homeRoute		| String	|  			| 主页路由：在 /sso/auth 登录后不指定 redirect 参数的情况下默认跳转的路由			|
| isSlo			| Boolean	| true		| 是否打开单点注销功能															|
| isHttp		| Boolean	| false		| 是否打开模式三（此值为 true 时将使用 http 请求：校验 ticket 值、单点注销、获取 userinfo），参考：[详解](/use/config?id=配置项详解：isHttp) 	|
| autoRenewTimeout	| Bolean	| false	| 是否在每次下发 ticket 时，自动续期 token 的有效期（根据全局 timeout 值）			|
| maxRegClient	| int		| 32		| 在 Access-Session 上记录 Client 信息的最高数量（-1=无限），超过此值将进行自动清退处理，先进先出			|
| isCheckSign	| Boolean	| true		| 是否校验参数签名（方便本地调试用的一个配置项，生产环境请务必为true）		|

配置示例：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
# Sa-Token 配置
sa-token:
    # SSO 单点登录服务端配置 
    sso-server:
        # Ticket有效期 (单位: 秒)，默认五分钟 
        ticket-timeout: 300
        # 所有允许的授权回调地址
        allow-url: "*"
```
<!------------- tab:properties 风格  ------------->
``` properties
# Ticket有效期 (单位: 秒)，默认五分钟 
sa-token.sso-server.ticket-timeout=300
# 所有允许的授权回调地址
sa-token.sso-server.allow-url="*"
```
<!---------------------------- tabs:end ---------------------------->

**SSO-Client 端配置：**

| 参数名称		| 类型		| 默认值		| 说明											|
| :--------		| :--------	| :--------	| :--------										|
| mode			| String	| 					| 指定当前系统集成 SSO 时使用的模式（约定型配置项，不对代码逻辑产生任何影响）			|
| client		| String	| ""				| 当前 Client 名称标识，用于和 ticket 码的互相锁定			|
| serverUrl		| String	| null				| 配置 Server 端主机总地址，拼接在 `authUrl`、`checkTicketUrl`、`userinfoUrl`、`sloUrl` 属性前面，用以简化各种 url 配置，参考：[详解](/sso/sso-questions?id=问：模式三配置一堆-xxx-url-，有办法简化一下吗？)	|
| authUrl		| String	| /sso/auth			| 配置 Server 端单点登录授权地址					|
| checkTicketUrl| String	| /sso/checkTicket	| 配置 Server 端的 `ticket` 校验地址							|
| getDataUrl	| String	| /sso/getData		| 配置 Server 端的 拉取数据 地址									|
| sloUrl		| String	| /sso/signout		| 配置 Server 端单点注销地址										|
| currSsoLogin	| String	| null				| 配置当前 Client 端的登录地址（为空时自动获取）	|
| currSsoLogoutCall	| String	| null			| 配置当前 Client 端的单点注销回调URL （为空时自动获取）	|
| isSlo			| Boolean	| true				| 是否打开单点注销功能							|
| isHttp		| Boolean	| false				| 是否打开模式三（此值为 true 时将使用 http 请求：校验 ticket 值、单点注销、拉取数据getData），参考：[详解](/use/config?id=配置项详解：isHttp) 	|
| isCheckSign	| Boolean	| true		| 是否校验参数签名（方便本地调试用的一个配置项，生产环境请务必为true）		|

配置示例：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
# Sa-Token 配置
sa-token: 
    # SSO-相关配置
    sso-client: 
        # sso-server 端主机地址
        server-url: http://sa-sso-server.com:9000
        # 是否打开单点注销功能 
        is-slo: true
```
<!------------- tab:properties 风格  ------------->
``` properties
# sso-server 端主机地址
sa-token.sso-client.server-url=http://sa-sso-server.com:9000
# 是否打开单点注销功能 
sa-token.sso-client.is-slo=true
```
<!---------------------------- tabs:end ---------------------------->




### 4、OAuth2.0相关配置 
| 参数名称					| 类型		| 默认值	| 说明																		|
| :--------					| :--------	| :--------	| :--------																	|
| enableAuthorizationCode	| Boolean	| true		| 是否打开模式：授权码（`Authorization Code`）								|
| enableImplicit			| Boolean	| true		| 是否打开模式：隐藏式（`Implicit`）											|
| enablePassword			| Boolean	| true		| 是否打开模式：密码式（`Password`）											|
| enableClientCredentials	| Boolean	| true		| 是否打开模式：凭证式（`Client Credentials`）								|
| isNewRefresh				| Boolean	| false		| 是否在每次 `Refresh-Token` 刷新 `Access-Token` 时，产生一个新的 `Refresh-Token`	|
| codeTimeout				| long		| 300		| Code授权码 保存的时间（单位：秒） 默认五分钟									|
| accessTokenTimeout		| long		| 7200		| `Access-Token` 保存的时间（单位：秒）默认两个小时								|
| refreshTokenTimeout		| long		| 2592000	| `Refresh-Token` 保存的时间（单位：秒） 默认30 天								|
| clientTokenTimeout		| long		| 7200		| `Client-Token` 保存的时间（单位：秒） 默认两个小时								|
| lowerClientTokenTimeout	| long		| 7200		| `Lower-Client-Token` 保存的时间（单位：秒） ，默认为-1，代表延续 `Client-Token` 的有效时间 	|
| openidDigestPrefix		| String	| openid_default_digest_prefix		| 默认 openid 生成算法中使用的摘要前缀				 	|
| higherScope				| String	| 		| 指定高级权限，多个用逗号隔开				 	|
| lowerScope				| String	| 		| 指定低级权限，多个用逗号隔开				 	|
| mode4ReturnAccessToken	| Boolean	| false	| 模式4是否返回 AccessToken 字段，用于兼容OAuth2标准协议			 	|
| hideStatusField			| Boolean	| false	| 是否在返回值中隐藏默认的状态字段 (code、msg、data)			 	|
| oidc		| SaOAuth2OidcConfig	| new SaOAuth2OidcConfig()	| OIDC 相关配置			 	|

配置示例：
<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
# Sa-Token 配置
sa-token: 
    token-name: sa-token-oauth2-server
    # OAuth2.0 配置 
    oauth2-server: 
        enable-authorization-code: true
        enable-implicit: true
        enable-password: true
        enable-client-credentials: true
```
<!------------- tab:properties 风格  ------------->
``` properties
# Sa-Token 配置 
sa-token.token-name=sa-token-oauth2-server
# OAuth2.0 配置 
sa-token.oauth2-server.enable-authorization-code=true
sa-token.oauth2-server.enable-implicit=true
sa-token.oauth2-server.enable-password=true
sa-token.oauth2-server.enable-client-credentials=true
```
<!---------------------------- tabs:end ---------------------------->


##### OIDC 相关配置
| 参数名称					| 类型		| 默认值	| 说明																			|
| :--------					| :--------	| :--------	| :--------																	|
| iss						| String	| 			| iss 值，如不配置则自动计算													|
| idTokenTimeout			| long		| 600		| idToken 有效期（单位秒） 默认十分钟											|

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
# Sa-Token 配置
sa-token: 
    oauth2-server: 
		oidc: 
			iss: xxx
			idTokenTimeout: 600
```
<!------------- tab:properties 风格  ------------->
``` properties
sa-token.oauth2-server.oidc.iss=xxx
sa-token.oauth2-server.oidc.idTokenTimeout=600
```
<!---------------------------- tabs:end ---------------------------->



##### SaClientModel属性定义
| 参数名称				| 类型			| 默认值	| 说明													|
| :--------				| :--------		| :--------	| :--------											|
| clientId				| String		| null		| 应用id，应该全局唯一								|
| clientSecret			| String		| null		| 应用秘钥											|
| contractScopes		| List<String>	| null		| 应用签约的所有权限 									|
| allowRedirectUris		| List<String>	| null		| 应用允许授权的所有URL（可以使用 `*` 号通配符）			|
| allowGrantTypes		| List<String>	| new ArrayList<>()	| 应用允许的所有 `grant_type`							|
| isNewRefresh			| Boolean		| 取全局配置		| 单独配置此Client：是否在每次 `Refresh-Token` 刷新 `Access-Token` 时，产生一个新的 Refresh-Token [ 默认取全局配置 ]	|
| accessTokenTimeout	| long			| 取全局配置		| 单独配置此Client：`Access-Token` 保存的时间（单位：秒）  [默认取全局配置]	|
| refreshTokenTimeout	| long			| 取全局配置		| 单独配置此Client：`Refresh-Token` 保存的时间（单位：秒） [默认取全局配置]	|
| clientTokenTimeout	| long			| 取全局配置		| 单独配置此Client：`Client-Token` 保存的时间（单位：秒） [默认取全局配置]	|
|lowerClientTokenTimeout	| long		| 取全局配置		| 单独配置此Client：`Lower-Client-Token` 保存的时间（单位：秒） [默认取全局配置]	|



### 5、部分配置项详解

对部分配置项做一下详解 

#### 配置项详解：maxLoginCount

配置含义：同一账号最大登录数量。

在配置 `isConcurrent=true`, `isShare=false` 时，Sa-Token 将允许同一账号并发登录，且每次登录都会产生一个新Token，
这些 Token 都会以 `TokenSign` 的形式记录在其 `Account-Session` 之上，这就造成一个问题：

随着同一账号登录的次数越来越多，TokenSign 的列表也会越来越大，极端情况下，列表长度可能达到成百上千以上，严重拖慢数据处理速度，
为此 Sa-Token 对这个 TokenSign 列表的大小设定一个上限值，也就是 `maxLoginCount`，默认值=12。

假设一个账号的登录数量超过 `maxLoginCount` 后，将会主动注销第一个登录的会话（先进先出），以此保证队列中的有效会话数量始终 `<= maxLoginCount` 值。


#### 配置项详解：tokenSessionCheckLogin
配置含义：获取 `Token-Session` 时是否必须登录 （如果配置为true，会在每次获取 `Token-Session` 时校验是否登录）。

在调用 `StpUtil.login(id)` 登录后，

- 调用 `StpUtil.getSession()` 可以获取这个会话的 `Account-Session` 对象。
- 调用 `StpUtil.getTokenSession()` 可以获取这个会话 `Token-Session` 对象。

关于两种 Session 有何区别，可以参考这篇：[Session模型详解](/fun/session-model)，此处暂不赘述。

从设计上讲，无论会话是否已经登录，只要前端提供了Token，我们就可以找到这个 Token 的专属 `Token-Session` 对象，**这非常灵活但不安全**，
因为前端提交的 Token 可能是任意伪造的。

为了解决这个问题，`StpUtil.getTokenSession()` 方法在获取 `Token-Session` 时，会率先检测一下这个 Token 是否是一个有效Token：
- 如果是有效Token，正常返回 `Token-Session` 对象
- 如果是无效Token，则抛出异常。

这样就保证了伪造的 Token 是无法获取 `Token-Session` 对象的。

但是 —— 有的场景下我们又确实需要在登录之前就使用 Token-Session 对象，这时候就把配置项 `tokenSessionCheckLogin` 值改为 `false` 即可。

<!-- 
#### 配置项详解：isAutoMode

配置含义：是否自动判断此 Client 开放的授权模式。

- 此值为 true 时：四种模式（`isCode、isImplicit、isPassword、isClient`）是否生效，依靠全局设置；
- 此值为 false 时：四种模式（`isCode、isImplicit、isPassword、isClient`）是否生效，依靠局部配置+全局配置（两个都为 true 时才打开） 
 -->

#### 配置项详解：isHttp

配置含义：是否打开单点登录模式三。

- 此配置项为 false 时，代表使用SSO模式二：使用 Redis 校验 ticket 值、删除 Redis 数据做到单点注销、使用 Redis 同步 Userinfo 数据。
- 此配置项为 true 时，代表使用SSO模式三：使用 Http 请求校验 ticket 值、使用 Http 请求做到单点注销、使用 Http 请求同步 Userinfo 数据。




---

<a class="case-btn" href="https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/resources/application.yml"
	target="_blank">
	本章代码示例：Sa-Token 框架配置 —— [ application.yml ]
</a>
<a class="dt-btn" href="https://www.wenjuan.ltd/s/nUfe2iU/" target="_blank">本章小练习：Sa-Token 基础 - 框架配置，章节测试</a>

