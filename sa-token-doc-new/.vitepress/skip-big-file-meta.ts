/**
 * 构建时拷贝 public/，但跳过 public/big-file 里的仓库元数据。
 *
 * big-file 是嵌进去的独立 git（图片仓）。.git 拷进 dist 又大又没意义。
 * Vite 默认 copyPublicDir 太粗，关了之后我们自己 cp，并在结束后再清一遍。
 */
import fs from 'node:fs/promises'
import path from 'node:path'
import type { Plugin } from 'vite'

const SKIP = new Set(['.git', '.gitignore', 'README.md'])

/** 是否是 big-file 根下那几个不该上传的文件 */
function skipBigFileMeta(publicDir: string, src: string) {
  const rel = path.relative(publicDir, src)
  if (rel.startsWith('..') || path.isAbsolute(rel)) return false
  const parts = rel.split(path.sep)
  return parts[0] === 'big-file' && SKIP.has(parts[1])
}

async function stripFromDist(outDir: string) {
  const base = path.join(outDir, 'big-file')
  await Promise.all([
    fs.rm(path.join(base, '.git'), { recursive: true, force: true }),
    fs.rm(path.join(base, '.gitignore'), { force: true }),
    fs.rm(path.join(base, 'README.md'), { force: true })
  ])
}

export function skipBigFileMetaFromBuild(): Plugin {
  let publicDir = ''
  let outDir = ''
  return {
    name: 'skip-big-file-meta-from-build',
    apply: 'build',
    config() {
      return { build: { copyPublicDir: false } }
    },
    configResolved(config) {
      publicDir = config.publicDir
      outDir = config.build.outDir
    },
    async writeBundle() {
      if (!publicDir) return
      await fs.cp(publicDir, outDir, {
        recursive: true,
        filter: (src) => !skipBigFileMeta(publicDir, src)
      })
    },
    async closeBundle() {
      if (outDir) await stripFromDist(outDir)
    }
  }
}
