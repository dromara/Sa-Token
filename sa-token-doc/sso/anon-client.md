# 匿名 Client 接入

匿名 Client 就是指在客户端没有配置 `sso-client` 的应用，没有一个明确的 “Client” 标识名称。

匿名 Client 在一些关键步骤中不会构建 `client` 参数，如：“重定向至认证中心授权地址”、“校验 ticket”、“单点注销” 等。

要想匿名 client 接入，你需要做一些特殊配置。


### 1、在 sso-server 端开启匿名 client 接入

开启方式一，通过配置项方式：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
# Sa-Token 配置
sa-token:
    # SSO-Server 配置
    sso-server:
        # 是否启用匿名 client (开启匿名 client 后，允许客户端接入时不提交 client 参数)
        allow-anon-client: true
        # 所有允许的授权回调地址 (匿名 client 使用)
        allow-url: "*"
        # API 接口调用秘钥 (全局默认 + 匿名 client 使用)
        secret-key: kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
```
<!------------- tab:properties 风格  ------------->
``` properties
# SSO-Server 配置
# 是否启用匿名 client (开启匿名 client 后，允许客户端接入时不提交 client 参数)
sa-token.sso-server.allow-anon-client=true
# 所有允许的授权回调地址 (匿名 client 使用)
sa-token.sso-server.allow-url=*
# API 接口调用秘钥 (全局默认 + 匿名 client 使用)
sa-token.sso-server.secret-key=kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
```
<!---------------------------- tabs:end ---------------------------->


开启方式二，通过代码重写方式：

``` java
/**
 * 重写 SaSsoServerTemplate 部分方法，增强功能
 */
@Component
public class CustomSaSsoServerTemplate extends SaSsoServerTemplate {

    /**
     * 获取配置项：是否允许匿名 client 接入
     */
    @Override
    public boolean getConfigOfAllowAnonClient() {
        return true;
    }

    /**
     * 获取匿名 client 配置信息
     */
    @Override
    public SaSsoClientModel getAnonClient() {
        SaSsoClientModel scm = new SaSsoClientModel();
        scm.setAllowUrl("*");  // 允许的授权地址
        scm.setIsSlo(true);  // 是否允许单点注销
        scm.setSecretKey("kQwIOrYvnXmSDkwEiFngrKidMcdrgKor");  // 客户端密钥
        return scm;
    }
}
```


### 2、在 sso-server 端开启匿名 client 接入

然后在对应的应用端不要配置 client 字段，例如：

``` yml
# sa-token配置 
sa-token:
    # 配置一个不同的 token-name，以避免在和模式三 demo 一起测试时发生数据覆盖
    token-name: satoken-client-anon
    # sso-client 相关配置
    sso-client:
        # client 标识 匿名应用就是指不配置 client 标识的应用
        # client: sso-client3
        # sso-server 端主机地址
        server-url: http://sa-sso-server.com:9000
        # 使用 Http 请求校验ticket (模式三)
        is-http: true
        # 是否在登录时注册单点登录回调接口 (匿名应用想要参与单点注销必须打开这个)
        reg-logout-call: true
        # API 接口调用秘钥
        secret-key: kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
```


> [!TIP| label:demo] 
> 匿名 Client 接入的 Demo 示例地址：[sa-token-demo-sso3-client-anon](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso3-client-anon)

这里有个值得注意的配置项：`reg-logout-call: true`，是干嘛的？

简单来讲，就是匿名应用不包含 client 字段信息，因此 sso-server 端也无法配置此 client 的消息推送地址，所以此 client 无法接受到消息推送，也就无法参与到单点注销的环路中来。

因此，新增一个配置项 `reg-logout-call: true`，代表在登录的同时把当前项目的单点注销回调地址 `/sso/logoutCall` 发送到 sso-server 端，
这样 sso-server 端有了备案，也就可以成功通知此应用发起单点注销掉了。

如果当前应用不需要单点注销可以不配置此字段。

