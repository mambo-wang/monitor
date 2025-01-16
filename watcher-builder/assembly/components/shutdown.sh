#!/bin/sh

WATCHER_HOME=`cat /etc/watcher_home`
VDI_INSTALL_LOG="$WATCHER_HOME/logs/startup.log"

echo "[INFO][dependencies] shutdown." | tee -a $VDI_INSTALL_LOG
CURRENTDIR=$(cd `dirname $0`;pwd)

bash ${CURRENTDIR}/nginx/shutdown.sh
bash ${CURRENTDIR}/mongodb/shutdown.sh
bash ${CURRENTDIR}/zookeeper/shutdown.sh
bash ${CURRENTDIR}/kafka/shutdown.sh

if [ "$?" -ne 0 ]; then
  echo "[INFO][dependencies] shutdown failed: $(date "+%Y-%m-%d %H:%M:%S")" | tee -a $VDI_INSTALL_LOG
  exit 1
fi
echo "[INFO][dependencies] shutdown success." | tee -a $VDI_INSTALL_LOG
exit 0
