/**
 * Gitee star 检查弹层（对应旧站 static/is-star-plugin.js）。
 * 默认关，index.ts 里 reservedPlugins.star 改 true 才往页面塞脚本。
 */
export function startStarCheck() {
  const s = document.createElement('script')
  s.src = '/static/is-star-plugin.js'
  document.body.appendChild(s)
}
