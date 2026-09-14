/**
 * 开发服务器插件：让 `npm run docs:dev` 的 URL 和线上一致。
 *
 * VitePress 默认会把 / 当成文档首页。我们要：
 * - `/`、`/index.html` → 直接吐 public/index.html（官网首页）
 * - `/readme.html` 直接交给 VitePress（文档介绍页是 srcDir 根 readme.md）
 * - `/README.html` → 改写成 `/readme.html`（旧大写入口）
 *
 * 只在 dev 生效。正式构建靠 config.ts 的 restoreHomeAndReadme。
 */
import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import type { Plugin } from 'vite'

const homeHtml = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../public/index.html')

function pathnameOf(url = '') {
  return url.split('?')[0]
}

function isHomeRequest(url = '') {
  const pathname = pathnameOf(url)
  return pathname === '/' || pathname === '/index.html'
}

function isUpperReadmeRequest(url = '') {
  const pathname = pathnameOf(url)
  return pathname === '/README.html' || pathname === '/README'
}

export function serveStaticHome(): Plugin {
  return {
    name: 'serve-static-home',
    configureServer(server) {
      // unshift：插到最前面，先于 VitePress 自己的 HTML 中间件
      server.middlewares.stack.unshift({
        route: '',
        handle(req, res, next) {
          if (req.method !== 'GET' && req.method !== 'HEAD') {
            next()
            return
          }
          const url = req.url ?? ''
          if (isUpperReadmeRequest(url)) {
            const q = url.includes('?') ? url.slice(url.indexOf('?')) : ''
            req.url = `/readme.html${q}`
            next()
            return
          }
          if (!isHomeRequest(url)) {
            next()
            return
          }
          res.statusCode = 200
          res.setHeader('Content-Type', 'text/html; charset=utf-8')
          res.end(fs.readFileSync(homeHtml))
        }
      })
    }
  }
}
