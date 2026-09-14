# Sa-Token 文档站

本目录是 [sa-token.com](https://sa-token.com) 的文档源码，基于 VitePress。

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

- `/` 是官网首页（`public/index.html`）
- `/readme.html` 是文档介绍页
- `/blog/` 是独立博客页

改侧栏：直接改 `.vitepress/sidebar.ts`。

## 发布

```bash
cd sa-token-doc-new
npm i
npm run docs:build
```

把 **`dist/` 里的内容** 上传到 `sa-token.com` 根目录。上传的是编译后的 HTML / JS / CSS，不要上传 `docs/` 下的 markdown。

## 目录说明

```
sa-token-doc-new/
  .vitepress/            配置、主题、侧栏
  public/                独立首页、博客、静态资源
  docs/                  文档正文（md）
  dist/                  构建产物（上传这个）
```
