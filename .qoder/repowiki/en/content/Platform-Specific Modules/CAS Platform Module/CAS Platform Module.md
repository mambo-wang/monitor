# CAS Platform Module

<cite>
**Referenced Files in This Document**
- [CasCasServerLogPatternHandler.java](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasCasServerLogPatternHandler.java)
- [CasCatalinaLogPatternHandler.java](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasCatalinaLogPatternHandler.java)
- [CasLibvirtLogPatternHandler.java](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasLibvirtLogPatternHandler.java)
- [CasQemuLogPatternHandler.java](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasQemuLogPatternHandler.java)
- [CasHostHandler.java](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasHostHandler.java)
- [CasSSHService.java](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/ssh/CasSSHService.java)
- [HostCpuUsageCollector.java](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/severPerformanceMonitor/HostCpuUsageCollector.java)
- [HostPerformanceCollector.java](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/report/HostPerformanceCollector.java)
- [CasLogCollector.java](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/log/CasLogCollector.java)
- [CasTestConnectionApi.java](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasTestConnectionApi.java)
- [CasHostOperateCommandExecutor.java](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/operate/CasHostOperateCommandExecutor.java)
- [CasUriConstants.java](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Project Structure](#project-structure)
3. [Core Components](#core-components)
4. [Architecture Overview](#architecture-overview)
5. [Detailed Component Analysis](#detailed-component-analysis)
6. [Dependency Analysis](#dependency-analysis)
7. [Performance Considerations](#performance-considerations)
8. [Troubleshooting Guide](#troubleshooting-guide)
9. [Conclusion](#conclusion)
10. [Appendices](#appendices)

## Introduction
This document describes the CAS (Cloud Application Services) platform monitoring module. It covers the metrics collection capabilities for cluster basics, host performance, VM statistics, and storage information, along with the 20+ specialized collectors for CPU usage, memory consumption, disk I/O, network throughput, and virtual machine operations. It also documents the log pattern handlers for parsing CAS-specific log formats (server logs, Catalina logs, libvirt logs, QEMU logs), SSH authentication mechanisms, host operation command executors for VM management, and integration with CAS REST APIs. Guidance is included for configuration, metric interpretation, and troubleshooting CAS-specific monitoring challenges.

## Project Structure
The CAS monitoring module resides under watcher-cas and integrates with watcher-sdk for shared REST clients, DTOs, and URI constants. Key areas:
- Log parsing: dedicated handlers for CAS server, Catalina, libvirt, and QEMU logs
- Metrics collection: cluster/host/VM collectors for CPU, memory, disk I/O, network, and storage
- Operations: host and VM operation executors via CAS REST APIs
- Authentication: SSH enablement and verification against CAS endpoints
- Logging: batch log collection and download from CAS

```mermaid
graph TB
subgraph "CAS Module"
L1["CasCasServerLogPatternHandler"]
L2["CasCatalinaLogPatternHandler"]
L3["CasLibvirtLogPatternHandler"]
L4["CasQemuLogPatternHandler"]
M1["HostCpuUsageCollector"]
M2["HostPerformanceCollector"]
O1["CasHostOperateCommandExecutor"]
S1["CasSSHService"]
H1["CasHostHandler"]
LOG["CasLogCollector"]
T1["CasTestConnectionApi"]
end
subgraph "SDK"
U1["CasUriConstants"]
end
L1 --> U1
L2 --> U1
L3 --> U1
L4 --> U1
M1 --> U1
M2 --> U1
O1 --> U1
S1 --> U1
H1 --> U1
LOG --> U1
T1 --> U1
```

**Diagram sources**
- [CasCasServerLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasCasServerLogPatternHandler.java#L1-L52)
- [CasCatalinaLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasCatalinaLogPatternHandler.java#L1-L52)
- [CasLibvirtLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasLibvirtLogPatternHandler.java#L1-L52)
- [CasQemuLogPatternHandler.java:1-50](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasQemuLogPatternHandler.java#L1-L50)
- [HostCpuUsageCollector.java:1-216](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/severPerformanceMonitor/HostCpuUsageCollector.java#L1-L216)
- [HostPerformanceCollector.java:1-68](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/report/HostPerformanceCollector.java#L1-L68)
- [CasHostOperateCommandExecutor.java:1-158](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/operate/CasHostOperateCommandExecutor.java#L1-L158)
- [CasSSHService.java:1-131](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/ssh/CasSSHService.java#L1-L131)
- [CasHostHandler.java:1-61](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasHostHandler.java#L1-L61)
- [CasLogCollector.java:1-112](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/log/CasLogCollector.java#L1-L112)
- [CasTestConnectionApi.java:1-30](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasTestConnectionApi.java#L1-L30)
- [CasUriConstants.java:1-822](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L1-L822)

**Section sources**
- [CasCasServerLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasCasServerLogPatternHandler.java#L1-L52)
- [CasCatalinaLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasCatalinaLogPatternHandler.java#L1-L52)
- [CasLibvirtLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasLibvirtLogPatternHandler.java#L1-L52)
- [CasQemuLogPatternHandler.java:1-50](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasQemuLogPatternHandler.java#L1-L50)
- [HostCpuUsageCollector.java:1-216](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/severPerformanceMonitor/HostCpuUsageCollector.java#L1-L216)
- [HostPerformanceCollector.java:1-68](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/report/HostPerformanceCollector.java#L1-L68)
- [CasHostOperateCommandExecutor.java:1-158](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/operate/CasHostOperateCommandExecutor.java#L1-L158)
- [CasSSHService.java:1-131](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/ssh/CasSSHService.java#L1-L131)
- [CasHostHandler.java:1-61](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasHostHandler.java#L1-L61)
- [CasLogCollector.java:1-112](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/log/CasLogCollector.java#L1-L112)
- [CasTestConnectionApi.java:1-30](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasTestConnectionApi.java#L1-L30)
- [CasUriConstants.java:1-822](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L1-L822)

## Core Components
- Log pattern handlers: Parse CAS-specific log formats for server logs, Catalina logs, libvirt logs, and QEMU logs into structured log lines.
- Metrics collectors: Specialized collectors for CPU usage, memory, disk I/O, network throughput, and VM statistics across hosts and clusters.
- Host and VM operations: Command executors to manage host lifecycle and VM operations via CAS REST endpoints.
- SSH authentication: Verify and modify SSH enablement on CAS-managed hosts using CAS REST endpoints.
- Host discovery: Resolve host credentials and IDs from CAS for unified SSH access.
- Batch log collection: Trigger and download batch logs from CAS with progress polling.
- Connectivity testing: Validate CAS platform connectivity via a dedicated endpoint.

**Section sources**
- [CasCasServerLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasCasServerLogPatternHandler.java#L1-L52)
- [CasCatalinaLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasCatalinaLogPatternHandler.java#L1-L52)
- [CasLibvirtLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasLibvirtLogPatternHandler.java#L1-L52)
- [CasQemuLogPatternHandler.java:1-50](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasQemuLogPatternHandler.java#L1-L50)
- [HostCpuUsageCollector.java:1-216](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/severPerformanceMonitor/HostCpuUsageCollector.java#L1-L216)
- [HostPerformanceCollector.java:1-68](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/report/HostPerformanceCollector.java#L1-L68)
- [CasHostOperateCommandExecutor.java:1-158](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/operate/CasHostOperateCommandExecutor.java#L1-L158)
- [CasSSHService.java:1-131](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/ssh/CasSSHService.java#L1-L131)
- [CasHostHandler.java:1-61](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasHostHandler.java#L1-L61)
- [CasLogCollector.java:1-112](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/log/CasLogCollector.java#L1-L112)
- [CasTestConnectionApi.java:1-30](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasTestConnectionApi.java#L1-L30)

## Architecture Overview
The CAS monitoring module orchestrates log parsing, metrics collection, and operational tasks through CAS REST endpoints defined centrally. Components interact with CAS via typed URIs and DTOs provided by the SDK.

```mermaid
graph TB
subgraph "Monitoring"
HP["HostPerformanceCollector"]
HC["HostCpuUsageCollector"]
LOG["CasLogCollector"]
end
subgraph "Operations"
HO["CasHostOperateCommandExecutor"]
SSH["CasSSHService"]
HH["CasHostHandler"]
end
subgraph "Parsing"
P1["CasCasServerLogPatternHandler"]
P2["CasCatalinaLogPatternHandler"]
P3["CasLibvirtLogPatternHandler"]
P4["CasQemuLogPatternHandler"]
end
subgraph "CAS REST"
URI["CasUriConstants"]
end
HP --> URI
HC --> URI
LOG --> URI
HO --> URI
SSH --> URI
HH --> URI
P1 --> URI
P2 --> URI
P3 --> URI
P4 --> URI
```

**Diagram sources**
- [HostPerformanceCollector.java:1-68](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/report/HostPerformanceCollector.java#L1-L68)
- [HostCpuUsageCollector.java:1-216](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/severPerformanceMonitor/HostCpuUsageCollector.java#L1-L216)
- [CasLogCollector.java:1-112](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/log/CasLogCollector.java#L1-L112)
- [CasHostOperateCommandExecutor.java:1-158](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/operate/CasHostOperateCommandExecutor.java#L1-L158)
- [CasSSHService.java:1-131](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/ssh/CasSSHService.java#L1-L131)
- [CasHostHandler.java:1-61](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasHostHandler.java#L1-L61)
- [CasCasServerLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasCasServerLogPatternHandler.java#L1-L52)
- [CasCatalinaLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasCatalinaLogPatternHandler.java#L1-L52)
- [CasLibvirtLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasLibvirtLogPatternHandler.java#L1-L52)
- [CasQemuLogPatternHandler.java:1-50](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasQemuLogPatternHandler.java#L1-L50)
- [CasUriConstants.java:1-822](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L1-L822)

## Detailed Component Analysis

### Log Pattern Handlers
These handlers parse CAS-specific log formats into structured log lines for downstream processing.

```mermaid
classDiagram
class DefaultLogPatternHandler
class LogPatternApi
class CasCasServerLogPatternHandler {
+parseLine(message) Optional~LogLine~
+logType() RealTimeLogTypeEnum
}
class CasCatalinaLogPatternHandler {
+parseLine(message) Optional~LogLine~
+logType() RealTimeLogTypeEnum
}
class CasLibvirtLogPatternHandler {
+parseLine(message) Optional~LogLine~
+logType() RealTimeLogTypeEnum
}
class CasQemuLogPatternHandler {
+parseLine(message) Optional~LogLine~
+logType() RealTimeLogTypeEnum
}
CasCasServerLogPatternHandler --|> DefaultLogPatternHandler
CasCasServerLogPatternHandler ..|> LogPatternApi
CasCatalinaLogPatternHandler --|> DefaultLogPatternHandler
CasCatalinaLogPatternHandler ..|> LogPatternApi
CasLibvirtLogPatternHandler --|> DefaultLogPatternHandler
CasLibvirtLogPatternHandler ..|> LogPatternApi
CasQemuLogPatternHandler --|> DefaultLogPatternHandler
CasQemuLogPatternHandler ..|> LogPatternApi
```

**Diagram sources**
- [CasCasServerLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasCasServerLogPatternHandler.java#L1-L52)
- [CasCatalinaLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasCatalinaLogPatternHandler.java#L1-L52)
- [CasLibvirtLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasLibvirtLogPatternHandler.java#L1-L52)
- [CasQemuLogPatternHandler.java:1-50](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasQemuLogPatternHandler.java#L1-L50)

**Section sources**
- [CasCasServerLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasCasServerLogPatternHandler.java#L1-L52)
- [CasCatalinaLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasCatalinaLogPatternHandler.java#L1-L52)
- [CasLibvirtLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasLibvirtLogPatternHandler.java#L1-L52)
- [CasQemuLogPatternHandler.java:1-50](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasQemuLogPatternHandler.java#L1-L50)

### Host and VM Metrics Collection
The collectors query CAS REST endpoints to gather performance metrics for hosts, clusters, and domains.

```mermaid
sequenceDiagram
participant Coll as "HostCpuUsageCollector"
participant SDK as "CasRestConnection"
participant CAS as "CAS REST"
Coll->>SDK : get(platform, host, protocol, port, username, password, url, typeRef)
SDK->>CAS : GET /cas/casrs/host/{id}/cpuTrend
CAS-->>SDK : List<Rate>
SDK-->>Coll : List<Rate>
Coll->>Coll : build DataValueAndTagsDTO
Coll-->>Coll : return List<DataValueAndTagsDTO>
```

**Diagram sources**
- [HostCpuUsageCollector.java:1-216](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/severPerformanceMonitor/HostCpuUsageCollector.java#L1-L216)
- [CasUriConstants.java:154-155](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L154-L155)

**Section sources**
- [HostCpuUsageCollector.java:1-216](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/severPerformanceMonitor/HostCpuUsageCollector.java#L1-L216)
- [HostPerformanceCollector.java:1-68](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/report/HostPerformanceCollector.java#L1-L68)
- [CasUriConstants.java:1-822](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L1-L822)

### Host Operation Command Executor
Executes host lifecycle operations (wake, shutdown, restart, enter/exit maintenance) and refreshes status.

```mermaid
sequenceDiagram
participant Exec as "CasHostOperateCommandExecutor"
participant SDK as "CasRestConnection"
participant CAS as "CAS REST"
Exec->>SDK : get(platform, host, protocol, port, username, password, HOST_BASIC_INFO, typeRef)
SDK->>CAS : GET /cas/casrs/host/id/{hostId}
CAS-->>SDK : HostInfo
SDK-->>Exec : HostInfo
Exec->>SDK : put(platform, host, protocol, port, username, password, HOST_WAKE/SHUTDOWN/RESTART/INTO/EXIT, dto, typeRef)
SDK->>CAS : PUT /cas/casrs/host/{action}
CAS-->>SDK : CasRsTaskMsg
SDK-->>Exec : CasRsTaskMsg
Exec->>SDK : get(platform, host, protocol, port, username, password, MESSAGE/{msgId}, typeRef)
SDK->>CAS : GET /cas/casrs/message/{msgId}
CAS-->>SDK : CasRsTaskMsg
SDK-->>Exec : CasRsTaskMsg
```

**Diagram sources**
- [CasHostOperateCommandExecutor.java:1-158](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/operate/CasHostOperateCommandExecutor.java#L1-L158)
- [CasUriConstants.java:16-20](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L16-L20)

**Section sources**
- [CasHostOperateCommandExecutor.java:1-158](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/operate/CasHostOperateCommandExecutor.java#L1-L158)
- [CasUriConstants.java:1-822](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L1-L822)

### SSH Authentication Mechanisms
Verifies user SSH credentials and toggles SSH enablement on CAS-managed hosts.

```mermaid
sequenceDiagram
participant SSH as "CasSSHService"
participant SDK as "CasRestConnection"
participant TOK as "CasTokenRestConnection"
participant CAS as "CAS REST"
SSH->>SDK : post(RESOURCE_CAS, ip, protocol, port, username, password, LOGIN, HttpEntity, typeRef)
SDK->>CAS : POST /cas/spring_check
CAS-->>SDK : VdisshCheckResult
SDK-->>SSH : VdisshCheckResult
SSH->>TOK : refreshToken(ip, protocol, port, username, password)
TOK->>CAS : GET /cas/token
CAS-->>TOK : cookie/token
TOK-->>SSH : token
SSH->>SDK : post(RESOURCE_CAS, ip, protocol, port, username, password, MODIFY_CAS_SSH_AUTH, HttpEntity, typeRef)
SDK->>CAS : POST /cas/casrs/parameter/modifyEnableSSH
CAS-->>SDK : response
SDK-->>SSH : response
```

**Diagram sources**
- [CasSSHService.java:1-131](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/ssh/CasSSHService.java#L1-L131)
- [CasUriConstants.java:800-819](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L800-L819)

**Section sources**
- [CasSSHService.java:1-131](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/ssh/CasSSHService.java#L1-L131)
- [CasUriConstants.java:800-822](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L800-L822)

### Host Discovery and Credentials
Resolves host credentials and IDs for unified SSH access.

```mermaid
flowchart TD
A["getHost(RestHost, endpoint)"] --> B{"endpoint == '0'?"}
B --> |Yes| C["return SSHHost(management platform)"]
B --> |No| D["GET /cas/casrs/host/id/{id}"]
D --> E["HostInfo(ip, user, pwd, id, name)"]
E --> F["SSHHost.newInstance(...)"]
```

**Diagram sources**
- [CasHostHandler.java:1-61](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasHostHandler.java#L1-L61)
- [CasUriConstants.java:102-103](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L102-L103)

**Section sources**
- [CasHostHandler.java:1-61](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasHostHandler.java#L1-L61)
- [CasUriConstants.java:1-822](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L1-L822)

### Batch Log Collection
Triggers log collection on CAS hosts and downloads the resulting archive after completion.

```mermaid
sequenceDiagram
participant LC as "CasLogCollector"
participant TOK as "CasTokenRestConnection"
participant SDK as "CasRestConnection"
participant CAS as "CAS REST"
LC->>TOK : get(host, protocol, username, password, port, QUERY_HOST_LIST, typeRef)
TOK->>CAS : GET /cas/host/queryHostList
CAS-->>TOK : List<CasHostInfoDTO>
TOK-->>LC : List<CasHostInfoDTO>
loop until success
LC->>TOK : put(host, protocol, username, password, port, GATHER_LOG, dto, typeRef)
TOK->>CAS : PUT /cas/operationlog/gatherLog
CAS-->>TOK : RpcResult<String>{success,msgId}
TOK-->>LC : RpcResult<String>
end
loop until completed
LC->>SDK : get(platform, host, protocol, port, username, password, MESSAGE/{msgId}, typeRef)
SDK->>CAS : GET /cas/casrs/message/{msgId}
CAS-->>SDK : CasRsTaskMsg{completed}
SDK-->>LC : CasRsTaskMsg
end
LC->>TOK : downloadBigFile(host, protocol, username, password, port, DOWNLOAD_LOGFILE/{uuid}, dir, ext)
TOK->>CAS : GET /cas/download/logfile
CAS-->>TOK : tar.gz
TOK-->>LC : file
```

**Diagram sources**
- [CasLogCollector.java:1-112](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/log/CasLogCollector.java#L1-L112)
- [CasUriConstants.java:783-798](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L783-L798)

**Section sources**
- [CasLogCollector.java:1-112](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/log/CasLogCollector.java#L1-L112)
- [CasUriConstants.java:783-798](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L783-L798)

### Connectivity Testing
Validates CAS platform connectivity using a test endpoint.

```mermaid
sequenceDiagram
participant Test as "CasTestConnectionApi"
participant SDK as "CasRestConnection"
participant CAS as "CAS REST"
Test->>SDK : get(platform, host, protocol, port, username, password, TEST_CONNECTION, typeRef)
SDK->>CAS : GET /cas/casrs/operator/test
CAS-->>SDK : response
SDK-->>Test : response
```

**Diagram sources**
- [CasTestConnectionApi.java:1-30](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasTestConnectionApi.java#L1-L30)
- [CasUriConstants.java](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L15)

**Section sources**
- [CasTestConnectionApi.java:1-30](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasTestConnectionApi.java#L1-L30)
- [CasUriConstants.java](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L15)

## Dependency Analysis
The CAS module depends on SDK-provided REST connections and URI constants. The following diagram highlights key dependencies among major components.

```mermaid
graph LR
HC["HostCpuUsageCollector"] --> URI["CasUriConstants"]
HP["HostPerformanceCollector"] --> URI
HO["CasHostOperateCommandExecutor"] --> URI
SSH["CasSSHService"] --> URI
HH["CasHostHandler"] --> URI
LOG["CasLogCollector"] --> URI
P1["CasCasServerLogPatternHandler"] --> URI
P2["CasCatalinaLogPatternHandler"] --> URI
P3["CasLibvirtLogPatternHandler"] --> URI
P4["CasQemuLogPatternHandler"] --> URI
```

**Diagram sources**
- [HostCpuUsageCollector.java:1-216](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/severPerformanceMonitor/HostCpuUsageCollector.java#L1-L216)
- [HostPerformanceCollector.java:1-68](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/report/HostPerformanceCollector.java#L1-L68)
- [CasHostOperateCommandExecutor.java:1-158](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/operate/CasHostOperateCommandExecutor.java#L1-L158)
- [CasSSHService.java:1-131](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/ssh/CasSSHService.java#L1-L131)
- [CasHostHandler.java:1-61](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasHostHandler.java#L1-L61)
- [CasLogCollector.java:1-112](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/log/CasLogCollector.java#L1-L112)
- [CasCasServerLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasCasServerLogPatternHandler.java#L1-L52)
- [CasCatalinaLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasCatalinaLogPatternHandler.java#L1-L52)
- [CasLibvirtLogPatternHandler.java:1-52](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasLibvirtLogPatternHandler.java#L1-L52)
- [CasQemuLogPatternHandler.java:1-50](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasQemuLogPatternHandler.java#L1-L50)
- [CasUriConstants.java:1-822](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L1-L822)

**Section sources**
- [CasUriConstants.java:1-822](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/constant/uri/CasUriConstants.java#L1-L822)

## Performance Considerations
- Parallel collection: Some collectors stream and process lists in parallel to reduce latency when querying multiple hosts or clusters.
- Minimal retries: Authentication and operation flows handle transient errors and retry conditions carefully to avoid unnecessary load.
- Polling intervals: Log collection and task completion polling use fixed intervals; tuning these may impact responsiveness and load.
- Endpoint selection: Prefer targeted queries (by host/cluster/domain IDs) to minimize payload sizes and improve throughput.

[No sources needed since this section provides general guidance]

## Troubleshooting Guide
Common issues and resolutions:
- SSH authentication failures: Verify credentials and permissions via the login endpoint; ensure required permissions are present before enabling SSH.
- Operation timeouts: Host operations may take time; confirm task completion via message endpoints and handle transient HTTP client errors gracefully.
- Log collection stuck: Ensure only valid host IDs are provided; invalid IDs can block collection queues. Monitor message completion and retry on failure.
- Connectivity tests failing: Confirm the test endpoint is reachable and CAS is responding; check network policies and firewall rules.

**Section sources**
- [CasSSHService.java:42-67](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/ssh/CasSSHService.java#L42-L67)
- [CasHostOperateCommandExecutor.java:50-65](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/operate/CasHostOperateCommandExecutor.java#L50-L65)
- [CasLogCollector.java:35-102](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/log/CasLogCollector.java#L35-L102)
- [CasTestConnectionApi.java:18-23](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/CasTestConnectionApi.java#L18-L23)

## Conclusion
The CAS monitoring module provides comprehensive coverage for log parsing, metrics collection, host/VM operations, SSH authentication, and batch log collection. Its design leverages SDK-defined REST endpoints and URI constants to integrate tightly with the CAS platform, enabling robust monitoring and operational workflows.

[No sources needed since this section summarizes without analyzing specific files]

## Appendices

### Configuration Examples
- SSH enablement toggle: Use the modify endpoint to set SSH enablement and verify via the find endpoint.
- Log collection: Provide a time window and target host IDs; the collector validates host IDs against CAS and polls for completion.
- Metrics collection: Select appropriate collectors by tags (host IDs, cluster IDs, domain IDs) to scope queries efficiently.

**Section sources**
- [CasSSHService.java:69-100](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/ssh/CasSSHService.java#L69-L100)
- [CasLogCollector.java:35-102](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/log/CasLogCollector.java#L35-L102)
- [HostCpuUsageCollector.java:44-58](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/severPerformanceMonitor/HostCpuUsageCollector.java#L44-L58)

### Metric Interpretation Guidelines
- CPU usage: Collected trends represent utilization rates; interpret spikes as potential contention or workload bursts.
- Memory usage: Monitor growth trends to detect leaks or misconfigurations.
- Disk I/O and network throughput: Use top-N collectors to identify bottlenecks per host or cluster.
- VM statistics: Track CPU, memory, disk, and network trends for individual domains to correlate performance with guest activity.

**Section sources**
- [HostCpuUsageCollector.java:1-216](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/severPerformanceMonitor/HostCpuUsageCollector.java#L1-L216)
- [HostPerformanceCollector.java:1-68](file://watcher-cas/src/main/java/com/virtual/cloud/om/cas/service/report/HostPerformanceCollector.java#L1-L68)