# Sa-Token 文档站（VitePress）

从 Docsify 迁过来的新文档工程。源目录 `sa-token-doc/` 上线前一直留着，不要改那边。

线上文档：https://sa-token.com  
本目录构建产物才是要上传到站点根目录的文件。

## 环境

- Node **20.11+**（建议 22）
- 包管理：npm

```bash
cd sa-token-doc-new
npm i
```

## 常用命令

| 命令 | 作用 |
|---|---|
| `npm run docs:dev` | 本地预览（http://localhost:5173） |
| `npm run docs:build` | 编译到 `dist/` |
| `npm run docs:preview` | 预览已构建的 dist |

开发时：

- `/` 是独立官网首页（`public/index.html`）
- `/readme.html` 是文档介绍页
- `/blog/` 仍是独立 HTML，不进 VitePress 主题

## 发版（上传 dist，不要上传 md）

```bash
cd sa-token-doc-new
npm i
npm run docs:build
```

把 **`dist/` 里的内容** 发到 `sa-token.com` 根目录。

- 上传的是编译后的 HTML / JS / CSS，**不是** `docs/` 下的 markdown
- 不要再按 Docsify 的方式把 md 丢到服务器
- 服务器上已有的历史版 `/v/v1.xx/` **不要覆盖**
- `doc.html`、`doc/index.html` 是旧 hash 链接的跳转页，要一起带上

改文档日常流程：改对应 md → `npm run docs:build` → 上传 dist。  
改侧栏：直接改 `.vitepress/sidebar.ts`。

## 目录说明

```
sa-token-doc-new/
  .vitepress/            配置、主题、侧栏
  public/                独立首页、博客、static、doc.html 跳转
  docs/                  文档正文（md）
  dist/                  构建产物（上传这个）
```

备用插件（star / 章节锁 / 问卷 / 进度条）在 `.vitepress/theme/reserved/`，默认全关。开关见该目录 README。
