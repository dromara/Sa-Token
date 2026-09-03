@echo off
chcp 65001 >nul

:: Sa-Token 全量测试 + JaCoCo 覆盖率聚合报告
call mvn verify -pl sa-token-testing/sa-token-coverage -am

echo.
echo.
echo ----------- coverage report -----------
echo 全仓库汇总报告:
echo   sa-token-testing\sa-token-coverage\target\site\jacoco-aggregate\index.html
echo.
echo 各模块报告 (示例):
echo   sa-token-core\target\site\jacoco\index.html
echo   sa-token-testing\sa-token-integration-boot2\target\site\jacoco\index.html
echo.
pause
