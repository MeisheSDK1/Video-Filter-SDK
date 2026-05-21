@echo off 
ECHO start bat
ECHO 1 %1
ECHO 2 %2
ECHO 3 %3

set ftpUser=ftpuser
ECHO %ftpUser%
set ftpPass=Meishe2020
ECHO %ftpPass%
set ftpIP=192.168.100.111
ECHO ftpIP: %ftpIP%
set ftpFolder=%1
set LocalFolder=%2
echo 'ftpFolder: '%ftpFolder%
echo 'LocalFolder: '%LocalFolder%

echo open 192.168.100.111>>ftp.tmp
echo ftpuser>>ftp.tmp
echo Meishe2020>>ftp.tmp
echo cd "%ftpFolder%">>ftp.tmp
echo lcd "%LocalFolder%">>ftp.tmp
echo mget %3>>ftp.tmp
echo bye>>ftp.tmp
ftp -v -i -s:ftp.tmp
del ftp.tmp
pause
@echo on