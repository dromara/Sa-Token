# 登录验证
--- 


## 核心思想

- 所谓登录验证，说白了就是限制某些接口只有登录后才能访问（如：查询我的账号资料）
- 如何判断你有没有登录？当然是登录成功后我给你做个标记
- 在需要鉴权的接口里检查标记，有标记者视为已登录，无标记者视为未登录
- 根据以上思路，我们很容易想到以下api：


## 具体API

#### StpUtil.setLoginId(Object login_id)
- 标记当前会话登录的账号id
- 建议的参数类型：long | int | String， 不可以传入复杂类型，如：User、Admin等等

#### StpUtil.logout()
- 当前会话注销登录 

#### StpUtil.isLogin()
- 获取当前会话是否已经登录，返回true=已登录，false=未登录

#### StpUtil.getLoginId()
- 获取当前会话登录id, 如果未登录，则抛出异常：`NotLoginException`
- 类似API还有：
	- `StpUtil.getLoginId_asString()`		获取当前会话登录id, 并转化为String类型
	- `StpUtil.getLoginId_asInt()`		获取当前会话登录id, 并转化为int类型
	- `StpUtil.getLoginId_asLong()`		获取当前会话登录id, 并转化为long类型

#### StpUtil.getLoginId(T default_value)
- 获取当前会话登录id, 如果未登录，则返回默认值 （default_value可以为任意类型）
- 类似API还有：
	- `StpUtil.getLoginId_defaultNull()`		获取当前会话登录id, 如果未登录，则返回null 



