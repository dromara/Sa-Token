# OAuth2-自定义数据加载器



### 1、基于内存的数据加载

在之前搭建 OAuth2-Server 示例中，我们演示了 client 信息配置方案：
``` java
// Sa-Token OAuth2 定制化配置
@Autowired
public void configOAuth2Server(SaOAuth2ServerConfig oauth2Server) {
	
	// 添加 client 
	oauth2Server.addClient(
		new SaClientModel()
			.setClientId("1001")    // client id
			.setClientSecret("aaaa-bbbb-cccc-dddd-eeee")    // client 秘钥
			.addAllowRedirectUris("*")    // 所有允许授权的 url
			.addContractScopes("openid", "userid", "userinfo")    // 所有签约的权限
			.addAllowGrantTypes(	 // 所有允许的授权模式
					GrantType.authorization_code, // 授权码式
					GrantType.implicit,  // 隐式式
					GrantType.refresh_token,  // 刷新令牌
					GrantType.password,  // 密码式
					GrantType.client_credentials,  // 客户端模式
			)
	)
	
	// 可以添加更多 client 信息，只要保持 clientId 唯一就行了
	// oauth2Server.addClient(...)

}
```

你也可以在 `application.yml` 配置中 `client` 信息：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
# sa-token配置
sa-token: 
    # OAuth2.0 配置 
    oauth2-server:
		# client 列表 
        clients:
            # 客户端1
            1001:
                # 客户端id
                client-id: 1001
                # 客户端秘钥
                client-secret: aaaa-bbbb-cccc-dddd-eeee
                # 所有允许授权的 url
                allow-redirect-uris:
                  - http://sa-oauth-client.com:8002
                  - http://sa-oauth-client.com:8002/*
                # 所有签约的权限
                contract-scopes:
                  - openid
                  - userid
                  - userinfo
                # 所有允许的授权模式
                allow-grant-types:
                  - authorization_code
                  - implicit
                  - refresh_token
                  - password
                  - client_credentials
            # 客户端2
            1002:
                # 客户端id
                client-id: 1002
				# 更多配置 ...
```
<!------------- tab:properties 风格  ------------->
``` properties
########### 客户端1
# 客户端id
sa-token.oauth2-server.clients.1001.client-id=1001
# 客户端秘钥
sa-token.oauth2-server.clients.1001.client-secret=aaaa-bbbb-cccc-dddd-eeee
# 所有允许授权的 url
sa-token.oauth2-server.clients.1001.allow-redirect-uris[0]=http://sa-oauth-client.com:8002
sa-token.oauth2-server.clients.1001.allow-redirect-uris[1]=http://sa-oauth-client.com:8002/*
# 所有签约的权限
sa-token.oauth2-server.clients.1001.contract-scopes[0]=openid
sa-token.oauth2-server.clients.1001.contract-scopes[1]=userid
sa-token.oauth2-server.clients.1001.contract-scopes[2]=userinfo
# 所有允许的授权模式
sa-token.oauth2-server.clients.1001.allow-grant-types[0]=authorization_code
sa-token.oauth2-server.clients.1001.allow-grant-types[1]=implicit
sa-token.oauth2-server.clients.1001.allow-grant-types[2]=refresh_token
sa-token.oauth2-server.clients.1001.allow-grant-types[3]=password
sa-token.oauth2-server.clients.1001.allow-grant-types[4]=client_credentials

########### 客户端2
sa-token.oauth2-server.clients.1002.client-id=1002
sa-token.oauth2-server.clients.1002.client-secret=...
```
<!---------------------------- tabs:end ---------------------------->


这两种方案都是基于内存形式的 client 信息配置，只适合简单的测试，一般真实项目的 client 信息都是保存在数据库中的，下面演示一下如何在数据库中动态获取 client 信息


### 2、基于数据库的数据加载

你只需要自定义数据加载器：新建 `SaOAuth2DataLoaderImpl` 实现 `SaOAuth2DataLoader` 接口。


``` java
/**
 * Sa-Token OAuth2：自定义数据加载器
 */
