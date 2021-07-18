# 单点登录
--- 

### 什么是单点登录？解决什么问题？

举个场景：假设你的系统被切割成N个部分：商城、论坛、直播、社交、视频…… 并且这些模块都部署在不同的服务器下，
如果用户每访问其中一个模块都要进行一次登录注册，那么用户将会疯掉

为了不让用户疯掉，我们急需一套机制将这个N个模块的授权进行共享，使得用户在其中一个模块登录授权之后，便可以畅通无阻的访问其它模块

单点登录——就是为了解决这个问题而生

简单来讲就是，单点登录可以做到：在多个应用系统中，用户只需要登录一次就可以访问所有相互信任的应用系统。

下面我们将详细介绍“同域Cookie共享"模式下的单点登录（cas机制将会在以后的章节中进行整合）


### 解决思路？

首先我们分析一下多个系统之间为什么无法同步登录状态？
1. 前端的`token`无法在多个系统下共享
2. 后台的`Session`无法在多个服务间共享

所以单点登录第一招，就是对症下药：
1. 使用`共享Cookie`来解决`token共享`问题
2. 使用`Redis`来解决`Session共享`问题

在前面的章节我们已经了解了`Sa-Token`整合`Redis`的步骤，现在我们来讲一下如何在多个域名下共享Cookie。

首先我们需要明确一点：根据`CORS策略`，在A域名下写入的Cookie，在B域名下是无法读取的，浏览器对跨域访问有着非常严格的限制 <br>

既然如此，我们如何做到让`Cookie`在多个域名下共享？其实关于跨域Cookie访问，浏览器还有一条规则，那就是同域名下的二级域名是可以共享Cookie的。
举个例子：只要我们将`Cookie`写入父级域名`stp.com`下，在其任意一个二级域名比如`s1.stp.com`都是可以共享访问的，这就为我们需要的`token共享`提供了必要的前提

OK，所有理论就绪，下面开始实战


### 集成步骤

Sa-Token整合同域下的单点登录非常简单，相比于正常的登录，你只需要在配置文件中增加配置 `sa-token.cookie-domain=xxx.com` 来指定一下Cookie写入时指定的父级域名即可，详细步骤示例如下：

#### 1. 准备工作
首先修改hosts文件(`C:\windows\system32\drivers\etc\hosts`)，添加以下IP映射，方便我们进行测试：
``` url
127.0.0.1 s1.stp.com
127.0.0.1 s2.stp.com
127.0.0.1 s3.stp.com
```

#### 2. 指定Cookie的作用域
常规情况下，在`s1.stp.com`域名访问服务器，其Cookie也只能写入到`s1.stp.com`下，为了将Cookie写入到其父级域名`stp.com`下，我们需要在配置文件中新增配置: 
``` yml
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
- 所有子系统的域名，必须同属一个父级域名

如果我们我们的子系统在完全不同的域名下，我们又该怎么完成单点登录功能呢？

根据前面的总结，单点登录的关键点在于我们如何完成多个系统之间的token共享，而`Cookie`并非实现此功能的唯一方案，既然浏览器对`Cookie`限制重重，我们何不干脆直接放弃`Cookie`，转投`LocalStorage`的怀抱? 

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
参考[单点登录的三种方式](https://www.cnblogs.com/yonghengzh/p/13712729.html)













