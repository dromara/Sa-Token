// 

// 声明 docsify 插件
var isFillInWjPlugin = function(hook, vm) {
	
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
		isFillIn(vm);
	});
	
	// 钩子函数：初始化并第一次加载完成数据后调用，没有参数。
	hook.ready(function() {
		
	});
	
}


// 检查成功后，多少天不再检查 
const wjAllowDisparity = 1000 * 60 * 60 * 24 * 30 * 3;
// const allowDisparity = 1000 * 10;


// 判断当前是否已填写 
function isFillIn(vm) {
	// 非PC端不检查
	if(document.body.offsetWidth < 800) {
		console.log('small screen ... wj ');
		return;
	}
	
	// 白名单路由不判断
	const whiteList = ['/', '/more/link', '/more/demand-commit', '/more/join-group', '/more/sa-token-donate', '/more/wenjuan', 
			'/sso/sso-pro', '/more/update-log', '/more/common-questions', '/fun/sa-token-test', '/fun/issue-template'];
	if(whiteList.indexOf(vm.route.path) >= 0) {
		console.log('white route ... wj');
		return;
	}
	
	// 判断是否近期已经判断过了
	try{
		const isFillIn = localStorage.isFillIn;
		if(isFillIn) {
			// 记录 star 的时间，和当前时间的差距
			const disparity = new Date().getTime() - parseInt(isFillIn);
			
			// 差距小于一月，不再检测，大于一月，再检测一下
			if(disparity < wjAllowDisparity) {
				console.log('checked ... wj ');
				return;
			}
		}
	}catch(e){
		console.error(e);
	}
	
	// 本次打开页面的内存内已经弹出了的话，也不再弹了 
	if(window.isYtcXsjfkasjda) {
		return;
	}
	window.isYtcXsjfkasjda = true;
	
	// 弹出弹框，邀请填写 
	const tipStr = `
		<div>
			<h3>
				嗨，同学你好！  
			</h3>
			<p>
				我们想以运营一款产品的心态来运营一个开源框架，所以我们迫切希望您能够填写这份问卷，这有 6 道选择题，
				应该只会略微占用您 1~3 分钟的时间。  
			</p>
			<p>问卷地址：<a href="https://wj.qq.com/s2/14587150/b5b4/" target="_blank">https://wj.qq.com/s2/14587150/b5b4/</a></p>
			<p>Sa-Token 将会非常重视每一位粉丝的宝贵意见！😇😇😇</p>
		</div>
		`;
	
	const index = layer.confirm(tipStr, {
			title: '问卷调查填写邀请', 
			btn: ['我已填写 (1月内不再弹出)', '暂时不要 (1天内不再弹出)'], 
			// btn: ['同意授权检测', '暂时不要，我先看看文档'], 
			area: '480px', 
			offset: '30%'
		}, 
		// 点击确定
		function(index) {
			layer.close(index);
			localStorage.isFillIn = new Date().getTime();
			
			layer.msg('感谢你的支持，Sa-Token 将努力变得更加完善！  ❤️ ❤️ ❤️ ')
		},
		// 点击取消
		function(){
			// 一天内不再检查
			const ygTime = allowDisparity - (1000 * 60 * 60 * 24);
			localStorage.isFillIn = new Date().getTime() - ygTime;
			
			layer.alert('你可以随时在右上角 [ 相关资源 -> 问卷调查 ] 处找到问卷链接', function(index) {
				layer.close(index);
			})
		}
	);
}

