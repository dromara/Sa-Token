/**
 * 文档章节锁（对应旧站 static/custom-docsify-plugins/doc-lock-plugin.js）。
 * 默认关，index.ts 里 reservedPlugins.docLock 改 true 才加载 CSS + JS。
 * 锁哪些章节、怎么解锁，逻辑全在那份旧脚本里，这里只负责挂上去。
 */
export function startDocLock() {
  const css = document.createElement('link')
  css.rel = 'stylesheet'
  css.href = '/static/custom-docsify-plugins/doc-lock-plugin.css'
  document.head.appendChild(css)
  const s = document.createElement('script')
  s.src = '/static/custom-docsify-plugins/doc-lock-plugin.js'
  document.body.appendChild(s)
}
