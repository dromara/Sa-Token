/**
 * 问卷邀请弹层（对应旧站 static/is-fill-in-wj-plugin.js）。
 * 默认关，index.ts 里 reservedPlugins.survey 改 true 才往页面塞脚本。
 */
export function startSurvey() {
  const s = document.createElement('script')
  s.src = '/static/is-fill-in-wj-plugin.js'
  document.body.appendChild(s)
}
