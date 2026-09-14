<!--
  文档正文底部：上一篇 / 下一篇（顺序与 sidebar.ts 一致）。
-->
<template>
  <nav v-if="prev || next" class="doc-post-nav" aria-label="文档分页">
    <a v-if="prev" class="doc-post-nav-prev" :href="prev.link">← {{ prev.text }}</a>
    <span v-else class="doc-post-nav-placeholder"></span>
    <a v-if="next" class="doc-post-nav-next" :href="next.link">{{ next.text }} →</a>
    <span v-else class="doc-post-nav-placeholder"></span>
  </nav>
</template>

<script setup lang="ts">
import { useRoute } from 'vitepress'
import { computed } from 'vue'
import { getDocPager } from '../doc-pager.ts'

const route = useRoute()
const pager = computed(() => getDocPager(route.path))
const prev = computed(() => pager.value.prev)
const next = computed(() => pager.value.next)
</script>
