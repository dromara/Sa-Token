# Commit Message 示例

基于 Sa-Token 项目近期提交整理，遵循 Conventional Commits + 50/72 规则。

## feat - 新增功能

```
feat: 添加 sa-token-jackson3 插件
feat: 新增 sa-token-spring-boot4-starter 集成包
feat: 新增 sa-token-reactor-spring-boot4-starter 集成包
feat(sign): 新增签名模板自定义能力
```

## fix - 修复问题

```
fix: 修复 StpUtil.getLoginIdByTokenNotThinkFreeze 方法缺少 static 的问题
fix: 修正一处代码注释错误：SaTokenDao 注释中 数据有效期 应为 小于等于-2 (掉了等于)
fix: Bearer 全局统一大小写
fix: SaOAuth2Strategy中removeGrantTypeHandler的引用有误
```

## refactor - 重构/优化

```
refactor: 移除冗余导包
refactor: 重命名 SaRepeatLoginsMode -> SaReplacedLoginExitMode
refactor: 优化项目构建配置
refactor: 优化 OAuth2 模块在请求中读取 Client 信息算法
refactor: 优化模块依赖关系
refactor: 重构模块依赖层级
refactor: sa-token-dependencies 重构为 sa-token-basic-dependencies
refactor: SaTokenDubboContextFilter 改为使用 SaTokenContextDubboUtil 清理上下文
```

## perf - 性能优化

```
perf: 优化 StrFormatter 常量规范与封装
perf: 优化 pattern 缓存，消除魔法值
```

## docs - 文档

```
docs: 订正文档错别字
docs: 同步最新文章列表、赞助者名单
docs: 为 sa-token-sso 模块定义 STS 协议
docs: 优化 readme
docs: 同步最新博客链接
```

## chore - 杂项

```
chore: 修复注释错别字
chore: 增加忽略 .vscode 目录
```

## demo - 示例

```
demo: 新增 sa-token-demo-webflux-springboot4 示例
demo: 新增 SpringBoot4 整合 demo 示例
```

## test - 测试

```
test: 新增 sa-token-jackson3 单元测试
```

## memo - 备忘录

```
memo: 备忘录重构为专门的文件夹
```

## style - 代码格式

```
style: 统一代码缩进与空格
style: 修复 ESLint 警告
```

## revert - 回滚

```
revert: feat(sign): 新增签名模板自定义能力
```

## AI - AI 相关

```
AI: 新增 skills/remove-redundancy-import/SKILL.md，用于检查项目中的java类无效冗余导包信息并移除
AI: 新增 SKILL: organize-update-log ，用于格式化整理版本更新日志信息
```
