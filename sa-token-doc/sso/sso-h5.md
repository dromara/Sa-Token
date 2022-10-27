# SSO整合-前后端分离架构下的整合方案

---

如果我们已有的系统是前后端分离模式，我们显然不能为了接入SSO而改造系统的基础架构，官方仓库的示例采用的是前后端一体方案，要将其改造为前后台分离架构模式非常简单

以`sa-token-demo-sso2-client`为例：

### 1、新建`H5Controller`开放接口
``` java
/**
 * 前后台分离架构下集成SSO所需的代码 
 */
@RestController
public class H5Controller {

	// 当前是否登录 
	@RequestMapping("/sso/isLogin")
	public Object isLogin() {
		return SaResult.data(StpUtil.isLogin());
	}
	
	// 返回SSO认证中心登录地址 
	@RequestMapping("/sso/getSsoAuthUrl")
	public SaResult getSsoAuthUrl(String clientLoginUrl) {
		String serverAuthUrl = SaSsoUtil.buildServerAuthUrl(clientLoginUrl, "");
		return SaResult.data(serverAuthUrl);
	}
	
	// 根据ticket进行登录 
	@RequestMapping("/sso/doLoginByTicket")
	public SaResult doLoginByTicket(String ticket) {
		Object loginId = SaSsoProcessor.instance.checkTicket(ticket, "/sso/doLoginByTicket");
		if(loginId != null) {
			StpUtil.login(loginId);
			return SaResult.data(StpUtil.getTokenValue());
		}
		return SaResult.error("无效ticket：" + ticket); 
	}

	// 全局异常拦截 
	@ExceptionHandler
	public SaResult handlerException(Exception e) {
		e.printStackTrace(); 
		return SaResult.error(e.getMessage());
	}
	
}
```

### 2、增加跨域过滤器`CorsFilter.java`
源码详见：[CorsFilter.java](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-sso2-client/src/main/java/com/pj/h5/CorsFilter.java)，
将其复制到项目中即可 

### 3、新建前端项目 
任意文件夹新建前端项目：`sa-token-demo-sso-client-h5`，在根目录添加测试文件：`index.html`
``` js
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Sa-Token-SSO-Client端-测试页（前后端分离版）</title>
	</head>
	<body>
		<h2>Sa-Token SSO-Client 应用端（前后端分离版）</h2>
		<p>当前是否登录：<b class="is-login"></b></p>
		<p>
			<a href="javascript:location.href='sso-login.html?back=' + encodeURIComponent(location.href);">登录</a>
			<a href="javascript:location.href=baseUrl + '/sso/logout?satoken=' + localStorage.satoken + '&back=' + encodeURIComponent(location.href);">注销</a>
		</p>
		<script src="https://unpkg.zhimg.com/jquery@3.4.1/dist/jquery.min.js"></script>
		<script type="text/javascript">
		
			// 后端接口地址 
			var baseUrl = "http://sa-sso-client1.com:9001";
				
			// 查询当前会话是否登录 
			$.ajax({
				url: baseUrl + '/sso/isLogin',
				type: "post", 
				dataType: 'json',
				headers: {
					"X-Requested-With": "XMLHttpRequest",
					"satoken": localStorage.getItem("satoken")
				},
				success: function(res){
					$('.is-login').html(res.data + '');
				},
				error: function(xhr, type, errorThrown){
					return alert("异常：" + JSON.stringify(xhr));
				}
			});
			
		</script>
	</body>
</html>
```

### 4、添加登录处理文件`sso-login.html`
源码详见：[sso-login.html](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-sso-client-h5/sso-login.html)，
将其复制到项目中即可，与`index.html`一样放在根目录下 


### 5、测试运行
先启动Server服务端与Client服务端，再随便找个能预览html的工具打开前端项目（比如[HBuilderX](https://www.dcloud.io/hbuilderx.html)），测试流程与一体版一致 


### 6、SSO-Server 端的前后台分离
疑问：上述代码都是针对 Client 端进行拆分，如果我想在 SSO-Server 端也进行前后台分离改造，应该怎么做？

> 答：解决思路都是大同小异的，与Client一样，我们需要把原本在 “后端处理的授权重定向逻辑” 拿到前端来实现。

由于集成代码与 Client 端类似，这里暂不贴详细代码，我们可以下载官方仓库，里面有搭建好的demo

使用前端ide导入项目 `/sa-token-demo/sa-token-demo-sso-server-h5`，浏览器访问 `sso-auth.html` 页面：

![sso-type2-server-h5-auth.png](https://oss.dev33.cn/sa-token/doc/sso/sso-type2-server-h5-auth.png 's-w-sh')

复制上述地址，将其配置到 Client 端的 yml 配置文件中，例如：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
sa-token: 
	sso: 
		# SSO-Server端 统一认证地址 
	    auth-url: http://127.0.0.1:8848/sa-token-demo-sso-server-h5/sso-auth.html
```
<!------------- tab:properties 风格  ------------->
``` properties
# SSO-Server端 统一认证地址 
sa-token.sso.auth-url=http://127.0.0.1:8848/sa-token-demo-sso-server-h5/sso-auth.html
```
<!---------------------------- tabs:end ---------------------------->


然后我们启动项目 `sa-token-demo-sso-server` 与 `sa-token-demo-sso2-client`，按照之前的测试步骤访问：
[http://sa-sso-client1.com:9001/](http://sa-sso-client1.com:9001/)，即可以前后端分离模式完成 SSO-Server 端的授权登录。

