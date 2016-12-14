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

SET JBOSS_USER=admin
SET JBOSS_PASSWORD=password
SET KEYCLOAK_USER=admin
SET KEYCLOAK_PASSWORD=password
SET KEYCLOAK_KEYSTORE_PASSWORD=secret

SET KEYCLOAK_VERSION=keycloak-2.3.0.Final
SET JBOSS_HOME=.\%KEYCLOAK_VERSION%
SET KEYCLOAK_PACKAGE=.\%KEYCLOAK_VERSION%.zip
SET NOPAUSE=true

CALL mvn -B -f ..\AccountFederationProvider clean assembly:assembly

CALL RMDIR %KEYCLOAK_VERSION% /s /q
CALL jar xf %KEYCLOAK_PACKAGE%

CALL keytool -genkey -noprompt -alias localhost -keyalg RSA -dname "CN=connect.voyages-sncf.com, OU=keycloak, O=VSCT, L=Nantes, S=France, C=FR" -keystore %JBOSS_HOME%\standalone\configuration\keycloak.jks -storepass %KEYCLOAK_KEYSTORE_PASSWORD% -keypass %KEYCLOAK_KEYSTORE_PASSWORD% -validity 10950

CALL execute.bat
CALL %JBOSS_HOME%\bin\add-user-keycloak.bat --user %KEYCLOAK_USER% --password %KEYCLOAK_PASSWORD% --realm master
CALL %JBOSS_HOME%\bin\add-user.bat --user %JBOSS_USER% --password %JBOSS_PASSWORD%

CALL %JBOSS_HOME%\bin\standalone.bat -Dkeycloak.migration.action=import -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.file=keycloak.json -Dkeycloak.migration.strategy=OVERWRITE_EXISTING