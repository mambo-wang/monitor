#!/bin/bash
WATCHER_BIN="$0"

while [ -h "$WATCHER_BIN" ]; do
  ls=`ls -ld "$WATCHER_BIN"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    WATCHER_BIN="$link"
  else
    WATCHER_BIN=`dirname "$WATCHER_BIN"`/"$link"
  fi
done

# Get standard environment variables
BIN_DIR=`dirname "$WATCHER_BIN"`

# Get watcher root
[ -z "$WATCHER_ROOT" ] && WATCHER_ROOT=`cd "$BIN_DIR/.." >/dev/null;pwd`;

PROCESS_FLAG="watcher-agent-process-flag:${WATCHER_ROOT}"

pkill -f $PROCESS_FLAG
sleep 5s
echo 'service watcher-agent stopped!'

