# 常用类、方法
本篇介绍Sa-Token中一些常用的全局对象、类

--- 

### SaManager
SaManager 负责管理 Sa-Token 所有运行时对象 
``` java
SaManager.getConfig();              // 获取全局配置对象 
SaManager.getSaTokenDao();          // 获取数据持久化对象 
SaManager.getStpInterface();        // 获取权限认证对象 
SaManager.getSaTokenAction();       // 获取框架行为对象
SaManager.getSaTokenContext();      // 获取上下文处理对象
SaManager.getSaTokenListener();     // 获取侦听器对象 
SaManager.getStpLogic("type");      // 获取指定账号类型的StpLogic对象 
```


### SaHolder
Sa-Token上下文持有类，通过此类快速获取当前环境的相关对象 
``` java
SaHolder.getRequest();           // 获取当前请求的 [Request] 对象 
SaHolder.getResponse();          // 获取当前请求的 [Response] 对象 
SaHolder.getStorage();           // 获取当前请求的 [存储器] 对象
```


### SaRouter
路由匹配工具类，详细戳：[路由拦截式鉴权](/use/route-check)


### SaFoxUtil
Sa-Token内部工具类，包含一些工具方法 
``` java
SaFoxUtil.printSaToken();           // 打印 Sa-Token 版本字符画
SaFoxUtil.getRandomString(8);       // 生成指定长度的随机字符串
SaFoxUtil.isEmpty(str);             // 指定字符串是否为null或者空字符串
SaFoxUtil.getMarking28();           // 以当前时间戳和随机int数字拼接一个随机字符串
SaFoxUtil.formatDate(date);         // 将日期格式化为yyyy-MM-dd HH:mm:ss字符串
SaFoxUtil.searchList();             // 从集合里查询数据
SaFoxUtil.vagueMatch(patt, str);    // 字符串模糊匹配
```

### SaTokenConfigFactory
配置对象工厂类，通过此类你可以方便的根据properties配置文件创建一个配置对象 

1、首先在项目根目录，创建一个配置文件：`sa-token.properties`

``` java
# token名称 (同时也是cookie名称)
tokenName=satoken
# token有效期，单位s 默认30天, -1代表永不过期 
timeout=2592000
# token临时有效期 (指定时间内无操作就视为token过期) 单位: 秒
activityTimeout=-1
# 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录) =-1
isConcurrent=true
# 在多人登录同一账号时，是否共用一个token (为true时所有登录共用一个token, 为false时每次登录新建一个token) 
isShare=true
# token风格
isLog=false
```

2、然后使用以下代码获取配置对象 
``` java
// 设置配置文件地址 
SaTokenConfigFactory.configPath = "sa-token.properties";

// 获取配置信息到 config 对象
SaTokenConfig config = SaTokenConfigFactory.createConfig();

// 注入到 SaManager 中
SaManager.setConfig(config);
```


### SpringMVCUtil
SpringMVC操作的工具类，位于包：`sa-token-spring-boot-starter`
``` java
SpringMVCUtil.getRequest();           // 获取本次请求的 request 对象 
SpringMVCUtil.getResponse();          // 获取本次请求的 response 对象 
```


### SaReactorHolder & SaReactorSyncHolder
Sa-Token集成Reactor时的 ServerWebExchange 工具类，位于包：`sa-token-reactor-spring-boot-starter`
``` java
// 异步方式获取 ServerWebExchange 对象 
SaReactorHolder.getContext().map(e -> {
	System.out.println(e);
});

// 同步方式获取 ServerWebExchange 对象 
ServerWebExchange e = SaReactorSyncHolder.getContext();
System.out.println(e);
```


