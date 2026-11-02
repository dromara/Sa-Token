<!--
  文档正文 h1 下面那条横幅广告（商业版）。
  框架介绍页 /readme.html 不展示。关掉写 localStorage.closeMdTopAdTime，一周内不再出现。
  挂载后把节点挪到 h1 后面，避免 Vue 插槽位置和旧站对不齐。
-->
<template>
  <div v-if="!closed && !isIntro" ref="root" class="doc-inline-ad">
    <div class="doc-inline-ad__card">
      <span class="doc-inline-ad__close" title="关闭" @click.prevent="closeAd">×</span>
      <a href="https://sa-max.cn?way=st_md_top" target="_blank">
        <div class="doc-inline-ad__body">
          <img class="doc-inline-ad__img" src="/big-file/contact/sa-token-syb-3.png" alt="" />
          <div class="doc-inline-ad__text">
            <p>一个项目搞定：同域、跨域、共享Redis、跨Redis、前后端一体、前后端分离、纯 js、vue2、vue3、非 Sa-Token 项目、非 java 项目等架构下的 SSO 认证需求。</p>
            <p>一次购买，永久授权。全源码交付，不含密 Jar。提供售后技术支持。</p>
          </div>
        </div>
      </a>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onContentUpdated, useRoute } from 'vitepress'
import { computed, nextTick, onMounted, ref, watch } from 'vue'

const route = useRoute()
const closed = ref(false)
const root = ref<HTMLElement | null>(null)
const HIDE_MS = 1000 * 60 * 60 * 24 * 7

const isIntro = computed(() => {
  const p = route.path.replace(/\.html$/, '')
  return p === '/README' || p === '/readme'
})

function readClosed() {
  try {
    const t = localStorage.closeMdTopAdTime
    if (t && Date.now() - parseInt(t, 10) < HIDE_MS) closed.value = true
  } catch {
    /* ignore */
  }
}

function closeAd() {
  const layer = (window as unknown as { layer?: { confirm: Function; msg: Function } }).layer
  if (!layer) {
    closed.value = true
    localStorage.closeMdTopAdTime = String(Date.now())
    return
  }
  layer.confirm('关闭后，一周内不再展现此信息', function () {
    closed.value = true
    layer.msg('关闭成功')
    localStorage.closeMdTopAdTime = String(Date.now())
  })
}

function placeAfterH1() {
  nextTick(() => {
    const el = root.value
    if (!el || closed.value) return
    const h1 = document.querySelector('#main .st-content h1')
    if (h1 && h1.nextElementSibling !== el) h1.after(el)
  })
}

readClosed()
onMounted(placeAfterH1)
onContentUpdated(() => {
  readClosed()
  placeAfterH1()
})
watch(() => route.path, placeAfterH1)
</script>
