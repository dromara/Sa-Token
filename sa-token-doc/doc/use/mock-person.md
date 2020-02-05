# 模拟他人
--- 

- 以上介绍的api都是操作当前账号，对当前账号进行各种鉴权操作，你可能会问，我能不能对别的账号进行一些操作？
- 比如：查看账号10001有无某个权限码、获取id账号为10002的用户session，等等...
- `sa-token`在api设计时充分考虑了这一点，暴露出多个api进行此类操作 


## 有关操作其它账号的api

#### StpUtil.getTokenValueByLoginId(Object login_id)
- 获取指定login_id的tokenValue值 

#### StpUtil.logoutByLoginId(Object login_id)
- 指定login_id的会话注销登录（踢人下线）

#### StpUtil.getSessionByLoginId(Object login_id)
- 获取指定login_id的session

#### StpUtil.hasPermission(Object login_id, Object pcode)
- 指定login_id是否含有指定权限



