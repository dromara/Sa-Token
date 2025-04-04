# API Key 接口调用秘钥

API Key（应用程序编程接口密钥） 是一种用于身份验证和授权的字符串代码，通常由服务提供商生成并分配给开发者或用户。它的主要作用是标识调用 API（应用程序编程接口）的请求来源，确保请求的合法性，并控制访问权限。

以上是官话，简单理解：API Key 是一种接口调用密钥，类似于会话 token ，但比会话 token 具有更灵活的权限控制。


### 1、需求场景

为了帮助大家更好的理解 API Key 的应用场景，我们假设具有以下业务场景：

> [!NOTE| label:业务场景] 
> 你们公司开发了一款论坛网站，非常火爆。
> 
> 某日，你发现一位用户的头像可以随着日期而变化，Ta 的头像总是显示当前最新日期。
> 
> 这并未引起你的警觉，因为你是一个程序员，在你看来，写一个任务脚本，每天定时调用 API 更新自己的头像是一件非常简单的事情。
> 
> 一个月后，越来越多的账号“具有了此功能”，仿佛发生了人传人，Ta 们的头像都可以随着日期而变化，而且颜色各不相同，DIY 的不亦乐乎。
> 
> 这引起了你的怀疑，如此大批账号的自动化更新行为，显然不是 “某个程序员利用定时脚本更新账号信息” 可以解释的。
> 
> 一番调查之后，你发现了事情的真相，没有灰产公司捣乱，这批账号也不是机器账号，只是有一个公司为你们的网站开发了一款插件。
> 
> 这款插件的作用是：用户把自己的 账号+密码 保存在插件中，插件便可以定时更新该账号的头像、昵称、资料等信息。
> 
> 你觉得插件很有意思，但是插件“要求用户提交账号密码”的行为，让你感到很不爽。
> 
> 总有一些用户为了得到“些许便利”，而出卖自己的账号密码给插件。
> 
> 随着时间推移，越来越多的第三方公司或个人为你的网站开发插件：有的可以自动更新账号资料、有的可以自动发帖，有的检测到新粉丝就发送消息通知...
> 
> 最终，不守规矩的插件出现了：一款插件在提供功能的同时，大量收集用户密码等隐私信息，作为不法用途。
> 
> 为了遏制这种现象，你们公司升级了系统，增加了 IP 校验等风控判断，阻断了这些插件的 API 调用。
> 
> 似乎……解决了问题？用户再也不会把账号密码交给第三方插件了。
> 
> 但是插件的需求总是存在的呀，有些用户确实很需要这些插件的能力来提高网站使用体验。
> 
> 俗话说的好，堵不如疏，既然用户有需求，第三方公司愿意免费打工开发插件，我们何不设计一套授权架构，
> 既不需要让用户把账号密码交给第三方插件，又能让插件得到一些权限来调用特定 API 为用户服务。
> 
> API Key 就是为了完成这种“可控式部分授权” 而设计的一种身份凭证。


为了让第三方插件为用户工作，用户必定是要为插件提供一个“凭证”信息的，然后插件利用“凭证”信息，代替用户调用特定 API 完成一些功能。

不同的凭证信息将会带来不同的后果：


| 提供的凭证					| 后果										|
| :--------					| :--------									|
| 账号密码					| 插件可以得到账号所有权限，安全风险极高		|
| 会话 token					| 插件可以调用几乎所有 API，安全风险极高，且容易受到用户退出登录导致 token 失效的影响		|
| API Key					| 在可控的范围内进行部分授权，且可以方便的随时取消授权，只要设计得当，不会造成安全问题		|

API Key 具有以下特点：
- 1、格式类似于会话 token，是一个随机字符串。
- 2、每个 API Key 都会和具体的用户 id 发生绑定，后端可以查询到此 API Key 的授权人是谁。
- 3、一个用户可以创建多个 API Key，用作不同的插件中。
- 4、每个 API Key 都可以赋予不同的 scope 权限，以做到最小化授权。
- 5、API Key 可以设置有效期，并且随时删除回收，做到灵活控制。





### 2、创建 API Key

理解了应用场景后，让我们看看 Sa-Token 为 API Key 提供了哪些方法
*（此插件是内嵌到 sa-token-core 核心包中的模块，大家无需再次引入其它依赖，插件直接可用）*：


``` java
// 为指定用户创建一个新的 API Key 
ApiKeyModel akModel = SaApiKeyUtil.createApiKeyModel(10001).setTitle("test");
System.out.println("API Key 值：" + akModel.getApiKey());

// 保存 API Key 
SaApiKeyUtil.saveApiKey(akModel);

// 删除 API Key 
SaApiKeyUtil.deleteApiKey(apiKey);
```

一个 ApiKeyModel 可设置以下属性：
``` java
ApiKeyModel akModel = new ApiKeyModel();
akModel.setLoginId(10001);  // 设置绑定的用户 id
akModel.setApiKey("AK-NAO6u57zbOWCmLaiVQuVW2tyt3rHpZrXkaQp");  // 设置 API Key 值
akModel.setTitle("commit");	  // 设置名称
akModel.setIntro("提交代码专用");   // 设置描述
akModel.addScope("commit", "pull");  // 设置权限范围
akModel.setExpiresTime(System.currentTimeMillis() + 2592000);  // 设置失效时间，13位时间戳，-1=永不失效
akModel.setIsValid(true);   // 设置是否有效
akModel.addExtra("name", "张三");   // 设置扩展信息
// 保存 
SaApiKeyUtil.saveApiKey(akModel);  
```

