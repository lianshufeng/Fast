#!/bin/bash

#安装环境
sudo yum install -y dos2unix

#防火墙
sudo firewall-cmd --add-port=27017/tcp --permanent
sudo firewall-cmd --add-port=27018/tcp --permanent
sudo firewall-cmd --add-port=27019/tcp --permanent
sudo firewall-cmd --add-port=27020/tcp --permanent
firewall-cmd --reload 


#载入环境变量
dos2unix * .env
cd `dirname $0`
source ${PWD}/.env


#启动服务
docker-compose down ; docker-compose up -d


# 延迟初始化集群
#sleep 5
#docker exec -it mongo1 /bin/bash init_mongodb.sh

#连接客户端
#docker exec -it mongo1 /bin/bash /opt/cmd/client.sh


