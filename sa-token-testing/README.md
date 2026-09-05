# sa-token-testing

测试基础设施与集成测试。公共接线在 `integration-boot2` 验一次；`integration-boot3/4` 只补版本差异，不重复全量。

## 子模块

### sa-token-test-support

测试支持（无 `@Test`），供契约测试等复用。

### sa-token-json-test-common

测试支持（无 `@Test`），供 JSON 插件单测复用：

- sa-token-jackson
- sa-token-jackson3
- sa-token-fastjson
- sa-token-fastjson2
- sa-token-fory-json
- sa-token-snack3
- sa-token-snack4

### sa-token-http-test-common

测试支持（无 `@Test`），供 HTTP 客户端插件单测复用：

- sa-token-forest
- sa-token-okhttps
- sa-token-rest-client
- sa-token-rest-template

### sa-token-plugin-contract-test

契约测试（无 Spring 容器）：

- sa-token-jwt
- sa-token-temp-jwt
- sa-token-serializer-features
- sa-token-sso
- sa-token-oauth2
- sa-token-sign

### sa-token-integration-boot2

集成测试（Boot 2 主集成，覆盖 Starter 接线与业务场景）：

- sa-token-spring-boot-starter
- sa-token-spring-boot-webmvc-reactor-v2v3v4-common
- sa-token-servlet
- sa-token-jackson（Starter 传递依赖）

### sa-token-integration-beaninject-boot2

Bean 注入专项集成测试（**独立模块 / 独立 JVM**，避免污染 `SaManager` 等全局静态状态）：

- 覆盖 `SaBeanInject` + OAuth2 / SSO / Sign / ApiKey 全部 `*BeanInject` 注入点
- 仅验证 Spring Bean → Manager/Strategy 的注入链路，不测插件业务

### sa-token-integration-boot3

集成测试（仅 Boot 3 与 Boot 2 的行为差异）：

- sa-token-spring-boot3-starter
- sa-token-spring-boot-webmvc-v3v4-common
- sa-token-jakarta-servlet

### sa-token-integration-boot4

集成测试（仅 Boot 4 差异；`webmvc-v3v4-common` / `jakarta-servlet` 见 boot3）：

- sa-token-spring-boot4-starter
- sa-token-jackson3

### sa-token-coverage

覆盖率聚合（非功能测试），汇总全仓库生产模块 JaCoCo 报告。

## 运行

```bash
mvn test -pl sa-token-testing/sa-token-integration-boot2 -am
mvn test -pl sa-token-testing/sa-token-integration-beaninject-boot2 -am
mvn test -pl sa-token-testing/sa-token-integration-boot3 -am
mvn test -pl sa-token-testing/sa-token-integration-boot4 -am
```

根目录全量：`mvn test.bat`
