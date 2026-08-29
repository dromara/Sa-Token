/**
 * 旧站遗留脚本：翻译、赞助表、水滴换肤、动态演示图、「加载图片」按钮。
 * 文档 Layout 挂载时 bootLegacy() 拉 public/static 里的 js；切页走 onDocPageChange。
 */
import { attachZoomImage } from './image-zoom.ts'

function loadScript(src: string) {
  return new Promise<void>((resolve, reject) => {
    if (document.querySelector(`script[src="${src}"]`)) {
      resolve()
      return
    }
    const el = document.createElement('script')
    el.src = src
    el.onload = () => resolve()
    el.onerror = () => reject(new Error(src))
    document.body.appendChild(el)
  })
}

function loadCss(href: string) {
  if (document.querySelector(`link[href="${href}"]`)) return
  const el = document.createElement('link')
  el.rel = 'stylesheet'
  el.href = href
  document.head.appendChild(el)
}

/** 文档里「点击加载演示图」按钮：点一下把 img 插到按钮后面 */
function bindShowImg() {
  document.addEventListener('click', (e) => {
    const t = (e.target as HTMLElement | null)?.closest?.('.show-img') as HTMLElement | null
    if (!t) return
    const src = t.getAttribute('img-src')
    if (!src) return
    const img = document.createElement('img')
    img.className = 'show-to-img'
    img.src = src
    t.after(img)
    t.remove()
    attachZoomImage(img)
  })
}

function bindDonate() {
  const w = window as unknown as {
    renderDonateTable?: () => void
    onZanzhuSortClick?: () => void
  }
  if (document.querySelector('.zanzhu-table') && w.renderDonateTable) {
    w.renderDonateTable()
    w.onZanzhuSortClick?.()
  }
}

function initVersion() {
  const w = window as unknown as {
    SaTokenVersions?: { initVersionSelects: () => void }
  }
  w.SaTokenVersions?.initVersionSelects()
}

function isZvoTranslate(t: unknown): t is { execute: () => void } {
  return (
    typeof t === 'object' &&
    t !== null &&
    typeof (t as { execute?: unknown }).execute === 'function' &&
    typeof (t as { version?: unknown }).version === 'string'
  )
}

function initTranslate() {
  const w = window as unknown as { initSaTranslate?: () => void; translate?: unknown }
  try {
    w.initSaTranslate?.()
    const t = w.translate
    if (isZvoTranslate(t)) t.execute()
  } catch {
    /* 浏览器翻译扩展会占用 window.translate，不能让它把整站打挂 */
  }
}

function rcTips() {
  if (location.host !== 'rc.sa-token.com') return
  const layer = (window as unknown as { layer?: { alert: Function } }).layer
  layer?.alert(
    '<b>当前文档为RC预览版文档，仅做学习测试使用，正式项目请使用正式版：<a href="https://sa-token.com/" target="_blank">https://sa-token.com/</a></b>'
  )
}

let booted = false

/** 首次进入文档站：拉 jquery/layer/版本下拉/翻译/赞助/换肤 */
export async function bootLegacy() {
  if (booted) return
  booted = true
  loadCss('/static/doc.css')
  loadCss('/static/water-change-theme/water-change-theme.css')
  bindShowImg()
  await loadScript('/static/jquery.min.js')
  await loadScript('/static/layer-v3.1.1/layer.js')
  await loadScript('/static/all-version-common.js')
  initVersion()
  try {
    await loadScript('/static/translate.js')
    await loadScript('/static/sa-translate.js')
    initTranslate()
  } catch {
    /* 翻译脚本失败不影响版本下拉 / 主题 / 赞赏 */
  }
  await loadScript('/static/donate/donate-list.js')
  await loadScript('/static/donate/donate-fun.js')
  await loadScript('/static/water-change-theme/gsap-3.12.2.min.js')
  await loadScript('/static/water-change-theme/water-change-theme.js')
  await loadScript('/a/star-guide/star-guide.js')
  bindDonate()
  rcTips()
}

/** 文档 SPA 切页后：翻译重跑、赞助表重绑、侧栏滚到当前项 */
export function onDocPageChange() {
  initTranslate()
  bindDonate()
  const el = document.querySelector('.sidebar .active-rep')
  el?.scrollIntoView({ block: 'center' })
}
