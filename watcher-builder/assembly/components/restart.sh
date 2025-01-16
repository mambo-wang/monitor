#!/bin/sh

WATCHER_HOME=`cat /etc/watcher_home`
VDI_INSTALL_LOG="$WATCHER_HOME/logs/startup.log"

echo "[INFO][dependencies] restart." | tee -a $VDI_INSTALL_LOG
CURRENTDIR=$(cd `dirname $0`;pwd)

bash ${CURRENTDIR}/nginx/restart.sh
bash ${CURRENTDIR}/mongodb/restart.sh
bash ${CURRENTDIR}/zookeeper/restart.sh
bash ${CURRENTDIR}/kafka/restart.sh

if [ "$?" -ne 0 ]; then
  echo "[INFO][dependencies] restart failed: $(date "+%Y-%m-%d %H:%M:%S")" | tee -a $VDI_INSTALL_LOG
  exit 1
fi
echo "[INFO][dependencies] restart success." | tee -a $VDI_INSTALL_LOG
exit 0
