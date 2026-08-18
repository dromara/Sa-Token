[简体中文](./README.md) | [繁體中文](./README_zh_TW.md) | [English](./README_en.md) | 日本語 | [한국어](./README_ko.md) | [Русский](./README_ru.md)

<p align="center">
	<img alt="logo" src="https://sa-token.com/logo.png" width="150" height="150">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Sa-Token v1.46.0</h1>
<h4 align="center">✨ オープンソース、無料、ワンストップの Java 権限認証フレームワークで、認証をシンプルかつエレガントにします。 </h4>
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
<!-- <p align="center"> 学習してテストするには、master ブランチをプルしてください。dev は開発ブランチにあります (ルート ディレクトリで `git checkout master` を実行します) </p> -->
<p align="center">
<a href="https://sa-token.com?way=readme" target="_blank"> オンライン ドキュメント: https://sa-token.com</a>
	&nbsp;|&nbsp;
<a href="https://sa-token.com/doc.html#/more/demand-commit" target="_blank">送信リクエスト</a>
</p>


---

### 📝 序文:

私が Sa-Token のコードの最初の行を提出した 2020 年の初めを振り返ると、当時市場に出ていた Java に欠けていたのは、シンプルで使いやすい認証フレームワークだけでなく、明確で自己一貫性のあるパーミッション アーキテクチャの設計アイデアのセットでもありました。

したがって、ここ数年、私は Sa-Token ドキュメントの作成に多くの時間を費やしてきました。ほぼすべての章、すべての文、すべての単語が、最も明確で簡潔でわかりやすい表現を達成するために、繰り返し修正され、細かく磨き上げられています。ドキュメントを注意深く読むと、Sa-Token フレームワーク自体だけでなく、ほとんどのシナリオにおける権限設計のベスト プラクティスについても学ぶことができます。



### 🛠️ SAトークンの紹介

Sa-Token は、軽量の Java 権限認証フレームワークであり、現在、ログイン認証、権限認証、シングル サインオン、OAuth2.0、およびマイクロサービス認証の 5 つのコア モジュールを備えています。

**以下の機能をまだ使用していますか?Stop ⚠️ Sa-Token にお任せを！**

