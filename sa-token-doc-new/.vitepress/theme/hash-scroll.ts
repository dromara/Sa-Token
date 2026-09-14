/**
 * 长页锚点：精准定位 + 过渡滚动。
 * 对齐 sa-token-doc/static/docsify-plugin.js 功能8
 *（挡住框架/浏览器的 scrollTo，避免两套动画在终点互抢导致抖动）
 *
 * 直开带 hash 的地址时，先摘掉 hash，等目标渲染后再从页顶过渡过去。
 * 否则浏览器会在标题一出现就把滚动钉在锚点上，动画看起来像「没有」。
 */

const HASH_TOP_MARGIN = 80

declare global {
  interface Window {
    __ST_PENDING_HASH__?: string
  }
}

let pendingHash = ''
let hashScrollRaf = 0
let hashScrollTimer = 0
let hashScrollToken = 0
let animating = false
let nativeScrollIntoView: typeof Element.prototype.scrollIntoView | undefined
let nativeScrollTo: typeof window.scrollTo | undefined
let scrollHijacked = false

function rawHash() {
  const hash = location.hash || pendingHash || window.__ST_PENDING_HASH__ || ''
  if (!hash.startsWith('#') || hash === '#') return ''
  return hash
}

function getHashId() {
  const hash = rawHash()
  if (!hash) return ''
  try {
    return decodeURIComponent(hash.slice(1))
  } catch {
    return hash.slice(1)
  }
}

/** Docsify 旧锚点常带前缀 `_`（如 `_6、商务合作`），VitePress slug 没有 */
function findHashEl(id: string) {
  if (!id) return null
  const ids = id.startsWith('_') ? [id, id.slice(1)] : [id, `_${id}`]
  for (const candidate of ids) {
    const el = document.getElementById(candidate)
    if (el) return el
  }
  return null
}

function holdHash() {
  const hash = rawHash()
  if (!hash) return
  pendingHash = hash
  window.__ST_PENDING_HASH__ = hash
  if (location.hash) {
    history.replaceState(history.state || {}, '', location.pathname + location.search)
  }
}

function releaseHash() {
  const hash = pendingHash || window.__ST_PENDING_HASH__ || ''
  if (!hash) return
  if (location.hash !== hash) {
    history.replaceState(history.state || {}, '', location.pathname + location.search + hash)
  }
  pendingHash = ''
  window.__ST_PENDING_HASH__ = undefined
}

function getPageY() {
  return window.pageYOffset || document.documentElement.scrollTop || 0
}

function setPageY(y: number) {
  document.documentElement.scrollTop = y
  document.body.scrollTop = y
}

function easeInOutCubic(t: number) {
  return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2
}

function topMargin() {
  const pad = parseFloat(getComputedStyle(document.documentElement).scrollPaddingTop)
  return Number.isFinite(pad) ? pad : HASH_TOP_MARGIN
}

function hijackNativeScroll() {
  if (scrollHijacked) return
  nativeScrollIntoView = Element.prototype.scrollIntoView
  nativeScrollTo = window.scrollTo.bind(window)
  Element.prototype.scrollIntoView = function () {}
  window.scrollTo = function () {}
  scrollHijacked = true
}

function restoreNativeScroll() {
  if (!scrollHijacked) return
  if (nativeScrollIntoView) Element.prototype.scrollIntoView = nativeScrollIntoView
  if (nativeScrollTo) window.scrollTo = nativeScrollTo
  scrollHijacked = false
}

function cancelHashScroll() {
  hashScrollToken++
  animating = false
  if (hashScrollRaf) {
    cancelAnimationFrame(hashScrollRaf)
    hashScrollRaf = 0
  }
  if (hashScrollTimer) {
    clearTimeout(hashScrollTimer)
    hashScrollTimer = 0
  }
  restoreNativeScroll()
}

function onUserScrollIntent() {
  if (animating) cancelHashScroll()
}

function targetY(el: Element) {
  return Math.max(0, Math.round(el.getBoundingClientRect().top + getPageY() - topMargin()))
}

function scheduleTick(tick: (now?: number) => void) {
  if (hashScrollRaf) cancelAnimationFrame(hashScrollRaf)
  if (hashScrollTimer) clearTimeout(hashScrollTimer)
  let rafFired = false
  hashScrollRaf = requestAnimationFrame((now) => {
    rafFired = true
    tick(now)
  })
  hashScrollTimer = window.setTimeout(() => {
    if (!rafFired) tick()
  }, 32)
}

export function scrollToHashId(opts?: { fromTop?: boolean }) {
  const fromTop = !!opts?.fromTop
  if (fromTop) holdHash()
  const id = getHashId()
  if (!id) {
    cancelHashScroll()
    return
  }

  const token = ++hashScrollToken
  hijackNativeScroll()
  let startY = 0
  let startTime = 0
  let duration = 0
  let waitStart = 0
  let started = false
  let snapped = false
  let lockedEndY = 0

  if (hashScrollRaf) cancelAnimationFrame(hashScrollRaf)
  if (hashScrollTimer) clearTimeout(hashScrollTimer)

  function tick(now?: number) {
    if (token !== hashScrollToken) return
    now = now || performance.now()
    const el = findHashEl(id)
    if (!el) {
      if (fromTop) setPageY(0)
      if (!waitStart) waitStart = now
      if (now - waitStart < 2000) {
        scheduleTick(tick)
      } else {
        hashScrollRaf = 0
        restoreNativeScroll()
        releaseHash()
      }
      return
    }

    if (!started) {
      started = true
      animating = true
      if (fromTop) {
        setPageY(0)
        releaseHash()
        setPageY(0)
        startY = 0
      } else {
        startY = getPageY()
      }
      startTime = now
      lockedEndY = targetY(el)
      duration = Math.min(650, Math.max(400, Math.abs(lockedEndY - startY) * 0.04))
    } else if (Math.abs(targetY(el) - lockedEndY) > 4) {
      lockedEndY = targetY(el)
    }

    const elapsed = now - startTime
    if (elapsed < duration) {
      setPageY(Math.round(startY + (lockedEndY - startY) * easeInOutCubic(elapsed / duration)))
      scheduleTick(tick)
      return
    }

    if (!snapped) {
      snapped = true
      setPageY(targetY(el))
    }
    if (elapsed < Math.max(duration, 520)) {
      scheduleTick(tick)
    } else {
      hashScrollRaf = 0
      animating = false
      restoreNativeScroll()
    }
  }

  scheduleTick(tick)
}

let booted = false

export function bootHashScroll() {
  if (booted) return
  booted = true
  if ('scrollRestoration' in history) history.scrollRestoration = 'manual'
  holdHash()
  window.addEventListener('wheel', onUserScrollIntent, { passive: true })
  window.addEventListener('touchmove', onUserScrollIntent, { passive: true })
  window.addEventListener('keydown', (e) => {
    if (
      e.key === 'ArrowUp' ||
      e.key === 'ArrowDown' ||
      e.key === 'PageUp' ||
      e.key === 'PageDown' ||
      e.key === 'Home' ||
      e.key === 'End' ||
      e.key === ' '
    ) {
      onUserScrollIntent()
    }
  })
  window.addEventListener('hashchange', () => scrollToHashId())
  window.addEventListener('popstate', () => scrollToHashId())
}
