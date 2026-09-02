/**
 * 构建部署后，把 sitemap 里的 URL 批量通知 Bing（IndexNow）。
 *
 * 用法：
 *   npm run docs:build
 *   （上传 dist 到服务器）
 *   npm run bing:indexnow
 *
 * 前提：public/file-ver/ 里的 key 文件已部署到站点根目录，例如
 *   https://sa-token.com/98c12512b8e6428482d8cb1c69a00eb8.txt
 */
import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const HOST = 'sa-token.com'
const KEY = '98c12512b8e6428482d8cb1c69a00eb8'
const KEY_LOCATION = `https://${HOST}/${KEY}.txt`
const API = 'https://api.indexnow.org/indexnow'
const BATCH_SIZE = 500

const root = path.join(path.dirname(fileURLToPath(import.meta.url)), '..')
const distDir = (() => {
  const i = process.argv.indexOf('--dist')
  return i >= 0 ? process.argv[i + 1] : path.join(root, 'dist')
})()

function extractLocs(xml) {
  return [...xml.matchAll(/<loc>([^<]+)<\/loc>/g)].map((m) => m[1].trim())
}

function collectUrls() {
  const urls = new Set([`https://${HOST}/`])
  const files = [
    path.join(distDir, 'sitemap-docs.xml'),
    path.join(distDir, 'blog', 'sitemap.xml'),
  ]
  for (const file of files) {
    if (!fs.existsSync(file)) {
      console.warn(`跳过（不存在）: ${file}`)
      continue
    }
    for (const url of extractLocs(fs.readFileSync(file, 'utf8'))) {
      urls.add(url)
    }
  }
  return [...urls]
}

async function submitBatch(urlList) {
  const res = await fetch(API, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json; charset=utf-8' },
    body: JSON.stringify({
      host: HOST,
      key: KEY,
      keyLocation: KEY_LOCATION,
      urlList,
    }),
  })
  return { status: res.status, statusText: res.statusText, body: await res.text() }
}

async function main() {
  const urls = collectUrls()
  if (!urls.length) {
    console.error('未找到 URL，请先执行 npm run docs:build')
    process.exit(1)
  }

  console.log(`IndexNow：准备提交 ${urls.length} 个 URL`)
  console.log(`Key 文件须已上线: ${KEY_LOCATION}`)

  const batches = Math.ceil(urls.length / BATCH_SIZE)
  for (let i = 0; i < urls.length; i += BATCH_SIZE) {
    const batch = urls.slice(i, i + BATCH_SIZE)
    const { status, statusText, body } = await submitBatch(batch)
    const n = Math.floor(i / BATCH_SIZE) + 1
    console.log(`批次 ${n}/${batches}: HTTP ${status} ${statusText}${body ? ` — ${body}` : ''}`)

    if (status === 403) {
      console.error('\n403：Key 文件还没部署到线上，或内容不对。先上传 dist，再重试。')
      process.exit(1)
    }
    if (status >= 400) {
      process.exit(1)
    }
  }

  console.log('IndexNow 提交完成')
}

main()
