/**
 * 旧站入口 /doc.html：浏览器一律 JS 跳到新地址；
 * 源码里仍放真实目录内链，给不执行 JS 的爬虫跟。
 */
import fs from 'node:fs'
import path from 'node:path'
import { sidebar } from './sidebar.ts'
import { SITE_ORIGIN } from './seo.ts'

type SidebarGroup = {
  text: string
  items?: { text: string; link?: string }[]
}

const DOC_CANONICAL = `${SITE_ORIGIN}/doc.html`

/** 浏览器打开即跳：/doc.html → /readme.html，/doc.html#/use/login-auth → /use/login-auth.html */
const HASH_REDIRECT_SCRIPT = `(function () {
  var hash = location.hash || '';
  var search = location.search || '';
  var inner = hash.replace(/^#\\/?/, '');
  var qIndex = inner.indexOf('?');
  var pathPart = qIndex >= 0 ? inner.slice(0, qIndex) : inner;
  var id = '';
  if (qIndex >= 0) {
    inner.slice(qIndex + 1).split('&').forEach(function (pair) {
      var kv = pair.split('=');
      if (kv[0] === 'id') {
        try { id = decodeURIComponent(kv[1] || ''); }
        catch (e) { id = kv[1] || ''; }
      }
    });
  }
  pathPart = (pathPart || '').replace(/\\/+$/, '');
  var dest;
  if (!pathPart || pathPart === 'index' || pathPart === 'README' || pathPart === 'readme') dest = '/readme.html';
  else dest = '/' + pathPart.replace(/^\\/+/, '') + '.html';
  dest = dest.replace(/\\/+/g, '/');
  if (search) dest += search;
  if (id.charAt(0) === '_') id = id.slice(1);
  if (id) dest += '#' + id;
  location.replace(dest);
})();`

function escapeHtml(s: string) {
  return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;')
}

function renderToc() {
  const groups = sidebar as SidebarGroup[]
  const chunks: string[] = []
  for (const group of groups) {
    const items = (group.items || []).filter((item) => item.link && !item.link.startsWith('http'))
    if (!items.length) continue
    chunks.push(`    <h2>${escapeHtml(group.text)}</h2>`)
    chunks.push('    <ul>')
    for (const item of items) {
      chunks.push(`      <li><a href="${escapeHtml(item.link!)}">${escapeHtml(item.text)}</a></li>`)
    }
    chunks.push('    </ul>')
  }
  return chunks.join('\n')
}

/** 写出 /doc.html 源码：目录给爬虫，hash 跳转给旧书签 */
export function renderLegacyDocHtml() {
  return `<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <title>Sa-Token 文档</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="Sa-Token 官方文档目录：登录认证、权限认证、SSO、OAuth2.0、微服务鉴权等章节。">
  <link rel="canonical" href="${DOC_CANONICAL}">
  <link rel="icon" href="/favicon.ico">
  <script>${HASH_REDIRECT_SCRIPT}</script>
  <style>
    body { max-width: 720px; margin: 32px auto; padding: 0 20px 48px; font-family: "PingFang SC", "Microsoft YaHei", sans-serif; color: #2c3e50; line-height: 1.7; }
    h1 { font-size: 28px; font-weight: 600; }
    h2 { margin-top: 28px; font-size: 18px; color: #1e8f5c; }
    p { color: #4e6e8e; }
    a { color: #1e8f5c; }
    ul { padding-left: 20px; }
    li { margin: 4px 0; }
  </style>
</head>
<body>
  <h1>Sa-Token 文档</h1>
  <p>正在跳转到新版文档… 若没有自动跳转，请从下方目录进入对应章节。</p>
  <p><a href="/readme.html">进入文档介绍</a> · <a href="/">返回官网首页</a></p>
  <nav>
${renderToc()}
  </nav>
</body>
</html>
`
}

/** 构建后覆盖 dist 里从 public 拷来的空跳转页 */
export function writeLegacyDocHtml(dist: string) {
  const html = renderLegacyDocHtml()
  fs.writeFileSync(path.join(dist, 'doc.html'), html)
  const dir = path.join(dist, 'doc')
  fs.mkdirSync(dir, { recursive: true })
  fs.writeFileSync(path.join(dir, 'index.html'), html)
}
