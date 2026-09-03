@echo off
chcp 65001 >nul

:: Sa-Token 全量单元测试 + 集成测试
call mvn test

echo.
echo.
echo ----------- test end -----------
echo.
pause
