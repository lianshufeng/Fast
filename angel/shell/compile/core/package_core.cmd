@echo off
set workPath=%~dp0..\..\..\..
cd /d %workPath%

echo start package
mvn clean package -P %1