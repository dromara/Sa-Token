/**
 * 公众号关注解锁章节（对应旧站 doc-lock-by-gzh-plugin.js）。
 * 默认关，index.ts 里 reservedPlugins.docLockByGzh 改 true 才加载。
 * CSS 和普通章节锁共用 doc-lock-plugin.css。
 */
export function startDocLockByGzh() {
  const css = document.createElement('link')
  css.rel = 'stylesheet'
  css.href = '/static/custom-docsify-plugins/doc-lock-plugin.css'
  document.head.appendChild(css)
  const s = document.createElement('script')
  s.src = '/static/custom-docsify-plugins/doc-lock-by-gzh-plugin.js'
  document.body.appendChild(s)
}
