# Sa-Token-Quick-Login 快速登录认证
--- 


### 解决什么问题

Sa-Token-Quick-Login 可以为一个系统快速的、零代码 注入一个登录页面 

试想一下，假如我们开发了一个非常简单的小系统，比如说：服务器性能监控页面，
我们将它部署在服务器上，通过访问这个页面，我们可以随时了解服务器性能信息，非常方便

然而，这个页面方便我们的同时，也方便了一些不法的攻击者，由于这个页面毫无防护的暴露在公网中，任何一台安装了浏览器的电脑都可以随时访问它！

为此，我们必须给这个系统加上一个登录认证，只有知晓了后台密码的人员才可以进行访问

细细想来，完成这个功能你需要：

1. 编写前端登录页面，手写各种表单样式
2. 寻找合适的ajax类库，`jQuery`？`Axios`？还是直接前后台不分离？
3. 寻找合适的模板引擎，比如`jsp`、`Thymeleaf`、`FreeMarker`、`Velocity`……选哪个呢？
4. 处理后台各种拦截认证逻辑，前后台接口对接
5. 你可能还会遇到令人头痛欲裂的模板引擎中`ContextPath`处理
6. ……

你马上就会发现，写个监控页你一下午就可以搞定，然而这个登录页你却可能需要花上两三天的时间，这是一笔及其不划算的时间浪费

那么现在你可能就会有个疑问，难道就没有什么方法给我的小项目快速增加一个登录功能吗？

Sa-Token-Quick-Login便是为了解决这个问题！


### 适用场景

Sa-Token-Quick-Login 旨在用最小的成本为项目增加一个登录认证功能

- **简单**：只需要引入一个依赖便可为系统注入登录功能，快速、简单、零代码！
- **不可定制**：由于登录页面不可定制，所以Sa-Token-Quick-Login非常不适合普通项目的登录认证模块，STQL也无意去解决所有项目的登录认证模块

Sa-Token-Quick-Login的定位是这样的场景：你的项目需要一个登录认证功能、这个认证页面可以不华丽、可以烂，但是一定要有，同时你又不想花费太多的时间浪费在登录页面上，
那么你便可以尝试一下`Sa-Token-Quick-Login`


### 集成步骤
首先我们需要创建一个SpringBoot的demo项目，比如：`sa-token-demo-quick-login`

##### 1、添加pom依赖
``` xml
<!-- Sa-Token-Quick-Login 插件 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-quick-login</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```

##### 2、启动类
``` java
@SpringBootApplication
public class SaTokenQuickDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(SaTokenQuickDemoApplication.class, args);
		
		System.out.println("\n------ 启动成功 ------");
		System.out.println("name: " + SaQuickManager.getConfig().getName());
		System.out.println("pwd:  " + SaQuickManager.getConfig().getPwd());
	}
}
```

##### 3、新建测试Controller
``` java
/**
 * 测试专用Controller 
 */
@RestController
public class TestController {
	// 浏览器访问测试： http://localhost:8081
	@RequestMapping({"/", "/index"})
	public String index() {
		String str = "<br />"
				+ "<h1 style='text-align: center;'>资源页 （登录后才可进入本页面） </h1>"
				+ "<hr/>"
				+ "<p style='text-align: center;'> Sa-Token " + SaTokenConsts.VERSION_NO + " </p>";
		return str;
	}
}
```

### 测试访问
启动项目，使用浏览器访问：`http://localhost:8081`，首次访问时，由于处于未登录状态，会被强制进入登录页面

