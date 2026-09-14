/**
 * 顶部阅读进度条（对应旧站 static/docsify-plugins/progress.update.js）。
 * 默认关，index.ts 里 reservedPlugins.progress 改 true 才加载。
 * 不引旧 JS，直接在这里画一根 3px 条，跟着滚动改宽度。
 */
export function startProgress() {
  if (document.getElementById('progress-display')) return
  const bar = document.createElement('div')
  bar.style.cssText = 'position:fixed;top:0;left:0;width:100%;height:3px;z-index:9999999999;'
  bar.innerHTML =
    '<div id="progress-display" style="background-color:var(--theme-color,#42b983);width:0;height:3px;transition:width .3s;"></div>'
  document.body.appendChild(bar)
  window.addEventListener(
    'scroll',
    () => {
      const el = document.getElementById('progress-display')
      if (!el) return
      const top = document.documentElement.scrollTop || document.body.scrollTop
      const remain = document.documentElement.scrollHeight - document.documentElement.clientHeight
      el.style.width = (remain <= 0 ? 0 : Math.ceil((top / remain) * 100)) + '%'
    },
    { passive: true }
  )
}
