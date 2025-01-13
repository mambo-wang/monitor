#!/bin/bash

# current directory
CURRENTDIR=$(cd `dirname $0`;pwd)

bash ${CURRENTDIR}/shutdown.sh
sleep 5s
bash ${CURRENTDIR}/startup.sh
