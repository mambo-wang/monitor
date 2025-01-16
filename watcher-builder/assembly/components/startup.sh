#!/bin/sh

WATCHER_HOME=`cat /etc/watcher_home`
VDI_INSTALL_LOG="$WATCHER_HOME/logs/startup.log"

INSTALL_TYPE=$1
echo "[INFO][mongodb-4.2.20] this node is $INSTALL_TYPE" | tee -a $VDI_INSTALL_LOG
if [ ! -n "$INSTALL_TYPE" ]; then
  INSTALL_TYPE=master
fi

echo "[INFO][dependencies] startup." | tee -a $VDI_INSTALL_LOG
CURRENTDIR=$(cd `dirname $0`;pwd)

HOSTS_CONTENT=`cat /etc/hosts`
if [[ "$HOSTS_CONTENT" =~ watcher001 ]]; then
  echo "[INFO][hosts] already config"
else
  sed -i '$a 127.0.0.1 watcher001 watcher001' /etc/hosts
  systemctl stop firewalld.service
  systemctl disable firewalld.service
fi

bash ${CURRENTDIR}/jdk/install.sh
bash ${CURRENTDIR}/sshpass/install.sh
bash ${CURRENTDIR}/nginx/startup.sh
bash ${CURRENTDIR}/mongodb/startup.sh $INSTALL_TYPE
bash ${CURRENTDIR}/zookeeper/startup.sh
bash ${CURRENTDIR}/kafka/startup.sh

if [ "$?" -ne 0 ]; then
  echo "[INFO][dependencies] startup failed: $(date "+%Y-%m-%d %H:%M:%S")" | tee -a $VDI_INSTALL_LOG
  exit 1
fi
echo "[INFO][dependencies] startup success." | tee -a $VDI_INSTALL_LOG
echo "${CURRENTDIR}/../" > /etc/watcher_home

exit 0
