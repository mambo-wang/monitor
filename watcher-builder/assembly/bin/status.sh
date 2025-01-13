#!/bin/sh

STATUS=`ps -ef | grep agent`
if [[ "$STATUS" =~ watcher-agent-process-flag ]]; then
  echo "watcher is running"
else
  echo "watcher is shutdown"
fi
exit 0
