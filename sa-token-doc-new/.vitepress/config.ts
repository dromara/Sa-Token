/**
 * VitePress 站点总配置（本工程入口）。
 *
 * 布局约定：
 * - 本文件所在目录 `.vitepress/` 是 VitePress 根
 * - `srcDir: 'docs'` → markdown 在 ../docs/
 * - `public/` 在工程根（和 .vitepress 平级），下面 vite.publicDir 指过去
 * - 构建产物 `dist/`，上传这个目录到 sa-token.com 根，不要上传 md
 *
 * `/` 必须是官网首页，不能是文档壳。构建结束后 restoreHomeAndReadme
 * 会把 VitePress 生成的 index 挪成 readme.html，再把 public/index.html 盖回去。
 */
import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { defineConfig } from 'vitepress'
import { tabsMarkdownPlugin } from 'vitepress-plugin-tabs'
import { serveFileVer } from './file-ver.ts'
import { skipBigFileMetaFromBuild } from './skip-big-file-meta.ts'
import { serveStaticHome } from './static-home.ts'
import { SA_TOKEN_VERSION } from './version.ts'
import docsifyDark from './shiki-docsify-dark.ts'
import { generateBlogCategoryPages } from './blog-category-pages.ts'
import {
  DOC_TITLE_SUFFIX,
  SITE_DESCRIPTION,
  SITE_ORIGIN,
  applyPageSeo,
  buildHead,
  filterSitemapItems,
  rewriteMarkdownDocLinks,
  stripSitemapJunkXml
} from './seo.ts'

const configDir = path.dirname(fileURLToPath(import.meta.url))
/** 工程根下的 public/，给 vite.publicDir 和首页还原用 */
const publicDir = path.resolve(configDir, '../public')

/**
 * 构建结束后处理首页：VitePress 会把文档介绍页写成 dist/index.html，
 * 我们要的是 / = 官网首页、/readme.html = 文档介绍。
 */
function restoreHomeAndReadme(dist: string, publicRoot: string) {
  const publicHome = path.join(publicRoot, 'index.html')
  const distIndex = path.join(dist, 'index.html')
  const distReadme = path.join(dist, 'readme.html')
  if (!fs.existsSync(distIndex) || !fs.existsSync(publicHome)) return
  const indexHtml = fs.readFileSync(distIndex, 'utf8')
  const isVpDoc = indexHtml.includes('st-shell') || indexHtml.includes('vitepress')
  const isHome = indexHtml.includes('doc-btn') && !indexHtml.includes('st-shell')
  // 当前 index 若是文档壳，先另存为 readme.html
  if (isVpDoc && !isHome) {
    fs.writeFileSync(distReadme, indexHtml)
  }
  fs.copyFileSync(publicHome, distIndex)
  // VitePress 会自己写一份文档壳 404，用 public/404.html 盖回去
  const public404 = path.join(publicRoot, '404.html')
  if (fs.existsSync(public404)) {
    fs.copyFileSync(public404, path.join(dist, '404.html'))
  }
}

/** sitemap XML 里没有这条 loc 时补一条 */
function ensureSitemapUrl(xml: string, loc: string, lastmod: string) {
  if (xml.includes(`<loc>${loc}</loc>`)) return xml
  if (!xml.includes('</urlset>')) return xml
  const extra = `  <url>\n    <loc>${loc}</loc>\n    <lastmod>${lastmod}</lastmod>\n  </url>\n`
  return xml.replace('</urlset>', extra + '</urlset>')
}

/**
 * md 正文里会出现 `List<String>` 这种泛型，VitePress 会当成 HTML 标签吃掉。
 * 代码围栏里的 < 不动，围栏外的 `<` 后面若像标签就转成 `&lt;`。
 */
