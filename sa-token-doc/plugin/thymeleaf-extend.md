# Thymeleaf 标签方言

本插件的作用是让我们可以在 Thymeleaf 页面中使用 Sa-Token 相关API，俗称 —— 标签方言。

--- 

### 1、引入依赖 
首先我们确保项目已经引入 Thymeleaf 依赖，然后在此基础上继续添加：

<!---------------------------- tabs:start ---------------------------->
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- 在 thymeleaf 标签中使用 Sa-Token -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-dialect-thymeleaf</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// 在 thymeleaf 标签中使用 Sa-Token
implementation 'cn.dev33:sa-token-dialect-thymeleaf:${sa.top.version}'
```
<!---------------------------- tabs:end ---------------------------->



### 2、注册标签方言对象 
在 SaTokenConfigure 配置类中注册 Bean 
``` java
@Configuration
public class SaTokenConfigure {
	// Sa-Token 标签方言 (Thymeleaf版)
	@Bean
	public SaTokenDialect getSaTokenDialect() {
		return new SaTokenDialect();
	}
}
```


### 3、使用标签方言 
然后我们就可以愉快的使用在 Thymeleaf 页面中使用标签方言了 

##### 3.1、登录判断 
``` html
<h2>标签方言测试页面</h2>
<p>
	登录之后才能显示：
	<span sa:login>value</span>
</p>
<p>
	不登录才能显示：
	<span sa:notLogin>value</span>
</p>
```

##### 3.2、角色判断
``` html
<p>
	具有角色 admin 才能显示：
	<span sa:hasRole="admin">value</span>
</p>
<p>
	同时具备多个角色才能显示：
	<span sa:hasRoleAnd="admin, ceo, cto">value</span>
</p>
<p>
	只要具有其中一个角色就能显示：
	<span sa:hasRoleOr="admin, ceo, cto">value</span>
</p>
<p>
	不具有角色 admin 才能显示：
	<span sa:notRole="admin">value</span>
</p>
```

##### 3.3、权限判断
``` html
<p>
	具有权限 user-add 才能显示：
	<span sa:hasPermission="user-add">value</span>
</p>
<p>
	同时具备多个权限才能显示：
	<span sa:hasPermissionAnd="user-add, user-delete, user-get">value</span>
</p>
<p>
	只要具有其中一个权限就能显示：
	<span sa:hasPermissionOr="user-add, user-delete, user-get">value</span>
</p>
<p>
	不具有权限 user-add 才能显示：
	<span sa:notPermission="user-add">value</span>
</p>
```


### 4、调用 Sa-Token 相关API  

以上的标签方言，可以满足我们大多数场景下的权限判断，然后有时候我们依然需要更加灵活的在页面中调用 Sa-Token 框架API  

首先在 SaTokenConfigure 配置类中为 Thymeleaf 配置全局对象：

(注意: 如果`SaTokenConfigure`继承了`WebMvcConfigurer`等类, 可能会造成循环依赖, 如果遇到, 请新建一个其他配置类完成此项配置)

``` java
@Configuration
public class SaTokenConfigure{
	// ... 其它代码
	
	// 为 Thymeleaf 注入全局变量，以便在页面中调用 Sa-Token 的方法 
	@Autowired
	private void configureThymeleafStaticVars(ThymeleafViewResolver viewResolver) {
		viewResolver.addStaticVariable("stp", StpUtil.stpLogic);
	}
}
```

然后我们就可以在页面上调用 StpLogic 的 API 了，例如：
 
``` html
<p>调用 StpLogic 方法调用测试</p>
<p th:if="${stp.isLogin()}">
	从SaSession中取值：
	<span th:text="${stp.getSession().get('name')}"></span>
</p>
```


### 5、代码提示

如果想在写标签属性时增加代码提示：

![thymeleaf-code-tips.png](https://oss.dev33.cn/sa-token/doc/thymeleaf-code-tips.png 's-w')

只需在头部声明增加上对应的命名空间即可：

``` html
<!DOCTYPE html>
<html lang="zh" xmlns:sa="http://www.thymeleaf.org/extras/sa-token">
	<head>
		<!-- 代码 -->
	</head>
	<body>
		<!-- 代码 -->
	</body>
</html>
```




