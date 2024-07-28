# 解决反向代理 uri 丢失的问题

--- 

使用 `request.getRequestURL()` 可获取当前程序所在外网的访问地址，在 Sa-Token 中，其 `SaHolder.getRequest().getUrl()` 也正是借助此API完成，
有很多模块都用到了这个能力，比如SSO单点登录。

我们可以使用如下代码测试此API
``` java
// 显示当前程序所在外网的都访问地址
@RequestMapping("test")
public String test() {
	return "您访问的是：" + SaHolder.getRequest().getUrl();
}
```

从浏览器访问此接口，我们可以看到：

![test-curr-domain.png](https://oss.dev33.cn/sa-token/doc/test-curr-domain.png 's-w-sh')

此 API 在本地开发时一般可以正常工作，然而如果我们在部署时使用 Nginx 做了一层反向代理后，其最终结果可能会和我们预想的有一点偏差：

![test-curr-domain-fxdl.png](https://oss.dev33.cn/sa-token/doc/test-curr-domain-fxdl.png 's-w-sh')

不仅是 Nginx，所有包含路由转发的地方都有可能导致上述丢失 uri 的现象，解决方案也很简单，既然程序无法自动识别，我们改成手动获取即可，Sa-Token 提供两个方案：


### 方案一：Nginx转发时追加 header 参数

##### 1、首先在 Nginx 代理转发的地方增加参数

![nginx-add-header.png](https://oss.dev33.cn/sa-token/doc/nginx-add-header.png 's-w-sh')

重点是这一句：`proxy_set_header Public-Network-URL http://$http_host$request_uri;`

##### 2、在程序中新增类 `CustomSaTokenContextForSpring.java`，重写获取uri的逻辑

``` java
@Primary
@Component
public class CustomSaTokenContextForSpring extends SaTokenContextForSpring {
	
	@Override
	public SaRequest getRequest() {
		return new SaRequestForServlet(SpringMVCUtil.getRequest()) {
			@Override
			public String getUrl() {
				if(request.getHeader("Public-Network-URL") != null) {
					return request.getHeader("Public-Network-URL");
				}
				return request.getRequestURL().toString();
			}
		};
	}

}
```

其它逻辑保持不变，框架即可正确获取 uri 地址

> [!ATTENTION| label:风险警告] 
> 注意：步骤一与步骤二需要同步存在，否则可能有前端假传 header 参数造成安全问题 


### 方案二：直接在yml中配置当前项目的网络访问地址

在 `application.yml` 中增加配置：

<!---------------------------- tabs:start ---------------------------->
<!------------- tab:yaml 风格  ------------->
``` yaml
sa-token: 
    # 配置当前项目的网络访问地址
    curr-domain: http://local.dev33.cn:8902/api
```
<!------------- tab:properties 风格  ------------->
``` properties
# 配置当前项目的网络访问地址
sa-token.curr-domain=http://local.dev33.cn:8902/api
```
<!---------------------------- tabs:end ---------------------------->

即可避免路由转发过程中丢失 uri 的问题 
