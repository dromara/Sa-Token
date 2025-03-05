// 服务器接口主机地址 
var baseUrl = "http://localhost:8081";

// 封装一下Ajax
function ajax(path, data, successFn) {
	console.log(baseUrl + path);
	fetch(baseUrl + path, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded',
			'satoken': localStorage.getItem('satoken')
		},
		body: serializeToQueryString(data),		
	})
	.then(response => response.json())
	.then(res => {
		console.log('返回数据：', res);
		successFn(res);
	})
	.catch(error => {
		console.error('提交失败:', error);
		return alert("异常：" + JSON.stringify(error));
	});
}

// 获取本地的 设备id 
function getLocalDeviceId() {
	let localDeviceId = localStorage.getItem('local-device-id');
	if(!localDeviceId) {
		localDeviceId = randomString(60);
		localStorage.setItem('local-device-id', localDeviceId);
	}
	return localDeviceId;
}



// ------------ 工具方法 ---------------

// 从url中查询到指定名称的参数值 
function getParam(name, defaultValue){
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i=0;i<vars.length;i++) {
		var pair = vars[i].split("=");
		if(pair[0] == name){return pair[1];}
	}
	return(defaultValue == undefined ? null : defaultValue);
}

// 将 json 对象序列化为kv字符串，形如：name=Joh&age=30&active=true
function serializeToQueryString(obj) {
  return Object.entries(obj)
    .filter(([_, value]) => value != null) // 过滤 null 和 undefined
    .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
    .join('&');
}

// 随机生成字符串 
function randomString(len) {
　　len = len || 32;
　　var $chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890'; 
　　var maxPos = $chars.length;
　　var str = '';
　　for (i = 0; i < len; i++) {
　　　　str += $chars.charAt(Math.floor(Math.random() * maxPos));
　　}
　　return str;
}