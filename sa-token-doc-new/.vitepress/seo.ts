/**
 * 文档页 SEO：标题、description、canonical、Open Graph、JSON-LD、sitemap 过滤。
 * 给爬虫看的地址一律不带 ?way=（统计参数只留在用户地址栏）。
 */
import fs from 'node:fs'
import path from 'node:path'
import type { HeadConfig, PageData } from 'vitepress'

export const SITE_ORIGIN = 'https://sa-token.com'
export const SITE_DESCRIPTION =
  'Sa-Token 官方文档：Java 权限认证框架接入指南，覆盖登录认证、权限认证、SSO 单点登录、OAuth2.0、微服务网关鉴权、分布式 Session、注解鉴权与路由拦截等。'
export const OG_IMAGE = `${SITE_ORIGIN}/logo.png`

/** md 相对路径 → 线上路径，如 use/login-auth.md → /use/login-auth.html */
export function pagePath(relativePath: string) {
  if (!relativePath || relativePath === '404.md') return '/404.html'
  if (/^readme\.md$/i.test(relativePath)) return '/readme.html'
  const p = relativePath.replace(/(^|\/)index\.md$/, '$1').replace(/\.md$/, '.html')
  return p.startsWith('/') ? p : `/${p}`
}

/** 站内 href 补上 .html，外链原样返回。空路径落到 /readme.html */
export function withHtmlExt(href: string) {
  if (/^https?:\/\//.test(href)) return href
  if (!href || href === '/') return '/readme.html'
  const hashIndex = href.indexOf('#')
  const hash = hashIndex >= 0 ? href.slice(hashIndex) : ''
  const beforeHash = hashIndex >= 0 ? href.slice(0, hashIndex) : href
  const qIndex = beforeHash.indexOf('?')
  const query = qIndex >= 0 ? beforeHash.slice(qIndex) : ''
  let p = (qIndex >= 0 ? beforeHash.slice(0, qIndex) : beforeHash).replace(/\/+$/, '')
  if (!p) return '/readme.html' + query + hash
  if (!p.startsWith('/')) p = `/${p}`
  if (!/\.[a-zA-Z0-9]+$/.test(p)) p += '.html'
  return p + query + hash
}

/** 把正文里的站内链接补成 .html，给爬虫跟。 */
export function rewriteMarkdownDocLinks(src: string) {
  return src.replace(/\]\((\/(?!\/)[^)\s]+)\)/g, (full, inner: string) => {
    const hashIndex = inner.indexOf('#')
    const hash = hashIndex >= 0 ? inner.slice(hashIndex) : ''
    const beforeHash = hashIndex >= 0 ? inner.slice(0, hashIndex) : inner
    const qIndex = beforeHash.indexOf('?')
    const query = qIndex >= 0 ? beforeHash.slice(qIndex) : ''
    const p = qIndex >= 0 ? beforeHash.slice(0, qIndex) : beforeHash
    if (!p || p === '/') return full
    if (/\.[a-zA-Z0-9]+$/.test(p)) return full
    return `](${p}.html${query}${hash})`
  })
}

