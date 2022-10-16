# 会话查询

尽管框架将大部分操作提供了简易的封装，但在一些特殊场景下，我们仍需要绕过框架，直达数据底层进行一些操作。

Sa-Token提供以下API助你直接操作会话列表：


--- 

## 具体API

``` java
// 查询所有已登录的 Token
StpUtil.searchTokenValue(String keyword, int start, int size, boolean sortType);

// 查询所有账号 Session 会话
StpUtil.searchSessionId(String keyword, int start, int size, boolean sortType);

// 查询所有令牌 Session 会话
StpUtil.searchTokenSessionId(String keyword, int start, int size, boolean sortType);
```


#### 参数详解：
- `keyword`: 查询关键字，只有包括这个字符串的 token 值才会被查询出来。
- `start`: 数据开始处索引, 值为-1时代表一次性取出所有数据。
- `size`: 要获取的数据条数。
- `sortType`: 排序方式（true=正序：先登录的在前，false=反序：后登录的在前）。

简单样例：
``` java
// 查询 value 包括 1000 的所有 token，结果集从第 0 条开始，返回 10 条
List<String> tokenList = StpUtil.searchTokenValue("1000", 0, 10, true);	
for (String token : tokenList) {
	System.out.println(token);
}
```

#### 深入：`StpUtil.searchTokenValue` 和 `StpUtil.searchSessionId` 有哪些区别？

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
List<String> sessionIdList = StpUtil.searchSessionId("", -1, -1, false);

for (String sessionId : sessionIdList) {
	
	// 根据会话id，查询对应的 SaSession 对象，此处一个 SaSession 对象即代表一个登录的账号 
	SaSession session = StpUtil.getSessionBySessionId(sessionId);
	
	// 查询这个账号都在哪些设备登录了，依据上面的示例，账号A 的 tokenSign 数量是 3，账号B 的 tokenSign 数量是 2 
	List<TokenSign> tokenSignList = session.getTokenSignList();
	System.out.println("会话id：" + sessionId + "，共在 " + tokenSignList.size() + " 设备登录");
}
```



<br/>

#### 注意事项：
由于会话查询底层采用了遍历方式获取数据，当数据量过大时此操作将会比较耗时，有多耗时呢？这里提供一份参考数据：
- 单机模式下：百万会话取出10条 Token 平均耗时 `0.255s`。
- Redis模式下：百万会话取出10条 Token 平均耗时 `3.322s`。

请根据业务实际水平合理调用API。


> 如果需要实时获取当前登录人数或者需要在用户退出后自动触发某事件等，建议采用 WebSocket。


--- 

<a class="case-btn" href="https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/cases/up/SearchSessionController.java"
	target="_blank">
	本章代码示例：Sa-Token 会话查询  —— [ com.pj.cases.up.SearchSessionController.java ]
</a>


