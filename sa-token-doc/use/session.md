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

- `Account-Session`: 指的是框架为每个 账号id 分配的 Session 
- `Token-Session`: 指的是框架为每个 token 分配的 Session  
- `Custom-Session`: 指的是以一个 特定的值 作为SessionId，来分配的 Session 

> 有关Account-Session与Token-Session的详细区别，可参考：[Session模型详解](/fun/session-model)


### Account-Session
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
// 获取当前 Token 的 Token-Session 对象
StpUtil.getTokenSession();

// 获取指定 Token 的 Token-Session 对象
StpUtil.getTokenSessionByToken(token);
```


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

// 取值 (若无值则执行参数方法, 之后将结果保存到此键名下,并返回此结果   若有值则直接返回, 无需执行参数方法)
session.get("name", () -> {
            return ...;
        });

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
	// 在 HttpSession 上写入一个值 
    session.setAttribute("name", 66);
	// 在 SaSession 进行取值
    System.out.println(StpUtil.getSession().get("name"));	// 输出null
}
```
**要点：**
1. `SaSession` 与 `HttpSession` 没有任何关系，在`HttpSession`上写入的值，在`SaSession`中无法取出
2. `HttpSession`并未被框架接管，在使用Sa-Token时，请在任何情况下均使用`SaSession`，不要使用`HttpSession` 


### 未登录场景下获取 Token-Session 

默认场景下，只有登录后才能通过 `StpUtil.getTokenSession()` 获取 `Token-Session`。

如果想要在未登录场景下获取 Token-Session ，有两种方法：

- 方法一：将全局配置项 `tokenSessionCheckLogin` 改为 false，详见：[框架配置](/use/config?id=所有可配置项)
- 方法二：使用匿名 Token-Session

``` java
// 获取当前 Token 的匿名 Token-Session （可在未登录情况下使用的 Token-Session）
StpUtil.getAnonTokenSession();
```

注意点：如果前端没有提交 Token ，或者提交的 Token 是一个无效 Token 的话，框架将不会根据此 Token 创建 `Token-Session` 对象，
而是随机一个新的 Token 值来创建 `Token-Session` 对象，此 Token 值可以通过 `StpUtil.getTokenValue()` 获取到。


---

<a class="case-btn" href="https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/cases/use/SaSessionController.java"
	target="_blank">
	本章代码示例：Sa-Token Session 会话 —— [ com.pj.cases.use.SaSessionController.java ]
</a>
<a class="dt-btn" href="https://www.wenjuan.ltd/s/MNnUr2V/" target="_blank">本章小练习：Sa-Token 基础 - Session 会话，章节测试</a>

