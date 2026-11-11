@echo off
echo ===================================================
echo   FindMyStay - Starting Embedded Server
echo ===================================================

REM Check if compiled
if not exist "build\classes\com\findmystay\server\EmbeddedServer.class" (
    echo Classes not found. Running compile.bat first...
    call compile.bat
    if %ERRORLEVEL% NEQ 0 (
        echo Build failed. Cannot start server.
        pause
        exit /b 1
    )
)

echo Starting server on http://localhost:8080/
echo Press Ctrl+C to stop.
echo.

java -cp "build\classes;lib\*" com.findmystay.server.EmbeddedServer

pause

