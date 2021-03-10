# Session会话
--- 

### Session是什么？

Session是会话中专业的数据缓存组件，通过`Session`我们可以很方便的缓存一些高频读写数据，提高程序性能<br>
在`sa-token`中, `Session` 分为三种, 分别是：
- `User-Session`: 指的是框架为每个`loginId`分配的`Session` 
- `Token-Session`: 指的是框架为每个`token`分配的`Session` 
- `自定义Session`: 指的是以一个`特定的值`作为SessionId，来分配的`Session` 

> 有关User-Session与Token-Session的详细区别，请参考：[Session模型详解](/fun/session-model)


### User-Session
有关账号Session的API如下：
``` java
// 获取当前账号id的Session (必须是登录后才能调用)
StpUtil.getSession();

// 获取当前账号id的Session, 并决定在Session尚未创建时，是否新建并返回
StpUtil.getSession(true);

// 获取账号id为10001的Session
StpUtil.getSessionByLoginId(10001);

// 获取账号id为10001的Session, 并决定在Session尚未创建时，是否新建并返回
StpUtil.getSessionByLoginId(10001, true);

// 获取SessionId为xxxx-xxxx的Session, 在Session尚未创建时, 返回null 
StpUtil.getSessionBySessionId("xxxx-xxxx");
```


### Token-Session
有关令牌Session的API如下：
``` java
// 获取当前token的专属Session 
StpUtil.getTokenSession();

// 获取指定token的专属Session 
StpUtil.getTokenSessionByToken(token);
```
在未登录状态下是否可以获取`Token-Session`？这取决于你配置的`tokenSessionCheckLogin`值是否为false，详见：[框架配置](/use/config?id=所有可配置项)


### 自定义Session
自定义Session指的是以一个`特定的值`作为SessionId来分配的`Session`, 借助自定义Session，你可以为系统中的任意元素分配相应的session<br>
例如以商品id作为key为每个商品分配一个Session，以便于缓存和商品相关的数据，其相关API如下：
``` java
// 查询指定key的Session是否存在
SaSessionCustomUtil.isExists("goods-10001");

// 获取指定key的Session，如果没有，则新建并返回
SaSessionCustomUtil.getSessionById("goods-10001");

// 获取指定key的Session，如果没有，第二个参数决定是否新建并返回  
SaSessionCustomUtil.getSessionById("goods-10001", false);   

// 删除指定key的Session
SaSessionCustomUtil.deleteSessionById("goods-10001");
```


### Session相关操作
那么获取到的`SaSession`具体有哪些方法可供操作？
``` java
session.getId();                          // 返回此Session的id 
session.getCreateTime();                  // 返回此Session的创建时间 (时间戳) 
session.getAttribute('name');             // 在Session上获取一个值 
session.getAttribute('name', 'zhang');    // 在Session上获取一个值，并指定取不到值时返回的默认值
session.setAttribute('name', 'zhang');    // 在Session上写入一个值 
session.removeAttribute('name');          // 在Session上移除一个值 
session.clearAttribute();                 // 清空此Session的所有值 
session.containsAttribute('name');        // 获取此Session是否含有指定key (返回true或false)
session.attributeKeys();                  // 获取此Session会话上所有key (返回Set<String>)
session.getDataMap();                     // 返回此Session会话上的底层数据对象（如果更新map里的值，请调用session.update()方法避免产生脏数据）
session.update();                         // 将这个Session从持久库更新一下
session.logout();                         // 注销此Session会话 (从持久库删除此Session)
```
具体可参考`javax.servlet.http.HttpSession`，`SaSession`所含方法与其大体类似


