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
goto launch

:fail
echo Install Java (JDK or JRE) if you do not already have. JPass will not work without it.
echo Please make sure PATH, or JAVA_HOME environment variables point to a valid Java installation.
echo Could not execute JPass (exit: %ERRORLEVEL%)
pause
goto end

:launch
start "JPass" /B "%JAVA_EXE%" -jar "%JPASS_HOME%\jpass-1.0.2-SNAPSHOT.jar" %*
goto end

:end
