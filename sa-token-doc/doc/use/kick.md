# 踢人下线
所谓踢人下线，核心操作就是找到其指定`loginId`的token，并设置其失效

--- 


## 具体API

#### StpUtil.logoutByLoginId(Object loginId)
让指定loginId的会话注销登录（踢人下线），例如：

``` java
// 使账号id为10001的会话注销登录，待到10001再次访问系统时会抛出`NotLoginException`异常，场景值为-5
StpUtil.logoutByLoginId(10001); 
```

