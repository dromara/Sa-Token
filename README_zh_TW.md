[简体中文](./README.md) | 繁體中文 | [English](./README_en.md) | [日本語](./README_ja.md) | [한국어](./README_ko.md) | [Русский](./README_ru.md)

<p align="center">
	<img alt="logo" src="https://sa-token.com/logo.png" width="150" height="150">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Sa-Token v1.46.0</h1>
<h4 align="center">✨ 開源、免費、一站式 java 權限認證框架，讓鑑權變得簡單、優雅！ </h4>
<p align="center">
	<a href="https://gitee.com/dromara/sa-token/stargazers"><img src="https://gitee.com/dromara/sa-token/badge/star.svg?theme=gvp"></a>
	<a href="https://gitee.com/dromara/sa-token/members"><img src="https://gitee.com/dromara/sa-token/badge/fork.svg?theme=gvp"></a>
	<a href="https://atomgit.com/dromara/sa-token/stargazers"><img src="https://atomgit.com/dromara/Sa-Token/star/badge.svg"></a>
	<a href="https://github.com/dromara/sa-token/stargazers"><img src="https://img.shields.io/github/stars/dromara/sa-token?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/dromara/sa-token/network/members"><img src="https://img.shields.io/github/forks/dromara/sa-token?style=flat-square&logo=GitHub"></a>
	<!-- <a href="https://github.com/dromara/sa-token/watchers"><img src="https://img.shields.io/github/watchers/dromara/sa-token?style=flat-square&logo=GitHub"></a> -->
	<!-- <a href="https://github.com/dromara/sa-token/issues"><img src="https://img.shields.io/github/issues/dromara/sa-token.svg?style=flat-square&logo=GitHub"></a> -->
	<a href="https://github.com/dromara/sa-token/blob/master/LICENSE"><img src="https://img.shields.io/github/license/dromara/sa-token.svg?style=flat-square"></a>
</p>
<!-- <p align="center">學習測試請拉取 master 分支，dev 是在開發分支 (在根目錄執行 `git checkout master`)</p> -->
<p align="center">
	<a href="https://sa-token.com?way=readme" target="_blank">在線文檔：https://sa-token.com</a>
	&nbsp;|&nbsp;
	<a href="https://sa-token.com/doc.html#/more/demand-commit" target="_blank">需求提交</a>
</p>


---

### 📝 前言：

回望 2020 年初，我為 Sa-Token 提交第一行代碼之際，彼時市面上 Java 缺少的不僅是一個簡潔好用的鑑權框架，更是一整套清晰、自洽的權限架構設計思想。

因此，這幾年間我將大量時間傾注在 Sa-Token 的文檔編寫，幾乎每一章節、每一句話、每一個字都經過反覆修改、精細打磨，以求做到最清晰、幹練、易懂的表述。用心閱讀文檔，你學習到的將不止是 Sa-Token 框架本身，更是絕大多數場景下權限設計的最佳實踐。



### 🛠️ Sa-Token 介紹

Sa-Token 是一個輕量級 Java 權限認證框架，目前擁有五大核心模塊：登錄認證、權限認證、單點登錄、OAuth2.0、微服務鑑權。

**你還在手搓以下功能？Stop ⚠️ 讓 Sa-Token 來！**

