// 声明 docsify 插件
var myDocsifyPlugin = function(hook, vm) {

	// 文档页顶栏：根据当前路由高亮对应导航项
	function updateDocNavActive() {
		var header = document.querySelector('.doc-header .nav-right');
		if (!header) return;

		var hash = location.hash || '#/';
		var onDocPage = location.pathname.indexOf('doc.html') !== -1;

		header.querySelectorAll('.wzi').forEach(function(a) {
			a.classList.remove('nav-active');
		});

		header.querySelectorAll(':scope > a.wzi').forEach(function(a) {
			var href = a.getAttribute('href');
			if (!href || href.indexOf('javascript') === 0 || href.indexOf('http') === 0) return;
			if (href.indexOf('#/') === 0 && (hash === href || hash.indexOf(href + '/') === 0)) {
				a.classList.add('nav-active');
			}
		});

		header.querySelectorAll(':scope > .zk-box').forEach(function(box) {
			var trigger = box.querySelector(':scope > .wzi');
			if (!trigger) return;
			box.querySelectorAll('.zk-context a[href^="#/"]').forEach(function(link) {
				var href = link.getAttribute('href');
				if (hash === href || hash.indexOf(href + '/') === 0) {
					trigger.classList.add('nav-active');
				}
			});
		});

		var docLink = header.querySelector(':scope > a.wzi[href*="doc.html"]');
		if (docLink && onDocPage) {
			var hashNavActive = header.querySelector(':scope > a.wzi.nav-active[href^="#/"]');
			var zkNavActive = header.querySelector(':scope > .zk-box > .wzi.nav-active');
			if (!hashNavActive && !zkNavActive) {
				docLink.classList.add('nav-active');
			}
		}
	}

	// 功能6：标题下面的广告（正文区 doc-inline-ad，仅 SSO / OAuth2 章节）
	function renderDocInlineAd(routePath) {
		if (!/^\/(sso|oauth2)\//.test(routePath) || $(window).width() < 800) {
			return;
		}

		var ad = `<div class="doc-inline-ad">
			<div class="doc-inline-ad__card">
				<span class="doc-inline-ad__close" title="关闭">×</span>
				<a href="https://sa-max.cn?way=st_md_top" target="_blank">
					<div class="doc-inline-ad__body">
						<img class="doc-inline-ad__img" src="/big-file/contact/sa-token-syb-3.png" />
						<div class="doc-inline-ad__text">
							<p>一个项目搞定：同域、跨域、共享Redis、跨Redis、前后端一体、前后端分离、纯 js、vue2、vue3、非 Sa-Token 项目、非 java 项目等架构下的 SSO 认证需求。</p>
							<p>一次购买，永久授权。全源码交付，不含密 Jar。提供售后技术支持。</p>
						</div>
					</div>
				</a>
			</div>
		</div>`;

		// 如果一周内用户点击过关闭广告，则不再展现
		var allowJg = 1000 * 60 * 60 * 24 * 7;
		try {
			var closeAdTime = localStorage.closeMdTopAdTime;
			if (closeAdTime) {
				var closeAdJg = new Date().getTime() - parseInt(closeAdTime);
				if (closeAdJg < allowJg) {
					return;
				}
			}
		} catch (e) {
			console.error(e);
		}

		$('#main h1').after(ad);

		$('#main .doc-inline-ad .doc-inline-ad__close').click(function(e) {
			e.preventDefault();
			e.stopPropagation();
			layer.confirm('关闭后，一周内不再展现此信息', function() {
				$('#main .doc-inline-ad').fadeOut(1000);
				layer.msg('关闭成功');
				localStorage.closeMdTopAdTime = new Date().getTime();
			});
		});
	}
	
	// 钩子函数：解析之前执行
	hook.beforeEach(function(content) {
		try{
			// 功能 1，替换全局变量 
			content = content.replace(/\$\{sa.top.version\}/g, window.saTokenTopVersion);
			
			// 添加 [toc] 标记
			content = content.replace(/\[\[toc\]\]/g, '<div class="toc-box"></div>');
			
		}catch(e){
			// 
		}
		return content;
	});
	
	// 钩子函数：每次路由切换时，解析内容之后执行 
	hook.afterEach(function(html) {
		
		// 功能 2，文章底部添加仓库地址  
		var giteeUrl = `https://gitee.com/dromara/sa-token/tree/dev/sa-token-doc/${vm.route.file}`;
		var githubUrl = `https://github.com/dromara/sa-token/tree/dev/sa-token-doc/${vm.route.file}`;
		var atomgitUrl = `https://atomgit.com/dromara/sa-token/tree/dev/sa-token-doc/${vm.route.file}`;
		var footer = `
			<br/><br/><br/><br/><br/><br/><br/><hr/>
			<footer>
				<span>发现错误？ 您可以在 <a href="${giteeUrl}" target="_blank">Gitee</a> 或 <a href="${githubUrl}" target="_blank">GitHub</a> 或 <a href="${atomgitUrl}" target="_blank">AtomGit</a> 帮助我们完善此页文档！</span>
				或 <a href="#/more/join-group" target="_blank">加入讨论群</a> 交流反馈。
				<br/>
				<p style="font-size: 12px; color: #999;">我们坚信，即使再复杂的技术，也可以用清晰、干练、易懂的文字描述出它的具体细节，如果你在阅读文档时有难以理解的章节，那一定是我们还没有优化好它，
					请向我们 <a href="#/more/demand-commit" target="_blank">反馈</a> 你的困惑之处，我们将持续优化文档。</p>
				<br/>
				<a href="https://beian.miit.gov.cn/" target="_blank" class="footer-beian">鲁ICP备18046274号-4</a>
			</footer>
		`;
		return html + footer;
	});
	
	// 钩子函数：每次路由切换时数据全部加载完成后调用，没有参数。
	hook.doneEach(function() {
		
		// 功能3，给代码盒子，添加行数样式 
		$('pre code').each(function(){
			var lines = $(this).text().split('\n').length;
			var $numbering = $('<ul/>').addClass('code-line-box');
			$(this)
				.addClass('has-numbering')
				.parent()
				.append($numbering);
			for(i=1;i<=lines;i++){
				$numbering.append($('<li/>').text(i));
			}
		});
		
		// 功能4，添加 toc 目录 
		var dStr = "";
		$('#main h2, #main h3, #main h4, #main h5, #main h6').each(function() {
			$('.toc-box').append('<li class="toc-' + this.localName + '">' + this.innerHTML + '</li>');
		});
		
		// 功能5，统计赞助人数
		// if($('.zanzhu-count').length && $('.zanzhu-box table').length) {
		// 	$('.zanzhu-count').html($('.zanzhu-box table tr').length);
		// }
		
		// 功能5，渲染赞助数据 
		if($('.zanzhu-table').length) {
			// $('.zanzhu-count').html($('.zanzhu-box table tr').length);
			// console.log(123);
			renderDonateTable();
			onZanzhuSortClick();
		}

		// 功能7，更新文档导航高亮
		updateDocNavActive();

		// 功能6，标题下面的广告
		renderDocInlineAd(vm.route.path);
		
	});
	
	// 钩子函数：初始化并第一次加载完成数据后调用，没有参数。
	hook.ready(function() {
		updateDocNavActive();
		window.addEventListener('hashchange', updateDocNavActive);

		// 将搜索框转移到右上角 
		document.querySelector(".sear-box").innerHTML = '';
		document.querySelector(".sear-box").append(document.querySelector(".search"));
		document.querySelector(".search input").placeholder = '搜索…';
		
		// 点击input时，展开 
		$('.sear-box input').click(function() {
			if($('.search input').val() != '') {
				$('.results-panel').addClass('show');
			}
		});
		// 失去焦点时，收缩 
		$('.sear-box input').blur(function() {
			setTimeout(function() {
				$('.results-panel').removeClass('show');
			}, 200);
		})
		// 选择一项时，收缩 
		$('.sear-box').on('click', '.matching-post', function() {
			console.log('click……');
			// $('.search input').val('');
			$('.results-panel').removeClass('show');
		});
		
		// 点击按钮，加载图片
		$(document).on('click', '.show-img', function(){
			var src = $(this).attr('img-src');
			var img = '<img class="show-to-img" src="' + src + '" />';
			$(this).after(img);
			$(this).remove();
		})
		
		// 点击按钮，加载图片
		$(document).on('click', '.show-to-img', function(){
			open(this.src);
		})
		
	});
	
}