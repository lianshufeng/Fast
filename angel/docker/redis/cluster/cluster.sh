#!/bin/bash

cd `dirname $0`;

#执行初始化脚本
source .env
source /etc/profile

#集群必须使用ip
redis-cli --cluster create --cluster-yes --cluster-replicas 1 $host:6379 $host:6380 $host:6381 $host:6382 $host:6383 $host:6384 
