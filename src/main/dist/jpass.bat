@echo off

if exist "%JAVA_HOME%\bin\javaw.exe" (
  set "JAVA_EXE=%JAVA_HOME%\bin\javaw.exe"
) else (
  set JAVA_EXE=javaw.exe
)

"%JAVA_EXE%" -version >nul 2>&1
if %ERRORLEVEL% neq 0 goto fail

set BASEDIR=%~f0

:strip
set REMOVED=%BASEDIR:~-1%
set BASEDIR=%BASEDIR:~0,-1%
if not "%REMOVED%" == "\" goto strip

set JPASS_HOME=%BASEDIR%

:launch
start "JPass" /B "%JAVA_EXE%" -jar "%JPASS_HOME%\jpass-0.1.27-RELEASE.jar" %*
if %ERRORLEVEL% neq 0 goto fail
goto end

:fail
echo Could not execute JPass (exit: %ERRORLEVEL%)
goto end

:end
