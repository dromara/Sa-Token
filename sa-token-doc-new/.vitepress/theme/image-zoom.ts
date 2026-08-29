/**
 * 正文图片点击放大（medium-zoom）。切页要重新 bind，否则新图点不了。
 */
import mediumZoom, { type Zoom } from 'medium-zoom'

let zoom: Zoom | undefined

function collectImgs() {
  return [...document.querySelectorAll('#main img')].filter((el) => {
    const img = el as HTMLImageElement
    if (img.classList.contains('emoji')) return false
    if (img.hasAttribute('data-no-zoom')) return false
    if (img.closest('a, .badge-box')) return false
    return true
  }) as HTMLImageElement[]
}

export function bindDocImageZoom() {
  const opts = { margin: 24, background: '#fff' }
  if (!zoom) zoom = mediumZoom(opts)
  else zoom.update(opts)
  void zoom.close()
  zoom.detach()
  const imgs = collectImgs()
  if (imgs.length) zoom.attach(imgs)
}

export function attachZoomImage(img: HTMLImageElement) {
  if (!zoom) bindDocImageZoom()
  else zoom.attach(img)
}
