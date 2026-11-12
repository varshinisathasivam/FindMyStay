@echo off
setlocal

echo ===================================================
echo   FindMyStay - Starting Embedded Server
echo ===================================================

REM Compile latest sources before starting the server
echo Checking and building latest sources (Java 21)...
call compile.bat
set "BUILD_STATUS=%ERRORLEVEL%"

if not "%BUILD_STATUS%"=="0" goto :build_failed

echo.
echo ===================================================
echo   Starting server on http://localhost:8080/
echo   Press Ctrl+C to stop.
echo ===================================================
echo.

java -cp "build\classes;lib\*" com.findmystay.server.EmbeddedServer
goto :end

:build_failed
echo.
echo ===================================================
echo   [ERROR] Build failed (Exit code: %BUILD_STATUS%). Server cannot be started.
echo ===================================================
pause
exit /b %BUILD_STATUS%

:end
endlocal
exit /b 0
