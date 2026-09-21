/**
 * Access Report（ar.js）— sa-token.com 文档站：渠道 + 跨站客户端 id + 访问上报
 * 零依赖。由 all-version-common.js 动态加载 /static/ar.js。
 *
 * 1、上报页面访问到 REPORT_URL（fetch POST，静默；失败不弹窗）
 *     进页 open；之后按时刻表心跳；关页 / 切走再补一枪。
 *     仅 hostname 恰好为 sa-token.com，或 localStorage.testAR === '1'，或 URL ?testAR=1 时才报。
 *     SKIP_PATH_PREFIXES 匹配的路径不报（首页 iframe 内嵌页）。
 *     不满足条件时整份脚本直接退出：不写 client_id、不拦链接、不上报，如同未加载。
 *     - 1.1、reportType：open = 插入本页访问；heartbeat = 按 recVisitId 更新停留和滚动
 *     - 1.2、recClientId：客户端 id，本站首次访问随机生成（也可从 URL 继承），localStorage 持久
 *     - 1.3、recVisitId：每次打开 / F5 / SPA 切章新生成，代表一次 PV（切章不重载脚本，由主题派 st-ar-page）
 *     - 1.4、firstWay：首次接触渠道，localStorage.first_way，键不存在时写一次后不改
 *     - 1.5、activeWay：最近一次 ?way=（其次 ?hmsr=），对应 localStorage.way
 *     - 1.6、pageUrl：当前完整地址（上报前已去掉 rec_client_id）
 *     - 1.7、referrer：来源页
 *     - 1.8、pageHost：当前域名
 *     - 1.9、maxScrollY：本页最大滚动；心跳带上，服务端 GREATEST
 *     - 1.10、IP / UA / 归属地 / 时间：前端不传，服务端写
 * 2、不改站内 href。way 只从 URL / localStorage 读，用于上报。
 * 3、对站外 sa-max.cn 拼接 rec_client_id（拦截 click / auxclick，不改 a.href）
 *     左键本页跳；target=_blank / Ctrl / ⌘ / Shift / 中键新开。右键复制仍是原地址。
 * 4、从 URL 接收 rec_client_id：本地没有 id 时采用，已有则忽略；读完 replaceState 去掉该参数
 */
