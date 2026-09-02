[简体中文](./README.md) | [繁體中文](./README_zh_TW.md) | [English](./README_en.md) | [日本語](./README_ja.md) | 한국어 | [Русский](./README_ru.md)

<p align="center">
	<img alt="logo" src="https://sa-token.com/logo.png" width="150" height="150">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Sa-Token v1.46.0</h1>
<h4 align="center">✨ 오픈 소스, 무료, 원스톱 Java 권한 인증 프레임워크로 인증을 간단하고 우아하게 만듭니다! </h4>
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
<!-- <p align="center"> 학습 및 테스트를 위해 마스터 브랜치를 당겨주세요. dev는 개발 브랜치에 있습니다(루트 디렉터리에서 `git checkout master` 실행)</p> -->
<p align="center">
<a href="https://sa-token.com?way=readme" target="_blank">온라인 문서: https://sa-token.com</a>
	&nbsp;|&nbsp;
<a href="https://sa-token.com/doc.html#/more/demand-commit" target="_blank">요구 사항 제출</a>
</p>


---

### 📝 서문:

2020년 초를 되돌아보면 Sa-Token의 첫 번째 코드 라인을 제출했을 때 **당시 시장에 출시된 Java에는 간단하고 사용하기 쉬운 인증 프레임워크뿐 아니라 명확하고 일관된 권한 아키텍처 설계 사상이 부족했습니다**.

따라서 지난 몇 년 동안 저는 Sa-Token 문서를 작성하는 데 많은 시간을 투자했습니다. 가장 명확하고 간결하며 이해하기 쉬운 표현을 얻기 위해 거의 모든 장, 모든 문장, 모든 단어가 반복적으로 수정되고 세밀하게 다듬어졌습니다. **문서를 주의 깊게 읽으면 Sa-Token 프레임워크 자체뿐만 아니라 대부분의 시나리오에서 권한 인증 설계에 대한 모범 사례도 배울 수 있습니다**.



### 🛠️ Sa-Token 소개

Sa-Token은 현재 로그인 인증, 권한 인증, Single Sign-On, OAuth2.0 및 마이크로서비스 인증의 5가지 핵심 모듈이 있는 경량 Java 권한 인증 프레임워크입니다.

아직도 다음 기능을 손수 만들고 계신가요? **Stop, Sa-Token에게 맡기세요!**

