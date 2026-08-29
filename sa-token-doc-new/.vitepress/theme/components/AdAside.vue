<!--
  文档页右侧栏下半截：商业版推荐广告 + 加群 / 离线文档 / Demo 下载。
  关广告写 localStorage.closeAdTimeRight，一天内不再出现（旧站同逻辑）。
  样式走 public/static/doc.css 的 .ad-box / .help-btn / .ew-wa，不在本文件写 CSS。
-->
<template>
  <div>
    <div v-if="!closed" class="ad-box">
      <div class="ad-title">
        <span class="ad-tips">推荐</span>
        <span class="ad-tips ad-close" @click="closeAd">关闭</span>
      </div>
      <div class="top-ad-box" style="margin-bottom: 12px;">
        <a href="https://sa-max.cn?way=st_r" target="_blank" rel="noreferrer">
          <div class="mad-bg-box">
            <div class="mad-context-box">
              <img class="mad-img" src="/big-file/contact/sa-token-syb-3.png" alt="" width="130" height="100" />
              <span class="mad-text">
                <b>Sa-Token 商业版：轻松搭建 SSO 单点登录、OAuth2.0 统一认证、API Key 认证。全源码交付、可二开。</b>
              </span>
            </div>
          </div>
        </a>
      </div>
    </div>
    <div class="help-btn">
      <a href="/more/join-group.html">
        加入 Sa-Token 框架交流群
      </a>
    </div>
    <div class="ew-wa">
      <p>
        <a href="https://pan.quark.cn/s/d5abda720e88" target="_blank" rel="noreferrer">离线版文档</a>
        <a href="https://pan.quark.cn/s/fea7e5ec72ee" target="_blank" rel="noreferrer">历史所有版本文档</a>
        <a href="/more/download-demos.html">Demo 示例大全下载</a>
      </p>
    </div>
    <div class="ew-wa">
      <p>如果 Sa-Token 帮助到了你，希望你可以向同事、朋友推荐了解本框架，这对我们非常重要，感谢支持！</p>
      <p>加油，工程师！</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const closed = ref(false)
const HIDE_MS = 1000 * 60 * 60 * 24 * 1

try {
  const t = localStorage.closeAdTimeRight
  if (t && Date.now() - parseInt(t, 10) < HIDE_MS) closed.value = true
} catch {
  /* ignore */
}

function closeAd() {
  const layer = (window as unknown as { layer?: { confirm: Function; msg: Function } }).layer
  if (!layer) {
    closed.value = true
    localStorage.closeAdTimeRight = String(Date.now())
    return
  }
  layer.confirm('关闭后，一天内不再展现此信息', function () {
    closed.value = true
    layer.msg('关闭成功')
    localStorage.closeAdTimeRight = String(Date.now())
  })
}
</script>