function escapeGenericsOutsideFences(src: string) {
  const re = /(```[\s\S]*?```|~~~[\s\S]*?~~~)/g
  const parts: { code: boolean; text: string }[] = []
  let last = 0
  let m: RegExpExecArray | null
  while ((m = re.exec(src))) {
    if (m.index > last) parts.push({ code: false, text: src.slice(last, m.index) })
    parts.push({ code: true, text: m[0] })
    last = m.index + m[0].length
  }
  if (last < src.length) parts.push({ code: false, text: src.slice(last) })
  return parts
    .map((p) => (p.code ? p.text : p.text.replace(/<(?=[A-Z>])/g, '&lt;')))
    .join('')
}

/** 编译 md 前的预处理：版本占位、去掉 [[toc]]、修 HTML 注释、补 .html 链接 */
function prepareMarkdown(md: { core: { ruler: { after: Function } } }) {
  md.core.ruler.after('normalize', 'sa-token-prepare', (state: { src: string }) => {
    state.src = state.src.replace(/\$\{sa\.top\.version\}/g, SA_TOKEN_VERSION)
    // VitePress 自带大纲，旧站 [[toc]] 占位直接删
    state.src = state.src.replace(/\[\[toc\]\]/gi, '')
    // HTML 注释里若出现 `--`，markdown-it 会解析失败，换成破折号
    state.src = state.src.replace(/<!--[\s\S]*?-->/g, (block: string) => {
      const inner = block.slice(4, -3).replace(/--+/g, '—')
      return `<!--${inner}-->`
    })
    state.src = escapeGenericsOutsideFences(state.src)
    state.src = rewriteMarkdownDocLinks(state.src)
  })
  // ``` java 这种语言名带空格时 Shiki 会挂，trim 一下
  md.core.ruler.after('block', 'sa-token-trim-fence-info', (state: { tokens: { type: string; info?: string }[] }) => {
    for (const t of state.tokens) {
      if (t.type === 'fence' && t.info) t.info = t.info.trim()
    }
  })
}

/** VitePress 外链渲染器会补 rel=noreferrer，这里从输出里剥掉，和旧站 <a> 不写 rel 对齐 */
function stripMarkdownAnchorRel(md: {
  renderer: { rules: { link_open?: (...args: unknown[]) => string } }
}) {
  const prev = md.renderer.rules.link_open
  if (!prev) return
  md.renderer.rules.link_open = (...args) => {
    const html = prev(...args)
    return html.replace(/\srel="[^"]*"/g, '')
  }
}

