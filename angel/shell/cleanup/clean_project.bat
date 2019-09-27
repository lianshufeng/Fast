@echo off
set workPath=%~dp0..\..\..

echo remove logs
rd /s /q %workPath%\logs

echo remove idead
rd /s /q %workPath%\.idea
cd /d %workPath%
del /q /s *.iml

echo mavn clean 
cd /d %workPath%
cmd /c  mvn clean

pause