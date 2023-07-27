function initSwiper () {
	if(window.swiper){
		return;
	}
	window.swiper = new Swiper(".mySwiper", {
		// 最大容纳的slide数量，auto=自动
		slidesPerView: "auto",
		// 主角 slide 居中 
		centeredSlides: true,
		// 使左右 slide 贴合容器
		// centeredSlidesBounds: true,
		// 循环 
		loop: true,
		// 自动播放 
		autoplay: {
			// 3秒切换一次
			delay: 3000,
		},
		// slide 间距 
		spaceBetween: 30,
		// 点击 slide 时，过渡到这个 slide 
		slideToClickedSlide: true,
		// 切换效果 slide=普通位移、fade=淡入、cube=方块、coverflow=3d流、flip=3d翻转、cards=卡片式、creative=创意性
		effect: 'coverflow',
		// 抓取时，鼠标变小手 
        grabCursor: true,
		// 分页器 
		pagination: {
			el: ".swiper-pagination",
			// 点击时切换 slide
			clickable: true,
			// 分页器样式，bullets=原点，fraction=分式，progressbar=进度条，custom=自定义
			type: "bullets",
			// 点击小点，切换 slide 
			clickable :true,
			// 将按钮从小点变成数字 
			renderBullet: function (index, className) {
				return '<span class="' + className + '">' + (index + 1) + '</span>';
			},
		},
		// 左右切换按钮 
		navigation: {
			nextEl: ".swiper-button-next",
			prevEl: ".swiper-button-prev",
		},
	});
}

$(function(){
	initSwiper();
})

// 滚动到 swiper 时，再加载 
// $(document).scroll(function(){
// 	// 页面滚动条高度 > ry盒子到顶部距离 + window 视口高度 时，swiper出现 
// 	if($(document).scrollTop() > $('.ry-kuai').offset().top - $(window).height()) {
// 		initSwiper();
// 	}
// })