#!/bin/sh

# check if java executable exists
if [ -d "$JAVA_HOME" -a -x "$JAVA_HOME/bin/java" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD=java
fi

$JAVACMD -version >/dev/null 2>&1
if [ "$?" != "0" ]; then
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

# execute jpass
exec "$JAVACMD" -jar "$JPASS_PATH/jpass-0.1.27-RELEASE.jar" "$@"
if [ "$?" != "0" ]; then
  echo "Could not execute JPass (exit: $?)"
  exit
fi
