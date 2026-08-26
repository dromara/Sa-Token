(function () {
  var SCROLL_OFFSET = 90;
  var PRISM_LANG = {
    xml: 'markup',
    html: 'markup',
    cmd: 'bash',
    url: 'plain',
    yml: 'yaml',
    js: 'javascript',
    ts: 'typescript',
    txt: 'plain',
    text: 'plain'
  };

  function normalizePrismLang(lang) {
    return PRISM_LANG[lang] || lang;
  }

  function addCodeLineNumbers(code) {
    var pre = code.parentElement;
    if (!pre || pre.querySelector('.code-line-box')) return;

    var lines = (code.textContent || '').split('\n');
    if (lines[lines.length - 1] === '') lines.pop();

    code.classList.add('has-numbering');
    var numbering = document.createElement('ul');
    numbering.className = 'code-line-box';
    var count = Math.max(lines.length, 1);
    for (var i = 1; i <= count; i++) {
      var li = document.createElement('li');
      li.textContent = String(i);
      numbering.appendChild(li);
    }
    pre.appendChild(numbering);
  }

  function copyText(text) {
    if (navigator.clipboard && navigator.clipboard.writeText) {
      return navigator.clipboard.writeText(text);
    }
    return new Promise(function (resolve, reject) {
      var ta = document.createElement('textarea');
      ta.value = text;
      ta.setAttribute('readonly', '');
      ta.style.cssText = 'position:fixed;left:-9999px';
      document.body.appendChild(ta);
      ta.select();
      try {
        document.execCommand('copy') ? resolve() : reject();
      } catch (err) {
        reject(err);
      }
      document.body.removeChild(ta);
    });
  }

  function initCopyCode() {
    if (!document.body.classList.contains('blog-article-page')) return;
    var section = document.querySelector('#main .markdown-section');
    if (!section) return;

    var buttonHtml =
      '<button type="button" class="docsify-copy-code-button">' +
      '<span class="label">复制到剪贴板</span>' +
      '<span class="error">错误</span>' +
      '<span class="success">复制成功</span>' +
      '</button>';

    section.querySelectorAll('pre').forEach(function (pre) {
      if (!pre.querySelector('code')) return;
      if (pre.querySelector('.docsify-copy-code-button')) return;
      pre.insertAdjacentHTML('beforeend', buttonHtml);
    });

    section.addEventListener('click', function (e) {
      var btn = e.target.closest('.docsify-copy-code-button');
      if (!btn) return;
      e.preventDefault();
      var code = btn.parentElement && btn.parentElement.querySelector('code');
      if (!code) return;
      copyText(code.textContent || '').then(function () {
        btn.classList.add('success');
        setTimeout(function () { btn.classList.remove('success'); }, 1000);
      }).catch(function () {
        btn.classList.add('error');
        setTimeout(function () { btn.classList.remove('error'); }, 1000);
      });
    });
  }

  function initCodeHighlight() {
    document.querySelectorAll('pre[v-pre][data-lang] > code').forEach(function (code) {
      var lang = code.parentElement.getAttribute('data-lang');
      if (!lang) return;
      code.classList.add('lang-' + lang);
      code.classList.add('language-' + normalizePrismLang(lang));
    });

    if (window.Prism && typeof window.Prism.highlightAll === 'function') {
      window.Prism.highlightAll();
    }

    document.querySelectorAll('pre[v-pre][data-lang]').forEach(function (pre) {
      var lang = pre.getAttribute('data-lang');
      var code = pre.querySelector('code');
      if (!lang || !code) return;
      code.classList.add('lang-' + lang);
      pre.classList.remove('language-' + normalizePrismLang(lang));
    });

    document.querySelectorAll('.markdown-section pre:not([data-lang]) > code').forEach(addCodeLineNumbers);
  }

  function initSidebarDrawer() {
    var toggle = document.querySelector('.blog-sidebar-toggle');
    var mask = document.querySelector('.blog-sidebar-mask');
    if (!toggle || !mask) return;

    toggle.addEventListener('click', function () {
      document.body.classList.toggle('blog-sidebar-open');
    });
    mask.addEventListener('click', function () {
      document.body.classList.remove('blog-sidebar-open');
    });
  }

  function getScrollTopFor(el) {
    var top = el.getBoundingClientRect().top + (window.pageYOffset || document.documentElement.scrollTop);
    return Math.max(0, top - SCROLL_OFFSET);
  }

  function supportsSmoothScroll() {
    return 'scrollBehavior' in document.documentElement.style;
  }

  function animateScrollTo(top, duration) {
    var start = window.pageYOffset || document.documentElement.scrollTop;
    var change = top - start;
    if (Math.abs(change) < 1) return;

    var startTime = performance.now();
    duration = duration || 450;

    function step(now) {
      var elapsed = now - startTime;
      var progress = Math.min(elapsed / duration, 1);
      var ease = progress < 0.5
        ? 4 * progress * progress * progress
        : 1 - Math.pow(-2 * progress + 2, 3) / 2;
      window.scrollTo(0, start + change * ease);
      if (progress < 1) requestAnimationFrame(step);
    }

    requestAnimationFrame(step);
  }

  function scrollToSection(id, smooth) {
    if (!id) return false;
    var el = document.getElementById(id);
    if (!el) return false;
    var top = getScrollTopFor(el);

    if (!smooth) {
      window.scrollTo(0, top);
      return true;
    }

    if (supportsSmoothScroll()) {
      window.scrollTo({ top: top, behavior: 'smooth' });
    } else {
      animateScrollTo(top);
    }
    return true;
  }

  function hashForId(id) {
    return '#' + encodeURIComponent(id);
  }

  function hashToId() {
    var raw = (location.hash || '').replace(/^#/, '');
    if (!raw) return '';
    try {
      return decodeURIComponent(raw);
    } catch (e) {
      return raw;
    }
  }

  function setUrlHash(id) {
    var hash = hashForId(id);
    if (location.hash === hash) return;
    history.pushState(null, '', location.pathname + location.search + hash);
  }

  function navigateToSection(id, smooth) {
    if (!scrollToSection(id, smooth)) return;
    setUrlHash(id);
  }

  function initTocHighlight() {
    var tocLinks = document.querySelectorAll('.blog-article-toc .blog-toc-list a');
    if (!tocLinks.length) return;

    var headings = [];
    tocLinks.forEach(function (a) {
      var id = (a.getAttribute('href') || '').replace(/^#/, '');
      try {
        id = decodeURIComponent(id);
      } catch (e) {}
      var el = document.getElementById(id);
      if (el) headings.push({ el: el, link: a, li: a.parentElement });
    });
    if (!headings.length) return;

    function updateActiveByScroll() {
      var current = headings[0];
      for (var i = 0; i < headings.length; i++) {
        if (headings[i].el.getBoundingClientRect().top - SCROLL_OFFSET <= 1) {
          current = headings[i];
        }
      }
      headings.forEach(function (h) {
        h.li.classList.remove('active');
      });
      current.li.classList.add('active');
    }

    window.addEventListener('scroll', updateActiveByScroll, { passive: true });
    updateActiveByScroll();

    tocLinks.forEach(function (a) {
      if (!a.getAttribute('title')) a.setAttribute('title', a.textContent.trim());
      a.addEventListener('click', function (e) {
        e.preventDefault();
        var id = (a.getAttribute('href') || '').replace(/^#/, '');
        try {
          id = decodeURIComponent(id);
        } catch (err) {}
        navigateToSection(id, true);
        document.body.classList.remove('blog-sidebar-open');
      });
    });
  }

  function initHeadingClicks() {
    var content = document.querySelector('#main .markdown-section');
    if (!content) return;

    content.addEventListener('click', function (e) {
      var heading = e.target.closest('h2[id], h3[id], h4[id]');
      if (!heading || !content.contains(heading)) return;
      if (e.target.closest('a')) return;
      e.preventDefault();
      navigateToSection(heading.id, true);
    });
  }

  function initHashNavigation() {
    function applyHash(smooth) {
      var id = hashToId();
      if (!id) return;
      scrollToSection(id, smooth);
    }

    window.addEventListener('hashchange', function () {
      applyHash(true);
    });

    window.addEventListener('popstate', function () {
      applyHash(true);
    });

    if (location.hash) {
      if ('scrollRestoration' in history) {
        history.scrollRestoration = 'manual';
      }
      window.scrollTo(0, 0);
      requestAnimationFrame(function () {
        applyHash(false);
      });
    }
  }

  function scrollActiveSidebarItem() {
    var active = document.querySelector('.blog-sidebar .active-rep');
    var sidebar = document.querySelector('.blog-sidebar');
    if (!active || !sidebar) return;
    try {
      var offset = active.offsetTop - sidebar.clientHeight / 3;
      sidebar.scrollTop = Math.max(0, offset);
    } catch (e) {}
  }

  function initAdClose() {
    if (!window.jQuery || $(window).width() < 800) return;

    var allowJg = 86400000;
    try {
      var closeAdTime = localStorage.closeAdTimeRight;
      if (closeAdTime && (Date.now() - parseInt(closeAdTime, 10)) < allowJg) {
        $('.ad-box').remove();
        return;
      }
    } catch (e) {}

    $('.ad-close').click(function () {
      layer.confirm('关闭后，一天内不再展现此信息', function () {
        $('.ad-box').fadeOut(1000);
        layer.msg('关闭成功');
        localStorage.closeAdTimeRight = Date.now();
      });
    });
  }

  function initIndexDescClamp() {
    if (!document.body.classList.contains('blog-index-page')) return;

    function isMultilineTitle(el) {
      var range = document.createRange();
      range.selectNodeContents(el);
      return range.getClientRects().length > 1;
    }

    function update() {
      document.querySelectorAll('.blog-index-card').forEach(function (card) {
        var title = card.querySelector('.blog-index-card-text h2 a');
        if (!title) return;
        card.classList.toggle('blog-index-card--title-multiline', isMultilineTitle(title));
      });
    }

    update();
    var timer;
    window.addEventListener('resize', function () {
      clearTimeout(timer);
      timer = setTimeout(update, 150);
    });
  }

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  function platformCssSlug(platform) {
    var name = String(platform || '').trim();
    var lookup = {
      csdn: 'csdn',
      '掘金': 'juejin',
      '公众号': 'weixin',
      '51cto': '51cto',
      '博客园': 'cnblogs',
      '知乎': 'zhihu',
      '百家号': 'baijiahao',
      'php中文网': 'php'
    };
    var key = /^[\x00-\x7F]*$/.test(name) ? name.toLowerCase() : name;
    return lookup[key] || 'default';
  }

  function renderCommunityList(items) {
    var list = document.querySelector('.blog-community-list');
    if (!list) return;
    if (!items || !items.length) {
      list.innerHTML = '<p class="blog-community-empty">暂无社区文章</p>';
      return;
    }
    var html = '';
    items.forEach(function (item) {
      var slug = platformCssSlug(item.platform);
      html +=
        '<article class="blog-community-card">' +
        '<span class="blog-community-platform blog-community-platform--' + slug + '">' +
        escapeHtml(item.platform) + '</span>' +
        '<a class="blog-community-title" href="' + escapeHtml(item.url) + '" ' +
        'target="_blank" rel="noopener noreferrer">' + escapeHtml(item.title) + '</a>' +
        '<time class="blog-community-date" datetime="' + escapeHtml(item.date) + '">' +
        escapeHtml(item.date) + '</time>' +
        '</article>';
    });
    list.innerHTML = html;
  }

  function renderRecommendedList(items) {
    var list = document.querySelector('.blog-recommended-list');
    if (!list) return;
    if (!items || !items.length) {
      list.innerHTML = '<p class="blog-recommended-empty">暂无推荐文章</p>';
      return;
    }
    var html = '';
    items.forEach(function (item) {
      html +=
        '<article class="blog-recommended-card">' +
        '<a class="blog-recommended-title" href="' + escapeHtml(item.url) + '" ' +
        'target="_blank" rel="noopener noreferrer">' + escapeHtml(item.title) + '</a>' +
        '<time class="blog-recommended-date" datetime="' + escapeHtml(item.date) + '">' +
        escapeHtml(item.date) + '</time>' +
        '</article>';
    });
    list.innerHTML = html;
  }

  function initBlogExtraLists() {
    renderCommunityList(window.SA_TOKEN_BLOG_COMMUNITY);
    renderRecommendedList(window.SA_TOKEN_BLOG_RECOMMENDED);
  }

  function initBlogIndexTabs() {
    var tabBar = document.querySelector('.blog-index-tabs');
    if (!tabBar) return;

    var buttons = tabBar.querySelectorAll('.blog-index-tab');
    var panels = document.querySelectorAll('.blog-index-panel');

    function tabFromHash() {
      var hash = location.hash;
      if (hash === '#community') return 'community';
      if (hash === '#recommended') return 'recommended';
      return 'official';
    }

    function setTabHash(tab) {
      var nextHash = tab === 'official' ? '' : '#' + tab;
      if (location.hash === nextHash) return;
      if (nextHash) {
        history.replaceState(null, '', nextHash);
      } else {
        history.replaceState(null, '', location.pathname + location.search);
      }
    }

    function activateTab(tab, updateHash) {
      if (updateHash !== false) setTabHash(tab);
      buttons.forEach(function (item) {
        var active = item.getAttribute('data-tab') === tab;
        item.classList.toggle('is-active', active);
        item.setAttribute('aria-selected', active ? 'true' : 'false');
      });
      panels.forEach(function (panel) {
        panel.classList.toggle('is-active', panel.getAttribute('data-panel') === tab);
      });
    }

    buttons.forEach(function (btn) {
      btn.addEventListener('click', function () {
        activateTab(btn.getAttribute('data-tab'));
      });
    });

    window.addEventListener('hashchange', function () {
      activateTab(tabFromHash(), false);
    });

    activateTab(tabFromHash(), false);
  }

  function initImageZoom() {
    if (!document.body.classList.contains('blog-article-page')) return;
    if (typeof window.mediumZoom !== 'function') return;

    var matches = Element.prototype.matches || Element.prototype.msMatchesSelector;
    var imgs = [].slice.call(
      document.querySelectorAll('#main .markdown-section img:not(.emoji)')
    ).filter(function (img) {
      if (img.hasAttribute('data-no-zoom') && !img.closest('.blog-article-cover')) return false;
      return matches.call(img, 'a img') === false;
    });
    if (!imgs.length) return;

    window.mediumZoom(imgs, {
      margin: 24,
      background: 'rgba(0, 0, 0, 0.85)'
    });
  }

  function currentBlogUrl() {
    var path = (location.pathname || '').replace(/\\/g, '/');
    var idx = path.lastIndexOf('/blog/');
    if (idx < 0) return '';
    return path.slice(idx + 6);
  }

  function renderBlogSidebar() {
    var mount = document.querySelector('.blog-sidebar');
    if (!mount) return;
    var groups = window.SA_TOKEN_BLOG_SIDEBAR;
    if (!groups || !groups.length) return;

    var current = currentBlogUrl();
    var html = '<div class="sidebar-nav"><ul>';
    groups.forEach(function (group) {
      html += '<li><strong>' + escapeHtml(group.label) + '</strong><ul>';
      (group.items || []).forEach(function (item) {
        var active = item.url === current ? 'active-rep' : '';
        var title = escapeHtml(item.title);
        html +=
          '<li class="' + active + '">' +
          '<a href="/blog/' + escapeHtml(item.url) + '" title="' + title + '">' + title + '</a>' +
          '</li>';
      });
      html += '</ul></li>';
    });
    html += '</ul></div>';
    mount.innerHTML = html;
  }

  function initSidebarEnd() {
    var nav = document.querySelector('.blog-sidebar .sidebar-nav');
    if (!nav || nav.querySelector('.blog-sidebar-end')) return;
    var end = document.createElement('div');
    end.className = 'blog-sidebar-end';
    end.textContent = '--------- 到底线了 ---------';
    nav.appendChild(end);
  }

  /** 正文链接新窗口打开（锚点、上一篇/下一篇除外） */
  function initContentLinks() {
    if (!document.body.classList.contains('blog-article-page')) return;
    var section = document.querySelector('#main .markdown-section');
    if (!section) return;

    section.querySelectorAll('a[href]').forEach(function (a) {
      if (a.closest('.blog-post-nav')) return;
      var href = a.getAttribute('href') || '';
      if (!href || href.charAt(0) === '#' || href.indexOf('javascript:') === 0) return;
      a.setAttribute('target', '_blank');
      a.setAttribute('rel', 'noopener noreferrer');
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    initSidebarDrawer();
    renderBlogSidebar();
    initSidebarEnd();
    initBlogExtraLists();
    initBlogIndexTabs();
    initIndexDescClamp();
    initTocHighlight();
    initHeadingClicks();
    initHashNavigation();
    scrollActiveSidebarItem();
    initCodeHighlight();
    initCopyCode();
    initImageZoom();
    initContentLinks();
    initAdClose();
    if (typeof initSaTranslate === 'function') initSaTranslate();
  });
})();
