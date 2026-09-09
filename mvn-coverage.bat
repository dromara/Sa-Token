@echo off
@chcp 65001 >nul
@cd /d "%~dp0"

@if not exist "build-log" mkdir "build-log"

@for /f %%I in ('powershell -NoProfile -Command "Get-Date -Format yyyy-MM-dd-HH-mm-ss"') do set "TS=%%I"
@set "LOG=build-log\mvn-coverage-%TS%.log"
@set "COVERAGE_LOG=%CD%\%LOG%"
@set "TEE_PS=%TEMP%\sa-token-mvn-coverage-tee.ps1"

@echo.
@echo 构建日志同步输出到: %LOG%
@echo.

rem pipe is not a TTY; keep Maven color. JVM UTF-8.
rem copy log by bytes; do not use Tee-Object, it strips ANSI.
@set "MAVEN_OPTS=%MAVEN_OPTS% -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8"

@> "%TEE_PS%" echo $log = $env:COVERAGE_LOG
@>> "%TEE_PS%" echo if ([string]::IsNullOrWhiteSpace($log)) { throw 'COVERAGE_LOG is empty' }
@>> "%TEE_PS%" echo try {
@>> "%TEE_PS%" echo Add-Type -TypeDefinition 'using System; using System.Runtime.InteropServices; public class Win32CM { [DllImport("kernel32.dll")] public static extern IntPtr GetStdHandle(int n); [DllImport("kernel32.dll")] public static extern bool GetConsoleMode(IntPtr h, out uint m); [DllImport("kernel32.dll")] public static extern bool SetConsoleMode(IntPtr h, uint m); }'
@>> "%TEE_PS%" echo $h = [Win32CM]::GetStdHandle(-11)
@>> "%TEE_PS%" echo $m = 0
@>> "%TEE_PS%" echo if ([Win32CM]::GetConsoleMode($h, [ref]$m)) { [void][Win32CM]::SetConsoleMode($h, $m -bor 4) }
@>> "%TEE_PS%" echo } catch {}
@>> "%TEE_PS%" echo $in = [Console]::OpenStandardInput()
@>> "%TEE_PS%" echo $out = [Console]::OpenStandardOutput()
@>> "%TEE_PS%" echo $fs = [IO.File]::Create($log)
@>> "%TEE_PS%" echo $buf = New-Object byte[] 8192
@>> "%TEE_PS%" echo try {
@>> "%TEE_PS%" echo while (($n = $in.Read($buf, 0, $buf.Length)) -gt 0) { $out.Write($buf, 0, $n); $out.Flush(); $fs.Write($buf, 0, $n) }
@>> "%TEE_PS%" echo } finally { $fs.Dispose() }

rem clean: IDE may write broken class stubs into target; skip compile otherwise.
@for /f %%I in ('powershell -NoProfile -Command "(Get-Date).Ticks"') do set "T0=%%I"
@call mvn -Dstyle.color=always clean verify -pl sa-token-testing/sa-token-coverage -am 2>&1 | powershell -NoProfile -ExecutionPolicy Bypass -File "%TEE_PS%"

rem mvn.cmd turns echo back on after a pipe; keep @ on later lines.
@echo off
@for /f "delims=" %%I in ('powershell -NoProfile -Command "$t=[TimeSpan]::FromTicks((Get-Date).Ticks-$env:T0); $m=[int][Math]::Floor($t.TotalMinutes); '{0} min {1} s' -f $m,$t.Seconds"') do set "ELAPSED=%%I"
@echo.
@echo.
@echo ----------- coverage report -----------
@echo 全仓库汇总报告:
@echo   sa-token-testing\sa-token-coverage\target\site\jacoco-aggregate\index.html
@echo.
@echo 各模块报告 (示例):
@echo   sa-token-core\target\site\jacoco\index.html
@echo   sa-token-testing\sa-token-integration-boot2\target\site\jacoco\index.html
@echo.
@echo 本次构建日志:
@echo   %LOG%
@echo 本轮构建耗时: %ELAPSED%
@echo.
@pause
