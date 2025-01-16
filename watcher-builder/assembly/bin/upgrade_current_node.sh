#!/bin/bash

WATCHER_HOME=`cat /etc/watcher_home`
LOGFILE="$WATCHER_HOME/logs/upgrade.log"
# current directory
CURRENTDIR=$(cd `dirname $0`;pwd)

if [ -f "/etc/watcher_home" ]; then
    echo "[INFO] go upgrade..." | tee -a ${LOGFILE}
else
    echo "[INFO] please install..." | tee -a ${LOGFILE}
    exit 1
fi

WATCHER_HOME=`cat /etc/watcher_home`
echo "[INFO] watcher home is ${WATCHER_HOME}"

echo -e "mkdir log path start" | tee -a ${LOGFILE}
mkdir -p $WATCHER_HOME/logs/gc
mkdir -p $WATCHER_HOME/logs/dump
echo -e "mkdir log path success: $(date "+%Y-%m-%d %H:%M:%S")" | tee -a ${LOGFILE}


\cp -af ${CURRENTDIR}/../bin/ ${WATCHER_HOME}
\cp -af ${CURRENTDIR}/../lib/ ${WATCHER_HOME}
\cp -af ${CURRENTDIR}/../plugins/ ${WATCHER_HOME}
# 暂不替换application-prod.properties，以免覆盖集群相关参数
\cp -af ${CURRENTDIR}/../conf/application.properties ${WATCHER_HOME}/conf
\cp -af ${CURRENTDIR}/../conf/logback-prod.xml ${WATCHER_HOME}/conf
\cp -af ${CURRENTDIR}/../html ${WATCHER_HOME}/components/nginx/nginx
\cp -af ${CURRENTDIR}/../inspect ${WATCHER_HOME}/components

#暂不考虑升级基础组件
bash ${WATCHER_HOME}/bin/restart.sh
echo "[INFO] current node upgrade success!" | tee -a ${LOGFILE}








