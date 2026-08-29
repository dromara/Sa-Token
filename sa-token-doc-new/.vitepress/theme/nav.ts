/** 顶栏菜单数据，给 SiteHeader.vue 用。external: true 表示整页跳，不走文档 SPA。 */
export type NavItem = {
  text: string
  link?: string
  external?: boolean
  accent?: boolean
  hideOnNarrow?: boolean
  items?: { text: string; link: string; external?: boolean }[]
}

export const nav: NavItem[] = [
  { text: '首页', link: '/', external: true },
  { text: '文档', link: '/readme.html' },
  { text: '博客', link: '/blog/index.html', external: true },
  {
    text: '视频',
    hideOnNarrow: true,
    items: [
      { text: '乐之者java（登录认证/权限管理/apiKey等）', link: 'https://www.bilibili.com/video/BV1LzNu6KEsW', external: true },
      { text: '抓蛙师（23集）', link: 'https://www.bilibili.com/video/BV1PF9QBXEet/?p=53', external: true },
      { text: '朱老师的小课堂（7集）', link: 'https://www.bilibili.com/video/BV1fsUVBWEyH/', external: true },
      { text: '王清江唷 SSO篇（29集）', link: 'https://www.bilibili.com/video/BV1NF1FBpEe6/', external: true },
      { text: 'fox说技术（7集）', link: 'https://www.bilibili.com/video/BV1uZUpYVEst/', external: true },
      { text: '架构驿站（11集）', link: 'https://www.bilibili.com/video/BV1eFtRezERp?p=87', external: true },
      { text: '王清江唷（99集）', link: 'https://www.bilibili.com/video/BV1Zt421u7gk/', external: true },
      { text: '筑梦信仰-joy（20集）', link: 'https://www.bilibili.com/video/BV1kG411o7Ms/', external: true },
      { text: '达达-Java（26集）', link: 'https://www.bilibili.com/video/BV11u4y197JL/', external: true },
      { text: '晒太阳的盐（22集）', link: 'https://space.bilibili.com/473679148/video', external: true },
      { text: '[ + 课程提交 ]', link: 'https://wj.qq.com/s2/27539608/zzzd/', external: true }
    ]
  },
  {
    text: '案例',
    hideOnNarrow: true,
    items: [
      { text: 'Gitee - Awesome-Sa-Token', link: 'https://gitee.com/sa-tokens/awesome-sa-token', external: true },
      { text: 'GitHub - Awesome-Sa-Token', link: 'https://github.com/sa-tokens/awesome-sa-token', external: true },
      { text: 'AtomGit - Awesome-Sa-Token', link: 'https://atomgit.com/sa-tokens/awesome-sa-token', external: true }
    ]
  },
  { text: '加群', link: '/more/join-group.html', hideOnNarrow: true },
  { text: '需求提交', link: '/more/demand-commit.html', hideOnNarrow: true },
  { text: '赞助', link: '/more/sa-token-donate.html', hideOnNarrow: true },
  {
    text: '🔥 SSO/OAuth2 商业版',
    link: 'https://sa-max.cn?way=st_doc_top',
    external: true,
    accent: true
  },
  {
    text: '安全推荐',
    hideOnNarrow: true,
    items: [
      { text: '开发者安全 Checklist', link: 'https://github.com/FallibleInc/security-guide-for-developers/blob/master/security-checklist-zh.md', external: true },
      { text: 'API 安全 Checklist', link: 'https://github.com/shieldfy/API-Security-Checklist/blob/master/README-zh.md', external: true },
      { text: '腾讯代码安全指南', link: 'https://github.com/Tencent/secguide', external: true },
      { text: 'Web 安全学习笔记', link: 'https://github.com/LyleMi/Learn-Web-Hacking', external: true },
      { text: 'OWASP Cheat Sheet Series', link: 'https://github.com/OWASP/CheatSheetSeries', external: true },
      { text: 'PayloadsAllTheThings', link: 'https://github.com/swisskyrepo/PayloadsAllTheThings', external: true }
    ]
  },
  {
    text: '相关资源',
    hideOnNarrow: true,
    items: [
      { text: '更新日志', link: '/more/update-log.html' },
      { text: '常见报错', link: '/more/common-questions.html' },
      { text: '推荐公众号', link: '/more/tj-gzh.html' },
      { text: '在线考试', link: '/fun/sa-token-test.html' },
      { text: '在线提问', link: '/fun/issue-template.html' },
      { text: '问卷调查', link: '/more/wenjuan.html' }
    ]
  }
]
