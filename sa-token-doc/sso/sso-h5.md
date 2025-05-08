# SSO整合-前后端分离架构下的整合方案

---

## SSO-Client 前后端分离

要在前后端分离的环境中接入 SSO，思路不难，主要的工作是吧后端 `/sso/login` 接口的路由中转工作拿到前端来，以`sa-token-demo-sso3-client`为例：

### 1、在 sso-client 后端新建`H5Controller`，开放接口：

``` java
/**
 * 前后台分离架构下集成 SSO 所需的代码 （SSO-Client端）
 */
@RestController
public class H5Controller {

	// 判断当前是否登录
	@RequestMapping("/sso/isLogin")
	public Object isLogin() {
		return SaResult.data(StpUtil.isLogin()).set("loginId", StpUtil.getLoginIdDefaultNull());
	}
	
	// 返回SSO认证中心登录地址 
	@RequestMapping("/sso/getSsoAuthUrl")
	public SaResult getSsoAuthUrl(String clientLoginUrl) {
		String serverAuthUrl = SaSsoClientUtil.buildServerAuthUrl(clientLoginUrl, "");
		return SaResult.data(serverAuthUrl);
	}
	
	// 根据 ticket 进行登录
	@RequestMapping("/sso/doLoginByTicket")
	public SaResult doLoginByTicket(String ticket) {
		SaCheckTicketResult ctr = SaSsoClientProcessor.instance.checkTicket(ticket);
		StpUtil.login(ctr.loginId, new SaLoginParameter()
				.setTimeout(ctr.remainTokenTimeout)
				.setDeviceId(ctr.deviceId)
		);
		return SaResult.data(StpUtil.getTokenValue());
	}

}
```


### 2、增加跨域处理策略 

``` java
/**
 * [Sa-Token 权限认证] 配置类
 */
@Configuration
public class SaTokenConfigure {

    /**
     * CORS 跨域处理策略
     */
    @Bean
    public SaCorsHandleFunction corsHandle() {
        return (req, res, sto) -> {
            res.
                    // 允许指定域访问跨域资源
                    setHeader("Access-Control-Allow-Origin", "*")
                    // 允许所有请求方式
                    .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
                    // 有效时间
                    .setHeader("Access-Control-Max-Age", "3600")
                    // 允许的header参数
                    .setHeader("Access-Control-Allow-Headers", "*");

            // 如果是预检请求，则立即返回到前端
            SaRouter.match(SaHttpMethod.OPTIONS)
                    .free(r -> System.out.println("--------OPTIONS预检请求，不做处理"))
                    .back();
        };
    }

}
```

详细参考：[解决跨域问题](/fun/cors-filter)


### 3、新建前端项目 
任意文件夹新建前端项目：`sa-token-demo-sso-client-h5`，在根目录添加测试文件：`index.html`
``` html
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Sa-Token-SSO-Client端-测试页（前后端分离版-原生h5）</title>
	</head>
	<body>
		<h2>Sa-Token SSO-Client 应用端（前后端分离版-原生h5）</h2>
		<p>当前是否登录：<b class="is-login"></b></p>
		<p>
			<a href="javascript: login();">登录</a> - 
			<a href="javascript: doLogoutByAlone();">单应用注销</a> - 
			<a href="javascript: doLogoutBySingleDeviceId();">单浏览器注销</a> - 
			<a href="javascript: doLogout();">全端注销</a> - 
			<a href="javascript: doMyInfo();">账号资料</a>
		</p>
		<script src="sso-common.js"></script>
		<script type="text/javascript">
			
			// 登录 
			function login() {
				location.href = 'sso-login.html?back=' + encodeURIComponent(location.href);
			}
			
			// 单应用注销
			function doLogoutByAlone() {
				ajax('/sso/logoutByAlone', {}, function(res){
					doIsLogin();
				})
			}
			
			// 单浏览器注销
			function doLogoutBySingleDeviceId() {
				ajax('/sso/logout', { singleDeviceIdLogout: true }, function(res){
					doIsLogin();
				})
			}
			
			// 全端注销
			function doLogout() {
				ajax('/sso/logout', {  }, function(res){
					doIsLogin();
				})
			}
			
			// 账号资料
			function doMyInfo() {
				ajax('/sso/myInfo', {  }, function(res){
					alert(JSON.stringify(res));
				})
			}
			
			// 判断是否登录 
			function doIsLogin() {
				ajax('/sso/isLogin', {}, function(res){
					if(res.data) {
						setHtml('.is-login', res.data + ' (' + res.loginId + ')');
					} else {
						setHtml('.is-login', res.data);
					}
				})
			}
			doIsLogin();
			
		</script>
	</body>
</html>
```

### 4、添加单点登录登录中转页

在根目录创建文件：`sso-login.html`

