# OAuth2-自定义 Scope 权限及处理器

--- 

### 1、需求场景
一般情况下，对于第三方 oauth2-client 来讲，仅仅拿到用户的 access_token 是不够的，还需要拿到更多的信息，比如用户昵称、头像等资料。

sa-token-oauth2 提供两种模式，让 access_token 可以得到更多信息。

- 自定义接口模式：在 oauth2-server 端开放一个资料查询接口，在 oauth2-client 得到 `access_token` 后，再次调用这个接口来获取 `userinfo` 信息。
- 自定义权限处理器模式：自定义一个 `ScopeHandler`，直接在返回 `access_token` 时追加字段，将 `userinfo` 信息和 `access_token` 一并返回到 oauth2-client。


### 2、自定义接口模式

#### 1、新建查询接口

在 oauth2-server 新建接口，查询指定 `access_token` 代表的 `userId` 其 `userinfo`：

``` java
// 获取 userinfo 信息：昵称、头像、性别等等
@RequestMapping("/oauth2/userinfo")
public SaResult userinfo() {
	// 获取 Access-Token 对应的账号id
	String accessToken = SaOAuth2Manager.getDataResolver().readAccessToken(SaHolder.getRequest());
	Object loginId = SaOAuth2Util.getLoginIdByAccessToken(accessToken);
	System.out.println("-------- 此Access-Token对应的账号id: " + loginId);
	
	// 校验 Access-Token 是否具有权限: userinfo
	SaOAuth2Util.checkScope(accessToken, "userinfo");
	
	// 模拟账号信息 （真实环境需要查询数据库获取信息）
	Map<String, Object> map = new LinkedHashMap<>();
	// map.put("userId", loginId);  一般原则下，oauth2-server 不能把 userId 返回给 oauth2-client
	map.put("nickname", "林小林");
	map.put("avatar", "http://xxx.com/1.jpg");
	map.put("age", "18");
	map.put("sex", "男");
	map.put("address", "山东省 青岛市 城阳区");
	return SaResult.ok().setMap(map);
}
```


#### 2、申请 code 时指定权限
oauth2-client 申请 `code` 时，一定需要加上 `userinfo` 权限

``` url
http://sa-oauth-server.com:8000/oauth2/authorize
	?response_type=code
	&client_id=1001
	&redirect_uri=http://sa-oauth-client.com:8002/
	&scope=userinfo
```


#### 3、code 换 access_token
访问上述链接后，得到 `code` 授权码，然后我们拿着 `code` 换 `access_token`

``` url
http://sa-oauth-server.com:8000/oauth2/token
    ?grant_type=authorization_code
    &client_id=1001
    &client_secret=aaaa-bbbb-cccc-dddd-eeee
    &code=${code}
```

#### 4、access_token 取 userinfo 
使用返回的 `access_token` 再次访问接口 `/oauth2/userinfo`

``` url
http://sa-oauth-server.com:8000/oauth2/userinfo?access_token=${access_token}
```

返回以下结果：
``` js
{
  "code": 200,
  "msg": "ok",
  "data": null,
  "nickname": "林小林",
  "avatar": "http://xxx.com/1.jpg",
  "age": "18",
  "sex": "男",
  "address": "山东省 青岛市 城阳区"
}
```

拿到 userinfo。



### 3、自定义权限处理器模式

#### 1、新建权限处理器
在 oauth2-server 新建 `UserinfoScopeHandler.java` 实现 `SaOAuth2ScopeHandlerInterface` 接口：

``` java
@Component
public class UserinfoScopeHandler implements SaOAuth2ScopeHandlerInterface {

    @Override
    public String getHandlerScope() {
        return "userinfo";
    }

    @Override
    public void workAccessToken(AccessTokenModel at) {
        System.out.println("--------- userinfo 权限，加工 AccessTokenModel --------- ");
        // 模拟账号信息 （真实环境需要查询数据库获取信息）
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("userId", "10008");
        map.put("nickname", "shengzhang_");
        map.put("avatar", "http://xxx.com/1.jpg");
        map.put("age", "18");
        map.put("sex", "男");
        map.put("address", "山东省 青岛市 城阳区");
        at.extraData.putAll(map);
    }

    @Override
    public void workClientToken(ClientTokenModel ct) {
    }

}
```

如上所述，所有写入到 `extraData` 中的数据，都将追加返回到 oauth2-client 端。


#### 2、申请 code 时指定权限
oauth2-client 申请 `code` 时，一定需要加上 `userinfo` 权限
``` url
http://sa-oauth-server.com:8000/oauth2/authorize
	?response_type=code
	&client_id=1001
	&redirect_uri=http://sa-oauth-client.com:8002/
	&scope=userinfo
```


#### 3、code 换 access_token
访问上述链接后，得到 `code` 授权码，然后我们拿着 `code` 换 `access_token`
``` url
http://sa-oauth-server.com:8000/oauth2/token
    ?grant_type=authorization_code
    &client_id=1001
    &client_secret=aaaa-bbbb-cccc-dddd-eeee
    &code=${code}
```

返回结果如下
``` js
{
  "code": 200,
  "msg": "ok",
  "data": null,
  "token_type": "bearer",
  "access_token": "LQ24xI0hX25vIzvciHPA0PNsnGCweSFM1Bzl8783li07VAXpw8sEfn9xsta2",
  "refresh_token": "rKB8mby1Mw8yZXHbWzliHx6lmatcLcULLw5C5cUMBhMMRx72DFg5u0owZgrA",
  "expires_in": 7199,
  "refresh_expires_in": 2591999,
  "client_id": "1001",
  "scope": "openid,userid,userinfo",
  "userinfo": {
    "userId": "10008",
    "nickname": "shengzhang_",
    "avatar": "http://xxx.com/1.jpg",
    "age": "18",
    "sex": "男",
    "address": "山东省 青岛市 城阳区"
  }
}
```

拿到 userinfo。

#### 总结
相比于自定义接口模式，自定义权限处理器模式可以少一次网络请求，让 oauth2-client 端提前拿到 `userinfo` 信息。



### 4、最终权限处理器
当一个自定义权限处理器，监听的 scope 字符串为 `_FINALLY_WORK_SCOPE` 时，则代表这个权限处理器为“最终权限处理器”。

最终权限处理器会永远在所有权限处理器工作完成之后执行一次，即使 oauth2-client 端没有申请任何 scope，最终权限处理器也会固定执行。

示例：
``` java
/**
 * 最终权限处理器：在所有权限处理器工作完成之后，执行此权限处理器
 */
@Component
public class FinallyWorkScopeHandler implements SaOAuth2ScopeHandlerInterface {

    @Override
    public String getHandlerScope() {
        return SaOAuth2Consts._FINALLY_WORK_SCOPE;
    }

    @Override
    public void workAccessToken(AccessTokenModel at) {
        // 在所有权限处理器工作完成之后，执行此处方法加工 AccessToken
        // System.out.println(123);
    }

    @Override
    public void workClientToken(ClientTokenModel ct) {
        // System.out.println(456);
    }
}
```










