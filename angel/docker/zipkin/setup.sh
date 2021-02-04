#!/bin/bash
cd `dirname $0`;

#安装以来
yum install -y dos2unix
#转换编码
dos2unix * .env

#加载环境变量
source ./.env

#拷贝数据库初始化文件
mkdir -p ${store}
docker rm -f zipkin_mysql
docker run --name zipkin_mysql -d openzipkin/zipkin-mysql
docker cp zipkin_mysql:/mysql/data ${store}
docker rm -f zipkin_mysql
chmod -R 777 ${store}


#重启
docker-compose down ; docker-compose up -d