![sa-token-jss](https://sa-token.com/big-file/index/intro/sa-token-jss--tran.png)

SpringBoot プロジェクトで Sa-Token を使用するには、pom.xml に依存関係を導入するだけです。

``` xml
<!-- Sa-Token 機関認証、オンラインドキュメント: https://sa-token.com -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>1.46.0</version>
</dependency>
```

SpringBoot2 と Sa-Token のサポートに加えて、SpringBoot3/4、Solon、JFinal などの一般的な Web フレームワークの統合パッケージも提供しており、すぐに使用できるようになります。


<details>
<summary><b>簡単な表示例:</b> (クリックして展開/折りたたみ)</summary>

Sa-Token は、システムの許可認証部分をシンプルかつエレガントな方法で完了することを目的としています。ログイン認証を例に挙げると、必要なのは以下だけです。

``` java
// セッションログイン。パラメータにはログイン者のアカウントIDが入力されます。
StpUtil.login(10001);
```

インターフェースを実装したり、構成ファイルを作成したりする必要はありません。セッションのログイン認証を完了するには、この静的コードを呼び出すだけです。

インターフェイスにアクセスする前にログインが必要な場合は、次のコードを呼び出すだけで済みます。

``` java
// 現在のクライアントがログインしているかどうかを確認し、ログインしていない場合は `NotLoginException` 例外をスローします
StpUtil.checkLogin();
```

Sa-Token では、ほとんどの関数は 1 行のコードで解決できます。

人々をオフラインに追い出す：

``` java
// アカウント ID 10077 のセッションをオフラインにキックします
StpUtil.kickout(10077);
```

権限認証:

``` java
// アノテーション認証: `user:add` 権限を持つセッションのみがメソッドに入ることができます
@SaCheckPermission("user:add")
public String insert(SysUser user) {
    // ... 
    return "用户增加";
}
```

ルートインターセプト認証:

``` java
// ルートに応じてモジュールを分割し、モジュールごとに認証が異なります
registry.addInterceptor(new SaInterceptor(handler -> {
	SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
	SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
	SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
	SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
	SaRouter.match("/notice/**", r -> StpUtil.checkPermission("notice"));
	// さらにモジュール...
})).addPathPatterns("/**");
```

**以前にShiroまたはSpringSecurityを使用したことがある場合は、Sa-Tokenに切り替えると質的な飛躍を経験するでしょう。**

<!-- Shiro や SpringSecurity などのフレームワークに飽きたら、これらの伝統的な古いフレームワークと比較して、Sa-Token の API 設計がいかにシンプルでエレガントであるかがわかるでしょう。 -->

</details>


<details>
<summary><b>コアモジュールリスト:</b> (クリックして展開/折りたたみ)</summary>

- **ログイン認証** - シングルエンド ログイン、マルチエンド ログイン、同一エンド相互排他ログイン、7 日以内はログイン不要。
- **権限認証**——権限認証、ロール認証、セッション二次認証。
- **ユーザーをオフラインにキックする** - アカウント ID に基づいてユーザーをオフラインにキックし、トークンの値に基づいてユーザーをオフラインにキックします。
- **アノテーション認証** - 認証をビジネス コードからエレガントに分離します。
- **ルート インターセプト認証** - ルート インターセプト認証に基づいて、Restful モードを適応させることができます。
- **セッション** - すべてのエンドの共有セッション、一方のエンドの排他的セッション、カスタマイズされたセッション、値への便利なアクセス。
- **永続層拡張機能** - Redis を統合でき、再起動後にデータが失われることはありません。
- **フロントエンドとバックエンドの分離** - Cookie をサポートしていない APP、アプレット、その他の端末も簡単に認証できます。
- **トークン スタイルのカスタマイズ** - 6 つの組み込みトークン スタイル。トークン生成戦略をカスタマイズすることもできます。
- **Remember Me モード** - [Remember Me] モードに適応し、検証せずにブラウザを再起動します。
- **第 2 レベルの認証** - セキュリティを確保するために、ログインに基づいて再度認証を行います。
- **他の人のアカウントを模倣** - ユーザー ステータス データをリアルタイムで操作します。
- **一時的な ID 切り替え** - セッション ID を別のアカウントに一時的に切り替えます。
- **同一エンドの相互排他ログイン** - QQ と同様、携帯電話とコンピュータは同時にオンラインですが、ログインは両方の携帯電話で相互排他的です。
- **アカウント禁止** - ログイン禁止、ビジネス分類禁止、およびペナルティラダー禁止。
- **パスワード暗号化** - MD5、SHA1、SHA256、および AES を迅速に暗号化できる基本的な暗号化アルゴリズムを提供します。
- **セッション クエリ** - 便利で柔軟なセッション クエリ インターフェイスを提供します。
- **HTTP 基本認証** - HTTP 基本認証およびダイジェスト認証にアクセスするための 1 行のコード。
- **グローバル リスナー** - ユーザーのログイン、ログアウト、オフラインへのキックなどの主要な操作中に、一部の AOP 操作を実行します。
- **グローバル フィルター** - クロスドメイン、最初のセキュリティ応答のグローバル設定、およびその他の操作を便利に処理します。
- **複数アカウント システム認証** - 1 つのシステム内の複数のアカウントは個別に認証されます (モールのユーザー テーブルと管理者テーブルなど)
- **シングル サインオン** - 3 つのシングル サインオン モードが組み込まれています: 同一ドメイン、クロスドメイン、同一 Redis、クロス Redis、フロントエンドとバックエンドの分離、およびその他のアーキテクチャを処理できます。
- **シングル ポイント ログアウト** - 任意のサブシステムでログアウトを開始すると、システム全体がオフラインになります。
- **OAuth2.0 認証** - openid モードをサポートする OAuth2.0 サービスを簡単に構築します。
- **分散セッション** - 共有データセンターで分散セッション ソリューションを提供します。
- **マイクロサービス ゲートウェイ認証** - Gateway、ShenYu、Zuul などの一般的なゲートウェイのルート インターセプト認証に適合します。
- **RPC 呼び出し認証** - ゲートウェイ転送認証、RPC 呼び出し認証により、サービス呼び出しが無防備なままにならないようにします。
- **一時的なトークン認証** - 短期的なトークン認証の問題を解決します。
- **独立した Redis** - 権限キャッシュとビジネス キャッシュを分離します。
- **Quick クイックログイン認証** - ゼロコードでログイン ページをプロジェクトに挿入します。
- **タグ方言** - Thymeleaf タグ方言統合パッケージを提供し、beetl 統合サンプルを提供します。
- **jwt 統合** - jwt 統合ソリューションの 3 つのモードを提供し、トークン拡張パラメータ機能を提供します。
- **RPC 呼び出し状態の転送** - RPC 呼び出し時にログイン状態が失われないように、dubbo や grpc などの統合パッケージを提供します。
- **パラメータ署名** - パラメータの改ざんとリクエストのリプレイを防止するために、クロスシステム API 呼び出し署名検証モジュールを提供します。
- **自動更新** - 柔軟に使用でき、自動的に更新できる 2 つのトークン有効期限戦略を提供します。
- **すぐに使える** - SpringMVC、WebFlux、Solon など、すぐに使用できる一般的なフレームワーク統合パッケージを提供します。
- **最新のテクノロジー スタック** - 最新のテクノロジー スタックに適応: SpringBoot 3.x、jdk 17 をサポートします。

</details>



### 🍃 SSO シングル サインオン

Sa-Token SSO は 3 つのモードに分かれており、`同域、跨域、共享Redis、跨Redis、前后端一体、前后端分离、纯 js、vue2、vue3、java 项目、非 java 项目` およびその他のアーキテクチャでの SSO 認証要件を解決できます。

![sa-token-jss](https://sa-token.com/big-file/doc/sso/sa-token-sso--white.png)


|システムアーキテクチャ |導入パターン |はじめに |ドキュメントへのリンク |
| :--------						| :--------	|:----------------| :--------	|
|同じドメイン内のフロントエンド + Redis と同じドメイン内のバックエンド |モード 1 |共有 Cookie 同期セッション | [文書](https://sa-token.com/doc.html#/sso/sso-type1)、[例](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso1-client) |
|フロントエンドの異なるドメイン + Redis と同じバックエンド |モード 2 | URL リダイレクト伝播セッション | [文書](https://sa-token.com/doc.html#/sso/sso-type2)、[例](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso2-client) |
|異なるフロントエンド ドメイン + 異なるバックエンド Redis |モード 3 |セッションを取得するための HTTP リクエスト | [文書](https://sa-token.com/doc.html#/sso/sso-type3)、[例](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso3-client) |


1. フロントエンドの同じドメイン: `c1.domain.com`、`c2.domain.com`、`c3.domain.com` など、同じメイン ドメイン名の下に複数のシステムを展開できることを意味します。
2. バックエンドは Redis と同じです。これは、複数のシステムが同じ Redis に接続してセッション データを共有できることを意味します。
3. フロントエンドが同じドメイン内にあり、バックエンドが同じ Redis 内にあることを確認できない場合は、3 番目のサポート モードである Http リクエスト検証チケットを使用してセッションを取得できます。
4. 提供: NoSdk モードのサンプル + sso サーバー インターフェイスのドキュメント、非 Sa-Token プロジェクトおよび非 Java プロジェクトも接続できます。
5. 複数のセキュリティ検証を提供します: ドメイン名検証、チケット検証、パラメータ署名検証、チケットハイジャック、リクエストリプレイ、その他の攻撃を効果的に防止します。
6. 提供: 実践的な問題点に関する多数の教示: sso サーバーのフロントエンドとバックエンドの分離設計、sso クライアントのフロントエンドとフロントエンドの分離設計、ユーザー データの同期/移行ソリューションの設計。
7. 提供: 一般的な SSO ログイン プロセスにすぐに慣れるのに役立つ、直接実行可能なデモ サンプル。
8. 提供: 詳細な最適化、パラメーター損失の防止: 著者は複数の SSO フレームワークをテストしましたが、すべてのパラメーターが失われました。たとえば、ログイン前は `http://a.com?id=1&name=2` でしたが、ログインに成功すると `http://a.com?id=1` になりました。 Sa-Token-SSO には、パラメーターが失われないようにする特別なアルゴリズムがあり、ログイン成功後に元のパスが正確に返されます。




### 🍂 OAuth2認可認証
Sa-Token OAuth2 モジュールは、さまざまなシナリオでの承認ニーズを解決するために 4 つの承認モードに分割されています。

|認可モデル |はじめに |
| :--------					| :--------					|
|認証コードの形式 | OAuth2 標準の認証手順では、サーバー側がコードを解放し、クライアント側がコード code を取得して、それを access_token | と引き換えます。
|非表示 |代替オプションとして、サーバーは URL リダイレクトを使用して、access_token をクライアント ページに直接転送します。
|パスワードの種類 |クライアントは、認証のためにユーザーのアカウントのパスワードを直接交換します。
|クライアント証明書の種類 |クライアント レベルのサーバー側 client_token は、アプリケーション自体のリソース認可を表します。

詳細な参考資料: [https://sa-token.com/doc.html#/oauth2/readme](https://sa-token.com/doc.html#/oauth2/readme)


### 📖❓ Q&A

**1. Sa-Token の機能は十分ですか？**

7 年間の努力: 5 つのコア モジュール (ログイン、認証、SSO、OAuth2、マイクロサービス) + 多くの実用的なプラグイン (ショート トークン、jwt 統合、API パラメーターの署名、API キー認証...) 権限認証だけでなく、ワンストップ ソリューションを提供します。


**2. Sa-Tokenは簡単に習得できますか?**

中国語のドキュメント + 中国語のコード コメント + 中国語のコミュニケーション コミュニティ + 多数の実践的な事例ブログ + 複数のビデオ チュートリアル + 多数の優れたオープンソース プロジェクト統合事例。


**3. Sa-Tokenを使っている人は多いのでしょうか？**

統計日 (2026-1-25) の時点で、Sa-Token は次のとおりです。

- Gitee フォロワーは 48627 スターに達し、プラットフォーム上のすべての推奨プロジェクトのリストで 1 位にランクされました。
- GitHub の注目度は 18523 スターに達し、これは主要な競合フレームワーク Spring Security の 1.97 倍、Apache Taro の 4.19 倍です。
- 25 個以上の WeChat ファン グループ (500 人)、8 個以上の QQ ファン グループ (1000 人または 2000 人)、オンライン ドキュメント アクセス (月間 PV は 200,000 人以上)。

これは多くの開発者が自分の足で投票したデータです。これらのデータがSa-Tokenの人気をどんな言葉よりも証明できると信じています。


**4. Sa-Token はどのような権威ある認証を持っていますか?**

栄誉には、Gitee GVP Most Valuable オープンソース プロジェクト、GitCode G-Star 高品質オープンソース プロジェクト、OSCHINA 2021 人気指数 TOP 30 オープンソース プロジェクト、OSCHINA 2022 中国で最も人気のあるオープンソース プロジェクト コミュニティの 1 つ、Open Atomic Foundation 2023 急成長オープンソース プロジェクト、Dromara Organization トップ プロジェクト (One)、Trusted Open Source Community Community の準備メンバー、および自身が活動するオープンソースコミュニティ「Dromara」「2024年中国インターネット発展イノベーション・投資コンペティション（オープンソース）」で準優勝を受賞。 Gitee ハイスター プロジェクト (5000+スター)。 2025 年の Web アプリケーション開発における Gitee のトップ 2 オープンソース プロジェクト。


**5. Sa-Tokenには料金がかかりますか?**

Sa-Token は、Apache-2.0 オープン ソース プロトコルを採用し、フレームワーク自体とオンライン ドキュメントが常に無料でオープンであることを約束します。もちろん、Sa-Token のスポンサーに興味がある場合は、遠慮はしません: [スポンサーリンク](https://sa-token.com/doc.html#/more/sa-token-donate)。
スポンサーリストをオンラインドキュメント表示と定期的に同期させます。 (注意すべき点が 1 つあります。このスポンサーシップは単なる友好的なスポンサーシップであり、商業的な交流を提供するものではありません)


**6. Sa-Token はカプセル化された SpringSecurity ですか? ApacheShiroのシェルでしょうか？**

いいえ。 Sa-Token はバックエンド テンプレートでも、xx フレームワークの二次カプセル化シェルでもありません。コア パッケージへの依存関係がなく、完全に独立した制御可能なアーキテクチャ コア + 多くの主流フレームワークの統合適応を備えた、ゼロから開始する純粋な自社開発フレームワークです。
						



#### 証明書 ⭐ トロフィー 🏆 栄誉の展示

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



### 🚀 優れたオープンソース統合事例

- [[ Snowy ]](https://gitee.com/xiaonuobase/snowy): Vue3 + Vite + SpringBoot + MP + HuTool + SaToken を使用して、国家機密を前後に分離する中国初の高速開発プラットフォーム。
- [[ RuoYi-Vue-Plus ]](https://gitee.com/dromara/RuoYi-Vue-Plus): RuoYi-Vue のすべての機能を書き直し、定期的な同期のために Sa-Token、Mybatis-Plus、Xxl-Job、knife4j、OSS を統合します。
- [[ Smart-Admin ]](https://gitee.com/lab1024/smart-admin): SmartAdmin は、「高品質のコード」をコアとして、「シンプル、効率的、安全」なミドルエンドとバックエンドを備えた初の国内迅速開発プラットフォームです。
- [[ Chengdan ]](https://gitee.com/orangeform/orange-admin): 台湾の Chengdan のローコード ジェネレーター。マルチアプリケーション、マルチテナント、マルチチャネル、ワークフロー、フレームワーク技術スタックの自由な組み合わせなどを完全にサポートできます。
- [[ Deng Deng ]](https://gitee.com/dromara/lamp-cloud): マルチテナント ソリューションに焦点を当てたミッドエンドおよびバックエンドの迅速な開発プラットフォーム。独立したデータベース、共有データ アーキテクチャ、非テナント モードをサポートします ✨
- [[ Shiyi Blog ]](https://gitee.com/quequnlong/shiyi-blog): フロントエンドとバックエンドを分離した vue + springboot ブログ システム。



1つずつ表示しきれない優れたオープンソース事例は他にもあります。[Awesome-Sa-Token](https://gitee.com/sa-token/awesome-sa-token)を参照してください。


### 🌍 他の言語バージョン

Sa-Token コミュニティのメンバーは、多言語実装バージョンを提供しました。

- Rust バージョン: [https://github.com/sa-tokens/sa-token-rust](https://github.com/sa-tokens/sa-token-rust)
- Go バージョン: [https://github.com/sa-tokens/sa-token-go](https://github.com/sa-tokens/sa-token-go)
- NodeJS バージョン: [https://github.com/xiaoLangtou/xlt-token](https://github.com/xiaoLangtou/xlt-token)
- PHP バージョン: [https://gitee.com/jinan-jimeng-network_0/real-token](https://gitee.com/jinan-jimeng-network_0/real-token)

上記の言語に精通した開発者を心から招待し、関連するバージョンを一緒に構築してください。 🤝


### 🔗 フレンドリーリンク
- [[ OkHttps ]](https://gitee.com/ejlchina-zhxu/okhttps): 軽量の http 通信フレームワーク、非常にエレガントな API、WebSocket および Stomp プロトコルをサポート
- [[ Forest ]](https://gitee.com/dromara/forest): 宣言型とプログラム型の二重栽培により、HTTP リクエストの送信が難しくなくなりました。
- [[ Bean Searcher ]](https://github.com/troyzhxu/bean-searcher): GraphQL の REST バージョン — エンティティ定義境界、パラメーター駆動クエリ、および複雑なクエリを実装するための 1 行のコード!
- [[ Jpom ]](https://gitee.com/dromara/Jpom): シンプルで軽量、低侵入型のオンライン構築、自動展開、日常の運用と保守、およびプロジェクト監視ソフトウェア。
- [[ TLog ]](https://gitee.com/dromara/TLog): 軽量の分散ログタグ追跡アーティファクト。
- [[ hippo4j ]](https://gitee.com/agentart/hippo4j): 監視およびアラーム機能を備えた強力な動的スレッド プール フレームワーク。
- [[ hertzbeat ]](https://gitee.com/dromara/hertzbeat): 使いやすくフレンドリーなオープンソースのリアルタイム監視および警報システム、エージェント不要、高性能クラスター、強力なカスタム監視機能。
- [[ Solon ]](https://gitee.com/noear/solon): より現代的なアプリケーション開発フレームワーク: より速く、より小さく、より無料。
- [[ Chat2DB ]](https://github.com/chat2db/Chat2DB): Mysql、pg、Oracle、Redis など 22 のデータベースの管理をサポートする AI 主導のデータベース管理および BI ツール。



### 📦 コードホスティング
- Gitee：[https://gitee.com/dromara/sa-token](https://gitee.com/dromara/sa-token)
- GitHub：[https://github.com/dromara/sa-token](https://github.com/dromara/sa-token)
- AtomGit：[https://atomgit.com/dromara/sa-token](https://atomgit.com/dromara/sa-token)



### 📚 事例集

**フレームワークのほぼすべての技術点に対応する統合例を別途作成しました。この圧縮パッケージには合計 60 以上のデモ**が含まれており、Sa-Token ログイン認証、権限認証、SSO シングル サインオン、OAUth2 統合認証、マイクロサービス認証、API キー認証、JWT 統合、システム間呼び出しパラメータ署名検証など、認証と認証のあらゆる側面をカバーしています。

ダウンロードアドレス: [https://sa-token.com/doc.html#/more/download-demos](https://sa-token.com/doc.html#/more/download-demos)

<img class="s-w" src="https://sa-token.com/big-file/contact/show/sa-token-demos-pre-liubai.png" />



### 💬コミュニケーショングループ
<!-- QQ コミュニケーション グループ: 685792424 [クリックして参加](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=Y05Ld4125W92YSwZ0gA8e3RhG9Q4Vsfx&authKey=IomXuIuhP9g8G7l%2ByfkrRsS7i%2Fna0lIBpkTXxx%2BQEaz0NNEyJq00kgeiC4dUyNLS&noverify=0&group_code=685792424)-->
<!-- QQ コミュニケーション グループ: 1081649142 [クリックして参加](https://qm.qq.com/q/SCAaZ6Ros2) -->

QQ コミュニケーション グループ: 1098917026 [クリックして参加](https://qm.qq.com/q/26OFBGd2Zy)

WeChatコミュニケーショングループ:

<!-- <img src="https://oss.dev33.cn/sa-token/qr/wx-qr-m-400k.png" width="230px" title="微信群" /> -->

<img src="https://sa-token.com/big-file/contact/i-wx-qr2.jpg" width="230px" title="微信群" />

PS: QR コードをスキャンして WeChat (注釈: sa-token) を追加し、グループ チャットへの参加を招待します。

<br>

<img class="s-w" src="https://sa-token.com/big-file/contact/show/wx-group-show3--liubai.png" style="max-width: 50%;" alt="微信群" />


グループチャットに参加するメリット:
- フレームワークの更新通知をできるだけ早く受け取ります。
- フレームワークのバグ通知をできるだけ早く受け取ります。
- 新しいオープンソース ケースの通知をできるだけ早く受け取ります。
- 多くの偉い人たち 🖐️🐟️ とお互いに (mō yú) コミュニケーション (huá shuiq) しましょう。

