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
rem log keeps full stacks; console drops at / Caused-by frames
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
@>> "%TEE_PS%" echo $lineMem = New-Object System.IO.MemoryStream
@>> "%TEE_PS%" echo $utf8 = New-Object System.Text.UTF8Encoding $false
@>> "%TEE_PS%" echo try {
@>> "%TEE_PS%" echo while (($n = $in.Read($buf, 0, $buf.Length)) -gt 0) {
@>> "%TEE_PS%" echo $fs.Write($buf, 0, $n)
@>> "%TEE_PS%" echo for ($i = 0; $i -lt $n; $i++) {
@>> "%TEE_PS%" echo $b = $buf[$i]
@>> "%TEE_PS%" echo [void]$lineMem.WriteByte($b)
@>> "%TEE_PS%" echo if ($b -ne 10) { continue }
@>> "%TEE_PS%" echo $lineBytes = $lineMem.ToArray()
@>> "%TEE_PS%" echo $lineMem.SetLength(0)
@>> "%TEE_PS%" echo $s = $utf8.GetString($lineBytes)
@>> "%TEE_PS%" echo $plain = [regex]::Replace($s, [char]27 + '\[[0-9;]*m', '')
@>> "%TEE_PS%" echo $t = $plain.TrimStart()
@>> "%TEE_PS%" echo if ($t.StartsWith('at ')) { continue }
@>> "%TEE_PS%" echo if ($t.StartsWith('Caused by:')) { continue }
@>> "%TEE_PS%" echo if ($t.StartsWith('... ') -and $t.Contains(' more')) { continue }
@>> "%TEE_PS%" echo $out.Write($lineBytes, 0, $lineBytes.Length)
@>> "%TEE_PS%" echo $out.Flush()
@>> "%TEE_PS%" echo }
@>> "%TEE_PS%" echo }
@>> "%TEE_PS%" echo if ($lineMem.Length -gt 0) { $rest = $lineMem.ToArray(); $out.Write($rest, 0, $rest.Length); $out.Flush() }
@>> "%TEE_PS%" echo } finally { $fs.Dispose() }

rem clean: IDE may write broken class stubs into target; skip compile otherwise.
@for /f %%I in ('powershell -NoProfile -Command "(Get-Date).Ticks"') do set "T0=%%I"
@call mvn -Dstyle.color=always -DskipTests=false -Djacoco.skip=false clean verify -pl sa-token-testing/sa-token-coverage -am 2>&1 | powershell -NoProfile -ExecutionPolicy Bypass -File "%TEE_PS%"

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
