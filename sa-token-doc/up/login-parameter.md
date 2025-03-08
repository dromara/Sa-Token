# 登录参数

在之前的章节我们提到，通过 `StpUtil.login(xxx)` 可以完成指定账号登录，同时你可以指定第二个参数来扩展登录信息，比如：

``` java
// 指定`账号id`和`设备类型`进行登录
StpUtil.login(10001, "PC");    

// 设置登录账号 id 为 10001，并指定是否为 “记住我” 模式
StpUtil.login(10001, false);
```

除了以上内容，第二个参数你还可以指定一个 `SaLoginParameter`，来详细控制登录的多个细节，例如：

``` java
StpUtil.login(10001, new SaLoginParameter()
		.setDeviceType("PC")             // 此次登录的客户端设备类型, 一般用于完成 [同端互斥登录] 功能
		.setDeviceId("xxxxxxxxx")        // 此次登录的客户端设备ID, 登录成功后该设备将标记为可信任设备
		.setIsLastingCookie(true)        // 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
		.setTimeout(60 * 60 * 24 * 7)    // 指定此次登录 token 的有效期, 单位:秒，-1=永久有效
		.setActiveTimeout(60 * 60 * 24 * 7) // 指定此次登录 token 的最低活跃频率, 单位:秒，-1=不进行活跃检查
		.setIsConcurrent(true)           // 是否允许同一账号多地同时登录 （为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
		.setIsShare(true)                // 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个token, 为 false 时每次登录新建一个 token）
		.setMaxLoginCount(12)            // 同一账号最大登录数量，-1代表不限 （只有在 isConcurrent=true, isShare=false 时此配置项才有意义）
		.setMaxTryTimes(12)              // 在每次创建 token 时的最高循环次数，用于保证 token 唯一性（-1=不循环尝试，直接使用）
		.setExtra("key", "value")        // 记录在 Token 上的扩展参数（只在 jwt 模式下生效）
		.setToken("xxxx-xxxx-xxxx-xxxx") // 预定此次登录的生成的Token 
		.setIsWriteHeader(false)         // 是否在登录后将 Token 写入到响应头
		.setTerminalExtra("key", "value")// 本次登录挂载到 SaTerminalInfo 的自定义扩展数据
);
```

以上大部分参数在未指定时将使用全局配置作为默认值。


