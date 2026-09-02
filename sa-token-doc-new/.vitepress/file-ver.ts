/**
 * SEO 域名验证文件集中在 public/file-ver/，构建与 dev 都映射到站点根路径。
 */
import fs from 'node:fs'
import type { Dirent } from 'node:fs'
import fsP from 'node:fs/promises'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import type { Plugin } from 'vite'

export const FILE_VER = 'file-ver'

/** 仅仓库内说明，不部署到站点根目录 */
const SKIP_DEPLOY = new Set(['readme.txt'])

function shouldDeployFileVer(name: string) {
  return !SKIP_DEPLOY.has(name.toLowerCase())
}

const configDir = path.dirname(fileURLToPath(import.meta.url))
export const defaultFileVerDir = path.resolve(configDir, '../public', FILE_VER)

const MIME: Record<string, string> = {
  '.html': 'text/html; charset=utf-8',
  '.txt': 'text/plain; charset=utf-8',
  '.xml': 'application/xml; charset=utf-8'
}

/** public/file-ver 下的路径不参与整目录拷贝 */
export function isUnderFileVer(publicDir: string, src: string) {
  const rel = path.relative(publicDir, src)
  if (rel.startsWith('..') || path.isAbsolute(rel)) return false
  return rel.split(path.sep)[0] === FILE_VER
}

/** 构建：把 public/file-ver/* 写到 dist 根目录 */
export async function copyFileVerToRoot(publicDir: string, outDir: string) {
  const verDir = path.join(publicDir, FILE_VER)
  let entries: Dirent[]
  try {
    entries = await fsP.readdir(verDir, { withFileTypes: true })
  } catch {
    return
  }
  for (const ent of entries) {
    if (!ent.isFile() || !shouldDeployFileVer(ent.name)) continue
    await fsP.copyFile(path.join(verDir, ent.name), path.join(outDir, ent.name))
  }
}

/** 开发：/google....html 等根路径请求映射到 public/file-ver/ */
export function serveFileVer(fileVerDir = defaultFileVerDir): Plugin {
  const root = path.resolve(fileVerDir)
  return {
    name: 'serve-file-ver',
    configureServer(server) {
      server.middlewares.stack.unshift({
        route: '',
        handle(req, res, next) {
          if (req.method !== 'GET' && req.method !== 'HEAD') {
            next()
            return
          }
          const pathname = decodeURIComponent((req.url ?? '').split('?')[0])
          const name = pathname.slice(1)
          if (!name || name.includes('/') || !shouldDeployFileVer(name)) {
            next()
            return
          }
          const file = path.resolve(root, name)
          if (!file.startsWith(root + path.sep) && file !== root) {
            next()
            return
          }
          if (!fs.existsSync(file) || !fs.statSync(file).isFile()) {
            next()
            return
          }
          res.statusCode = 200
          res.setHeader('Content-Type', MIME[path.extname(name).toLowerCase()] ?? 'application/octet-stream')
          if (req.method === 'HEAD') {
            res.end()
            return
          }
          res.end(fs.readFileSync(file))
        }
      })
    }
  }
}
