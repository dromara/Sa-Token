# OAuth2-Server 端前后台分离

### 1、设计分析

要使 OAuth2-Server 端做到前后台分离，则需要对接口进行一部分改造：

- 改造前的接口列表：
	- `http:{后端主机}/oauth2/authorize`
	- `http:{后端主机}/oauth2/token`
	- `http:{后端主机}/oauth2/refresh`
	- 更多...
- 改造后的接口列表：
	- `http:{前端主机}/oauth2/authorize`
	- `http:{后端主机}/oauth2/token`
	- `http:{后端主机}/oauth2/refresh`
	- 更多...

也就是，只需要重点改造 `/oauth2/authorize` 一个接口即可，`/oauth2/authorize` 接口主要做了三件事：
1. 判断用户在 oauth2-server 端是否登录，未登录会进入 [登录页面]，已登录则进入下一步。
2. 判断应用请求的 scope 是否需要用户手动确认授权，需要会进入 [确认授权页面]，不需要则进入下一步。
3. 重定向至 `redirect_uri` 指定的 url 地址，并携带 code 授权码参数。

我们只需要把上述逻辑从 oauth2-server 的后端搬到 oauth2-server 的前端即可。


### 2、OAuth2-Server 后端添加接口

首先在 `oauth2-server` 的后端添加一个接口，用于获取最终授权重定向地址：

``` java
/**
 * Sa-Token OAuth2 Server端 控制器 (前后端分离情形下所需要的接口)
 */
@RestController
public class SaOAuth2ServerH5Controller {

    /**
     * 获取最终授权重定向地址，形如：http://xxx.com/xxx?code=xxxxx
     *
     * <p> 情况1：客户端未登录，返回 code=401，提示用户登录 <p/>
     * <p> 情况2：请求的 scope 需要客户端手动确认授权，返回 code=411，提示用户手动确认 <p/>
     * <p> 情况3：已登录且请求的 scope 已确认授权，返回 code=200，redirect_uri=最终重定向 url 地址(携带code码参数) <p/>
     *
     * @return /
     */
    @PostMapping("/oauth2/getRedirectUri")
    public Object getRedirectUri() {

        // 获取变量
        SaRequest req = SaHolder.getRequest();
        SaOAuth2ServerConfig cfg = SaOAuth2Manager.getServerConfig();
        SaOAuth2DataGenerate dataGenerate = SaOAuth2Manager.getDataGenerate();
        SaOAuth2Template oauth2Template = SaOAuth2Manager.getTemplate();
        String responseType = req.getParamNotNull(SaOAuth2Consts.Param.response_type);

        // 1、先判断是否开启了指定的授权模式
        SaOAuth2ServerProcessor.instance.checkAuthorizeResponseType(responseType, req, cfg);

        // 2、如果尚未登录, 则先去登录
        long loginId = SaOAuth2Manager.getStpLogic().getLoginId(0L);
        if(loginId == 0L) {
            return SaResult.get(401, "need login", null);
        }

        // 3、构建请求 Model
        RequestAuthModel ra = SaOAuth2Manager.getDataResolver().readRequestAuthModel(req, loginId);

        // 4、校验：重定向域名是否合法
        oauth2Template.checkRedirectUri(ra.clientId, ra.redirectUri);

        // 5、校验：此次申请的Scope，该Client是否已经签约
        oauth2Template.checkContractScope(ra.clientId, ra.scopes);

        // 6、判断：如果此次申请的Scope，该用户尚未授权，则转到授权页面
        boolean isNeedCarefulConfirm = oauth2Template.isNeedCarefulConfirm(ra.loginId, ra.clientId, ra.scopes);
        if(isNeedCarefulConfirm) {
            // code=411，需要用户手动确认授权
            return SaResult.get(411, "need confirm", null);
        }

        // 7、判断授权类型，重定向到不同地址
        // 		如果是 授权码式，则：开始重定向授权，下放code
        if(SaOAuth2Consts.ResponseType.code.equals(ra.responseType)) {
            CodeModel codeModel = dataGenerate.generateCode(ra);
            String redirectUri = dataGenerate.buildRedirectUri(ra.redirectUri, codeModel.code, ra.state);
            return SaResult.ok().set("redirect_uri", redirectUri);
        }

        // 		如果是 隐藏式，则：开始重定向授权，下放 token
        if(SaOAuth2Consts.ResponseType.token.equals(ra.responseType)) {
            AccessTokenModel at = dataGenerate.generateAccessToken(ra, false, null);
            String redirectUri = dataGenerate.buildImplicitRedirectUri(ra.redirectUri, at.accessToken, ra.state);
            return SaResult.ok().set("redirect_uri", redirectUri);
        }

        // 默认返回
        throw new SaOAuth2Exception("无效 response_type: " + ra.responseType).setCode(SaOAuth2ErrorCode.CODE_30125);
    }

}
```

### 3、新建前端项目

既然是前后台分离，那肯定要有一个独立的前端项目，所需代码比较冗长，不便于在文档处直接展示，大家可以参考在线仓库示例：

[sa-token-demo-oauth2-server-h5/](https://gitee.com/dromara/sa-token/blob/dev/sa-token-demo/sa-token-demo-oauth2/sa-token-demo-oauth2-server-h5/)


### 4、运行测试

在前端 ide 中导入 demo 案例的 `sa-token-demo-oauth2-server-h5` 项目，然后直接预览 `oauth2-authorize.html` 页面，如图所示：

![sa-oauth2-server-authorize-h5.png](https://oss.dev33.cn/sa-token/doc/oauth2-new/sa-oauth2-server-authorize-h5.png 's-w-sh')

复制上述地址，然后将其配置到 “OAuth2前端测试页” 的 “OAuth2 Server 授权页地址” 选项中，其它选项保持默认不变：

![sa-oauth2-client-test-h5-page-setting.png](https://oss.dev33.cn/sa-token/doc/oauth2-new/sa-oauth2-client-test-h5-page-setting.png 's-w-sh')

然后根据 “OAuth2前端测试页” 的页面提示进行测试即可，此处不再赘述。