![sa-token-jss](https://sa-token.com/big-file/index/intro/sa-token-jss--tran.png)

要在 SpringBoot 項目中使用 Sa-Token，你只需要在 pom.xml 中引入依賴：

``` xml
<!-- Sa-Token 權限認證, 在線文檔：https://sa-token.com -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>1.46.0</version>
</dependency>
```

除了支持 SpringBoot2、Sa-Token 還為 SpringBoot3/4、Solon、JFinal 等常見 Web 框架提供集成包，做到真正的開箱即用。


<details>
<summary><b>簡單示例展示：</b>（點擊展開 / 摺疊）</summary>

Sa-Token 旨在以簡單、優雅的方式完成系統的權限認證部分，以登錄認證為例，你只需要：

``` java
// 會話登錄，參數填登錄人的賬號id 
StpUtil.login(10001);
```

無需實現任何接口，無需創建任何配置文件，只需要這一句靜態代碼的調用，便可以完成會話登錄認證。

如果一個接口需要登錄後才能訪問，我們只需調用以下代碼：

``` java
// 校驗當前客戶端是否已經登錄，如果未登錄則拋出 `NotLoginException` 異常
StpUtil.checkLogin();
```

在 Sa-Token 中，大多數功能都可以一行代碼解決：

踢人下線：

``` java
// 將賬號id為 10077 的會話踢下線 
StpUtil.kickout(10077);
```

權限認證：

``` java
// 註解鑑權：只有具備 `user:add` 權限的會話才可以進入方法
@SaCheckPermission("user:add")
public String insert(SysUser user) {
    // ... 
    return "用戶增加";
}
```

路由攔截鑑權：

``` java
// 根據路由劃分模塊，不同模塊不同鑑權 
registry.addInterceptor(new SaInterceptor(handler -> {
	SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
	SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
	SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
	SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
	SaRouter.match("/notice/**", r -> StpUtil.checkPermission("notice"));
	// 更多模塊... 
})).addPathPatterns("/**");
```

**如果您曾經使用過 Shiro、SpringSecurity，在切換到 Sa-Token 後，您將體會到質的飛躍。**

<!-- 當你受夠 Shiro、SpringSecurity 等框架的三拜九叩之後，你就會明白，相對於這些傳統老牌框架，Sa-Token 的 API 設計是多麼的簡單、優雅！ -->

</details>


<details>
<summary> <b>核心模塊一覽：</b>（點擊展開 / 摺疊） </summary>

- **登錄認證** —— 單端登錄、多端登錄、同端互斥登錄、七天內免登錄。
- **權限認證** —— 權限認證、角色認證、會話二級認證。
- **踢人下線** —— 根據賬號id踢人下線、根據Token值踢人下線。
- **註解式鑑權** —— 優雅的將鑑權與業務代碼分離。
- **路由攔截式鑑權** —— 根據路由攔截鑑權，可適配 restful 模式。
- **Session會話** —— 全端共享Session,單端獨享Session,自定義Session,方便的存取值。
- **持久層擴展** —— 可集成 Redis，重啟數據不丟失。
- **前後臺分離** —— APP、小程序等不支持 Cookie 的終端也可以輕鬆鑑權。
- **Token風格定製** —— 內置六種 Token 風格，還可：自定義 Token 生成策略。
- **記住我模式** —— 適配 [記住我] 模式，重啟瀏覽器免驗證。
- **二級認證** —— 在已登錄的基礎上再次認證，保證安全性。 
- **模擬他人賬號** —— 實時操作任意用戶狀態數據。
- **臨時身份切換** —— 將會話身份臨時切換為其它賬號。
- **同端互斥登錄** —— 像QQ一樣手機電腦同時在線，但是兩個手機上互斥登錄。
- **賬號封禁** —— 登錄封禁、按照業務分類封禁、按照處罰階梯封禁。
- **密碼加密** —— 提供基礎加密算法，可快速 MD5、SHA1、SHA256、AES 加密。
- **會話查詢** —— 提供方便靈活的會話查詢接口。
- **Http Basic認證** —— 一行代碼接入 Http Basic、Digest 認證。
- **全局偵聽器** —— 在用戶登陸、註銷、被踢下線等關鍵性操作時進行一些AOP操作。
- **全局過濾器** —— 方便的處理跨域，全局設置安全響應頭等操作。
- **多賬號體系認證** —— 一個系統多套賬號分開鑑權（比如商城的 User 表和 Admin 表）
- **單點登錄** —— 內置三種單點登錄模式：同域、跨域、同Redis、跨Redis、前後端分離等架構都可以搞定。
- **單點註銷** —— 任意子系統內發起註銷，即可全端下線。
- **OAuth2.0認證** —— 輕鬆搭建 OAuth2.0 服務，支持openid模式 。
- **分佈式會話** —— 提供共享數據中心分佈式會話方案。
- **微服務網關鑑權** —— 適配Gateway、ShenYu、Zuul等常見網關的路由攔截認證。
- **RPC調用鑑權** —— 網關轉發鑑權，RPC調用鑑權，讓服務調用不再裸奔
- **臨時Token認證** —— 解決短時間的 Token 授權問題。
- **獨立Redis** —— 將權限緩存與業務緩存分離。
- **Quick快速登錄認證** —— 為項目零代碼注入一個登錄頁面。
- **標籤方言** —— 提供 Thymeleaf 標籤方言集成包，提供 beetl 集成示例。
- **jwt集成** —— 提供三種模式的 jwt 集成方案，提供 token 擴展參數能力。
- **RPC調用狀態傳遞** —— 提供 dubbo、grpc 等集成包，在RPC調用時登錄狀態不丟失。
- **參數簽名** —— 提供跨系統API調用簽名校驗模塊，防參數篡改，防請求重放。
- **自動續簽** —— 提供兩種Token過期策略，靈活搭配使用，還可自動續簽。
- **開箱即用** —— 提供SpringMVC、WebFlux、Solon 等常見框架集成包，開箱即用。
- **最新技術棧** —— 適配最新技術棧：支持 SpringBoot 3.x，jdk 17。

</details>



### 🍃 SSO 單點登錄

Sa-Token SSO 分為三種模式，可解決：`同域、跨域、共享Redis、跨Redis、前後端一體、前後端分離、純 js、vue2、vue3、java 項目、非 java 項目` 等架構下的 SSO 認證需求：

![sa-token-jss](https://sa-token.com/big-file/doc/sso/sa-token-sso--white.png)


| 系統架構						| 採用模式	| 簡介						        |  文檔鏈接	|
| :--------						| :--------	|:----------------| :--------	|
| 前端同域 + 後端同 Redis			| 模式一		| 共享Cookie同步會話			 | [文檔](https://sa-token.com/doc.html#/sso/sso-type1)、[示例](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso1-client)	|
| 前端不同域 + 後端同 Redis		| 模式二		| URL重定向傳播會話 			  | [文檔](https://sa-token.com/doc.html#/sso/sso-type2)、[示例](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso2-client)	|
| 前端不同域 + 後端 不同Redis		| 模式三		| HTTP請求獲取會話			   | [文檔](https://sa-token.com/doc.html#/sso/sso-type3)、[示例](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso3-client)	|


1. 前端同域：就是指多個系統可以部署在同一個主域名之下，比如：`c1.domain.com`、`c2.domain.com`、`c3.domain.com`
2. 後端同 Redis：就是指多個系統可以連接同一個 Redis，共享會話數據。
3. 如果無法做到前端同域、後端同 Redis，可以走託底的模式三：Http請求校驗 ticket 獲取會話。
4. 提供：NoSdk 模式示例 + sso-server 接口文檔，非 Sa-Token 項目、非 java 項目也可以對接。
5. 提供：多重安全校驗：域名校驗、ticket校驗、參數簽名校驗，有效防 ticket 劫持，防請求重放等攻擊。
6. 提供：大量實戰痛點教學：sso-server 前後端分離設計、sso-client 前後端分離設計、用戶數據同步/遷移方案設計。
7. 提供：直接可運行的 demo 示例，助你快速熟悉 SSO 大致登錄流程。
8. 提供：深度細節優化，參數防丟：筆者曾試驗多個SSO框架，均有參數丟失情況，比如登錄前是：`http://a.com?id=1&name=2`，登錄成功後就變成了：`http://a.com?id=1`，Sa-Token-SSO 內有專門算法保證了參數不丟失，登錄成功後精準原路返回。




### 🍂 OAuth2 授權認證
Sa-Token OAuth2 模塊分為四種授權模式，解決不同場景下的授權需求 

| 授權模式					| 簡介						|
| :--------					| :--------					|
| 授權碼式					| OAuth2 標準授權步驟，server 端下放 code，client 端獲取 code 碼兌換 access_token			|
| 隱藏式					| 備用選擇，server 端使用 URL 重定向方式直接將 access_token 下放到 client 端頁面 			|
| 密碼式					| client 直接拿著用戶的賬號密碼換取授權 access_token				|
| 客戶端憑證式				| server 端針對 client 級別的 client_token，代表應用自身的資源授權		|

詳細參考文檔：[https://sa-token.com/doc.html#/oauth2/readme](https://sa-token.com/doc.html#/oauth2/readme)


### 📖❓ 疑問解答

**1、Sa-Token 功能全不全？**

七年磨一劍：五大核心模塊(登錄、鑑權、SSO、OAuth2、微服務) + 眾多實用插件 (短 token、jwt 集成、API 參數簽名、API Key 秘鑰授權...) 我們提供的不只是權限認證，我們提供的是一站式解決方案。


**2、Sa-Token 好不好學？**

中文文檔 + 中文代碼註釋 + 中文交流社區 + 大量實戰案例博客 + 多個視頻教程 + 大量優秀開源項目集成案例。


**3、Sa-Token 用的人多不多？**

截止統計日 (2026-1-25) 起，Sa-Token 在：

- Gitee 關注量達到 48627 Star，位列平臺所有推薦項目排行榜第一名。
- GitHub 關注量達到 18523 Star，是主要競爭框架 Spring Security 的 1.97 倍，Apache Shiro 的 4.19 倍。
- 25+ 微信粉絲群 (500人)，8+ QQ粉絲群 (1000人 or 2000人) ，在線文檔訪問量月PV 20萬+。

這是眾多開發者用腳投票的數據，相信這些數據比任何言語都能證明 Sa-Token 的熱度。


**4、Sa-Token 有哪些權威認證？**

曾獲榮譽包括但不限於：Gitee GVP 最有價值開源項目、GitCode G-Star 優質開源項目、OSCHINA 2021 人氣指數 TOP 30 開源項目、OSCHINA 2022 年度最火熱中國開源項目社區之一、開放原子基金會2023快速成長開源項目、 Dromara 組織頂尖項目（之一）、可信開源社區共同體預備成員、所在開源社區 “Dromara” 榮獲《2024中國互聯網發展創新與投資大賽（開源）》二等獎。 Gitee High Star 計劃項目(5000+star)。Gitee 2025年度開源項目 Web應用開發 Top 2。


**5、Sa-Token 收費嗎？**

Sa-Token 採用 Apache-2.0 開源協議，承諾框架本身與在線文檔永久免費開放。當然如果您有心贊助 Sa-Token，我們也不迴避：[贊助鏈接](https://sa-token.com/doc.html#/more/sa-token-donate)。
我們將定期同步贊助者名單到在線文檔展示。（您需要注意的一點是：該贊助僅為友情贊助，不提供任何商業交換）


**6、Sa-Token 是封裝的 SpringSecurity 嗎？是套殼 ApacheShiro 嗎？**

不是。Sa-Token 不是一個後臺模板，也不是針對 xx 框架的二次封裝套殼，而是從 0 開始的純血自研框架，核心包零依賴，完全自主可控的架構內核 + 眾多主流框架的集成適配。
						



#### 證書 ⭐ 獎盃 🏆 榮譽展示 

<table align="center">
  <tr>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/gvp.jpg" title="GVP - Gitee 最有價值開源項目" /></td>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/g-star.jpg" title="GitCode G-Star 優質開源項目" /></td>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/osc-2021.jpg" title="OSCHINA 2021 人氣指數 TOP 30 開源項目" /></td>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/osc-2022--chang.jpg" title="OSCHINA 2022 年度最火熱中國開源項目社區" /></td>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/kexin.jpg" title="可信開源社區共同體預備成員" /></td>
  </tr>
  <tr>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/gitee-star-5000.jpg" title="Gitee 5000 star 專屬獎盃" /></td>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/gitee-2025--chang.jpg" title="Gitee 2025年度開源項目 Web應用開發 Top 2" /></td>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/dromara.jpg" title="Dromara 組織頂尖項目（之一）" /></td>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/kaifangyuanzi2--chang.jpg" title="開放原子基金會2023快速成長開源項目" /></td>
    <td align="center" width="330"><img src="https://sa-token.com/big-file/index/awards-zip/dromara-2024-tzds.jpg" title="Dromara 榮獲《2024中國互聯網發展創新與投資大賽（開源）》二等獎" /></td>
  </tr>
</table>



### 🚀 優秀開源集成案例

- [[ Snowy ]](https://gitee.com/xiaonuobase/snowy)：國內首個國密前後分離快速開發平臺，採用 Vue3 + Vite + SpringBoot + Mp + HuTool + SaToken。
- [[ RuoYi-Vue-Plus ]](https://gitee.com/dromara/RuoYi-Vue-Plus)：重寫RuoYi-Vue所有功能 集成 Sa-Token、Mybatis-Plus、Xxl-Job、knife4j、OSS 定期同步。
- [[ Smart-Admin ]](https://gitee.com/lab1024/smart-admin)：SmartAdmin 國內首個以「高質量代碼」為核心，「簡潔、高效、安全」中後臺快速開發平臺。
- [[ 橙單 ]](https://gitee.com/orangeform/orange-admin)： 橙單中臺化低代碼生成器。可完整支持多應用、多租戶、多渠道、工作流、框架技術棧自由組合等。
- [[ 燈燈 ]](https://gitee.com/dromara/lamp-cloud)： 專注於多租戶解決方案的中後臺快速開發平臺。支持獨立數據庫、共享數據架構 和 非租戶模式 ✨
- [[ 拾壹博客 ]](https://gitee.com/quequnlong/shiyi-blog)：一款 vue + springboot 前後端分離的博客系統。



還有更多優秀開源案例無法逐一展示，請參考：[Awesome-Sa-Token](https://gitee.com/sa-token/awesome-sa-token)


### 🌍 其它語言版本

Sa-Token 社區成員貢獻了多語言實現版本：

- Rust 版本：[https://github.com/sa-tokens/sa-token-rust](https://github.com/sa-tokens/sa-token-rust)
- Go 版本：[https://github.com/sa-tokens/sa-token-go](https://github.com/sa-tokens/sa-token-go)
- NodeJS 版本：[https://github.com/xiaoLangtou/xlt-token](https://github.com/xiaoLangtou/xlt-token)
- PHP 版本：[https://gitee.com/jinan-jimeng-network_0/real-token](https://gitee.com/jinan-jimeng-network_0/real-token)

我們誠邀對上述語言較為熟練的開發者，一起建設相關版本。🤝


### 🔗 友情鏈接
- [[ OkHttps ]](https://gitee.com/ejlchina-zhxu/okhttps)：輕量級 http 通信框架，API無比優雅，支持 WebSocket、Stomp 協議
- [[ Forest ]](https://gitee.com/dromara/forest)：聲明式與編程式雙修，讓天下沒有難以發送的 HTTP 請求
- [[ Bean Searcher ]](https://github.com/troyzhxu/bean-searcher)：REST 版的 GraphQL — 實體定義邊界，參數驅動查詢，一行代碼實現複雜查詢！
- [[ Jpom ]](https://gitee.com/dromara/Jpom)：簡而輕的低侵入式在線構建、自動部署、日常運維、項目監控軟件。
- [[ TLog ]](https://gitee.com/dromara/TLog)：一個輕量級的分佈式日誌標記追蹤神器。
- [[ hippo4j ]](https://gitee.com/agentart/hippo4j)：強大的動態線程池框架，附帶監控報警功能。
- [[ hertzbeat ]](https://gitee.com/dromara/hertzbeat)：易用友好的開源實時監控告警系統，無需Agent，高性能集群，強大自定義監控能力。
- [[ Solon ]](https://gitee.com/noear/solon)：一個更現代感的應用開發框架：更快、更小、更自由。
- [[ Chat2DB ]](https://github.com/chat2db/Chat2DB)：一個AI驅動的數據庫管理和BI工具，支持Mysql、pg、Oracle、Redis等22種數據庫的管理。



### 📦 代碼託管
- Gitee：[https://gitee.com/dromara/sa-token](https://gitee.com/dromara/sa-token)
- GitHub：[https://github.com/dromara/sa-token](https://github.com/dromara/sa-token)
- AtomGit：[https://atomgit.com/dromara/sa-token](https://atomgit.com/dromara/sa-token)



### 📚 示例大全

**我們為框架幾乎所有技術點均單獨製作了對應的集成示例，此壓縮包共計 60+ Demo**：涵蓋 Sa-Token 登錄認證、權限認證、SSO 單點登錄、OAUth2 統一認證、微服務鑑權、API Key 認證、JWT集成、跨系統調用參數簽名校驗 等鑑權認證的方方面面。

下載地址：[https://sa-token.com/doc.html#/more/download-demos](https://sa-token.com/doc.html#/more/download-demos) 

<img class="s-w" src="https://sa-token.com/big-file/contact/show/sa-token-demos-pre-liubai.png" />



### 💬 交流群
<!-- QQ交流群：685792424 [點擊加入](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=Y05Ld4125W92YSwZ0gA8e3RhG9Q4Vsfx&authKey=IomXuIuhP9g8G7l%2ByfkrRsS7i%2Fna0lIBpkTXxx%2BQEaz0NNEyJq00kgeiC4dUyNLS&noverify=0&group_code=685792424)-->
<!-- QQ交流群：1081649142 [點擊加入](https://qm.qq.com/q/SCAaZ6Ros2) -->

QQ交流群：1098917026 [點擊加入](https://qm.qq.com/q/26OFBGd2Zy) 

微信交流群：

<!-- <img src="https://oss.dev33.cn/sa-token/qr/wx-qr-m-400k.png" width="230px" title="微信群" /> -->

<img src="https://sa-token.com/big-file/contact/i-wx-qr2.jpg" width="230px" title="微信群" />

PS：掃碼添加微信 (備註：sa-token)，邀您加入群聊。

<br>

<img class="s-w" src="https://sa-token.com/big-file/contact/show/wx-group-show3--liubai.png" style="max-width: 50%;" alt="微信群" />


加入群聊的好處：
- 第一時間收到框架更新通知。
- 第一時間收到框架 bug 通知。
- 第一時間收到新增開源案例通知。
- 和眾多大佬一起互相 (huá shuǐ) 交流 (mō yú) 🖐️🐟️。

