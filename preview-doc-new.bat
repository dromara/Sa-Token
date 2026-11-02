@echo off
chcp 65001 >nul
cd /d "%~dp0sa-token-doc-new"
if errorlevel 1 (
  echo 找不到 sa-token-doc-new 目录
  pause
  exit /b 1
)

if not exist "node_modules\" (
  echo 首次运行，正在 npm i ...
  call npm i
  if errorlevel 1 (
    echo npm i 失败
    pause
    exit /b 1
  )
)

call npm run docs:dev -- --open
if errorlevel 1 (
  echo 预览启动失败
  pause
)
