#!/bin/bash
WATCHER_HOME=`cat /etc/watcher_home`
VDI_INSTALL_LOG="$WATCHER_HOME/logs/startup.log"
LOCAL_IP_ONE=$1
LOCAL_IP_TWO=$2
echo "LOCAL_IP_ONE: " $LOCAL_IP_ONE
echo "LOCAL_IP_TWO: " $LOCAL_IP_TWO
echo "===== start to open firewalld ===="
systemctl start firewalld.service
systemctl enable firewalld.service

firewall-cmd --direct --permanent --add-rule ipv4 filter INPUT 0 --destination 224.0.0.18 --protocol vrrp -j ACCEPT >/dev/null 2>&1
firewall-cmd --direct --permanent --add-rule ipv4 filter OUTPUT 0 --destination 224.0.0.18 --protocol vrrp -j ACCEPT >/dev/null 2>&1

firewall-cmd --permanent --add-rich-rule="rule family=ipv4 source address=$LOCAL_IP_ONE accept"
firewall-cmd --permanent --add-rich-rule="rule family=ipv4 source address=$LOCAL_IP_TWO accept"

firewall-cmd --zone=public --add-port=80/tcp --permanent

firewall-cmd --reload
echo "==== open firewalld success ===="