![登录](https://oss.dev33.cn/sa-token/doc/sa-quick-login.png 's-w')

使用默认账号：`sa / 123456`进行登录，会看到资源页面

![登录](https://oss.dev33.cn/sa-token/doc/sa-quick-login-index.png 's-w')


### 可配置信息
你可以在yml中添加如下配置 (所有配置都是可选的) 
``` java
# Sa-Token-Quick-Login 配置
sa: 
	# 登录账号
	name: sa
	# 登录密码
	pwd: 123456
	# 是否自动随机生成账号密码 (此项为true时, name与pwd失效)
	auto: false
	# 是否开启全局认证(关闭后将不再强行拦截) 
	auth: true
	# 登录页标题
	title: Sa-Token 登录
	# 是否显示底部版权信息 
	copr: true
    # 指定拦截路径 
    # include: /**
    # 指定排除路径
    # exclude: /sss,/fff
```

<br>

**注：**示例源码在`/sa-token-demo/sa-token-demo-quick-login`目录下，可结合源码查看学习



### 使用独立jar包运行
使用`sa-token-quick-login`只需引入一个依赖即可为系统注入登录模块，现在我们更进一步，将这个项目打成一个独立的jar包

通过这个jar包，我们可以方便的部署任意静态网站！做到真正的零编码注入登录功能。


##### 打包步骤

首先放上懒人链接：[sa-quick-dist.jar](https://gitee.com/dromara/sa-token/attach_files/695353/download) ，不想手动操作的同学可以直接点此链接下载打包后的jar文件 

1、首先将 `sa-token-demo-quick-login` 模块添加到顶级父模块的`<modules>`节点中

``` xml
<!-- 所有模块 -->
<modules>
	<module>sa-token-core</module>
	<module>sa-token-starter</module>
	<module>sa-token-plugin</module>
	<module>sa-token-demo\sa-token-demo-quick-login</module>
</modules>
```

2、在项目根目录进入cmd执行打包命令

``` cmd
mvn clean package
```

3、进入`\sa-token-demo\sa-token-demo-quick-login\target` 文件夹，找到打包好的jar文件 

``` cmd
sa-token-demo-quick-login-0.0.1-SNAPSHOT.jar
```

4、我们将其重命名为`sa-quick-dist.jar`，现在这个jar包就是我们的最终程序，我们在这个`\target`目录直接进入cmd，执行如下命令启动jar包

``` cmd
java -jar sa-quick-dist.jar
```

5、测试访问，根据控制台输出提示，我们使用浏览器访问测试: `http://localhost:8080`

![sa-quick-start](https://oss.dev33.cn/sa-token/doc/sa-quick-start.png 's-w')

如果可以进入登录界面，则代表打包运行成功 <br>
当然仅仅运行成功还不够，下面我们演示一下如何使用这个jar包进行静态网站部署


### 所有功能示例

##### Case 1. 指定静态资源路径
``` cmd
java -jar sa-quick-dist.jar --sa.dir file:E:\www
```
使用dir参数指定`E:\www`目录作为资源目录进行部署 (现在我们可以通过浏览器访问`E:\www`目录下的文件了！)

##### Case 2. 指定登录名与密码
``` cmd
java -jar sa-quick-dist.jar --sa.name=zhang --sa.pwd=zhang123
```
现在，默认的账号`sa/123456`将被废弃，而是使用`zhang/zhang123`进行账号校验

##### Case 3. 指定其自动生成账号密码
``` cmd
java -jar sa-quick-dist.jar --sa.auto=true
```
每次启动时随机生成账号密码（会在启动成功时打印到控制台上）

##### Case 4. 指定登录页的标题
``` cmd
java -jar sa-quick-dist.jar --sa.title="XXX 系统登录"
```

##### Case 5. 关闭账号校验，仅作为静态资源部署使用
``` cmd
java -jar sa-quick-dist.jar --sa.auth=false
```

##### Case 6. 指定启动端口（默认8080）
``` cmd
java -jar sa-quick-dist.jar --server.port=80 
```

注：所有参数可组合使用


### 使用SpringBoot默认资源路径
SpringBoot默认开放了一些路径作为资源目录，比如`classpath:/static/`，
怎么使用呢？我们只需要在jar包同目录创建一个`\static`文件夹，将静态资源文件复制到此目录下，然后启动jar包即可访问

同时，我们还可以在jar包同目录创建yml配置文件，来覆盖jar包内的yml配置，如下图所示：

![sa-quick-case.png](https://oss.dev33.cn/sa-token/doc/sa-quick-case.png 's-w')

例如如上目录中`/static`中有一个`1.jpg`文件，我们启动jar包后访问`http://localhost:8080/1.jpg`即可查看到此文件，这是Springboot自带的功能，在此不再赘述







