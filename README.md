# Fast
Spring cloud and boot Framework

## 特点：
### 高可用
	spring cloud 注册与发现服务
### 可伸缩
	spring cloud 注册与发现服务
### 高内聚低耦合
	spring 
### 快速开发
	#### 多环境选择
	idea 默认提供 开发、测试、生产环境快速切换(dev、test、pord)
	#### 内聚模块
	多个component(组件)，遵循spring boot的约定配置，拎包入住的规范
	#### 应用中心
	简化配置，拎包即可使用
	#### 用户中心
	简化用户鉴权，含常用api
	#### 网关服务
	网络入口，统一身份识别
	#### 单机应用
	脱离应用中心，可独立运行，适合新产品试错。
	



## 环境
JDK：1.8+ , IDE：idea , 推荐插件 ( Spring Assistant , Maven Helper , YAML/Ansible support)

## 编译与部署
war : idea选择环境一键打包,拷贝到tomcat运行(注意：本地端口与上下文)<br/>jar : 需要修改pom配置

## 依赖服务
ElasticSearch -> MQ ( kafka、ZooKeeper ) -> Git(配置中心) 

## 启动顺序
应用中心 -> 配置中心 -> 用户中心 -> [自定义模块] -> 监控 -> 网关 







## 待完成... 