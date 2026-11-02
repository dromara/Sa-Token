
const STAR_GUIDE_SAVE_KEY = 'isShowStarGuide';
const STAR_GUIDE_LAST_PATH = 'starGuideLastPath';
const STAR_GUIDE_PAGE_COUNT = 'starGuidePageCount';

// /readme.html 和 /readme、/ 和 /index.html 当成同一页；hash 不参与
function starGuidePagePath() {
	return location.pathname
		.replace(/\/index\.html$/, '/')
		.replace(/\.html$/, '') || '/';
}

// 本会话换了一个真实地址才 +1，刷新、锚点切不过来
function starGuideTouchPage() {
	const path = starGuidePagePath();
	try {
		const last = sessionStorage[STAR_GUIDE_LAST_PATH];
		let n = parseInt(sessionStorage[STAR_GUIDE_PAGE_COUNT], 10) || 0;
		if (path !== last) {
			n += 1;
			sessionStorage[STAR_GUIDE_LAST_PATH] = path;
			sessionStorage[STAR_GUIDE_PAGE_COUNT] = String(n);
		}
		return n;
	} catch (e) {
		console.error(e);
		return 1;
	}
}

// 判断当前是否已弹出
function isShowStarGuide() {
	// 非PC端不检查
	if(document.body.offsetWidth < 800) {
		console.log('small screen ... isShowStarGuide ');
		return;
	}

	// 检查成功后，多少天不再检查
	const alertAllowDisparity = 1000 * 60 * 60 * 24 * 30; // 30天
	// const allowDisparity = 1000 * 10;

	// 判断是否近期已经判断过了
	try{
		const showAlert = localStorage[STAR_GUIDE_SAVE_KEY];
		if(showAlert) {
			// 记录 star 的时间，和当前时间的差距
			const disparity = new Date().getTime() - parseInt(showAlert);

			// 差距小于一月，不再检测，大于一月，再检测一下
			if(disparity < alertAllowDisparity) {
				console.log('checked ... wj ');
				return;
			}
		}
	}catch(e){
		console.error(e);
	}

	// 第一次打开不弹，进到本会话第 2 个页面再弹
	if (starGuideTouchPage() < 2) {
		return;
	}

	// 本次打开页面的内存内已经弹出了的话，也不再弹了
	if(window.isYtcXsjfkasjda3232) {
		return;
	}
	window.isYtcXsjfkasjda3232 = true;

	// 弹出弹框，邀请填写
	const tipStr = `
		<div style="color: #000;">
			<div>
				<iframe src="/a/star-guide/index.html"
					style="width:100%; height:250px; border:2px solid #ddd; border-radius: 2px;"></iframe>
			</div>
			<p style="margin-top: 18px;">
				<b style="color: green;">Sa-Token 采用 Apache-2.0 开源协议，承诺框架本身与在线文档永久免费开放</b>。
			</p>
			<p style="margin-top: 14px;">
				如果 Sa-Token 帮助到了你，希望你可以为项目点个 star ⭐，这对我们非常重要，感谢支持！
			</p>
		</div>
		`;

	const index = layer.confirm(tipStr, {
			title: '支持项目',
			btn: ['确定'],
			area: '570px',
			offset: '10%',
			cancel: function() {
				localStorage[STAR_GUIDE_SAVE_KEY] = new Date().getTime();
			}
		},
		// 点击确定
		function(index) {
			layer.close(index);
			localStorage[STAR_GUIDE_SAVE_KEY] = new Date().getTime();
			open('https://gitee.com/dromara/sa-token');
			// open('https://github.com/dromara/sa-token');
			// open('https://atomgit.com/dromara/sa-token');

		},
		// 点击取消
		function(){
			localStorage[STAR_GUIDE_SAVE_KEY] = new Date().getTime();
		}
	);
}

// VitePress 切章走 history.pushState，不会重新跑本脚本，钩住 URL 变化再检查一次
function bindStarGuideUrlWatch() {
	if (window.__starGuideUrlWatch) {
		return;
	}
	window.__starGuideUrlWatch = true;
	const onUrlMaybeChange = function() {
		setTimeout(isShowStarGuide, 0);
	};
	['pushState', 'replaceState'].forEach(function(type) {
		const raw = history[type];
		history[type] = function() {
			const ret = raw.apply(this, arguments);
			onUrlMaybeChange();
			return ret;
		};
	});
	window.addEventListener('popstate', onUrlMaybeChange);
}

bindStarGuideUrlWatch();
isShowStarGuide();
