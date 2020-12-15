# 踢人下线
--- 


## 核心思想

- 所谓踢人下线，核心操作就是找到其指定`loginId`的token，并设置其失效


## 具体API

#### StpUtil.logoutByLoginId(Object loginId)
- 让指定loginId的会话注销登录（清退下线）

#### StpUtil.kickoutByLoginId(Object loginId)
- 让指定loginId的会话注销登录（踢人下线）

## 详解
- `logoutByLoginId` 和 `kickoutByLoginId` 都可以将用户强制下线，不同点在于：
	- `logoutByLoginId` 是将人清退，用户得到的提示是 [token无效] ，对于失效原因尚未可知 （NotLoginException场景值为-2） 
	- `kickoutByLoginId` 是将人踢下线，用户可得到明确提示 [已被踢下线] （NotLoginException场景值为-5） 
