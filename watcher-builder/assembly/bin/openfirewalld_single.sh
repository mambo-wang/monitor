#!/bin/bash


echo "===== start to open firewalld ===="
systemctl start firewalld.service
systemctl enable firewalld.service
firewall-cmd --zone=public --add-port=80/tcp --permanent
firewall-cmd --reload
echo "==== open firewalld success ===="