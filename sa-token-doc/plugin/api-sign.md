# API 接口参数签名

在涉及跨系统接口调用时，我们容易碰到以下安全问题：

- 请求身份被伪造。
- 请求参数被篡改。
- 请求被抓包，然后重放攻击。

sa-token api-sign 模块将帮你轻松解决以上难题。*（此插件是内嵌到 sa-token-core 核心包中的模块，开发者无需再次引入其它依赖，插件直接可用）*

本篇将根据假设的需求场景，循序渐进讲明白跨系统接口调用时必做的几个步骤，以及为什么要有这些步骤的原因。


### 1、需求场景

假设我们有如下业务需求：

> 用户在 A 系统参与活动成功后，活动奖励以余额的形式下发到 B 系统。


### 2、初始方案：直接裸奔

在不考虑安全问题的情况下，我们很容易完成这个需求：

1、在 B 系统开放一个接口。

``` java
/**
 * 为指定用户添加指定余额
 * 
 * @param userId 用户 id
 * @param money 要添加的余额，单位：分
 * @return / 
 */
@RequestMapping("addMoney")
public SaResult addMoney(long userId, long money) {
	// 处理业务 
	// ...
	
	// 返回 
	return SaResult.ok();
}
```

2、在 A 系统使用 http 工具类调用这个接口。

``` java
long userId = 10001;
long money = 1000;
String res = HttpUtil.request("http://a.com/api/addMoney?userId=" + userId + "&money=" + money);
```

上述代码简单的完成了需求，但是很明显它有一个安全问题：

B 系统开放的接口不仅可以被 A 系统调用，还可以被其它任何人调用，甚至别人可以本地跑一个 for 循环调用这个接口，为自己无限充值金额。


### 3、方案升级：增加 secretKey 校验

为防止 B 系统开放的接口被陌生人任意调用，我们增加一个 secretKey 参数

``` java
// 为指定用户添加指定余额
@RequestMapping("addMoney")
public SaResult addMoney(long userId, long money, String secretKey) {
	// 1、先校验 secretKey 参数是否正确，如果不正确直接拒绝响应请求
	if( ! check(secretKey) ) {
		return SaResult.error("无效 secretKey，无法响应请求");
	}
	
	// 2、业务代码 
	// ...
	
	// 3、返回
	return SaResult.ok();
}
```

由于 A 系统是我们 “自己人”，所以它可以拿着 `secretKey` 进行合法请求：

``` java
long userId = 10001;
long money = 1000;
String secretKey = "xxxxxxxxxxxxxxxxxxxx";
String res = HttpUtil.request("http://a.com/api/addMoney?userId=" + userId + "&money=" + money + "&secretKey=" + secretKey);
```

现在，即使 B 系统的接口被暴露了，也不会被陌生人任意调用了，安全性得到了一定的保证，但是仍然存在一些问题：

- 如果请求被抓包，secretKey 就会泄露，因为每次请求都在 url 中明文传输了 secretKey 参数。
- 如果请求被抓包，请求的其它参数就可以被任意修改，例如可以将 money 参数修改为 9999999，B系统无法确定参数是否被修改过。



### 4、方案再升级：使用摘要算法生成参数签名

首先，在 A 系统不要直接发起请求，而是先计算一个 sign 参数：

``` java
// 声明变量
long userId = 10001;
long money = 1000;
String secretKey = "xxxxxxxxxxxxxxxxxxxx";

// 计算 sign 参数
String sign = md5("userId=" + userId + "&money=" + money + "&key=" + secretKey);

// 将 sign 拼接在请求地址后面
String res = HttpUtil.request("http://a.com/api/addMoney?userId=" + userId + "&money=" + money + "&sign=" + sign);
```

**注意此处计算签名时，需要将所有参数按照字典顺序依次排列（key除外，挂在最后面）。**以下所有计算签名时同理，不再赘述。

然后在 B 系统接收请求时，使用同样的算法、同样的秘钥，生成 sign 字符串，与参数中 sign 值进行比较：


``` java
// 为指定用户添加指定余额
@RequestMapping("addMoney")
public SaResult addMoney(long userId, long money, String sign) {

	// 在 B 系统，使用同样的算法、同样的密钥，计算出 sign2，与传入的 sign 进行比对
	String sign2 = md5("userId=" + userId + "&money=" + money + "&key=" + secretKey);
	if( ! sign2.equals(sign)) {
		return SaResult.error("无效 sign，无法响应请求");
	}

	// 2、业务代码 
	// ...
	
	// 3、返回
	return SaResult.ok();
}
```

