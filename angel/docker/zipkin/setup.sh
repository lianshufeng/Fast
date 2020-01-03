#!/bin/bash
cd `dirname $0`;

#安装以来
yum install -y dos2unix
#转换编码
dos2unix * .env

#重启
docker-compose down ; docker-compose up -d

#初始化脚本
#docker exec -it zipkin_mysql /bin/bash /tmp/data/init_data.sh



