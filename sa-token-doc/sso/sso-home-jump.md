# SSO 平台中心跳转模式，点连接跳入子系统

--- 

有的时候，我们需要把 sso-server 搭建成一个平台中心，效果图大致如下：

![sso-home-jump.png](https://oss.dev33.cn/sa-token/doc/sso/sso-home-jump.png 's-w-sh')

如图所示，用户先从 sso-server 登录进入平台首页，在首页上有各个子系统的进入链接，用户点击链接进入子系统（免登录）。

怎么做到如上效果呢？当然，加个超链接跳到子系统并不难，难点在于跳转的同时我们需要让用户自动登录上子系统，从而达到：平台中心一处登录，所有子系统无障碍通行的效果。

怎么做到跳转的时候自动登录呢？直接跳转肯定是不会自动登录的，我们需要对链接改造一下：

假设子系统的地址是：

``` url
http://sa-sso-client1.com:9003/
```

那么我们改造后的地址就是：

``` url
/sso/auth?client=sso-client3&redirect=http://sa-sso-client1.com:9003/sso/login?back=http://sa-sso-client1.com:9003/
```

格式形如：`/sso/auth?client={client标识}&redirect=${子系统首页}/sso/login?back=${子系统首页}`

--- 

### 完整代码示例：

1、在 sso-server 中配置 `home-route` 字段：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
# Sa-Token 配置
sa-token:
    # SSO-Server 配置
    sso-server:
        # 主页路由：在 /sso/auth 登录页不指定 redirect 参数时，默认跳转的地址
        home-route: /home
```
<!------------- tab:properties 风格  ------------->
``` properties
# 主页路由：在 /sso/auth 登录页不指定 redirect 参数时，默认跳转的地址
sa-token.sso-server.home-route: /home
```
<!---------------------------- tabs:end ---------------------------->



2、在 sso-server 中添加 `HomeController`，作为平台中心首页：

``` java
/**
 * SSO 平台中心模式示例，跳连接进入子系统 
 */
@RestController
public class HomeController {
	// 平台化首页
    @RequestMapping({"/", "/home"})
    public Object index() {
        // 如果未登录，则先去登录
        if(!StpUtil.isLogin()) {
            return SaHolder.getResponse().redirect("/sso/auth");
        }

        // 拼接各个子系统的地址，格式形如：/sso/auth?client=xxx&redirect=${子系统首页}/sso/login?back=${子系统首页}
        String link1 = "/sso/auth?client=sso-client3&redirect=http://sa-sso-client1.com:9003/sso/login?back=http://sa-sso-client1.com:9003/";
        String link2 = "/sso/auth?client=sso-client3&redirect=http://sa-sso-client2.com:9003/sso/login?back=http://sa-sso-client2.com:9003/";
        String link3 = "/sso/auth?client=sso-client3&redirect=http://sa-sso-client3.com:9003/sso/login?back=http://sa-sso-client3.com:9003/";

        // 组织网页结构返回到前端
        String title = "<h2>SSO 平台首页 (平台中心模式)</h2>";
        String client1 = "<p><a href='" + link1 + "' target='_blank'> 进入Client1系统 </a></p>";
        String client2 = "<p><a href='" + link2 + "' target='_blank'> 进入Client2系统 </a></p>";
        String client3 = "<p><a href='" + link3 + "' target='_blank'> 进入Client3系统 </a></p>";

        return title + client1 + client2 + client3;
    }
}
```


### 测试访问

启动项目，访问：[http://sa-sso-server.com:9000](http://sa-sso-server.com:9000)

首次访问，因为我们没有登录，所以会被重定向到 `/sso/auth` 登录页，我们登录上之后，便会跳转到平台中心首页：

![sso-home-jump-do.png](https://oss.dev33.cn/sa-token/doc/sso/sso-home-jump-do.png 's-w-sh')

依次点击三个链接，便可在跳转的同时自动登录上子系统。
