# Sa-Token 中的 Session会话 模型详解

--- 

### 1、Account-Session 

提起Session，你脑海中最先浮现的可能就是 JSP 中的 HttpSession，它的工作原理可以大致总结为：

客户端每次与服务器第一次握手时，会被强制分配一个 `[唯一id]` 作为身份标识，注入到 Cookie 之中，
之后每次发起请求时，客户端都要将它提交到后台，服务器根据 `[唯一id]` 找到每个请求专属的Session对象，维持会话

这种机制简单粗暴，却有N多明显的缺点：

1. 同一账号分别在PC、APP登录，会被识别为两个不相干的会话 
2. 一个设备难以同时登录两个账号
3. 每次一个新的客户端访问服务器时，都会产生一个新的Session对象，即使这个客户端只访问了一次页面 
4. 在不支持Cookie的客户端下，这种机制会失效 


Sa-Token Session可以理解为 HttpSession 的升级版：

1. Sa-Token只在调用`StpUtil.login(id)`登录会话时才会产生Session，不会为每个陌生会话都产生Session，节省性能 
2. 在登录时产生的Session，是分配给账号id的，而不是分配给指定客户端的，也就是说在PC、APP上登录的同一账号所得到的Session也是同一个，所以两端可以非常轻松的同步数据  
3. Sa-Token支持Cookie、Header、body三个途径提交Token，而不是仅限于Cookie 
4. 由于不强依赖Cookie，所以只要将Token存储到不同的地方，便可以做到一个客户端同时登录多个账号 

这种为账号id分配的Session，我们给它起一个合适的名字：`Account-Session`，你可以通过如下方式操作它：
``` java
// 获取当前会话的 Account-Session 
SaSession session = StpUtil.getSession();

// 从 Account-Session 中读取、写入数据 
session.get("name");
session.set("name", "张三");
```

使用`Account-Session`在不同端同步数据是非常方便的，因为只要 PC 和 APP 登录的账号id一致，它们对应的都是同一个Session，
举个应用场景：在PC端点赞的帖子列表，在APP端的点赞记录里也要同步显示出来


### 2、Token-Session  

随着业务推进，我们还可能会遇到一些需要数据隔离的场景：

> [!NOTE| label:业务场景] 
> 指定客户端超过两小时无操作就自动下线，如果两小时内有操作，就再续期两小时，直到新的两小时无操作 

那么这种请求访问记录应该存储在哪里呢？放在 Account-Session 里吗？

可别忘了，PC端和APP端可是共享的同一个 Account-Session ，如果把数据放在这里，
那就意味着，即使用户在PC端一直无操作，只要手机上用户还在不间断的操作，那PC端也不会过期！

解决这个问题的关键在于，虽然两个设备登录的是同一账号，但是两个它们得到的token是不一样的，
Sa-Token针对会话登录，不仅为账号id分配了`Account-Session`，同时还为每个token分配了不同的`Token-Session`

不同的设备端，哪怕登录了同一账号，只要它们得到的token不一致，它们对应的 `Token-Session` 就不一致，这就为我们不同端的独立数据读写提供了支持：

``` java
// 获取当前会话的 Token-Session 
SaSession session = StpUtil.getTokenSession();

// 从 Token-Session 中读取、写入数据 
session.get("name");
session.set("name", "张三");
```

### 3、Custom-Session

除了以上两种Session，Sa-Token还提供了第三种Session，那就是：`Custom-Session`，你可以将其理解为：自定义Session

Custom-Session不依赖特定的 账号id 或者 token，而是依赖于你提供的SessionId：

``` java
// 获取指定key的 Custom-Session 
SaSession session = SaSessionCustomUtil.getSessionById("goods-10001");

// 从 Custom-Session 中读取、写入数据 
session.get("name");
session.set("name", "张三");
```

只要两个自定义Session的Id一致，它们就是同一个Session 

Custom-Session的会话有效期默认使用`SaManager.getConfig().getTimeout()`, 如果需要修改会话有效期, 可以在创建之后, 使用对象方法修改

``` java
session.updateTimeout(1000); // 参数说明和全局有效期保持一致
```


### 4、Session模型结构图 

三种Session创建时机：

- `Account-Session`: 指的是框架为每个 账号id 分配的 Session 
- `Token-Session`: 指的是框架为每个 token 分配的 Session  
- `Custom-Session`: 指的是以一个 特定的值 作为SessionId，来分配的 Session 


**假设三个客户端登录同一账号，且配置了不共享token，那么此时的Session模型是：**

![session-model](https://oss.dev33.cn/sa-token/doc/session-model3.png 's-w')

简而言之：
- `Account-Session`  以账号 id 为主，只要 token 指向的账号 id 一致，那么对应的Session对象就一致
- `Token-Session` 以token为主，只要token不同，那么对应的Session对象就不同
- `Custom-Session` 以特定的key为主，不同key对应不同的Session对象，同样的key指向同一个Session对象 



