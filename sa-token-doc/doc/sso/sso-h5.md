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
	@RequestMapping("/isLogin")
	public Object isLogin() {
		return SaResult.data(StpUtil.isLogin());
	}
	
	// 返回SSO认证中心登录地址 
	@RequestMapping("/getSsoAuthUrl")
	public SaResult getSsoAuthUrl(String clientLoginUrl) {
		String serverAuthUrl = SaSsoUtil.buildServerAuthUrl(clientLoginUrl, "");
		return SaResult.data(serverAuthUrl);
	}
	
	// 根据ticket进行登录 
	@RequestMapping("/doLoginByTicket")
	public SaResult doLoginByTicket(String ticket) {
		Object loginId = checkTicket(ticket);
		if(loginId != null) {
			StpUtil.login(loginId);
			return SaResult.data(StpUtil.getTokenValue());
		}
		return SaResult.error("无效ticket：" + ticket); 
	}

	// 校验 Ticket码，获取账号Id 
	private Object checkTicket(String ticket) {
		return SaSsoUtil.checkTicket(ticket);
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
任意文件夹新建前端项目：`sa-token-demo-sso2-client-h5`，在根目录添加测试文件：`index.html`
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
				url: baseUrl + '/isLogin',
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
源码详见：[sso-login.html](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-sso2-client-h5/sso-login.html)，
将其复制到项目中即可，与`index.html`一样放在根目录下 


### 5、测试运行
先启动Server服务端与Client服务端，再随便找个能预览html的工具打开前端项目（比如[HBuilderX](https://www.dcloud.io/hbuilderx.html)），测试流程与一体版一致 

### 6、疑问：我在SSO模式三的demo中加入上述代码，提示我ticket无效，是怎么回事？
上述代码是以SSO模式二为基础的，提示“Ticket无效”的原因很简单，因为SSO模式三种 Server端 与 Client端 连接的不是同一个Redis，
所以Client端校验Ticket时无法在Redis中查询到相应的值，才会产生异常：“Ticket无效”

要使上述代码生效很简单，我们只需更改一下校验Ticket的逻辑即可，将 `H5Controller` 中的 `checkTicket` 方法代码改为：
``` java
// 校验 Ticket码，获取账号Id 
private Object checkTicket(String ticket) {
	SaSsoConfig cfg = SaManager.getConfig().getSso();
	String ssoLogoutCall = null; 
	if(cfg.isSlo) {
		ssoLogoutCall = SaHolder.getRequest().getUrl().replace("/doLoginByTicket", Api.ssoLogoutCall); 
	}
	String checkUrl = SaSsoUtil.buildCheckTicketUrl(ticket, ssoLogoutCall);
	Object body = cfg.sendHttp.apply(checkUrl);
	return (SaFoxUtil.isEmpty(body) ? null : body);
}
```

重新运行项目，即可在SSO模式三中成功整合前后台分离模式 。




