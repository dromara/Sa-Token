# JSON 序列化扩展

--- 

## JSON 序列化插件大全

Sa-Token 在 Session 存储、Redis 缓存等场景下需要对对象进行 JSON 序列化与反序列化。框架将 JSON 转换逻辑抽象到 `SaJsonTemplate` 接口，
开发者只需引入对应的 JSON 插件依赖，框架会通过 SPI 机制自动完成注入，接口签名：[SaJsonTemplate.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-core/src/main/java/cn/dev33/satoken/json/SaJsonTemplate.java)
 
框架已提供的 JSON 序列化插件包括：

- **sa-token-jackson**：集成 Jackson（com.fasterxml.jackson），适用于 SpringBoot2/3 等环境。
- **sa-token-jackson3**：集成 Jackson 3（tools.jackson.core），适用于 SpringBoot4、Java 17+ 等环境。
- **sa-token-fastjson**：集成 Fastjson。
- **sa-token-fastjson2**：集成 Fastjson2。
- **sa-token-snack3**：集成 Snack3。
- **sa-token-snack4**：集成 Snack4。

> 默认 JSON 组件：
> 
> - `sa-token-spring-boot-starter` 会自动引入 `sa-token-jackson` 作为默认 JSON 方案。
> - `sa-token-spring-boot3-starter` 会自动引入 `sa-token-jackson`  作为默认 JSON 方案。
> - `sa-token-spring-boot4-starter` 会自动引入 `sa-token-jackson3`  作为默认 JSON 方案。
> 
> 如需更换为其它 JSON 框架，引入对应插件依赖即可。

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:Jackson ------------->
``` xml
<!-- Sa-Token 整合 Jackson -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-jackson</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
Gradle 参考：`implementation 'cn.dev33:sa-token-jackson:${sa.top.version}'`

<!------------- tab:Jackson3 ------------->
``` xml
<!-- Sa-Token 整合 Jackson3 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-jackson3</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
Gradle 参考：`implementation 'cn.dev33:sa-token-jackson3:${sa.top.version}'`

<!------------- tab:Fastjson ------------->
``` xml
<!-- Sa-Token 整合 Fastjson -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-fastjson</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
Gradle 参考：`implementation 'cn.dev33:sa-token-fastjson:${sa.top.version}'`

<!------------- tab:Fastjson2 ------------->
``` xml
<!-- Sa-Token 整合 Fastjson2 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-fastjson2</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
Gradle 参考：`implementation 'cn.dev33:sa-token-fastjson2:${sa.top.version}'`

<!------------- tab:Snack3 ------------->
``` xml
<!-- Sa-Token 整合 Snack3 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-snack3</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
Gradle 参考：`implementation 'cn.dev33:sa-token-snack3:${sa.top.version}'`

<!------------- tab:Snack4 ------------->
``` xml
<!-- Sa-Token 整合 Snack4 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-snack4</artifactId>
	<version>${sa.top.version}</version>
</dependency>
```
Gradle 参考：`implementation 'cn.dev33:sa-token-snack4:${sa.top.version}'`

<!---------------------------- tabs:end ------------------------------>


## JSON 全局类型白名单机制

### 报错示例

如果你在往 SaSession 上存储实体类字段，或从 Redis 反序列化 Session / 对象时报错：

```
无法反序列化的类型：com.pj.model.SysUser，请先将其注册到 JSON 全局类型白名单
```

### 这是因为

Sa-Token 在 **集成 Redis** 等持久化场景下，会把 `SaSession` 以及 `SaTokenDao.getObject()` / `setObject()` 中的对象先做 **JSON 序列化** 再写入 Redis。而后在使用数据时再进行反序列化。

为防止攻击者篡改 Redis 中的类型标记、实例化 classpath 上任意类（多态反序列化 RCE），或开发者使用 `sa-token-json` 组件将不安全的前端提交内容进行反序列化，框架只允许 **白名单内的类型** 参与这类多态反序列化。  

白名单内置常见 JDK 值类型，以及已实现 `SaJsonType` 的框架 Model 等；**你的业务实体类默认不在白名单中**，因此反序列化时会抛出上述异常。

> 说明：`sa-token-fastjson` / `sa-token-fastjson2` / `sa-token-snack3` 默认不在 JSON 中写入类型信息，一般不会出现此报错；业务对象请通过 `SaSession.getModel(key, Class)` 或 `jsonToObject(json, Class)` 指定类型。

白名单由 [SaJsonStrategy](https://gitee.com/dromara/sa-token/blob/master/sa-token-core/src/main/java/cn/dev33/satoken/strategy/SaJsonStrategy.java) 统一管理。**首次** 构建 JSON 插件（如 `SaJsonTemplateForJackson`）时会完成初始化；初始化之后不可再注册类型。

### 下面是注册 JSON 全局类型白名单的几种方式

#### 1、实体类实现 `SaJsonType`（推荐）

业务 Model 实现标记接口 `SaJsonType` 即可加入白名单，无需额外配置：

``` java
public class SysUser implements SaJsonType {
	// ...
}
```

#### 2、启动前调用 `registerAllowType`

在 **JSON 插件完成初始化之前** 注册（Spring Boot 请在 `main` 方法里、`SpringApplication.run` 之前；Solon 请在 `Solon.start` 之前）：

``` java
import cn.dev33.satoken.strategy.SaJsonStrategy;

public static void main(String[] args) {
	// 在项目启动前，将所有需要反序列化的 Bean Class 进行注册
	SaJsonStrategy.instance.registerAllowType(SysUser.class);

	SpringApplication.run(Application.class, args);
}
```

#### 3、通过 SPI 文件批量声明

在 `resources/META-INF/satoken/sa-json-type.list` 中按行写入完整类名（`#` 开头为注释）：

``` properties
# 允许参与多态 JSON 反序列化的业务类型
com.pj.model.SysUser
com.pj.model.SysRole
```

---

有关 Redis 集成与序列化配置，详细参考：[集成 Redis](/up/integ-redis)

