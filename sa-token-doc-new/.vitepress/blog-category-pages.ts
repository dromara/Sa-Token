/**
 * 构建结束后按 blog-sidebar.js 生成博客分类索引页（/blog/essays/index.html 等）。
 * 不动 md 正文，只增聚合页供收录与内链。
 */
import fs from 'node:fs'
import path from 'node:path'
import { SITE_ORIGIN } from './seo.ts'

type BlogSidebarItem = { title: string; url: string }
type BlogSidebarGroup = { label: string; items: BlogSidebarItem[] }

const CATEGORY_KEYWORDS: Record<string, string> = {
  featured: '精选,Spring Boot,实战教程',
  essays: '随笔,Spring Boot,接入教程,SSO,JWT',
  release: '版本更新,发版日志',
  special: '特殊文章,生态',
  other: '其它,入门,Shiro对比'
}

function parseBlogSidebar(publicRoot: string): BlogSidebarGroup[] {
  const raw = fs.readFileSync(path.join(publicRoot, 'blog/common/blog-sidebar.js'), 'utf8')
  const json = raw.replace(/^\s*window\.SA_TOKEN_BLOG_SIDEBAR\s*=\s*/, '').replace(/;\s*$/, '')
  return JSON.parse(json) as BlogSidebarGroup[]
}

function escapeHtml(s: string) {
  return s
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

function buildCategoryHtml(label: string, folder: string, items: BlogSidebarItem[]) {
  const canonical = `${SITE_ORIGIN}/blog/${folder}/index.html`
  const pageTitle = `Sa-Token 博客 - ${label}`
  const description = `Sa-Token 官方博客「${label}」文章列表：${items
    .slice(0, 3)
    .map((i) => i.title)
    .join('、')}等 ${items.length} 篇。`
  const extra = (CATEGORY_KEYWORDS[folder] || 'Java权限认证').split(',')
  const keywords = [...new Set(['Sa-Token', '博客', label, ...extra])].join(',')
  const list = items
    .map(
      (item) =>
        `<li><a href="/blog/${escapeHtml(item.url)}">${escapeHtml(item.title)}</a></li>`
    )
    .join('\n')
  const jsonLd = JSON.stringify({
    '@context': 'https://schema.org',
    '@type': 'CollectionPage',
    name: pageTitle,
    description,
    url: canonical,
    inLanguage: 'zh-CN',
    isPartOf: { '@type': 'Blog', name: 'Sa-Token 博客', url: `${SITE_ORIGIN}/blog/index.html` }
  })

  return `<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <title>${escapeHtml(pageTitle)}</title>
  <meta name="description" content="${escapeHtml(description)}">
  <meta name="keywords" content="${escapeHtml(keywords)}">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="canonical" href="${canonical}">
  <meta property="og:type" content="website">
  <meta property="og:title" content="${escapeHtml(pageTitle)}">
  <meta property="og:description" content="${escapeHtml(description)}">
  <meta property="og:url" content="${canonical}">
  <meta property="og:image" content="${SITE_ORIGIN}/logo.png">
  <meta property="og:site_name" content="Sa-Token">
  <meta property="og:locale" content="zh_CN">
  <link rel="shortcut icon" type="image/x-icon" href="/favicon.ico">
  <link rel="stylesheet" href="/static/doc.css">
  <script type="application/ld+json">${jsonLd}</script>
</head>
<body>
  <header style="padding:16px 24px;border-bottom:1px solid #eee;">
    <a href="/"><img src="/logo.png" alt="Sa-Token" height="32" style="vertical-align:middle"></a>
    <nav style="margin-top:12px;font-size:14px;">
      <a href="/">首页</a> ·
      <a href="/readme.html">文档</a> ·
      <a href="/blog/index.html">博客</a> ·
      <strong>${escapeHtml(label)}</strong>
    </nav>
  </header>
  <main style="max-width:800px;margin:24px auto;padding:0 16px;">
    <h1>${escapeHtml(pageTitle)}</h1>
    <p>${escapeHtml(description)}</p>
    <ul style="line-height:2;">
${list}
    </ul>
  </main>
  <footer style="text-align:center;padding:24px;font-size:13px;color:#666;">
    <a href="/blog/index.html">返回博客首页</a> · Copyright © Sa-Token · sa-token.com
  </footer>
</body>
</html>`
}

function appendBlogSitemapUrls(sitemapPath: string, urls: string[], today: string) {
  if (!fs.existsSync(sitemapPath) || !urls.length) return
  let xml = fs.readFileSync(sitemapPath, 'utf8')
  for (const loc of urls) {
    if (xml.includes(`<loc>${loc}</loc>`)) continue
    xml = xml.replace(
      '</urlset>',
      `  <url>\n    <loc>${loc}</loc>\n    <lastmod>${today}</lastmod>\n  </url>\n</urlset>`
    )
  }
  fs.writeFileSync(sitemapPath, xml)
}

/** 在 dist 写出分类索引页，并把 URL 补进 blog/sitemap.xml */
export function generateBlogCategoryPages(dist: string, publicRoot: string) {
  const groups = parseBlogSidebar(publicRoot)
  const today = new Date().toISOString().slice(0, 10)
  const newLocs: string[] = []

  for (const group of groups) {
    if (!group.items?.length) continue
    const folder = group.items[0].url.split('/')[0]
    if (!folder) continue
    const outDir = path.join(dist, 'blog', folder)
    fs.mkdirSync(outDir, { recursive: true })
    fs.writeFileSync(path.join(outDir, 'index.html'), buildCategoryHtml(group.label, folder, group.items))
    newLocs.push(`${SITE_ORIGIN}/blog/${folder}/index.html`)
  }

  appendBlogSitemapUrls(path.join(dist, 'blog/sitemap.xml'), newLocs, today)
}
