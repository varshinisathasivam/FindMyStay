@echo off
echo ===================================================
echo   FindMyStay - Build Script
echo ===================================================

REM ----- Setup directories -----
if not exist "build\classes" mkdir "build\classes"
if not exist "build\dist"    mkdir "build\dist"
if not exist "src\webapp\WEB-INF\lib" mkdir "src\webapp\WEB-INF\lib"

REM ----- Copy runtime JARs into WEB-INF\lib so the WAR is self-contained -----
echo Copying runtime JARs into WEB-INF\lib...
copy /Y "lib\mysql-connector-j.jar"      "src\webapp\WEB-INF\lib\" > nul
copy /Y "lib\tomcat-embed-el.jar"        "src\webapp\WEB-INF\lib\" > nul
copy /Y "lib\jakarta.annotation-api.jar" "src\webapp\WEB-INF\lib\" > nul
echo Done.

REM ----- Compile all Java sources -----
echo Compiling Java sources...
dir /s /b src\com\findmystay\*.java > sources.txt
javac -encoding UTF-8 -cp "lib\jakarta.servlet-api.jar;lib\tomcat-embed-core.jar;lib\mysql-connector-j.jar;lib\jakarta.annotation-api.jar;lib\*" -d build\classes @sources.txt
del sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed with error code %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)
echo Compilation successful!

REM ----- Stage files for WAR packaging (classes inside WEB-INF/classes) -----
echo Staging WAR directory structure...
if exist "build\staging" rmdir /s /q "build\staging"
mkdir "build\staging"
xcopy /E /I /Q /Y "src\webapp" "build\staging" > nul
if not exist "build\staging\WEB-INF\classes" mkdir "build\staging\WEB-INF\classes"
xcopy /E /I /Q /Y "build\classes" "build\staging\WEB-INF\classes" > nul

REM ----- Package WAR -----
echo.
echo Packaging FindMyStay.war...
jar -cvf build\dist\FindMyStay.war -C build\staging .
rmdir /s /q "build\staging"
echo.
echo ===================================================
echo   BUILD COMPLETE
echo   WAR  : build\dist\FindMyStay.war
echo   Run  : run.bat
echo ===================================================
