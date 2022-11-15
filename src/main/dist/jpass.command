#!/bin/sh

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

# detect mac
macosx=false;
case "`uname -s`" in
    Darwin*)
        macosx=true
        ;;
esac

if $macosx ; then
    LAF_OPTS="-Dapple.laf.useScreenMenuBar=true"
else
    LAF_OPTS="-Dapple.laf.useScreenMenuBar=false"
fi

# execute jpass
exec "$JAVACMD" "$LAF_OPTS" -jar "$JPASS_HOME/jpass-1.0.2-SNAPSHOT.jar" "$@"
