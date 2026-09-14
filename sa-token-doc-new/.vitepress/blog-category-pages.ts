/**
 * 构建结束后按 blog-sidebar.js 生成博客分类索引页（/blog/essays/index.html 等）。
 * 复用博客模板（doc-nav、页脚、样式），与 /blog/index.html 视觉一致。
 */
import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
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

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const TEMPLATE_DIR = path.join(__dirname, '../public/blog/template')

function fillTemplate(template: string, vars: Record<string, string>) {
  let out = template
  for (const [key, val] of Object.entries(vars)) {
    out = out.replaceAll(`{{${key}}}`, val ?? '')
  }
  return out
}

function loadPartial(name: string) {
  return fs.readFileSync(path.join(TEMPLATE_DIR, 'partials', name), 'utf8')
}

function loadCategoryTemplate() {
  return fs.readFileSync(path.join(TEMPLATE_DIR, 'category-template.html'), 'utf8')
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

function renderCategoryCards(label: string, description: string, items: BlogSidebarItem[]) {
  const cards = items.map((item) => {
    const href = `/blog/${item.url}`
    return (
      `<article class="blog-index-card">` +
      `<div class="blog-index-card-body">` +
      `<div class="blog-index-card-text">` +
      `<h2><a href="${href}">${escapeHtml(item.title)}</a></h2>` +
      `</div>` +
      `<div class="blog-index-card-cover blog-index-card-cover--empty"></div>` +
      `</div></article>`
    )
  })
  return (
    `<h1>${escapeHtml(`Sa-Token 博客 - ${label}`)}</h1>` +
    `<p class="blog-index-desc">${escapeHtml(description)}</p>` +
    `<div class="blog-index-list">${cards.join('')}</div>`
  )
}

function buildCategoryHtml(
  label: string,
  folder: string,
  items: BlogSidebarItem[],
  template: string,
  docNavTpl: string,
  blogRightAsideTpl: string
) {
  const canonical = `${SITE_ORIGIN}/blog/${folder}/index.html`
  const pageTitle = `Sa-Token 博客 - ${label}`
  const description = `Sa-Token 官方博客「${label}」文章列表：${items
    .slice(0, 3)
    .map((i) => i.title)
    .join('、')}等 ${items.length} 篇。`
  const extra = (CATEGORY_KEYWORDS[folder] || 'Java权限认证').split(',')
  const keywords = [...new Set(['Sa-Token', '博客', label, ...extra])].join(',')
  const jsonLd = JSON.stringify({
    '@context': 'https://schema.org',
    '@type': 'CollectionPage',
    name: pageTitle,
    description,
    url: canonical,
    inLanguage: 'zh-CN',
    isPartOf: { '@type': 'Blog', name: 'Sa-Token 博客', url: `${SITE_ORIGIN}/blog/index.html` }
  })

  const assetPrefix = '../../'
  const blogCommonPrefix = '../common/'
  const docNav = fillTemplate(docNavTpl, {
    assetPrefix,
    homeHref: '../../index.html',
    docHref: '../../doc.html',
    blogHref: '/blog/index.html',
    blogNavActive: ' nav-active'
  })
  const blogRightAside = fillTemplate(blogRightAsideTpl, {
    docHref: '../../doc.html'
  })

  return fillTemplate(template, {
    title: escapeHtml(pageTitle),
    description: escapeHtml(description),
    keywords: escapeHtml(keywords),
    canonicalUrl: canonical,
    ogImage: `${SITE_ORIGIN}/logo.png`,
    jsonLd,
    assetPrefix,
    blogCommonPrefix,
    docNav,
    blogRightAside,
    categoryLabel: escapeHtml(label),
    articleCount: String(items.length),
    categoryContent: renderCategoryCards(label, description, items)
  })
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

/** 在 dist 写出分类索引页，并把 URL 补进 blog/sitemap.xml（构建结束时并入根 sitemap.xml） */
export function generateBlogCategoryPages(dist: string, publicRoot: string) {
  const groups = parseBlogSidebar(publicRoot)
  const today = new Date().toISOString().slice(0, 10)
  const newLocs: string[] = []
  const template = loadCategoryTemplate()
  const docNavTpl = loadPartial('doc-nav.html')
  const blogRightAsideTpl = loadPartial('blog-right-aside-index.html')

  for (const group of groups) {
    if (!group.items?.length) continue
    const folder = group.items[0].url.split('/')[0]
    if (!folder) continue
    const outDir = path.join(dist, 'blog', folder)
    fs.mkdirSync(outDir, { recursive: true })
    fs.writeFileSync(
      path.join(outDir, 'index.html'),
      buildCategoryHtml(group.label, folder, group.items, template, docNavTpl, blogRightAsideTpl)
    )
    newLocs.push(`${SITE_ORIGIN}/blog/${folder}/index.html`)
  }

  appendBlogSitemapUrls(path.join(dist, 'blog/sitemap.xml'), newLocs, today)
}
