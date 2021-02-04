#!/bin/bash

cd `dirname $0`;
mkdir -p conf

#安装依赖
yum install -y dos2unix wget curl unzip ;

dos2unix $(pwd)/.env
source  $(pwd)/.env


#开启防火墙
firewall-cmd --add-port=6379/tcp --permanent
firewall-cmd --add-port=16379/tcp --permanent

firewall-cmd --add-port=6380/tcp --permanent 
firewall-cmd --add-port=16380/tcp --permanent 

firewall-cmd --add-port=6381/tcp --permanent 
firewall-cmd --add-port=16381/tcp --permanent 

firewall-cmd --add-port=6382/tcp --permanent 
firewall-cmd --add-port=16382/tcp --permanent 

firewall-cmd --add-port=6383/tcp --permanent 
firewall-cmd --add-port=16383/tcp --permanent 

firewall-cmd --add-port=6384/tcp --permanent 
firewall-cmd --add-port=16384/tcp --permanent 

firewall-cmd --reload


#更新 redis 配置文件
function updateRedis(){
	
	#下载配置
	wget https://raw.githubusercontent.com/antirez/redis/5.0/redis.conf -O $(pwd)/redis.conf  

	#启用集群模式
	sed -i 's/# cluster-enabled/cluster-enabled/g' $(pwd)/redis.conf

	sed -i 's/# cluster-config-file/cluster-config-file/g' $(pwd)/redis.conf
	#超时时间
	sed -i 's/# cluster-node-timeout/cluster-node-timeout/g' $(pwd)/redis.conf

	#仅追加
	#sed -i 's/appendonly no/appendonly yes/g' $(pwd)/redis.conf
	
	#后台运行，docker里不能后台启动
	#sed -i 's/daemonize no/daemonize yes/g' $(pwd)/redis.conf
	#非保护模式
	sed -i 's/protected-mode yes/protected-mode no/g' $(pwd)/redis.conf

	sed -i 's/#pidfile/pidfile/g' $(pwd)/redis.conf

	sed -i "s/bind 127.0.0.1/bind 0.0.0.0/g" $(pwd)/redis.conf
}


#拷贝配置文件
function copyRedisConf(){
	cat $(pwd)/redis.conf > $(pwd)/conf/redis-$1.conf
	#修改端口
	sed -i "s/port 6379/port $1/g" $(pwd)/conf/redis-$1.conf
	
}



#执行更新reids 配置文件， 不用每次都修改配置
updateRedis
copyRedisConf 6379
copyRedisConf 6380
copyRedisConf 6381
copyRedisConf 6382
copyRedisConf 6383
copyRedisConf 6384
rm -rf $(pwd)/redis.conf



echo "input : "
echo "docker-compose up -d"
echo "docker exec -it redis1 bash /opt/cluster.sh"









