#!/bin/sh

echo "[INFO][filebeat-8.0.1] shutdown filebeat."
CURRENTDIR=$(cd `dirname $0`;pwd)

pkill -f filebeat-watcher

if [ "$?" -ne 0 ]; then
  echo "[INFO][filebeat-8.0.1] shutdown failed."
  exit 1
fi
echo "[INFO][filebeat-8.0.1] shutdown filebeat success."
exit 0
