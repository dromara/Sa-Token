# SSO模式一 共享Cookie同步会话

如果我们的系统可以保证部署在同一个主域名之下，并且后端连接同一个Redis，那么便可以使用 **`[共享Cookie同步会话]`** 的方式做到单点登录 

> Sa-Token整合同域单点登录非常简单，相比于正常的登录，你只需增加配置 `sa-token.cookie-domain=xxx.com` 指定一下Cookie写入时的父级域名即可 <br>
> 整合示例在官方仓库的 `/sa-token-demo/sa-token-demo-sso1/`，如遇到难点可结合源码进行测试学习

--- 

### 0、解决思路？

首先我们分析一下多个系统之间，为什么无法同步登录状态？
1. 前端的`Token`无法在多个系统下共享
2. 后端的`Session`无法在多个系统间共享

所以单点登录第一招，就是对症下药：
1. 使用`共享Cookie`来解决Token共享问题
2. 使用`Redis`来解决Session共享问题

所谓共享Cookie，就是主域名Cookie在二级域名下的共享，举个例子：写在父域名`stp.com`下的Cookie，在`s1.stp.com`、`s2.stp.com`等子域名都是可以共享访问的

而共享Redis，并不需要我们把所有项目的数据都放在同一个Redis中，Sa-Token提供了 **[权限缓存与业务缓存分离]** 的解决方案，详情戳：[Alone独立Redis插件](/plugin/alone-redis)

> PS：这里建议不要用B项目去连接A项目的Redis，也不要A项目连接B项目的Redis，而是抽离出一个单独的 SSO-Redis，A 和 B 一起连接这个 SSO-Redis

OK，所有理论就绪，下面开始实战



### 1、准备工作

首先修改hosts文件`(C:\WINDOWS\system32\drivers\etc\hosts)`，添加以下IP映射，方便我们进行测试：
``` text
127.0.0.1 s1.stp.com
127.0.0.1 s2.stp.com
127.0.0.1 s3.stp.com
```

### 2、指定Cookie的作用域
在`s1.stp.com`访问服务器，其Cookie也只能写入到`s1.stp.com`下，为了将Cookie写入到其父级域名`stp.com`下，我们需要新增配置: 
``` yml
spring: 
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
	public AjaxJson doLogin(@RequestParam(defaultValue = "10001") String id) {
		System.out.println("---------------- 进行登录 ");
		StpUtil.login(id);
		return AjaxJson.getSuccess("登录成功: " + id);
	}

	// 测试：是否登录
	@RequestMapping("isLogin")
	public AjaxJson isLogin() {
		System.out.println("---------------- 是否登录 ");
		boolean isLogin = StpUtil.isLogin();
		return AjaxJson.getSuccess("是否登录: " + isLogin);
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

现在访问任意节点的登录接口：[http://s1.stp.com:8081/sso/doLogin](http://s1.stp.com:8081/sso/doLogin) 

![sso-type1-login.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type1-login.png 's-w-sh')

然后再次刷新上面三个测试接口，均可以得到以下结果：

![sso-type1-yd.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type1-yd.png 's-w-sh')

测试完毕 


### 5、跨域模式下的解决方案 

如上，我们使用极其简单的步骤实现了同域下的单点登录，聪明如你😏，马上想到了这种模式有着一个不小的限制：

> 所有子系统的域名，必须同属一个父级域名

如果我们的子系统在完全不同的域名下，我们又该怎么完成单点登录功能呢？

且往下看，[SSO模式二：URL重定向传播会话](/sso/sso-type2)




