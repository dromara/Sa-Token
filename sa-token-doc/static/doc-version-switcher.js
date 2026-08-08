(function (root, factory) {
  var api = factory();

  if (typeof module === "object" && module.exports) {
    module.exports = api;
  }

  root.DocVersionSwitcher = api;
})(typeof globalThis !== "undefined" ? globalThis : this, function () {
  var LATEST_VERSION = "v1.45.0";
  var VERSION_OPTIONS = [
    { label: "v1.44.0", value: "/v/v1.44.0/doc.html" },
    { label: "v1.43.0", value: "/v/v1.43.0/doc.html" },
    { label: "v1.42.0", value: "/v/v1.42.0/doc.html" },
    { label: "v1.41.0", value: "/v/v1.41.0/doc.html" },
    { label: "v1.40.0", value: "/v/v1.40.0/doc.html" },
    { label: "v1.39.0", value: "/v/v1.39.0/doc.html" },
    { label: "v1.38.0", value: "/v/v1.38.0/doc.html" },
    { label: "v1.37.0", value: "/v/v1.37.0/doc.html" },
    { label: "v1.36.0", value: "/v/v1.36.0/doc.html" },
    { label: "v1.35.0", value: "/v/v1.35.0/doc.html" },
    { label: "v1.34.0", value: "/v/v1.34.0/doc.html" },
    { label: "v1.33.0", value: "/v/v1.33.0/doc.html" },
    { label: "v1.32.0", value: "/v/v1.32.0/doc.html" },
    { label: "v1.31.0", value: "/v/v1.31.0/doc.html" },
    { label: "v1.30.0", value: "/v/v1.30.0/doc/index.html" },
    { label: "v1.29.0", value: "/v/v1.29.0/doc/index.html" },
    { label: "v1.28.0", value: "/v/v1.28.0/doc/index.html" },
    { label: "v1.27.0", value: "/v/v1.27.0/doc/index.html" },
    { label: "v1.26.0", value: "/v/v1.26.0/doc/index.html" },
    { label: "v1.25.0", value: "/v/v1.25.0/doc/index.html" },
    { label: "v1.24.0", value: "/v/v1.24.0/doc/index.html" },
    { label: "v1.23.0", value: "/v/v1.23.0/doc/index.html" },
    { label: "v1.22.0", value: "/v/v1.22.0/doc/index.html" },
    { label: "v1.21.0", value: "/v/v1.21.0/doc/index.html" },
    { label: "v1.20.0", value: "/v/v1.20.0/doc/index.html" },
    { label: "v1.19.0", value: "/v/v1.19.0/doc/index.html" },
    { label: "v1.18.0", value: "/v/v1.18.0/doc/index.html" },
    { label: "v1.17.0", value: "/v/v1.17.0/doc/index.html" },
    { label: "v1.16.0", value: "/v/v1.16.0/doc/index.html" },
    { label: "v1.15.0", value: "/v/v1.15.0/doc/index.html" },
    { label: "v1.14.0", value: "/v/v1.14.0/doc/index.html" },
    { label: "v1.13.0", value: "/v/v1.13.0/doc/index.html" },
    { label: "v1.12.1", value: "/v/v1.12.1/doc/index.html" },
    { label: "v1.12.0", value: "/v/v1.12.0/doc/index.html" },
    { label: "v1.11.0", value: "/v/v1.11.0/doc/index.html" },
    { label: "v1.10.0", value: "/v/v1.10.0/doc/index.html" },
    { label: "v1.9.0", value: "/v/v1.9.0/doc/index.html" },
    { label: "v1.8.0", value: "/v/v1.8.0/doc/index.html" },
    { label: "v1.7.0", value: "/v/v1.7.0/doc/index.html" },
    { label: "v1.6.0", value: "/v/v1.6.0/doc/index.html" },
    { label: "v1.5.1", value: "/v/v1.5.1/doc/index.html" },
    { label: "v1.4.0", value: "/v/v1.4.0/doc/index.html" },
    { label: "v1.3.0", value: "/v/v1.3.0/doc/index.html" },
    { label: "v1.2.0", value: "/v/v1.2.0/doc/index.html" },
    { label: "v1.1.0", value: "/v/v1.1.0/doc/index.html" },
    { label: "v1.0.0", value: "/v/v1.0.0/doc/index.html" }
  ];

  function normalizePath(path) {
    if (!path) {
      return "/doc.html";
    }

    return path.split("?")[0].split("#")[0];
  }

  function getCurrentVersion(path) {
    var normalizedPath = normalizePath(path);
    var match = normalizedPath.match(/^\/v\/(v[^/]+)\/doc(?:\/index)?\.html$/);

    if (match) {
      return match[1];
    }

    return "latest";
  }

  function resolveVersionPath(version) {
    if (version === "latest") {
      return "/doc.html";
    }

    if (version === "home") {
      return "/";
    }

    var versionOption = VERSION_OPTIONS.find(function (option) {
      return option.label === version || option.value === version;
    });

    return versionOption ? versionOption.value : version;
  }

  function buildVersionOptions(currentPath) {
    var currentVersion = getCurrentVersion(currentPath);
    var options = [
      {
        value: "latest",
        label: "最新版(" + LATEST_VERSION + ")",
        path: "/doc.html",
        selected: currentVersion === "latest"
      }
    ];

    VERSION_OPTIONS.forEach(function (option) {
      options.push({
        value: option.value,
        label: option.label,
        path: option.value,
        selected: option.label === currentVersion
      });
    });

    options.push({
      value: "/",
      label: "首页",
      path: "/",
      selected: false
    });

    return options;
  }

  function buildTargetPath(option, currentHash) {
    var targetPath = option && option.path ? option.path : resolveVersionPath(option.value);

    if (!currentHash || targetPath === "/") {
      return targetPath;
    }

    return targetPath + currentHash;
  }

  function render(selectElement, currentPath, currentHash) {
    if (!selectElement) {
      return;
    }

    var options = buildVersionOptions(currentPath);

    selectElement.innerHTML = "";
    selectElement.onchange = null;

    options.forEach(function (option) {
      var optionElement = document.createElement("option");
      optionElement.value = option.value;
      optionElement.textContent = option.label;
      optionElement.selected = option.selected;
      optionElement.dataset.path = option.path;
      selectElement.appendChild(optionElement);
    });

    selectElement.onchange = function () {
      var selectedOption = options[selectElement.selectedIndex];
      window.location.href = buildTargetPath(selectedOption, currentHash || window.location.hash);
    };
  }

  function init(config) {
    if (typeof document === "undefined") {
      return;
    }

    var target = (config && config.select) || document.querySelector(".select-version");
    var currentPath = (config && config.currentPath) || window.location.pathname;
    var currentHash = (config && config.currentHash) || window.location.hash;

    render(target, currentPath, currentHash);
  }

  return {
    LATEST_VERSION: LATEST_VERSION,
    VERSION_OPTIONS: VERSION_OPTIONS,
    buildVersionOptions: buildVersionOptions,
    resolveVersionPath: resolveVersionPath,
    init: init
  };
});
