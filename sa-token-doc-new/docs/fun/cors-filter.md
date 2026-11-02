---
title: "Sa-Token 解决跨域问题"
keywords: "Sa-Token,sa-token,satoken,Sa-Token文档,解决跨域问题,附录"
description: "Sa-Token 解决跨域问题：前后端分离时 CORS 需与鉴权过滤器协同，否则浏览器会拦跨域响应。本页按参考1至3给出三篇外部实践（掘金一篇、微信公众号两篇），打开链接对照跨域头、预检与过滤器顺序，本页不重复贴配置代码。三篇均为社区实践而非框架内置示例，建议打开后按文章步骤改过滤器顺序与跨域响应头。"
---

# 解决跨域问题 

<!-- 参考：[https://blog.csdn.net/shengzhang_/article/details/119928794](https://blog.csdn.net/shengzhang_/article/details/119928794) -->

参考1: [https://juejin.cn/post/7491603065944129590](https://juejin.cn/post/7491603065944129590)

参考2: [https://mp.weixin.qq.com/s/tbqjCKrTMj-l1lZbeyu81g](https://mp.weixin.qq.com/s/tbqjCKrTMj-l1lZbeyu81g)

参考3: [https://mp.weixin.qq.com/s/8aziIhqGCb_qsr8kLiqzmg](https://mp.weixin.qq.com/s/8aziIhqGCb_qsr8kLiqzmg)