# 权限认证

--- 


### 设计思路

所谓权限认证，核心逻辑就是判断一个账号是否拥有指定权限：<br/>
- 有，就让你通过。
- 没有？那么禁止访问！

深入到底层数据中，就是每个账号都会拥有一个权限码集合，框架来校验这个集合中是否包含指定的权限码。 

例如：当前账号拥有权限码集合 `["user-add", "user-delete", "user-get"]`，这时候我来校验权限 `"user-update"`，则其结果就是：**验证失败，禁止访问**。 <br/>


<button class="show-img" img-src="https://oss.dev33.cn/sa-token/doc/g/g3--jur-auth.gif">加载动态演示图</button>


所以现在问题的核心就是：
1. 如何获取一个账号所拥有的的权限码集合？
2. 本次操作需要验证的权限码是哪个？

### 获取当前账号权限码集合
因为每个项目的需求不同，其权限设计也千变万化，因此 [ 获取当前账号权限码集合 ] 这一操作不可能内置到框架中，
所以 Sa-Token 将此操作以接口的方式暴露给你，以方便你根据自己的业务逻辑进行重写。

你需要做的就是新建一个类，实现 `StpInterface`接口，例如以下代码：

``` java 
/**
 * 自定义权限验证接口扩展
 */
@Component	// 保证此类被SpringBoot扫描，完成Sa-Token的自定义权限验证扩展 
public class StpInterfaceImpl implements StpInterface {

	/**
	 * 返回一个账号所拥有的权限码集合 
	 */
	@Override
	public List<String> getPermissionList(Object loginId, String loginType) {
		// 本list仅做模拟，实际项目中要根据具体业务逻辑来查询权限
		List<String> list = new ArrayList<String>();	
		list.add("101");
		list.add("user.add");
		list.add("user.update");
		list.add("user.get");
		// list.add("user.delete");
		list.add("art.*");
		return list;
	}

	/**
	 * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
	 */
	@Override
	public List<String> getRoleList(Object loginId, String loginType) {
		// 本list仅做模拟，实际项目中要根据具体业务逻辑来查询角色
		List<String> list = new ArrayList<String>();	
		list.add("admin");
		list.add("super-admin");
		return list;
	}

}
```

**参数解释：**
- loginId：账号id，即你在调用 `StpUtil.login(id)` 时写入的标识值。
- loginType：账号体系标识，此处可以暂时忽略，在 [ 多账户认证 ] 章节下会对这个概念做详细的解释。

可参考代码：[码云：StpInterfaceImpl.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/satoken/StpInterfaceImpl.java)

> 注意: StpInterface 接口在需要鉴权时由框架自动调用，开发者只需要配置好就可以使用下面的鉴权方法或后面的注解鉴权


### 权限校验
然后就可以用以下api来鉴权了

``` java	
// 获取：当前账号所拥有的权限集合
StpUtil.getPermissionList();

// 判断：当前账号是否含有指定权限, 返回 true 或 false
StpUtil.hasPermission("user.add");		

// 校验：当前账号是否含有指定权限, 如果验证未通过，则抛出异常: NotPermissionException 
StpUtil.checkPermission("user.add");		

// 校验：当前账号是否含有指定权限 [指定多个，必须全部验证通过]
StpUtil.checkPermissionAnd("user.add", "user.delete", "user.get");		

// 校验：当前账号是否含有指定权限 [指定多个，只要其一验证通过即可]
StpUtil.checkPermissionOr("user.add", "user.delete", "user.get");	
```

扩展：`NotPermissionException` 对象可通过 `getLoginType()` 方法获取具体是哪个 `StpLogic` 抛出的异常


### 角色校验
在Sa-Token中，角色和权限可以独立验证

