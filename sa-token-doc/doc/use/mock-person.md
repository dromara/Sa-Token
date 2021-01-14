# 模拟他人
--- 


- 以上介绍的api都是操作当前账号，对当前账号进行各种鉴权操作，你可能会问，我能不能对别的账号进行一些操作？
- 比如：查看账号`10001`有无某个权限码、获取id账号为`10002`的用户`User-Session`，等等...
- `sa-token`在api设计时充分考虑了这一点，暴露出多个api进行此类操作 


## 有关操作其它账号的api

``` java
StpUtil.getTokenValueByLoginId(10001);         // 获取指定账号10001的`tokenValue`值 
StpUtil.logoutByLoginId(10001);                // 将账号10001的会话注销登录（踢人下线）
StpUtil.getSessionByLoginId(10001);            // 获取账号10001的Session对象, 如果session尚未创建, 则新建并返回
StpUtil.getSessionByLoginId(10001, false);     // 获取账号10001的Session对象, 如果session尚未创建, 则返回null 
StpUtil.hasRole("super-admin");                // 获取账号10001是否含有指定角色标识 
StpUtil.hasPermission("user:add");             // 获取账号10001是否含有指定权限码
```



## 临时身份切换

有时候，我们需要直接将当前会话的身份切换为其它账号，比如：
``` java
StpUtil.switchTo(10044);    // 将当前会话[身份临时切换]为其它账号 
StpUtil.getLoginId();       // 此时再调用此方法会返回 10044 (我们临时切换到的账号id)
StpUtil.endSwitch();        // 结束 [身份临时切换]
```

你还可以: 直接在一个代码段里方法内，临时切换身份为指定loginId (此方式无需手动调用`StpUtil.endSwitch()`关闭身份切换)
``` java
System.out.println("------- [身份临时切换]调用开始...");
StpUtil.switchTo(10044, new SaFunction() {
	@Override
	public void run() {
		System.out.println("是否正在身份临时切换中: " + StpUtil.isSwitch()); 
		System.out.println("获取当前登录账号id: " + StpUtil.getLoginId());
	}
});
System.out.println("------- [身份临时切换]调用结束...");
```

如果你使用的JDK版本是1.8或以上，上面这一坨可以简写为以下形式：
``` java
System.out.println("------- [身份临时切换]调用开始...");
StpUtil.switchTo(10044, () -> {
	System.out.println("是否正在身份临时切换中: " + StpUtil.isSwitch()); 
	System.out.println("获取当前登录账号id: " + StpUtil.getLoginId());
});
System.out.println("------- [身份临时切换]调用结束...");
```


