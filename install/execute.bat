@echo off

REM Copyright (C) 2016 VSCT
REM
REM Licensed under the Apache License, Version 2.0 (the "License");
REM you may not use this file except in compliance with the License.
REM You may obtain a copy of the License at
REM
REM http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.

@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT" setlocal

set JBOSS_CLI=%JBOSS_HOME%\bin\jboss-cli.bat
set JBOSS_MODE=standalone
set JBOSS_CONFIG=%JBOSS_MODE%.xml
set NOPAUSE=true

echo => Starting WildFly server
START CMD /C CALL %JBOSS_HOME%/bin/%JBOSS_MODE%.bat -c %JBOSS_CONFIG%

echo => Waiting for the server to boot
:wait_for_server
CALL %JBOSS_CLI% -c "ls /deployment"
if NOT %ERRORLEVEL% == 0 GOTO wait_for_server

echo => Executing the commands
CALL %JBOSS_CLI% -c --file=commands.cli

echo => Shutting down WildFly
if %JBOSS_MODE% == standalone (
    CALL %JBOSS_CLI% -c ":shutdown"
) else (
    CALL %JBOSS_CLI% -c "/host=*:shutdown"
)