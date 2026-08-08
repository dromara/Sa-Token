const test = require("node:test");
const assert = require("node:assert/strict");

const {
  VERSION_OPTIONS,
  buildVersionOptions,
  init,
  resolveVersionPath,
} = require("./doc-version-switcher.js");

test("最新版路径始终回到根级 doc.html", () => {
  assert.equal(resolveVersionPath("latest"), "/doc.html");
});

test("旧版本路径使用绝对路径", () => {
  assert.equal(resolveVersionPath("v1.39.0"), "/v/v1.39.0/doc.html");
  assert.equal(resolveVersionPath("v1.30.0"), "/v/v1.30.0/doc/index.html");
});

test("当前位于旧版本页面时，构建出的下拉列表包含统一的最新版本集合，并正确选中当前版本", () => {
  const options = buildVersionOptions("/v/v1.39.0/doc.html");

  assert.equal(options[0].value, "latest");
  assert.equal(options[0].label, "最新版(v1.45.0)");
  assert.equal(options[0].selected, false);

  assert.equal(options[1].label, "v1.44.0");
  assert.equal(options[1].selected, false);

  const current = options.find((option) => option.label === "v1.39.0");
  assert.ok(current);
  assert.equal(current.selected, true);

  const oldest = options.find((option) => option.label === "v1.0.0");
  assert.ok(oldest);
  assert.equal(oldest.value, "/v/v1.0.0/doc/index.html");

  const home = options.find((option) => option.label === "首页");
  assert.ok(home);
  assert.equal(home.value, "/");

  assert.equal(options.length, VERSION_OPTIONS.length + 2);
});

test("当前位于最新版页面时，最新版被正确选中", () => {
  const options = buildVersionOptions("/doc.html");

  assert.equal(options[0].selected, true);
  assert.equal(options[1].selected, false);
});

test("旧格式的 /doc/index.html 页面也能正确选中当前版本", () => {
  const options = buildVersionOptions("/v/v1.30.0/doc/index.html");
  const current = options.find((option) => option.label === "v1.30.0");

  assert.ok(current);
  assert.equal(current.selected, true);
});

test("旧版本页面点击最新版时，会正确跳回最新版并保留当前 hash", () => {
  const select = createFakeSelect();
  const originalDocument = global.document;
  const originalWindow = global.window;

  global.document = createFakeDocument(select);
  global.window = { location: { pathname: "/v/v1.39.0/doc.html", hash: "#/start/example", href: "" } };

  init({ select, currentPath: global.window.location.pathname, currentHash: global.window.location.hash });

  select.selectedIndex = 0;
  select.onchange();

  assert.equal(global.window.location.href, "/doc.html#/start/example");

  global.document = originalDocument;
  global.window = originalWindow;
});

test("旧版本页面切换到其他旧版本时，会使用统一版本列表并保留当前 hash", () => {
  const select = createFakeSelect();
  const originalDocument = global.document;
  const originalWindow = global.window;

  global.document = createFakeDocument(select);
  global.window = { location: { pathname: "/v/v1.39.0/doc.html", hash: "#/start/example", href: "" } };

  init({ select, currentPath: global.window.location.pathname, currentHash: global.window.location.hash });

  const targetIndex = select.children.findIndex((option) => option.textContent === "v1.38.0");
  assert.notEqual(targetIndex, -1);

  select.selectedIndex = targetIndex;
  select.onchange();

  assert.equal(global.window.location.href, "/v/v1.38.0/doc.html#/start/example");

  global.document = originalDocument;
  global.window = originalWindow;
});

function createFakeSelect() {
  return {
    children: [],
    innerHTML: "",
    onchange: null,
    selectedIndex: 0,
    appendChild(child) {
      this.children.push(child);
    },
  };
}

function createFakeDocument(select) {
  return {
    querySelector() {
      return select;
    },
    createElement() {
      return {
        dataset: {},
        value: "",
        textContent: "",
        selected: false,
      };
    },
  };
}
