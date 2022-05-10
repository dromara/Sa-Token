# 踢人下线
所谓踢人下线，核心操作就是找到其指定`loginId`对应的`token`，并设置其失效

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


### 3、账号封禁
对于违规账号，有时候我们仅仅将其踢下线还是远远不够的，我们还需要对其进行**账号封禁**防止其再次登录

``` java
// 封禁指定账号 
// 参数一：账号id
// 参数二：封禁时长，单位：秒  (86400秒=1天，此值为-1时，代表永久封禁)
StpUtil.disable(10001, 86400); 

// 获取指定账号是否已被封禁 (true=已被封禁, false=未被封禁) 
StpUtil.isDisable(10001); 

// 获取指定账号剩余封禁时间，单位：秒
StpUtil.getDisableTime(10001); 

// 解除封禁
StpUtil.untieDisable(10001); 
```


#### 注意点
对于正在登录的账号，对其账号封禁时并不会使其立刻注销<br>
如果需要将其封禁后立即掉线，可采取先踢再封禁的策略，例如：
``` java
// 先踢下线
StpUtil.kickout(10001); 
// 再封禁账号
StpUtil.disable(10001, 86400); 
```