``` html
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Sa-Token-SSO-Client端-登录中转页页</title>
		<style type="text/css">
		
		</style>
	</head>
	<body>
		<div class="login-box">
			加载中 ... 
		</div>
		<script src="sso-common.js"></script>
		<script type="text/javascript">
		
			var back = getParam('back', '/');
			var ticket = getParam('ticket');
			
			window.onload = function(){
				if(ticket) {
					doLoginByTicket(ticket);
				} else {
					goSsoAuthUrl();
				}
			}
			
			// 重定向至认证中心 
			function goSsoAuthUrl() {
				ajax('/sso/getSsoAuthUrl', {clientLoginUrl: location.href}, function(res) {
					location.href = res.data;
				})
			}
		
			// 根据ticket值登录 
			function doLoginByTicket(ticket) {
				ajax('/sso/doLoginByTicket', {ticket: ticket}, function(res) {
					localStorage.setItem('satoken', res.data);
					location.href = decodeURIComponent(back); 
				})
			}
			
		</script>
	</body>
</html>
```


### 5、添加公共 js文件
新建 `sso-common.js`：

``` js
// 服务器接口主机地址
// var baseUrl = "http://sa-sso-client1.com:9002";  // 模式二后端 
var baseUrl = "http://sa-sso-client1.com:9003";  // 模式三后端 

// 封装一下Ajax
function ajax(path, data, successFn, errorFn) {
	console.log('发起请求：', baseUrl + path, JSON.stringify(data));
	fetch(baseUrl + path, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded',
				'X-Requested-With': 'XMLHttpRequest',
				'satoken': localStorage.getItem('satoken')
			},
			body: serializeToQueryString(data),
		})
		.then(response => response.json())
		.then(res => {
			console.log('返回数据：', res);
			if(res.code === 500) {
				return alert(res.msg);
			}
			successFn(res);
		})
		.catch(error => {
			console.error('请求失败:', error);
			return alert("异常：" + JSON.stringify(error));
		});
}

// ------------ 工具方法 ---------------

// 从url中查询到指定名称的参数值
function getParam(name, defaultValue) {
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i = 0; i < vars.length; i++) {
		var pair = vars[i].split("=");
		if (pair[0] == name) {
			return pair[1];
		}
	}
	return (defaultValue == undefined ? null : defaultValue);
}

// 将 json 对象序列化为kv字符串，形如：name=Joh&age=30&active=true
function serializeToQueryString(obj) {
	return Object.entries(obj)
		.filter(([_, value]) => value != null) // 过滤 null 和 undefined
		.map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
		.join('&');
}

// 向指定标签里 set 内容 
function setHtml(select, html) {
	const dom = document.querySelector('.is-login');
	if(dom) {
		dom.innerHTML = html;
	}
}
```


### 6、测试运行
先启动 Server 服务端与 Client 服务端，再随便找个能预览html的工具打开前端项目（比如[HBuilderX](https://www.dcloud.io/hbuilderx.html)），测试流程与一体版一致，暂不赘述。




> [!TIP| label:另附其它技术栈的前后端分离 demo 示例：] 
> - sso-client 前后端分离 - 原生h5：[源码链接](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso-client-h5) 
> - sso-client 前后端分离 - vue2：[源码链接](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso-client-vue2) 
> - sso-client 前后端分离 - vue3：[源码链接](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso-client-vue3) 


## SSO-Server 前后端分离

解决思路与 SSO-Client 一样，我们需要把原本在 “后端处理的授权重定向逻辑” 拿到前端来实现。

由于集成代码与 Client 端类似，这里暂不贴详细代码，我们可以下载官方仓库，里面有搭建好的demo。

使用前端 ide 导入项目 `/sa-token-demo/sa-token-demo-sso/sa-token-demo-sso-server-h5`，浏览器访问 `sso-auth.html` 页面：

![sso-type2-server-h5-auth.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type2-server-h5-auth.png 's-w-sh')

复制上述地址，将其配置到 Client 端的配置项 `sa-token.sso-client.auth-url` ，例如：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
sa-token: 
	sso-client: 
        # sso-server 端主机地址
        server-url: http://sa-sso-server.com:9000
        # 在 sso-server 端前后端分离时需要单独配置 auth-url 参数（上面的不要注释，auth-url 配置项和 server-url 要同时存在）
	    auth-url: http://127.0.0.1:8848/sa-token-demo-sso-server-h5/sso-auth.html
```
<!------------- tab:properties 风格  ------------->
``` properties
# SSO-Server端 统一认证地址 
sa-token.sso-client.server-url=http://sa-sso-server.com:9000
# 在 sso-server 端前后端分离时需要单独配置 auth-url 参数（上面的不要注释，auth-url 配置项和 server-url 要同时存在）
sa-token.sso-client.auth-url=http://127.0.0.1:8848/sa-token-demo-sso-server-h5/sso-auth.html
```
<!---------------------------- tabs:end ---------------------------->


然后我们启动项目 sso-server 与 sso-client ，按照之前的测试步骤访问：
[http://sa-sso-client1.com:9003/](http://sa-sso-client1.com:9003/)，即可以前后端分离模式完成 SSO-Server 端的授权登录。

