# ShowTime监控系统

本系统是一个全方位监控的组件，尤其适合于新华三云基产品线的监控。Show代表展示，Time代表监控的对象为日志/指标等时间序列数据。

## 功能集

- 日志采集、全文检索
- 指标采集、监控报表
- 爬虫/知识库

## 技术栈

- SpringBoot  代码框架
- Quartz  定时任务
- MongoDB  数据持久化存储
- Elasticsearch  全文检索
- Kafka  消息引擎
- Filebeat  日志采集
- Kibana  ES可视化
- ClickHouse(待实现) 列数据库
- TDengine或InfluxDB(待实现)  时序数据库
- Promethus （待实现）
- Grafana （待实现）开源报表
- Logstash(待实现)

## 代码架构

### agent单体多模块服务
- watcher-agent：程序主入口/资源管理/定时指标采集
- watcher-log：日志收集策略下发/日志实时收集/全文检索
- watcher-builder：打包, 注意此代码库已将各个依赖的安装包删除，因此部署时不会自动安装jdk mongo kafka es 等依赖
- watcher-workspace/cas/uis/onestor：各个产品的指标收集/日志格式解析/告警上报
- watcher-sdk：公共配置/DTO/工具类
