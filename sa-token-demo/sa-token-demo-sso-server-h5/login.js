// 服务端地址 
var baseUrl = "http://sa-sso-server.com:9000";

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
			console.log('返回数据：', res);
			successFn(res);
		},
		error: function(xhr, type, errorThrown){
			if(xhr.status == 0){
				return alert('无法连接到服务器，请检查网络');
			}
			return alert("异常：" + JSON.stringify(xhr));
		}
	});
}

// ----------------------------------- 相关事件 -----------------------------------

// 检查当前是否已经登录，如果已登录则直接开始跳转，如果未登录则等待用户输入账号密码 
sa.ajax("/sso/getRedirectUrl", {redirect: getParam('redirect', ''), mode: getParam('mode', '')}, function(res) {
	if(res.code == 200) {
		// 已登录，并且redirect地址有效，开始跳转  
		location.href = decodeURIComponent(res.data);
	} else if(res.code == 401) {
		console.log('未登录');
	} else {
		layer.alert(res.msg); 
	}
})

// 登录
$('.login-btn').click(function(){
	sa.loading("正在登录...");
	// 开始登录
	var data = {
		name: $('[name=name]').val(),
		pwd: $('[name=pwd]').val()
	};
	sa.ajax("/sso/doLogin", data, function(res) {
		sa.hideLoading();
		if(res.code == 200) {
			localStorage.setItem('satoken', res.data);
			layer.msg('登录成功', {anim: 0, icon: 6 }); 
			setTimeout(function() {
				location.reload();
			}, 800);
		} else {
			layer.msg(res.msg, {anim: 6, icon: 2 }); 
		}
	})
});


// 绑定回车事件
$('[name=name],[name=pwd]').bind('keypress', function(event){
	if(event.keyCode == "13") {
		$('.login-btn').click();
	}
});

// 输入框获取焦点
$("[name=name]").focus();

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

// 打印信息 
var str = "This page is provided by Sa-Token, Please refer to: " + "https://sa-token.cc/";
console.log(str);
