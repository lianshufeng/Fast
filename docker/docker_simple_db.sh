#需要使用的简易环境

docker pull lianshufeng/kafka
docker pull lianshufeng/mongodb
docker pull redis
docker pull openzipkin/zipkin

VmHost=192.168.145.129

docker rm -f redis
docker rm -f kafka
docker rm -f mongo0 mongo1 mongo2 mongo3
rm -rf /opt/redis/
rm -rf /opt/kafka/
rm -rf /opt/mongo/




#########kafka
sudo firewall-cmd --add-port=2181/tcp --permanent 
sudo firewall-cmd --add-port=9092/tcp --permanent 
firewall-cmd --reload 
docker run --restart always -d --name kafka \
-v /opt/kafka/config:/opt/kafka/config \
-v /opt/kafka/logs:/opt/kafka/logs \
-v /opt/kafka/kafka_logs:/tmp/kafka-logs \
-e KAFKA_LISTENERS="$VmHost:9092" -p 2181:2181 -p 9092:9092 lianshufeng/kafka




######Mongodb
sudo firewall-cmd --add-port=27017/tcp --permanent 
sudo firewall-cmd --add-port=27018/tcp --permanent 
sudo firewall-cmd --add-port=27019/tcp --permanent
sudo firewall-cmd --add-port=27020/tcp --permanent
firewall-cmd --reload 
for((i=0;i<4;i++));
do 
  let port=27017+$i
  docker run --name mongo$i --restart always --privileged=true -p $port:27017 -v /opt/mongo/store/mongo$i:/opt/mongo/store -v /etc/localtime:/etc/localtime:ro -e ReplSetInitiate="$VmHost:27017,$VmHost:27018,$VmHost:27019" -e ReplSetArbiter="$VmHost:27020" -e MongoInitRootUserName="admin" -e MongoInitRootPassWord="687mongo2018" -d lianshufeng/mongodb 
done

#初始化
docker exec -it mongo0 /bin/bash init_mongodb.sh

#客户端登陆
docker exec -it mongo0 /bin/bash
VmHost=192.168.145.129
mongo --host MongoSets/$VmHost:27017,$VmHost:27018,$VmHost:27019,$VmHost:27020 admin -u admin -p 687mongo2018



######redis
sudo firewall-cmd --add-port=6379/tcp --permanent
firewall-cmd --reload 
docker run --name redis --restart always -p 6379:6379 -v /opt/redis:/data -d redis redis-server



######zipkin
sudo firewall-cmd --add-port=9411/tcp --permanent
firewall-cmd --reload
docker run -d -p 9411:9411 openzipkin/zipkin