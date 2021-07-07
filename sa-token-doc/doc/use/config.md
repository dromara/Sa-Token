# 框架配置
你可以**零配置启动框架** <br>
但同时你也可以通过配置，定制性使用框架，`Sa-Token`支持多种方式配置框架信息





### 方式1、在`application.yml`配置

``` java
# Sa-Token配置
sa-token: 
	# token名称 (同时也是cookie名称)
	token-name: satoken
	# token有效期，单位s 默认30天, -1代表永不过期 
	timeout: 2592000
	# token临时有效期 (指定时间内无操作就视为token过期) 单位: 秒
	activity-timeout: -1
	# 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录) 
	is-concurrent: false
	# 在多人登录同一账号时，是否共用一个token (为true时所有登录共用一个token, 为false时每次登录新建一个token) 
	is-share: false
	# token风格
	token-style: uuid
	# 是否输出操作日志 
	is-log: false
```

如果你习惯于 `application.properties` 类型的配置文件，那也很好办: 百度： [springboot properties与yml 配置文件的区别](https://www.baidu.com/s?ie=UTF-8&wd=springboot%20properties%E4%B8%8Eyml%20%E9%85%8D%E7%BD%AE%E6%96%87%E4%BB%B6%E7%9A%84%E5%8C%BA%E5%88%AB)

!> 注：旧版本配置前缀为`[spring.sa-token.]`，自v1.21.0开始，均改为`[sa-token.]`，目前版本暂时向下兼容，请尽快更新 


### 方式2、通过代码配置
方式1：
``` java 
/**
 * Sa-Token代码方式进行配置
 */
@Configuration
public class SaTokenConfigure {

	// 获取配置Bean (以代码的方式配置Sa-Token, 此配置会覆盖yml中的配置)
    @Bean
    @Primary
    public SaTokenConfig getSaTokenConfigPrimary() {
		SaTokenConfig config = new SaTokenConfig();
		config.setTokenName("satoken");             // token名称 (同时也是cookie名称)
		config.setTimeout(30 * 24 * 60 * 60);       // token有效期，单位s 默认30天
		config.setActivityTimeout(-1);              // token临时有效期 (指定时间内无操作就视为token过期) 单位: 秒
		config.setIsConcurrent(true);               // 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录) 
		config.setIsShare(true);                    // 在多人登录同一账号时，是否共用一个token (为true时所有登录共用一个token, 为false时每次登录新建一个token) 
		config.setTokenStyle("uuid");               // token风格 
		config.setIsLog(false);                     // 是否输出操作日志 
		return config;
	}
	
}
```

方式2：
``` java
// 以代码的方式配置Sa-Token-Config 
@Autowired
public void configSaToken(SaTokenConfig config) {
	// config.setTokenName("satoken333");             // token名称 (同时也是cookie名称)
	// ... 
}
```

PS：两者的区别在于：**`方式1会覆盖yml中的配置，方式2会与yml中的配置合并`**


--- 
### 所有可配置项
| 参数名称				| 类型		| 默认值		| 说明																				|
| :--------				| :--------	| :--------	| :--------																			|
| tokenName				| String	| satoken	| token名称 (同时也是cookie名称)													|
| timeout				| long		| 2592000	| token有效期，单位/秒 默认30天，-1代表永久有效	[参考：token有效期详解](/fun/token-timeout)		|
| activityTimeout		| long		| -1		| token临时有效期 (指定时间内无操作就视为token过期) 单位: 秒, 默认-1 代表不限制 (例如可以设置为1800代表30分钟内无操作就过期) 	[参考：token有效期详解](/fun/token-timeout)													|
| isConcurrent			| Boolean	| true		| 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录)															|
| isShare				| Boolean	| true		| 在多人登录同一账号时，是否共用一个token (为true时所有登录共用一个token, 为false时每次登录新建一个token) 	|
| isReadBody			| Boolean	| true		| 是否尝试从请求体里读取token														|
| isReadHead			| Boolean	| true		| 是否尝试从header里读取token														|
| isReadCookie			| Boolean	| true		| 是否尝试从cookie里读取token														|
| tokenStyle			| String	| uuid		| token风格, [参考：花式token](/use/token-style)										|
| dataRefreshPeriod		| int		| 30		| 默认dao层实现类中，每次清理过期数据间隔的时间 (单位: 秒) ，默认值30秒，设置为-1代表不启动定时清理 		|
| tokenSessionCheckLogin	| Boolean	| true	| 获取token专属session时是否必须登录 (如果配置为true，会在每次获取token专属session时校验是否登录)		|
| autoRenew				| Boolean	| true		| 是否打开自动续签 (如果此值为true, 框架会在每次直接或间接调用getLoginId()时进行一次过期检查与续签操作)		|
| tokenPrefix			| Boolean	| true		| token前缀, 格式样例(satoken: Bearer xxxx-xxxx-xxxx-xxxx)	[参考：token前缀](/use/token-prefix) 			|
| isPrint				| Boolean	| true		| 是否在初始化配置时打印版本字符画													|
| isLog					| Boolean	| false		| 是否打印操作日志																	|
| jwtSecretKey			| String	| null		| jwt秘钥 (只有集成 sa-token-temp-jwt 模块时此参数才会生效)							|
| sso					| Object	| new SaSsoConfig()		| SSO 单点登录相关配置													|


### 单点登录相关配置 
| 参数名称		| 类型		| 默认值		| 说明																			|
| :--------		| :--------	| :--------	| :--------																		|
| ticketTimeout	| long		| 300		| Ticket有效期 (单位: 秒)														|
| allowUrl		| String	| *			| 所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket)，[详情请参考 SSO模式二的域名校验章节]		|
| secretkey		| String	| null		| 调用秘钥 (用于SSO模式三单点注销的接口通信身份校验)								|
| authUrl		| String	| null		| SSO-Server端 单点登录地址														|
| isHttp		| Boolean	| false		| 是否使用http请求校验ticket值													|
| checkTicketUrl| String	| null		| SSO-Server端 Ticket校验地址													|
| isSlo			| Boolean	| false		| 是否打开单点注销功能															|
| sloUrl		| String	| null		| SSO-Server端 单点注销地址														|
| ssoLogoutCall	| String	| null		| SSO-Client端 当前Client端的单点注销回调URL （为空时自动获取）					|

配置示例：
``` yml
# sa-token配置 
sa-token: 
    # SSO-相关配置
    sso: 
        # SSO-Server端 单点登录地址 
        auth-url: http://sa-sso-server.com:9000/ssoAuth
```