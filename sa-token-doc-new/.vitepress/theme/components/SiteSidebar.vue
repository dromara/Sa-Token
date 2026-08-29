<!--
  左侧全站目录。数据来自 ../../sidebar.ts，改侧栏改那个文件。
  当前页加 active-rep。外链（商业版等）新窗口打开。
  展开/收起不在这里，由 Layout.vue 的 sidebarOpen 控制。
-->
<template>
  <nav class="st-sidebar sidebar" aria-label="站点目录">
    <div class="sidebar-nav">
      <ul>
        <li v-for="group in groups" :key="group.text">
          <p><strong>{{ group.text }}</strong></p>
          <ul>
            <li
              v-for="item in group.items"
              :key="item.text"
              :class="{ 'active-rep': item.link && isActivePath(route.path, item.link) }"
            >
              <a
                v-if="item.link"
                :href="item.link"
                :target="isExternalLink(item.link) ? '_blank' : undefined"
                :rel="isExternalLink(item.link) ? 'noreferrer' : undefined"
              >{{ item.text }}</a>
            </li>
          </ul>
        </li>
      </ul>
      <br /><br /><br /><br /><br /><br /><br />
      <p class="sidebar-end">----- 到底线了 -----</p>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { useRoute } from 'vitepress'
import { computed } from 'vue'
import { sidebar } from '../../sidebar.ts'
import { isActivePath, isExternalLink } from '../paths.ts'

const route = useRoute()
const groups = computed(() => (Array.isArray(sidebar) ? sidebar : []))
</script>
