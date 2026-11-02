<!--
  文档页脚：当前这篇 md 在 Gitee / GitHub / AtomGit 的「改此页」链接，以及备案号。
  仓库路径写死 sa-token-doc-new/docs/，和 VitePress srcDir 对齐。
-->
<template>
  <div class="st-footer-wrap">
    <br /><br /><br /><br /><br /><br /><br />
    <hr />
    <footer>
      <span>
        发现错误？ 您可以在
        <a :href="gitee" target="_blank">Gitee</a>
        或
        <a :href="github" target="_blank">GitHub</a>
        或
        <a :href="atomgit" target="_blank">AtomGit</a>
        帮助我们完善此页文档！
      </span>
      或 <a href="/more/join-group.html" target="_blank">加入讨论群</a> 交流反馈。
      <br />
      <p style="font-size: 12px; color: #999;">
        我们坚信，即使再复杂的技术，也可以用清晰、干练、易懂的文字描述出它的具体细节，如果你在阅读文档时有难以理解的章节，那一定是我们还没有优化好它，
        请向我们 <a href="/more/demand-commit.html" target="_blank">反馈</a> 你的困惑之处，我们将持续优化文档。
      </p>
      <br />
      <a href="https://beian.miit.gov.cn/" target="_blank" class="footer-beian">鲁ICP备18046274号-5</a>
      <span style="color:#999;font-size:12px;margin:0 12px;">|</span>
      <a
        href="https://beian.mps.gov.cn/#/query/webSearch?code=37011202002956"
        rel="noreferrer"
        target="_blank"
        class="footer-beian"
      >
        <img
          src="/big-file/index/gonganbeian.png"
          alt="公安备案"
          style="width:14px;height:14px;vertical-align:-2px;margin-right:3px;"
        >鲁公网安备37011202002956号
      </a>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { useData } from 'vitepress'
import { computed } from 'vue'

const { page } = useData()
const file = computed(() => page.value.filePath.replace(/\\/g, '/'))
const gitee = computed(() => `https://gitee.com/dromara/sa-token/tree/dev/sa-token-doc-new/docs/${file.value}`)
const github = computed(() => `https://github.com/dromara/sa-token/tree/dev/sa-token-doc-new/docs/${file.value}`)
const atomgit = computed(() => `https://atomgit.com/dromara/sa-token/tree/dev/sa-token-doc-new/docs/${file.value}`)
</script>
