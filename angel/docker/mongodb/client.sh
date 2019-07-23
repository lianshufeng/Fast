#!/bin/bash

#载入环境变量
cd `dirname $0`
source ${PWD}/.env

#连接客户端
mongo --host MongoSets/${VmHost}:27017,${VmHost}:27018,${VmHost}:27019,${VmHost}:27020 admin -u ${MongoInitRootUserName} -p ${MongoInitRootPassWord}