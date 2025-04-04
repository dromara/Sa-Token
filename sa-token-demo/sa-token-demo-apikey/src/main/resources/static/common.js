// 服务器接口主机地址
var baseUrl = "http://localhost:8081";

// 封装一下Ajax
function ajax(path, data, successFn, errorFn) {
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
			if(res.code == 200) {
				successFn(res);
			} else {
				if(errorFn) {
					errorFn(res);
				} else {
					showMsg('错误：' + res.msg);
				}
			}
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

// 带动画的弹出提示 
function showMsg(message) {
	const alertBox = document.createElement('div');

	// 初始样式（包含隐藏状态）
	Object.assign(alertBox.style, {
		position: 'fixed',
		left: '50%',
		top: '50%',
		transform: 'translate(-50%, -50%) scale(0.8) translateY(-30px)', // 初始缩放+位移
		opacity: '0',
		background: 'rgba(0, 0, 0, 0.85)',
		color: 'white',
		padding: '16px 32px',
		borderRadius: '8px',
		transition: 'all 0.4s cubic-bezier(0.68, -0.55, 0.27, 1.55)', // 弹性动画曲线
		pointerEvents: 'none',
		whiteSpace: 'nowrap',
		fontSize: '16px',
		boxShadow: '0 4px 12px rgba(0,0,0,0.25)' // 添加投影增强立体感
	});
	alertBox.textContent = message;

	document.body.appendChild(alertBox);

	// 强制重绘确保动画触发
	void alertBox.offsetHeight;

	// 应用入场动画
	Object.assign(alertBox.style, {
		opacity: '1',
		transform: 'translate(-50%, -50%) scale(1) translateY(-20px)'
	});

	// 自动消失逻辑
	setTimeout(() => {
		Object.assign(alertBox.style, {
			opacity: '0',
			transform: 'translate(-50%, -50%) scale(0.9) translateY(-20px)'
		});

		alertBox.addEventListener('transitionend', () => {
			alertBox.remove();
		}, {
			once: true
		});
	}, 3000);
}

// 将日期格式化  yyyy-MM-dd HH:mm:ss
function formatDateTime(date) {
	date = new Date(date);
	// 补零函数
	const pad = (n, len) => n.toString().padStart(len, '0');

	// 分解时间组件
	const year = date.getFullYear();
	const month = pad(date.getMonth() + 1, 2); // 0-11 → 1-12
	const day = pad(date.getDate(), 2);
	const hours = pad(date.getHours(), 2); // 24小时制
	const minutes = pad(date.getMinutes(), 2);
	const seconds = pad(date.getSeconds(), 2);
	const milliseconds = pad(date.getMilliseconds(), 3);

	// 拼接格式
	// return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}.${milliseconds}`;
	return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}