/**
 * 为文档 md 补 frontmatter description（已有 description 的跳过，不改正文）。
 * 用法：npx tsx scripts/sync-doc-descriptions.mjs
 */
import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { DOC_DESCRIPTIONS } from './doc-descriptions.mjs'

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..')
const docsDir = path.join(root, 'docs')

function yamlQuote(s) {
  return `"${s.replace(/\\/g, '\\\\').replace(/"/g, '\\"')}"`
}

function hasDescription(fm) {
  return /^description\s*:/m.test(fm)
}

function upsertDescription(raw, description) {
  if (!raw.startsWith('---')) {
    return `---\ndescription: ${yamlQuote(description)}\n---\n\n${raw}`
  }
  const end = raw.indexOf('\n---', 4)
  if (end < 0) return raw
  const fm = raw.slice(4, end)
  if (hasDescription(fm)) return raw
  const rest = raw.slice(end + 4)
  const descLine = `description: ${yamlQuote(description)}\n`
  const newFm = fm.trimEnd() ? `${fm.trimEnd()}\n${descLine}` : descLine
  return `---\n${newFm}---${rest}`
}

function walk(dir, list = []) {
  for (const f of fs.readdirSync(dir)) {
    const p = path.join(dir, f)
    if (fs.statSync(p).isDirectory()) walk(p, list)
    else if (f.endsWith('.md')) list.push(p)
  }
  return list
}

const skip = new Set(['include', 'pro'])
const files = walk(docsDir).filter((f) => {
  const rel = path.relative(docsDir, f).replace(/\\/g, '/')
  const top = rel.split('/')[0]
  return !skip.has(top)
})

let updated = 0
let missing = 0
for (const abs of files) {
  const rel = path.relative(docsDir, abs).replace(/\\/g, '/')
  const description = DOC_DESCRIPTIONS[rel]
  if (!description) {
    const raw = fs.readFileSync(abs, 'utf8')
    if (raw.startsWith('---')) {
      const end = raw.indexOf('\n---', 4)
      const fm = end > 0 ? raw.slice(4, end) : ''
      if (!hasDescription(fm)) missing++
    } else missing++
    continue
  }
  const raw = fs.readFileSync(abs, 'utf8')
  const next = upsertDescription(raw, description)
  if (next !== raw) {
    fs.writeFileSync(abs, next)
    updated++
  }
}

console.log(`sync-doc-descriptions: updated ${updated} files`)
if (missing) console.log(`sync-doc-descriptions: ${missing} files still without map entry`)
