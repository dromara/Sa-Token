/**
 * Sa-Token 多语言切换（基于 translate.js）
 *
 * 用法：
 * 1. 页面放容器：<span id="translate" class="select-language-box ignore"></span>
 * 2. 引入 translate.js + 本文件
 * 3. 文档页：挂到 Docsify plugins → saTranslatePlugin
 * 4. 首页等普通页：直接调 initSaTranslate()
 *
 * 官方文档：https://translate.zvo.cn
 * 仓库：https://gitee.com/mail_osc/translate
 */
(function (global) {
	'use strict';

	var inited = false;

	/** 初始化翻译配置，并执行第一次翻译 */
	function initSaTranslate() {
		if (inited) {
			return;
		}
		if (!global.translate || typeof global.translate.execute !== 'function' || typeof global.translate.version !== 'string') {
			console.warn('[sa-translate] 未加载 translate.js，跳过多语言初始化');
			return;
		}
		inited = true;

		var translate = global.translate;

		// 当前网页原文语种
		translate.language.setLocal('chinese_simplified');

		// 免费通道：走浏览器端 Edge 翻译（无需 API Key）
		translate.service.use('client.edge');

		// 顶部下拉只展示这几种语言（可按需增减）
		// 语种 ID 见：http://translate.zvo.cn/support_language.html
		// 德语官方码是 deutsch（不是 german）
		translate.selectLanguageTag.languages = [
			'chinese_simplified',
			'chinese_traditional',
			'english',
			'japanese',
			'korean',
			'russian',
			'vietnamese',
			'spanish',
			'deutsch',
			'french',
			'indonesian'
		].join(',');

		// 代码别被翻坏：忽略 code / pre
		translate.ignore.tag.push('code');
		translate.ignore.tag.push('pre');

		// 动态改 DOM 时也能跟上（Docsify 切页、首页轮播文案等）
		translate.listener.start();

		// 开始翻译（会同时在 #translate 里生成 select）
		// 样式靠 CSS 选择器 #translateSelectLanguage，切换语种重绘也不丢
		translate.execute();
	}

	/**
	 * Docsify 插件：
	 * - ready：文档框架就绪后初始化
	 * - doneEach：每次切换章节后再翻一遍，避免新内容漏翻
	 */
	function saTranslatePlugin(hook) {
		if (!global.translate) {
			console.warn('[sa-translate] 未加载 translate.js，跳过多语言初始化');
			return;
		}

		hook.ready(function () {
			initSaTranslate();
		});
		hook.doneEach(function () {
			if (inited) {
				global.translate.execute();
			}
		});
	}

	global.initSaTranslate = initSaTranslate;
	global.saTranslatePlugin = saTranslatePlugin;
})(typeof window !== 'undefined' ? window : this);
