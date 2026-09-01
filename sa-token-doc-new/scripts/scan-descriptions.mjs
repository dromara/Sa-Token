import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { sidebar } from '../.vitepress/sidebar.ts'

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..')
const docsDir = path.join(root, 'docs')

function stripToPlain(line) {
  return line
    .replace(/<[^>]+>/g, ' ')
    .replace(/!\[[^\]]*\]\([^)]*\)/g, '')
    .replace(/\[([^\]]+)\]\([^)]*\)/g, '$1')
    .replace(/[*_`~#>]/g, '')
    .replace(/&nbsp;/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
}

function extractDescription(md, fallback) {
  let body = md.replace(/^---\r?\n[\s\S]*?\r?\n---\r?\n/, '')
  body = body.replace(/```[\s\S]*?```/g, '\n')
  const chunks = []
  for (const raw of body.split(/\r?\n/)) {
    const line = raw.trim()
    if (!line) continue
    if (/^:{1,3}\s?/.test(line)) continue
    if (/^==+\s/.test(line)) continue
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

function parseFm(md) {
  if (!md.startsWith('---')) return {}
  const end = md.indexOf('\n---', 4)
  if (end < 0) return {}
  const fm = md.slice(4, end)
  const titleM = fm.match(/^title:\s*(.+)$/m)
  const descM = fm.match(/^description:\s*(.+)$/m)
  const unquote = (s) => s.replace(/^"(.*)"$/, '$1').replace(/^'(.*)'$/, '$1').trim()
  return {
    title: titleM ? unquote(titleM[1]) : undefined,
    desc: descM ? unquote(descM[1]) : undefined
  }
}

function walk(dir, list = []) {
  for (const f of fs.readdirSync(dir)) {
    const p = path.join(dir, f)
    if (fs.statSync(p).isDirectory()) walk(p, list)
    else if (f.endsWith('.md')) list.push(p)
  }
  return list
}

const appendixLinks = new Set()
for (const g of sidebar) {
  if (g.text === '附录') {
    for (const item of g.items || []) {
      if (item.link) appendixLinks.add(item.link.split('#')[0])
    }
  }
}

const fallback = 'FALLBACK'
const files = walk(docsDir).filter((f) => !f.includes('include') && !f.includes(`${path.sep}pro${path.sep}`))
const noDesc = []

for (const f of files) {
  const raw = fs.readFileSync(f, 'utf8')
  const fm = parseFm(raw)
  if (fm.desc) continue
  const rel = path.relative(docsDir, f).replace(/\\/g, '/')
  const href = '/' + rel.replace(/\.md$/, '.html').replace(/^readme\.md$/, 'readme.html')
  const auto = extractDescription(raw, fallback)
  noDesc.push({
    rel,
    href,
    title: fm.title,
    auto,
    isFallback: auto === fallback,
    isAppendix: appendixLinks.has(href)
  })
}

console.log(`Total docs: ${files.length}`)
console.log(`Missing description: ${noDesc.length}`)
console.log(`Appendix missing: ${noDesc.filter((x) => x.isAppendix).length}`)
console.log(`Fallback auto: ${noDesc.filter((x) => x.isFallback).length}`)

for (const x of noDesc.sort((a, b) => a.rel.localeCompare(b.rel))) {
  console.log(`\n[${x.isAppendix ? '附录' : '其他'}] ${x.rel}`)
  console.log(`  title: ${x.title || '(none)'}`)
  console.log(`  auto: ${x.auto}`)
}
