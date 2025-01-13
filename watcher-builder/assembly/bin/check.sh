#!/bin/bash

WATCHER_HOME=`cat /etc/watcher_home`
echo "check cron is start, $(date "+%Y-%m-%d %H:%M:%S")" > ${WATCHER_HOME}/logs/check.log
STATUS=`ps -ef | grep mongo`
if [[ "$STATUS" =~ mongodb.conf ]]; then
  echo "mongodb is running, $(date "+%Y-%m-%d %H:%M:%S")" >> ${WATCHER_HOME}/logs/check.log
else
  echo "mongodb is shutdown, repair it ! $(date "+%Y-%m-%d %H:%M:%S")" >> ${WATCHER_HOME}/logs/check.log
  bash ${WATCHER_HOME}/components/mongodb/startup.sh
fi

AGENT_STATUS=`ps -ef | grep agent`
if [[ "$AGENT_STATUS" =~ watcher-agent-process-flag ]]; then
  echo "agent is running, $(date "+%Y-%m-%d %H:%M:%S")" >> ${WATCHER_HOME}/logs/check.log
else
  echo "agent is shutdown, repair it ! $(date "+%Y-%m-%d %H:%M:%S")" >> ${WATCHER_HOME}/logs/check.log
  bash ${WATCHER_HOME}/bin/startup.sh
fi
exit 0






