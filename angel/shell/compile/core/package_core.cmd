@echo off
set workPath=%~dp0..\..\..\..
cd /d %workPath%

echo start package
:: mvn -T 1C -Dmaven.javadoc.skip=true -Dmaven.test.skip=true -Denforcer.skip=true -Dfindbugs.skip=true -Dcheckstyle.skip=true  clean package -P %1
mvn clean package -P %1