因为 sign 的值是由 userId、money、secretKey 三个参数共同决定的，所以只要有一个参数不一致，就会造成最终生成 sign 也是不一致的，所以，根据比对结果：

- 如果 sign 一致，说明这是个合法请求。
- 如果 sign 不一致，说明发起请求的客户端秘钥不正确，或者请求参数被篡改过，是个不合法请求。

此方案优点：
- 不在 url 中直接传递 secretKey 参数了，避免了泄露风险。
- 由于 sign 参数的限制，请求中的参数也不可被篡改，B 系统可放心的使用这些参数。

此方案仍然存在以下缺陷：
- 被抓包后，请求可以被无限重放，B 系统无法判断请求是真正来自于 A 系统发出的，还是被抓包后重放的。



### 5、方案再再升级：追加 nonce 随机字符串

首先，在 A 系统发起调用前，追加一个 nonce 参数，一起参与到签名中：

``` java
// 声明变量
long userId = 10001;
long money = 1000;
String nonce = SaFoxUtil.getRandomString(32); // 随机32位字符串
String secretKey = "xxxxxxxxxxxxxxxxxxxx";

// 计算 sign 参数
String sign = md5("userId=" + userId + "&money=" + money + "&nonce=" + nonce + "&key=" + secretKey);

// 将 sign 拼接在请求地址后面
String res = HttpUtil.request("http://a.com/api/addMoney?userId=" + userId + "&money=" + money + "nonce=" + nonce + "&sign=" + sign);
```

然后在 B 系统接收请求时，也把 nonce 参数加进去生成 sign 字符串，进行比较：

``` java
// 为指定用户添加指定余额
@RequestMapping("addMoney")
public SaResult addMoney(long userId, long money, String nonce, String sign) {

	// 1、检查此 nonce 是否已被使用过了
	if(CacheUtil.get("nonce_" + nonce) != null) {
		return SaResult.error("此 nonce 已被使用过了，请求无效");
	}

	// 2、验证签名
	String sign2 = md5("userId=" + userId + "&money=" + money + "&nonce=" + nonce + "&key=" + secretKey);
	if( ! sign2.equals(sign)) {
		return SaResult.error("无效 sign，无法响应请求");
	}

	// 3、将 nonce 记入缓存，防止重复使用
	CacheUtil.set("nonce_" + nonce, "1");

	// 4、业务代码 
	// ...

	// 5、返回
	return SaResult.ok();
}
```

代码分析：
 
- 为方便理解，我们先看第 3 步：此处在校验签名成功后，将 nonce 随机字符串记入缓存中。
- 再看第 1 步：每次请求进来，先查看一下缓存中是否已经记录了这个随机字符串，如果是，则立即返回：无效请求。

这两步的组合，保证了一个 nonce 随机字符串只能被使用一次，如果请求被抓包后重放，是无法通过 nonce 校验的。

至此，问题似乎已被解决了 …… 吗？

别急，我们还有一个问题没有考虑：这个 nonce 在字符串在缓存应该被保存多久呢？

- 保存 15 分钟？那抓包的人只需要等待 15 分钟，你的 nonce 记录在缓存中消失，请求就可以被重放了。
- 那保存 24 小时？保存一周？保存半个月？好像无论保存多久，都无法从根本上解决这个问题。

你可能会想到，那我永久保存吧。这样确实能解决问题，但显然服务器承载不了这么做，即使再微小的数据量，在时间的累加下，也总一天会超出服务器能够承载的上限。


### 6、方案再再再升级：追加 timestamp 时间戳

我们可以再追加一个 timestamp 时间戳参数，将请求的有效性限定在一个有限时间范围内，例如 15分钟。

首先，在 A 系统追加 timestamp 参数：

``` java
// 声明变量
long userId = 10001;
long money = 1000;
String nonce = SaFoxUtil.getRandomString(32); // 随机32位字符串
long timestamp = System.currentTimeMillis(); // 随机32位字符串
String secretKey = "xxxxxxxxxxxxxxxxxxxx";

// 计算 sign 参数
String sign = md5("userId=" + userId + "&money=" + money + "&nonce=" + nonce + "&timestamp=" + timestamp + "&key=" + secretKey);

// 将 sign 拼接在请求地址后面
String res = HttpUtil.request("http://a.com/api/addMoney" +
		"?userId=" + userId + "&money=" + money + "&nonce=" + nonce + "&timestamp=" + timestamp + "&sign=" + sign);
```

