# SpEL 表达式注解鉴权

Sa-Token 提供一个 `@SaCheckEL` 鉴权注解，该注解允许你使用 SpEL 表达式进行鉴权。


### 1、引入插件

由于该注解的工作底层需要依赖 SpringAOP 切面编程，因此你需要单独引入插件包 `sa-token-spring-el` 才可以使用此注解。

<!---------------------------- tabs:start ---------------------------->
<!-------- tab:Maven 方式 -------->
``` xml 
<!-- Sa-Token 注解鉴权使用 EL 表达式 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-el</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
<!-------- tab:Gradle 方式 -------->
``` gradle
// Sa-Token 注解鉴权使用 EL 表达式
implementation 'cn.dev33:sa-token-spring-el:${sa.top.version}'
```
<!---------------------------- tabs:end ---------------------------->


### 2、简单示例

以下是一些使用示例：
``` java
@RestController
@RequestMapping("/check-el/")
public class SaCheckELController {

	// 登录校验 
	@SaCheckEL("stp.checkLogin()")
	@RequestMapping("test1")
	public SaResult test1() {
		return SaResult.ok();
	}

	// 权限校验 
	@SaCheckEL("stp.checkPermission('user:edit')")
	@RequestMapping("test3")
	public SaResult test3() {
		return SaResult.ok();
	}

	// 参数长度校验 
	@SaCheckEL("NEED( #name.length() > 3 )")
	@RequestMapping("test5")
	public SaResult test5(@RequestParam(defaultValue = "") String name) {
		return SaResult.ok().set("name", name);
	}

	// SaSession 里取值校验 
	@SaCheckEL("NEED( stp.getSession().get('name') == 'zhangsan' )")
	@RequestMapping("test8")
	public SaResult test8() {
		return SaResult.ok();
	}

}
```


### 3、多账号体系鉴权

要在 EL 表达式中使用多账号体系鉴权模式，你需要在配置类中重写 `SaCheckELRootMap 扩展函数`，增加 EL 表达式可使用的根对象：

``` java
@Configuration
public class SaTokenConfigure {
	
    /**
     * 重写 Sa-Token 框架内部算法策略 
     */
    @PostConstruct
    public void rewriteSaStrategy() {
		// 重写 SaCheckELRootMap 扩展函数，增加注解鉴权 EL 表达式可使用的根对象
		SaAnnotationStrategy.instance.checkELRootMapExtendFunction = rootMap -> {
			System.out.println("--------- 执行 SaCheckELRootMap 增强，目前已包含的的跟对象包括：" + rootMap.keySet());
			// 新增 stpUser 根对象，使之可以在表达式中通过 stpUser.checkLogin() 方式进行多账号体系鉴权
			rootMap.put("stpUser", StpUserUtil.getStpLogic());
		};
    }

}
```

然后就可以使用多账号体系鉴权模式了

``` java
// 多账号体系鉴权测试 
@SaCheckEL("stpUser.checkLogin()")
@RequestMapping("test9")
public SaResult test9() {
	return SaResult.ok();
}
```


### 4、调用本类成员变量
``` java
// 本模块需要鉴权的权限码
public String permissionCode = "article:add";

// 调用本类的成员变量 
@SaCheckEL("stp.checkPermission( this.permissionCode )")
@RequestMapping("test10")
public SaResult test10() {
	return SaResult.ok();
}
```


### 5、忽略鉴权
配合 `@SaIgnore` 注解做到忽略某接口的鉴权
``` java 
// 忽略鉴权测试 
@SaIgnore
@SaCheckEL("stp.checkPermission( 'abc' )")
@RequestMapping("test11")
public SaResult test11() {
	return SaResult.ok();
}
```


### 6、代码提示

如果在书写 SpEL 表达式时需要代码提示：

![sa-check-el-code-tips.png](https://oss.dev33.cn/sa-token/doc/plugin/sa-check-el-code-tips.png 's-w')

可以在 idea 中安装 **SpEL Assistant** 插件，该插件由 `@ly-chn` 提供，允许为自定义注解书写 SpEL 表达式时增加代码提示功能，
开源地址：[https://github.com/ly-chn/SpEL-Assistant](https://github.com/ly-chn/SpEL-Assistant)

安装方式：直接在 idea 插件商店中搜索 “**SpEL Assistant**” 即可

![sa-check-el-code-tips.png](https://oss.dev33.cn/sa-token/doc/plugin/sa-check-el-setup-plugin.png 's-w')


<a class="case-btn" href="https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/cases/more/SaCheckELController.java"
	target="_blank">
	本章代码示例：Sa-Token SpEL表达式注解鉴权 —— [ SaCheckELController.java ]
</a>