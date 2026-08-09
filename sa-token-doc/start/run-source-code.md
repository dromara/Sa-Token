# 源码运行指南

Sa-Token 并非一个完整可运行的传统项目，而是一个需要被引入的框架。因此不存在传统意义下的运行源码，**你可以运行的是其中各项 demo 示例**。

如果你想阅读或调试 Sa-Token 源码，可按以下步骤在本地运行框架及 Demo 示例。

源码目录说明请参考：[仓库目录](/arch/dir-intro)。

---

### 1、下载源码

**方式一：Git 克隆（推荐）**

Sa-Token 源码目前在 Gitee、GitHub、AtomGit 三个平台进行托管，**你可以从任意其一进行克隆下载源码**。

**学习测试建议拉取 `master` 分支**；`dev` 为开发分支，代码随时变动，有时候会运行不起来。（如果你要提交 pr，则需要拉取 dev 分支）

<!---------------------------- tabs:start ------------------------------>
<!-------- tab:Gitee -------->
``` shell
git clone -b master https://gitee.com/dromara/sa-token.git
```

<!-------- tab:GitHub -------->
``` shell
git clone -b master https://github.com/dromara/sa-token.git
```

<!-------- tab:AtomGit -------->
``` shell
git clone -b master https://atomgit.com/dromara/sa-token.git
```
<!---------------------------- tabs:end ------------------------------>

**方式二：官网 / 仓库页下载压缩包**

在 [Gitee](https://gitee.com/dromara/sa-token)、[GitHub](https://github.com/dromara/sa-token)、[AtomGit](https://atomgit.com/dromara/sa-token) 三者任意一个平台，使用「克隆 / 下载」下载源码压缩包，解压后即可使用。

---

### 2、从 IDEA 导入项目

1. 打开 IDEA，选择 **Open**，选中源码根目录下的 `pom.xml`（`sa-token-parent` 工程）。
2. 以 **Maven 项目** 方式导入，等待依赖下载与索引完成。
3. 如需运行 Demo，在 `sa-token-demo` 目录下找到对应示例（如 `sa-token-demo-springboot`），右键将其 **Add as Maven Project** 添加为 Maven 模块。

> 注：`sa-token-demo`、`sa-token-test` 为独立 Maven 工程，未纳入根 `pom.xml` 的 modules，需按需单独添加。

---

### 3、运行

1. 下载代码（学习测试用 `master` 分支）。
2. 从根目录导入项目。
3. 选择相应的示例添加为 Maven 项目，打开 `XxxApplication.java` 运行。

<img class="s-w-sh" src="/big-file/doc/start/import-demo-run.png" alt="运行示例">

常用 Demo 路径示例：

| 示例 | 路径 |
|------|------|
| SpringBoot 集成 | `/sa-token-demo/sa-token-demo-springboot` |
| WebFlux 集成 | `/sa-token-demo/sa-token-demo-webflux` |
| SSO 单点登录 | `/sa-token-demo/sa-token-demo-sso/` |
| OAuth2 认证 | `/sa-token-demo/sa-token-demo-oauth2/` |

更多示例见：[Sa-Token 集成示例大全下载](/more/download-demos)。

---

### 常见问题

**首次运行报错：程序包 cn.dev33.satoken.oauth2 不存在**

部分插件为可选模块，首次导入时可能尚未编译。可在项目根目录执行 `mvn package` 后重新运行。更多排查方式见：[常见报错 - 导入源码 oauth2 不存在](/more/common-questions?id=q：在-idea-导入源码，运行报错：java-程序包cndev33satokenoauth2不存在。)

**一键预览文档**

根目录执行 `preview-doc.bat` 可在本地预览 `sa-token-doc` 在线文档。
