@echo off
set workPath=%~dp0..


:start
echo 是否删除环境变量，确认请输入 y
set choice=&set /p "choice="
if /i "%choice%"=="y" goto ye
goto ne

:ye
echo 开始删除所有的生产环境配置
cd /d %workPath%
del /s /q  *-prod.yml
del /s /q  *-test.yml
del /s /q  *-pre.yml
del /s /q  *-dev.yml

:ne
echo "取消操作"


goto start

pause