(function () {

	// -------- 配置项 --------
	var REPORT_URL = 'https://sa-token.com/st-server/ar';
	var REPORT_HOST = 'sa-token.com'; // 恰好匹配才上报；www / rc 等子域不报
	var TEST_LS_KEY = 'testAR';
	var TEST_LS_ON = '1';
	/** 内嵌页路径前缀：命中则整份脚本不跑（不上报、不写 id、不拦跳转） */
	var SKIP_PATH_PREFIXES = [
		'/a/github-stars-vs/',
		'/a/star-guide/'
	];
	var OUTBOUND_HOST = 'sa-max.cn';

	var EMPTY = '-';
	var LS_FIRST = 'first_way';
	var LS_WAY = 'way';
	var LS_CLIENT = 'rec_client_id';

	/** 读 localStorage，失败当没有 */
	function lsGet(key) {
		try { return localStorage.getItem(key); } catch (e) { return null; }
	}
	/** 写 localStorage，失败忽略 */
	function lsSet(key, val) {
		try { localStorage.setItem(key, val); } catch (e) {}
	}

	/** 对外用的渠道值：空串、null、'-' 都当无渠道 */
	function normWay(v) {
		return (v && v !== EMPTY) ? v : '';
	}

	/** 生成客户端 / 访问 id */
	function uuid() {
		if (window.crypto && crypto.randomUUID) {
			return crypto.randomUUID();
		}
		return Date.now().toString(36) + '-' + Math.random().toString(36).slice(2, 10);
	}

	/** 读当前页 query 参数 */
	function qp(name) {
		try {
			return new URL(location.href).searchParams.get(name) || '';
		} catch (e) {
			return '';
		}
	}

	/** 是否内嵌页（SKIP_PATH_PREFIXES 前缀匹配 pathname） */
	function isSkipPath() {
		var p = location.pathname || '';
		for (var i = 0; i < SKIP_PATH_PREFIXES.length; i++) {
			if (p.indexOf(SKIP_PATH_PREFIXES[i]) === 0) return true;
		}
		return false;
	}

	/** 本页是否允许跑采集：非内嵌，且（正式域名 或 localStorage.testAR=1 或 URL ?testAR=1） */
	function allowReport() {
		if (isSkipPath()) return false;
		if (location.hostname === REPORT_HOST) return true;
		if (lsGet(TEST_LS_KEY) === TEST_LS_ON) return true;
		return qp(TEST_LS_KEY) === TEST_LS_ON;
	}

	if (!allowReport()) return;

	/** URL 上的渠道：优先 ?way=，其次百度统计 ?hmsr= */
	function urlWay() {
		return qp('way') || qp('hmsr') || '';
	}

	/**
	 * 同步 localStorage 里的渠道。
	 * first_way：键不存在时写一次（URL → 已有 way → 空），之后不改。
	 * way：URL 有渠道就覆盖；没有则沿用，空或 '-' 写成空串。
	 */
	function initWay() {
		var fromUrl = urlWay();
		if (lsGet(LS_FIRST) == null) {
			if (fromUrl) {
				lsSet(LS_FIRST, fromUrl);
			} else if (normWay(lsGet(LS_WAY))) {
				lsSet(LS_FIRST, lsGet(LS_WAY));
			} else {
				lsSet(LS_FIRST, '');
			}
		}
		if (fromUrl) {
			lsSet(LS_WAY, fromUrl);
		} else if (!lsGet(LS_WAY) || lsGet(LS_WAY) === EMPTY) {
			lsSet(LS_WAY, '');
		}
	}

	/** 最近接触渠道（last-touch），上报字段 activeWay */
	function getWay() {
		return normWay(lsGet(LS_WAY));
	}

	/** 首次接触渠道（first-touch），上报字段 firstWay */
	function getFirstWay() {
		return normWay(lsGet(LS_FIRST));
	}

	initWay();

	/** 读 URL 上的 rec_client_id，并立刻从地址栏去掉（其它 query / hash 保留） */
	function takeRecClientIdFromUrl() {
		var id = '';
		try {
			var u = new URL(location.href);
			id = u.searchParams.get('rec_client_id') || '';
			if (u.searchParams.has('rec_client_id')) {
				u.searchParams.delete('rec_client_id');
				history.replaceState(null, '', u.pathname + u.search + u.hash);
			}
		} catch (e) {}
		return id;
	}

	/** 全站访客 id：已有 localStorage 则用；否则认 URL，再没有就随机并持久化 */
	function getClientId() {
		var fromUrl = takeRecClientIdFromUrl();
		var id = lsGet(LS_CLIENT);
		if (id) return id;
		id = fromUrl || uuid();
		lsSet(LS_CLIENT, id);
		return id;
	}

	var recClientId = getClientId();
	var recVisitId = ''; // 每次打开 / F5 / SPA 切章在 beginVisit 里生成

	/** 指向 sa-max.cn 时返回带 rec_client_id 的跳转地址；否则空串。不改 a.href */
	function outboundJumpUrl(href) {
		if (!href || !recClientId) return '';
		try {
			var u = new URL(href, location.href);
			if (u.hostname !== OUTBOUND_HOST) return '';
			u.searchParams.set('rec_client_id', recClientId);
			return u.href;
		} catch (e) {
			return '';
		}
	}

	/**
	 * 拦截跳转：标签 href 保持原样。
	 * 左键本页跳；target=_blank / Ctrl / ⌘ / Shift / 中键新开。
	 * click 只处理左键，中键交给 auxclick，避免 Firefox 双开。
	 */
	function interceptOutboundClick(e) {
		if (e.defaultPrevented || e.altKey) return;
		if (e.type === 'click' && e.button !== 0) return;
		if (e.type === 'auxclick' && e.button !== 1) return;
		var t = e.target;
		if (!t || !t.closest) return;
		var el = t.closest('a');
		var next = el && outboundJumpUrl(el.getAttribute('href'));
		if (!next) return;
		e.preventDefault();
		if (el.target === '_blank' || e.ctrlKey || e.metaKey || e.shiftKey || e.button === 1) {
			window.open(next, '_blank', 'noopener');
		} else {
			location.href = next;
		}
	}
	document.addEventListener('click', interceptOutboundClick, true);
	document.addEventListener('auxclick', interceptOutboundClick, true);

	var pageOpenTime = 0;
	var maxScrollY = 0; // 本页滚过的最大 scrollY
	var lastPageKey = ''; // pathname + search，用来判断是切章还是同页锚点
	var spaFrom = document.referrer || '';
	var pingTimer = null;

	/** 记下本页滚动峰值，随心跳上报 */
	function bumpScroll() {
		var y = window.pageYOffset || document.documentElement.scrollTop || 0;
		if (y > maxScrollY) maxScrollY = y;
	}

	/**
	 * 心跳时刻表（秒，相对本页打开时刻）。
	 * 秒：5～50；分：1～50；时：1～12（0.5h 步进）、13～168。168h 停。
	 */
	function buildPingAtSec() {
		var list = [5, 10, 20, 30, 40, 50];
		var mins = [1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 40, 50];
		for (var i = 0; i < mins.length; i++) {
			list.push(mins[i] * 60);
		}
		for (var h = 1; h <= 12; h += 0.5) {
			list.push(h * 3600);
		}
		for (var h2 = 13; h2 <= 168; h2++) {
			list.push(h2 * 3600);
		}
		return list;
	}
	var pingAtSec = buildPingAtSec();

	/** 静默 POST：open 插记录，heartbeat 更新停留和滚动 */
	function report(reportType) {
		if (!recVisitId) return;
		bumpScroll();
		var body = new URLSearchParams({
			reportType: reportType,
			recClientId: recClientId,
			recVisitId: recVisitId,
			pageUrl: location.href,
			referrer: spaFrom,
			pageHost: location.host,
			firstWay: getFirstWay(),
			activeWay: getWay(),
			maxScrollY: String(maxScrollY)
		}).toString();
		try {
			fetch(REPORT_URL, {
				method: 'POST',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
				body: body,
				credentials: 'omit',
				keepalive: reportType === 'heartbeat'
			}).catch(function () {});
		} catch (e) {}
	}

	/** 当前页键：不含 hash，同页锚点不算新访问 */
	function pageKey() {
		try {
			var u = new URL(location.href);
			return u.pathname + u.search;
		} catch (e) {
			return location.pathname || '';
		}
	}

	function stopPing() {
		if (pingTimer) {
			clearTimeout(pingTimer);
			pingTimer = null;
		}
	}

	/** 按时刻表排下一次 heartbeat；定时器晚了只补当前这一跳 */
	function scheduleNext() {
		stopPing();
		var elapsed = (Date.now() - pageOpenTime) / 1000;
		var next = null;
		for (var i = 0; i < pingAtSec.length; i++) {
			if (pingAtSec[i] > elapsed) {
				next = pingAtSec[i];
				break;
			}
		}
		if (next == null) return;
		var delay = Math.max(0, next * 1000 - (Date.now() - pageOpenTime));
		pingTimer = setTimeout(function () {
			pingTimer = null;
			report('heartbeat');
			scheduleNext();
		}, delay);
	}

	/** 开一次 PV：新 recVisitId + open；SPA 切章先给上一章补心跳 */
	function beginVisit() {
		var key = pageKey();
		if (key === lastPageKey) return;
		stopPing();
		if (lastPageKey) {
			report('heartbeat');
			spaFrom = location.origin + lastPageKey;
		}
		lastPageKey = key;
		recVisitId = uuid();
		pageOpenTime = Date.now();
		maxScrollY = 0;
		report('open');
		scheduleNext();
	}

	/** 首次 open + 听 SPA 切章 + 关页再补一枪 */
	function startReport() {
		window.addEventListener('scroll', bumpScroll, { passive: true });
		window.addEventListener('pagehide', function () {
			report('heartbeat');
		});
		window.addEventListener('st-ar-page', beginVisit);
		beginVisit();
	}

	if (document.readyState === 'loading') {
		document.addEventListener('DOMContentLoaded', startReport);
	} else {
		startReport();
	}

})();
