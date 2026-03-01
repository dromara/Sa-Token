
// ----------------------------------- 相关事件 -----------------------------------

// 检查当前是否已经登录，如果已登录则直接开始跳转，如果未登录则等待用户输入账号密码 
var pData = {
	client: getParam('client', ''), 
	redirect: getParam('redirect', ''), 
	mode: getParam('mode', '')
};
// 提供 redirect 参数时，登录后往 redirect 跳转
if(pData.redirect) {
	sa.ajax("/sso/getRedirectUrl", pData, function(res) {
		if(res.code == 200) {
			// 已登录，并且redirect地址有效，开始跳转  
			location.href = res.data;
		} else if(res.code == 401) {
			console.log('未登录');
		} else {
			layer.alert(res.msg); 
		}
	})
} else {
	// 未提供 redirect 参数时，登录后往 home 跳转
	sa.ajax("/sso/isLogin", {}, function(res) {
		if(res.data) {
			location.href = './home.html';
		} else {
			console.log('未登录，请先登录...');
		}
	})
}


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
