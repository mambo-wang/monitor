#!/bin/sh

STATUS=`ps -ef | grep filebeat`
if [[ "$STATUS" =~ filebeat-watcher ]]; then
  echo "filebeat is running"
else
  echo "filebeat is shutdown"
fi
exit 0
