@echo off
set workPath=%~dp0..


:start
echo �Ƿ�ɾ������������ȷ�������� y
set choice=&set /p "choice="
if /i "%choice%"=="y" goto ye
goto ne

:ye
echo ��ʼɾ�����е�������������
cd /d %workPath%
del /s /q  *-prod.yml
del /s /q  *-test.yml
del /s /q  *-pre.yml
del /s /q  *-dev.yml

:ne
echo "ȡ������"


goto start

pause

