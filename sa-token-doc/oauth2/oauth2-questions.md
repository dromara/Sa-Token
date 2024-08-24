# Sa-Token-OAuth2整合-常见问题总结

OAuth2 集成常见问题整理

[[toc]]

--- 


### 问：搭建好 oauth2-server 服务后，访问返回：`{"msg": "not handle"}`。

返回这个信息，代表你访问的路由有错误，比如说：

- 统一认证登录地址是：`http://{host}:{port}/oauth2/authorize`。
- 而你访问的却是：`http://{host}:{port}/oauth2/authorize2`。

地址写错了，框架就不会处理这个请求，会直接返回 `{"msg": "not handle"}`，所有开放地址可参考：[SSO 开放接口](/oauth2/oauth2-apidoc)

如果仔细检查地址后没有写错，却依然返回了这个信息，那有可能是对应的接口没有打开，比如说：

- sso-server 端的单点注销地址：`http://{host}:{port}/sso/signout`；
- sso-client 端的注销地址：`http://{host}:{port}/sso/logout`；

都需要在配置文件配置：`sa-token.sso.is-slo=true`后，才会打开。



### 问：我参照文档搭建 oauth2-server，一直提示：code 无效，请问怎么回事？
一个 code 码只能使用一次，多次使用就会报这个错。





### 问：Sa-Token-OAuth2 怎么集成多账号模式？

在 `configOAuth2Server` 里指定 oauth2 模块使用的 `StpLogic` 对象即可： 

``` java
// Sa-Token OAuth2 定制化配置
@Autowired
public void configOAuth2Server(SaOAuth2ServerConfig oauth2Server) {
	// 其它配置 ... 
	
	// 指定 oauth2 模块使用的 `StpLogic` 对象 
	SaOAuth2Manager.setStpLogic(StpUserUtil.stpLogic);
}
```