# SSO整合 - NoSdk、ReSdk 模式与非 java 项目

---

经常有小伙伴提问：客户端不使用 Sa-Token，能否接入 SSO 认证中心？当然是可以的。

SSO-Server 所有接口都是通过 http 协议开放的，这意味着原则上只要一个语言支持 http 请求调用就可以对接 SSO-Server，参考： [SSO 认证中心开放接口](/sso/sso-apidoc)

### NoSdk 模式

NoSdk 模式（不使用SDK）：通过 http 工具类调用接口的方式来对接 SSO-Server。

参考 demo：[sa-token-demo-sso3-client-nosdk](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso3-client-nosdk)

该 demo 假设应用端没有使用任何“权限认证框架”，使用最基础的 ServletAPI 进行会话管理，模拟了 `/sso/login`、 `/sso/logout`、 `/sso/pushC` 三个接口的处理逻辑。

> [!INFO| label:NoSdk 模式优缺点] 
> - 1、支持客户端使用任意技术栈。
> - 2、代码简单易懂，流程直观清晰。
> - 3、用 http 工具类模拟 Sa-Token SSO 内部实现，样版代码较多，略显冗余。



### ReSdk 模式

ReSdk 模式（重写SDK部分方法）：通过重写框架关键步骤点，来对接 SSO-Server。

参考 demo：[sa-token-demo-sso3-client-resdk](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso3-client-resdk)

> [!INFO| label:ReSdk 模式优缺点] 
> - 1、支持客户端使用任意技术栈。
> - 2、仅重写少量部分关键代码，即可完成对接。几乎可以得到 Sa-Token SSO 模块全量能力。
> - 3、此模式需要对 Sa-Token SSO 内部实现较为熟悉，才可以驾驭。




### 非 java 语言项目

sso-server 的所有接口均以 http 协议对外开放，因此原则上支持任何语言对接，只要这个语言支持 http 请求调用。

例如 PHP、.NET、Node.js 等语言的项目，无法集成 Sa-Token，同上，也可以通过 http 工具类调用接口的方式来对接 SSO-Server。

建议各位同学先搞懂 NoSdk 模式的对接流程，再参照 [SSO 认证中心开放接口](/sso/sso-apidoc) 章节进行对接。


