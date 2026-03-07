// 绑定修改背景色的按钮事件
$('.theme-box span').click(function() {
	// 获取主题色 
	let bgColor = this.style.backgroundColor;

	// 获取点击位置
	const rect = this.getBoundingClientRect();
	const x = rect.left + rect.width / 2;
	const y = rect.top + rect.height / 2;

	// 创建水滴元素
	createWaterDrop(x - 7, y + 5, bgColor);

	// setBg(bgColor);
	localStorage.setItem('bg-color-value', bgColor)
})

// 创建水滴动画
function createWaterDrop(x, y, color) {
	// 创建水滴元素
	const waterDrop = document.createElement('div');
	waterDrop.className = 'water-drop';
	waterDrop.style.backgroundColor = color;
	waterDrop.style.left = `${x}px`;
	waterDrop.style.top = `${y}px`;

	// 添加到文档中
	document.body.appendChild(waterDrop);

	// 获取视口高度
	const viewportHeight = window.innerHeight;

	// 使用GSAP创建水滴下落动画
	gsap.to(waterDrop, {
		top: viewportHeight - 30, // 调整为视口底部内，避免触发滚动条
		duration: 1.5,
		ease: "power2.in", // 加速度下落
		onComplete: function() {
			// 动画完成后移除水滴
			document.body.removeChild(waterDrop);

			// 创建颜色扩散效果
			createColorWave(x, viewportHeight, color);
		}
	});
}


// 创建颜色扩散效果
function createColorWave(x, y, color) {
	// 创建颜色波元素
	const colorWave = document.createElement('div');
	colorWave.className = 'color-wave';
	colorWave.style.backgroundColor = color;

	// 计算所需的最小半径（确保能覆盖整个视口）
	const maxDistance = Math.sqrt(
		Math.pow(Math.max(x, window.innerWidth - x), 2) +
		Math.pow(Math.max(y, window.innerHeight - y), 2)
	);

	// 设置颜色波的初始位置和大小
	colorWave.style.width = `${maxDistance * 2}px`;
	colorWave.style.height = `${maxDistance * 2}px`;
	colorWave.style.left = `${x - maxDistance}px`;
	colorWave.style.top = `${y - maxDistance}px`;

	// 确保 colorWave 在所有内容之下
	// const contentElements = document.querySelectorAll('nav, main, footer');
	// contentElements.forEach(el => {
	// 	if (!el.style.zIndex || parseInt(el.style.zIndex) <= 10) {
	// 		el.style.zIndex = '20';
	// 	}
	// });

	// 添加到文档中
	document.body.appendChild(colorWave);

	// 使用 GSAP 创建扩散动画
	gsap.to(colorWave, {
		scale: 1,
		duration: 1.2,
		ease: "power2.out",
		onComplete: function() {
			// 动画完成后更改背景色
			// document.body.style.backgroundColor = color;
			setBg(color)

			// 延迟移除颜色波
			setTimeout(() => {
				document.body.removeChild(colorWave);
			}, 500);
		}
	});
}


// 读取上次记录
let bgColor = localStorage.getItem('bg-color-value');
if (bgColor) {
	setBg(bgColor);
}

// 设置背景颜色 
function setBg(bgColor) {
	console.log('---- 背景颜色设定为：', bgColor);

	// -------- 设置 body 背景
	document.body.style.backgroundColor = bgColor;

	// -------- 设置 header 头背景
	// 如果是 16 进制，转 rgba
	if (bgColor.indexOf('#') == 0) {
		bgColor = hexToRgba(bgColor, 0.97);
	}
	// 如果是 rgb，转 rgba
	else if (bgColor.match(/\,/g).length == 2) {
		bgColor = bgColor.replace(')', ' ,0.97)');
	}

	document.querySelector('.doc-header').style.backgroundColor = bgColor;
}

// 16进制 转 rgba
function hexToRgba(str, a) {
	a = a || 1;

	var reg = /^#([0-9A-Fa-f]{3}|[0-9A-Fa-f]{6})$/
	if (!reg.test(str)) {
		return;
	}
	let newStr = (str.toLowerCase()).replace(/\#/g, '')
	let len = newStr.length;
	if (len == 3) {
		let t = ''
		for (var i = 0; i < len; i++) {
			t += newStr.slice(i, i + 1).concat(newStr.slice(i, i + 1))
		}
		newStr = t
	}
	let arr = []; //将字符串分隔，两个两个的分隔
	for (var i = 0; i < 6; i = i + 2) {
		let s = newStr.slice(i, i + 2)
		arr.push(parseInt("0x" + s))
	}
	return 'rgb(' + arr.join(",") + ', ' + a + ')';
}