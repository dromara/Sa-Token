---
title: "Spring Boot 源码运行指南"
keywords: "Sa-Token,sa-token,satoken,Sa-Token文档,源码运行指南,Spring Boot"

description: "本地运行 Sa-Token 源码与 Demo：从 Gitee / GitHub / AtomGit 克隆，到启动官方示例项目的步骤。"
---

# 源码运行指南

如果你想阅读或调试 Sa-Token 源码，可按以下步骤在本地运行框架及 Demo 示例。

---

### 1、下载源码

**方式一：Git 克隆（推荐）**

Sa-Token 源码目前在 Gitee、GitHub、AtomGit 三个平台进行托管，**你可以从任意其一进行克隆下载源码**。

**学习测试建议拉取 `master` 分支**；`dev` 为开发分支，代码随时变动，有时候会运行不起来。（如果你要提交 pr，则需要拉取 dev 分支）

:::tabs
== Gitee

``` shell
git clone -b master https://gitee.com/dromara/sa-token.git
```

== GitHub

``` shell
git clone -b master https://github.com/dromara/sa-token.git
```

== AtomGit

``` shell
git clone -b master https://atomgit.com/dromara/sa-token.git
```

:::


**方式二：官网 / 仓库页下载压缩包**

在 [Gitee](https://gitee.com/dromara/sa-token)、[GitHub](https://github.com/dromara/sa-token)、[AtomGit](https://atomgit.com/dromara/sa-token) 三者任意一个平台，使用「克隆 / 下载」下载源码压缩包，解压后即可使用。

---

### 2、从 IDEA 导入项目

打开 IDEA，选择 **Open**，选中源码根目录，进行打开。

源码目录说明请参考：[仓库目录](/arch/dir-intro)。

---

### 3、运行

Sa-Token 并非一个完整可运行的传统项目，而是一个需要被引入的框架。因此不存在传统意义下的运行源码，**你可以运行的是其中各项 demo 示例**。

打开 `sa-token-demo` 目录，选择你想要运行的示例添加为 Maven 项目，打开 `XxxApplication.java` 运行。

<img class="s-w-sh" src="/big-file/doc/start/import-demo-run.png" alt="运行示例">

注意：首次在 IDEA 中运行项目时，可能会触发报错：

```
java: 程序包cn.dev33.satoken.apikey不存在
```

这是因为在 Sa-Token 中，`sa-token-apikey`、`sso`、`oauth2`、`sign`、`jwt` 等模块均为可选包，IDEA 有时候会跳过相关模块的编译，然后运行时又找不到这些包，触发报错。

**解决方案是先运行一下 `sa-token-demo-first-run` 这个 demo**，该项目强制引入了这些模块，会引导 IDEA 对相关模块进行编译。

如果此项目仍然无法运行成功，请根据控制台的报错提示，**把相关模块加入到此项目的 pom.xml 中**，然后点击右上角 maven 刷新依赖图标，然后再次点击运行。

如果还不行，那就在项目根目录进入 cmd，执行 `mvn package`，然后重新运行试试。

如果还是不行，那就加群反馈一下，我们再根据你的情况寻找一下新方案。


### 4、运行文档

根目录双击执行 `preview-doc.bat` 可在本地预览 `sa-token-doc` 文档。

