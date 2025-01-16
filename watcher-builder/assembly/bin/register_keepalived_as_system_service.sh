#!/bin/bash

WATCHER_BIN="$0"
BIN_DIR=`dirname "$WATCHER_BIN"`
[ -z "$WATCHER_HOME" ] && WATCHER_HOME=`cd "$BIN_DIR/.." >/dev/null;pwd`;
echo "[INFO] watcher home is ${WATCHER_HOME}"
echo "${WATCHER_HOME}" > /etc/watcher_home

chmod -R +x ${WATCHER_HOME}

sed -i "s#WATCHER_HOME#${WATCHER_HOME}#g" ${WATCHER_HOME}/conf/keepalived.service

cp -f ${WATCHER_HOME}/conf/keepalived.service /etc/systemd/system

systemctl daemon-reload

systemctl enable keepalived.service >/dev/null 2>&1
systemctl start keepalived.service >/dev/null 2>&1

echo "[INFO] register keepalived as system services success !"




