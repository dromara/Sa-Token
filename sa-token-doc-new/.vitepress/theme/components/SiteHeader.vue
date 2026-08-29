<!--
  文档顶栏：Logo、版本下拉、翻译、搜索、主题色板、导航（数据在 ../nav.ts）。
  「首页」「博客」必须整页跳（goStatic），不能走 VitePress SPA，否则会套上文档壳。
  版本下拉、翻译、换肤的实际逻辑在 legacy.ts 里拉的旧站 JS，这里只留 DOM 挂点。
-->
<template>
  <header class="doc-header">
    <div class="nav-left">
      <a href="/" @click="goStatic($event, '/', true)">
        <div class="logo-box">
          <img src="/logo.png" title="logo" alt="Sa-Token" />
          <span class="logo-text">Sa-Token</span>
        </div>
      </a>
      <select class="select-version p-none nav-t2"></select>
      <span id="translate" class="select-language-box ignore nav-t2"></span>
    </div>
    <nav class="nav-right">
      <div class="sear-box p-none nav-t4" tabindex="-1">
        <VPNavBarSearch />
      </div>
      <div class="zk-box">
        <a class="wzi" href="javascript:;">
          <img class="theme-btn" src="/static/icon/theme.svg" alt="主题" />
        </a>
        <div class="zk-context theme-box">
          <div>
            <div style="height: 5px;"></div>
            <span
              v-for="item in lightColors"
              :key="item.color"
              :style="{ backgroundColor: item.color }"
              :title="item.title"
            ></span>
            <span
              v-for="item in darkThemes"
              :key="item.id"
              :style="{ backgroundColor: item.color }"
              :data-st-dark="item.id"
              :title="item.title"
            ></span>
          </div>
        </div>
      </div>
      <template v-for="item in nav" :key="item.text">
        <div v-if="item.items" class="zk-box" :class="{ 'nav-t3': item.hideOnNarrow, 'nav-t2': !item.hideOnNarrow && item.text === '相关资源' }">
          <a class="wzi" href="javascript:;" :class="{ 'nav-active': isDropdownActive(item.items) }">
            <span>{{ item.text }} </span>
            <span class="zk-icon"></span>
          </a>
          <div class="zk-context">
            <div>
              <a
                v-for="child in item.items"
                :key="child.link"
                v-bind="attrs(child.link, child.external)"
              >{{ child.text }}</a>
            </div>
          </div>
        </div>
        <a
          v-else-if="item.link"
          v-bind="attrs(item.link, item.external)"
          class="wzi"
          :class="{
            'nav-active': isItemActive(item.link, item.external),
            'p-none': item.hideOnNarrow,
            'nav-t3': item.hideOnNarrow,
            'nav-t2': item.accent,
            'nav-t1': !item.hideOnNarrow && !item.accent
          }"
          @click="goStatic($event, item.link, item.external)"
        >{{ item.text }}</a>
      </template>
    </nav>
    <Teleport to="body">
      <a
        href="https://github.com/dromara/sa-token"
        target="_blank"
        class="github-corner"
        aria-label="View source on Github"
      >
        <svg width="80" height="80" viewBox="0 0 250 250" aria-hidden="true">
          <path d="M0,0 L115,115 L130,115 L142,142 L250,250 L250,0 Z"></path>
          <path
            d="M128.3,109.0 C113.8,99.7 119.0,89.6 119.0,89.6 C122.0,82.7 120.5,78.6 120.5,78.6 C119.2,72.0 123.4,76.3 123.4,76.3 C127.3,80.9 125.5,87.3 125.5,87.3 C122.9,97.6 130.6,101.9 134.4,103.2"
            fill="currentColor"
            style="transform-origin: 130px 106px"
            class="octo-arm"
          ></path>
          <path
            d="M115.0,115.0 C114.9,115.1 118.7,116.5 119.8,115.4 L133.7,101.6 C136.9,99.2 139.9,98.4 142.2,98.6 C133.8,88.0 127.5,74.4 143.8,58.0 C148.5,53.4 154.0,51.2 159.7,51.0 C160.3,49.4 163.2,43.6 171.4,40.1 C171.4,40.1 176.1,42.5 178.8,56.2 C183.1,58.6 187.2,61.8 190.9,65.4 C194.5,69.0 197.7,73.2 200.1,77.6 C213.8,80.2 216.3,84.9 216.3,84.9 C212.7,93.1 206.9,96.0 205.4,96.6 C205.1,102.4 203.0,107.8 198.3,112.5 C181.9,128.9 168.3,122.5 157.7,114.1 C157.9,116.9 156.7,120.9 152.7,124.9 L141.0,136.5 C139.8,137.7 141.6,141.9 141.8,141.8 Z"
            fill="currentColor"
            class="octo-body"
          ></path>
        </svg>
      </a>
    </Teleport>
  </header>
</template>

<script setup lang="ts">
import { useRoute } from 'vitepress'
import VPNavBarSearch from 'vitepress/dist/client/theme-default/components/VPNavBarSearch.vue'
import { nav } from '../nav.ts'
import { isActivePath, isDocsSection, isExternalLink, normalizePath } from '../paths.ts'

const route = useRoute()

function attrs(link: string, external?: boolean) {
  const isExt = external || isExternalLink(link)
  return {
    href: link,
    target: isExt && isExternalLink(link) ? '_blank' : undefined,
    rel: isExt && isExternalLink(link) ? 'noreferrer' : undefined
  }
}

function goStatic(e: MouseEvent, link: string, external?: boolean) {
  if (!external || isExternalLink(link)) return
  e.preventDefault()
  window.location.assign(link)
}

function navMatches(link?: string, external?: boolean) {
  if (!link || external) return false
  return isActivePath(route.path, link)
}

function isDropdownActive(items?: { link: string; external?: boolean }[]) {
  return !!items?.some((c) => navMatches(c.link, c.external))
}

/** 对齐旧站：文档是兜底高亮；加群/需求提交/赞助或下拉子项命中时，不再同时亮「文档」 */
function isItemActive(link: string, external?: boolean) {
  if (external) return false
  if (normalizePath(link) === '/readme') {
    if (!isDocsSection(route.path)) return false
    return !nav.some(
      (item) =>
        (item.link && normalizePath(item.link) !== '/readme' && navMatches(item.link, item.external)) ||
        isDropdownActive(item.items)
    )
  }
  return navMatches(link, external)
}

const lightColors = [
  { color: '#FFFFFF', title: '纯白' },
  { color: '#f5f5f5', title: '浅灰' },
  { color: '#f5e5f5', title: '淡粉' },
  { color: '#F1FAFA', title: '薄荷白' },
  { color: '#f5f5d5', title: '奶油黄' },
  { color: '#E8E8FF', title: '淡紫' },
  { color: '#f0f9eb', title: '浅绿' },
  { color: '#d5f5f5', title: '浅青' },
  { color: '#ebe5dd', title: '暖米' },
  { color: '#e8f4ff', title: '浅蓝' }
]

const darkThemes = [
  { color: '#1e1e1e', id: 'vscode-dark', title: 'VS Code Dark+' },
  { color: '#2b2b2b', id: 'darcula', title: 'JetBrains Darcula' },
  { color: '#282c34', id: 'one-dark', title: 'Atom One Dark' },
  { color: '#282a36', id: 'dracula', title: 'Dracula' },
  { color: '#1a1b26', id: 'tokyo-night', title: 'Tokyo Night' }
]
</script>
