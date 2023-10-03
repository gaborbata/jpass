#!/bin/sh

# JPass installation script for Linux

# check if java executable exists
if [ -d "$JAVA_HOME" -a -x "$JAVA_HOME/bin/java" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD=java
fi

$JAVACMD -version >/dev/null 2>&1
if [ "$?" != "0" ]; then
  echo "Install Java (JDK or JRE) if you do not already have. JPass will not work without it."
  echo "Please make sure PATH, or JAVA_HOME environment variables point to a valid Java installation."
  echo "Could not execute JPass (exit: $?)"
  exit
fi

# detect the absolute path of jpass
JPASS_HOME=`dirname "$0"`

# detect cygwin
cygwin=false;
case "`uname -s`" in
    CYGWIN*)
        cygwin=true
        ;;
esac

if $cygwin ; then
    JPASS_PATH=`cygpath "$JPASS_HOME"`
else
    JPASS_PATH="$JPASS_HOME"
fi

echo "Install application to /opt/jpass..."
if [ ! -d "/opt" ]; then
  echo "ERROR: /opt folder does not exists"
  exit
fi

mkdir -p "/opt/jpass"
cp -R "$JPASS_PATH" "/opt/jpass"
chmod +x "/opt/jpass/jpass.sh"

echo "Add JPass desktop entry..."
if [ ! -d "/usr/share/applications" ]; then
  echo "ERROR: Could not create desktop entry: /usr/share/applications is missing"
  exit
fi

cp "$JPASS_PATH/jpass.desktop" "/usr/share/applications"

