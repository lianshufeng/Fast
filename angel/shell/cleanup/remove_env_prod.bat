@echo off
set workPath=%~dp0..\..\..


:start
set choice=&set /p "choice="
if /i "%choice%"=="y" goto ye
goto ne

:ye
cd /d %workPath%
del /s /q  *-prod.yml
del /s /q  *-test.yml
del /s /q  *-pre.yml
del /s /q  *-dev.yml

:ne


goto start

pause

