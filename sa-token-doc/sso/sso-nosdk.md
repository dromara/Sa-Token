# SSO整合 - NoSdk 模式与非 java 项目

---

经常有小伙伴提问：客户端不使用 Sa-Token，能否接入 SSO 认证中心？当然是可以的。

SSO-Server 所有接口都是通过 http 协议开放的，这意味着原则上只要一个语言支持 http 请求调用就可以对接 SSO-Server，参考： [SSO 认证中心开放接口](/sso/sso-apidoc)

### NoSdk 模式

NoSdk 模式（不使用SDK）：通过 http 工具类调用接口的方式来对接 SSO-Server。

参考 demo：[sa-token-demo-sso3-client-nosdk](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso3-client-nosdk)

该 demo 假设应用端没有使用任何“权限认证框架”，使用最基础的 ServletAPI 进行会话管理，模拟了 `/sso/login`、 `/sso/logout`、 `/sso/logoutCall` 三个接口的处理逻辑。

> [!WARNING| label:NoSdk 示例不再主维护] 
> 基于以下原因：
> - 1、NoSdk demo 相当于通过 http 工具类再次重写了一遍 Sa-Token SSO 模块代码，繁琐且冗余。
> - 2、重写的代码无法拥有 Sa-Token SSO 模块全部能力，仅能完成基本对接，算是一个简化版 SDK。
> 
> 自 v1.43.0 版本起，不再主维护 NoSdk 模式，仓库示例仅做留档参考，大家可以转为 ReSdk 模式。


### ReSdk 模式

ReSdk 模式（重写SDK部分方法）：通过重写框架关键步骤点，来对接 SSO-Server。

参考 demo：[sa-token-demo-sso3-client-resdk](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso3-client-resdk)

> [!INFO| label:ReSdk 模式优点] 
> - 1、依然支持客户端使用任意技术栈。
> - 2、仅重写少量部分关键代码，即可完成对接。几乎可以得到 Sa-Token SSO 模块全量能力。

建议新项目首选 ReSdk 模式作为参考。




### 非 java 语言项目

sso-server 的所有接口均以 http 协议对外开放，因此原则上支持任何语言对接，只要这个语言支持 http 请求调用。

例如 PHP、.NET、Node.js 等语言的项目，无法集成 Sa-Token，同上，也可以通过 http 工具类调用接口的方式来对接 SSO-Server。

建议各位同学先搞懂 NoSdk 模式的对接流程，再参照 [SSO 认证中心开放接口](/sso/sso-apidoc) 章节进行对接。


