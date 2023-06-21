# Solon 集成 Sa-Token 示例

本篇介绍在 Solon 应用中如何集成 Sa-Token。

整合示例在官方仓库的 /sa-token-demo/sa-token-demo-solon 文件夹下，如遇到难点可结合源码进行学习测试。

> Solon 是一个高效的国产应用开发框架：更快、更小、更简单。
> 
> - 启动快 5 ～ 10 倍；
> - qps 高 2～ 3 倍；
> - 运行时内存节省 1/3 ~ 1/2；
> - 打包可以缩到 1/2 ~ 1/10；
> - 同时支持 jdk8、jdk11、jdk17、jdk20。
> 
> 详情可参考：[https://solon.noear.org/](https://solon.noear.org/)


---

### 1、创建项目

在 IDE 中新建一个 Solon 项目，例如：sa-token-demo-solon （可以借助 [Solon Initializr](https://solon.noear.org/start/) 生成） 

### 2、添加依赖

在项目中添加依赖：

<!---------------------------- tabs:start ---------------------------->
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- Sa-Token 权限认证，在线文档：https://sa-token.cc -->
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-solon-plugin</artifactId>
    <version>${sa.top.version}</version>
</dependency>
```

<!-------- tab:Gradle 方式 -------->
``` gradle
// Sa-Token 权限认证，在线文档：https://sa-token.cc
implementation 'cn.dev33:sa-token-solon-plugin:${sa.top.version}'
```
<!---------------------------- tabs:end ---------------------------->



Maven依赖一直无法加载成功？[参考解决方案](/doc.html#/start/maven-pull)

更多内测版本了解：[Sa-Token最新版本](https://gitee.com/dromara/sa-token/blob/dev/sa-token-doc/start/new-version.md)



### 3、设置配置文件

你可以**零配置启动项目** ，但同时你也可以在 `app.yml` 中增加如下配置，定制性使用框架：

<!---------------------------- tabs:start ---------------------------->

<!------------- tab:app.yml 风格  ------------->

```yaml
server:
    # 端口
    port: 8081
    
############## Sa-Token 配置 (文档: https://sa-token.cc) ##############
sa-token: 
	# token 名称（同时也是 cookie 名称）
	token-name: satoken
	# token 有效期（单位：秒） 默认30天，-1 代表永久有效
	timeout: 2592000
	# token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
	active-timeout: -1
	# 是否允许同一账号多地同时登录 （为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
	is-concurrent: true
	# 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token, 为 false 时每次登录新建一个 token）
	is-share: true
	# token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
	token-style: uuid
	# 是否输出操作日志 
	is-log: true
```

<!------------- tab:app.properties 风格  ------------->
```properties
# 端口
server.port=8081
    
############## Sa-Token 配置 (文档: https://sa-token.cc) ##############

# token 名称（同时也是 cookie 名称）
sa-token.token-name=satoken
# token 有效期（单位：秒） 默认30天，-1 代表永久有效
sa-token.timeout=2592000
# token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
sa-token.active-timeout=-1
# 是否允许同一账号多地同时登录 （为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
sa-token.is-concurrent=true
# 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token, 为 false 时每次登录新建一个 token）
sa-token.is-share=true
# token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
sa-token.token-style=uuid
# 是否输出操作日志 
sa-token.is-log=true
```

<!---------------------------- tabs:end ---------------------------->




### 4、创建启动类

在项目中新建包 `com.pj` ，在此包内新建主类 `SaTokenDemoApp.java`，复制以下代码：

```java
@SolonMain
public class SaTokenDemoApp {
    public static void main(String[] args) {
        Solon.start(SaTokenDemoApp.class, args);
        System.out.println("启动成功：Sa-Token配置如下：" + SaManager.getConfig());
    }
}
```

### 5、创建测试Controller

```java
@Mapping("/user/")
@Controller
public class UserController {

    // 测试登录，浏览器访问： http://localhost:8081/user/doLogin?username=zhang&password=123456
    @Mapping("doLogin")
    public String doLogin(String username, String password) {
        // 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对 
        if("zhang".equals(username) && "123456".equals(password)) {
            StpUtil.login(10001);
            return "登录成功";
        }
        return "登录失败";
    }

    // 查询登录状态，浏览器访问： http://localhost:8081/user/isLogin
    @Mapping("isLogin")
    public String isLogin() {
        return "当前会话是否登录：" + StpUtil.isLogin();
    }
    
}
```

### 6、运行

启动代码，从浏览器依次访问上述测试接口：

![](https://oss.dev33.cn/sa-token/doc/test-do-login.png)


![](https://oss.dev33.cn/sa-token/doc/test-is-login.png)


### 详细了解

通过这个示例，你已经对 Sa-Token 有了初步的了解，那么现在开始详细了解一下它都有哪些吧：

[登录认证](/use/login-auth) (与 Springboot 处理类似)





