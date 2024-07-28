// 提取次级导航栏显示到右上角 
// 

// 是否都开右边菜单
let isOpenRightSubTitle = false;

		// 重新定位 active-rep 对应的菜单 
function positioningVmActiveRep(vm) {
	const vmPath = '#' + vm.route.path;
	$('.sidebar-nav>ul>li>ul>li>a').each(function(item) {
		if($(this).attr('href') === vmPath) {
			// $(this).parent().attr('active-rep', true);
			$(this).parent().addClass('active-rep')
			// console.log($(this));
		}
	})
}

function subNavDraw(hook, vm) {
	
    // 钩子函数：每次路由切换时数据全部加载完成后调用，没有参数。
    hook.doneEach(function () {
		// 只在宽屏下展现，太小的屏幕不展现 
		if(document.body.clientWidth < 1100) {
			isOpenRightSubTitle = false;
			return;
		} else {
			isOpenRightSubTitle = true;
		}
		
		// 修改高度 
		const $dom = $('.app-sub-sidebar');
		$('.doc-right-more-item').css({ top: ($dom.height() + 80) + 'px' })
		
		// 重新定位 active-rep 对应的菜单 
		positioningVmActiveRep(vm);
    })
	
	
	// 钩子函数：初始化并第一次加载完成数据后调用，没有参数。
	hook.ready(function () {
		
	})
	
	
}

window.$docsify.plugins = [].concat(subNavDraw, window.$docsify.plugins)

// 滚动时设置一下左侧滚动条高度，不要超出可视区域 
$(document).scroll(function(){
	if(isOpenRightSubTitle) {
		const offsetTop = $('.active-rep').get(0).offsetTop;
		$('.sidebar').scrollTop(offsetTop - ($('.sidebar').height() / 2))
	}
})
