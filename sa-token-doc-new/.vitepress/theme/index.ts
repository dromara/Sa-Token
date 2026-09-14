/**
 * 自定义文档主题入口（不用 VitePress 默认壳，Layout 是我们自己的）。
 *
 * 这里主要拦路由：首页 / 博客是独立 HTML，不能走 SPA，必须整页跳。
 * 同页点目录锚点则自己滚，避免 VitePress 再切一次路由。
 */
import type { Theme } from 'vitepress'
import { inBrowser } from 'vitepress'
import { enhanceAppWithTabs } from 'vitepress-plugin-tabs/client'
import Layout from './Layout.vue'
import { bootReserved } from './reserved/index.ts'
import { bootHashScroll, scrollToHashId } from './hash-scroll.ts'
import { onDocPageChange } from './legacy.ts'
import 'vitepress/dist/client/theme-default/styles/vars.css'
import 'vitepress/dist/client/theme-default/styles/base.css'
import 'vitepress/dist/client/theme-default/styles/icons.css'
import 'vitepress/dist/client/theme-default/styles/utils.css'
import 'vitepress/dist/client/theme-default/styles/components/custom-block.css'
import 'vitepress/dist/client/theme-default/styles/components/vp-code.css'
import 'vitepress/dist/client/theme-default/styles/components/vp-code-group.css'
import 'vitepress/dist/client/theme-default/styles/components/vp-doc.css'
import './custom.css'

/** 这些路径是 public 里的静态页，不能交给 VitePress 客户端路由 */
function isStaticHtml(to: string) {
  const path = to.startsWith('http') ? new URL(to).pathname : to.split('?')[0].split('#')[0]
  return path === '/' || path === '/index.html' || path === '/blog' || path.startsWith('/blog/')
}

/** /use/foo.html 和 /use/foo 当成同一页 */
function pathsMatch(a: string, b: string) {
  const key = (p: string) => p.replace(/\/index\.html$/, '/').replace(/\.html$/, '')
  return a === b || key(a) === key(b)
}

export default {
  Layout,
  enhanceApp({ app, router }) {
    enhanceAppWithTabs(app)
    if (!inBrowser) return

    const go = router.go.bind(router)
    router.go = async (to, options) => {
      // 点「首页」「博客」：整页跳，否则会套上文档壳
      if (to && isStaticHtml(String(to))) {
        window.location.assign(to)
        return
      }
      try {
        const url = new URL(String(to), location.href)
        const cur = new URL(location.href)
        const initialLoad = !!(options as { initialLoad?: boolean } | undefined)?.initialLoad
        // 同一文档页只换 hash：改地址栏并滚过去，不要重新加载整页
        if (
          !initialLoad &&
          url.origin === cur.origin &&
          pathsMatch(url.pathname, cur.pathname) &&
          url.hash &&
          url.hash !== '#'
        ) {
          if (url.hash !== cur.hash) {
            history.replaceState({ scrollPosition: window.scrollY }, '')
            history.pushState({}, '', url.pathname + url.search + url.hash)
            router.route.hash = decodeURIComponent(url.hash)
          }
          scrollToHashId()
          return
        }
      } catch {
        /* fall through */
      }
      return go(to, options)
    }

    // 捕获阶段拦 <a href="/blog/...">，VitePress 默认会 preventDefault 走 SPA
    window.addEventListener(
      'click',
      (e) => {
        if (e.button !== 0) return
        if (e.metaKey || e.ctrlKey || e.shiftKey || e.altKey) return
        const el = (e.target as HTMLElement | null)?.closest?.('a')
        if (!el || el.target === '_blank') return
        const href = el.getAttribute('href')
        if (!href || !isStaticHtml(href)) return
        e.preventDefault()
        e.stopImmediatePropagation()
        window.location.assign(el.href)
      },
      true
    )

    router.onAfterRouteChange = () => {
      try {
        onDocPageChange()
      } catch (err) {
        console.warn('[sa-token-doc] onDocPageChange', err)
      }
      try {
        scrollToHashId({ fromTop: true })
      } catch (err) {
        console.warn('[sa-token-doc] scrollToHashId', err)
      }
    }

    bootHashScroll()
    bootReserved()
  }
} satisfies Theme
