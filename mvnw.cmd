@echo off

SET BASE_DIR=%~dp0
SET WRAPPER_JAR=%BASE_DIR%.mvn\wrapper\maven-wrapper.jar
SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

IF NOT EXIST "%WRAPPER_JAR%" (
    echo Downloading Maven Wrapper...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar' -OutFile '%WRAPPER_JAR%'"
)

IF "%JAVA_HOME%"=="" (
    SET JAVA_CMD=java
) ELSE (
    SET JAVA_CMD=%JAVA_HOME%\bin\java
)

REM %~dp0 termina con \, che in una stringa tra virgolette farebbe escaping della " finale.
REM Rimuoviamo l'ultimo carattere per evitarlo.
SET PROJECT_DIR=%BASE_DIR:~0,-1%

%JAVA_CMD% -classpath "%WRAPPER_JAR%" -Dmaven.multiModuleProjectDirectory="%PROJECT_DIR%" %WRAPPER_LAUNCHER% %*
