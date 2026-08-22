# 更新日志

上次同步日期：2026-08-21 12:33:07


## v1.46.0 版本

### 2026-08-21 · 博客代码块增强与文章收录
- 新增：博客代码块语法高亮（JSON/TS/Rust/TOML）与复制按钮。
- 新增：收录 Sa-Token Rust/Go 生态相关博客文章并补全导航。
- 优化：更新旋武社区文章封面图并刷新全站 sitemap。
- 优化：统一博客文章标题层级与样式，调整旋武社区文章目录结构。


### 2026-08-20 · 博客动态侧边栏与图床
- 新增：博客侧边栏改为 JS 动态渲染，新增 v1.46.0 发布文章并刷新 SEO。 **[重要]**
- 修改：博客文章图片资源迁移至 CDN（res.dev33.cn）。
- 优化：同步刷新全站 sitemap lastmod 日期。
- 新增：收录 Sa-Token-Rust 入驻旋武社区等多篇历史博客文章并补全导航。
- 优化：更新博客文章开源仓库地址为 dromara，优化文档与 sitemap。


### 2026-08-19 · 文档与 Redis 集成说明
- 新增：文档补充 Redis 6.0 以下版本 `SET KEEPTTL` 报错解决方案（`common-questions.md`、`integ-redis.md`）。
- 优化：README 多语言版本前言措辞与加粗强调。
- 文档：整理 v1.46.0 更新日志，补充重要/漏洞修复标记并规范链接。


### 2026-08-18 · SSO 安全加固与 HTTP 请求插件
- 新增：HTTP 请求扩展插件（`sa-token-rest-client` / `sa-token-rest-template`，RestClient / RestTemplate 实现 `SaHttpTemplate`）、`http-extend.md` 文档，以及 SSO Client Spring Boot 4 示例。 **[重要]**
- 新增：SSO 客户端指定账号单点注销 API；`StpInterface.isDisabled` 补充 `loginType` 参数。
- 修复：SSO `buildServerAuthUrl` 识别 URL 编码后的 back 参数并增强重定向编码/解码健壮性；NoSdk demo 签名校验补充时间窗与 nonce 防重放。
- 修改：框架版本号升级至 1.46.0；javadoc/sources/gpg 仅在发 Maven 中央时启用；BOM 补齐 `sa-token-caffeine` 与 `sa-token-loveqq-boot-starter`。 **[重要]**
- 修复：`isUrl` 支持 IPv6 方括号地址并拒绝裸 IPv6；redis-template jdk-serializer KEEPTTL 泛型编译错误；序列化测试去掉 `SaJsonType`。
- 修改：OAuth2 `expiresTime` 为 -1 时按永久有效存储 Token；OAuth2 demo 拒绝授权时回传 state。


