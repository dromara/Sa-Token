# 踢人下线

所谓踢人下线，核心操作就是找到指定 `loginId` 对应的 `Token`，并设置其失效。

![踢下线](https://oss.dev33.cn/sa-token/doc/kickout.png)

--- 


### 1、强制注销
``` java
StpUtil.logout(10001);                    // 强制指定账号注销下线 
StpUtil.logout(10001, "PC");              // 强制指定账号指定端注销下线 
StpUtil.logoutByTokenValue("token");      // 强制指定 Token 注销下线 
```


### 2、踢人下线
``` java
StpUtil.kickout(10001);                    // 将指定账号踢下线 
StpUtil.kickout(10001, "PC");              // 将指定账号指定端踢下线
StpUtil.kickoutByTokenValue("token");      // 将指定 Token 踢下线
```

强制注销 和 踢人下线 的区别在于：
- 强制注销等价于对方主动调用了注销方法，再次访问会提示：Token无效。
- 踢人下线不会清除Token信息，而是将其打上特定标记，再次访问会提示：Token已被踢下线。


<button class="show-img" img-src="https://oss.dev33.cn/sa-token/doc/g/g3--kickout.gif">加载动态演示图</button>


---

<a class="case-btn" href="https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/cases/use/KickoutController.java"
	target="_blank">
	本章代码示例：Sa-Token 踢人下线 —— [ KickoutController.java ]
</a>
<a class="dt-btn" href="https://www.wenjuan.ltd/s/MFNN7bK/" target="_blank">本章小练习：Sa-Token 基础 - 踢人下线，章节测试</a>


