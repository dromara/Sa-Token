# OAuth2-自定义权限处理器 


### 1、需求场景

OAuth2 协议的 `/oauth2/token` 接口定义了两种获取 `access_token` 的 `grant_type`，分别是：
- `authorization_code`：使用用户授权的授权码获取 access_token。
- `password`：使用用户提交的账号、密码来获取 access_token。

除此之外，你还可以自定义 `grant_type`，来支持更多的场景。

假设有以下需求：通过 手机号+验证码 登录，返回 `access_token`。

--- 


### 2、实现步骤

#### 2.1、新增验证码发送接口

首先在 oauth2-server 端开放一个接口，为指定手机号发送验证码。

``` java
/**
 * 自定义手机登录接口
 */
@RestController
public class PhoneLoginController {

    @RequestMapping("/oauth2/sendPhoneCode")
    public SaResult sendCode(String phone) {
        String code = SaFoxUtil.getRandomNumber(100000, 999999) + "";
        SaManager.getSaTokenDao().set("phone_code:" + phone, code, 60 * 5);
        System.out.println("手机号：" + phone + "，验证码：" + code + "，已发送成功");
        return SaResult.ok("验证码发送成功");
    }

}
```

真实项目肯定是要对接短信服务商的，此处我们仅做模拟代码，将发送的验证码打印在控制台上。


#### 2.2、自定义 grant_type 处理器

在 oauth2-server 新建 `PhoneCodeGrantTypeHandler` 实现 `SaOAuth2GrantTypeHandlerInterface` 接口：

``` java
/**
 * 自定义 phone_code 授权模式处理器 
 */
@Component
public class PhoneCodeGrantTypeHandler implements SaOAuth2GrantTypeHandlerInterface {

    @Override
    public String getHandlerGrantType() {
        return "phone_code";
    }

    @Override
    public AccessTokenModel getAccessToken(SaRequest req, String clientId, List<String> scopes) {

        // 获取前端提交的参数 
        String phone = req.getParamNotNull("phone");
        String code = req.getParamNotNull("code");
        String realCode = SaManager.getSaTokenDao().get("phone_code:" + phone);

        // 1、校验验证码是否正确
        if(!code.equals(realCode)) {
            throw new SaOAuth2Exception("验证码错误");
        }

        // 2、校验通过，删除验证码
        SaManager.getSaTokenDao().delete("phone_code:" + phone);

        // 3、登录
        long userId = 10001; // 模拟 userId，真实项目应该根据手机号从数据库查询

        // 4、构建 ra 对象
        RequestAuthModel ra = new RequestAuthModel();
        ra.clientId = clientId;
        ra.loginId = userId;
        ra.scopes = scopes;

        // 5、生成 Access-Token
        AccessTokenModel at = SaOAuth2Manager.getDataGenerate().generateAccessToken(ra, true, atm -> atm.grantType = "phone_code");
        return at;
    }
}
```

#### 2.3、为应用添加允许的授权类型

在 `SaOAuth2DataLoader` 实现类中，为 client 的允许授权类型增加自定义的 `phone_code` 

``` java
// Sa-Token OAuth2：自定义数据加载器 
@Component
public class SaOAuth2DataLoaderImpl implements SaOAuth2DataLoader {
	@Override
	public SaClientModel getClientModel(String clientId) {
		if("1001".equals(clientId)) {
			return new SaClientModel()
					.setClientId("1001")  
					.setClientSecret("aaaa-bbbb-cccc-dddd-eeee")  
					.addAllowRedirectUris("*")    // 所有允许授权的 url
					.addContractScopes("openid", "userid", "userinfo")  
					.addAllowGrantTypes( 
							GrantType.authorization_code, 
							GrantType.implicit, 
							GrantType.refresh_token, 
							GrantType.password, 
							GrantType.client_credentials, 
							"phone_code"  // 重要代码：自定义授权模式 手机号验证码登录
					)
			;
		}
		return null;
	}
	// 其它代码 ... 
}
```

完工，开始测试。


### 3、测试步骤

#### 1、先发送验证码 

``` url
http://sa-oauth-server.com:8000/oauth2/sendPhoneCode?phone=13144556677
```

#### 2、请求 token  

注意 `grant_type` 要填写我们自定义的 `phone_code`，code 的具体值可以在后端的控制台上看到 

``` url
http://sa-oauth-server.com:8000/oauth2/token
    ?grant_type=phone_code
    &client_id=1001
    &client_secret=aaaa-bbbb-cccc-dddd-eeee
	&scope=openid
    &phone=13144556677
	&code={value}
```

返回结果参考如下：

``` js
{
    "code": 200,
    "msg": "ok",
    "data": null,
    "token_type": "bearer",
    "access_token": "pfxRz6KVacwvKNu4IHmDsCJs33kvvARs2z1lTch7stog8nRt6rfVLowtAZ0E",
    "refresh_token": "qcFD6Wo2qZidofXQtWF5oK5ML6ljHKufQ5SbouBxzGnHhnMjUG4VV0iXZhdE",
    "expires_in": 7199,
    "refresh_expires_in": 2591999,
    "client_id": "1001",
    "scope": "openid",
    "openid": "ded91dc189a437dd1bac2274be167d50"
}
```










