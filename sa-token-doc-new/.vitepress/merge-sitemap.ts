import fs from 'node:fs'

type SitemapEntry = { loc: string; lastmod?: string }

/** lastmod 统一成 YYYY-MM-DD。这是百度 / 搜狗 / 360 / Google / Bing 文档都认的交集格式。 */
export function toSitemapLastmodDate(lastmod: string) {
  const m = lastmod.trim().match(/^(\d{4}-\d{2}-\d{2})/)
  return m ? m[1] : lastmod.trim()
}

/** 从 urlset XML 抽出 loc / lastmod */
export function parseSitemapEntries(xml: string): SitemapEntry[] {
  const entries: SitemapEntry[] = []
  for (const block of xml.match(/<url>[\s\S]*?<\/url>/g) || []) {
    const loc = block.match(/<loc>([^<]+)<\/loc>/)?.[1]?.trim()
    if (!loc) continue
    const lastmod = block.match(/<lastmod>([^<]+)<\/lastmod>/)?.[1]?.trim()
    entries.push(lastmod ? { loc, lastmod } : { loc })
  }
  return entries
}

/** 官网入口页，合并后排到 sitemap 最前；其余条目保持原顺序 */
const SITEMAP_FRONT_PATHS = [
  '/',
  '/index.html',
  '/cases.html',
  '/readme.html',
  '/doc.html',
  '/blog/index.html'
]

function sitemapPathname(loc: string) {
  try {
    return new URL(loc).pathname
  } catch {
    return loc
  }
}

/** 把入口页抽到最前，找不到的 loc 跳过 */
export function prioritizeSitemapEntries(entries: SitemapEntry[]) {
  const taken = new Set<string>()
  const front: SitemapEntry[] = []
  for (const p of SITEMAP_FRONT_PATHS) {
    const hit = entries.find((e) => sitemapPathname(e.loc) === p)
    if (!hit || taken.has(hit.loc)) continue
    front.push(hit)
    taken.add(hit.loc)
  }
  return [...front, ...entries.filter((e) => !taken.has(e.loc))]
}

/** 多份 urlset 合并为一份，按 loc 去重（先出现的保留） */
export function mergeSitemapEntries(parts: SitemapEntry[][]): SitemapEntry[] {
  const seen = new Set<string>()
  const merged: SitemapEntry[] = []
  for (const part of parts) {
    for (const entry of part) {
      if (seen.has(entry.loc)) continue
      seen.add(entry.loc)
      merged.push(entry)
    }
  }
  return merged
}

export function buildSitemapXml(entries: SitemapEntry[]) {
  const body = entries
    .map((entry) => {
      const lastmod = entry.lastmod
        ? `\n    <lastmod>${toSitemapLastmodDate(entry.lastmod)}</lastmod>`
        : ''
      return `  <url>\n    <loc>${entry.loc}</loc>${lastmod}\n  </url>`
    })
    .join('\n')
  return (
    '<?xml version="1.0" encoding="UTF-8"?>\n' +
    '<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n' +
    `${body}\n` +
    '</urlset>\n'
  )
}

/** 读多个 sitemap 文件，写出合并后的 urlset XML */
export function mergeSitemapFiles(paths: string[]) {
  const parts: SitemapEntry[][] = []
  for (const file of paths) {
    if (!fs.existsSync(file)) continue
    parts.push(parseSitemapEntries(fs.readFileSync(file, 'utf8')))
  }
  return buildSitemapXml(prioritizeSitemapEntries(mergeSitemapEntries(parts)))
}
