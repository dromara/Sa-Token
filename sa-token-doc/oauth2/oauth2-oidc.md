# OAuth2 开启 OIDC 协议 （OpenID Connect）

--- 

### 1、开启步骤

1、引入 `sa-token-jwt` 依赖，用来签发 `id_token` 

<!---------------------------- tabs:start ---------------------------->
<!-------- tab:Maven 方式 -------->
``` xml
<!-- sa-token-jwt 签发 OIDC id_token 令牌 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-jwt</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// sa-token-jwt 签发 OIDC id_token 令牌 
implementation 'cn.dev33:sa-token-jwt:${sa.top.version}'
```
<!---------------------------- tabs:end ---------------------------->


2、在 `SaOAuth2DataLoader` 实现类中，返回的 `SaClientModel` 中添加 `oidc` 的签约权限。

``` java
@Component
public class SaOAuth2DataLoaderImpl implements SaOAuth2DataLoader {
	@Override
	public SaClientModel getClientModel(String clientId) {
		// 此为模拟数据，真实环境需要从数据库查询 
		if("1001".equals(clientId)) {
			return new SaClientModel()
					// .... 
					.addContractScopes("openid", "userid", "userinfo", "oidc")    // 此处添加上签约权限：oidc 
					.addAllowGrantTypes(	
							// ... 
					)
			;
		}
		return null;
	}
	// 其它代码 ... 
}
```

3、在 `application.yml` 配置文件中配置 jwt 生成秘钥：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
sa-token:
	# jwt秘钥 
	jwt-secret-key: asdasdasifhueuiwyurfewbfjsdafjk
```
<!------------- tab:properties 风格  ------------->
``` properties
# jwt秘钥 
sa-token.jwt-secret-key: asdasdasifhueuiwyurfewbfjsdafjk
```
<!---------------------------- tabs:end ---------------------------->

注：为了安全起见请不要直接复制官网示例这个字符串（随便按几个字符就好了）



### 2、测试

1、在 OAuth2-Client 端申请授权码时，添加上 `oidc` 权限：
``` url
http://sa-oauth-server.com:8000/oauth2/authorize
	?response_type=code
	&client_id=1001
	&redirect_uri=http://sa-oauth-client.com:8002/
	&scope=oidc
```

2、得到授权码后，然后拿着 `code` 换 `access_token`
``` url
http://sa-oauth-server.com:8000/oauth2/token
    ?grant_type=authorization_code
    &client_id=1001
    &client_secret=aaaa-bbbb-cccc-dddd-eeee
    &code=${code}
```

3、返回的结果中将包含 `id_token` 字段：
``` js
{
	"code": 200,
	"msg": "ok",
	"data": null,
	"token_type": "bearer",
	"access_token": "WdpjZdGlXdOzsAcr7gqPwmLVInHrhpznQa2pDOVqZmLXQynBflkcWqE6f5o2",
	"refresh_token": "hKHwBm3eH6iqSHlXRGWQaziV8OoyHvzmUb97lKEEZnZJLt3NunBFx7rVZWbT",
	"expires_in": 7199,
	"refresh_expires_in": 2591999,
	"client_id": "1001",
	"scope": "oidc",
	"id_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vc2Etb2F1dGgtc2VydmVyLmNvbTo4MDAwIiwic3ViIjoiMTAwMDEiLCJhdWQiOiIxMDAxIiwiZXhwIjoxNzI0NDI1OTg5LCJpYXQiOjE3MjQ0MjUzODksImF1dGhfdGltZSI6MTcyNDQwMDUyNiwibm9uY2UiOiJLTHlNR08zZ1R0YVdhMEFRcHF0RUNpTk9SWkY1QkhvRCIsImF6cCI6IjEwMDEifQ.gP3UYMexaQ9v0huKUuqhV9-dPxPpaEuFPIlPb2UZaOI"
}
```

4、解析 `id_token` 将得到以下载荷
``` js
{
  "iss": "http://sa-oauth-server.com:8000",  // 签发人
  "sub": "10001",   // userId 
  "aud": "1001",   // clientId
  "exp": 1724425989,   // 令牌到期时间，10位时间戳 
  "iat": 1724425389,   // 签发此令牌的时间，10位时间戳 
  "auth_time": 1724400526,   // 用户认证时间，10位时间戳 
  "nonce": "KLyMGO3gTtaWa0AQpqtECiNORZF5BHoD",  // 随机数，防止重放攻击 
  "azp": "1001"   // clientId
}
```

如果默认携带的载荷无法满足你的业务需求，你还可以自定义追加扩展字段，让 `id_token` 返回更多信息 


### 3、扩展 id_token 载荷

新建 `CustomOidcScopeHandler` 集成 `OidcScopeHandler`，扩展 OIDC 权限处理器，返回更多字段：
``` java
/**
 * 扩展 OIDC 权限处理器，返回更多字段
 */
@Component
public class CustomOidcScopeHandler extends OidcScopeHandler {

    @Override
    public IdTokenModel workExtraData(IdTokenModel idToken) {
        Object userId = idToken.sub;
        System.out.println("----- 为 idToken 追加扩展字段 ----- ");

        idToken.extraData.put("uid", userId); // 用户id
        idToken.extraData.put("nickname", "lin_xiao_lin"); // 昵称
        idToken.extraData.put("picture", "https://sa-token.cc/logo.png"); // 头像
        idToken.extraData.put("email", "456456@xx.com"); // 邮箱
        idToken.extraData.put("phone_number", "13144556677"); // 手机号
        // 更多字段 ...
        // 可参考：https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims

        return idToken;
    }

}
```

重启项目，再次请求授权，返回的 `id_token` 载荷将包含更多字段：

``` js
{
  "iss": "http://sa-oauth-server.com:8000",
  "sub": "10001",
  "aud": "1001",
  "exp": 1724430149,
  "iat": 1724429549,
  "auth_time": 1724400526,
  "nonce": "SBRLOcfeo9FFmLTB8OINvuulam5FMOre",
  "azp": "1001",
  "uid": "10001",
  "nickname": "lin_xiao_lin",
  "picture": "https://sa-token.cc/logo.png",
  "email": "456456@xx.com",
  "phone_number": "13144556677"
}
```


