# SSO模式一 共享Cookie同步会话

如果我们的多个系统可以做到：前端同域、后端同Redis，那么便可以使用 **`[共享Cookie同步会话]`** 的方式做到单点登录。

--- 

### 0、解决思路？

首先我们分析一下多个系统之间，为什么无法同步登录状态？
1. 前端的`Token`无法在多个系统下共享。
2. 后端的`Session`无法在多个系统间共享。

所以单点登录第一招，就是对症下药：
1. 使用`共享Cookie`来解决Token共享问题。
2. 使用`Redis`来解决Session共享问题。

所谓共享Cookie，就是主域名Cookie在二级域名下的共享，举个例子：写在父域名`stp.com`下的Cookie，在`s1.stp.com`、`s2.stp.com`等子域名都是可以共享访问的。

而共享Redis，并不需要我们把所有项目的数据都放在同一个Redis中，Sa-Token提供了 **[权限缓存与业务缓存分离]** 的解决方案，详情戳：[Alone独立Redis插件](/plugin/alone-redis)。

OK，所有理论就绪，下面开始实战：

> Sa-Token整合同域单点登录非常简单，相比于正常的登录，你只需增加配置 `sa-token.cookie-domain=xxx.com` 指定一下Cookie写入时的父级域名即可。 <br>
> 整合示例在官方仓库的 `/sa-token-demo/sa-token-demo-sso1/`，如遇到难点可结合源码进行测试学习。



### 1、准备工作

首先修改hosts文件`(C:\windows\system32\drivers\etc\hosts)`，添加以下IP映射，方便我们进行测试：
``` url
127.0.0.1 sso.stp.com
127.0.0.1 s1.stp.com
127.0.0.1 s2.stp.com
127.0.0.1 s3.stp.com
```

<!-- 其中：`sso.stp.com`为统一认证地址，当用户在其它 Client 端发起登录请求时，均将其重定向至认证中心，待到登录成功之后再原路返回到 Client 端。 -->
其中：`sso.stp.com`为统一认证地址，其它均为 Client 端。


### 2、指定Cookie的作用域
在`s1.stp.com`访问服务器，其Cookie也只能写入到`s1.stp.com`下，为了将Cookie写入到其父级域名`stp.com`下，我们需要新增配置：
``` yml
sa-token:
	# 写入Cookie时显式指定的作用域, 用于单点登录二级域名共享Cookie
	cookie-domain: stp.com
```

### 3、新增测试Controller
新建`SsoController.java`控制器，写入代码：
``` java
/**
 * 测试: 同域单点登录
 * @author kong
 */
@RestController
@RequestMapping("/sso/")
public class SsoController {

	// 测试：进行登录
	@RequestMapping("doLogin")
	public SaResult doLogin(@RequestParam(defaultValue = "10001") String id) {
		System.out.println("---------------- 进行登录 ");
		StpUtil.login(id);
		return SaResult.ok("登录成功: " + id);
	}

	// 测试：是否登录
	@RequestMapping("isLogin")
	public SaResult isLogin() {
		System.out.println("---------------- 是否登录 ");
		boolean isLogin = StpUtil.isLogin();
		return SaResult.ok("是否登录: " + isLogin);
	}

}
```

``` java
// 启动类
@SpringBootApplication
public class SaSsoApplication {
	public static void main(String[] args) {
		SpringApplication.run(SaSsoApplication.class, args);
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}
}
```


### 4、访问测试
启动项目，依次访问：
- [http://s1.stp.com:8081/sso/isLogin](http://s1.stp.com:8081/sso/isLogin)
- [http://s2.stp.com:8081/sso/isLogin](http://s2.stp.com:8081/sso/isLogin)
- [http://s3.stp.com:8081/sso/isLogin](http://s3.stp.com:8081/sso/isLogin)

均返回以下结果：

![sso-type1-wd.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type1-wd.png 's-w-sh')

现在访问SSO认证中心的登录接口：[http://sso.stp.com:8081/sso/doLogin](http://sso.stp.com:8081/sso/doLogin) 

![sso-type1-login.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type1-login.png 's-w-sh')

然后再次刷新上面三个测试接口，均可以得到以下结果：

![sso-type1-yd.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type1-yd.png 's-w-sh')

测试完毕！


### 5、完善统一认证中心

上面的示例，我们简单的演示了SSO模式一的认证原理。

当然，在实际的正式项目中，我们肯定不会每个 Client 端都内置一个登录接口，一般的做法是只在SSO认证中心保留登录接口，我们所有 Client 端的登录请求都会被重定向至认证中心，
待到登录成功之后再原路返回到 Client 端。

我们可以运行一下官方仓库的示例，里面有制作好的登录页面

> 下载官方示例，依次运行：
> - `/sa-token-demo/sa-token-demo-sso1-server/`
> - `/sa-token-demo/sa-token-demo-sso1-client/`


访问三个应用端：
- [http://s1.stp.com:9001/](http://s1.stp.com:9001/)
- [http://s2.stp.com:9001/](http://s2.stp.com:9001/)
- [http://s3.stp.com:9001/](http://s3.stp.com:9001/)

均返回：

![sso1--index.png](https://oss.dev33.cn/sa-token/doc/sso/sso1--index.png 's-w-sh')

然后点击登录，被重定向至SSO认证中心：

![sso1--login-page.png](https://oss.dev33.cn/sa-token/doc/sso/sso1--login-page.png 's-w-sh')

输入默认测试账号：`sa / 123456`，点击登录 

![sso1-login-ok.png](https://oss.dev33.cn/sa-token/doc/sso/sso1-login-ok.png 's-w-sh')

刷新另外两个Client端，均显示已登录 

![sso1-login-ok2.png](https://oss.dev33.cn/sa-token/doc/sso/sso1-login-ok2.png 's-w-sh')

测试完成 


### 6、跨域模式下的解决方案 

如上，我们使用极其简单的步骤实现了同域下的单点登录，聪明如你😏，马上想到了这种模式有着一个不小的限制：

> 所有子系统的域名，必须同属一个父级域名

如果我们的子系统在完全不同的域名下，我们又该怎么完成单点登录功能呢？

且往下看，[SSO模式二：URL重定向传播会话](/sso/sso-type2)




