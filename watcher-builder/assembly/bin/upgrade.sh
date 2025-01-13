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

nodes=`curl -X GET -H  "Accept:*/*" -H  "Request-Origion:Knife4j" -H  "Content-Type:application/x-www-form-urlencoded" "http://127.0.0.1:8888/watcher/deploy/host"`
echo "[INFO] query cluster info success"  | tee -a ${LOGFILE}

cd ${CURRENTDIR}/../
UPGRADE_PATH=$(cd `dirname $0`;pwd)
cd ${CURRENTDIR}/../../
UPGRADE_FATHER_PATH=$(cd `dirname $0`;pwd)
UPGRADE_SCRIPT_PATH=${CURRENTDIR}/upgrade_current_node.sh
echo "[INFO] upgrade path is $UPGRADE_PATH, father path is $UPGRADE_FATHER_PATH, upgrade script path is $UPGRADE_SCRIPT_PATH"  | tee -a ${LOGFILE}

array=($nodes)
for node in "${array[@]}";do
    OLD_IFS="$IFS"
    IFS=","
    infos=($node)
    IFS="$OLD_IFS"
    echo "[INFO] start to upgrade node ${infos[0]}"  | tee -a ${LOGFILE}
    sshpass -p ${infos[2]} scp -r -P 22 $UPGRADE_PATH ${infos[1]}@${infos[0]}:$UPGRADE_FATHER_PATH  >/dev/null 2>&1
    sshpass -p ${infos[2]} ssh -t -p 22 ${infos[1]}@${infos[0]} "bash $UPGRADE_SCRIPT_PATH"  >/dev/null 2>&1
    echo "[INFO] node ${infos[0]} upgrade success!"  | tee -a ${LOGFILE}
done

echo "[INFO] start to upgrade current node"  | tee -a ${LOGFILE}
bash $UPGRADE_SCRIPT_PATH  >/dev/null 2>&1
echo "[INFO] current node upgrade success!" | tee -a ${LOGFILE}
echo "[INFO] all nodes upgrade success!"  | tee -a ${LOGFILE}
exit 0








