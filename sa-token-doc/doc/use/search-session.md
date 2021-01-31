# 会话治理

尽管框架将大部分操作提供了简易的封装，但在一些特殊场景下，我们仍需要绕过框架，直达数据底层进行一些操作 <br>
sa-token提供以下API助你直接操作会话列表


--- 

## 具体API

``` java
// 查询所有token
StpUtil.searchTokenValue(String keyword, int start, int size);

// 查询所有账号Session会话
StpUtil.searchSessionId(String keyword, int start, int size);

// 查询所有令牌Session会话
StpUtil.searchTokenSessionId(String keyword, int start, int size);
```


#### 参数详解：
- `keyword`: 查询关键字，只有包括这个字符串的token值才会被查询出来
- `start`: 数据开始处索引, 值为-1时代表一次性取出所有数据 
- `size`: 要获取的数据条数 

使用示例：
``` java
// 查询value包括1000的所有token，结果集从第0条开始，返回10条
List<String> tokenList = StpUtil.searchTokenValue("1000", 0, 10);	
for (String token : tokenList) {
	System.out.println(token);
}
```


<br/>

#### 注意事项：
由于会话查询底层采用了遍历方式获取数据，当数据量过大时此操作将会比较耗时，有多耗时呢？这里提供一份参考数据：
- 单机模式下：百万会话取出10条token平均耗时 `0.255s`
- Redis模式下：百万会话取出10条token平均耗时 `3.322s`

请根据业务实际水平合理调用API


