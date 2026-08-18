/**
 * Sa-Token 全站公共脚本
 * 引入方式：<script src="/static/all-version-common.js"></script>
 *
 * - 百度统计 / 搜索引擎自动提交：引入即执行；localhost / 127.0.0.1 下跳过
 * - 文档版本下拉：不自动执行，文档页手动调用
 *   SaTokenVersions.initVersionSelects()
 *
 * 发新版时只需改 LATEST_VERSION / ALL_VERSIONS
 */
(function (global) {
	'use strict';

	/** 当前线上最新版（根目录 /doc.html） */
	var LATEST_VERSION = 'v1.46.0';

	/** 全部版本号，从新到旧（含最新版） */
	var ALL_VERSIONS = [
		'v1.46.0',
		'v1.45.0',
		'v1.44.0',
		'v1.43.0',
		'v1.42.0',
		'v1.41.0',
		'v1.40.0',
		'v1.39.0',
		'v1.38.0',
		'v1.37.0',
		'v1.36.0',
		'v1.35.0',
		'v1.34.0',
		'v1.33.0',
		'v1.32.0',
		'v1.31.0',
		'v1.30.0',
		'v1.29.0',
		'v1.28.0',
		'v1.27.0',
		'v1.26.0',
		'v1.25.0',
		'v1.24.0',
		'v1.23.0',
		'v1.22.0',
		'v1.21.0',
		'v1.20.0',
		'v1.19.0',
		'v1.18.0',
		'v1.17.0',
		'v1.16.0',
		'v1.15.0',
		'v1.14.0',
		'v1.13.0',
		'v1.12.1',
		'v1.12.0',
		'v1.11.0',
		'v1.10.0',
		'v1.9.0',
		'v1.8.0',
		'v1.7.0',
		'v1.6.0',
		'v1.5.1',
		'v1.4.0',
		'v1.3.0',
		'v1.2.0',
		'v1.1.0',
		'v1.0.0'
	];

	/**
	 * 根据版本号生成文档入口 URL
	 * - 最新版 → /doc.html
	 * - >= v1.31.0 → /v/{ver}/doc.html
	 * - <= v1.30.0 → /v/{ver}/doc/index.html
	 */
	function getDocUrl(version) {
		if (!version) {
			return '/doc.html';
		}
		var ver = normalizeVersion(version);
		if (ver === LATEST_VERSION) {
			return '/doc.html';
		}
		var parts = ver.replace(/^v/i, '').split('.').map(function (n) {
			return parseInt(n, 10) || 0;
		});
		var major = parts[0] || 0;
		var minor = parts[1] || 0;
		if (major > 1 || (major === 1 && minor >= 31)) {
			return '/v/' + ver + '/doc.html';
		}
		return '/v/' + ver + '/doc/index.html';
	}

	function normalizeVersion(version) {
		var v = String(version || '').trim();
		if (!v) {
			return '';
		}
		if (v.charAt(0).toLowerCase() !== 'v') {
			v = 'v' + v;
		}
		return v;
	}

	/** 当前 URL 是否属于根目录最新版文档 */
	function isLatestPath(pathname) {
		var path = pathname || (global.location && global.location.pathname) || '';
		if (/\/v\/v\d+\.\d+(\.\d+)?\//i.test(path)) {
			return false;
		}
		return true;
	}

	/**
	 * 判断当前页面属于哪个版本
	 * @returns {string} 如 v1.44.0；根目录最新版返回 LATEST_VERSION
	 */
	function getCurrentVersion(pathname) {
		var path = pathname || (global.location && global.location.pathname) || '';
		var match = path.match(/\/v\/(v\d+\.\d+(?:\.\d+)?)\//i);
		if (match) {
			return normalizeVersion(match[1]);
		}
		return LATEST_VERSION;
	}

	/** 当前是否最新版文档 */
	function isLatestVersion(pathname) {
		return getCurrentVersion(pathname) === LATEST_VERSION && isLatestPath(pathname);
	}

	/** 填充单个 select.select-version */
	function fillVersionSelect(select) {
		if (!select) {
			return;
		}
		var current = getCurrentVersion();
		var html = [];
		for (var i = 0; i < ALL_VERSIONS.length; i++) {
			var ver = ALL_VERSIONS[i];
			var url = getDocUrl(ver);
			var selected = ver === current ? ' selected' : '';
			html.push('<option value="' + url + '"' + selected + '>' + ver + '</option>');
		}
		html.push('<option value="/doc.html">最新版</option>');
		select.innerHTML = html.join('');
		if (!select.getAttribute('onchange')) {
			select.onchange = function () {
				if (this.value) {
					global.location.href = this.value;
				}
			};
		}
	}

	/**
	 * 填充页面上所有版本下拉框
	 * 仅文档页需要调用，其它页面不要调用
	 */
	function initVersionSelects() {
		if (!global.document) {
			return;
		}
		var nodes = document.querySelectorAll('select.select-version');
		for (var i = 0; i < nodes.length; i++) {
			fillVersionSelect(nodes[i]);
		}
	}

	var api = {
		LATEST_VERSION: LATEST_VERSION,
		ALL_VERSIONS: ALL_VERSIONS,
		getDocUrl: getDocUrl,
		getCurrentVersion: getCurrentVersion,
		isLatestVersion: isLatestVersion,
		isLatestPath: isLatestPath,
		fillVersionSelect: fillVersionSelect,
		initVersionSelects: initVersionSelects
	};

	global.SaTokenVersions = api;
	global.saTokenLatestVersion = LATEST_VERSION;
})(typeof window !== 'undefined' ? window : this);

/* 导航「博客」统一指向最新博客首页（旧版 /v/vX.Y.Z/ 下相对路径会指错） */
(function () {
	function fixBlogNavHref() {
		if (!document || !document.querySelectorAll) {
			return;
		}
		var links = document.querySelectorAll('a.wzi');
		for (var i = 0; i < links.length; i++) {
			var a = links[i];
			var text = (a.textContent || '').replace(/\s+/g, '');
			if (text !== '博客') {
				continue;
			}
			a.setAttribute('href', '/blog/index.html');
		}
	}
	if (document.readyState === 'loading') {
		document.addEventListener('DOMContentLoaded', fixBlogNavHref);
	} else {
		fixBlogNavHref();
	}
})();

/* 百度统计 + 搜索引擎自动提交（引入即执行；本地环境跳过） */
(function() {
	var host = (location.hostname || '').toLowerCase();
	if (host === 'localhost' || host === '127.0.0.1') {
		console.log('本地环境，跳过百度统计 + 搜索引擎自动提交...');
		return;
	} else {
		console.log('百度统计 + 搜索引擎自动提交... ');
	}

	/* 百度统计 */
	var _hmt = window._hmt = window._hmt || [];
	(function() {
		var hm = document.createElement("script");
		hm.src = "https://hm.baidu.com/hm.js?0123e86492363a4dd40db165092b557b";
		var s = document.getElementsByTagName("script")[0];
		s.parentNode.insertBefore(hm, s);
	})();

	/* 搜索引擎自动提交 */
	(function() {
		var bp = document.createElement('script');
		var curProtocol = window.location.protocol.split(':')[0];
		if (curProtocol === 'https') {
			bp.src = 'https://zz.bdstatic.com/linksubmit/push.js';
		} else {
			bp.src = 'http://push.zhanzhang.baidu.com/push.js';
		}
		var s = document.getElementsByTagName("script")[0];
		s.parentNode.insertBefore(bp, s);
	})();
})();