在 B 系统检测这个 timestamp 是否超出了允许的范围 

``` java
// 为指定用户添加指定余额
@RequestMapping("addMoney")
public SaResult addMoney(long userId, long money, long timestamp, String nonce, String sign) {

	// 1、检查 timestamp 是否超出允许的范围（此处假定最大允许15分钟差距）
	long timestampDisparity = System.currentTimeMillis() - timestamp; // 实际的时间差
	if(timestampDisparity > 1000 * 60 * 15) {
		return SaResult.error("timestamp 时间差超出允许的范围，请求无效");
	}

	// 2、检查此 nonce 是否已被使用过了
	// 代码同上，不再赘述

	// 3、验证签名
	// 代码同上，不再赘述

	// 4、将 nonce 记入缓存，ttl 有效期和 allowDisparity 允许时间差一致 
	CacheUtil.set("nonce_" + nonce, "1", 1000 * 60 * 15);

	// 5、业务代码 ...

	// 6、返回
	return SaResult.ok();
}
```

至此，抓包者：

- 如果在 15 分钟内重放攻击，nonce 参数不答应：缓存中可以查出 nonce 值，直接拒绝响应请求。
- 如果在 15 分钟后重放攻击，timestamp 参数不答应：超出了允许的 timestamp 时间差，直接拒绝响应请求。


### 7、服务器的时钟差异造成安全问题

以上的代码，均假设 A 系统服务器与 B 系统服务器的时钟一致，才可以正常完成安全校验，但在实际的开发场景中，有些服务器会存在时钟不准确的问题。

假设 A 服务器与 B 服务器的时钟差异为 10 分钟，即：在 A 服务器为 8:00 的时候，B 服务器为 7:50。

1. A 系统发起请求，其生成的时间戳也是代表 8:00。
2. B 系统接受到请求后，完成业务处理，此时 nonce 的 ttl 为 15分钟，到期时间为 7:50 + 15分 = 8:05。
3. 8.05 后，nonce 缓存消失，抓包者重放请求攻击：
	- timestamp 校验通过：因为时间戳差距仅有 8.05 - 8.00 = 5分钟，小于 15 分钟，校验通过。
	- nonce 校验通过：因为此时 nonce 缓存已经消失，可以通过校验。
	- sign 校验通过：因为这本来就是由 A 系统构建的一个合法签名。
	- 攻击完成。

要解决上述问题，有两种方案：
- 方案一：修改服务器时钟，使两个服务器时钟保持一致。
- 方案二：在代码层面兼容时钟不一致的场景。

要采用方案一的同学可自行搜索一下同步时钟的方法，在此暂不赘述，此处详细阐述一下方案二。

我们只需简单修改一下，B 系统校验参数的代码即可：

``` java
// 为指定用户添加指定余额
@RequestMapping("addMoney")
public SaResult addMoney(long userId, long money, long timestamp, String nonce, String sign) {

	// 1、检查 timestamp 是否超出允许的范围 （重点一：此处需要取绝对值）
	long timestampDisparity = Math.abs(System.currentTimeMillis() - timestamp);
	if(timestampDisparity > 1000 * 60 * 15) {
		return SaResult.error("timestamp 时间差超出允许的范围，请求无效");
	}

	// 2、检查此 nonce 是否已被使用过了
	// 代码同上，不再赘述 

	// 3、验证签名
	// 代码同上，不再赘述 

	// 4、将 nonce 记入缓存，防止重复使用（重点二：此处需要将 ttl 设定为允许 timestamp 时间差的值 x 2 ）
	CacheUtil.set("nonce_" + nonce, "1", (1000 * 60 * 15) * 2);

	// 5、业务代码 ...

	// 6、返回
	return SaResult.ok();
}
```


### 8、最终版方案

此处再贴一下完整的代码。

A 系统（发起请求端）：

``` java
// 声明变量
long userId = 10001;
long money = 1000;
String nonce = SaFoxUtil.getRandomString(32); // 随机32位字符串
long timestamp = System.currentTimeMillis(); // 当前时间戳
String secretKey = "xxxxxxxxxxxxxxxxxxxx";

// 计算 sign 参数
String sign = md5("userId=" + userId + "&money=" + money + "&nonce=" + nonce + "&timestamp=" + timestamp + "&key=" + secretKey);

// 将 sign 拼接在请求地址后面
String res = HttpUtil.request("http://a.com/api/addMoney" +
		"?userId=" + userId + "&money=" + money + "&nonce=" + nonce + "&timestamp=" + timestamp + "&sign=" + sign);
```

