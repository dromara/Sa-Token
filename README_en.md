[简体中文](./README.md) | [繁體中文](./README_zh_TW.md) | English | [日本語](./README_ja.md) | [한국어](./README_ko.md) | [Русский](./README_ru.md)

<p align="center">
	<img alt="logo" src="https://sa-token.com/logo.png" width="150" height="150">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Sa-Token v1.46.0</h1>
<h4 align="center">✨ Open source, free, one-stop java permission authentication framework, making authentication simple and elegant! </h4>
<p align="center">
	<a href="https://gitee.com/dromara/sa-token/stargazers"><img src="https://gitee.com/dromara/sa-token/badge/star.svg?theme=gvp"></a>
	<a href="https://gitee.com/dromara/sa-token/members"><img src="https://gitee.com/dromara/sa-token/badge/fork.svg?theme=gvp"></a>
	<a href="https://atomgit.com/dromara/sa-token/stargazers"><img src="https://atomgit.com/dromara/Sa-Token/star/badge.svg"></a>
	<a href="https://github.com/dromara/sa-token/stargazers"><img src="https://img.shields.io/github/stars/dromara/sa-token?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/dromara/sa-token/network/members"><img src="https://img.shields.io/github/forks/dromara/sa-token?style=flat-square&logo=GitHub"></a>
	<!-- <a href="https://github.com/dromara/sa-token/watchers"><img src="https://img.shields.io/github/watchers/dromara/sa-token?style=flat-square&logo=GitHub"></a> -->
	<!-- <a href="https://github.com/dromara/sa-token/issues"><img src="https://img.shields.io/github/issues/dromara/sa-token.svg?style=flat-square&logo=GitHub"></a> -->
	<img src="https://img.shields.io/badge/JDK-8~25-green.svg?style=flat-square" alt="JDK 8~25">
	<a href="https://github.com/dromara/sa-token/blob/master/LICENSE"><img src="https://img.shields.io/github/license/dromara/sa-token.svg?style=flat-square"></a>
</p>
<!-- <p align="center"> To learn and test, please pull the master branch, dev is in the development branch (execute `git checkout master` in the root directory) </p> -->
<p align="center">
<a href="https://sa-token.com?way=readme" target="_blank"> Online documentation: https://sa-token.com</a>
	&nbsp;|&nbsp;
<a href="https://sa-token.com/doc.html#/more/demand-commit" target="_blank">Request submission</a>
</p>


---

### 📝 Foreword:

Looking back at the beginning of 2020, when I submitted the first line of code for Sa-Token, **what Java on the market lacked at that time was not only a simple and easy-to-use authentication framework, but also a complete set of clear and self-consistent permission architecture design ideas**.

Therefore, in the past few years, I have devoted a lot of time to writing Sa-Token documentation. Almost every chapter, every sentence, and every word has been repeatedly revised and finely polished in order to achieve the clearest, most concise, and easy-to-understand expression. **Read the documentation carefully, and you will learn not only the Sa-Token framework itself, but also the best practices for authentication and authorization design in most scenarios**.



### 🛠️ Sa-Token Introduction

Sa-Token is a lightweight Java permission authentication framework that currently has five core modules: login authentication, permission authentication, single sign-on, OAuth2.0, and microservice authentication.

Still hand-rolling these features? **Stop, let Sa-Token handle them!**

