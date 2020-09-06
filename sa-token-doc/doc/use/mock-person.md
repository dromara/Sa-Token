# 模拟他人
--- 

- 以上介绍的api都是操作当前账号，对当前账号进行各种鉴权操作，你可能会问，我能不能对别的账号进行一些操作？
- 比如：查看账号`10001`有无某个权限码、获取id账号为`10002`的用户`session`，等等...
- `sa-token`在api设计时充分考虑了这一点，暴露出多个api进行此类操作 


## 有关操作其它账号的api

#### StpUtil.getTokenValueByLoginId(Object loginId)
- 获取指定`loginId`的`tokenValue`值 

#### StpUtil.logoutByLoginId(Object loginId)
- 指定`loginId`的会话注销登录（踢人下线）

#### StpUtil.getSessionByLoginId(Object loginId)
- 获取指定`loginId`的`session`(如果此id尚未创建`session`, 则返回`null`)
- 类似API还有：
	- `StpUtil.getSessionByLoginId(Object loginId, boolean isCreate)` 获取当前会话登录id, `isCreate`代表指定是否在无`session`的情况下新建并返回

#### StpUtil.hasPermission(Object loginId, Object pcode)
- 指定`loginId`是否含有指定权限



