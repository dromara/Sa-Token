# 和 jwt 集成 

本插件的作用是让 Sa-Token 和 jwt 做一个整合。 

--- 

### 1、引入依赖 
首先在项目已经引入 Sa-Token 的基础上，继续添加：

<!---------------------------- tabs:start ---------------------------->
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- Sa-Token 整合 jwt -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-jwt</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// Sa-Token 整合 jwt
implementation 'cn.dev33:sa-token-jwt:${sa.top.version}'
```
<!---------------------------- tabs:end ---------------------------->


> 注意: sa-token-jwt 显式依赖 hutool-jwt 5.7.14 版本，意味着：你的项目中要么不引入 Hutool，要么引入版本 >= 5.7.14 的 Hutool 版本

### 2、配置秘钥
在 `application.yml` 配置文件中配置 jwt 生成秘钥：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
sa-token:
	# jwt秘钥 
	jwt-secret-key: asdasdasifhueuiwyurfewbfjsdafjk
```
<!------------- tab:properties 风格  ------------->
``` properties
# jwt秘钥 
sa-token.jwt-secret-key: asdasdasifhueuiwyurfewbfjsdafjk
```
<!---------------------------- tabs:end ---------------------------->

注：为了安全起见请不要直接复制官网示例这个字符串（随便按几个字符就好了）


### 3、注入jwt实现
根据不同的整合规则，插件提供了三种不同的模式，你需要 **选择其中一种** 注入到你的项目中 

<!------------------------------ tabs:start ------------------------------>

<!-- tab: Simple 简单模式  -->
Simple 模式：Token 风格替换
``` java
@Configuration
public class SaTokenConfigure {
    // Sa-Token 整合 jwt (Simple 简单模式)
	@Bean
    public StpLogic getStpLogicJwt() {
    	return new StpLogicJwtForSimple();
    }
}
```

<!-- tab: Mixin 混入模式  -->
Mixin 模式：混入部分逻辑
``` java
@Configuration
public class SaTokenConfigure {
    // Sa-Token 整合 jwt (Mixin 混入模式)
	@Bean
    public StpLogic getStpLogicJwt() {
    	return new StpLogicJwtForMixin();
    }
}
```

<!-- tab: Stateless 无状态模式  -->
Stateless 模式：服务器完全无状态
``` java
@Configuration
public class SaTokenConfigure {
    // Sa-Token 整合 jwt (Stateless 无状态模式)
	@Bean
    public StpLogic getStpLogicJwt() {
    	return new StpLogicJwtForStateless();
    }
}
```

<!---------------------------- tabs:end ------------------------------>

### 4、开始使用
然后我们就可以像之前一样使用 Sa-Token 了 
``` java
/**
 * 登录测试 
 */
@RestController
@RequestMapping("/acc/")
public class LoginController {

    // 测试登录
    @RequestMapping("login")
    public SaResult login() {
		StpUtil.login(10001);
        return SaResult.ok("登录成功");
    }

    // 查询登录状态
    @RequestMapping("isLogin")
    public SaResult isLogin() {
        return SaResult.ok("是否登录：" + StpUtil.isLogin());
    }

    // 测试注销
    @RequestMapping("logout")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok();
    }

}
```

访问上述接口，观察Token生成的样式
``` java
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiMTAwMDEiLCJybiI6IjZYYzgySzBHVWV3Uk5NTTl1dFdjbnpFZFZHTVNYd3JOIn0.F_7fbHsFsDZmckHlGDaBuwDotZwAjZ0HB14DRujQfOQ
```


### 5、不同模式策略对比

注入不同模式会让框架具有不同的行为策略，以下是三种模式的差异点（为方便叙述，以下比较以同时引入 jwt 与 Redis 作为前提）：

| 功能点						| Simple 简单模式		| Mixin 混入模式			| Stateless 无状态模式	|
| :--------					| :--------		| :--------			| :--------			|
| Token风格					| jwt风格		| jwt风格			| jwt风格			|
| 登录数据存储				| Redis中		| Token中			| Token中			|
| Session存储				| Redis中		| Redis中			| 无Session			|
| 注销下线					| 前后端双清数据	| 前后端双清数据		| 前端清除数据		|
| 踢人下线API				| 支持			| 不支持				| 不支持				|
| 登录认证					| 支持			| 支持				| 支持				|
| 角色认证					| 支持			| 支持				| 支持				|
| 权限认证					| 支持			| 支持				| 支持				|
| timeout 有效期				| 支持			| 支持				| 支持				|
| activity-timeout 有效期	| 支持			| 支持				| 不支持				|
| id反查Token				| 支持			| 支持				| 不支持				|
| 会话管理					| 支持			| 部分支持			| 不支持				|
| 注解鉴权					| 支持			| 支持				| 支持				|
| 路由拦截鉴权				| 支持			| 支持				| 支持				|
| 账号封禁					| 支持			| 支持				| 不支持				|
| 身份切换					| 支持			| 支持				| 支持				|
| 二级认证					| 支持			| 支持				| 支持				|
| 模式总结					| Token风格替换	| jwt 与 Redis 逻辑混合	| 完全舍弃Redis，只用jwt		|



### 6、扩展参数
你可以通过以下方式在登录时注入扩展参数：

``` java
// 登录10001账号，并为生成的 Token 追加扩展参数name
StpUtil.login(10001, SaLoginConfig.setExtra("name", "zhangsan"));

// 连缀写法追加多个
StpUtil.login(10001, SaLoginConfig
				.setExtra("name", "zhangsan")
				.setExtra("age", 18)
				.setExtra("role", "超级管理员"));

// 获取扩展参数 
String name = StpUtil.getExtra("name");

// 获取任意 Token 的扩展参数 
String name = StpUtil.getExtra("tokenValue", "name");
```



### 7、在多账户模式中集成 jwt
sa-token-jwt 插件默认只为 `StpUtil` 注入 `StpLogicJwtFoxXxx` 实现，自定义的 `StpUserUtil` 是不会自动注入的，我们需要帮其手动注入：

``` java
/**
 * 为 StpUserUtil 注入 StpLogicJwt 实现 
 */
@Autowired
public void setUserStpLogic() {
	StpUserUtil.setStpLogic(new StpLogicJwtForSimple(StpUserUtil.TYPE));
}
```



### 8、自定义 SaJwtUtil 生成 token 的算法 

如果需要自定义生成 token 的算法（例如更换sign方式），直接重写 SaJwtTemplate 对象即可：

``` java
/**
 * 自定义 SaJwtUtil 生成 token 的算法 
 */
@Autowired
public void setSaJwtTemplate() {
	SaJwtUtil.setSaJwtTemplate(new SaJwtTemplate() {
		@Override
		public String generateToken(JWT jwt, String keyt) {
			System.out.println("------ 自定义了 token 生成算法");
			return super.generateToken(jwt, keyt);
		}
	});
}
```
