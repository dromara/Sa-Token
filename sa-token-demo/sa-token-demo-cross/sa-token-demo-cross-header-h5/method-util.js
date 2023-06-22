
// 后端服务地址
var baseUrl = "http://localhost:8081";

// 封装一下 Ajax 方法
var ajax = function(path, data, successFn) {
	$.ajax({
		url: baseUrl + path,
		type: "post", 
		data: data,
		dataType: 'json',
		headers: {
			"X-Requested-With": "XMLHttpRequest",
			"satoken": localStorage.getItem("satoken")
		},
		success: function(res){
			successFn(res);
		},
		error: function(xhr, type, errorThrown){
			return alert("异常：" + JSON.stringify(xhr));
		}
	});
}