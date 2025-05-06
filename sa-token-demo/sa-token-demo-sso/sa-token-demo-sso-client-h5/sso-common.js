// 服务器接口主机地址
// var baseUrl = "http://sa-sso-client1.com:9002";  // 模式二后端 
var baseUrl = "http://sa-sso-client1.com:9003";  // 模式三后端 

// 封装一下Ajax
function ajax(path, data, successFn, errorFn) {
	console.log('发起请求：', baseUrl + path, JSON.stringify(data));
	fetch(baseUrl + path, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded',
				'X-Requested-With': 'XMLHttpRequest',
				'satoken': localStorage.getItem('satoken')
			},
			body: serializeToQueryString(data),
		})
		.then(response => response.json())
		.then(res => {
			console.log('返回数据：', res);
			if(res.code === 500) {
				return alert(res.msg);
			}
			successFn(res);
		})
		.catch(error => {
			console.error('请求失败:', error);
			return alert("异常：" + JSON.stringify(error));
		});
}

// ------------ 工具方法 ---------------

// 从url中查询到指定名称的参数值
function getParam(name, defaultValue) {
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i = 0; i < vars.length; i++) {
		var pair = vars[i].split("=");
		if (pair[0] == name) {
			return pair[1];
		}
	}
	return (defaultValue == undefined ? null : defaultValue);
}

// 将 json 对象序列化为kv字符串，形如：name=Joh&age=30&active=true
function serializeToQueryString(obj) {
	return Object.entries(obj)
		.filter(([_, value]) => value != null) // 过滤 null 和 undefined
		.map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
		.join('&');
}

// 向指定标签里 set 内容 
function setHtml(select, html) {
	const dom = document.querySelector('.is-login');
	if(dom) {
		dom.innerHTML = html;
	}
}