### 2026-08-17 · 注销前侦听器与博客列表 JS 化
- 新增：注销前侦听器 `doBeforeLogout` / `doBeforeKickout` / `doBeforeReplaced`。 **[重要]**
- 新增：`getSaTokenConfig` 策略支持自定义配置来源。
- 修复：`isLastingCookie` 为 null 时 `getCookieTimeout` 不再 NPE。
- 修改：SSO `getClient` 统一通过 `getClients` 查找以支持子类覆盖；back 为空时 `buildServerAuthUrl` 跳过追加。 fix: [#842](https://github.com/dromara/Sa-Token/issues/842)
- 优化：`sa-token-dependencies` 不再锁定 Reactor 版本，避免与 Spring Cloud Gateway 冲突。 fix: [#622](https://github.com/dromara/Sa-Token/issues/622)
- 优化：博客社区/推荐列表改为 JS 数据驱动渲染（`community.js`、`recommended.js`），博客入口页迁移声明并补充最新文章；捐赠列表新增赞赏记录。


### 2026-08-16 · JSON 插件与安全增强
- 新增：`sa-token-fory-json` JSON 序列化插件（ForyJson 实现 `SaJsonTemplate`）。 **[重要]**
- 新增：`allowLoginIdColon` 配置，默认禁止 loginId 包含冒号。 **[重要]**
- 新增：`SaRequest` / `SaResponse` / `SaStorage` 创建策略（`SaCreateSaRequestFunction` 等）。 fix: [#841](https://github.com/dromara/Sa-Token/issues/841)
- 修改：随机字符串/数字改用 CSPRNG（`SecureRandom`）生成；JWT `extraData` 禁止包含保留字段。
- 重构：OAuth2 `doConfirm` 确认授权流程统一校验；Session 文档示例改用 `getModel` 泛型方法。
- 修复：JSON 反序列化容错优化。 fix: [#ICOTM8](https://gitee.com/dromara/sa-token/issues/ICOTM8)
- 修复：demo-ssm 适配 v1.42.0+ 上下文机制。


### 2026-08-15 · 安全漏洞修复
- 修复：OAuth2/SSO `redirect` 参数绕过 allow-url 校验的安全漏洞。 **[重要]**
- 修复：Jackson DefaultTyping 多态反序列化 RCE，新增 `SaJsonStrategy` 全局类型白名单。 **[重要]**
- 修复：Snack3 序列化不再写入类型信息，对齐 fastjson 安全策略。
- 修复：Context Filter 注册 REQUEST+ASYNC，修复 SSE/Flux 流式返回上下文未初始化。 fix: [#IC4XFE](https://gitee.com/dromara/sa-token/issues/IC4XFE) 、[#ICB9OJ](https://gitee.com/dromara/sa-token/issues/ICB9OJ)


### 2026-08-14 · Redis 与 Session 增强
- 修复：`sa-token-redis-template` 及 jdk-serializer 在 update key 时 TTL 偏移的问题。 fix: [#I80P5O](https://gitee.com/dromara/sa-token/issues/I80P5O) 、[#ICWJOQ](https://gitee.com/dromara/sa-token/issues/ICWJOQ)
- 新增：Session 新增 `getList` / `getSet` / `getMap` 类型安全集合读取。
- 重构：`SaOAuth2Dao` 改用带 Class 参数的 `getObject` 反序列化。 fix: [#IK5GYU](https://gitee.com/dromara/sa-token/issues/IK5GYU)
- 优化：升级 fastjson/fastjson2 至 2.0.64；OAuth2 server demo 前端资源本地化并切换 Redis 集成方式。


### 2026-08-13 · Redisson 独立连接与 Redis 优化
- 新增：`sa-token-alone-redisson` 独立连接插件及 Spring Boot 3/4 示例（`alone-redisson.md` 文档）。 **[重要]**  fix: [#710](https://github.com/dromara/Sa-Token/issues/710)、 fix: [#ICE2V0](https://gitee.com/dromara/sa-token/issues/ICE2V0)
- 新增：`SaTokenDaoForRedisson` 支持指定 Codec（默认 `StringCodec`），update 方法改用 `setAndKeepTTL` 原子保留原过期时间。 fix: [#791](https://github.com/dromara/Sa-Token/issues/791)
- 新增：RedisTemplate Dao 支持重写 `wrapKey` 自定义键前缀。 fix: [#956](https://github.com/dromara/Sa-Token/issues/956)
- 修复：redis-template `searchData` 由 KEYS 改为 SCAN，兼容低版本 Spring Data Redis。 fix: [#702](https://github.com/dromara/Sa-Token/issues/702)
- 修复：Boot3+ WebFlux/Gateway `setStatus` NoSuchMethodError（reactor starter 结构调整）。 **[重要]**  fix: [#IIAW1A](https://gitee.com/dromara/sa-token/issues/IIAW1A) 、[#916](https://github.com/dromara/Sa-Token/issues/916) 、[#IHRIBY](https://gitee.com/dromara/sa-token/issues/IHRIBY)
- 优化：精简 springboot-redisson 示例；补充按配置决定是否启用 Redis 的示例。 fix: [#I9I4ZG](https://gitee.com/dromara/sa-token/issues/I9I4ZG)


### 2026-08-12 · 独立博客、SEO 与暗色主题
- 新增：独立博客系统上线，收录 46 篇原创文章并新增 sitemap。 **[重要]**
- 新增：文档站暗色主题切换，支持 5 种 IDE 风格。
- 优化：全站 SEO（Open Graph、JSON-LD、Twitter Card、canonical）；博客按分类重组（essays、release、featured、other、special）。
- 修改：博客文章底部改为转载版权声明；补充 JSON Body 验签社区示例。
- 修复：博客导航链接统一为绝对路径；优化首页导航、讨论群文案与文章页网格背景。


### 2026-08-11 · 多语言支持
- 新增：文档站点多语言切换（基于 translate.js，支持 11 种语言）。 **[重要]**
- 新增：README 多语言版本（英文、日文、韩文、俄文、繁体中文）与切换入口。 **[重要]**
- 补充：SSE/Flux 流式返回相关常见问题。


### 2026-08-10 · 域名迁移与安全
- 修改：全仓库域名 sa-token.cc 统一迁移至 sa-token.com。 **[重要]**
- 新增：文档版本选择器改为动态加载（all-version-common.js）；README 补充 NodeJS 版本链接。 merge: [pr 928](https://github.com/dromara/Sa-Token/pull/928)  fix: [#900](https://github.com/dromara/Sa-Token/issues/900)
- 新增：SECURITY.md 安全策略及联系邮箱。 **[重要]**  merge: [pr 933](https://github.com/dromara/Sa-Token/pull/933)  fix: [#932](https://github.com/dromara/Sa-Token/issues/932) 、[#877](https://github.com/dromara/Sa-Token/issues/877)
- 修复：修正 ConcurrentHashMap 的错误 import 路径。 merge: [pr 359](https://gitee.com/dromara/sa-token/pulls/359)
- 修改：为 setLoginType 补充运行时不可修改警告注释；token_type 示例统一为 Bearer。
- 清理：忽略本地工具与临时产物目录（tools、temp-file、__pycache__）。


### 2026-08-09 · 文档与示例优化
- 新增：源码运行指南文档与首次运行引导示例（`sa-token-demo-first-run`）。
- 新增：导航栏安全推荐下拉菜单；首页 Stars 对比扩展鉴权与 OAuth2/JWT 框架。
- 优化：导航栏「案例」改为下拉菜单展示 Awesome-Sa-Token 链接；开源案例卡片悬浮流光效果。
- 优化：文档站点导航交互、首页渐变标题浏览器兼容、README 首屏需求提交入口。
- 修改：移除 OAuth2 常见问题中单点注销地址说明；修正 API 手册与 SSO/OAuth2/WebFlux/Dubbo 文档。


### 2026-08-08 · 文档补充与措辞统一
- 新增：文档前言补充浏览器书签快捷键说明。
- 优化：统一 token 续期相关注释措辞（`StpLogic`、`StpUtil`、`StpUserUtil`）。
- 修改：统一日志文案用词，「帐号」改为「账号」（`SaTokenListenerForLog`）。
- 更新：同步文档赞赏名单与博客文章收录列表。


### 2026-08-05 · 首页生态展示
- 新增：首页新增合作企业展示链接，并更新合作企业链接。
- 优化：调整首页 dromara 生态项目展示。
- 新增：新增赞赏记录。


### 2026-07-31 · 工程配置
- 修改：`.gitignore` 增加对 `.firecrawl` 与 `.claude` 目录的忽略。


### 2026-07-26 · 链接与博客更新
- 更新：新增公众号博客链接收录。
- 修改：README 中 Bean Searcher 链接更新为 REST 版本。


### 2026-07-20 · 文档整体优化
- 优化：文档站点整体优化（侧边栏、首页、捐赠页、pro 文档、docsify 插件等）。


### 2026-07-18 · 视频教程与赞赏
- 新增：新增乐之者java B站视频教程链接。
- 新增：新增 willSleep 赞赏记录。


## 博客收录与赞助商链接

### 2026-07-04
- 新增：收录 4 篇 Sa-Token 相关博客文章。


### 2026-06-29
- 新增：收录新的博客文章链接。
- 新增：首页新增赞助商链接（辽宁天云港云计算有限责任公司），新增 `github-logo.svg` 图标。


## 博客收录与企业案例

### 2026-06-21
- 新增：收录 4 篇 Sa-Token 相关博客文章。


### 2026-06-16
- 优化：文档站点整体优化（侧边栏、首页等）。


### 2026-06-15
- 新增：首页新增无锡梦雪网络科技有限公司企业案例。
- 更新：收录 4 篇博客文章、新增 2 条赞助者记录。


## 内容收录与 Solon 插件优化

### 2026-06-07
- 新增：收录 2 篇 CSDN 博文，更新捐赠列表。


### 2026-06-04
- 优化：`sa-token-solon-plugin` 由 PathAnalyzer 适配改为 PathMatcher，优化 Config 示例。 merge: [pr 364](https://gitee.com/dromara/sa-token/pulls/364)  fix: [#IJTNJK](https://gitee.com/dromara/sa-token/issues/IJTNJK)
- 优化：升级依赖 noear-redisx 至 1.8.5、noear-snack4 至 4.0.50。


### 2026-06-02
- 新增：收录博客文章链接与捐赠记录。


### 2026-05-28
- 更新：更新 Sa-Token 交流10群 QQ 群号与入群链接。


### 2026-05-24
- 新增：收录两篇 CSDN 博客链接。


### 2026-05-22
- 优化：优化 README 展示。
- 修改：订正 SSO-Client 接收消息推送地址的路由说明。


### 2026-05-19
- 新增：新增 star-guide 引导组件（`sa-token-doc/a/star-guide/`）。
- 更新：同步并调整博客文章收录列表。


### 2026-05-17
- 新增：新增捐赠记录（森林雨，2026-05-17）。


## 赞助与博客同步

### 2026-05-10
- 更新：同步最新赞助者列表与博客列表。


## 赞助名单同步与 skills 清理

### 2026-05-03
- 更新：同步最新赞助者名单。
- 清理：移除 `.agents/skills/` 下多个已弃用的 skill 文件。


## 博客同步与 Demo 示例大全

### 2026-04-26
- 更新：同步最新博客列表。


### 2026-04-24
- 优化：文档格式优化（`sa-token-doc/README.md`）。


### 2026-04-22
- 新增：新增「Sa-Token 集成 Demos 示例大全下载」文档（`download-demos.md`），并同步各章节下载链接。


### 2026-04-20
- 更新：同步最新博客列表。


## 赞助同步、多语言与 SSO 补全

### 2026-04-13
- 更新：更新赞助者名单与博客列表。


### 2026-04-09
- 新增：新增多语言版本链接（`README.md` 与文档 README）。


### 2026-04-04
- 优化：文档样式优化（`doc.html`、`static/doc.css`）。


### 2026-04-03
- 新增：新增 `.MEMO/4--sa-token依赖关系草稿.txt`，整理 pom.xml 依赖关系草稿。


### 2026-04-02
- 更新：同步最新博客列表与赞助者名单。


### 2026-04-01
- 新增：补全最新版 SSO NoSdk 模式实现，新增 `SsoSignUtil`，完善 demo 与 `sso-nosdk.md` 文档。 **[重要]**


## v1.45.0 发布与 SpringBoot4 支持

### 2026-03-11
- 优化：优化 README 与文档站点文案（前言、emoji、⚠️ 标识、仓库 description 等）。
- 新增：OAuth2 章节新增数据互通后的 token 过期策略说明。
- 修改：README 链接由 http 改为 https。


### 2026-03-10
- 重构：将 `.cursor/skills/` 目录迁移为通用的 `.agents/skills/` 目录。
- 新增：增加 `sa-token-alone-redis-by-spring-boot4` 包使用说明。


### 2026-03-09
- 更新：同步最新博客列表、赞助者名单。


### 2026-03-08
- 发布：发布 v1.45.0 版本。 **[重要]**
- 新增：新增 `sa-token-alone-redis-by-spring-boot4` 插件及 `sa-token-demo-alone-redis-sb4` 示例，实现 SpringBoot4 下权限缓存与数据缓存分离。 **[重要]**
- 新增：新增 json 序列化插件展示说明，补全 `StpUtil` API 说明与 `replacedLoginExitMode` 配置项说明。
- 优化：统一各模块 POM 的 name 与 artifactId 并补充 description，完善 pom.xml 信息。
- 新增：新增 `upgrade-version` skill，用于一键更新版本号。