export default defineConfig({
  lang: 'zh-CN',
  title: 'Sa-Token',
  srcDir: 'docs',              // markdown 在工程根下的 docs/
  outDir: 'dist',              // 构建产物，上传这个目录
  // 浏览器标题：`Sa-Token 登录认证 - Sa-Token 官方文档`
  titleTemplate: `:title - ${DOC_TITLE_SUFFIX}`,
  description: SITE_DESCRIPTION,
  // 不用 VitePress 自带亮暗切换，文档站走旧站 water-change-theme
  appearance: false,
  // 地址带 .html，普通静态服务器不用 rewrite
  cleanUrls: false,
  // md 里有死链也不让构建失败（旧站链接多，迁完慢慢收）
  ignoreDeadLinks: true,
  // 页脚「最后更新」取 git 提交时间
  lastUpdated: true,
  // 这些 md 不当成独立页面：include 片段、备份稿
  srcExclude: ['include/**', '**/*--backup.md'],
  head: [
    [
      'script',
      {},
      // 直开带 hash 的文档时先摘掉 hash，交给 hash-scroll.ts 从页顶滚过去，避免浏览器先钉死再抖
      `if(location.hash&&location.hash!=='#'){window.__ST_PENDING_HASH__=location.hash;if('scrollRestoration'in history)history.scrollRestoration='manual';history.replaceState(history.state||{},'',location.pathname+location.search)}`
    ],
    ['link', { rel: 'icon', href: '/favicon.ico' }],
    ['link', { rel: 'stylesheet', href: '/static/doc.css' }],
    ['meta', { property: 'og:site_name', content: 'Sa-Token' }],
    ['meta', { property: 'og:locale', content: 'zh_CN' }],
    ['meta', { property: 'og:image', content: `${SITE_ORIGIN}/logo.png` }]
  ],
  /**
   * 每编译一篇 md 都会跑。用来改这一页的 title / description。
   *
   * VitePress 默认 title 往往是站点名或文件名，百度看到会很糊。
   * applyPageSeo（seo.ts）会：优先用 md 头里的 title/description；
   * README 没有标题时改成「框架介绍」；否则从正文前几段抽 80～160 字当描述。
   * 这里改完的 pageData，后面 transformHead 还能读到。
   */
  transformPageData(pageData, ctx) {
    applyPageSeo(pageData, ctx.siteConfig.srcDir)
  },
  /**
   * 往这一页 <head> 里再塞标签，返回值会拼进 HTML。
   *
   * 上面 head: [] 是全站共用的（favicon、keywords、og:site_name）。
   * 每页还不一样的放这里：canonical（不带 ?way=）、og:title/url、
   * JSON-LD TechArticle、跳转页/商业版占位页的 noindex。
   * 具体清单在 seo.ts 的 buildHead。
   */
  transformHead({ pageData, title, description }) {
    return buildHead(pageData, title, description)
  },
  markdown: {
    // 代码块左侧显示行号（对齐旧站 code-line-box）
    lineNumbers: true,
    // 围栏暗底 + 旧站 Prism 前景色（字符串用主题绿 #42b983），主题定义在 shiki-docsify-dark.ts
    theme: docsifyDark,
    // md 里 ```gradle / ```cmd / ```url 这些语言名 Shiki 不认，映射到它有的高亮器
    languageAlias: {
      gradle: 'groovy',
      cmd: 'bash',
      url: 'plaintext'
    },
    // 正文大图加 loading="lazy"，首屏少下一点
    image: { lazyLoading: true },
    // ::: tip / warning / info / danger / details 这些提示框标题的中文。
    // 不配的话默认是英文 Tip / Warning。md 里还可以写成 ::: tip Cookie 是什么？ 覆盖这里的默认字。
    container: {
      infoLabel: '信息',
      tipLabel: '提示',
      warningLabel: '注意',
      dangerLabel: '警告',
      detailsLabel: '详情'
    },
    // 代码块右上角「复制」按钮的文案（鼠标悬停 / 点过之后）
    codeCopyButton: {
      tooltipText: '复制代码',
      copiedText: '已复制'
    },
    anchor: {
      // 标题变成 URL hash 时尽量用原文，少改字，旧站 ?id= 锚点才对得上
      slugify(str: string) {
        return str.trim().replace(/[`"'<>]/g, '').replace(/\s+/g, '-')
      }
    },
    // 往 markdown-it 上挂插件：tabs 语法 + 我们自己的预处理
    config(md) {
      md.use(tabsMarkdownPlugin)
      prepareMarkdown(md)
      // VitePress 外链默认 target=_blank + rel=noreferrer，后者会把引流来源洗成直访
      stripMarkdownAnchorRel(md)
    }
  },
  vue: {
    template: {
      compilerOptions: {
        // md 里写的 <green> / <red> 不当成未知 Vue 组件
        isCustomElement: (tag: string) => tag === 'green' || tag === 'red' || tag === 'font'
      },
      transformAssetUrls: {
        // 绝对路径的 src="/xxx" 不要被 Vue 当成模块去打包，否则 public 里的图会 404
        includeAbsolute: false
      }
    }
  },
  themeConfig: {
    // Ctrl+K / 点顶栏搜索：本地倒排索引，不走云端。文案改成中文。
    search: {
      provider: 'local',
      options: {
        translations: {
          button: { buttonText: '搜索...', buttonAriaLabel: '搜索文档' },
          modal: {
            displayDetails: '显示详情',
            resetButtonTitle: '清除',
            backButtonTitle: '关闭',
            noResultsText: '没有找到结果',
            footer: {
              selectText: '选择',
              navigateText: '切换',
              closeText: '关闭'
            }
          }
        }
      }
    }
  },
  /**
   * VitePress 构建时自动扫所有文档页，写出一份 sitemap XML。
   * 默认文件名是 dist/sitemap.xml。我们后面在 buildEnd 里会把它改名成
   * sitemap-docs.xml，根上的 sitemap.xml 改成「目录索引」（指向文档 + 博客两份）。
   */
  sitemap: {
    hostname: SITE_ORIGIN,
    transformItems: filterSitemapItems
  },
  /**
   * 全部页面渲染完、dist 已经写盘之后跑一次。这里做两件构建后收拾：
   *
   * 1) restoreHomeAndReadme
   *    VitePress 会把文档介绍页写成 dist/index.html。线上 / 必须是官网首页，
   *    所以把这份文档壳另存为 readme.html，再用 public/index.html 盖回 index.html。
   *
   * 2) 改 sitemap 结构
   *    把 VitePress 刚写的 sitemap.xml 改名为 sitemap-docs.xml（只含文档页），
   *    再写一个新的 sitemap.xml，类型是 sitemapindex，里面两条：
   *    - https://sa-token.com/sitemap-docs.xml
   *    - https://sa-token.com/blog/sitemap.xml（博客自己那份，不经 VitePress）
   *    百度不收 sitemapindex 当「普通收录」，但可以当站点地图提交。
   */
  buildEnd(siteConfig) {
    const dist = siteConfig.outDir
    restoreHomeAndReadme(dist, siteConfig.publicDir || publicDir)
    generateBlogCategoryPages(dist, siteConfig.publicDir || publicDir)
    // VitePress 默认写出 sitemap.xml（文档页）。我们改成 sitemapindex：
    // 根 sitemap.xml 指向 sitemap-docs.xml + blog/sitemap.xml
    const docsMap = path.join(dist, 'sitemap.xml')
    const docsDest = path.join(dist, 'sitemap-docs.xml')
    const today = new Date().toISOString().slice(0, 10)
    if (fs.existsSync(docsMap)) {
      fs.renameSync(docsMap, docsDest)
    }
    if (fs.existsSync(docsDest)) {
      let xml = fs.readFileSync(docsDest, 'utf8')
      xml = stripSitemapJunkXml(xml)
      // 营销首页 canonical 为 /，与 readme 一并写入文档 sitemap
      xml = ensureSitemapUrl(xml, `${SITE_ORIGIN}/`, today)
      xml = ensureSitemapUrl(xml, `${SITE_ORIGIN}/readme.html`, today)
      fs.writeFileSync(docsDest, xml)
    }
    fs.writeFileSync(
      docsMap,
      `<?xml version="1.0" encoding="UTF-8"?>
<sitemapindex xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
  <sitemap>
    <loc>${SITE_ORIGIN}/sitemap-docs.xml</loc>
    <lastmod>${today}</lastmod>
  </sitemap>
  <sitemap>
    <loc>${SITE_ORIGIN}/blog/sitemap.xml</loc>
    <lastmod>${today}</lastmod>
  </sitemap>
</sitemapindex>
`
    )
  },
  /**
   * 塞给底层 Vite 的配置。VitePress 本身是架在 Vite 上的。
   *
   * publicDir：静态资源目录。默认会去 srcDir 旁边找 public，我们的 public
   *   在工程根（和 .vitepress、docs 平级），所以必须写成绝对路径 publicDir。
   * plugins：
   *   - serveStaticHome：只在 dev 生效，让 / 吐官网首页、/readme.html 走文档
   *   - skipBigFileMetaFromBuild：只在 build 生效，拷 public 时丢掉 big-file/.git
   * optimizeDeps：预构建 tabs，避免 dev 第一次点文档白一下
   * server.host: true：Windows 上同时听 127.0.0.1 和局域网，否则浏览器打不开
   */
  vite: {
    publicDir,
    plugins: [serveFileVer(), serveStaticHome(), skipBigFileMetaFromBuild()],
    optimizeDeps: {
      // 预构建 tabs 插件，避免 dev 第一次打开文档空白一阵
      include: ['vitepress-plugin-tabs']
    },
    server: {
      // Windows 上 Vite 默认只绑 ::1，浏览器走 127.0.0.1 会直接连不上
      host: true,
      port: 5173
    }
  }
})
