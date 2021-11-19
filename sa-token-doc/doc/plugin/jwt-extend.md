# 和 jwt 集成 

本插件的作用是让 Sa-Token 和 jwt 做一个整合。 

--- 

### 1、引入依赖 
首先在项目已经引入 Sa-Token 的基础上，继续添加：

``` xml
<!-- Sa-Token 整合 jwt -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-jwt</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```


### 2、配置秘钥
在 `application.yml` 配置文件中配置 jwt 生成秘钥：
``` yml
sa-token:
	# jwt秘钥 
	jwt-secret-key: asdasdasifhueuiwyurfewbfjsdafjk
```
注：为了安全起见请不要直接复制官网示例这个字符串（随便按几个字符就好了）


### 3、注入jwt实现
根据不同的整合规则，插件提供了三种不同的模式，你需要 **选择其中一种** 注入到你的项目中 

<!------------------------------ tabs:start ------------------------------>

<!-- tab: Style 模式  -->
Style 模式：Token 风格替换
``` java
@Configuration
public class SaTokenConfigure {
    // Sa-Token 整合 jwt (Style模式)
	@Bean
    public StpLogic getStpLogicJwt() {
    	return new StpLogicJwtForStyle();
    }
}
```

<!-- tab: Mix 模式  -->
Mix 模式：混入部分逻辑
``` java
@Configuration
public class SaTokenConfigure {
    // Sa-Token 整合 jwt (Style模式)
	@Bean
    public StpLogic getStpLogicJwt() {
    	return new StpLogicJwtForMix();
    }
}
```

<!-- tab: Stateless模式  -->
Stateless 模式：服务器完全无状态
``` java
@Configuration
public class SaTokenConfigure {
    // Sa-Token 整合 jwt (Style模式)
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

| 功能点						| Style 模式		| Mix 模式			| Stateless 模式	|
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
| 账号封禁					| 支持			| 支持				| 不支持				|
| 身份切换					| 支持			| 支持				| 支持				|
| 二级认证					| 支持			| 支持				| 支持				|
| 模式总结					| Token风格替换	| jwt 与 Redis 逻辑混合	| 完全舍弃Redis，只用jwt		|





