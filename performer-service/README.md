# Client V2 Example

## Overview

This example demonstrates how to use the client V2 to interact with the ClickHouse server.

## How to Run

Apache Maven or IDE with Maven support is required to run this example.

First we need to compile the example :
```shell
mvn clean compile
```

To run:
```shell
 mvn exec:java -Dexec.mainClass="com.clickhouse.examples.client_v2.Main"
```

Addition options can be passed to the application:
- `-DchEndpoint` - Endpoint to connect in the format of URL (default: http://localhost:8123/)
- `-DchUser` - ClickHouse user name (default: default)
- `-DchPassword` - ClickHouse user password (default: empty)
- `-DchDatabase` - ClickHouse database name (default: default)

ui
http://192.168.233.10:8123/play?user=default#c2VsZWN0ICogZnJvbSBoYWNrZXJfbmV3c19hcnRpY2xlczs=

安装clickhouse的时候把配置文件里的倒数第5行的AmbientCapabilities那一行去掉才行