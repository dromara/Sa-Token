# 登录参数

### 1、登录参数

在之前的章节我们提到，通过 `StpUtil.login(xxx)` 可以完成指定账号登录，同时你可以指定第二个参数来扩展登录信息，比如：

``` java
// 指定`账号id`和`设备类型`进行登录
StpUtil.login(10001, "PC");    

// 设置登录账号 id 为 10001，并指定是否为 “记住我” 模式
StpUtil.login(10001, false);
```

除了以上内容，第二个参数你还可以指定一个 `SaLoginParameter` 对象，来详细控制登录的多个细节，例如：

``` java
StpUtil.login(10001, new SaLoginParameter()
		.setDeviceType("PC")             // 此次登录的客户端设备类型, 一般用于完成 [同端互斥登录] 功能
		.setDeviceId("xxxxxxxxx")        // 此次登录的客户端设备ID, 登录成功后该设备将标记为可信任设备
		.setIsLastingCookie(true)        // 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
		.setTimeout(60 * 60 * 24 * 7)    // 指定此次登录 token 的有效期, 单位:秒，-1=永久有效
		.setActiveTimeout(60 * 60 * 24 * 7) // 指定此次登录 token 的最低活跃频率, 单位:秒，-1=不进行活跃检查
		.setIsConcurrent(true)           // 是否允许同一账号多地同时登录 （为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
		.setIsShare(false)                // 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个token, 为 false 时每次登录新建一个 token）
		.setMaxLoginCount(12)            // 同一账号最大登录数量，-1代表不限 （只有在 isConcurrent=true, isShare=false 时此配置项才有意义）
		.setMaxTryTimes(12)              // 在每次创建 token 时的最高循环次数，用于保证 token 唯一性（-1=不循环尝试，直接使用）
		.setExtra("key", "value")        // 记录在 Token 上的扩展参数（只在 jwt 模式下生效）
		.setToken("xxxx-xxxx-xxxx-xxxx") // 预定此次登录的生成的Token 
		.setIsWriteHeader(false)         // 是否在登录后将 Token 写入到响应头
		.setTerminalExtra("key", "value")// 本次登录挂载到 SaTerminalInfo 的自定义扩展数据
		.setReplacedRange(SaReplacedRange.CURR_DEVICE_TYPE) // 顶人下线的范围: CURR_DEVICE_TYPE=当前指定的设备类型端, ALL_DEVICE_TYPE=所有设备类型端
		.setOverflowLogoutMode(SaLogoutMode.LOGOUT)         // 溢出 maxLoginCount 的客户端，将以何种方式注销下线: LOGOUT=注销下线, KICKOUT=踢人下线, REPLACED=顶人下线
		.setRightNowCreateTokenSession(true)                // 是否立即创建对应的 Token-Session （true=在登录时立即创建，false=在第一次调用 getTokenSession() 时创建）
		.setupCookieConfig(cookie->{     // 设置 Cookie 配置项 
			cookie.setDomain("sa-token.cc");  // 设置：作用域
			cookie.setPath("/shop");          // 设置：路径 （一般只有当你在一个域名下部署多个项目时才会用到此值。）
			cookie.setSecure(true);           // 设置：是否只在 https 协议下有效
			cookie.setHttpOnly(true);         // 设置：是否禁止 js 操作 Cookie 
			cookie.setSameSite("Lax");        // 设置：第三方限制级别（Strict=完全禁止，Lax=部分允许，None=不限制）
			cookie.addExtraAttr("aa", "bb");  // 设置：额外扩展属性
		}
);
```

以上大部分参数在未指定时将使用全局配置作为默认值。



### 2、注销参数

同样的，在调用注销时，也可以指定一些参数决定注销的细节行为：

``` java
// 当前客户端注销 
StpUtil.logout(new SaLogoutParameter()
		// 注销范围： TOKEN=只注销当前 token 的会话，ACCOUNT=注销当前 token 指向的 loginId 其所有客户端会话
		// 此参数只在调用 StpUtil.logout() 时有效
		.setRange(SaLogoutRange.TOKEN)   
);

// 指定 token 注销
StpUtil.logoutByTokenValue("xxxxxxxxxxxxxxxxxxxxxxx", new SaLogoutParameter()
		// 如果 token 已被冻结，是否保留其操作权 (是否允许此 token 调用注销API)（默认 false）
		// 此参数只在调用 StpUtil.[logout/kickout/replaced]ByTokenValue("token") 时有效
		.setIsKeepFreezeOps(false)  
		// 是否保留此 token 的 Token-Session 对象（默认 false）
		.setIsKeepTokenSession(true)  
);

// 指定 loginId 注销
StpUtil.logout(10001, new SaLogoutParameter()
		.setDeviceType("PC")  // 设置注销的设备类型 (如果不指定，则默认注销所有客户端)
		.setIsKeepTokenSession(true)  // 是否保留对应 token 的 Token-Session 对象（默认 false）
		.setMode(SaLogoutMode.REPLACED)  // 设置注销模式：LOGOUT=注销登录、KICKOUT=踢人下线，REPLACED=顶人下线（默认LOGOUT）
);
```

以上大部分参数在未指定时将使用全局配置作为默认值。


### 3、遍历登录终端详细操作

如果你的 登录策略 或 注销策略 非常复杂，凭借上述参数无法组合出你的业务场景，你可以手动遍历一个账号的已登录终端信息列表，手动决定某个设备是否下线，例如：

``` java
// 测试 
@RequestMapping("logout")
public SaResult logout() {
	
	// 遍历账号 10001 已登录终端列表，进行详细操作
	StpUtil.forEachTerminalList(10001, (session, ter) -> {
		// 根据登录顺序，奇数的保留，偶数的下线
		if(ter.getIndex() % 2 == 0) {
			StpUtil.removeTerminalByLogout(session, ter);   // 注销下线方式 移除这个登录客户端
			// StpUtil.removeTerminalByKickout(session, ter);  // 踢人下线方式 移除这个登录客户端
			// StpUtil.removeTerminalByReplaced(session, ter);  // 顶人下线方式 移除这个登录客户端
		}
	});
	
	return SaResult.ok();
}
```





