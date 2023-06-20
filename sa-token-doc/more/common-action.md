# 全局类、方法
本篇介绍 Sa-Token 中一些常用的全局对象、类

--- 

### SaManager
SaManager 负责管理 Sa-Token 所有全局组件。
``` java
SaManager.getConfig();                 // 获取全局配置对象 
SaManager.getSaTokenDao();             // 获取数据持久化对象 
SaManager.getStpInterface();           // 获取权限认证对象 
SaManager.getSaTokenContext();         // 获取一级Context处理对象
SaManager.getSaTokenSecondContext();   // 获取二级Context处理对象
SaManager.getSaTokenContextOrSecond(); // 获取一个可用的 Context 处理对象
SaManager.getSaTokenListener();        // 获取侦听器对象 
SaManager.getSaTemp();                 // 获取临时令牌认证模块对象 
SaManager.getSaJsonTemplate();         // 获取 JSON 转换器 Bean
SaManager.getSaSignTemplate();         // 获取参数签名 Bean 
SaManager.getStpLogic("type");         // 获取指定账号类型的StpLogic对象，获取不到时自动创建并返回 
SaManager.getStpLogic("type", false);  // 获取指定账号类型的StpLogic对象，获取不到时抛出异常 
SaManager.putStpLogic(stpLogic);       // 向全局集合中 put 一个 StpLogic 
```


### SaHolder
Sa-Token上下文持有类，通过此类快速获取当前环境的相关对象 
``` java
SaHolder.getContext();           // 获取当前请求的 SaTokenContext
SaHolder.getRequest();           // 获取当前请求的 [Request] 对象 
SaHolder.getResponse();          // 获取当前请求的 [Response] 对象 
SaHolder.getStorage();           // 获取当前请求的 [Storage] 对象
SaHolder.getApplication();       // 获取全局 SaApplication 对象
```


### SaRouter
路由匹配工具类，详细戳：[路由拦截式鉴权](/use/route-check)


### SaFoxUtil
Sa-Token内部工具类，包含一些工具方法 
``` java
SaFoxUtil.printSaToken();           // 打印 Sa-Token 版本字符画
SaFoxUtil.getRandomString(8);       // 生成指定长度的随机字符串
SaFoxUtil.isEmpty(str);             // 指定字符串是否为null或者空字符串
SaFoxUtil.isNotEmpty(str);          // 指定字符串是否不是null或者空字符串
SaFoxUtil.equals(a, b);             // 比较两个对象是否相等 
SaFoxUtil.getMarking28();           // 以当前时间戳和随机int数字拼接一个随机字符串
SaFoxUtil.formatDate(date);         // 将日期格式化为yyyy-MM-dd HH:mm:ss字符串
SaFoxUtil.searchList(dataList, prefix, keyword, start, size, sortType);             // 从集合里查询数据
SaFoxUtil.searchList(dataList, start, size, sortType);       // 从集合里查询数据
SaFoxUtil.vagueMatch(patt, str);    // 字符串模糊匹配
SaFoxUtil.getValueByType(obj, cs);    // 将指定值转化为指定类型
SaFoxUtil.joinParam(url, parameStr);    // 在url上拼接上kv参数并返回 
SaFoxUtil.joinParam(url, key, value);    // 在url上拼接上kv参数并返回 
SaFoxUtil.joinSharpParam(url, parameStr);    // 在url上拼接锚参数 
SaFoxUtil.joinSharpParam(url, key, value);    // 在url上拼接锚参数 
SaFoxUtil.arrayJoin(arr);    // 将数组的所有元素使用逗号拼接在一起
SaFoxUtil.isUrl(str);    // 使用正则表达式判断一个字符串是否为URL
SaFoxUtil.encodeUrl(str);    // URL编码 
SaFoxUtil.decoderUrl(str);    // URL解码 
SaFoxUtil.convertStringToList(str);    // 将指定字符串按照逗号分隔符转化为字符串集合 
SaFoxUtil.convertListToString(list);    // 将指定集合按照逗号连接成一个字符串 
SaFoxUtil.convertStringToArray(str);    // String 转 Array，按照逗号切割 
SaFoxUtil.convertArrayToString(arr);    // Array 转 String，按照逗号切割 
SaFoxUtil.emptyList();    // 返回一个空集合
SaFoxUtil.toList(... strs);    // String 数组转集合 
```


### SaTokenConfigFactory
配置对象工厂类，通过此类你可以方便的根据 properties 配置文件创建一个配置对象 

1、首先在项目根目录，创建一个配置文件：`sa-token.properties`

``` properties
# token 名称 (同时也是 cookie 名称)
tokenName=satoken
# token 有效期（单位：秒） 默认30天，-1 代表永久有效
timeout=2592000
# token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
activeTimeout=-1
# 是否允许同一账号多地同时登录 （为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
isConcurrent=true
# 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token, 为 false 时每次登录新建一个 token）
isShare=true
# token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
tokenStyle=uuid
# 是否输出操作日志 
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
SpringMVCUtil.isWeb();                // 判断当前是否处于 Web 上下文中  
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


