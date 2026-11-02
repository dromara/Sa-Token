(function () {
	var PAGE_SIZE = 24
	var state = { categoryId: 'all', keyword: '', sort: 'order', page: 1, data: null }
	var coverObserver = null

	function $(id) {
		return document.getElementById(id)
	}

	function esc(text) {
		return String(text == null ? '' : text)
			.replace(/&/g, '&amp;')
			.replace(/</g, '&lt;')
			.replace(/>/g, '&gt;')
			.replace(/"/g, '&quot;')
	}

	function readQuery() {
		var q = new URLSearchParams(location.search)
		state.categoryId = q.get('type') || q.get('c') || 'all'
		state.keyword = q.get('q') || ''
		state.sort = q.get('sort') === 'star' ? 'star' : 'order'
		var page = parseInt(q.get('p'), 10)
		state.page = page > 0 ? page : 1
	}

	function writeQuery() {
		var q = new URLSearchParams(location.search)
		q.delete('type')
		q.delete('c')
		q.delete('q')
		q.delete('sort')
		q.delete('p')
		if (state.categoryId !== 'all') q.set('type', state.categoryId)
		if (state.keyword) q.set('q', state.keyword)
		if (state.sort === 'star') q.set('sort', 'star')
		if (state.page > 1) q.set('p', String(state.page))
		var search = q.toString()
		var next = location.pathname + (search ? '?' + search : '')
		history.replaceState(null, '', next)
	}

	function matchKeyword(item, kw) {
		if (!kw) return true
		return [item.name, item.author, item.desc, item.category, item.lang].join(' ').toLowerCase().indexOf(kw) >= 0
	}

	function matchItem(item) {
		if (item.hidden) return false
		if (state.categoryId !== 'all' && item.categoryId !== state.categoryId) return false
		return matchKeyword(item, state.keyword.trim().toLowerCase())
	}

	function highlight(text) {
		var raw = String(text == null ? '' : text)
		var kw = state.keyword.trim()
		if (!kw) return esc(raw)
		var lower = raw.toLowerCase()
		var needle = kw.toLowerCase()
		var out = ''
		var i = 0
		var idx
		while ((idx = lower.indexOf(needle, i)) !== -1) {
			out += esc(raw.slice(i, idx)) + '<mark class="cases-hl">' + esc(raw.slice(idx, idx + kw.length)) + '</mark>'
			i = idx + kw.length
		}
		return out + esc(raw.slice(i))
	}

	function itemKey(item) {
		return item.url || String(item._order)
	}

	function coverHtml(item) {
		if (item.cover) {
			return '<img decoding="async" data-src="' + esc(item.cover) + '" alt="' + esc(item.name) + '">'
		}
		var mark = item.lang || item.category
		return '<div class="s-case-cover s-case-cover--' + esc(item.categoryId) + '" aria-hidden="true">' +
			'<span>' + esc(mark) + '</span>' +
			'<strong>' + esc(item.name) + '</strong>' +
			'</div>'
	}

	function cardHtml(item) {
		var author = item.author ? '<span class="s-author">' + highlight(item.author) + '</span>' : ''
		return '<div class="s-case" data-key="' + esc(itemKey(item)) + '">' +
			'<a href="' + esc(item.url) + '" target="_blank" rel="noopener" class="s-case-link">' + coverHtml(item) + '</a>' +
			'<div class="s-case-head">' +
			'<h3 class="s-case-title"><a href="' + esc(item.url) + '" target="_blank" rel="noopener">' + highlight(item.name) + '</a></h3>' +
			author +
			'</div>' +
			'<p class="s-case-intro">' + highlight(item.desc) + '</p>' +
			'</div>'
	}

	function bindCovers(box) {
		if (coverObserver) {
			coverObserver.disconnect()
			coverObserver = null
		}
		box.querySelectorAll('.s-case-link').forEach(function (link) {
			if (!link.querySelector('img[data-src]')) link.classList.add('is-loaded')
		})
		var imgs = box.querySelectorAll('img[data-src]')
		if (!imgs.length) return
		coverObserver = new IntersectionObserver(function (entries) {
			entries.forEach(function (entry) {
				if (!entry.isIntersecting) return
				var img = entry.target
				coverObserver.unobserve(img)
				function done() {
					if (img.parentElement) img.parentElement.classList.add('is-loaded')
				}
				img.addEventListener('load', done, { once: true })
				img.addEventListener('error', done, { once: true })
				img.src = img.getAttribute('data-src')
				img.removeAttribute('data-src')
				if (img.complete) done()
			})
		}, { rootMargin: '240px 0px', threshold: 0.01 })
		imgs.forEach(function (img) { coverObserver.observe(img) })
	}

	function sortList(list) {
		if (state.sort !== 'star') return list
		return list.slice().sort(function (a, b) {
			var sa = a.star || 0
			var sb = b.star || 0
			if (sb !== sa) return sb - sa
			return a._order - b._order
		})
	}

	function renderCats() {
		var html = '<button type="button" class="cases-cat' + (state.categoryId === 'all' ? ' is-active' : '') + '" data-id="all">全部</button>'
		state.data.categories.forEach(function (cat) {
			html += '<button type="button" class="cases-cat' + (state.categoryId === cat.id ? ' is-active' : '') + '" data-id="' + esc(cat.id) + '">' +
				esc(cat.name) + '</button>'
		})
		$('cases-cats').innerHTML = html
	}

	function sortBtns() {
		return '<button type="button" class="cases-sort' + (state.sort === 'order' ? ' is-active' : '') + '" data-sort="order">登记顺序</button>' +
			'<span>、</span>' +
			'<button type="button" class="cases-sort' + (state.sort === 'star' ? ' is-active' : '') + '" data-sort="star">star 优先</button>'
	}

	function renderCount() {
		var count = $('cases-count')
		var visible = state.data.projects.filter(function (item) { return !item.hidden })
		var kw = state.keyword.trim()
		if (kw) {
			var needle = kw.toLowerCase()
			var hits = 0
			visible.forEach(function (item) {
				if (matchKeyword(item, needle)) hits++
			})
			count.innerHTML = '找到 ' + hits + ' 个项目：' + sortBtns()
			return
		}
		count.innerHTML = '已收录 ' + visible.length + ' 个开源项目：' + sortBtns()
	}

	function pageItems(current, total) {
		var items = []
		for (var i = 1; i <= total; i++) {
			if (i === 1 || i === total || Math.abs(i - current) <= 2) items.push(i)
			else if (items[items.length - 1] !== '…') items.push('…')
		}
		return items
	}

	function pagerBtn(page, text, disabled, active) {
		return '<button type="button" class="cases-pager-btn' + (active ? ' is-active' : '') + '" data-page="' + page + '"' +
			(disabled ? ' disabled' : '') +
			(active ? ' aria-current="page"' : '') +
			'>' + text + '</button>'
	}

	function renderPager(pages) {
		var el = $('cases-pager')
		if (pages <= 1) {
			el.hidden = true
			el.innerHTML = ''
			return
		}
		el.hidden = false
		var html = pagerBtn('prev', '上一页', state.page <= 1)
		pageItems(state.page, pages).forEach(function (item) {
			if (item === '…') html += '<span class="cases-pager-ellipsis">…</span>'
			else html += pagerBtn(item, String(item), false, item === state.page)
		})
		html += pagerBtn('next', '下一页', state.page >= pages)
		el.innerHTML = html
	}

	function renderList() {
		var list = sortList(state.data.projects.filter(matchItem))
		var box = $('cases-list')
		var empty = $('cases-empty')
		var pages = Math.ceil(list.length / PAGE_SIZE)
		var pageBefore = state.page
		if (state.page < 1) state.page = 1
		if (pages && state.page > pages) state.page = pages
		if (state.page !== pageBefore) writeQuery()
		renderCount()
		if (!list.length) {
			box.innerHTML = ''
			empty.classList.add('is-show')
			renderPager(0)
			return
		}
		empty.classList.remove('is-show')
		var start = (state.page - 1) * PAGE_SIZE
		box.innerHTML = list.slice(start, start + PAGE_SIZE).map(cardHtml).join('')
		bindCovers(box)
		renderPager(pages)
	}

	function render() {
		renderCats()
		renderList()
	}

	function scrollToStuckCats() {
		var y = window.scrollY + $('cases-stick-probe').getBoundingClientRect().bottom - 60
		if (window.scrollY > y) window.scrollTo(0, Math.max(0, y))
		syncStuck()
	}

	$('cases-cats').addEventListener('click', function (e) {
		var btn = e.target.closest('.cases-cat')
		if (!btn) return
		state.categoryId = btn.getAttribute('data-id')
		state.page = 1
		writeQuery()
		render()
		scrollToStuckCats()
	})

	$('cases-count').addEventListener('click', function (e) {
		var btn = e.target.closest('.cases-sort')
		if (!btn) return
		state.sort = btn.getAttribute('data-sort') === 'star' ? 'star' : 'order'
		state.page = 1
		writeQuery()
		renderList()
	})

	$('cases-pager').addEventListener('click', function (e) {
		var btn = e.target.closest('[data-page]')
		if (!btn || btn.disabled) return
		var raw = btn.getAttribute('data-page')
		var next = state.page
		if (raw === 'prev') next = state.page - 1
		else if (raw === 'next') next = state.page + 1
		else next = parseInt(raw, 10)
		if (!next || next === state.page) return
		state.page = next
		writeQuery()
		renderList()
		scrollToStuckCats()
	})

	var timer = 0
	$('cases-search').addEventListener('input', function () {
		var value = this.value
		clearTimeout(timer)
		timer = setTimeout(function () {
			state.keyword = value
			state.page = 1
			writeQuery()
			renderList()
		}, 120)
	})

	readQuery()
	$('cases-search').value = state.keyword

	function syncStuck() {
		$('cases-toolbar').classList.toggle('is-stuck', $('cases-stick-probe').getBoundingClientRect().bottom <= 60)
	}
	window.addEventListener('scroll', syncStuck, { passive: true })
	syncStuck()

	fetch('/static/cases/cases.json')
		.then(function (res) { return res.json() })
		.then(function (data) {
			data.projects.forEach(function (item, i) { item._order = i })
			state.data = data
			if (!data.categories.some(function (c) { return c.id === state.categoryId })) {
				state.categoryId = 'all'
			}
			render()
		})
		.catch(function () {
			$('cases-count').textContent = '案例数据加载失败，请刷新重试'
		})
})()
