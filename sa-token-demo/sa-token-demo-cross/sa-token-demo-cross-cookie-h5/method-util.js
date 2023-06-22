
// 后端服务地址 (在 Cookie 版跨域模式中，此处应该是一个 https 地址) 
// var baseUrl = "http://localhost:8081";
var baseUrl = "https://20e331r221.yicp.fun";

// 封装一下 Ajax 方法
var ajax = function(path, data, successFn) {
	$.ajax({
		url: baseUrl + path,
		type: "post", 
		data: data,
		dataType: 'json',
		// 指定是跨域模式，需要提交第三方 Cookie 
		crossDomain: true,
		xhrFields:{
			withCredentials: true
		},
		headers: {
			"X-Requested-With": "XMLHttpRequest"
		},
		success: function(res){
			successFn(res);
		},
		error: function(xhr, type, errorThrown){
			return alert("异常：" + JSON.stringify(xhr));
		}
	});
}