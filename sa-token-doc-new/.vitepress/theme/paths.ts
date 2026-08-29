/**
 * 文档路径小工具：归一、比当前页、判断是不是文档区、是不是外链。
 * SiteHeader / SiteSidebar 比高亮、theme/index.ts 拦静态页时都用这里。
 */
/** 路径归一：去掉 query/hash/.html、末尾斜杠，方便比是不是同一页 */
export function normalizePath(path: string) {
  let p = path.split('?')[0].split('#')[0]
  if (!p.startsWith('/')) p = `/${p}`
  p = p.replace(/\.html$/, '')
  if (p.length > 1 && p.endsWith('/')) p = p.slice(0, -1)
  return p || '/'
}

export function isActivePath(current: string, link: string) {
  return normalizePath(current) === normalizePath(link)
}

/** 文档站编译页（不含独立首页 / 博客） */
export function isDocsSection(path: string) {
  const p = normalizePath(path)
  return p !== '/' && p !== '/index' && !p.startsWith('/blog')
}

export function isExternalLink(link: string) {
  return /^(https?:)?\/\//.test(link)
}