![sa-token-jss](https://sa-token.com/big-file/index/intro/sa-token-jss--tran.png)

To use Sa-Token in a SpringBoot project, you only need to introduce dependencies in pom.xml:

``` xml
<!-- Sa-Token authority authentication, online document: https://sa-token.com -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>1.46.0</version>
</dependency>
```

In addition to Spring Boot 2, Sa-Token also provides integration packages for common web frameworks such as Spring Boot 3/4, Solon, and JFinal — truly out of the box.


<details>
<summary><b>Simple examples:</b> (click to expand / collapse)</summary>

Sa-Token aims to complete the permission authentication part of the system in a simple and elegant way. Taking login authentication as an example, you only need:

``` java
// Session login, the parameter is filled in the account id of the login person
StpUtil.login(10001);
```

There is no need to implement any interface or create any configuration file. You only need to call this static code to complete the session login authentication.

If an interface requires login before accessing it, we only need to call the following code:

``` java
// Verify whether the current client has logged in, if not, throw `NotLoginException` exception
StpUtil.checkLogin();
```

In Sa-Token, most functions can be solved with one line of code:

Kick people offline:

``` java
// Kick the session with account ID 10077 offline
StpUtil.kickout(10077);
```

Permission authentication:

``` java
// Annotation authentication: Only sessions with `user:add` permission can enter the method
@SaCheckPermission("user:add")
public String insert(SysUser user) {
    // ... 
    return "用户增加";
}
```

Route interception authentication:

``` java
// Divide modules according to routes, and different modules have different authentication
registry.addInterceptor(new SaInterceptor(handler -> {
	SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
	SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
	SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
	SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
	SaRouter.match("/notice/**", r -> StpUtil.checkPermission("notice"));
	// More modules...
})).addPathPatterns("/**");
```

**If you have used Shiro or SpringSecurity before, you will experience a qualitative leap after switching to Sa-Token.**

<!-- When you are fed up with frameworks such as Shiro and SpringSecurity, you will understand how simple and elegant Sa-Token's API design is compared to these traditional old frameworks! -->

</details>


<details>
<summary><b>Core module list:</b> (click to expand/collapse)</summary>

- **Login Authentication** - Single-end login, multi-end login, same-end mutually exclusive login, no login required within seven days.
- **Permission authentication**——Permission authentication, role authentication, session secondary authentication.
- **Kick people offline** - Kick people offline based on account ID and kick people offline based on Token value.
- **Annotation Authentication** - Elegantly separates authentication from business code.
- **Route interception authentication** - Based on route interception authentication, the restful mode can be adapted.
- **Session** - Shared Session for all ends, exclusive Session for one end, customized Session, convenient access to values.
- **Persistence layer extension** - Redis can be integrated, and data will not be lost after restarting.
- **Separation of front and backend** - APPs, applets and other terminals that do not support cookies can also be easily authenticated.
- **Token style customization** - six built-in Token styles, you can also customize the Token generation strategy.
- **Remember Me Mode** - Adapt to [Remember Me] mode and restart the browser without verification.
- **Second Level Authentication** - Authentication again based on logged in to ensure security.
- **Impersonate other accounts** - Manipulate any user status data in real time.
- **Temporary identity switch** - Temporarily switch the session identity to another account.
- **Same-end mutually exclusive login** - Like QQ, the mobile phone and computer are online at the same time, but login is mutually exclusive on both mobile phones.
- **Account Banning** - Login ban, business classification ban, and penalty ladder ban.
- **Password Encryption** - Provides basic encryption algorithms, which can quickly encrypt MD5, SHA1, SHA256, and AES.
- **Session Query** - Provides a convenient and flexible session query interface.
- **Http Basic Authentication** - One line of code to access Http Basic and Digest authentication.
- **Global Listener** - Perform some AOP operations during key operations such as user login, logout, and being kicked offline.
- **Global Filter** - Conveniently handle cross-domain, global setting of security response first and other operations.
- **Multiple Account System Authentication** - Multiple accounts in one system are authenticated separately (such as the User table and Admin table of the mall)
- **Single Sign-On** - Built-in three single sign-on modes: same domain, cross-domain, same Redis, cross-Redis, front-end and back-end separation and other architectures can be handled.
- **Single point logout** - Initiate a logout in any subsystem, and the entire system will be offline.
- **OAuth2.0 authentication** - Easily build OAuth2.0 service, supporting openid mode.
- **Distributed Session** - Provides a distributed session solution in a shared data center.
- **Microservice Gateway Authentication** - Adapted to route interception authentication of common gateways such as Gateway, ShenYu, Zuul and so on.
- **RPC call authentication** - Gateway forwarding authentication, RPC call authentication, so that service calls are no longer left unprotected
- **Temporary Token Authentication** - Solve the short-term Token authorization problem.
- **Independent Redis** - Separate permission cache and business cache.
- **Quick Login Authentication** - Inject a login page into the project with zero code.
- **Tag Dialect** - Provides Thymeleaf tag dialect integration package and provides beetl integration examples.
- **jwt integration** - Provides three modes of jwt integration solutions and provides token expansion parameter capabilities.
- **RPC call status transfer** - Provides integration packages such as dubbo and grpc, so that the login status is not lost when RPC is called.
- **Parameter Signature** - Provides a cross-system API call signature verification module to prevent parameter tampering and request replay.
- **Automatic Renewal** - Provides two Token expiration strategies, which can be used flexibly and automatically renewed.
- **Out-of-the-box** - Provides common framework integration packages such as SpringMVC, WebFlux, Solon, etc., ready-to-use.
- **Latest technology stack** - Adapt to the latest technology stack: support SpringBoot 3.x, jdk 17.

</details>



### 🍃 SSO single sign-on

Sa-Token SSO is divided into three modes, which can solve the SSO authentication requirements under `同域、跨域、共享Redis、跨Redis、前后端一体、前后端分离、纯 js、vue2、vue3、java 项目、非 java 项目` and other architectures:

![sa-token-jss](https://sa-token.com/big-file/doc/sso/sa-token-sso--white.png)


| System Architecture | Adoption Patterns | Introduction | Documentation Links |
| :--------						| :--------	|:----------------| :--------	|
| Front-end in the same domain + back-end in the same domain as Redis | Mode 1 | Shared cookie synchronization session | [Document](https://sa-token.com/doc.html#/sso/sso-type1), [Example](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso1-client) |
| Different domains on the front end + the same backend as Redis | Mode 2 | URL redirection propagation session | [Document](https://sa-token.com/doc.html#/sso/sso-type2), [Example](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso2-client) |
| Different front-end domains + different back-end Redis | Mode 3 | HTTP request to obtain session | [Document](https://sa-token.com/doc.html#/sso/sso-type3), [Example](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso3-client) |


1. Front-end same domain: means that multiple systems can be deployed under the same main domain name, such as: `c1.domain.com`, `c2.domain.com`, `c3.domain.com`
2. The backend is the same as Redis: This means that multiple systems can connect to the same Redis and share session data.
3. If it is impossible to ensure that the front-end is in the same domain and the back-end is in the same Redis, you can use the third mode of support: Http request verification ticket to obtain the session.
4. Provided: NoSdk mode example + sso-server interface document, non-Sa-Token projects and non-java projects can also be connected.
5. Provide: multiple security verification: domain name verification, ticket verification, parameter signature verification, effectively preventing ticket hijacking, request replay and other attacks.
6. Provide: A large number of practical pain point teachings: sso-server front-end and back-end separation design, sso-client front-end and front-end separation design, user data synchronization/migration solution design.
7. Provide: Directly runnable demo examples to help you quickly become familiar with the general SSO login process.
8. Provide: In-depth detail optimization, parameter loss prevention: The author has tested multiple SSO frameworks, and all parameters have been lost. For example, before logging in, it was: `http://a.com?id=1&name=2`, and after successful login, it became: `http://a.com?id=1`. Sa-Token-SSO has a special algorithm to ensure that parameters are not lost, and the original path is accurately returned after successful login.




### 🍂 OAuth2 authorization authentication
The Sa-Token OAuth2 module is divided into four authorization modes to solve the authorization needs in different scenarios.

| Authorization Model | Introduction |
| :--------					| :--------					|
| Authorization code format | OAuth2 standard authorization steps, the server side releases the code, and the client side obtains the code code and redeems it for access_token |
| Hidden | Alternative option, the server uses URL redirection to directly transfer the access_token to the client page |
| Password type | The client directly exchanges the user's account password for authorization access_token |
| Client certificate type | The server-side client_token for the client level represents the resource authorization of the application itself |

Detailed reference document: [https://sa-token.com/doc.html#/oauth2/readme](https://sa-token.com/doc.html#/oauth2/readme)


### 📖❓ Q&A

**1. Does Sa-Token have a complete feature set?**

Seven years of hard work: five core modules (login, authentication, SSO, OAuth2, microservices) + many practical plug-ins (short token, jwt integration, API parameter signature, API Key authorization...) We provide not only permission authentication, we provide a one-stop solution.


**2. Is Sa-Token easy to learn?**

Chinese documentation + Chinese code comments + Chinese communication community + a large number of practical case blogs + multiple video tutorials + a large number of outstanding open source project integration cases.


**3. Are there many people using Sa-Token?**

As of the statistics date (2026-1-25), Sa-Token is in:

- Gitee followers reached 48627 Stars, ranking first in the list of all recommended projects on the platform.
- GitHub attention reached 18523 Stars, which is 1.97 times that of the main competing framework Spring Security and 4.19 times that of Apache Shiro.
- 25+ WeChat fan groups (500 people), 8+ QQ fan groups (1000 people or 2000 people), online document visits with monthly PV of 200,000+.

This is the data that many developers voted with their feet. I believe these data can prove the popularity of Sa-Token better than any words.


**4. What authoritative certification does Sa-Token have?**

Honors include but are not limited to: Gitee GVP Most Valuable Open Source Project, GitCode G-Star High-Quality Open Source Project, OSCHINA 2021 Popularity Index TOP 30 Open Source Project, OSCHINA 2022 One of the Most Popular Chinese Open Source Project Communities, Open Atomic Foundation 2023 Fast-Growing Open Source Project, Dromara Organization Top Project (One), Preparatory Member of the Trusted Open Source Community Community, and the open source community "Dromara" where he works Won the second prize in the "2024 China Internet Development Innovation and Investment Competition (Open Source)". Gitee High Star project (5000+star). Gitee's Top 2 Open Source Projects for Web Application Development in 2025.


**5. Is there any charge for Sa-Token?**

Sa-Token adopts the Apache-2.0 open source protocol and promises that the framework itself and online documents will always be free and open. Of course, if you are interested in sponsoring Sa-Token, we will not shy away from it: [Sponsorship Link](https://sa-token.com/doc.html#/more/sa-token-donate).
We will regularly synchronize the sponsor list to the online document display. (One thing you need to note is: this sponsorship is only a friendly sponsorship and does not provide any commercial exchange)


**6. Is Sa-Token an encapsulated SpringSecurity? Is it a shell of ApacheShiro?**

No. Sa-Token is not a backend template, nor is it a secondary encapsulation shell for the xx framework, but a pure self-developed framework starting from scratch, with zero dependencies on the core package, a completely independent and controllable architecture core + integrated adaptation of many mainstream frameworks.
						



#### Certificate ⭐ Trophy 🏆 Honor Display

<table align="center">
 <tr>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/gvp.jpg" title="GVP - Gitee 最有价值开源项目" /></td>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/g-star.jpg" title="GitCode G-Star 优质开源项目" /></td>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/osc-2021.jpg" title="OSCHINA 2021 人气指数 TOP 30 开源项目" /></td>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/osc-2022--chang.jpg" title="OSCHINA 2022 年度最火热中国开源项目社区" /></td>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/kexin.jpg" title="可信开源社区共同体预备成员" /></td>
 </tr>
 <tr>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/gitee-star-5000.jpg" title="Gitee 5000 star 专属奖杯" /></td>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/gitee-2025--chang.jpg" title="Gitee 2025年度开源项目 Web应用开发 Top 2" /></td>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/dromara.jpg" title="Dromara 组织顶尖项目（之一）" /></td>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/kaifangyuanzi2--chang.jpg" title="开放原子基金会2023快速成长开源项目" /></td>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/dromara-2024-tzds.jpg" title="Dromara 荣获《2024中国互联网发展创新与投资大赛（开源）》二等奖" /></td>
 </tr>
</table>



### 🚀 Excellent open source integration cases

- [[ Snowy ]](https://gitee.com/xiaonuobase/snowy): China’s first rapid development platform that separates national secrets before and after, using Vue3 + Vite + SpringBoot + MP + HuTool + SaToken.
- [[ RuoYi-Vue-Plus ]](https://gitee.com/dromara/RuoYi-Vue-Plus): Rewrite all functions of RuoYi-Vue and integrate Sa-Token, Mybatis-Plus, Xxl-Job, knife4j, and OSS for regular synchronization.
- [[ Smart-Admin ]](https://gitee.com/lab1024/smart-admin): SmartAdmin is the first domestic rapid development platform with "high-quality code" as the core and "simple, efficient and safe" middle and backend.
- [[ Chengdan ]](https://gitee.com/orangeform/orange-admin): Chengdan’s low-code generator in Taiwan. It can fully support multi-applications, multi-tenants, multi-channels, workflows, free combination of framework technology stacks, etc.
- [[ Deng Deng ]](https://gitee.com/dromara/lamp-cloud): A mid- and back-end rapid development platform focusing on multi-tenant solutions. Support independent database, shared data architecture and non-tenant mode ✨
- [[ Shiyi Blog ]](https://gitee.com/quequnlong/shiyi-blog): A vue + springboot blog system that separates the front and back ends.



There are more excellent open source cases that cannot be displayed one by one, please refer to: [Awesome-Sa-Token](https://gitee.com/sa-token/awesome-sa-token)


### 🌍 Other language versions

Sa-Token community members contributed multi-language implementation versions:

- Rust version: [https://github.com/sa-tokens/sa-token-rust](https://github.com/sa-tokens/sa-token-rust)
- Go version: [https://github.com/sa-tokens/sa-token-go](https://github.com/sa-tokens/sa-token-go)
- NodeJS version: [https://github.com/xiaoLangtou/xlt-token](https://github.com/xiaoLangtou/xlt-token)
- PHP version: [https://gitee.com/jinan-jimeng-network_0/real-token](https://gitee.com/jinan-jimeng-network_0/real-token)

We sincerely invite developers who are more proficient in the above languages to build relevant versions together. 🤝


### 🔗 Friendly links
- [[ OkHttps ]](https://gitee.com/ejlchina-zhxu/okhttps): lightweight http communication framework, extremely elegant API, supports WebSocket and Stomp protocols
- [[ Forest ]](https://gitee.com/dromara/forest): Declarative and programmatic dual cultivation, making the world no longer difficult to send HTTP requests
- [[ Bean Searcher ]](https://github.com/troyzhxu/bean-searcher): REST version of GraphQL — entity definition boundaries, parameter-driven queries, and one line of code to implement complex queries!
- [[ Jpom ]](https://gitee.com/dromara/Jpom): Simple and lightweight low-intrusive online construction, automatic deployment, daily operation and maintenance, and project monitoring software.
- [[ TLog ]](https://gitee.com/dromara/TLog): A lightweight distributed log tag tracking artifact.
- [[ hippo4j ]](https://gitee.com/agentart/hippo4j): A powerful dynamic thread pool framework with monitoring and alarm functions.
- [[ hertzbeat ]](https://gitee.com/dromara/hertzbeat): An easy-to-use and friendly open source real-time monitoring and alarm system, no Agent required, high-performance cluster, and powerful custom monitoring capabilities.
- [[ Solon ]](https://gitee.com/noear/solon): A more modern application development framework: faster, smaller, and more free.
- [[ Chat2DB ]](https://github.com/chat2db/Chat2DB): An AI-driven database management and BI tool that supports the management of 22 databases such as Mysql, pg, Oracle, and Redis.



### 📦 Code hosting
- Gitee：[https://gitee.com/dromara/sa-token](https://gitee.com/dromara/sa-token)
- GitHub：[https://github.com/dromara/sa-token](https://github.com/dromara/sa-token)
- AtomGit：[https://atomgit.com/dromara/sa-token](https://atomgit.com/dromara/sa-token)



### 📚 Collection of examples

**We have separately produced corresponding integration examples for almost all technical points of the framework. This compressed package has a total of 60+ Demo**: covering all aspects of authentication and authentication such as Sa-Token login authentication, authority authentication, SSO single sign-on, OAuth2 unified authentication, microservice authentication, API Key authentication, JWT integration, cross-system call parameter signature verification, etc.

Download address: [https://sa-token.com/doc.html#/more/download-demos](https://sa-token.com/doc.html#/more/download-demos)

<img class="s-w" src="https://sa-token.com/big-file/contact/show/sa-token-demos-pre-liubai.png" />



### 💬 Communication group
<!-- QQ communication group: 685792424 [Click to join](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=Y05Ld4125W92YSwZ0gA8e3RhG9Q4Vsfx&authKey=IomXuIuhP9g8G7l%2ByfkrRsS7i%2Fna0lIBpkTXxx%2BQEaz0NNEyJq00kgeiC4dUyNLS&noverify=0&group_code=685792424)-->
<!-- QQ communication group: 1081649142 [Click to join](https://qm.qq.com/q/SCAaZ6Ros2) -->

QQ communication group: 1098917026 [Click to join](https://qm.qq.com/q/26OFBGd2Zy)

WeChat communication group:

<!-- <img src="https://oss.dev33.cn/sa-token/qr/wx-qr-m-400k.png" width="230px" title="微信群" /> -->

<img src="https://sa-token.com/big-file/contact/i-wx-qr2.jpg" width="230px" title="微信群" />

PS: Scan the QR code to add WeChat (remark: sa-token) to invite you to join the group chat.

<br>

<img class="s-w" src="https://sa-token.com/big-file/contact/show/wx-group-show3--liubai.png" style="max-width: 50%;" alt="微信群" />


Benefits of joining a group chat:
- Receive framework update notifications as soon as possible.
- Receive framework bug notifications as soon as possible.
- Receive notifications of new open source cases as soon as possible.
- Communicate (huá shuǐ) with each other (mō yú) with many big guys 🖐️🐟️.

