# Workspace Platform Module

<cite>
**Referenced Files in This Document**
- [pom.xml](file://watcher-workspace/pom.xml)
- [NativeController.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/web/NativeController.java)
- [WorkspaceTestConnectionApi.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/WorkspaceTestConnectionApi.java)
- [WsHostHandler.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/WsHostHandler.java)
- [DesktopPoolBasicCollector.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/report/DesktopPoolBasicCollector.java)
- [DesktopPoolVmRelationCollector.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/report/DesktopPoolVmRelationCollector.java)
- [TerminalBasicCollector.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/report/TerminalBasicCollector.java)
- [WorkspaceDesktopPoolOperateCommandExecutor.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/operate/WorkspaceDesktopPoolOperateCommandExecutor.java)
- [WorkspaceTerminalOperateCommandExecutor.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/operate/WorkspaceTerminalOperateCommandExecutor.java)
- [WorkspaceHostLogCollector.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceHostLogCollector.java)
- [WorkspaceTerminalLogCollector.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceTerminalLogCollector.java)
- [WorkspaceVmLogCollector.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceVmLogCollector.java)
- [WorkspaceControllerLogPatternHandler.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceControllerLogPatternHandler.java)
- [WorkspaceGrpcLogPatternHandler.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceGrpcLogPatternHandler.java)
- [WorkspaceServerLogPatternHandler.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceServerLogPatternHandler.java)
- [OthersPatternHandler.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/OthersPatternHandler.java)
- [WorkspaceSshService.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/ssh/WorkspaceSshService.java)
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
This document describes the Workspace platform monitoring module responsible for desktop pool management and terminal monitoring. It covers the comprehensive reporting system for desktop pools and terminals, VM relation tracking, batch log collection for hosts, terminals, and VMs, specialized real-time log pattern handlers for controller, gRPC, and server-side logs, operational command executors for desktop pool and terminal management, test connection APIs, and integration with Workspace REST services. It also includes configuration examples, log parsing patterns, and operational management workflows.

## Project Structure
The Workspace module is a Spring Boot application packaged as a Maven artifact. It integrates with the shared SDK to communicate with Workspace REST endpoints, issue batch log requests, and parse real-time logs.

```mermaid
graph TB
subgraph "watcher-workspace"
A["web/NativeController.java"]
B["service/report/*.java"]
C["service/batchlog/*.java"]
D["service/realtimelog/*.java"]
E["service/operate/*.java"]
F["service/ssh/WorkspaceSshService.java"]
G["service/WorkspaceTestConnectionApi.java"]
H["service/WsHostHandler.java"]
end
subgraph "watcher-sdk"
I["api/*"]
J["dto/*"]
K["constant/*"]
L["config/rest/*"]
end
A --> I
B --> I
C --> I
D --> I
E --> I
F --> I
G --> I
H --> I
B --> L
C --> L
D --> J
E --> J
G --> L
H --> L
```

**Diagram sources**
- [pom.xml:14-30](file://watcher-workspace/pom.xml#L14-L30)
- [NativeController.java:1-19](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/web/NativeController.java#L1-L19)
- [DesktopPoolBasicCollector.java:1-150](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/report/DesktopPoolBasicCollector.java#L1-L150)
- [WorkspaceHostLogCollector.java:1-95](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceHostLogCollector.java#L1-L95)
- [WorkspaceControllerLogPatternHandler.java:1-58](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceControllerLogPatternHandler.java#L1-L58)

**Section sources**
- [pom.xml:1-45](file://watcher-workspace/pom.xml#L1-L45)

## Core Components
- Reporting collectors for desktop pools, VM relations, and terminals
- Batch log collectors for hosts, terminals, and VMs
- Real-time log pattern handlers for Workspace controller, gRPC, and server logs
- Operational command executors for desktop pools and terminals
- Test connection API and host handler for Workspace
- SSH service for Workspace-managed environments

**Section sources**
- [DesktopPoolBasicCollector.java:1-150](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/report/DesktopPoolBasicCollector.java#L1-L150)
- [DesktopPoolVmRelationCollector.java:1-116](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/report/DesktopPoolVmRelationCollector.java#L1-L116)
- [TerminalBasicCollector.java:1-97](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/report/TerminalBasicCollector.java#L1-L97)
- [WorkspaceHostLogCollector.java:1-95](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceHostLogCollector.java#L1-L95)
- [WorkspaceTerminalLogCollector.java:1-153](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceTerminalLogCollector.java#L1-L153)
- [WorkspaceVmLogCollector.java:1-153](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceVmLogCollector.java#L1-L153)
- [WorkspaceControllerLogPatternHandler.java:1-58](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceControllerLogPatternHandler.java#L1-L58)
- [WorkspaceGrpcLogPatternHandler.java:1-58](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceGrpcLogPatternHandler.java#L1-L58)
- [WorkspaceServerLogPatternHandler.java:1-59](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceServerLogPatternHandler.java#L1-L59)
- [WorkspaceDesktopPoolOperateCommandExecutor.java:131-303](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/operate/WorkspaceDesktopPoolOperateCommandExecutor.java#L131-L303)
- [WorkspaceTerminalOperateCommandExecutor.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/operate/WorkspaceTerminalOperateCommandExecutor.java)
- [WorkspaceTestConnectionApi.java:1-44](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/WorkspaceTestConnectionApi.java#L1-L44)
- [WsHostHandler.java:1-63](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/WsHostHandler.java#L1-L63)
- [WorkspaceSshService.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/ssh/WorkspaceSshService.java)

## Architecture Overview
The Workspace module orchestrates monitoring and operations via:
- REST connections to Workspace services for data retrieval and log collection
- Asynchronous parallelization for efficient data gathering
- Specialized log pattern handlers for structured real-time log parsing
- Operational executors to trigger actions against desktop pools and terminals

```mermaid
graph TB
subgraph "Workspace Module"
RP["Report Collectors<br/>DesktopPoolBasicCollector, DesktopPoolVmRelationCollector, TerminalBasicCollector"]
BL["Batch Log Collectors<br/>WorkspaceHostLogCollector, WorkspaceTerminalLogCollector, WorkspaceVmLogCollector"]
RL["Real-Time Log Handlers<br/>Controller, gRPC, Server, Others"]
OP["Operational Executors<br/>DesktopPool, Terminal"]
TC["Test Connection API"]
HH["Host Handler"]
SSH["SSH Service"]
end
subgraph "SDK"
RC["WsRestConnection / WsTokenRestConnection"]
DTO["DTOs for reports/logs/commands"]
CONST["URI constants, enums"]
end
RP --> RC
BL --> RC
RL --> DTO
OP --> RC
TC --> RC
HH --> RC
SSH --> RC
RP --> CONST
BL --> CONST
RL --> CONST
OP --> CONST
TC --> CONST
HH --> CONST
SSH --> CONST
```

**Diagram sources**
- [DesktopPoolBasicCollector.java:32-149](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/report/DesktopPoolBasicCollector.java#L32-L149)
- [WorkspaceHostLogCollector.java:30-93](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceHostLogCollector.java#L30-L93)
- [WorkspaceControllerLogPatternHandler.java:18-57](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceControllerLogPatternHandler.java#L18-L57)
- [WorkspaceDesktopPoolOperateCommandExecutor.java:131-303](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/operate/WorkspaceDesktopPoolOperateCommandExecutor.java#L131-L303)
- [WorkspaceTestConnectionApi.java:20-43](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/WorkspaceTestConnectionApi.java#L20-L43)
- [WsHostHandler.java:25-62](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/WsHostHandler.java#L25-L62)

## Detailed Component Analysis

### Reporting System: Desktop Pool Basics
- Retrieves desktop pool list and enriches metrics such as allocation counts, online/offline stats, template info, and cluster/host details.
- Uses asynchronous futures per desktop pool to parallelize queries and reduce total collection latency.
- Emits JSON-formatted metrics tagged with collection metadata.

```mermaid
sequenceDiagram
participant Coll as "DesktopPoolBasicCollector"
participant Conn as "WsTokenRestConnection"
participant WS as "Workspace REST"
Coll->>Conn : GET desktop pools list
Conn->>WS : Query desktop pools
WS-->>Conn : RpcListLoadResult<DesktopPoolDTO>
Conn-->>Coll : DesktopPoolDTO[]
loop For each desktop pool
Coll->>Conn : GET VM status stats
Conn->>WS : Query VMS_STAT
WS-->>Conn : DomainStatusStat
Coll->>Conn : GET desktop pool details
Conn->>WS : Query DESKTOPPOOLS_INFO_BY_ID
WS-->>Conn : DesktopPoolDTO
Coll->>Conn : GET template info
Conn->>WS : Query IMAGE_TEMPLATE_INFO
WS-->>Conn : ImageResponseDTO[]
Coll->>Conn : GET desktop pool summary (REST)
Conn->>WS : Query DESKTOP_POOL_BYID
WS-->>Conn : RestDesktopPoolDTO
end
Coll-->>Coll : Build DesktopPoolBasicDTO list
```

**Diagram sources**
- [DesktopPoolBasicCollector.java:36-139](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/report/DesktopPoolBasicCollector.java#L36-L139)

**Section sources**
- [DesktopPoolBasicCollector.java:1-150](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/report/DesktopPoolBasicCollector.java#L1-L150)

### Reporting System: VM Relation Tracking
- Enumerates desktop pools and determines whether each belongs to VMs or terminals.
- For VM-type pools, fetches VM UUIDs; for terminal-type pools, fetches terminal IDs.
- Produces a flat relation dataset linking desktop pools to underlying VMs or terminals.

```mermaid
flowchart TD
Start(["Start"]) --> FetchPools["Fetch desktop pools"]
FetchPools --> LoopPools{"For each pool"}
LoopPools --> CheckType{"computerType == 0?"}
CheckType --> |Yes| QueryVMs["GET desktop pool VMs"]
CheckType --> |No| QueryTerminals["GET desktop pool terminals"]
QueryVMs --> MapVMs["Map to DesktopPoolVmRelationDTO"]
QueryTerminals --> MapTerminals["Map to DesktopPoolVmRelationDTO"]
MapVMs --> NextPool["Next pool"]
MapTerminals --> NextPool
NextPool --> LoopPools
LoopPools --> |Done| Emit["Emit JSON relations"]
Emit --> End(["End"])
```

**Diagram sources**
- [DesktopPoolVmRelationCollector.java:36-104](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/report/DesktopPoolVmRelationCollector.java#L36-L104)

**Section sources**
- [DesktopPoolVmRelationCollector.java:1-116](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/report/DesktopPoolVmRelationCollector.java#L1-L116)

### Reporting System: Terminal Statistics
- Retrieves all terminals and maps fields such as device ID, MAC/IP, OS/arch, vendor/model, client/space agent versions, registration time, auth type, status, and group info.
- Returns a JSON payload suitable for downstream analytics.

```mermaid
sequenceDiagram
participant Coll as "TerminalBasicCollector"
participant Conn as "WsTokenRestConnection"
participant WS as "Workspace REST"
Coll->>Conn : GET terminal basic list
Conn->>WS : Query TERMINAL_BASIC
WS-->>Conn : RpcListLoadResult<VdiDeviceDTO>
Conn-->>Coll : VdiDeviceDTO[]
Coll-->>Coll : Map to TerminalBasicDTO list
```

**Diagram sources**
- [TerminalBasicCollector.java:34-85](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/report/TerminalBasicCollector.java#L34-L85)

**Section sources**
- [TerminalBasicCollector.java:1-97](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/report/TerminalBasicCollector.java#L1-L97)

### Batch Log Collection: Host Logs
- Applies a log-gathering job to target hosts, polls for completion, and downloads the resulting archive.
- Uses a token-based REST connection and supports configurable time windows.

```mermaid
sequenceDiagram
participant Coll as "WorkspaceHostLogCollector"
participant Conn as "WsTokenRestConnection"
participant WS as "Workspace REST"
Coll->>Coll : Build GatherLogNewDto
Coll->>Conn : POST GATHER_LOG
Conn->>WS : Apply host log collection
WS-->>Conn : RpcResult<String> uuid
loop Poll until ready
Coll->>Conn : GET GATHER_LOG_RESULT
Conn->>WS : Query result
WS-->>Conn : OperationLogResultDTO
end
Coll->>Conn : Download zip by uuid
Conn->>WS : GET GATHER_LOG_DOWNLOAD
WS-->>Conn : Archive bytes
```

**Diagram sources**
- [WorkspaceHostLogCollector.java:34-87](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceHostLogCollector.java#L34-L87)

**Section sources**
- [WorkspaceHostLogCollector.java:1-95](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceHostLogCollector.java#L1-L95)

### Batch Log Collection: Terminal Logs
- Initiates terminal log collection per target, recursively waits for completion, deduplicates by filename, and downloads successful archives.

```mermaid
flowchart TD
Start(["Start"]) --> ForEach["For each terminal target"]
ForEach --> Apply["POST TERMINAL_LOG_COLLECT"]
Apply --> Wait["Poll TERMINAL_LOGS until success fields filled"]
Wait --> Dedup["Group by filename and take first occurrence"]
Dedup --> Download{"Any succeeded?"}
Download --> |Yes| DL["Download zip by log id"]
Download --> |No| Fail["Mark as failed"]
DL --> Next["Next target"]
Fail --> Next
Next --> ForEach
ForEach --> |Done| Decide["Decide result: success/part_success/fail"]
Decide --> End(["End"])
```

**Diagram sources**
- [WorkspaceTerminalLogCollector.java:34-96](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceTerminalLogCollector.java#L34-L96)

**Section sources**
- [WorkspaceTerminalLogCollector.java:1-153](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceTerminalLogCollector.java#L1-L153)

### Batch Log Collection: VM Logs
- Triggers VM log collection per VM title/id, polls for completion, and downloads successful archives.

```mermaid
sequenceDiagram
participant Coll as "WorkspaceVmLogCollector"
participant Conn as "WsTokenRestConnection"
participant WS as "Workspace REST"
Coll->>Coll : For each VM target
Coll->>Conn : POST VM_LOG_COLLECT(title, id)
Conn->>WS : Apply VM log collection
WS-->>Conn : RpcResult<String> uuid
Coll->>Conn : GET VM_LOGS
Conn->>WS : Query VM logs list
WS-->>Conn : RpcResult<List<VmLogDTO>>
Coll->>Coll : Filter by title and status==completed
alt Success
Coll->>Conn : Download VM_LOG_DOWNLOAD(id)
Conn->>WS : GET download
WS-->>Conn : Archive bytes
else Failure
Coll->>Coll : Record failure
end
```

**Diagram sources**
- [WorkspaceVmLogCollector.java:36-89](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceVmLogCollector.java#L36-L89)

**Section sources**
- [WorkspaceVmLogCollector.java:1-153](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceVmLogCollector.java#L1-L153)

### Real-Time Log Processing: Pattern Handlers
- Controller logs: Parses structured lines with timestamp, level, thread, request UUID, IP/port, method, line number, and message.
- gRPC logs: Similar structure tailored for gRPC request contexts.
- Server logs: Parses server-side entries with similar fields.
- Others: Fallback handler for unhandled patterns.

```mermaid
classDiagram
class DefaultLogPatternHandler
class LogPatternApi {
+parseLine(message) Optional~LogLine~
+logType() RealTimeLogTypeEnum
}
class WorkspaceControllerLogPatternHandler {
+parseLine(message) Optional~LogLine~
+logType() RealTimeLogTypeEnum
}
class WorkspaceGrpcLogPatternHandler {
+parseLine(message) Optional~LogLine~
+logType() RealTimeLogTypeEnum
}
class WorkspaceServerLogPatternHandler {
+parseLine(message) Optional~LogLine~
+logType() RealTimeLogTypeEnum
}
class OthersPatternHandler {
+parseLine(message) Optional~LogLine~
+logType() RealTimeLogTypeEnum
}
LogPatternApi <|.. WorkspaceControllerLogPatternHandler
LogPatternApi <|.. WorkspaceGrpcLogPatternHandler
LogPatternApi <|.. WorkspaceServerLogPatternHandler
LogPatternApi <|.. OthersPatternHandler
DefaultLogPatternHandler <|-- WorkspaceControllerLogPatternHandler
DefaultLogPatternHandler <|-- WorkspaceGrpcLogPatternHandler
DefaultLogPatternHandler <|-- WorkspaceServerLogPatternHandler
DefaultLogPatternHandler <|-- OthersPatternHandler
```

**Diagram sources**
- [WorkspaceControllerLogPatternHandler.java:18-57](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceControllerLogPatternHandler.java#L18-L57)
- [WorkspaceGrpcLogPatternHandler.java:18-57](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceGrpcLogPatternHandler.java#L18-L57)
- [WorkspaceServerLogPatternHandler.java:18-58](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceServerLogPatternHandler.java#L18-L58)
- [OthersPatternHandler.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/OthersPatternHandler)

**Section sources**
- [WorkspaceControllerLogPatternHandler.java:1-58](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceControllerLogPatternHandler.java#L1-L58)
- [WorkspaceGrpcLogPatternHandler.java:1-58](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceGrpcLogPatternHandler.java#L1-L58)
- [WorkspaceServerLogPatternHandler.java:1-59](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceServerLogPatternHandler.java#L1-L59)
- [OthersPatternHandler.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/OthersPatternHandler)

### Operational Command Executors: Desktop Pool and Terminals
- Desktop pool executor: Lists terminals in a pool and executes operations (e.g., power actions) in parallel across devices, aggregating results and emitting status refresh events.
- Terminal executor: Executes targeted operations on individual terminals.

```mermaid
sequenceDiagram
participant Exec as "WorkspaceDesktopPoolOperateCommandExecutor"
participant Conn as "WsTokenRestConnection"
participant WS as "Workspace REST"
Exec->>Conn : GET pool terminals
Conn->>WS : Query DESKTOPPOOLS_TERMINALS
WS-->>Conn : RpcListLoadResult<VdiDeviceDTO>
Conn-->>Exec : Device list
par Parallel operations
Exec->>Conn : Operate on each terminal
Conn->>WS : Terminal operation
WS-->>Conn : Result
end
Exec-->>Exec : Aggregate results and emit refresh event
```

**Diagram sources**
- [WorkspaceDesktopPoolOperateCommandExecutor.java:131-303](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/operate/WorkspaceDesktopPoolOperateCommandExecutor.java#L131-L303)

**Section sources**
- [WorkspaceDesktopPoolOperateCommandExecutor.java:131-303](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/operate/WorkspaceDesktopPoolOperateCommandExecutor.java#L131-L303)
- [WorkspaceTerminalOperateCommandExecutor.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/operate/WorkspaceTerminalOperateCommandExecutor.java)

### Test Connection API and Host Handler
- Test connection API: Encrypts credentials and validates connectivity to Workspace via a test endpoint, returning error messages on failure.
- Host handler: Resolves SSH host credentials for Workspace-managed hosts, supporting both management platform and specific host endpoints.

```mermaid
sequenceDiagram
participant Test as "WorkspaceTestConnectionApi"
participant Conn as "WsRestConnection"
participant WS as "Workspace REST"
Test->>Test : Build WorkspaceLoginInfoDTO (encrypted)
Test->>Conn : POST TEST_CONNECTION
Conn->>WS : Authenticate
WS-->>Conn : RpcResult<WorkspaceLoginResultDTO>
Conn-->>Test : Result
alt Success
Test-->>Caller : null (no error)
else Failure
Test-->>Caller : errorMessage
end
```

**Diagram sources**
- [WorkspaceTestConnectionApi.java:23-37](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/WorkspaceTestConnectionApi.java#L23-L37)

**Section sources**
- [WorkspaceTestConnectionApi.java:1-44](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/WorkspaceTestConnectionApi.java#L1-L44)
- [WsHostHandler.java:25-62](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/WsHostHandler.java#L25-L62)

### Native Controller and SSH Service
- Native controller: Provides a simple health/home endpoint under /home.
- SSH service: Offers SSH-related capabilities integrated with Workspace-managed environments.

**Section sources**
- [NativeController.java:1-19](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/web/NativeController.java#L1-L19)
- [WorkspaceSshService.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/ssh/WorkspaceSshService.java)

## Dependency Analysis
The Workspace module depends on the SDK for:
- REST connection abstractions (token and non-token)
- DTOs for reports, logs, and commands
- Constants for URIs and enumerations
- Utilities and exception handling

```mermaid
graph LR
WS["watcher-workspace"] --> SDK["watcher-sdk"]
SDK --> API["sdk/api/*"]
SDK --> DTO["sdk/dto/*"]
SDK --> CONST["sdk/constant/*"]
SDK --> CFG["sdk/config/rest/*"]
WS --> API
WS --> DTO
WS --> CONST
WS --> CFG
```

**Diagram sources**
- [pom.xml:22-29](file://watcher-workspace/pom.xml#L22-L29)

**Section sources**
- [pom.xml:1-45](file://watcher-workspace/pom.xml#L1-L45)

## Performance Considerations
- Parallelization: Desktop pool and relation collectors use asynchronous futures to minimize total collection time.
- Polling cadence: Batch log collectors implement bounded retries with short sleeps to balance responsiveness and load.
- Result deduplication: Terminal log collector groups by filename to avoid redundant downloads.
- JSON parsing: Real-time log handlers rely on lightweight regex parsing; ensure log formats remain stable to avoid re-parsing overhead.

[No sources needed since this section provides general guidance]

## Troubleshooting Guide
- Authentication failures: Verify encrypted credentials and endpoint reachability in the test connection API.
- Empty collections: Desktop pool collectors handle empty lists gracefully; confirm pool existence and permissions.
- Batch log timeouts: Increase polling intervals or adjust maximum retry counts in log collectors.
- Parsing mismatches: Validate log line formats against pattern handlers; update regex if Workspace log format changes.

**Section sources**
- [WorkspaceTestConnectionApi.java:23-37](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/WorkspaceTestConnectionApi.java#L23-L37)
- [DesktopPoolBasicCollector.java:50-52](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/report/DesktopPoolBasicCollector.java#L50-L52)
- [WorkspaceTerminalLogCollector.java:127-140](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceTerminalLogCollector.java#L127-L140)
- [WorkspaceVmLogCollector.java:120-141](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceVmLogCollector.java#L120-L141)

## Conclusion
The Workspace platform module provides a robust foundation for desktop pool and terminal monitoring, including comprehensive reporting, batch log collection, and real-time log parsing. Its integration with Workspace REST services and SDK enables scalable, parallelized operations and reliable diagnostics across hosts, VMs, and terminals.

[No sources needed since this section summarizes without analyzing specific files]

## Appendices

### Configuration Examples
- REST endpoints: Use URI constants from the SDK to construct requests for desktop pools, terminals, VMs, and logs.
- Credentials: The test connection API encrypts username/password before sending to Workspace.
- Log patterns: Ensure log formats match the Workspace-specific handlers for accurate parsing.

**Section sources**
- [WorkspaceTestConnectionApi.java:24-36](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/WorkspaceTestConnectionApi.java#L24-L36)
- [WorkspaceControllerLogPatternHandler.java:21-51](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceControllerLogPatternHandler.java#L21-L51)
- [WorkspaceGrpcLogPatternHandler.java:20-50](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceGrpcLogPatternHandler.java#L20-L50)
- [WorkspaceServerLogPatternHandler.java:21-51](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/realtimelog/WorkspaceServerLogPatternHandler.java#L21-L51)

### Operational Management Workflows
- Desktop pool operations: Enumerate terminals and dispatch operations in parallel; aggregate results and push status updates.
- Terminal operations: Target individual terminals for lifecycle actions.
- Host/VM log collection: Initiate jobs, poll for completion, and download archives.

**Section sources**
- [WorkspaceDesktopPoolOperateCommandExecutor.java:131-303](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/operate/WorkspaceDesktopPoolOperateCommandExecutor.java#L131-L303)
- [WorkspaceTerminalOperateCommandExecutor.java](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/operate/WorkspaceTerminalOperateCommandExecutor.java)
- [WorkspaceHostLogCollector.java:34-87](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceHostLogCollector.java#L34-L87)
- [WorkspaceTerminalLogCollector.java:34-96](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceTerminalLogCollector.java#L34-L96)
- [WorkspaceVmLogCollector.java:36-89](file://watcher-workspace/src/main/java/com/virtual/cloud/om/workspace/service/batchlog/WorkspaceVmLogCollector.java#L36-L89)