# OpenId 与 UnionId

<p><a class="case-btn case-btn-video" href="https://www.bilibili.com/video/BV1oz6AY5ERJ/" target="_blank">
	参考视频：OAuth2 授权流程中的 clientId、openId、unionId、userId 都是干嘛的？
</a></p>


### 1、OpenId 

openid 是用户在某一 client 下的唯一标识，其有如下特点：

- 一个用户在同一个 client 下，openid 是固定的，每次请求都会返回相同的值。
- 一个用户在不同的 client 下，openid 是不同的，会返回不同的值。

oauth2-client 在每次授权时可根据返回的 openid 值来确定用户身份。

框架默认的 openid 生成算法为：
``` java
md5(prefix + "_" + clientId + "_" + loginId);
```

其中的 prefix 前缀默认值为：`openid_default_digest_prefix`，你可以通过以下方式配置：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
# sa-token配置
sa-token:
	oauth2-server:
		# 默认 openid 生成算法中使用的摘要前缀
		openid-digest-prefix: xxxxxx
```
<!------------- tab:properties 风格  ------------->
``` properties
# 默认 openid 生成算法中使用的摘要前缀
sa-token.oauth2-server.openid-digest-prefix=xxxxxx
```
<!---------------------------- tabs:end ---------------------------->


你也可以通过实现 `SaOAuth2DataLoader` 接口完全自定义 OpenId 生成算法：

``` java
/**
 * Sa-Token OAuth2：自定义数据加载器
 */
@Component
public class SaOAuth2DataLoaderImpl implements SaOAuth2DataLoader {
	
	// 自定义 openid 生成算法 
	@Override
	public String getOpenid(String clientId, Object loginId) {
		// 此种写法代表使用框架默认算法生成 openid，真实环境建议改为从数据库查询
		return SaOAuth2DataLoader.super.getOpenid(clientId, loginId);
	}

}
``` 


#### openid 算法要求


正常来讲，openid 算法需要保证：

1. 单个 clientId 下同一 loginId 生成的 `openid` 一致。[必须]
2. 多个 clientId 下同一 loginId 生成的 `openid` 不一致。[非常建议]
3. 客户端无法通过 clientId + loginId 推测 `openid` 值。[建议]
4. 客户端无法通过 clientId + loginId + openid 推测该 loginId 在其它 clientId 下的 `openid` 值。[建议]
5. oauth2-server 自身由 `openid` 可以反查出对应的 clientId 和 loginId。[根据业务需求而定是否满足]

框架内置的算法，可以满足 1和2，如果自定义了 `sa-token.oauth2-server.openid-digest-prefix` 配置，可以满足3。

如果自定义配置的 prefix 长度较短，或比较简单呈现规律性，则有客户端根据 clientId + loginId + openid 穷举爆破出 `prefix` 的风险，
从而获得提前计算彩虹表来推测出其它 clientId、loginId 对应 openid 值的能力。

如果自定义的 prefix 前缀比较复杂，让客户端无法爆破，则可以满足4。但依然无法满足5。

所以 openid 算法的最优解，应该是 oauth2-server 采用随机字符串作为 openid，然后自建数据库表来维护其映射关系，这样可以同时满足12345。

表结构参考如下：

- id：数据id，主键。
- client_id：应用id。
- user_id：用户账号id。
- openid：对应的 openid 值，随机字符串。
- create_time：数据创建时间。
- xxx：其它需要扩展的字段。



### 2、UnionId 

`UnionId` 的特点与 `OpenId` 几乎一致：同一用户在不同 client 里的 UnionId 值是不同的，除非这些应用属于同一主体。

例如：甲公司申请了`应用A`、`应用B`、`应用C`，乙公司申请了`应用D`、`应用F`，那么用户张三：
- 在应用 A、B、C 里的 UnionId 值一致。
- 在应用 D、F 里的 UnionId 值一致。
- 在应用 A 和 应用 D 之间，UnionId 值不一致。

那么 Sa-Token 框架是如何识别到某两个应用是否为同一主体的呢？这就需要你在注册应用时指定 `subjectId` 属性了：

``` java
/**
 * Sa-Token OAuth2：自定义数据加载器
 */
@Component
public class SaOAuth2DataLoaderImpl implements SaOAuth2DataLoader {
    
    // 根据 clientId 获取 Client 信息
    @Override
    public SaClientModel getClientModel(String clientId) {
        // 此为模拟数据，真实环境需要从数据库查询 
        if("1001".equals(clientId)) {
            return new SaClientModel()
					.setClientId("xxxx")  
					.setClientSecret("xxxx")   
					.setSubjectId("1000001")   // 关键代码：主体 id (可选)
					// ....
            ;
        }
        return null;
    }
    
}
```

`subjectId` 代表此应用的拥有者，相同 `subjectId` 值的应用将被识别为同一主体，在授权中返回的 `unionid` 值也将一致。

框架默认的 `unionid` 生成算法为：

``` java
md5(prefix + "_" + subjectId + "_" + loginId);
```

其中的 prefix 前缀默认值为：`unionid_default_digest_prefix`，你可以通过以下方式配置：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
# sa-token配置
sa-token:
	oauth2-server:
		# 默认 unionid 生成算法中使用的摘要前缀
		unionid-digest-prefix: xxxxxx
```
<!------------- tab:properties 风格  ------------->
``` properties
# 默认 unionid 生成算法中使用的摘要前缀
sa-token.oauth2-server.unionid-digest-prefix=xxxxxx
```
<!---------------------------- tabs:end ---------------------------->


你也可以通过实现 `SaOAuth2DataLoader` 接口完全自定义 UnionId 生成算法：

``` java
/**
 * Sa-Token OAuth2：自定义数据加载器
 */
@Component
public class SaOAuth2DataLoaderImpl implements SaOAuth2DataLoader {
	
	// 自定义 unionid 生成算法 
	@Override
	public String getUnionid(String subjectId, Object loginId) {
		// 此种写法代表使用框架默认算法生成 unionid，真实环境建议改为从数据库查询
		return SaOAuth2DataLoader.super.getUnionid(subjectId, loginId);
	}

}
``` 

unionid 算法要求与 openid 基本一致，可参考上述 openid 算法要求介绍，此处暂不赘述。




### 3、总结

| 类型			| 概念															|
| :--------		| :--------														|
| userid       	| 在 oauth2-server 端的用户，其唯一标识							|
| clientid     	| 第三方公司在 oauth2-server 开放平台申请的应用，其唯一标识			|
| openid       	| 用户在某个应用下的唯一标识										|
| unionid      	| 用户在某一组应用下的唯一标识	（按照主体id分组）					|





