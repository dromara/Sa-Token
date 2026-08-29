<!--
  文档页骨架：顶栏 + 侧栏 + 正文 + 右侧广告/大纲 + 页脚。
  首页和博客不走这个组件（它们是 public 里的独立 HTML）。
-->
<template>
  <div class="st-shell main-box" :class="{ 'sidebar-open': sidebarOpen, 'sidebar-ready': sidebarReady }">
    <SiteHeader />
    <div v-if="page.isNotFound" class="st-404">
      <p class="st-404__kicker">这里是 Sa-Token 文档</p>
      <h1>访问的页面不存在</h1>
      <p>
        <a href="/">回到首页</a>
        <a href="/readme.html">回到文档页</a>
      </p>
    </div>
    <div v-else class="st-body">
      <div class="st-menu-mask" @click="sidebarOpen = false"></div>
      <SiteSidebar />
      <button type="button" class="sidebar-toggle" :aria-expanded="sidebarOpen" aria-label="折叠侧栏" @click="toggleSidebar">
        <div class="sidebar-toggle-button">
          <span></span>
          <span></span>
          <span></span>
        </div>
      </button>
      <div class="st-article content">
        <div id="main" class="markdown-section">
          <AdBanner v-if="!isIntro" />
          <Content class="vp-doc st-content" />
          <SiteFooter />
        </div>
      </div>
      <aside class="st-aside doc-right-bj-box">
        <SiteOutline />
        <div class="doc-right-more-item">
          <AdAside />
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Content, onContentUpdated, useData, useRoute } from 'vitepress'
import { computed, onMounted, ref, watch } from 'vue'
import AdAside from './components/AdAside.vue'
import AdBanner from './components/AdBanner.vue'
import SiteFooter from './components/SiteFooter.vue'
import SiteHeader from './components/SiteHeader.vue'
import SiteOutline from './components/SiteOutline.vue'
import SiteSidebar from './components/SiteSidebar.vue'
import { bindDocImageZoom } from './image-zoom.ts'
import { bootLegacy } from './legacy.ts'
import './layout.css'

const { page } = useData()
const route = useRoute()
const sidebarOpen = ref(true)
const sidebarReady = ref(false)
/** 框架介绍页不插正文顶广告，和旧站一致 */
const isIntro = computed(() => {
  const p = route.path.replace(/\.html$/, '')
  return p === '/README' || p === '/readme'
})

function isNarrow() {
  return window.innerWidth <= 800
}

function toggleSidebar() {
  sidebarOpen.value = !sidebarOpen.value
}

watch(
  () => route.path,
  () => {
    if (typeof window !== 'undefined' && isNarrow()) sidebarOpen.value = false
  }
)

watch(
  () => page.value.frontmatter.redirect,
  (to) => {
    // md 头里写了 redirect: 就整页跳走（商业版占位页等）
    if (typeof to === 'string' && to && typeof window !== 'undefined') window.location.replace(to)
  }
)

onMounted(() => {
  const to = page.value.frontmatter.redirect
  if (typeof to === 'string' && to) window.location.replace(to)
  if (isNarrow()) sidebarOpen.value = false
  requestAnimationFrame(() => {
    sidebarReady.value = true
  })
  window.matchMedia('(max-width: 800px)').addEventListener('change', (e) => {
    sidebarOpen.value = !e.matches
  })
  bootLegacy()
  bindDocImageZoom()
})
onContentUpdated(() => {
  bindDocImageZoom()
})
</script>
