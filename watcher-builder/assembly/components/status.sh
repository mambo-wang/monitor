#!/bin/sh

CURRENTDIR=$(cd `dirname $0`;pwd)
bash ${CURRENTDIR}/nginx/status.sh
bash ${CURRENTDIR}/mongodb/status.sh
bash ${CURRENTDIR}/zookeeper/status.sh
bash ${CURRENTDIR}/kafka/status.sh
exit 0
