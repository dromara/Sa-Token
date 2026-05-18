
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
	const SAVE_KEY = 'isShowStarGuide';
	try{
		const showAlert = localStorage[SAVE_KEY];
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
	
	// 本次打开页面的内存内已经弹出了的话，也不再弹了 
	if(window.isYtcXsjfkasjda3232) {
		return;
	}
	window.isYtcXsjfkasjda3232 = true;
	
	// 弹出弹框，邀请填写 
	const tipStr = `
		<div style="color: #000;">
			<div>
				<iframe src="./a/star-guide/index.html" 
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
				localStorage[SAVE_KEY] = new Date().getTime();
			}
		}, 
		// 点击确定
		function(index) {
			layer.close(index);
			localStorage[SAVE_KEY] = new Date().getTime();
			open('https://gitee.com/dromara/sa-token');
			// open('https://github.com/dromara/sa-token');
			// open('https://atomgit.com/dromara/sa-token');
			
		},
		// 点击取消
		function(){
			localStorage[SAVE_KEY] = new Date().getTime();
		}
	);
}	
isShowStarGuide();
