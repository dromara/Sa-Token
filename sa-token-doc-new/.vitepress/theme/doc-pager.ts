/**
 * 按 sidebar.ts 顺序生成文档「上一篇 / 下一篇」。
 * 仅收录站内链接，外链（商业版等）跳过。
 */
import type { DefaultTheme } from 'vitepress'
import { sidebar } from '../sidebar.ts'
import { isExternalLink, normalizePath } from './paths.ts'

export type DocPagerItem = { text: string; link: string }

export function flattenSidebar(groups: DefaultTheme.Sidebar): DocPagerItem[] {
  const items: DocPagerItem[] = []
  if (!Array.isArray(groups)) return items
  for (const group of groups) {
    if (!('items' in group) || !group.items) continue
    for (const item of group.items) {
      if (!item.link || isExternalLink(item.link)) continue
      items.push({ text: item.text, link: item.link })
    }
  }
  return items
}

const DOC_PAGES = flattenSidebar(sidebar)

export function getDocPager(currentPath: string) {
  const cur = normalizePath(currentPath)
  const i = DOC_PAGES.findIndex((item) => normalizePath(item.link) === cur)
  if (i < 0) return { prev: null, next: null }
  return {
    prev: i > 0 ? DOC_PAGES[i - 1] : null,
    next: i < DOC_PAGES.length - 1 ? DOC_PAGES[i + 1] : null
  }
}
