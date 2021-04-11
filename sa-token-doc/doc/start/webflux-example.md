# Spring WebFlux 集成 Sa-Token 示例

WebFlux基于Reactor响应式模型开发，有着与标准ServletAPI完全不同的底层架构，因此要适配WebFlux, 必须提供与Reactor相关的整合实现，
本篇将以WebFlux为例，展示sa-token与Reactor响应式模型web框架相整合的示例, **你可以用同样方式去对接其它Reactor模型Web框架**

整合示例在官方仓库的`/sa-token-demo-webflux`文件夹下，如遇到难点可结合源码进行测试学习


---

### 1、创建项目
在IDE中新建一个SpringBoot项目，例如：`sa-token-demo-webflux`（不会的同学请自行百度或者参考github示例）


### 2、设置pom包依赖
在 `pom.xml` 中添加依赖：

``` xml 
<!-- Sa-Token 权限认证（Reactor响应式集成）, 在线文档：http://sa-token.dev33.cn/ -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-reactor-spring-boot-starter</artifactId>
	<version>1.15.2</version>
</dependency>
```


### 3、设置配置文件
你可以**零配置启动项目** ，但同时你也可以在`application.yml`中增加如下配置，定制性使用框架：

``` java
spring: 
    # sa-token配置
    sa-token: 
        # token名称 (同时也是cookie名称)
        token-name: satoken
        # token有效期，单位s 默认30天, -1代表永不过期 
        timeout: 2592000
        # token临时有效期 (指定时间内无操作就视为token过期) 单位: 秒
        activity-timeout: -1
        # 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录) 
        allow-concurrent-login: false
        # 在多人登录同一账号时，是否共用一个token (为true时所有登录共用一个token, 为false时每次登录新建一个token) 
        is-share: false
        # token风格
        token-style: uuid
```

如果你习惯于 `application.properties` 类型的配置文件，那也很好办:  <br>
百度： [springboot properties与yml 配置文件的区别](https://www.baidu.com/s?ie=UTF-8&wd=springboot%20properties%E4%B8%8Eyml%20%E9%85%8D%E7%BD%AE%E6%96%87%E4%BB%B6%E7%9A%84%E5%8C%BA%E5%88%AB)


### 4、创建启动类
在项目中新建包 `com.pj` ，在此包内新建主类 `SaTokenDemoApplication.java`，输入以下代码：

``` java
@SpringBootApplication
public class SaTokenDemoApplication {
	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(SaTokenDemoApplication.class, args);
		System.out.println("启动成功：sa-token配置如下：" + SaTokenManager.getConfig());
	}
}
```

### 5、运行
运行代码，当你从控制台看到类似下面的内容时，就代表框架已经成功集成了

![运行结果](https://color-test.oss-cn-qingdao.aliyuncs.com/sa-token/app-run.jpg)







