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
