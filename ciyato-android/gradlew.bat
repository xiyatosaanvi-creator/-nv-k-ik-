@echo off
rem Gradle wrapper batch script for Windows
set APP_HOME=%~dp0
set GRADLE_WRAPPER_JAR=%APP_HOME%gradle\wrapper\gradle-wrapper.jar

if not exist "%GRADLE_WRAPPER_JAR%" (
    echo Gradle wrapper JAR not found. Please open this project in Android Studio.
    echo Android Studio will download the Gradle wrapper automatically.
    exit /b 1
)

java -jar "%GRADLE_WRAPPER_JAR%" %*
