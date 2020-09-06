# session会话
--- 


## 账号session
账号`session`指的是为每个登录账号分配的`session` 

#### StpUtil.getSession()
- 返回当前登录账号的`session`（必须是登录后才能调用）


## 自定义session
自定义`session`指的是未登录状态下，以一个特定的值作为key，来分配的`session` 

#### SaSessionCustomUtil.isExists(String sessionId)
- 查询指定key的`session`，是否存在

#### SaSessionCustomUtil.getSessionById(String sessionId)
- 获取指定key的`session`，如果没有，则新建并返回  

#### SaSessionCustomUtil.delSessionById(String sessionId)
- 删除指定key的`session`


## session相关操作
那么获取到的`SaSession`具体有哪些方法可供操作？

#### getId()
- 返回此`session`的id

#### setAttribute(String key, Object value)
- 在此`session`对象上写入值 

#### getAttribute(String key)
- 在此`session`对象上查询值 

具体可参考`javax.servlet.http.HttpSession`，`SaSession`所含方法与其大体类似