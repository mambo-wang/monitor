#!/bin/bash

# Find java
if type java | grep -q 'java' ; then
    echo Found java executable in PATH >/dev/null
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    echo Found java executable in JAVA_HOME
    _java="$JAVA_HOME/bin/java"
else
    echo "[ERROR] Java is not installed. Please install JAVA 1.8 or upper version"
    exit 1
fi

if [ ! -z "$_java" ]; then
    version=$(java -version 2>&1 | sed -n ';s/.* version "\(.*\)\.\(.*\)\..*"/\1\2/p;')
    if [ "$version" -ge 18 ]; then
        echo "[INFO] Java version is $version, check OK, greater than 1.8"
    else
        echo "[ERROR] Java version is $version, below 1.8"
        exit 1
    fi
else
    exit 1
fi

# Resolve links - $0 may be a softlink
WATCHER_BIN="$0"

while [ -h "$WATCHER_BIN" ]; do
  ls=`ls -ld "$WATCHER_BIN"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    WATCHER_BIN="$link"
  else
    WATCHER_BIN=`dirname "$WATCHER_BIN"`/"$link"
  fi
done

# Get standard environment variables
BIN_DIR=`dirname "$WATCHER_BIN"`

# Get watcher root
[ -z "$WATCHER_ROOT" ] && WATCHER_ROOT=`cd "$BIN_DIR/.." >/dev/null;pwd`;

# Get watcher lib
[ -z "$WATCHER_LIBDIR" ] && WATCHER_LIBDIR=`cd "$WATCHER_ROOT/lib" >/dev/null;pwd`

# Get watcher plugin
[ -z "$PLUGIN_DIR" ] && PLUGIN_DIR=`cd "$WATCHER_ROOT/plugins" >/dev/null;pwd`

# Get watcher conf
[ -z "$CONF_DIR" ] && CONF_DIR=`cd "$WATCHER_ROOT/conf/watcher" >/dev/null;pwd`

echo -e "mkdir log path start" | tee -a ${LOGFILE}
mkdir -p $WATCHER_ROOT/logs/gc
mkdir -p $WATCHER_ROOT/logs/dump
echo -e "mkdir log path success: $(date "+%Y-%m-%d %H:%M:%S")" | tee -a ${LOGFILE}

ERROR_OUT="$WATCHER_ROOT/logs/error.out"
PROCESS_FLAG="watcher-agent-process-flag:${WATCHER_ROOT}"
JAVA_MEM_OPTS="-Xmx960m -Xms960m"
JAVA_GC_OPTS="$JAVA_GC_OPTS
-XX:+PrintHeapAtGC
-XX:+UseGCLogFileRotation
-XX:NumberOfGCLogFiles=5
-XX:GCLogFileSize=10M"
JAVA_GC_OPTS="$JAVA_GC_OPTS -Xloggc:$WATCHER_ROOT/logs/gc/oad-watcher_gc%t.log"
JAVA_GC_OPTS="$JAVA_GC_OPTS -XX:+PrintGCDateStamps -XX:+PrintGCDetails"
JAVA_GC_OPTS="$JAVA_GC_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$WATCHER_ROOT/logs/dump/oad-watcher_pid%p.hprof"
JAVA_GC_OPTS="$JAVA_GC_OPTS -XX:ErrorFile=$WATCHER_ROOT/logs/gc/oad-watcher_err_pid%p.log"
JAVA_OPTS="$JAVA_OPTS $JAVA_MEM_OPTS $JAVA_GC_OPTS"
# current directory
CURRENTDIR=$(cd `dirname $0`;pwd)
chmod +x ${CURRENTDIR}/*.sh
cd ${CURRENTDIR}
#--Duser.timezone=GMT+08 时区
nohup $_java -Dloader.path=$WATCHER_LIBDIR,$PLUGIN_DIR,$CONF_DIR -Dspring.profiles.active="prod"  -Dspring.config.location="$CONF_DIR/" -Dwatcher.home="$WATCHER_ROOT" -Djdk.crypto.KeyAgreement.legacyKDF=true -noverify -Xdebug -Xrunjdwp:transport=dt_socket,address=18888,server=y,suspend=n -jar $JAVA_OPTS ${BIN_DIR}/agent.jar $PROCESS_FLAG 1>/dev/null 2>$ERROR_OUT &

echo "service watcher-agent started!"
