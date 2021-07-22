# 同端互斥登录

如果你经常使用腾讯QQ，就会发现它的登录有如下特点：它可以手机电脑同时在线，但是不能在两个手机上同时登录一个账号 <br/>
同端互斥登录，指的就是像腾讯QQ一样，在同一类型设备上只允许单地点登录，在不同类型设备上允许同时在线

--- 

## 具体API

在 Sa-Token 中如何做到同端互斥登录? <br/>
首先在配置文件中，将 `isConcurrent` 配置为false，然后调用登录等相关接口时声明设备标识即可：


#### 指定设备标识登录
``` java
// 指定`账号id`和`设备标识`进行登录
StpUtil.login(10001, "PC");	
```
调用此方法登录后，同设备的会被顶下线（不同设备不受影响），再次访问系统时会抛出 `NotLoginException` 异常，场景值=`-4`


#### 指定设备标识强制注销
``` java
// 指定`账号id`和`设备标识`进行强制注销 (踢人下线)
StpUtil.logoutByLoginId(10001, "PC");	
```
如果第二个参数填写null或不填，代表将这个账号id所有在线端踢下线，被踢出者再次访问系统时会抛出 `NotLoginException` 异常，场景值=`-5`


#### 查询当前登录的设备标识
``` java
// 返回当前token的登录设备
StpUtil.getLoginDevice();	
```


#### Id 反查 Token
``` java
// 获取指定loginId指定设备端的tokenValue 
StpUtil.getTokenValueByLoginId(10001, "APP");	
```


> 不同设备账号在登录时设置不同的token有效期等信息, 详见[登录时指定token有效期](/use/remember-me?id=登录时指定token有效期)