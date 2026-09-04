@echo off
chcp 65001 >nul

:: Sa-Token 全量单元测试 + 集成测试
call mvn test

echo.
echo ----------- Coverage Summary Pages -----------
echo Aggregate : sa-token-testing\sa-token-coverage\target\site\jacoco-aggregate\coverage-summary.html
echo Core      : sa-token-core\target\site\jacoco\coverage-summary.html
echo JaCoCo    : sa-token-testing\sa-token-coverage\target\site\jacoco-aggregate\index.html
echo.
echo ----------- test end -----------
echo.
pause
