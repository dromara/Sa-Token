# 三大作用域 

--- 

Sa-Token 数据存储有三大作用域，分别是：
- `SaStorage` - 请求作用域：存储的数据只在一次请求内有效。
- `SaSession` - 会话作用域：存储的数据在一次会话范围内有效。
- `SaApplication` - 全局作用域：存储的数据在全局范围内有效。


### SaStorage - 请求作用域
在 SaStorage 中存储的数据只在一次请求范围内有效，请求结束后数据自动清除。使用 SaStorage 时无需处于登录状态。

``` java
SaStorage storage = SaHolder.getStorage();
storage.get("key");   // 取值
storage.set("key", "value");   // 写值 
storage.delete("key");   // 删值 
```


### SaSession - 会话作用域
在 SaSession 存储的数据在一次会话范围内有效，会话结束后数据自动清除。必须登录后才能使用 SaSession 对象。

``` java
SaSession session = StpUtil.getSession();
session.get("key");   // 取值
session.set("key", "value");   // 写值 
session.delete("key");   // 删值 
```


### SaApplication - 全局作用域
在 SaApplication 存储的数据在全局范围内有效，应用关闭后数据自动清除（如果集成了 Redis 那则是 Redis 关闭后数据自动清除）。使用 SaApplication 时无需处于登录状态。

``` java
SaApplication application = SaHolder.getApplication();
application.get("key");   // 取值
application.set("key", "value");   // 写值 
application.delete("key");   // 删值 
```

