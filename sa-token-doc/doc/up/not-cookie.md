# 前后台分离（无Cookie模式）
--- 

### 何为无Cookie模式? 

无Cookie：特指不支持Cookie功能的终端，通俗来讲就是我们常说的 —— **前后台分离模式**

常规PC端鉴权方法，一般由`Cookie模式`完成，而 Cookie 有两个特性:
1. 可由后端控制写入
2. 每次请求自动提交

这就使得我们在前端代码中，无需任何特殊操作，就能完成鉴权的全部流程（因为整个流程都是后端控制完成的）<br/>
而在app、小程序等前后台分离场景中，一般是没有 Cookie 这一功能的，此时大多数人都会一脸懵逼，咋进行鉴权啊？

见招拆招，其实答案很简单：
- 不能后端控制写入了，就前端自己写入（难点在**后端如何将token传递到前端**）
- 每次请求不能自动提交了，那就手动提交（难点在**前端如何将token传递到后端**，同时**后端将其读取出来**）



### 1、后端将 token 返回到前端

1. 首先调用 `StpUtil.login(id)` 进行登录 
2. 调用 `StpUtil.getTokenInfo()` 返回当前会话的token详细参数 
	- 此方法返回一个对象，其有两个关键属性：`tokenName`和`tokenValue`（token 的名称和 token 的值）
	- 将此对象传递到前台，让前端人员将这两个值保存到本地

### 2、前端将 token 提交到后端
1. 无论是app还是小程序，其传递方式都大同小异
2. 那就是，将 token 塞到请求`header`里 ，格式为：`{tokenName: tokenValue}`
3. 以经典跨端框架 [uni-app](https://uniapp.dcloud.io/) 为例： 

**方式1，简单粗暴**

``` js 
// 1、首先在登录时，将 tokenValue 存储在本地，例如：
uni.setStorageSync('tokenValue', tokenValue);

// 2、在发起ajax请求的地方，获取这个值，并塞到header里 
uni.request({
	url: 'https://www.example.com/request', // 仅为示例，并非真实接口地址。
	header: {
		"content-type": "application/x-www-form-urlencoded",
		"satoken": uni.getStorageSync('tokenValue')		// 关键代码, 注意参数名字是 satoken 
	},
	success: (res) => {
		console.log(res.data);	
	}
});
```

**方式2，更加灵活**
	
``` js
// 1、首先在登录时，将tokenName和tokenValue一起存储在本地，例如：
uni.setStorageSync('tokenName', tokenName); 
uni.setStorageSync('tokenValue', tokenValue); 

// 2、在发起ajax的地方，获取这两个值, 并组织到head里 
var tokenName = uni.getStorageSync('tokenName');	// 从本地缓存读取tokenName值
var tokenValue = uni.getStorageSync('tokenValue');	// 从本地缓存读取tokenValue值
var header = {
	"content-type": "application/x-www-form-urlencoded"
};
if (tokenName != undefined && tokenName != '') {
	header[tokenName] = tokenValue;
}

// 3、后续在发起请求时将 header 对象塞到请求头部 
uni.request({
	url: 'https://www.example.com/request', // 仅为示例，并非真实接口地址。
	header: header,
	success: (res) => {
		console.log(res.data);	
	}
});
```

4. 只要按照如此方法将`token`值传递到后端，Sa-Token 就能像传统PC端一样自动读取到 token 值，进行鉴权
5. 你可能会有疑问，难道我每个`ajax`都要写这么一坨？岂不是麻烦死了
	- 你当然不能每个 ajax 都写这么一坨，因为这种重复性代码都是要封装在一个函数里统一调用的 


### 其它解决方案？
如果你对 Cookie 非常了解，那你就会明白，所谓 Cookie ，本质上就是一个特殊的`header`参数而已，
而既然它只是一个 header 参数，我们就能手动模拟实现它，从而完成鉴权操作

这其实是对`无Cookie模式`的另一种解决方案，有兴趣的同学可以百度了解一下，在此暂不赘述 

