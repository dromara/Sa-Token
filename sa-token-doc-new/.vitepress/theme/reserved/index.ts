/**
 * 现网 Docsify 里注释掉的插件，本地化后放这里。
 * 上线默认全关。要开哪个把对应项改 true，再 npm run docs:build。
 * 说明见同目录 README.md。
 */
export const reservedPlugins = {
  star: false,        // Gitee star 检查弹层
  survey: false,      // 问卷邀请
  docLock: false,     // 章节锁
  docLockByGzh: false, // 公众号章节锁
  progress: false     // 顶部阅读进度条
}

/** 按开关动态 import，关着的插件不会打进首包 */
export function bootReserved() {
  if (reservedPlugins.progress) {
    import('./progress.ts').then((m) => m.startProgress())
  }
  if (reservedPlugins.star) {
    import('./star.ts').then((m) => m.startStarCheck())
  }
  if (reservedPlugins.survey) {
    import('./survey.ts').then((m) => m.startSurvey())
  }
  if (reservedPlugins.docLock) {
    import('./doc-lock.ts').then((m) => m.startDocLock())
  }
  if (reservedPlugins.docLockByGzh) {
    import('./doc-lock-gzh.ts').then((m) => m.startDocLockByGzh())
  }
}
