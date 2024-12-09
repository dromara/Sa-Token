# SSO整合 - NoSdk 模式与非 java 项目

---

### NoSdk 模式

如果我们的 SSO 应用端不想或不能集成 Sa-Token，则可以使用 NoSdk 模式来对接。

NoSdk 模式：通过 http 工具类调用接口的方式来对接 SSO-Server。

其实原理很简单，不能集成 Sa-Token 了，那我们就手动写代码模拟出 Sa-Token 在 SSO 流程所做的工作即可。

由于所需代码较多，无法在文档处直接展示，demo 地址可参考：
[sa-token-demo-sso3-client-nosdk](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso3-client-nosdk)

该 demo 假设应用端没有使用任何“权限认证框架”，使用最基础的 ServletAPI 进行会话管理，模拟了 `/sso/login`、`/sso/logout`、`/sso/logoutCall` 三个接口的处理逻辑。

建议各位同学在阅读源码时结合 [SSO 认证中心开放接口](/sso/sso-apidoc) 观看。





### 非 java 语言项目

sso-server 的所有接口均以 http 协议对外开放，因此原则上支持任何语言对接，只要这个语言支持 http 请求调用。

例如 PHP、.NET、Node.js 等语言的项目，无法集成 Sa-Token，同上，也可以通过 http 工具类调用接口的方式来对接 SSO-Server。

建议各位同学先搞懂 NoSdk 模式的对接流程，再参照 [SSO 认证中心开放接口](/sso/sso-apidoc) 章节进行对接。