B 系统（接收请求端）：

``` java
// 为指定用户添加指定余额
@RequestMapping("addMoney")
public SaResult addMoney(long userId, long money, long timestamp, String nonce, String sign) {

	// 1、检查 timestamp 是否超出允许的范围
	long allowDisparity = 1000 * 60 * 15;	// 允许的时间差：15分钟
	long timestampDisparity = Math.abs(System.currentTimeMillis() - timestamp); // 实际的时间差
	if(timestampDisparity > allowDisparity) {
		return SaResult.error("timestamp 时间差超出允许的范围，请求无效");
	}

	// 2、检查此 nonce 是否已被使用过了
	if(CacheUtil.get("nonce_" + nonce) != null) {
		return SaResult.error("此 nonce 已被使用过了，请求无效");
	}

	// 3、验证签名
	String sign2 = md5("userId=" + userId + "&money=" + money + "&nonce=" + nonce + "&key=" + secretKey);
	if( ! sign2.equals(sign)) {
		return SaResult.error("无效 sign，无法响应请求");
	}

	// 4、将 nonce 记入缓存，防止重复使用，注意此处需要将 ttl 设定为允许 timestamp 时间差的值 x 2  
	CacheUtil.set("nonce_" + nonce, "1", allowDisparity * 2);

	// 5、业务代码 ...

	// 6、返回
	return SaResult.ok();
}
```


### 9、使用 Sa-Token 框架完成 API 参数签名

接下来步入正题，使用 Sa-Token 内置的 sign 模块，方便的完成 API 签名创建、校验等步骤：
- 不限制请求的参数数量，方便组织业务需求代码。
- 自动补全 nonce、timestamp 参数，省时省力。
- 自动构建签名，并序列化参数为字符串。
- 一句代码完成 nonce、timestamp、sign 的校验，防伪造请求调用、防参数篡改、防重放攻击。


#### 9.1、引入依赖
api-sign 模块已内嵌到核心包，只需要引入 sa-token 本身依赖即可：（请求发起端和接收端都需要引入）

<!---------------------------- tabs:start ---------------------------->
<!-------- tab:Maven 方式 -------->

``` xml 
<!-- Sa-Token 权限认证，在线文档：https://sa-token.cc -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```

<!-------- tab:Gradle 方式 -------->

``` gradle
// Sa-Token 权限认证，在线文档：https://sa-token.cc
implementation 'cn.dev33:sa-token-spring-boot-starter:${sa.top.version}'
```
<!---------------------------- tabs:end ---------------------------->


#### 9.2、配置秘钥
请求发起端和接收端需要配置一个相同的秘钥，在 `application.yml` 中配置：

``` yml
sa-token: 
    sign:
        # API 接口签名秘钥 （随便乱摁几个字母即可）
        secret-key: kQwIOrYvnXmSDkwEiFngrKidMcdrgKor
```

#### 9.3、请求发起端构建签名

``` java
// 请求地址
String url = "http://a.com/api/addMoney";

// 请求参数
Map<String, Object> paramMap = new LinkedHashMap<>();
paramMap.put("userId", 10001);
paramMap.put("money", 1000);
// 更多参数，不限制数量...

// 补全 timestamp、nonce、sign 参数，并序列化为 kv 字符串
String paramStr = SaSignUtil.addSignParamsAndJoin(paramMap);

// 将参数字符串拼接在请求地址后面
url += "?" + paramStr;

// 发送请求
String res = HttpUtil.request(url);

// 根据返回值做后续处理
System.out.println("server 端返回信息：" + res);
```


#### 9.4、请求接受端校验签名
``` java
// 为指定用户添加指定余额
@RequestMapping("addMoney")
public SaResult addMoney(long userId, long money) {

	// 1、校验请求中的签名
	SaSignUtil.checkRequest(SaHolder.getRequest());
	
	// 2、校验通过，处理业务
	System.out.println("userId=" + userId);
	System.out.println("money=" + money);
	
	// 3、返回
	return SaResult.ok();
}
```

如上代码便可简单方便的完成 API 接口参数签名校验，当请求端的秘钥不对，或者请求参数被篡改、请求被重放时，均无法通过 `SaSignUtil.checkRequest` 校验。

``` js
{
  "code": 500,
  "msg": "无效签名：9c3e3e98c7d543fb599766c9d3f3b5ff",
  "data": null
}
```

完。











