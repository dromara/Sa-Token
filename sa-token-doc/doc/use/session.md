# Session会话
--- 

### Session是什么？

Session是会话中专业的数据缓存组件，通过 Session 我们可以很方便的缓存一些高频读写数据，提高程序性能，例如：

``` java
// 在登录时缓存user对象 
StpUtil.getSession().set("user", user);

// 然后我们就可以在任意处使用这个user对象
SysUser user = (SysUser) StpUtil.getSession().get("user");
```

在 Sa-Token 中，Session 分为三种，分别是：

- `User-Session`: 指的是框架为每个 账号id 分配的 Session 
- `Token-Session`: 指的是框架为每个 token 分配的 Session  
- `Custom-Session`: 指的是以一个 特定的值 作为SessionId，来分配的 Session 

> 有关User-Session与Token-Session的详细区别，可参考：[Session模型详解](/fun/session-model)


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


### 在 Session 上存取值

``` java
// 写值 
session.set("name", "zhang"); 

// 写值 (只有在此key原本无值的时候才会写入)
session.setDefaultValue("name", "zhang");

// 取值
session.get("name");

// 取值 (指定默认值)
session.get("name", "<defaultValue>"); 

// ---------- 数据类型转换： ----------
session.getInt("age");         // 取值 (转int类型)
session.getLong("age");        // 取值 (转long类型)
session.getString("name");     // 取值 (转String类型)
session.getDouble("result");   // 取值 (转double类型)
session.getFloat("result");    // 取值 (转float类型)
session.getModel("key", Student.class);     // 取值 (指定转换类型)
session.getModel("key", Student.class, <defaultValue>);  // 取值 (指定转换类型, 并指定值为Null时返回的默认值)

// 是否含有某个key (返回true或false)
session.has("key"); 

// 删值 
session.delete('name');          

// 清空所有值 
session.clear();                 

// 获取此 Session 的所有key (返回Set<String>)
session.keys();      
```


### 其它操作

``` java
// 返回此 Session 的id 
session.getId();                          

// 返回此 Session 的创建时间 (时间戳) 
session.getCreateTime();                  

// 返回此 Session 会话上的底层数据对象（如果更新map里的值，请调用session.update()方法避免产生脏数据）
session.getDataMap();                     

// 将这个 Session 从持久库更新一下
session.update();                         

// 注销此 Session 会话 (从持久库删除此Session)
session.logout();                         
```


### Session环境隔离说明
有同学经常会把 `SaSession` 与 `HttpSession` 进行混淆，例如：
``` java
@PostMapping("/resetPoints")
public void reset(HttpSession session) {
	// 在HttpSession上写入一个值 
    session.setAttribute("name", 66);
	// 在SaSession进行取值
    System.out.println(StpUtil.getSession().getAttribute("name"));	// 输出null
}
```
**要点：**
1. `SaSession` 与 `HttpSession` 没有任何关系，在`HttpSession`上写入的值，在`SaSession`中无法取出
2. `HttpSession`并未被框架接管，在使用Sa-Token时，请在任何情况下均使用`SaSession`，不要使用`HttpSession` 

