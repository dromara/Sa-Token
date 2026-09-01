/**
 * 按 sidebar.ts 为文档 md 补 frontmatter 的 title / keywords（已有 title 的跳过，不改正文）。
 * 用法：npx tsx scripts/sync-doc-frontmatter-seo.mjs
 */
import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { sidebar } from '../.vitepress/sidebar.ts'

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..')
const docsDir = path.join(root, 'docs')

function linkToMd(link) {
  const href = link.split('#')[0]
  if (!href.endsWith('.html')) return null
  let md = href.replace(/^\//, '').replace(/\.html$/, '.md')
  if (md === 'readme.md') return 'readme.md'
  return md
}

function yamlQuote(s) {
  return `"${s.replace(/\\/g, '\\\\').replace(/"/g, '\\"')}"`
}

function seoTitle(text, section, mdRel) {
  const t = text.trim()
  if (/sa-?token|satoken/i.test(t)) return t
  if (mdRel.startsWith('use/')) return `Spring Boot ${t}`
  if (mdRel.startsWith('start/') && !/spring/i.test(t)) return `Spring Boot ${t.replace(/^在\s*/, '')}`
  if (mdRel.includes('jwt')) return 'Spring Boot 整合 Sa-Token JWT'
  return `Sa-Token ${t}`
}

function buildKeywords(title, section, mdRel) {
  const seen = new Set(['Sa-Token', 'sa-token', 'satoken', 'Sa-Token文档'])
  const add = (s) => {
    const x = s?.trim()
    if (x) seen.add(x)
  }
  add(title.replace(/^Sa-Token\s+/i, '').replace(/^Spring Boot\s+/i, ''))
  if (section && section !== '开始') add(section)
  const rel = mdRel.replace(/\.md$/, '').toLowerCase()
  if (rel.startsWith('sso/')) {
    add('SSO')
    add('单点登录')
  }
  if (rel.startsWith('oauth2/')) add('OAuth2')
  if (rel.startsWith('micro/')) add('微服务鉴权')
  if (rel.startsWith('start/')) add('Spring Boot')
  if (rel.startsWith('use/')) add('Java权限认证')
  if (rel.includes('jwt')) add('JWT')
  if (rel.includes('redis')) add('Redis')
  return [...seen].slice(0, 14).join(',')
}

function upsertFrontmatter(raw, title, keywords) {
  if (!raw.startsWith('---')) {
    return `---\ntitle: ${yamlQuote(title)}\nkeywords: ${yamlQuote(keywords)}\n---\n\n${raw}`
  }
  const end = raw.indexOf('\n---', 4)
  if (end < 0) return raw
  const fm = raw.slice(4, end)
  if (/^title\s*:/m.test(fm)) return raw
  const rest = raw.slice(end + 4)
  const block = `title: ${yamlQuote(title)}\nkeywords: ${yamlQuote(keywords)}\n${fm.trimEnd()}\n`
  return `---\n${block}---${rest}`
}

const ORPHAN_PAGES = {
  'sso/sso-diff-key.md': { text: '不同 SSO Client 配置不同秘钥', section: '单点登录' },
  'start/new-version.md': { text: 'Sa-Token 最新版本', section: '开始' },
  'start/maven-pull.md': { text: 'Maven 依赖拉取失败排查', section: '开始' },
  'fun/tech-stack.md': { text: '源码技术栈', section: '附录' },
  'fun/refer-info.md': { text: '开发参考资料', section: '附录' },
  'more/recommended.md': { text: '推荐阅读', section: '其它' },
  'more/tj-gzh-hz.md': { text: '公众号合作', section: '其它' }
}

function buildMap(groups) {
  const map = { ...ORPHAN_PAGES }
  for (const group of groups) {
    for (const item of group.items || []) {
      if (!item.link || item.link.startsWith('http')) continue
      const md = linkToMd(item.link)
      if (!md) continue
      map[md] = { text: item.text, section: group.text }
    }
  }
  return map
}

const map = buildMap(sidebar)
let updated = 0
for (const [mdRel, info] of Object.entries(map)) {
  const abs = path.join(docsDir, mdRel)
  if (!fs.existsSync(abs)) continue
  const raw = fs.readFileSync(abs, 'utf8')
  if (raw.startsWith('---')) {
    const end = raw.indexOf('\n---', 4)
    const fm = end > 0 ? raw.slice(4, end) : ''
    if (/^title\s*:/m.test(fm)) continue
  }
  const title = seoTitle(info.text, info.section, mdRel)
  const keywords = buildKeywords(title, info.section, mdRel)
  fs.writeFileSync(abs, upsertFrontmatter(raw, title, keywords))
  updated++
}
console.log(`sync-doc-frontmatter-seo: updated ${updated} files`)
