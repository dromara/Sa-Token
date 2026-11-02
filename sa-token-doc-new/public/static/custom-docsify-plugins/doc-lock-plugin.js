// 章节锁定插件 

// 声明 docsify 插件
var docLockPlugin = function(hook, vm) {
	
	// 钩子函数：解析之前执行
	hook.beforeEach(function(content) {
		return content;
	});
	
	// 钩子函数：每次路由切换时，解析内容之后执行 
	hook.afterEach(function(html) {
		return html;
	});
	
	// 钩子函数：每次路由切换时数据全部加载完成后调用，没有参数。
	hook.doneEach(function() {
		// isShowTanChuang(vm);
	});
	
	// 钩子函数：初始化并第一次加载完成数据后调用，没有参数。
	hook.ready(function() {
		
	});
	
	
	
	
	
	// ======================================== 弹窗方法 
	
	// 检查成功后，多少天不再检查 
	const dl_AllowDisparity = 1000 * 60 * 60 * 24 * 30 * 1;  // 1个月
	// 拦截 path ，如果填 /** 代表所有路径，填 /sso/* 代表 /sso/ 目录下所有路径
	const dl_exeArray = [
		'/sso/*', '/oauth2/*', '/more/common-questions', '/up/*', '/micro/*', '/plugin/*'
	];
	// 排除 path 
	const dl_excludeArray = [
		'/sso/readme', '/oauth2/readme'
	];
	// 本次存储时，使用的标记 key
	const dl_saveKey = 'dl_saveKey';
	
	
	// 判断当前是否应该弹出 
	function isShowTanChuang(vm) {
		// 非PC端不检查
		// if(document.body.offsetWidth < 800) {
		// 	console.log('small screen ... wj ');
		// 	return;
		// }
		
		// 判断是否需要拦截 
		const isExe = isExePath(vm.route.path, dl_exeArray, dl_excludeArray);
		if(!isExe) {
			return;
		}
		
		// 判断是否近期已经判断过了
		try{
			const flagTime = localStorage[dl_saveKey];
			if(flagTime) {
				// 记录 存储 的时间，和当前时间的差距
				const disparity = new Date().getTime() - parseInt(flagTime);
				
				// 差距小于指定时间，不再检测 
				if(disparity < dl_AllowDisparity) {
					console.log('checked ... docLock ');
					return;
				}
			}
		}catch(e){
			console.error(e);
		}
		
		// 本次打开页面的内存内已经弹出了的话，也不再弹了 
		// if(window.isYtcXsjfkasjdaaaa) {
		// 	return;
		// }
		// window.isYtcXsjfkasjdaaaa = true;
		
		// 验证成功的回调
		const okFn = function() {
			console.log('ok 了');
			localStorage.setItem(dl_saveKey, new Date().getTime() );
			$('body').css({'overflow': 'auto'});
			layer.msg('感谢你的支持，Sa-Token 将努力变得更加完善！  ❤️ ❤️ ❤️ ');
		}
		// 点了返回的回调 
		const backFu = function() {
			$('body').css({'overflow': 'auto'});
			location.href = '#/';
		}
		// 弹窗验证 
		showDocLock(okFn, backFu);
		$('body').css({'overflow': 'hidden'});
		
		// 弹出弹框，邀请填写 
		return;
	}
	
	
	// ======================================== 路径判断
	
	/**
	 * 判断一个路径，是否会被成功拦截，返回 true 或 false 
	 * @param {Object} path   要判断的路径，例如：/sso/apidoc
	 * @param {Object} exeArray   要拦截的路径数组，例如：['/sso/*', '/oauth2/*', '/more/common-questions'  ]，如果填 /** 代表所有路径，填 /sso/* 代表 /sso/ 目录下所有路径
	 * @param {Object} excludeArray  要排除的路径数组，规则同上 
	 */
	function isExePath( path, exeArray,  excludeArray) {
		 // 参数验证和初始化
		exeArray = exeArray || [];
		excludeArray = excludeArray || [];
		
		// 标准化路径，确保以 / 开头
		path = normalizePath(path);
		
		// 先检查排除规则（优先级更高）
		for (let pattern of excludeArray) {
			if (matchPattern(path, pattern)) {
				return false; // 被排除，不拦截
			}
		}
		
		// 再检查拦截规则
		for (let pattern of exeArray) {
			if (matchPattern(path, pattern)) {
				return true; // 需要拦截
			}
		}
		
		return false; // 默认不拦截
	}
	
	/**
	 * 标准化路径
	 */
	function normalizePath(path) {
	    if (!path) return '/';
	    if (!path.startsWith('/')) return '/' + path;
	    return path;
	}
	
	/**
	 * 增强版模式匹配
	 */
	function matchPattern(path, pattern) {
	    // 处理空值
	    if (!pattern) return false;
	    
	    pattern = pattern.trim();
	    
	    // /** 匹配所有
	    if (pattern === '/**' || pattern === '**') {
	        return true;
	    }
	    
	    // 处理前置和后置通配符
	    if (pattern === '*' || pattern === '/*') {
	        return true;
	    }
	    
	    // 精确匹配
	    if (!pattern.includes('*')) {
	        return path === pattern || path === normalizePath(pattern);
	    }
	    
	    // 转换模式为正则表达式
	    const regexStr = pattern
	        // 转义正则特殊字符
	        .replace(/[.+?^${}()|[\]\\]/g, '\\$&')
	        // 处理 ** 通配符（匹配多级目录）
	        .replace(/\/\*\*/g, '(/.*)?')
	        // 处理 * 通配符（匹配单级目录）
	        .replace(/\*/g, '[^/]*')
	        // 确保匹配完整路径
	        .replace(/^\//, '^/')
	        .replace(/$/, '$');
	    
	    try {
	        const regex = new RegExp(regexStr);
	        return regex.test(path);
	    } catch (e) {
	        console.error(`Invalid pattern: ${pattern}`, e);
	        return false;
	    }
	}
	
	
	
}








// =========================== AI 生成的弹窗代码

function initTanChuangFun() {
	
	// 配置项
	const CONFIG = {
		correctPassword: 'sa-token yyds', // 正确密码
		qqImageUrl: './big-file/doc/zong/doc-lock-pre-qq.png',
		wechatImageUrl: './big-file/doc/zong/doc-lock-pre-wx.png'
	};
	
	// 弹窗HTML模板
	const modalHTML = `
		<div class="modal-overlay" id="passwordModal">
			<div class="modal">
				<div class="modal-header">
					<h2>🔒 本章节已锁定，输入密码后即可正常访问：</h2>
				</div>
				<div class="modal-body">
					<p class="error-message" id="errorMessage">密码错误，请重新输入！</p>
					
					<div class="password-form">
						<input type="text" class="password-input" id="passwordInput" placeholder="加群可获得访问密码" autocomplete="off">
						
						<div class="form-actions">
							<button class="form-btn btn-verify" id="verifyBtn">验证</button>
							<button class="form-btn btn-back" id="backBtn">回首页</button>
						</div>
					</div>
					
					<div class="password-help-section">
						<div class="help-text">
							加入 QQ群 或 微信群 后可在群公告查看密码：<a href="#/more/join-group" target="_black">加群链接</a>
						</div>
						
						<div class="images-container">
							<div class="qr-image-container">
								<img src="${CONFIG.qqImageUrl}" alt="QQ 群公告" class="qr-image">
								<div class="image-label">QQ 群公告</div>
							</div>
							<div class="qr-image-container">
								<img src="${CONFIG.wechatImageUrl}" alt="微信群公告" class="qr-image">
								<div class="image-label">微信群公告</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="image-overlay" id="imageOverlay">
			<img src="" alt="放大图片" class="enlarged-image" id="enlargedImage">
		</div>
	`;
	
	// 初始化变量
	let passwordModal, passwordInput, errorMessage, verifyBtn, backBtn;
	let imageOverlay, enlargedImage;
	
	
	let okCallFn = null;
	let backCallFn = null;
	
	/**
	 * 初始化弹窗
	 * 将弹窗HTML插入到页面中，并绑定事件
	 */
	function initModal() {
		// 插入弹窗HTML到页面
		document.body.insertAdjacentHTML('beforeend', modalHTML);
		
		// 获取DOM元素
		passwordModal = document.getElementById('passwordModal');
		passwordInput = document.getElementById('passwordInput');
		errorMessage = document.getElementById('errorMessage');
		verifyBtn = document.getElementById('verifyBtn');
		backBtn = document.getElementById('backBtn');
		imageOverlay = document.getElementById('imageOverlay');
		enlargedImage = document.getElementById('enlargedImage');
		
		// 绑定事件
		bindEvents();
	}
	
	/**
	 * 绑定所有事件
	 */
	function bindEvents() {
		// 触发按钮点击事件
		// document.getElementById('accessBtn').addEventListener('click', openModal);
		
		// 验证按钮点击事件
		verifyBtn.addEventListener('click', validatePassword);
		
		// 返回按钮点击事件
		backBtn.addEventListener('click', function(){
			closeModal();
			backCallFn();
		});
		
		// 输入框回车事件
		passwordInput.addEventListener('keypress', function(e) {
			if (e.key === 'Enter') {
				validatePassword();
			}
		});
		
		// 只在非移动端绑定图片点击事件
		if (!isMobileDevice()) {
			// 图片点击放大事件
			document.querySelectorAll('.qr-image').forEach(img => {
				img.addEventListener('click', function() {
					enlargeImage(this.src);
				});
			});
			
			// 放大图片关闭事件
			imageOverlay.addEventListener('click', function(e) {
				if (e.target === this || e.target === enlargedImage) {
					closeImageOverlay();
				}
			});
			
			// ESC键关闭放大图片
			document.addEventListener('keydown', function(e) {
				if (e.key === 'Escape' && imageOverlay.classList.contains('active')) {
					closeImageOverlay();
				}
			});
		}
	}
	
	/**
	 * 检测是否为移动设备
	 * @returns {boolean} 是否为移动设备
	 */
	function isMobileDevice() {
		return window.innerWidth <= 768;
	}
	
	/**
	 * 打开密码弹窗
	 */
	function openModal() {
		passwordModal.classList.add('active');
		passwordInput.focus();
		errorMessage.classList.remove('show');
		passwordInput.value = '';
	}
	
	/**
	 * 关闭密码弹窗
	 */
	function closeModal() {
		passwordModal.classList.remove('active');
	}
	
	/**
	 * 密码验证函数
	 * 宽松验证策略：允许左右空格，中间空格可省略
	 */
	function validatePassword() {
		let enteredPassword = passwordInput.value.trim(); // 去除左右空格
		
		// 标准化：移除所有空格
		const normalizedEntered = enteredPassword.replace(/\s+/g, '');
		const normalizedCorrect = CONFIG.correctPassword.replace(/\s+/g, '');
		
		if (normalizedEntered === normalizedCorrect) {
			// 密码正确，解锁章节
			unlockChapter();
		} else {
			// 密码错误，显示错误信息
			showError();
		}
	}
	
	/**
	 * 显示密码错误提示
	 */
	function showError() {
		errorMessage.classList.add('show');
		passwordInput.value = '';
		passwordInput.focus();
		
		// 添加抖动效果
		passwordInput.classList.add('shake');
		setTimeout(() => {
			passwordInput.classList.remove('shake');
		}, 500);
	}
	
	/**
	 * 解锁章节
	 */
	function unlockChapter() {
		closeModal();
		okCallFn();
		
		
		// // 更新章节内容
		// const lockedSection = document.querySelector('.locked-section');
		// const tocLockedItems = document.querySelectorAll('.toc a.locked');
		
		// // 更新章节显示
		// lockedSection.innerHTML = `
		// 	<h3><i class="fas fa-unlock-alt" style="color:#2ecc71;"></i> 章节已解锁</h3>
		// 	<p>感谢您加入我们的社区！现在您可以查看高级配置指南的全部内容。</p>
		// 	<div style="text-align: left; margin-top: 20px;">
		// 		<h4>高级配置内容示例：</h4>
		// 		<p>1. 自定义插件开发：详细讲解如何为项目开发自定义插件，包括插件结构、API接口和最佳实践。</p>
		// 		<p>2. 性能调优指南：深入分析项目性能瓶颈，并提供多种优化方案和调优技巧。</p>
		// 		<p>3. 高级集成方案：介绍如何将项目与其他流行框架和工具进行深度集成。</p>
		// 		<p>4. 企业级部署：针对生产环境的企业级部署方案，包括高可用、负载均衡和监控配置。</p>
		// 	</div>
		// 	<p style="margin-top: 20px; color: #27ae60; font-weight: 600;">
		// 		<i class="fas fa-check-circle"></i> 您现在可以访问所有高级章节内容了！
		// 	</p>
		// `;
		
		// // 更新目录状态
		// tocLockedItems.forEach(item => {
		// 	if (item.textContent.includes('高级配置指南')) {
		// 		item.classList.remove('locked');
		// 		item.innerHTML = '<i class="fas fa-unlock-alt" style="color:#2ecc71;"></i> 高级配置指南';
		// 	}
		// });
		
		// // 显示成功通知
		// showNotification('章节解锁成功！您现在可以访问高级配置指南。');
	}
	
	/**
	 * 放大图片
	 * @param {string} src - 图片地址
	 */
	function enlargeImage(src) {
		enlargedImage.src = src;
		imageOverlay.classList.add('active');
	}
	
	/**
	 * 关闭图片放大层
	 */
	function closeImageOverlay() {
		imageOverlay.classList.remove('active');
	}
	
	/**
	 * 显示通知
	 * @param {string} message - 通知内容
	 */
	function showNotification(message) {
		const notification = document.createElement('div');
		notification.style.cssText = `
			position: fixed;
			top: 20px;
			right: 20px;
			background-color: #2ecc71;
			color: white;
			padding: 15px 25px;
			border-radius: 4px;
			box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
			z-index: 1001;
			font-weight: 600;
			display: flex;
			align-items: center;
			gap: 10px;
			transform: translateX(150%);
			transition: transform 0.5s ease;
		`;
		
		notification.innerHTML = `<i class="fas fa-check-circle"></i><span>${message}</span>`;
		document.body.appendChild(notification);
		
		// 显示通知
		setTimeout(() => {
			notification.style.transform = 'translateX(0)';
		}, 10);
		
		// 3秒后隐藏
		setTimeout(() => {
			notification.style.transform = 'translateX(150%)';
			setTimeout(() => {
				document.body.removeChild(notification);
			}, 500);
		}, 3000);
	}
	
	/**
	 * 添加抖动动画样式
	 */
	function addShakeAnimation() {
		const style = document.createElement('style');
		style.textContent = `
			@keyframes shake {
				0%, 100% { transform: translateX(0); }
				10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
				20%, 40%, 60%, 80% { transform: translateX(5px); }
			}
			.shake {
				animation: shake 0.5s;
				border-color: #e74c3c !important;
			}
		`;
		document.head.appendChild(style);
	}
	
	// 初始化
	document.addEventListener('DOMContentLoaded', function() {
		initModal();
		addShakeAnimation();
	});
	
	// 显示弹窗： 验证成功的回调、点击返回的回调 
	window.showDocLock = function(okFn, backFn) {
		okCallFn = okFn;
		backCallFn = backFn;
		// 打开 
		openModal();
	}
	
};
initTanChuangFun();