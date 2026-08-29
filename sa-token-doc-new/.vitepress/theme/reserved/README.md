# 备用插件（默认关闭）

从现网 Docsify 注释插件本地化改装而来，**上线默认全关**。

开关在同目录 `index.ts` 的 `reservedPlugins`。改 `true` 后重新 `npm run docs:build`。

| 开关 | 原文件 | 行为 |
|---|---|---|
| `star` | `static/is-star-plugin.js` | Gitee star 检查弹层 |
| `survey` | `static/is-fill-in-wj-plugin.js` | 问卷邀请 |
| `docLock` | `static/custom-docsify-plugins/doc-lock-plugin.js` | 章节锁 |
| `docLockByGzh` | `static/custom-docsify-plugins/doc-lock-by-gzh-plugin.js` | 公众号章节锁 |
| `progress` | `static/docsify-plugins/progress.update.js` | 顶部阅读进度条 |

脚本和样式都在本仓库 `public/static/`，不走 CDN。
路径匹配已改成 VitePress 的 `location.pathname`（`/sso/xxx.html`），不再用 Docsify `vm.route.path`。
