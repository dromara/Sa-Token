// OAuth-Server 后端 接口地址 
var baseUrl = "http://sa-oauth-server.com:8000";


// ----------------------------------- 相关事件 -----------------------------------

// 显示默认区域 
function showDefaultRegion(){
	$('.content-box').hide();
	$('.region-default').show();
}
// 显示登录框区域 
function showLoginRegion(){
	$('.content-box').hide();
	$('.region-login').show();
}
// 显示确认授权框区域 
function showConfirmRegion(){
	$('.content-box').hide();
	$('.region-confirm').show(); 
	$('.show-clientId').text(getParam('client_id'));
	$('.show-scope').text(getParam('scope'));
}

// 检查当前是否已经登录，如果已登录则直接开始跳转，如果未登录则等待用户输入账号密码 
function tryJump(){
	var data = location.search.substr(1);
	sa.ajax("/oauth2/getRedirectUri", data, function(res) {
		// 情况1：客户端未登录，返回 code=401，提示用户登录
		if(res.code === 401) {
			showLoginRegion();
			return;
		}
		
		// 情况2：请求的 scope 需要客户端手动确认授权，返回 code=411，提示用户手动确认 
		if(res.code === 411) {
			showConfirmRegion();
			return;
		}
		
		// 情况3：已登录且请求的 scope 已确认授权，返回 code=200，data=最终重定向 url 地址(携带code码参数)
		if(res.code == 200) {
			console.log('跳转：', res.redirect_uri);
			location.href = res.redirect_uri;
			return;
		}
		
		console.log('未知状态码，', res.code, res);
		layer.alert('错误：' + JSON.stringify(res))
	})
}

// 登录事件 
function doLogin() {
	// 开始登录
	var data = {
		name: $('[name=name]').val(),
		pwd: $('[name=pwd]').val()
	};
	sa.ajax("/oauth2/doLogin", data, function(res) {
		if(res.code == 200) {
			localStorage.setItem('satoken', res.satoken);
			layer.msg('登录成功', {anim: 0, icon: 6 }); 
			setTimeout(function() {
				location.reload();
			}, 800);
		} else {
			layer.msg(res.msg, {anim: 6, icon: 2 }); 
		}
	})
}

// 确认授权事件 
function yes() {
	var data = location.search.substr(1) + '&build_redirect_uri=true';
	sa.ajax("/oauth2/doConfirm", data, function(res) {
		if(res.code == 200) {
			layer.msg('确认授权成功，即将跳转...', {anim: 0, icon: 6 }); 
			setTimeout(function() {
				console.log('跳转：', res.redirect_uri);
				location.href = res.redirect_uri;
			}, 800);
		} else {
			layer.msg(res.msg, {anim: 6, icon: 2 }); 
		}
	})
}

// 拒绝授权事件 
function no() {
	var url = joinParam(getParam('redirect_uri'), "handle=refuse&msg=用户拒绝了授权");
	location.href = url;
}

// 页面加载完毕后触发 
window.onload = function() {
	tryJump();
	
	// 绑定回车事件
	$('[name=name],[name=pwd]').bind('keypress', function(event){
		if(event.keyCode == "13") {
			$('.login-btn').click();
		}
	});
	
	// 输入框获取焦点
	$("[name=name]").focus();
}



// ----------------------------------- 工具函数封装 -----------------------------------

// sa 
var sa = {};

// 打开loading
sa.loading = function(msg) {
	layer.closeAll();	// 开始前先把所有弹窗关了
	return layer.msg(msg, {icon: 16, shade: 0.3, time: 1000 * 20, skin: 'ajax-layer-load'});
};

// 隐藏loading
sa.hideLoading = function() {
	layer.closeAll();
};

// 封装一下Ajax
sa.ajax = function(url, data, successFn) {
	sa.loading("加载中...");
	$.ajax({
		url: baseUrl + url,
		type: "post", 
		data: data,
		dataType: 'json',
		headers: {
			'X-Requested-With': 'XMLHttpRequest',
			'satoken': localStorage.getItem('satoken')
		},
		success: function(res){
			sa.hideLoading();
			console.log('返回数据：', res);
			successFn(res);
		},
		error: function(xhr, type, errorThrown){
			sa.hideLoading();
			if(xhr.status == 0){
				return alert('无法连接到服务器，请检查网络');
			}
			return alert("异常：" + JSON.stringify(xhr));
		}
	});
}

// 从url中查询到指定名称的参数值 
function getParam(name, defaultValue){
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i=0;i<vars.length;i++) {
		var pair = vars[i].split("=");
		if(pair[0] == name){return pair[1] + (pair[2] ? '=' + pair[2] : '');}
	}
	return(defaultValue == undefined ? null : defaultValue);
}

// 在url上拼接上kv参数并返回 
function joinParam(url, parameStr) {
	if(parameStr == null || parameStr.length == 0) {
		return url;
	}
	var index = url.indexOf('?');
	// ? 不存在
	if(index == -1) {
		return url + '?' + parameStr;
	}
	// ? 是最后一位
	if(index == url.length - 1) {
		return url + parameStr;
	}
	// ? 是其中一位
	if(index > -1 && index < url.length - 1) {
		// 如果最后一位是 不是&, 且 parameStr 第一位不是 &, 就增送一个 &
		if(url.lastIndexOf('&') != url.length - 1 && parameStrindexOf('&') != 0) {
			return url + '&' + parameStr;
		} else {
			return url + parameStr;
		}
	}
}

// 打印信息 
var str = "This page is provided by Sa-Token, Please refer to: " + "https://sa-token.cc/";
console.log(str);

