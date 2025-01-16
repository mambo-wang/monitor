#!/bin/sh

echo "[INFO][filebeat-8.0.1] start startup filebeat."
CURRENTDIR=$(cd `dirname $0`;pwd)

nohup ${CURRENTDIR}/filebeat-8.0.1-linux-x86_64/filebeat -e -c ${CURRENTDIR}/filebeat-8.0.1-linux-x86_64/filebeat-watcher.yml > ${CURRENTDIR}/filebeat-nohup.log 2>&1 &

if [ "$?" -ne 0 ]; then
  echo "[INFO][filebeat-8.0.1] startup failed."
  exit 1
fi

echo "[INFO][filebeat-8.0.1] filebeat startup success."
exit 0
