// 暗色主题（参考常见 IDE）
const ST_DARK_THEMES = [
	{ id: 'vscode-dark', name: 'VS Code Dark+', color: '#1e1e1e' },
	{ id: 'darcula', name: 'JetBrains Darcula', color: '#2b2b2b' },
	{ id: 'one-dark', name: 'Atom One Dark', color: '#282c34' },
	{ id: 'dracula', name: 'Dracula', color: '#282a36' },
	{ id: 'tokyo-night', name: 'Tokyo Night', color: '#1a1b26' }
];

const ST_THEME_MODE_KEY = 'st-theme-mode';
const ST_DARK_ID_KEY = 'st-dark-theme-id';
const ST_BG_COLOR_KEY = 'bg-color-value';

function getDarkTheme(id) {
	for (let i = 0; i < ST_DARK_THEMES.length; i++) {
		if (ST_DARK_THEMES[i].id === id) {
			return ST_DARK_THEMES[i];
		}
	}
	return null;
}

function clearLightInlineStyles() {
	document.body.style.backgroundColor = '';
	const header = document.querySelector('.doc-header');
	if (header) {
		header.style.backgroundColor = '';
	}
}

// 应用暗色主题
function setDarkTheme(themeId) {
	const theme = getDarkTheme(themeId);
	if (!theme) {
		return;
	}
	clearLightInlineStyles();
	document.body.setAttribute('data-st-theme', theme.id);
	console.log('---- 暗色主题设定为：', theme.name);
}

// 绑定修改背景色 / 暗色主题
$('.theme-box').on('click', 'span', function() {
	const darkId = this.getAttribute('data-st-dark');
	const rect = this.getBoundingClientRect();
	const x = rect.left + rect.width / 2;
	const y = rect.top + rect.height / 2;

	if (darkId) {
		const theme = getDarkTheme(darkId);
		if (!theme) {
			return;
		}
		createWaterDrop(x - 7, y + 5, theme.color, function() {
			setDarkTheme(theme.id);
		});
		localStorage.setItem(ST_THEME_MODE_KEY, 'dark');
		localStorage.setItem(ST_DARK_ID_KEY, theme.id);
		return;
	}

	// 浅色：只改背景
	const bgColor = this.style.backgroundColor;
	createWaterDrop(x - 7, y + 5, bgColor, function() {
		document.body.removeAttribute('data-st-theme');
		setBg(bgColor);
	});
	localStorage.setItem(ST_THEME_MODE_KEY, 'light');
	localStorage.removeItem(ST_DARK_ID_KEY);
	localStorage.setItem(ST_BG_COLOR_KEY, bgColor);
});

// 创建水滴动画（onDone：扩散结束后的回调）
function createWaterDrop(x, y, color, onDone) {
	const waterDrop = document.createElement('div');
	waterDrop.className = 'water-drop';
	waterDrop.style.backgroundColor = color;
	waterDrop.style.left = `${x}px`;
	waterDrop.style.top = `${y}px`;

	document.body.appendChild(waterDrop);

	const viewportHeight = window.innerHeight;

	gsap.to(waterDrop, {
		top: viewportHeight - 30,
		duration: 1.5,
		ease: "power2.in",
		onComplete: function() {
			document.body.removeChild(waterDrop);
			createColorWave(x, viewportHeight, color, onDone);
		}
	});
}


// 创建颜色扩散效果
function createColorWave(x, y, color, onDone) {
	const colorWave = document.createElement('div');
	colorWave.className = 'color-wave';
	colorWave.style.backgroundColor = color;

	const maxDistance = Math.sqrt(
		Math.pow(Math.max(x, window.innerWidth - x), 2) +
		Math.pow(Math.max(y, window.innerHeight - y), 2)
	);

	colorWave.style.width = `${maxDistance * 2}px`;
	colorWave.style.height = `${maxDistance * 2}px`;
	colorWave.style.left = `${x - maxDistance}px`;
	colorWave.style.top = `${y - maxDistance}px`;

	document.body.appendChild(colorWave);

	gsap.to(colorWave, {
		scale: 1,
		duration: 1.2,
		ease: "power2.out",
		onComplete: function() {
			if (typeof onDone === 'function') {
				onDone();
			} else {
				setBg(color);
			}

			setTimeout(() => {
				document.body.removeChild(colorWave);
			}, 500);
		}
	});
}


// 读取上次记录
(function restoreTheme() {
	const mode = localStorage.getItem(ST_THEME_MODE_KEY);
	if (mode === 'dark') {
		const darkId = localStorage.getItem(ST_DARK_ID_KEY);
		if (getDarkTheme(darkId)) {
			setDarkTheme(darkId);
			return;
		}
	}

	const bgColor = localStorage.getItem(ST_BG_COLOR_KEY);
	if (bgColor) {
		document.body.removeAttribute('data-st-theme');
		setBg(bgColor);
	}
})();

// 设置背景颜色（浅色模式）
function setBg(bgColor) {
	console.log('---- 背景颜色设定为：', bgColor);

	document.body.style.backgroundColor = bgColor;

	let headerBg = bgColor;
	// 如果是 16 进制，转 rgba
	if (headerBg.indexOf('#') == 0) {
		headerBg = hexToRgba(headerBg, 0.97);
	}
	// 如果是 rgb，转 rgba
	else if (headerBg.match(/\,/g) && headerBg.match(/\,/g).length == 2) {
		headerBg = headerBg.replace(')', ' ,0.97)');
	}

	document.querySelector('.doc-header').style.backgroundColor = headerBg;
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
	let arr = [];
	for (var i = 0; i < 6; i = i + 2) {
		let s = newStr.slice(i, i + 2)
		arr.push(parseInt("0x" + s))
	}
	return 'rgb(' + arr.join(",") + ', ' + a + ')';
}