``` java
// 获取：当前账号所拥有的角色集合
StpUtil.getRoleList();

// 判断：当前账号是否拥有指定角色, 返回 true 或 false
StpUtil.hasRole("super-admin");		

// 校验：当前账号是否含有指定角色标识, 如果验证未通过，则抛出异常: NotRoleException
StpUtil.checkRole("super-admin");		

// 校验：当前账号是否含有指定角色标识 [指定多个，必须全部验证通过]
StpUtil.checkRoleAnd("super-admin", "shop-admin");		

// 校验：当前账号是否含有指定角色标识 [指定多个，只要其一验证通过即可] 
StpUtil.checkRoleOr("super-admin", "shop-admin");		
```

扩展：`NotRoleException` 对象可通过 `getLoginType()` 方法获取具体是哪个 `StpLogic` 抛出的异常



### 拦截全局异常
有同学要问，鉴权失败，抛出异常，然后呢？要把异常显示给用户看吗？**当然不可以！**

你可以创建一个全局异常拦截器，统一返回给前端的格式，参考：

``` java
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 全局异常拦截 
    @ExceptionHandler
    public SaResult handlerException(Exception e) {
        e.printStackTrace(); 
        return SaResult.error(e.getMessage());
    }
}
```

可参考：[码云：GlobalException.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/current/GlobalException.java)


### 权限通配符
Sa-Token允许你根据通配符指定**泛权限**，例如当一个账号拥有`art.*`的权限时，`art.add`、`art.delete`、`art.update`都将匹配通过

``` java
// 当拥有 art.* 权限时
StpUtil.hasPermission("art.add");        // true
StpUtil.hasPermission("art.update");     // true
StpUtil.hasPermission("goods.add");      // false

// 当拥有 *.delete 权限时
StpUtil.hasPermission("art.delete");      // true
StpUtil.hasPermission("user.delete");     // true
StpUtil.hasPermission("user.update");     // false

// 当拥有 *.js 权限时
StpUtil.hasPermission("index.js");        // true
StpUtil.hasPermission("index.css");       // false
StpUtil.hasPermission("index.html");      // false
```

!> 上帝权限：当一个账号拥有 `"*"` 权限时，他可以验证通过任何权限码 （角色认证同理）


### 如何把权限精确到按钮级？
权限精确到按钮级的意思就是指：**权限范围可以控制到页面上的每一个按钮是否显示**。

思路：如此精确的范围控制只依赖后端已经难以完成，此时需要前端进行一定的逻辑判断。

如果是前后端一体项目，可以参考：[Thymeleaf 标签方言](/plugin/thymeleaf-extend)，如果是前后端分离项目，则：

1. 在登录时，把当前账号拥有的所有权限码一次性返回给前端。
2. 前端将权限码集合保存在`localStorage`或其它全局状态管理对象中。
3. 在需要权限控制的按钮上，使用 js 进行逻辑判断，例如在`Vue`框架中我们可以使用如下写法：
``` js
<button v-if="arr.indexOf('user.delete') > -1">删除按钮</button>
```
其中：`arr`是当前用户拥有的权限码数组，`user.delete`是显示按钮需要拥有的权限码，`删除按钮`是用户拥有权限码才可以看到的内容。


注意：以上写法只为提供一个参考示例，不同框架有不同写法，大家可根据项目技术栈灵活封装进行调用。


### 前端有了鉴权后端还需要鉴权吗？
**需要！**

前端的鉴权只是一个辅助功能，对于专业人员这些限制都是可以轻松绕过的，为保证服务器安全，无论前端是否进行了权限校验，后端接口都需要对会话请求再次进行权限校验！



---

<a class="case-btn" href="https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/cases/use/JurAuthController.java"
	target="_blank">
	本章代码示例：Sa-Token 权限认证 —— [ com.pj.cases.use.JurAuthController.java ]
</a>
<a class="dt-btn" href="https://www.wenjuan.ltd/s/ZfIjYr9/" target="_blank">本章小练习：Sa-Token 基础 - 权限认证，章节测试</a>