查询：

``` java
// 获取 API Key 详细信息 
ApiKeyModel akModel = SaApiKeyUtil.getApiKey("AK-NAO6u57zbOWCmLaiVQuVW2tyt3rHpZrXkaQp");

// 直接获取 ApiKey 所代表的 loginId
Object loginId = SaApiKeyUtil.getLoginIdByApiKey("AK-NAO6u57zbOWCmLaiVQuVW2tyt3rHpZrXkaQp");

// 获取指定 loginId 的 ApiKey 列表记录
List<ApiKeyModel> apiKeyList = SaApiKeyUtil.getApiKeyList(10001);
```


### 3、校验 API Key

``` java
// 校验指定 API Key 是否有效，无效会抛出异常 ApiKeyException
SaApiKeyUtil.checkApiKey("AK-XxxXxxXxx");

// 校验指定 API Key 是否具有指定 Scope 权限，不具有会抛出异常 ApiKeyScopeException
SaApiKeyUtil.checkApiKeyScope("AK-XxxXxxXxx", "userinfo");

// 校验指定 API Key 是否具有指定 Scope 权限，返回 true 或 false
SaApiKeyUtil.hasApiKeyScope("AK-XxxXxxXxx", "userinfo");

// 校验指定 API Key 是否属于指定账号 id 
SaApiKeyUtil.checkApiKeyLoginId("AK-XxxXxxXxx", 10001);
```

注解鉴权示例：
``` java
/**
 * API Key 资源 相关接口
 */
@RestController
public class ApiKeyResourcesController {

	// 必须携带有效的 ApiKey 才能访问
	@SaCheckApiKey
	@RequestMapping("/akRes1")
	public SaResult akRes1() {
		ApiKeyModel akModel = SaApiKeyUtil.currentApiKey();
		System.out.println("当前 ApiKey: " + akModel);
		return SaResult.ok("调用成功");
	}

	// 必须携带有效的 ApiKey ，且具有 userinfo 权限
	@SaCheckApiKey(scope = "userinfo")
	@RequestMapping("/akRes2")
	public SaResult akRes2() {
		ApiKeyModel akModel = SaApiKeyUtil.currentApiKey();
		System.out.println("当前 ApiKey: " + akModel);
		return SaResult.ok("调用成功");
	}

	// 必须携带有效的 ApiKey ，且同时具有 userinfo、chat 权限
	@SaCheckApiKey(scope = {"userinfo", "chat"})
	@RequestMapping("/akRes3")
	public SaResult akRes3() {
		ApiKeyModel akModel = SaApiKeyUtil.currentApiKey();
		System.out.println("当前 ApiKey: " + akModel);
		return SaResult.ok("调用成功");
	}

	// 必须携带有效的 ApiKey ，且具有 userinfo、chat 其中之一权限
	@SaCheckApiKey(scope = {"userinfo", "chat"}, mode = SaMode.OR)
	@RequestMapping("/akRes4")
	public SaResult akRes4() {
		ApiKeyModel akModel = SaApiKeyUtil.currentApiKey();
		System.out.println("当前 ApiKey: " + akModel);
		return SaResult.ok("调用成功");
	}

}
```


### 4、打开数据库模式

框架默认将所有 API Key 信息保存在缓存中，这可以称之为“缓存模式”，这种模式下，重启缓存库后，数据将丢失。

如果你想改为“数据库模式”，可以通过 `implements SaApiKeyDataLoader` 实现从数据库加载的逻辑。

``` java
/**
 * API Key 数据加载器实现类 （从数据库查询）
 */
@Component 
public class SaApiKeyDataLoaderImpl implements SaApiKeyDataLoader {

    @Autowired
    SaApiKeyMapper apiKeyMapper;

    // 指定框架不再维护 API Key 索引信息，而是由我们手动从数据库维护
    @Override
    public Boolean getIsRecordIndex() {
        return false;
    }

    // 根据 apiKey 从数据库获取 ApiKeyModel 信息 （实现此方法无需为数据做缓存处理，框架内部已包含缓存逻辑）
    @Override
    public ApiKeyModel getApiKeyModelFromDatabase(String apiKey) {
        return apiKeyMapper.getApiKeyModel(apiKey);
    }

}
```

参考上述代码实现后，框架内部逻辑将会做出一些改变，请注意以下事项：

- 1、调用 `SaApiKeyUtil.getApiKey("ApiKey")` 时，会先从缓存中查询，查询不到时调用 `getApiKeyModelFromDatabase` 从数据库加载。
- 2、框架不再维护 API Key 索引数据，这意味着无法再调用 `SaApiKeyUtil.getApiKeyList(10001)` 来获取一个用户的所有的 API Key 数据，请自行从数据库查询。
- 3、调用 `SaApiKeyUtil.saveApiKey(akModel)` 保存时，只会把 API Key 数据保存到缓存中，请自行补充额外代码向数据库保存数据。
- 4、调用 `SaApiKeyUtil.deleteApiKey("ApiKey")` 时，只会删除这个 API Key 在缓存中的数据，不会删除数据库的数据，请自行补充相关代码保证数据双删。
- 5、其它诸如查询 `SaApiKeyUtil.getApiKey("ApiKey")` 或校验 `SaApiKeyUtil.checkApiKeyScope("ApiKey", "userinfo")` 等方法，依旧可以正常调用。










