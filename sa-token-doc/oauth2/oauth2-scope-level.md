# OAuth2 - 为 Scope 划分等级


### 1、划分等级 

我们可以通过配置文件来为 scope 划分等级

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
# sa-token 配置
sa-token: 
    # OAuth2.0 配置 
    oauth2-server:
        # 定义哪些 scope 是高级权限，多个用逗号隔开
        higher-scope: openid,userid
        # 定义哪些 scope 是低级权限，多个用逗号隔开
        lower-scope: userinfo
```
<!------------- tab:properties 风格  ------------->
``` properties
# 定义哪些 scope 是高级权限，多个用逗号隔开
sa-token.oauth2-server.higher-scope=openid,userid
# 定义哪些 scope 是低级权限，多个用逗号隔开
sa-token.oauth2-server.lower-scope=userinfo
```
<!---------------------------- tabs:end ---------------------------->

如上所示：
- 通过 `sa-token.oauth2-server.higher-scope` 配置项指定的 `scope` 将变成 **高级权限**。
- 通过 `sa-token.oauth2-server.lower-scope` 配置项指定的 `scope` 将变成 **低级权限**。
- 其它未指定的 `scope` 将默认为 **一般权限**。

不同的权限等级其差异主要表现在：oauth2-client 授权时是否需要用户手动确认授权。

| 权限等级		| 申请授权时表现						| 
| :--------		| :--------							| 
| 高级权限		| 申请授权时：每次都需要用户手动点击确认授权按钮，才会下放 code 授权码	| 
| 一般权限		| 申请授权时：如果申请的 scope 用户近期授权过，则静默授权，如果近期未授权过，则需要手动点击确认授权按钮		| 
| 低级权限		| 申请授权时：不需要用户手动点击确认授权，程序自动完成静默授权						| 


### 2、详细举例

1、如下例子，oauth2-client 申请的 `openid` 权限为**高级权限**，每次都需要用户手动点击确认授权按钮，才会下放 code 授权码。

``` url
http://{host}:{port}/oauth2/authorize
	?response_type=code
	&client_id=1001
	&redirect_uri=http://sa-oauth-client.com:8002/
	&scope=openid
```

2、如下例子，oauth2-client 申请的 `userinfo` 权限为**低级权限**，此时不需要用户手动点击确认授权，程序自动完成静默授权。

``` url
http://{host}:{port}/oauth2/authorize
	?response_type=code
	&client_id=1001
	&redirect_uri=http://sa-oauth-client.com:8002/
	&scope=userinfo
```

3、如下例子，oauth2-client 申请的 `fans_list` 权限为**一般权限**，首次申请时，需要用户手动点击确认授权，第二次再申请则是静默授权。

``` url
http://{host}:{port}/oauth2/authorize
	?response_type=code
	&client_id=1001
	&redirect_uri=http://sa-oauth-client.com:8002/
	&scope=fans_list
```

4、如下例子，oauth2-client 申请的 `openid,userid,userinfo,fans_list` 权限同时包括 **高级权限**、**低级权限**、**一般权限**：

``` url
http://{host}:{port}/oauth2/authorize
	?response_type=code
	&client_id=1001
	&redirect_uri=http://sa-oauth-client.com:8002/
	&scope=openid,userid,userinfo,fans_list
```

此时是否需要用户手动点击确认授权按钮？具体规则表现为：
- 如果请求的 scope 列表包括高级权限，则必须用户手动点击确认授权。
- 如果 scope 列表不包括高级权限，则将 scope 列表中的所有低级权限剔除。
- 剔除后的 list 大小如果为零，则直接静默授权通过。
- 剔除后的 list 大小如果不为零，则判断剩余的这些 scope 是否全部已近期授权过：
	- 如果是，则静默授权。
	- 如果否，则需要用户手动点击确认授权。


### 3、申请高级权限时 `/oauth2/authorize` 无法通过验证

由于申请高级权限时，每次都必须用户手动点击确认授权，`/oauth2/authorize` 路由接口是无法完成权限验证操作的。

此时需要将构建 `redirect_uri` 的动作提前，在 `/oauth2/doConfirm` 确认授权接口时额外追加 `build_redirect_uri: true` 等参数：
``` url
http://{host}:{port}/oauth2/doConfirm
    ?client={value}
    &scope={value}
    &build_redirect_uri=true
    &response_type={value}
    &redirect_uri={value}
    &state={value}
```

返回结果示例：
``` js
{
	code: 200, 
	msg: 'ok', 
	data: null,
	redirect_uri: 'http://sa-oauth-client.com:8002/?code=n12TTc1M9REfJVqKm0wewDz0tNZDBhE1A90irOJmxD0zb92pdhUK8NghJfuC'
}
```

其中 `redirect_uri` 参数为授权挂载code地址，直接在 ajax 回调函数中使用 `location.href=res.redirect_uri` 跳转即可。

自定义确认授权视图修改参考：
``` java
// 授权确认视图
cfg.confirmView = (clientId, scopes)->{
	String scopeStr = SaFoxUtil.convertListToString(scopes);
	String yesCode =
			"fetch('/oauth2/doConfirm' + location.search + '&build_redirect_uri=true', {method: 'POST'})" +
					".then(res => res.json())" +
					".then(res => location.href=res.redirect_uri)";
	String res = "<p>应用 " + clientId + " 请求授权：" + scopeStr + "，是否同意？</p>"
			+ "<p>" +
			"		<button onclick=\"" + yesCode + "\">同意</button>" +
			"		<button onclick='history.back()'>拒绝</button>" +
			"</p>";
	return res;
};
```