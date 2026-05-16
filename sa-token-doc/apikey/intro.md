# ApiKey 介绍

ApiKey 是一种常见的开放接口鉴权方案，调用方在请求时携带一个由服务端预先签发的密钥，服务端据此识别调用方身份与权限范围。

Sa-Token 内置了一套完整的 ApiKey 模型，开箱即用，并支持自定义存储与加载逻辑。


### 一、基本使用

Sa-Token 提供了 `SaApiKeyUtil` 工具类用于创建、查询、删除 ApiKey。

``` java
// 创建一个 ApiKey 模型
ApiKeyModel ak = SaApiKeyUtil.createApiKeyModel();

// 查询指定 ApiKey 的信息
ApiKeyModel info = SaApiKeyUtil.getApiKey(apiKey);

// 删除指定 ApiKey
SaApiKeyUtil.deleteApiKey(apiKey);
```


### 二、缓存机制说明

为了减少对底层存储的访问压力，Sa-Token 默认会对 ApiKey 数据进行缓存。理解缓存的生命周期有助于在生产环境中正确使用：

1. `SaApiKeyUtil.getApiKey(apiKey)` 默认会**优先从缓存读取**，命中缓存时不再访问数据源（`SaApiKeyDataLoader`）。
2. 如果你在框架之外直接修改了底层 ApiKey 存储中的数据（例如：直接修改数据库行记录），在缓存条目过期或被清除之前，这些变更**不会被框架感知**。
3. 如果希望每次读取都直接走数据源以保证强一致性，可以在自定义的 `SaApiKeyDataLoader` 实现中重写 `getIsAlwaysFromDataLoader()` 方法返回 `true`，此后所有 `getApiKey()` 调用都将绕过缓存。
4. 如果只是偶尔需要进行一次「新鲜读」，可以使用以下重载方法：
	- `SaApiKeyUtil.getApiKey(apiKey, false)`：单次查询时绕过缓存。
	- `SaApiKeyUtil.getApiKeyFromDataLoader(apiKey)`：直接从数据源加载，不读取也不写入缓存。


### 三、注意事项

如果你保持默认的「优先读缓存」模式，并在 Sa-Token 框架之外直接修改了 ApiKey 的字段（例如：scope、有效期、关联用户等），请务必在修改完成后手动清除对应的缓存条目：

``` java
SaApiKeyUtil.deleteApiKey(apiKey);
```

否则在缓存过期之前，框架仍会使用旧的数据进行鉴权，可能导致权限变更不及时生效。

如果你的业务对一致性要求较高，且无法保证所有变更都通过 `SaApiKeyUtil` 进行，建议直接将 `SaApiKeyDataLoader#getIsAlwaysFromDataLoader()` 重写为返回 `true`，让框架始终从数据源读取。
