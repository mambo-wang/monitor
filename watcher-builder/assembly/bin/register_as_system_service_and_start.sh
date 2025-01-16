#!/bin/bash

WATCHER_BIN="$0"
BIN_DIR=`dirname "$WATCHER_BIN"`
[ -z "$WATCHER_HOME" ] && WATCHER_HOME=`cd "$BIN_DIR/.." >/dev/null;pwd`;
echo "[INFO] watcher home is ${WATCHER_HOME}"
echo "${WATCHER_HOME}" > /etc/watcher_home

chmod -R +x ${WATCHER_HOME}

sed -i "s#WATCHER_HOME#${WATCHER_HOME}#g" ${WATCHER_HOME}/conf/agent.service
sed -i "s#WATCHER_HOME#${WATCHER_HOME}#g" ${WATCHER_HOME}/conf/kafka.service
sed -i "s#WATCHER_HOME#${WATCHER_HOME}#g" ${WATCHER_HOME}/conf/mongodb.service
sed -i "s#WATCHER_HOME#${WATCHER_HOME}#g" ${WATCHER_HOME}/conf/nginx.service
sed -i "s#WATCHER_HOME#${WATCHER_HOME}#g" ${WATCHER_HOME}/conf/zookeeper.service

cp -f ${WATCHER_HOME}/conf/agent.service /etc/systemd/system
cp -f ${WATCHER_HOME}/conf/kafka.service /etc/systemd/system
cp -f ${WATCHER_HOME}/conf/mongodb.service /etc/systemd/system
cp -f ${WATCHER_HOME}/conf/nginx.service /etc/systemd/system
cp -f ${WATCHER_HOME}/conf/zookeeper.service /etc/systemd/system

systemctl daemon-reload

systemctl enable zookeeper.service >/dev/null 2>&1
systemctl start zookeeper.service >/dev/null 2>&1

systemctl enable kafka.service >/dev/null 2>&1
systemctl start kafka.service >/dev/null 2>&1

systemctl enable mongodb.service >/dev/null 2>&1
systemctl start mongodb.service >/dev/null 2>&1

systemctl enable nginx.service >/dev/null 2>&1
systemctl start nginx.service >/dev/null 2>&1

systemctl enable agent.service >/dev/null 2>&1
systemctl start agent.service >/dev/null 2>&1

echo "[INFO] register system services success !"




