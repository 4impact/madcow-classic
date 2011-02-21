@echo off
setLocal EnableDelayedExpansion

set CLASSPATH="

for /R ./.madcow/lib %%a in (*.jar) do (
  set CLASSPATH=!CLASSPATH!;%%a
)
for /R ./lib %%a in (*.jar) do (
  set CLASSPATH=!CLASSPATH!;%%a
)
for /R ./.madcow/webtest/lib %%a in (*.jar) do (
  set CLASSPATH=!CLASSPATH!;%%a
)

set CLASSPATH=!CLASSPATH!"

java au.com.ts4impact.madcow.ant.MadcowAntProject %*
pause