/** 从一行 md 抽出纯文本，用来拼 description */
function stripToPlain(line: string) {
  return line
    .replace(/<[^>]+>/g, ' ')
    .replace(/!\[[^\]]*\]\([^)]*\)/g, '')
    .replace(/\[([^\]]+)\]\([^)]*\)/g, '$1')
    .replace(/[*_`~#>]/g, '')
    .replace(/&nbsp;/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
}

/** 从正文前几段抽 80～160 字当 meta description；太短就用站点默认文案 */
export function extractDescription(md: string, fallback: string) {
  let body = md.replace(/^---\r?\n[\s\S]*?\r?\n---\r?\n/, '')
  body = body.replace(/```[\s\S]*?```/g, '\n')
  const chunks: string[] = []
  for (const raw of body.split(/\r?\n/)) {
    const line = raw.trim()
    if (!line) continue
    if (/^#{1,6}\s/.test(line)) continue
    if (/^---+/.test(line)) continue
    if (/^</.test(line) && !/^(<green>|<red>|<font|<b |<b>|<span|<strong)/i.test(line)) continue
    const t = stripToPlain(line)
    if (t.length < 12) continue
    chunks.push(t)
    if (chunks.join(' ').length >= 80) break
  }
  let desc = chunks.join(' ')
  if (desc.length < 24) return fallback
  if (desc.length > 160) desc = desc.slice(0, 157).replace(/[，,。.\s]+$/, '') + '…'
  return desc
}

/** 跳转页、占位页、博客（博客有自己的 sitemap）不进文档 sitemap，省百度额度 */
function isSitemapJunk(url: string) {
  if (!url) return true
  if (url.includes('/blog/') || url.includes('/public/') || url.includes('/pro/')) return true
  if (url.includes('/plugin/plugin-dev.html')) return true
  if (url.includes('/use/dao-extend.html')) return true
  if (url.includes('/more/sa-token-donate-old.html')) return true
  if (url.includes('/sso/sso-pro.html')) return true
  if (url.includes('/404.html')) return true
  if (url === SITE_ORIGIN || url === `${SITE_ORIGIN}/` || url.includes(`${SITE_ORIGIN}/index.html`)) return true
  return false
}

/** VitePress sitemap 钩子：丢掉不该提交给百度的 URL */
export function filterSitemapItems(items: { url?: string }[]) {
  return items.filter((item) => !isSitemapJunk(item.url || ''))
}

/** VitePress 2 alpha 的 transformItems 不一定生效，构建后按 loc 再剔一遍。 */
export function stripSitemapJunkXml(xml: string) {
  return xml.replace(/<url>[\s\S]*?<\/url>/g, (block) => (isSitemapJunk(block) ? '' : block))
}

/**
 * 填这一页的 title / description，给浏览器标题栏和后面的 transformHead 用。
 *
 * 优先级：md 头 frontmatter.title/description → README 兜底「框架介绍」
 * → 从正文抽一段 → 再不行用站点默认 SITE_DESCRIPTION。
 */
export function applyPageSeo(pageData: PageData, srcDir: string) {
  if (pageData.isNotFound) {
    pageData.title = '页面不存在'
    pageData.description = '页面不存在'
    return
  }
  const rel = pageData.relativePath || ''
  const fmTitle = pageData.frontmatter.title
  if (typeof fmTitle === 'string' && fmTitle.trim()) {
    pageData.title = fmTitle.trim()
  } else if (/^readme\.md$/i.test(rel) && (pageData.title === 'Sa-Token' || !pageData.title)) {
    pageData.title = '框架介绍'
  }
  const fmDesc = pageData.frontmatter.description
  if (typeof fmDesc === 'string' && fmDesc.trim()) {
    pageData.description = fmDesc.trim()
    return
  }
  const abs = path.join(srcDir, rel)
  if (!rel || !fs.existsSync(abs)) return
  pageData.description = extractDescription(fs.readFileSync(abs, 'utf8'), SITE_DESCRIPTION)
}

/** 相对路径拼成绝对 URL，给 canonical / og:url */
function absUrl(href: string) {
  if (/^https?:\/\//.test(href)) return href
  const p = withHtmlExt(href).split('#')[0]
  return SITE_ORIGIN + (p.startsWith('/') ? p : `/${p}`)
}

/**
 * 拼这一页多出来的 <head> 标签（config 里 transformHead 的返回值）。
 *
 * - canonical / og:url：干净地址，故意丢掉 ?way=，避免百度把同一篇收成两条
 * - 有 redirect、或 /pro/、sso-pro：加 noindex，别浪费收录额度
 * - 正常文档页再加 JSON-LD（TechArticle / 介绍页用 WebPage）
 */
export function buildHead(pageData: PageData, title: string, description: string): HeadConfig[] {
  if (pageData.isNotFound) {
    return [['meta', { name: 'robots', content: 'noindex, nofollow' }]]
  }
  const redirect = pageData.frontmatter.redirect as string | undefined
  const relUrl = pagePath(pageData.relativePath)
  const noindex =
    !!pageData.frontmatter.noindex ||
    !!redirect ||
    relUrl.startsWith('/pro/') ||
    relUrl === '/sso/sso-pro.html'
  const pageUrl = `${SITE_ORIGIN}${pagePath(pageData.relativePath)}`
  const canonical = redirect ? absUrl(redirect) : pageUrl
  const desc = String(description || pageData.description || SITE_DESCRIPTION)
  const isReadme = /^readme\.md$/i.test(pageData.relativePath)
  const ogType = isReadme ? 'website' : 'article'
  const head: HeadConfig[] = []
  if (noindex) head.push(['meta', { name: 'robots', content: 'noindex, follow' }])
  if (redirect) {
    head.push(['meta', { 'http-equiv': 'refresh', content: `0;url=${withHtmlExt(redirect)}` }])
  }
  head.push(
    ['link', { rel: 'canonical', href: canonical }],
    ['meta', { property: 'og:type', content: ogType }],
    ['meta', { property: 'og:title', content: title }],
    ['meta', { property: 'og:description', content: desc }],
    ['meta', { property: 'og:url', content: canonical }],
    ['meta', { name: 'twitter:card', content: 'summary_large_image' }],
    ['meta', { name: 'twitter:title', content: title }],
    ['meta', { name: 'twitter:description', content: desc }],
    ['meta', { name: 'twitter:image', content: OG_IMAGE }]
  )
  const keywords = pageData.frontmatter.keywords
  if (typeof keywords === 'string' && keywords.trim()) {
    head.push(['meta', { name: 'keywords', content: keywords.trim() }])
  }
  if (noindex) return head
  const ld: Record<string, unknown> = {
    '@context': 'https://schema.org',
    '@type': isReadme ? 'WebPage' : 'TechArticle',
    headline: pageData.title || title,
    name: pageData.title || title,
    description: desc,
    url: canonical,
    image: OG_IMAGE,
    inLanguage: 'zh-CN',
    publisher: {
      '@type': 'Organization',
      name: 'Sa-Token',
      logo: { '@type': 'ImageObject', url: OG_IMAGE }
    }
  }
  if (pageData.lastUpdated) ld.dateModified = new Date(pageData.lastUpdated).toISOString()
  head.push(['script', { type: 'application/ld+json' }, JSON.stringify(ld)])
  return head
}
