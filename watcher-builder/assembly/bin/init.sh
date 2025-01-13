#!/bin/bash

WATCHER_HOME_FILE="/etc/watcher_home"
if [ -f ${WATCHER_HOME_FILE} ];then
  echo "[INFO] watcher is already initialized."
  exit 0
fi

WATCHER_BIN="$0"
BIN_DIR=`dirname "$WATCHER_BIN"`
[ -z "$WATCHER_HOME" ] && WATCHER_HOME=`cd "$BIN_DIR/.." >/dev/null;pwd`;
echo "[INFO] watcher home is ${WATCHER_HOME}"
echo "${WATCHER_HOME}" > /etc/watcher_home

echo "[INFO] watcher add crontab check is ${WATCHER_HOME}/bin/check.sh"
echo "*/1 * * * * sh ${WATCHER_HOME}/bin/check.sh" >> /var/spool/cron/root
echo "KexAlgorithms curve25519-sha256,curve25519-sha256@libssh.org,ecdh-sha2-nistp256,ecdh-sha2-nistp384,ecdh-sha2-nistp521" >> /etc/ssh/sshd_config
systemctl restart sshd

systemctl stop firewalld.service
exit 0






