# JSON 序列化扩展

--- 

Sa-Token 在 Session 存储、Redis 缓存等场景下需要对对象进行 JSON 序列化与反序列化。框架将 JSON 转换逻辑抽象到 `SaJsonTemplate` 接口，
开发者只需引入对应的 JSON 插件依赖，框架会通过 SPI 机制自动完成注入，接口签名：[SaJsonTemplate.java](https://gitee.com/dromara/sa-token/blob/master/sa-token-core/src/main/java/cn/dev33/satoken/json/SaJsonTemplate.java)
 
框架已提供的 JSON 序列化插件包括：

- **sa-token-jackson**：集成 Jackson（com.fasterxml.jackson），适用于 SpringBoot2/3 等环境。
- **sa-token-jackson3**：集成 Jackson 3（tools.jackson.core），适用于 SpringBoot4、Java 17+ 等环境。
- **sa-token-fastjson**：集成 Fastjson。
- **sa-token-fastjson2**：集成 Fastjson2。
- **sa-token-snack3**：集成 Snack3。
- **sa-token-snack4**：集成 Snack4。

> 若使用 `sa-token-spring-boot-starter` 集成包（含 SpringBoot3），框架会自动引入 Jackson 作为默认 JSON 方案，一般无需额外配置。如需更换为其它 JSON 框架，引入对应插件依赖即可。

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

<!---------------------------- tabs:end ---------------------------->


有关 Redis 集成与序列化配置，详细参考：[集成 Redis](/up/integ-redis)

更多自定义序列化方案（如 Base64、天干地支等），可参考：[序列化插件扩展包](/plugin/custom-serializer)
