# 会话查询

--- 

### 1、单账号会话查询

使用 `StpUtil.getTerminalListByLoginId( loginId )` 可获取指定账号已登录终端列表信息，例如：

``` java
public static void main(String[] args) {
	System.out.println("账号 10001 登录设备信息：");
	List<SaTerminalInfo> terminalList = StpUtil.getTerminalListByLoginId(10001);
	for (SaTerminalInfo ter : terminalList) {
		System.out.println("登录index=" + ter.getIndex() + ", 设备type=" + ter.getDeviceType() + ", token=" + ter.getTokenValue() + ", 登录time=" + ter.getCreateTime());
	}
}
```

控制台打印结果：

``` txt
账号 10001 登录设备信息：
登录index=1, 设备type=PC, token=a8fbb46f-e043-459a-a875-0a2874911be8, 登录time=1742354951192
登录index=2, 设备type=APP, token=882b6c9c-bdf9-4e8f-a42b-6e17d2fe0e34, 登录time=1742354960950
登录index=3, 设备type=WEB, token=dacac78c-0983-4819-ab8b-07e7603597fc, 登录time=1742354962848
```

一个 `SaTerminalInfo` 对象代表一个终端信息，其有如下字段：

``` java
terminal.getIndex();   // 登录会话索引值 (该账号第几个登录的设备)
terminal.getDeviceType();   // 所属设备类型，例如：PC、WEB、HD、MOBILE、APP
terminal.getTokenValue();   // 此次登录的token值
terminal.getCreateTime();   // 登录时间, 13位时间戳
terminal.getDeviceId();   // 设备id, 设备唯一标识
terminal.getExtra("key");  // 此次登录的额外自定义参数 
```

`Extra` 自定义参数可以在登录时通过如下方式指定: 
``` java
StpUtil.login(10001, new SaLoginParameter().setTerminalExtra("key", "value"));
```



### 2、全部会话检索 

``` java
// 查询所有已登录的 Token
StpUtil.searchTokenValue(String keyword, int start, int size, boolean sortType);

// 查询所有 Account-Session 会话
StpUtil.searchSessionId(String keyword, int start, int size, boolean sortType);

// 查询所有 Token-Session 会话
StpUtil.searchTokenSessionId(String keyword, int start, int size, boolean sortType);
```


#### 参数详解：
- `keyword`: 查询关键字，只有包括这个字符串的 token 值才会被查询出来。
- `start`: 数据开始处索引。
- `size`: 要获取的数据条数 （值为-1代表一直获取到末尾）。 
- `sortType`: 排序方式（true=正序：先登录的在前，false=反序：后登录的在前）。

简单样例：
``` java
// 查询 value 包括 1000 的所有 token，结果集从第 0 条开始，返回 10 条
List<String> tokenList = StpUtil.searchTokenValue("1000", 0, 10, true);	
for (String token : tokenList) {
	System.out.println(token);
}
```

#### 深入：`StpUtil.searchTokenValue` 和 `StpUtil.searchSessionId` 的区别？

- StpUtil.searchTokenValue 查询的是登录产生的所有 Token。 
- StpUtil.searchSessionId 查询的是所有已登录账号会话id。 

举个例子，项目配置如下：
``` yml
sa-token: 
	# 允许同一账号在多个设备一起登录
	is-concurrent: true
	# 同一账号每次登录产生不同的token
	is-share: false
```

假设此时账号A在 电脑、手机、平板 依次登录（共3次登录），账号B在 电脑、手机 依次登录（共2次登录），那么：

- `StpUtil.searchTokenValue` 将返回一共 5 个Token。
- `StpUtil.searchSessionId` 将返回一共 2 个 SessionId。

综上，若要遍历系统所有已登录的会话，代码将大致如下：
``` java
// 获取所有已登录的会话id
List<String> sessionIdList = StpUtil.searchSessionId("", 0, -1, false);

for (String sessionId : sessionIdList) {
	
	// 根据会话id，查询对应的 SaSession 对象，此处一个 SaSession 对象即代表一个登录的账号 
	SaSession session = StpUtil.getSessionBySessionId(sessionId);
	
	// 查询这个账号都在哪些设备登录了，依据上面的示例，账号A 的 SaTerminalInfo 数量是 3，账号B 的 SaTerminalInfo 数量是 2 
	List<SaTerminalInfo> terminalList = session.terminalListCopy();
	System.out.println("会话id：" + sessionId + "，共在 " + terminalList.size() + " 设备登录");
}
```



<br/>

#### 注意事项：
由于会话查询底层采用了遍历方式获取数据，当数据量过大时此操作将会比较耗时，有多耗时呢？这里提供一份参考数据：
- 单机模式下：百万会话取出10条 Token 平均耗时 `0.255s`。
- Redis模式下：百万会话取出10条 Token 平均耗时 `3.322s`。

请根据业务实际水平合理调用API。


> [!WARNING| label:注意] 
> 基于活跃 Token 的统计方式会比实际情况略有延迟，如果需要精确统计实时在线用户信息需要采用 WebSocket。


--- 

<a class="case-btn" href="https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/cases/up/SearchSessionController.java"
	target="_blank">
	本章代码示例：Sa-Token 会话查询  —— [ SearchSessionController.java ]
</a>


