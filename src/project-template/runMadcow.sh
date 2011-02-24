#!/bin/sh

if [ -z "$JAVA_HOME" ] ; then
    echo "environment variable JAVA_HOME must be set"
    exit 1
fi

$JAVA_HOME/bin/java -classpath ./.madcow/*:./.madcow/lib/*:lib/*:./.madcow/webtest/lib/* com.projectmadcow.ant.MadcowAntProject $@
