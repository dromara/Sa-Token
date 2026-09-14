import fs from 'node:fs'

type SitemapEntry = { loc: string; lastmod?: string }

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
      const lastmod = entry.lastmod ? `\n    <lastmod>${entry.lastmod}</lastmod>` : ''
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
  return buildSitemapXml(mergeSitemapEntries(parts))
}
