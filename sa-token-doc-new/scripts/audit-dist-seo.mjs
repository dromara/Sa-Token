/**
 * 构建后 SEO 抽查：canonical、description、keywords、JSON-LD、noindex、title
 * 用法：npx tsx scripts/audit-dist-seo.mjs
 */
import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const dist = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../dist')

function walkHtml(dir, list = []) {
  for (const f of fs.readdirSync(dir)) {
    const p = path.join(dir, f)
    if (fs.statSync(p).isDirectory()) walkHtml(p, list)
    else if (f.endsWith('.html')) list.push(p)
  }
  return list
}

function extractMeta(html, name, attr = 'name') {
  const re = new RegExp(`<meta[^>]+${attr}=["']${name}["'][^>]*>`, 'i')
  const m = html.match(re)
  if (!m) return null
  const content = m[0].match(/content=["']([^"']*)["']/i)
  return content ? content[1] : null
}

function extractLink(html, rel) {
  const re = new RegExp(`<link[^>]+rel=["']${rel}["'][^>]*>`, 'i')
  const m = html.match(re)
  if (!m) return null
  const href = m[0].match(/href=["']([^"']*)["']/i)
  return href ? href[1] : null
}

function extractTitle(html) {
  const m = html.match(/<title[^>]*>([^<]*)<\/title>/i)
  return m ? m[1].trim() : null
}

function hasJsonLd(html) {
  return /application\/ld\+json/i.test(html)
}

function isNoindex(html) {
  const robots = extractMeta(html, 'robots')
  return robots && /noindex/i.test(robots)
}

const SITE = 'https://sa-token.com'
const htmlFiles = walkHtml(dist)
const issues = []
const stats = {
  total: htmlFiles.length,
  noCanonical: 0,
  badCanonical: 0,
  noDescription: 0,
  shortDesc: 0,
  longDesc: 0,
  noKeywords: 0,
  noJsonLd: 0,
  noTitle: 0,
  noindexPages: 0,
  duplicateTitleSuffix: 0
}

for (const abs of htmlFiles) {
  const rel = path.relative(dist, abs).replace(/\\/g, '/')
  const html = fs.readFileSync(abs, 'utf8')
  const title = extractTitle(html)
  const canonical = extractLink(html, 'canonical')
  const desc = extractMeta(html, 'description')
  const keywords = extractMeta(html, 'keywords')
  const ogTitle = extractMeta(html, 'og:title', 'property')
  const ogDesc = extractMeta(html, 'og:description', 'property')

  if (!title) {
    stats.noTitle++
    issues.push({ rel, type: 'no-title' })
  }
  if (!canonical) {
    stats.noCanonical++
    issues.push({ rel, type: 'no-canonical' })
  } else if (!canonical.startsWith(SITE)) {
    stats.badCanonical++
    issues.push({ rel, type: 'bad-canonical', value: canonical })
  }
  if (!isNoindex(html)) {
    if (!desc) {
      stats.noDescription++
      issues.push({ rel, type: 'no-description' })
    } else if (desc.length < 40) {
      stats.shortDesc++
      issues.push({ rel, type: 'short-description', value: desc })
    } else if (desc.length > 200) {
      stats.longDesc++
      issues.push({ rel, type: 'long-description', len: desc.length })
    }
    if (!keywords) {
      stats.noKeywords++
      issues.push({ rel, type: 'no-keywords' })
    }
    if (!hasJsonLd(html)) {
      stats.noJsonLd++
      issues.push({ rel, type: 'no-jsonld' })
    }
  } else {
    stats.noindexPages++
  }
  if (title && (title.match(/Sa-Token/g) || []).length > 2) {
    stats.duplicateTitleSuffix++
    issues.push({ rel, type: 'title-sa-token-repeat', value: title })
  }
  if (ogTitle && title && !ogTitle.includes(ogTitle.split(' - ')[0]?.slice(0, 20))) {
    // skip loose check
  }
  if (!ogDesc && desc && !isNoindex(html)) {
    issues.push({ rel, type: 'no-og-description' })
  }
}

// sitemap check
const sitemapPath = path.join(dist, 'sitemap-docs.xml')
let sitemapCount = 0
let sitemapJunk = []
if (fs.existsSync(sitemapPath)) {
  const xml = fs.readFileSync(sitemapPath, 'utf8')
  const locs = [...xml.matchAll(/<loc>([^<]+)<\/loc>/g)].map((m) => m[1])
  sitemapCount = locs.length
  const junkPatterns = ['/blog/', '/pro/', 'sso-pro', 'plugin-dev', 'dao-extend', 'donate-old', '/404.html', '/index.html']
  sitemapJunk = locs.filter((u) => junkPatterns.some((p) => u.includes(p)))
}

// sample key pages
const samples = [
  'readme.html',
  'use/login-auth.html',
  'fun/log.html',
  'fun/update-version-flow.html',
  'blog/index.html',
  'index.html'
]

console.log('=== dist SEO audit ===')
console.log(`HTML files: ${stats.total}`)
console.log(`noindex pages: ${stats.noindexPages}`)
console.log(`missing canonical: ${stats.noCanonical}`)
console.log(`bad canonical: ${stats.badCanonical}`)
console.log(`missing description (indexable): ${stats.noDescription}`)
console.log(`short description (<40): ${stats.shortDesc}`)
console.log(`long description (>200): ${stats.longDesc}`)
console.log(`missing keywords: ${stats.noKeywords}`)
console.log(`missing JSON-LD: ${stats.noJsonLd}`)
console.log(`sitemap-docs URLs: ${sitemapCount}`)
console.log(`sitemap junk URLs: ${sitemapJunk.length}`)
if (sitemapJunk.length) console.log('  junk:', sitemapJunk.slice(0, 10))

console.log('\n=== sample pages ===')
for (const s of samples) {
  const p = path.join(dist, s)
  if (!fs.existsSync(p)) {
    console.log(`${s}: MISSING`)
    continue
  }
  const html = fs.readFileSync(p, 'utf8')
  console.log(`\n${s}`)
  console.log(`  title: ${extractTitle(html)}`)
  console.log(`  canonical: ${extractLink(html, 'canonical')}`)
  console.log(`  desc(${extractMeta(html, 'description')?.length || 0}): ${(extractMeta(html, 'description') || '').slice(0, 80)}…`)
  console.log(`  robots: ${extractMeta(html, 'robots') || 'index'}`)
  console.log(`  jsonld: ${hasJsonLd(html)}`)
}

const grouped = {}
for (const i of issues) {
  grouped[i.type] = (grouped[i.type] || 0) + 1
}
console.log('\n=== issue counts by type ===')
for (const [k, v] of Object.entries(grouped).sort((a, b) => b[1] - a[1])) {
  console.log(`  ${k}: ${v}`)
}

const showTypes = ['no-description', 'no-jsonld', 'no-canonical', 'short-description', 'no-keywords']
for (const t of showTypes) {
  const list = issues.filter((i) => i.type === t).slice(0, 15)
  if (list.length) {
    console.log(`\n--- ${t} (first ${list.length}) ---`)
    for (const i of list) console.log(`  ${i.rel}${i.value ? ' | ' + i.value : ''}`)
  }
}
