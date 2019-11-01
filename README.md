# elasticsearch-house
基于Elasticsearch的租房搜索系统

## 概述
### 系统架构
主要是以MySQL作为基础数据存储，结合ES实现站内搜索引擎。
![img/系统架构.png](img/系统架构.png)

### 涉及技术
- 核心搜索技术：
SpringBoot + Elasticsearch（方便实现站内搜索引擎）
- 站内搜索引擎实现：
Elasticsearch + MySQL + Kafka
- 地图搜索：
Elasticsearch + 百度地图
- 负载均衡，安全加固技术（监控报警）：
Elasticsearch + Nginx
- 日志数据分析技术：
ELK：Elasticsearch + Logstash + Kibana
- 数据库：
MySQL（其事务特性可做稳定的数据存储） + Spring Data JPA
- 前端框架：
thymeleaf（模版技术） + Bootstrap（前端开发框架） + JQuery
- 项目安全框架：
Spring Security（权限控制、自定义安全策略）
- 图片上传：
七牛云 + 百度开源框架webUpload
- 免注册登录：
阿里短信（基于阿里云通讯实现）

## 项目设计

### 数据库设计
#### ER图
实体-联系图(Entity Relationship Diagram)

- 用户ER图：
![img/用户.png](img/用户.png)

- 房源信息ER图
![img/房源.png](img/房源.png)
#### 数据库表对象模型
![img/数据库表模型.png](img/数据库表模型.png)


#### 结构
- 数据+结构转储sql
见文件：db/elasticsearch_house.sql
