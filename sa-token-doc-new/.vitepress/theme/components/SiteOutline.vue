<!--
  右侧「本页目录」：扫正文 h2/h3/h4，滚动时高亮当前章节。
  点链接是页内 #hash，滚动动画交给 hash-scroll.ts，这里只负责列出和标 active。
-->
<template>
  <div class="st-outline">
    <div class="doc-right-bj-box-title">目录</div>
    <ul v-if="items.length" class="app-sub-sidebar">
      <li
        v-for="item in items"
        :key="item.id"
        :class="['lv-' + item.level, { active: item.id === activeId }]"
      >
        <a :href="`#${item.id}`">{{ item.title }}</a>
      </li>
    </ul>
    <p v-else class="st-muted">本页无章节</p>
  </div>
</template>

<script setup lang="ts">
import { onContentUpdated } from 'vitepress'
import { onMounted, onUnmounted, ref } from 'vue'

type TocItem = { title: string; id: string; level: number }

const items = ref<TocItem[]>([])
const activeId = ref('')

function collect() {
  const root = document.querySelector('.st-content')
  if (!root) {
    items.value = []
    return
  }
  items.value = [...root.querySelectorAll<HTMLElement>('h2, h3, h4')].map((el) => ({
    title: (el.textContent || '').replace(/\u200b/g, '').trim(),
    id: el.id,
    level: Number(el.tagName.slice(1))
  }))
}

function onScroll() {
  const heads = [...document.querySelectorAll<HTMLElement>('.st-content h2, .st-content h3, .st-content h4')]
  if (!heads.length) {
    activeId.value = ''
    return
  }
  let current = heads[0].id
  for (const h of heads) {
    if (h.getBoundingClientRect().top <= 88) current = h.id
  }
  activeId.value = current
}

onMounted(() => {
  collect()
  onScroll()
  window.addEventListener('scroll', onScroll, { passive: true })
})
onContentUpdated(() => {
  collect()
  onScroll()
})
onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
})
</script>