@Component
public class SaOAuth2DataLoaderImpl implements SaOAuth2DataLoader {
	
	// 根据 clientId 获取 Client 信息
	@Override
	public SaClientModel getClientModel(String clientId) {
		// 此为模拟数据，真实环境需要从数据库查询 
		if("1001".equals(clientId)) {
			return new SaClientModel()
					.setClientId("1001")    // client id
					.setClientSecret("aaaa-bbbb-cccc-dddd-eeee")    // client 秘钥
					.addAllowRedirectUris("*")    // 所有允许授权的 url
					.addContractScopes("openid", "userid", "userinfo")    // 所有签约的权限
					.addAllowGrantTypes(	 // 所有允许的授权模式
							GrantType.authorization_code, // 授权码式
							GrantType.implicit,  // 隐式式
							GrantType.refresh_token,  // 刷新令牌
							GrantType.password,  // 密码式
							GrantType.client_credentials  // 客户端模式
					)
			;
		}
		return null;
	}
	
	// 根据 clientId 和 loginId 获取 openid
	@Override
	public String getOpenid(String clientId, Object loginId) {
		// 此处使用框架默认算法生成 openid，真实环境建议改为从数据库查询
		return SaOAuth2DataLoader.super.getOpenid(clientId, loginId);
	}

}
``` 

此种形式更加灵活，后续文档将默认按照此种形式来展示示例。


### 3、自定义 openid 生成算法

openid 是用户在某一 client 下的唯一标识，其有如下特点：

- 一个用户在同一个 client 下，openid 是固定的，每次请求都会返回相同的值。
- 一个用户在不同的 client 下，openid 是不同的，会返回不同的值。

oauth2-client 在每次授权时可根据返回的 openid 值来确定用户身份。

框架默认的 openid 生成算法为：
``` java
md5(prefix + "_" + clientId + "_" + loginId);
```

其中的 prefix 前缀默认值为：`openid_default_digest_prefix`，你可以通过以下方式配置：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
# sa-token配置
sa-token:
	oauth2-server:
		# 默认 openid 生成算法中使用的摘要前缀
		openid-digest-prefix: xxxxxx
```
<!------------- tab:properties 风格  ------------->
``` properties
# 默认 openid 生成算法中使用的摘要前缀
sa-token.oauth2-server.openid-digest-prefix=xxxxxx
```
<!---------------------------- tabs:end ---------------------------->

正常来讲，openid 算法需要保证：

1. 单个 clientId 下同一 loginId 生成的 `openid` 一致。[必须]
2. 多个 clientId 下同一 loginId 生成的 `openid` 不一致。[非常建议]
3. 客户端无法通过 clientId + loginId 推测 `openid` 值。[建议]
4. 客户端无法通过 clientId + loginId + openid 推测该 loginId 在其它 clientId 下的 `openid` 值。[建议]
5. oauth2-server 自身由 `openid` 可以反查出对应的 clientId 和 loginId。[根据业务需求而定是否满足]

框架内置的算法，可以满足 1和2，如果自定义了 `sa-token.oauth2-server.openid-digest-prefix` 配置，可以满足3。

如果自定义配置的 prefix 长度较短，或比较简单呈现规律性，则有客户端根据 clientId + loginId + openid 穷举爆破出 `prefix` 的风险，
从而获得提前计算彩虹表来推测出其它 clientId、loginId 对应 openid 值的能力。

如果自定义的 prefix 前缀比较复杂，让客户端无法爆破，则可以满足4。但依然无法满足5。

所以 openid 算法的最优解，应该是 oauth2-server 采用随机字符串作为 openid，然后自建数据库表来维护其映射关系，这样可以同时满足12345。

表结构参考如下：

- id：数据id，主键。
- client_id：应用id。
- user_id：用户账号id。
- openid：对应的 openid 值，随机字符串。
- create_time：数据创建时间。
- xxx：其它需要扩展的字段。
