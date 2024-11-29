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


