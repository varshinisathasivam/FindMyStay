@echo off
setlocal

echo ===================================================
echo   FindMyStay - Build Script (Java 21 Compatible)
echo ===================================================

REM ----- Setup directories (Clean previous classes for fresh compilation) -----
if exist "build\classes" rmdir /s /q "build\classes"
mkdir "build\classes"
if not exist "build\dist"    mkdir "build\dist"
if not exist "src\webapp\WEB-INF\lib" mkdir "src\webapp\WEB-INF\lib"

REM ----- Copy runtime JARs into WEB-INF\lib so the WAR is self-contained -----
echo Copying runtime JARs into WEB-INF\lib...
copy /Y "lib\mysql-connector-j.jar"      "src\webapp\WEB-INF\lib\" > nul
copy /Y "lib\tomcat-embed-el.jar"        "src\webapp\WEB-INF\lib\" > nul
copy /Y "lib\jakarta.annotation-api.jar" "src\webapp\WEB-INF\lib\" > nul
copy /Y "lib\jbcrypt-0.4.jar"            "src\webapp\WEB-INF\lib\" > nul
echo Done.

REM ----- Compile all Java sources for Java 21 target -----
echo Compiling Java sources with --release 21...
dir /s /b src\com\findmystay\*.java > sources.txt
javac --release 21 -encoding UTF-8 -cp "lib\jakarta.servlet-api.jar;lib\tomcat-embed-core.jar;lib\mysql-connector-j.jar;lib\jakarta.annotation-api.jar;lib\jbcrypt-0.4.jar;lib\*" -d build\classes @sources.txt
set "JAVAC_STATUS=%ERRORLEVEL%"

if exist sources.txt del /f /q sources.txt

if not "%JAVAC_STATUS%"=="0" goto :compile_failed
echo Compilation successful!

REM ----- Copy configuration template (No hardcoded credentials) -----
if exist "src\db.properties" (
    copy /Y "src\db.properties" "build\classes\db.properties" > nul
)
if exist "src\webapp\WEB-INF\classes" rmdir /s /q "src\webapp\WEB-INF\classes"

REM ----- Stage files for WAR packaging (classes inside WEB-INF/classes) -----
echo Staging WAR directory structure...
if exist "build\staging" rmdir /s /q "build\staging"
mkdir "build\staging"
xcopy /E /I /Q /Y "src\webapp" "build\staging" > nul
if not exist "build\staging\WEB-INF\classes" mkdir "build\staging\WEB-INF\classes"
xcopy /E /I /Q /Y "build\classes" "build\staging\WEB-INF\classes" > nul

REM Ensure no sensitive or local database properties file is packaged into the WAR
if exist "build\staging\WEB-INF\classes\db.properties" del /f /q "build\staging\WEB-INF\classes\db.properties" > nul

REM ----- Package WAR -----
echo.
echo Packaging FindMyStay.war...
jar -cvf build\dist\FindMyStay.war -C build\staging .
set "JAR_STATUS=%ERRORLEVEL%"

if exist "build\staging" rmdir /s /q "build\staging"

if not "%JAR_STATUS%"=="0" goto :packaging_failed
if not exist "build\dist\FindMyStay.war" goto :war_missing

echo.
echo ===================================================
echo   BUILD COMPLETE (Java 21 Bytecode Verified)
echo   WAR  : build\dist\FindMyStay.war
echo   Run  : run.bat
echo ===================================================
endlocal
exit /b 0

:compile_failed
echo.
echo ===================================================
echo   [ERROR] Compilation failed with error code %JAVAC_STATUS%
echo ===================================================
endlocal
exit /b %JAVAC_STATUS%

:packaging_failed
echo.
echo ===================================================
echo   [ERROR] WAR packaging failed with error code %JAR_STATUS%
echo ===================================================
endlocal
exit /b %JAR_STATUS%

:war_missing
echo.
echo ===================================================
echo   [ERROR] WAR file was not generated!
echo ===================================================
endlocal
exit /b 1