![sa-token-jss](https://sa-token.com/big-file/index/intro/sa-token-jss--tran.png)

SpringBoot 프로젝트에서 Sa-Token을 사용하려면 pom.xml에 종속성을 도입하기만 하면 됩니다.

``` xml
<!-- Sa-Token 권한 인증, 온라인 문서: https://sa-token.com -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot-starter</artifactId>
	<version>1.46.0</version>
</dependency>
```

SpringBoot2 및 Sa-Token을 지원하는 것 외에도 SpringBoot3/4, Solon 및 JFinal과 같은 일반적인 웹 프레임워크에 대한 통합 패키지도 제공하므로 즉시 사용할 수 있습니다.


<details>
<summary><b>간단한 디스플레이 예:</b> (확장/축소하려면 클릭)</summary>

Sa-Token은 간단하고 우아한 방식으로 시스템의 권한 인증 부분을 완성하는 것을 목표로 합니다. 로그인 인증을 예로 들면 다음만 필요합니다.

``` java
// 세션 로그인, 매개변수는 로그인하는 사람의 계정 ID로 채워집니다.
StpUtil.login(10001);
```

인터페이스를 구현하거나 구성 파일을 만들 필요가 없습니다. 세션 로그인 인증을 완료하려면 이 정적 코드만 호출하면 됩니다.

인터페이스에 액세스하기 전에 로그인이 필요한 경우 다음 코드만 호출하면 됩니다.

``` java
// 현재 클라이언트가 로그인되어 있는지 확인하십시오. 로그인되어 있지 않으면 `NotLoginException` 예외가 발생합니다.
StpUtil.checkLogin();
```

Sa-Token에서는 대부분의 기능을 한 줄의 코드로 해결할 수 있습니다.

사람들을 오프라인으로 몰아내기:

``` java
// 계정 ID 10077로 오프라인에서 세션 시작
StpUtil.kickout(10077);
```

권한 인증:

``` java
// 주석 인증: `user:add` 권한이 있는 세션만 메서드에 들어갈 수 있습니다.
@SaCheckPermission("user:add")
public String insert(SysUser user) {
    // ... 
    return "用户增加";
}
```

경로 차단 인증:

``` java
// 경로에 따라 모듈을 나누고, 모듈마다 인증이 다릅니다.
registry.addInterceptor(new SaInterceptor(handler -> {
	SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
	SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
	SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
	SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
	SaRouter.match("/notice/**", r -> StpUtil.checkPermission("notice"));
	// 더 많은 모듈...
})).addPathPatterns("/**");
```

**이전에 Shiro 또는 SpringSecurity를 사용한 적이 있다면 Sa-Token으로 전환한 후 질적 도약을 경험하게 될 것입니다.**

<!-- Shiro 및 SpringSecurity와 같은 프레임워크에 싫증이 나면 Sa-Token의 API 디자인이 이러한 기존 프레임워크와 비교하여 얼마나 단순하고 우아한지 이해하게 될 것입니다! -->

</details>


<details>
<summary><b>핵심 모듈 개요:</b> (확장/축소하려면 클릭)</summary>

- **로그인 인증** - 단일 엔드 로그인, 멀티 엔드 로그인, 동일 엔드 상호 배타적 로그인, 7일 이내에는 로그인이 필요하지 않습니다.
- **권한 인증**——권한 인증, 역할 인증, 세션 보조 인증.
- **오프라인 추방** - 계정 ID를 기준으로 사람들을 오프라인으로 추방하고 토큰 가치를 기준으로 사람들을 오프라인으로 추방합니다.
- **주석 인증** - 인증을 비즈니스 코드와 우아하게 분리합니다.
- **경로차단 인증** - 경로차단 인증을 기반으로 Restful 모드를 적용할 수 있습니다.
- **세션** - 모든 끝을 위한 공유 세션, 한쪽 끝을 위한 독점 세션, 맞춤형 세션, 가치에 대한 편리한 접근.
- **지속성 레이어 확장** - Redis를 통합할 수 있으며 다시 시작해도 데이터가 손실되지 않습니다.
- **프런트엔드와 백엔드 분리** - 쿠키를 지원하지 않는 앱, 애플릿, 기타 단말기도 쉽게 인증할 수 있습니다.
- **토큰 스타일 사용자 정의** - 6가지 기본 제공 토큰 스타일, 토큰 생성 전략을 사용자 정의할 수도 있습니다.
- **기억하기 모드** - [기억하기] 모드에 적응하고 확인 없이 브라우저를 다시 시작합니다.
- **2차 인증** - 보안 확보를 위해 로그인 기반으로 다시 인증합니다.
- **다른 사람의 계정을 모방** - 사용자 상태 데이터를 실시간으로 조작합니다.
- **임시 ID 전환** - 세션 ID를 일시적으로 다른 계정으로 전환합니다.
- **동일 엔드 상호 배타적 로그인** - QQ와 마찬가지로 휴대폰과 컴퓨터가 동시에 온라인 상태이지만 로그인은 두 휴대폰 모두에서 상호 배타적입니다.
- **계정 차단** - 로그인 차단, 업종 분류 차단, 페널티 래더 차단.
- **비밀번호 암호화** - MD5, SHA1, SHA256, AES를 빠르게 암호화할 수 있는 기본 암호화 알고리즘을 제공합니다.
- **세션 쿼리** - 편리하고 유연한 세션 쿼리 인터페이스를 제공합니다.
- **Http 기본 인증** - Http 기본 및 다이제스트 인증에 액세스하기 위한 코드 한 줄입니다.
- **전역 리스너** - 사용자 로그인, 로그아웃, 오프라인 추방과 같은 주요 작업 중에 일부 AOP 작업을 수행합니다.
- **글로벌 필터** - 크로스 도메인, 보안 대응 우선 글로벌 설정 및 기타 작업을 편리하게 처리합니다.
- **다중 계정 시스템 인증** - 하나의 시스템에서 여러 계정을 별도로 인증합니다. (몰의 사용자 테이블, 관리 테이블 등)
- **Single Sign-On** - 3가지 Single Sign-On 모드 내장: 동일한 도메인, 교차 도메인, 동일한 Redis, 교차 Redis, 프런트엔드 및 백엔드 분리 및 기타 아키텍처를 처리할 수 있습니다.
- **단일 지점 로그아웃** - 모든 하위 시스템에서 로그아웃을 시작하면 전체 시스템이 오프라인이 됩니다.
- **OAuth2.0 인증** - openid 모드를 지원하는 OAuth2.0 서비스를 쉽게 구축할 수 있습니다.
- **분산 세션** - 공유 데이터 센터에서 분산 세션 솔루션을 제공합니다.
- **마이크로서비스 게이트웨이 인증** - Gateway, ShenYu, Zuul 등과 같은 일반 게이트웨이의 경로 차단 인증에 적합합니다.
- **RPC 호출 인증** - 게이트웨이 전달 인증, RPC 호출 인증을 통해 서비스 호출이 더 이상 무방비 상태가 되지 않도록 함
- **임시 토큰 인증** - 단기 토큰 인증 문제를 해결합니다.
- **독립적인 Redis** - 권한 캐시와 비즈니스 캐시를 분리합니다.
- **Quick 빠른 로그인 인증** - 제로 코드로 프로젝트에 로그인 페이지를 삽입합니다.
- **Tag Dialect** - Thymeleaf 태그 Dialect 통합 패키지를 제공하고 beetl 통합 예제를 제공합니다.
- **jwt 통합** - jwt 통합 솔루션의 세 가지 모드를 제공하고 토큰 확장 매개변수 기능을 제공합니다.
- **RPC 호출 상태 전송** - RPC 호출 시 로그인 상태가 손실되지 않도록 dubbo, grpc 등의 통합 패키지를 제공합니다.
- **매개변수 서명** - 매개변수 변조를 방지하고 재생을 요청하기 위해 시스템 간 API 호출 서명 확인 모듈을 제공합니다.
- **자동 갱신** - 유연하게 자동으로 갱신할 수 있는 두 가지 토큰 만료 전략을 제공합니다.
- **즉시 사용 가능** - 즉시 사용 가능한 SpringMVC, WebFlux, Solon 등과 같은 공통 프레임워크 통합 패키지를 제공합니다.
- **최신 기술 스택** - 최신 기술 스택에 적응: SpringBoot 3.x, jdk 17을 지원합니다.

</details>



### 🍃 SSO 싱글 사인온

Sa-Token SSO는 `同域、跨域、共享Redis、跨Redis、前后端一体、前后端分离、纯 js、vue2、vue3、java 项目、非 java 项目`과 같은 아키텍처에서 SSO 인증 요구 사항을 해결할 수 있는 세 가지 모드로 구분됩니다.

![sa-token-jss](https://sa-token.com/big-file/doc/sso/sa-token-sso--white.png)


| 시스템 아키텍처 | 채택 패턴 | 소개 | 문서 링크 |
| :--------						| :--------	|:----------------| :--------	|
| Redis와 동일한 도메인의 프런트엔드 + 동일한 도메인의 백엔드 | 모드 1 | 공유 쿠키 동기화 세션 | [문서](https://sa-token.com/doc.html#/sso/sso-type1), [예제](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso1-client) |
| 프런트 엔드의 다른 도메인 + Redis와 동일한 백엔드 | 모드 2 | URL 리디렉션 전파 세션 | [문서](https://sa-token.com/doc.html#/sso/sso-type2), [예제](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso2-client) |
| 다양한 프런트엔드 도메인 + 다양한 백엔드 Redis | 모드 3 | 세션을 얻기 위한 HTTP 요청 | [문서](https://sa-token.com/doc.html#/sso/sso-type3), [예제](https://gitee.com/dromara/sa-token/blob/master/sa-token-demo/sa-token-demo-sso3-client) |


1. 프런트엔드 동일 도메인: `c1.domain.com`, `c2.domain.com`, `c3.domain.com`와 같이 동일한 기본 도메인 이름으로 여러 시스템을 배포할 수 있음을 의미합니다.
2. 백엔드는 Redis와 동일합니다. 즉, 여러 시스템이 동일한 Redis에 연결하고 세션 데이터를 공유할 수 있음을 의미합니다.
3. 프런트엔드가 동일한 도메인에 있고 백엔드가 동일한 Redis에 있는지 확인할 수 없는 경우 세 번째 지원 모드인 Http 요청 확인 티켓을 사용하여 세션을 얻을 수 있습니다.
4. 제공 : NoSdk 모드 예시 + sso-server 인터페이스 문서, Sa-Token이 아닌 프로젝트, Java가 아닌 프로젝트도 연결 가능합니다.
5. 제공: 다중 보안 확인: 도메인 이름 확인, 티켓 확인, 매개변수 서명 확인, 티켓 하이재킹, 요청 재생 및 기타 공격을 효과적으로 방지합니다.
6. 제공: 다양한 실제 문제점 교육: sso-서버 프런트엔드 및 백엔드 분리 설계, sso-클라이언트 프런트엔드 및 프런트엔드 분리 설계, 사용자 데이터 동기화/마이그레이션 솔루션 설계.
7. 제공: 일반 SSO 로그인 프로세스에 빠르게 익숙해지는 데 도움이 되는 직접 실행 가능한 데모 예제입니다.
8. 제공: 심층적인 세부 최적화, 매개변수 손실 방지: 저자는 여러 SSO 프레임워크를 테스트했으며 모든 매개변수가 손실되었습니다. 예를 들어, 로그인하기 전에는 `http://a.com?id=1&name=2`이었고, 로그인에 성공한 후에는 `http://a.com?id=1`이 되었습니다. Sa-Token-SSO에는 매개변수가 손실되지 않고 로그인 성공 후 원래 경로가 정확하게 반환되도록 보장하는 특별한 알고리즘이 있습니다.




### 🍂 OAuth2 인증 인증
Sa-Token OAuth2 모듈은 다양한 시나리오의 인증 요구 사항을 해결하기 위해 4가지 인증 모드로 구분됩니다.

| 인증 모델 | 소개 |
| :--------					| :--------					|
| 인증 코드 형식 | OAuth2 표준 인증 단계에서 서버측은 코드를 릴리스하고 클라이언트측은 코드 코드를 획득하여 access_token |
| 숨겨진 | 대체 옵션으로, 서버는 URL 리디렉션을 사용하여 access_token을 클라이언트 페이지로 직접 전송합니다. |
| 비밀번호 유형 | 클라이언트는 인증을 위해 사용자의 계정 비밀번호를 직접 교환합니다 access_token |
| 클라이언트 인증서 유형 | 클라이언트 레벨의 서버측 client_token은 애플리케이션 자체의 자원 인증을 나타냅니다. |

자세한 참조 문서: [https://sa-token.com/doc.html#/oauth2/readme](https://sa-token.com/doc.html#/oauth2/readme)


### 📖❓Q&A

**1. Sa-Token 기능은 충분한가요?**

7년간의 노력: 5개의 핵심 모듈(로그인, 인증, SSO, OAuth2, 마이크로서비스) + 많은 실용적인 플러그인(짧은 토큰, jwt 통합, API 매개변수 서명, API 키 인증...) 권한 인증뿐만 아니라 원스톱 솔루션을 제공합니다.


**2. Sa-Token은 배우기 쉬운가요?**

중국어 문서 + 중국어 코드 주석 + 중국어 커뮤니케이션 커뮤니티 + 수많은 실제 사례 블로그 + 여러 비디오 튜토리얼 + 수많은 우수한 오픈 소스 프로젝트 통합 사례.


**3. Sa-Token을 사용하는 사람들이 많이 있나요?**

통계일(2026-1-25) 현재 Sa-Token의 위치는 다음과 같습니다.

- Gitee 팔로어는 48627개의 별에 도달하여 플랫폼의 모든 추천 프로젝트 목록에서 1위를 차지했습니다.
- GitHub 관심은 18523 Stars에 도달했는데, 이는 주요 경쟁 프레임워크인 Spring Security의 1.97배, Apache Shiro의 4.19배입니다.
- 25개 이상의 WeChat 팬 그룹(500명), 8개 이상의 QQ 팬 그룹(1000명 또는 2000명), 월간 PV 200,000+ 이상의 온라인 문서 방문.

많은 개발자들이 발로 투표한 데이터입니다. 나는 이 데이터가 어떤 말보다 Sa-Token의 인기를 더 잘 증명할 수 있다고 믿습니다.


**4. Sa-Token은 어떤 권위 있는 인증을 보유하고 있나요?**

명예에는 Gitee GVP 최우수 오픈 소스 프로젝트, GitCode G-Star 고품질 오픈 소스 프로젝트, OSCHINA 2021 인기 지수 TOP 30 오픈 소스 프로젝트, OSCHINA 2022 가장 인기 있는 중국 오픈 소스 프로젝트 커뮤니티 중 하나, Open Atomic Foundation 2023 빠르게 성장하는 오픈 소스 프로젝트, Dromara 조직 상위 프로젝트(1개), 신뢰할 수 있는 오픈 소스 커뮤니티의 예비 회원 및 그가 활동하고 있는 오픈소스 커뮤니티 'Dromara'가 '2024년 중국 인터넷 발전 혁신 및 투자 대회(오픈소스)'에서 2등상을 수상했습니다. Gitee High Star 프로젝트(5000+star). 2025년 웹 애플리케이션 개발을 위한 Gitee의 상위 2개 오픈 소스 프로젝트입니다.


**5. Sa-Token에는 요금이 부과되나요?**

Sa-Token은 Apache-2.0 오픈 소스 프로토콜을 채택하고 프레임워크 자체와 온라인 문서가 항상 무료이고 개방적임을 약속합니다. 물론 Sa-Token 후원에 관심이 있으시면 [후원 링크](https://sa-token.com/doc.html#/more/sa-token-donate)를 주저하지 않으셔도 됩니다.
우리는 후원자 목록을 온라인 문서 표시와 정기적으로 동기화합니다. (주의하셔야 할 점은 본 후원은 우호적인 후원일 뿐 어떠한 상업적인 교류도 제공하지 않는다는 점입니다)


**6. Sa-Token은 캡슐화된 SpringSecurity입니까? ApacheShiro의 쉘입니까?**

아니요. Sa-Token은 백엔드 템플릿도 아니고 xx 프레임워크를 위한 보조 캡슐화 셸도 아니지만 처음부터 시작하는 순수한 자체 개발 프레임워크이며 코어 패키지에 대한 종속성이 전혀 없으며 완전히 독립적이고 제어 가능한 아키텍처 코어 + 많은 주류 프레임워크의 통합 적응입니다.
						



#### 인증서 ⭐ 트로피 🏆 명예 전시

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



### 🚀 우수한 오픈소스 통합 사례

- [[ Snowy ]](https://gitee.com/xiaonuobase/snowy): Vue3 + Vite + SpringBoot + MP + HuTool + SaToken을 사용하여 국가 기밀 전후를 분리하는 중국 최초의 급속 개발 플랫폼입니다.
- [[ RuoYi-Vue-Plus ]](https://gitee.com/dromara/RuoYi-Vue-Plus): RuoYi-Vue의 모든 기능을 다시 작성하고 정기적인 동기화를 위해 Sa-Token, Mybatis-Plus, Xxl-Job, Knife4j 및 OSS를 통합합니다.
- [[ Smart-Admin ]](https://gitee.com/lab1024/smart-admin) : SmartAdmin은 "고품질 코드"를 핵심으로 하고 "간단하고 효율적이며 안전한" 미들과 백엔드를 갖춘 국내 최초의 고속 개발 플랫폼입니다.
- [[ Orange Order ]](https://gitee.com/orangeform/orange-admin): Orange Order는 대만의 로우 코드 생성기입니다. 다중 애플리케이션, 다중 테넌트, 다중 채널, 워크플로우, 프레임워크 기술 스택의 자유로운 조합 등을 완벽하게 지원할 수 있습니다.
- [[ Deng Deng ]](https://gitee.com/dromara/lamp-cloud): 멀티 테넌트 솔루션에 초점을 맞춘 미드엔드 및 백엔드 신속한 개발 플랫폼입니다. 독립 데이터베이스, 공유 데이터 아키텍처 및 비테넌트 모드 지원 ✨
- [[ Shiyi 블로그 ]](https://gitee.com/quequnlong/shiyi-blog): 프런트엔드와 백엔드를 분리하는 vue + springboot 블로그 시스템입니다.



하나씩 표시할 수 없는 우수한 오픈소스 사례가 더 있습니다. [Awesome-Sa-Token](https://gitee.com/sa-token/awesome-sa-token)을 참조하세요.


### 🌍 다른 언어 버전

Sa-Token 커뮤니티 회원은 다국어 구현 버전에 기여했습니다.

- Rust 버전: [https://github.com/sa-tokens/sa-token-rust](https://github.com/sa-tokens/sa-token-rust)
- Go 버전: [https://github.com/sa-tokens/sa-token-go](https://github.com/sa-tokens/sa-token-go)
- NodeJS 버전: [https://github.com/xiaoLangtou/xlt-token](https://github.com/xiaoLangtou/xlt-token)
- PHP 버전: [https://gitee.com/jinan-jimeng-network_0/real-token](https://gitee.com/jinan-jimeng-network_0/real-token)

위의 언어에 좀 더 능숙한 개발자들이 함께 관련 버전을 만들어 보도록 진심으로 초대합니다. 🤝


### 🔗 친절한 링크
- [[ OkHttps ]](https://gitee.com/ejlchina-zhxu/okhttps): 경량 http 통신 프레임워크, 매우 우아한 API, WebSocket 및 Stomp 프로토콜 지원
- [[ Forest ]](https://gitee.com/dromara/forest): 선언적 및 프로그래밍적 이중 재배로 세상이 더 이상 HTTP 요청을 보내는 것이 어렵지 않게 만듭니다.
- [[ Bean Searcher ]](https://github.com/troyzhxu/bean-searcher): GraphQL의 REST 버전 - 엔터티는 경계를 정의하고 매개변수 기반 쿼리 및 복잡한 쿼리를 한 줄의 코드로 구현할 수 있습니다!
- [[ Jpom ]](https://gitee.com/dromara/Jpom): 간단하고 가벼운 저침해 온라인 구축, 자동 배포, 일일 운영 및 유지 관리, 프로젝트 모니터링 소프트웨어.
- [[ TLog ]](https://gitee.com/dromara/TLog): 경량 분산 로그 태그 추적 아티팩트입니다.
- [[ hippo4j ]](https://gitee.com/agentart/hippo4j): 모니터링 및 경보 기능을 갖춘 강력한 동적 스레드 풀 프레임워크입니다.
- [[ hertzbeat ]](https://gitee.com/dromara/hertzbeat): 사용하기 쉽고 친숙한 오픈 소스 실시간 모니터링 및 경보 시스템, 에이전트가 필요하지 않음, 고성능 클러스터 및 강력한 사용자 정의 모니터링 기능.
- [[ Solon ]](https://gitee.com/noear/solon): 보다 현대적인 애플리케이션 개발 프레임워크: 더 빠르고, 더 작고, 더 무료입니다.
- [[ Chat2DB ]](https://github.com/chat2db/Chat2DB): Mysql, pg, Oracle, Redis 등 22개의 데이터베이스 관리를 지원하는 AI 기반 데이터베이스 관리 및 BI 도구입니다.



### 📦 코드 호스팅
- Gitee：[https://gitee.com/dromara/sa-token](https://gitee.com/dromara/sa-token)
- GitHub：[https://github.com/dromara/sa-token](https://github.com/dromara/sa-token)
- AtomGit：[https://atomgit.com/dromara/sa-token](https://atomgit.com/dromara/sa-token)



### 📚 예시 모음

**우리는 프레임워크의 거의 모든 기술적 사항에 대해 해당 통합 예제를 별도로 제작했습니다. 이 압축 패키지에는 Sa-Token 로그인 인증, 권한 인증, SSO 싱글 사인온, OAuth2 통합 인증, 마이크로서비스 인증, API 키 인증, JWT 통합, 시스템 간 호출 매개변수 서명 확인 등과 같은 인증 및 인증의 모든 측면을 다루는 총 60개 이상의 데모**가 있습니다.

다운로드 주소: [https://sa-token.com/doc.html#/more/download-demos](https://sa-token.com/doc.html#/more/download-demos)

<img class="s-w" src="https://sa-token.com/big-file/contact/show/sa-token-demos-pre-liubai.png" />



### 💬 커뮤니케이션 그룹
<!-- QQ 커뮤니케이션 그룹: 685792424 [가입하려면 클릭](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=Y05Ld4125W92YSwZ0gA8e3RhG9Q4Vsfx&authKey=IomXuIuhP9g8G7l%2ByfkrRsS7i%2Fna0lIBpkTXxx%2BQEaz0NNEyJq00kgeiC4dUyNLS&noverify=0&group_code=685792424)-->
<!-- QQ 커뮤니케이션 그룹: 1081649142 [가입하려면 클릭](https://qm.qq.com/q/SCAaZ6Ros2) -->

QQ 커뮤니케이션 그룹: 1098917026 [가입하려면 클릭](https://qm.qq.com/q/26OFBGd2Zy)

WeChat 커뮤니케이션 그룹:

<!-- <img src="https://oss.dev33.cn/sa-token/qr/wx-qr-m-400k.png" width="230px" title="微信群" /> -->

<img src="https://sa-token.com/big-file/contact/i-wx-qr2.jpg" width="230px" title="微信群" />

추신: QR 코드를 스캔하여 WeChat(비고: sa-token)을 추가하여 그룹 채팅에 참여하도록 초대하세요.

<br>

<img class="s-w" src="https://sa-token.com/big-file/contact/show/wx-group-show3--liubai.png" style="max-width: 50%;" alt="微信群" />


그룹 채팅에 참여하면 얻을 수 있는 이점:
- 가능한 한 빨리 프레임워크 업데이트 알림을 받으세요.
- 가능한 한 빨리 프레임워크 버그 알림을 받습니다.
- 새로운 오픈소스 사례에 대한 알림을 최대한 빨리 받아보세요.
- 많은 덩치🖐️🐟️와 서로(모유) 소통(화수)합니다.

