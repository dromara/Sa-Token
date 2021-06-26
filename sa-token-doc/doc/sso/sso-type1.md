# SSO模式一 共享Cookie同步会话

如果我们的系统可以保证部署在同一个主域名之下，并且后端连接同一个Redis，那么便可以使用 `[共享Cookie同步会话]` 的方式做到单点登录 

--- 

### 解决思路？

首先我们分析一下多个系统之间为什么无法同步登录状态？
1. 前端的`Token`无法在多个系统下共享
2. 后端的`Session`无法在多个系统间共享

所以单点登录第一招，就是对症下药：
1. 使用`共享Cookie`来解决Token共享问题
2. 使用`Redis`来解决Session共享问题

所谓共享Cookie，就是主域名Cookie在二级域名下的共享，举个例子：写在父域名`stp.com`下的Cookie，在`s1.stp.com`、`s2.stp.com`等子域名都是可以共享访问的

而共享Redis，并不需要我们把所有项目的数据都放在同一个Redis中，Sa-Token提供了 **[权限缓存与业务缓存分离]** 的解决方案，详情戳：[Alone独立Redis插件](/plugin/alone-redis)

> PS：这里建议不要用B项目去连接A项目的Redis，也不要A项目连接B项目的Redis，而是抽离出一个单独的 SSO-Redis，A项目和B项目一起来连接这个 SSO-Redis

OK，所有理论就绪，下面开始实战


### 集成步骤

Sa-Token整合同域下的单点登录非常简单，相比于正常的登录，你只需要在配置文件中增加配置 `sa-token.cookie-domain=xxx.com` 来指定一下Cookie写入时指定的父级域名即可，详细步骤示例如下：

#### 1. 准备工作
首先修改hosts文件`(C:\WINDOWS\system32\drivers\etc\hosts)`，添加以下IP映射，方便我们进行测试：
``` text
127.0.0.1 s1.stp.com
127.0.0.1 s2.stp.com
127.0.0.1 s3.stp.com
```

#### 2. 指定Cookie的作用域
常规情况下，在`s1.stp.com`域名访问服务器，其Cookie也只能写入到`s1.stp.com`下，为了将Cookie写入到其父级域名`stp.com`下，我们需要在配置文件中新增配置: 
``` yml
spring: 
	sa-token:
		# 写入Cookie时显式指定的作用域, 用于单点登录二级域名共享Cookie
		cookie-domain: stp.com
```

#### 3. 新增测试Controller
新建`SSOController.java`控制器，写入代码：
``` java
/**
 * 测试: 同域单点登录
 * @author kong
 */
@RestController
@RequestMapping("/sso/")
public class SSOController {

	// 测试：进行登录
	@RequestMapping("doLogin")
	public AjaxJson doLogin(@RequestParam(defaultValue = "10001") String id) {
		System.out.println("---------------- 进行登录 ");
		StpUtil.login(id);
		return AjaxJson.getSuccess("登录成功: " + id);
	}

	// 测试：是否登录
	@RequestMapping("isLogin")
	public AjaxJson isLogin() {
		System.out.println("---------------- 是否登录 ");
		boolean isLogin = StpUtil.isLogin();
		return AjaxJson.getSuccess("是否登录: " + isLogin);
	}

}
```

#### 4、访问测试
启动项目，依次访问：
- [http://s1.stp.com:8081/sso/isLogin](http://s1.stp.com:8081/sso/isLogin)
- [http://s2.stp.com:8081/sso/isLogin](http://s2.stp.com:8081/sso/isLogin)
- [http://s3.stp.com:8081/sso/isLogin](http://s3.stp.com:8081/sso/isLogin)

均返回以下结果：
``` js
{
	"code": 200,
	"msg": "是否登录: false",
	"data": null
}
```

现在访问任意节点的登录接口：
- [http://s1.stp.com:8081/sso/doLogin](http://s1.stp.com:8081/sso/doLogin) 

``` js
{
	"code": 200,
	"msg": "登录成功: 10001",
	"data": null
}
```

然后再次刷新上面三个测试接口，均可以得到以下结果：
``` js
{
	"code": 200,
	"msg": "是否登录: true",
	"data": null
}
```

测试完毕 


### 跨域模式下的解决方案 

如上，我们使用极其简单的步骤实现了同域下的单点登录，聪明如你😏，马上想到了这种模式有着一个不小的限制：

> 所有子系统的域名，必须同属一个父级域名

如果我们的子系统在完全不同的域名下，我们又该怎么完成单点登录功能呢？

且往下看，[SSO模式二：URL重定向传播会话](/sso/sso-type2)

<!-- 根据前面的总结，单点登录的关键点在于我们如何完成多个系统之间的token共享，而`Cookie`并非实现此功能的唯一方案，既然浏览器对`Cookie`限制重重，我们何不干脆直接放弃`Cookie`，转投`LocalStorage`的怀抱? 

思路：建立一个登录中心，在中心登录之后将token一次性下发到所有子系统中

参考以下步骤：
``` js
// 在主域名登录请求回调函数里执行以下方法 

// 获取token 
var token = res.data.tokenValue;

// 创建子域的iframe, 用于传送数据
var iframe = document.createElement("iframe");
iframe.src = "http://s2.stp.com/xxx.html";
iframe.style.display = 'none';
document.body.append(iframe);

// 使用postMessage()发送数据到子系统 
setTimeout(function () {
	iframe.contentWindow.postMessage(token, "http://s2.stp.com");
}, 2000);

// 销毁iframe 
setTimeout(function () {
	iframe.remove();
}, 4000);


// 在子系统里接受消息
window.addEventListener('message', function (event) {
	console.log('收到消息', event.data);
	// 写入本地localStorage缓存中 
	localStorage.setItem('satoken', event.data)
}, false);

```


<br>

总结：此方式仍然限制较大，但巧在提供了一种简便的思路做到了跨域共享token，其实跨域模式下的单点登录标准解法还是cas流程，
参考[单点登录的三种方式](https://www.cnblogs.com/yonghengzh/p/13712729.html) -->





