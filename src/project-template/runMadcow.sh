#!/bin/sh

if [ -z "$JAVA_HOME" ] ; then
    echo "environment variable JAVA_HOME must be set"
    exit 1
fi

export MADCOW_LIB_CLASSPATH=`find ./.madcow/lib -name "*.jar" | tr "\n" ":"`
export MADCOW_CONF_CLASSPATH=./conf
export PROJECT_LIB_CLASSPATH=`find ./lib -name "*.jar" | tr "\n" ":"`
export WEBTEST_LIB_CLASSPATH=`find ./.madcow/webtest/lib -name "*.jar" | tr "\n" ":"`

export CLASSPATH=$MADCOW_LIB_CLASSPATH:$MADCOW_CONF_CLASSPATH:$PROJECT_LIB_CLASSPATH:$WEBTEST_LIB_CLASSPATH

$JAVA_HOME/bin/java -classpath $CLASSPATH com.projectmadcow.ant.MadcowAntProject $@