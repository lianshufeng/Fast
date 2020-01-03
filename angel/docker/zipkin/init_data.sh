#!/bin/bash
cd `dirname $0`;

#刷新环境变量
source ".env"

#生成脚本
echo "" > init_data.sql
echo "CREATE DATABASE $MYSQL_DB CHARACTER SET 'utf8';" >> init_data.sql
echo "use $MYSQL_DB" >> init_data.sql
cat init_zipkin.sql >> init_data.sql


mysql -u$MYSQL_USER -p$MYSQL_PASS < init_data.sql