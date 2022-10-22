# 二级认证

在某些敏感操作下，我们需要对已登录的会话进行二次验证。

比如代码托管平台的仓库删除操作，尽管我们已经登录了账号，当我们点击 **[删除]** 按钮时，还是需要再次输入一遍密码，这么做主要为了两点：

1. 保证操作者是当前账号本人。
2. 增加操作步骤，防止误删除重要数据。

这就是我们本篇要讲的 —— 二级认证，即：在已登录会话的基础上，进行再次验证，提高会话的安全性。


--- 

### 具体API

在`Sa-Token`中进行二级认证非常简单，只需要使用以下API：

``` java
// 在当前会话 开启二级认证，时间为120秒
StpUtil.openSafe(120); 

// 获取：当前会话是否处于二级认证时间内
StpUtil.isSafe(); 

// 检查当前会话是否已通过二级认证，如未通过则抛出异常
StpUtil.checkSafe(); 

// 获取当前会话的二级认证剩余有效时间 (单位: 秒, 返回-2代表尚未通过二级认证)
StpUtil.getSafeTime(); 

// 在当前会话 结束二级认证
StpUtil.closeSafe(); 
```


### 一个小示例

一个完整的二级认证业务流程，应该大致如下：
``` java
// 删除仓库
@RequestMapping("deleteProject")
public SaResult deleteProject(String projectId) {
	// 第1步，先检查当前会话是否已完成二级认证 
	if(!StpUtil.isSafe()) {
		return SaResult.error("仓库删除失败，请完成二级认证后再次访问接口");
	}

	// 第2步，如果已完成二级认证，则开始执行业务逻辑
	// ... 

	// 第3步，返回结果 
	return SaResult.ok("仓库删除成功"); 
}

// 提供密码进行二级认证 
@RequestMapping("openSafe")
public SaResult openSafe(String password) {
	// 比对密码（此处只是举例，真实项目时可拿其它参数进行校验）
	if("123456".equals(password)) {
		
		// 比对成功，为当前会话打开二级认证，有效期为120秒 
		StpUtil.openSafe(120);
		return SaResult.ok("二级认证成功");
	}
	
	// 如果密码校验失败，则二级认证也会失败
	return SaResult.error("二级认证失败"); 
}
```

调用步骤：
1. 前端调用 `deleteProject` 接口，尝试删除仓库。
2. 后端校验会话尚未完成二级认证，返回： `仓库删除失败，请完成二级认证后再次访问接口`。
3. 前端将信息提示给用户，用户输入密码，调用 `openSafe` 接口。
4. 后端比对用户输入的密码，完成二级认证，有效期为：120秒。
5. 前端在 120 秒内再次调用 `deleteProject` 接口，尝试删除仓库。
6. 后端校验会话已完成二级认证，返回：`仓库删除成功`。


### 指定业务标识进行二级认证

如果项目有多条业务线都需要敏感操作验证，则 `StpUtil.openSafe()` 无法提供细粒度的认证操作，
此时我们可以指定一个业务标识来分辨不同的业务线：

``` java
// 在当前会话 开启二级认证，业务标识为client，时间为600秒
StpUtil.openSafe("client", 600); 

// 获取：当前会话是否已完成指定业务的二级认证 
StpUtil.isSafe("client"); 

// 校验：当前会话是否已完成指定业务的二级认证 ，如未认证则抛出异常
StpUtil.checkSafe("client"); 

// 获取当前会话指定业务二级认证剩余有效时间 (单位: 秒, 返回-2代表尚未通过二级认证)
StpUtil.getSafeTime("client"); 

// 在当前会话 结束指定业务标识的二级认证
StpUtil.closeSafe("client"); 
```

业务标识可以填写任意字符串，不同业务标识之间的认证互不影响，比如：
``` java
// 打开了业务标识为 client 的二级认证 
StpUtil.openSafe("client"); 

// 判断是否处于 shop 的二级认证，会返回 false 
StpUtil.isSafe("shop");  // 返回 false 

// 也不会通过校验，会抛出异常 
StpUtil.checkSafe("shop"); 
```



### 使用注解进行二级认证
在一个方法上使用 `@SaCheckSafe` 注解，可以在代码进入此方法之前进行一次二级认证
``` java
// 二级认证：必须二级认证之后才能进入该方法 
@SaCheckSafe      
@RequestMapping("add")
public String add() {
    return "用户增加";
}

// 指定业务类型，进行二级认证校验
@SaCheckSafe("art")
@RequestMapping("add2")
public String add2() {
    return "文章增加";
}
```

详细使用方法可参考：[注解鉴权](/use/at-check)，此处不再赘述



---

<a class="case-btn" href="https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/cases/up/SafeAuthController.java"
	target="_blank">
	本章代码示例：Sa-Token 二级认证 —— [ com.pj.cases.up.SafeAuthController.java ]
</a>