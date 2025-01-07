# Freemarker 自定义标签 

本插件的作用是让我们可以在 Freemarker 页面中使用 Sa-Token 自定义标签以及相关API。

--- 

### 1、引入依赖 
首先我们确保项目已经引入 Freemarker 依赖，然后在此基础上继续添加：

<!---------------------------- tabs:start ---------------------------->
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- 在 Freemarker 页面中使用 Sa-Token 自定义标签 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-freemarker</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// 在 Freemarker 页面中使用 Sa-Token 自定义标签
implementation 'cn.dev33:sa-token-freemarker:${sa.top.version}'
```
<!---------------------------- tabs:end ---------------------------->



### 2、注入 Sa-Token Freemarker 标签模板模型 对象 
在 SaTokenConfigure 配置类中增加配置 
``` java
@Configuration
public class SaTokenConfigure {

	@Autowired
	FreeMarkerConfigurer configurer;

	/**
	 * 注入 Sa-Token Freemarker 标签模板模型 对象
	 */
	@PostConstruct
	public void setSaTokenTemplateModel() throws TemplateModelException {

		// 注入 Sa-Token Freemarker 标签模板模型，使之可以在 xxx.ftl 文件中使用 sa 标签，
		// 例如：<#if sa.login()>...</#if>
		configurer.getConfiguration().setSharedVariable("sa", new SaTokenTemplateModel());

		// 注入 Sa-Token Freemarker 全局对象，使之可以在 xxx.ftl 文件中调用 StpLogic 相关方法，
		// 例如：<span>${stp.getSession().get('name')}</span>
		configurer.getConfiguration().setSharedVariable("stp", StpUtil.stpLogic);
	}

}
```


### 3、使用自定义标签
然后我们就可以愉快的使用在 Freemarker 页面中使用 Sa-Token 自定义标签了 

##### 3.1、登录判断 
``` html
<h2>标签方言测试页面</h2>
<p>
	登录之后才能显示：
	<@sa.login>value</@sa.login>
</p>
<p>
	不登录才能显示：
	<@sa.notLogin>value</@sa.notLogin>
</p>
```

##### 3.2、角色判断
``` html
<p>
	具有角色 admin 才能显示：
	<@sa.hasRole value="admin">value</@sa.hasRole>
</p>
<p>
	同时具备多个角色才能显示：
	<@sa.hasRoleAnd value="admin, ceo, cto">value</@sa.hasRoleAnd>
</p>
<p>
	只要具有其中一个角色就能显示：
	<@sa.hasRoleOr value="admin, ceo, cto">value</@sa.hasRoleOr>
</p>
<p>
	不具有角色 admin 才能显示：
	<@sa.notRole value="admin">value</@sa.notRole>
</p>
```

##### 3.3、权限判断
``` html
<p>
	具有权限 user-add 才能显示：
	<@sa.hasPermission value="user-add">value</@sa.hasPermission>
</p>
<p>
	同时具备多个权限才能显示：
	<@sa.hasPermissionAnd value="user-add, user-delete, user-get">value</@sa.hasPermissionAnd>
</p>
<p>
	只要具有其中一个权限就能显示：
	<@sa.hasPermissionOr value="user-add, user-delete, user-get">value</@sa.hasPermissionOr>
</p>
<p>
	不具有权限 user-add 才能显示：
	<@sa.notPermission value="user-add">value</@sa.notPermission>
</p>
```


### 4、调用 Sa-Token 相关API  

以上的自定义标签，可以满足我们大多数场景下的权限判断，然后有时候我们依然需要更加灵活的在页面中调用 Sa-Token 框架API :

 
``` html
<p>
	从SaSession中取值：
	<#if stp.isLogin()>
		<span>${stp.getSession().get('name')}</span>
	</#if>
</p>
```




