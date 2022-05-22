# SSO模式一 共享Cookie同步会话

如果我们的多个系统可以做到：前端同域、后端同Redis，那么便可以使用 **`[共享Cookie同步会话]`** 的方式做到单点登录。

--- 

### 1、解决思路？

首先我们分析一下多个系统之间，为什么无法同步登录状态？
1. 前端的 `Token` 无法在多个系统下共享。
2. 后端的 `Session` 无法在多个系统间共享。

所以单点登录第一招，就是对症下药：
1. 使用 `共享Cookie` 来解决 Token 共享问题。
2. 使用 `Redis` 来解决 Session 共享问题。

所谓共享Cookie，就是主域名Cookie在二级域名下的共享，举个例子：写在父域名`stp.com`下的Cookie，在`s1.stp.com`、`s2.stp.com`等子域名都是可以共享访问的。

而共享Redis，并不需要我们把所有项目的数据都放在同一个Redis中，Sa-Token提供了 **[权限缓存与业务缓存分离]** 的解决方案，详情戳：[Alone独立Redis插件](/plugin/alone-redis)。


<button class="show-img" img-src="https://oss.dev33.cn/sa-token/doc/g/g3--sso1.gif">加载动态演示图</button>


OK，所有理论就绪，下面开始实战：


### 2、准备工作

首先修改hosts文件`(C:\windows\system32\drivers\etc\hosts)`，添加以下IP映射，方便我们进行测试：
``` url
127.0.0.1 sso.stp.com
127.0.0.1 s1.stp.com
127.0.0.1 s2.stp.com
127.0.0.1 s3.stp.com
```

其中：`sso.stp.com`为统一认证中心地址，当用户在其它 Client 端发起登录请求时，均将其重定向至认证中心，待到登录成功之后再原路返回到 Client 端。


### 3、指定Cookie的作用域
在`sso.stp.com`访问服务器，其Cookie也只能写入到`sso.stp.com`下，为了将Cookie写入到其父级域名`stp.com`下，我们需要更改 SSO-Server 端的 yml 配置：

``` yml
sa-token:
    cookie:
        # 配置Cookie作用域 
        domain: stp.com
```

这个配置原本是被注释掉的，现在将其打开。另外我们格外需要注意：
在SSO模式一测试完毕之后，一定要将这个配置再次注释掉，因为模式一与模式二三使用不同的授权流程，这行配置会影响到我们模式二和模式三的正常运行。 




### 4、搭建 Client 端项目 

> 搭建示例在官方仓库的 `/sa-token-demo/sa-token-demo-sso1-client/`，如遇到难点可结合源码进行测试学习。


#### 4.1、引入依赖
新建项目 sa-token-demo-sso1-client，并添加以下依赖：

``` xml
<!-- Sa-Token 权限认证, 在线文档：http://sa-token.dev33.cn/ -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>${sa-token-version}</version>
</dependency>
<!-- Sa-Token 插件：整合SSO -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-sso</artifactId>
	<version>${sa.top.version}</version>
</dependency>

<!-- Sa-Token 整合redis (使用jackson序列化方式) -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-dao-redis-jackson</artifactId>
	<version>${sa-token-version}</version>
</dependency>
<dependency>
	<groupId>org.apache.commons</groupId>
	<artifactId>commons-pool2</artifactId>
</dependency>

<!-- Sa-Token插件：权限缓存与业务缓存分离 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-alone-redis</artifactId>
	<version>${sa-token-version}</version>
</dependency>
```


#### 4.2、新建 Controller 控制器

``` java
/**
 * Sa-Token-SSO Client端 Controller 
 * @author kong
 */
@RestController
public class SsoClientController {

	// SSO-Client端：首页 
	@RequestMapping("/")
	public String index() {
		String authUrl = SaSsoManager.getConfig().getAuthUrl();
		String solUrl = SaSsoManager.getConfig().getSloUrl();
		String str = "<h2>Sa-Token SSO-Client 应用端</h2>" + 
					"<p>当前会话是否登录：" + StpUtil.isLogin() + "</p>" + 
					"<p><a href=\"javascript:location.href='" + authUrl + "?mode=simple&redirect=' + encodeURIComponent(location.href);\">登录</a> " + 
					"<a href=\"javascript:location.href='" + solUrl + "?back=' + encodeURIComponent(location.href);\">注销</a> </p>";
		return str;
	}
	
	// 全局异常拦截 
	@ExceptionHandler
	public SaResult handlerException(Exception e) {
		e.printStackTrace(); 
		return SaResult.error(e.getMessage());
	}
	
}
```

#### 4.3、application.yml 配置 

``` yml
# 端口
server:
    port: 9001

# sa-token配置 
sa-token: 
    # SSO-相关配置
    sso: 
        # SSO-Server端-单点登录授权地址 
        auth-url: http://sso.stp.com:9000/sso/auth
        # SSO-Server端-单点注销地址
        slo-url: http://sso.stp.com:9000/sso/logout
    
    # 配置Sa-Token单独使用的Redis连接 （此处需要和SSO-Server端连接同一个Redis）
    alone-redis: 
        # Redis数据库索引
        database: 1
        # Redis服务器地址
        host: 127.0.0.1
        # Redis服务器连接端口
        port: 6379
        # Redis服务器连接密码（默认为空）
        password: 
        # 连接超时时间
        timeout: 10s
```

#### 4.4、启动类

``` java
/**
 * SSO模式一，Client端 Demo 
 */
@SpringBootApplication
public class SaSsoClientApplication {
	public static void main(String[] args) {
		SpringApplication.run(SaSsoClientApplication.class, args);
		System.out.println("\nSa-Token SSO模式一 Client端启动成功");
	}
}
```


### 5、访问测试
启动项目，依次访问三个应用端：
- [http://s1.stp.com:9001/](http://s1.stp.com:9001/)
- [http://s2.stp.com:9001/](http://s2.stp.com:9001/)
- [http://s3.stp.com:9001/](http://s3.stp.com:9001/)


均返回：

![sso1--index.png](https://oss.dev33.cn/sa-token/doc/sso/sso1--index.png 's-w-sh')

然后点击登录，被重定向至SSO认证中心：

![sso1--login-page2.png](https://oss.dev33.cn/sa-token/doc/sso/sso1--login-page2.png 's-w-sh')

我们点击登录，然后刷新页面：

![sso1-login-ok.png](https://oss.dev33.cn/sa-token/doc/sso/sso1-login-ok.png 's-w-sh')

刷新另外两个Client端，均显示已登录 

![sso1-login-ok2.png](https://oss.dev33.cn/sa-token/doc/sso/sso1-login-ok2.png 's-w-sh')

测试完成 



### 6、跨域模式下的解决方案 

如上，我们使用简单的步骤实现了同域下的单点登录，聪明如你😏，马上想到了这种模式有着一个不小的限制：

> 所有子系统的域名，必须同属一个父级域名

如果我们的子系统在完全不同的域名下，我们又该怎么完成单点登录功能呢？

且往下看，[SSO模式二：URL重定向传播会话](/sso/sso-type2)





