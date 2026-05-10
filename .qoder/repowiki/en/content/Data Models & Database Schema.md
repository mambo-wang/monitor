# Data Models & Database Schema

<cite>
**Referenced Files in This Document**
- [ResourceEntity.java](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/ResourceEntity.java)
- [DataCenterConfig.java](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/DataCenterConfig.java)
- [Task.java](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/Task.java)
- [Warn.java](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/Warn.java)
- [TaskDTO.java](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/TaskDTO.java)
- [WarnDTO.java](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/WarnDTO.java)
- [schema-mysql.sql](file://watcher-agent/src/main/resources/schema-mysql.sql)
- [ResourceEntity.java](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/entity/mysql/ResourceEntity.java)
- [ResourceEntityMapper.java](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/mapper/ResourceEntityMapper.java)
- [TaskRepository.java](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/repository/TaskRepository.java)
- [TaskRepositoryImpl.java](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/repository/TaskRepositoryImpl.java)
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
This document provides comprehensive data model documentation for ShowTime’s monitoring platform. It focuses on the core entities and their relationships: ResourceEntity, DataCenterConfig, Task, and Warn. It also documents the mapping between DTOs and database entities, transformation patterns, validation rules, database schema, indexing, caching strategies, performance considerations, data lifecycle and retention, migration procedures, and common dashboard query patterns.

## Project Structure
The data model spans two modules:
- watcher-agent: domain entities and repositories for task and warn management, plus DTOs for data center configuration.
- watcher-sdk: shared DTOs and MyBatis-Plus entities/mappers for MySQL-backed persistence.

Key locations:
- Entities and DTOs: watcher-agent/src/main/java/com/virtual/cloud/om/agent/{entity,dto}
- SDK entities and mappers: watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/{entity,mapper,dto}
- Database schema: watcher-agent/src/main/resources/schema-mysql.sql

```mermaid
graph TB
subgraph "watcher-agent"
EA_Entity["agent.entity.*"]
EA_DTO["agent.dto.*"]
EA_Repo["agent.repository.*"]
end
subgraph "watcher-sdk"
SDK_Entity["sdk.entity.mysql.*"]
SDK_Mapper["sdk.mapper.*"]
SDK_DTO["sdk.dto.*"]
end
EA_Entity --> EA_Repo
EA_DTO --> EA_Repo
EA_Repo --> SDK_Entity
SDK_Entity --> SDK_Mapper
SDK_DTO --> EA_Entity
```

**Section sources**
- [schema-mysql.sql:1-204](file://watcher-agent/src/main/resources/schema-mysql.sql#L1-L204)

## Core Components
This section defines the core entities and their fields, types, and constraints. It also outlines DTO-to-entity transformations and validation rules.

- ResourceEntity (Agent-side)
  - Purpose: Represents monitored resource metadata (platform, IP, credentials, activation).
  - Fields: platform, id, ipAddress, port, ac, ci, protocol, authType, serverUsername, serverPassword, serverPort, active, createTime, updateTime.
  - Notes: Used primarily for credential and connectivity management.

- DataCenterConfig (Agent-side)
  - Purpose: Stores data center connection credentials and metadata.
  - Fields: id, ip, username, password, port, datacenterType.
  - Constraints: Unique key on (ip, platform) exists in the MySQL schema for resource table; DataCenterConfig is a separate configuration table.

- Task (Agent-side)
  - Purpose: Defines scheduled or ad-hoc tasks for strategy issuance/pull, health checks, and other operations.
  - Fields: id, taskName, description, cycleType, cycleDay, cycleTime, taskType, createdTime, availableStartTime, availableEndTime, hash, data (StrategyDTO), resourceId.
  - Validation: cycleType supports predefined constants; taskType includes constants for strategy issue/pull, health check, filebeat check, watcher warn, and ssh close.

- Warn (Agent-side)
  - Purpose: Tracks latest warning events per resource.
  - Fields: resourceId, eventTime, type, reportTime.

- DTOs
  - TaskDTO (SDK): Mirrors Task fields; includes data as Object for strategy payload.
  - WarnDTO (SDK): Mirrors Warn fields; id is the warning record identifier.

- Transformation patterns
  - Task.convertToDTO(): Copies Task fields to TaskDTO using property copying.
  - ResourceEntity (SDK) vs ResourceEntity (Agent): Agent-side DTOs are annotated for API docs; SDK-side entity is mapped to MySQL table resource_entity.

- Validation rules
  - Task.cycleType and taskType use predefined constants; invalid values should be rejected by callers.
  - DataCenterConfig.username/password are exposed as DTO fields; encryption/decryption handled externally.

**Section sources**
- [ResourceEntity.java:12-52](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/ResourceEntity.java#L12-L52)
- [DataCenterConfig.java:11-38](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/DataCenterConfig.java#L11-L38)
- [Task.java:21-146](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/Task.java#L21-L146)
- [Warn.java:18-36](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/Warn.java#L18-L36)
- [TaskDTO.java:18-67](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/TaskDTO.java#L18-L67)
- [WarnDTO.java:11-28](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/WarnDTO.java#L11-L28)

## Architecture Overview
The data layer follows a layered pattern:
- Domain entities (watcher-agent) encapsulate business logic and DTOs.
- SDK entities and mappers (watcher-sdk) provide MyBatis-Plus mapping to MySQL tables.
- Repositories (watcher-agent) define access contracts; current implementation logs disabled operations for MySQL single-node mode.

```mermaid
classDiagram
class ResourceEntity_Agent {
+String platform
+String id
+String ipAddress
+Integer port
+String ac
+String ci
+String protocol
+String authType
+String serverUsername
+String serverPassword
+Integer serverPort
+Integer active
+String createTime
+String updateTime
}
class ResourceEntity_SDK {
+String id
+String ipAddress
+Integer port
+String protocol
+String authType
+String ac
+String ci
+String platform
+String serverUsername
+String serverPassword
+Integer serverPort
+Integer active
+String createTime
+String updateTime
}
class Task_Agent {
+String id
+String taskName
+String description
+String cycleType
+Integer cycleDay
+String cycleTime
+String taskType
+String createdTime
+Long availableStartTime
+Long availableEndTime
+String hash
+StrategyDTO data
+String resourceId
+convertToDTO() TaskDTO
}
class TaskDTO_SDK {
+String id
+String taskName
+String description
+String cycleType
+Integer cycleDay
+String cycleTime
+String taskType
+String createdTime
+Long availableStartTime
+Long availableEndTime
+String hash
+Object data
+String resourceId
}
class Warn_Agent {
+String resourceId
+Long eventTime
+String type
+Long reportTime
}
class WarnDTO_SDK {
+String id
+Long eventTime
+String type
+Long reportTime
}
ResourceEntity_Agent --> ResourceEntity_SDK : "mapped by MyBatis-Plus"
Task_Agent --> TaskDTO_SDK : "convertToDTO()"
Warn_Agent --> WarnDTO_SDK : "DTO variant"
```

**Diagram sources**
- [ResourceEntity.java:12-52](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/ResourceEntity.java#L12-L52)
- [ResourceEntity.java:13-47](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/entity/mysql/ResourceEntity.java#L13-L47)
- [Task.java:140-144](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/Task.java#L140-L144)
- [TaskDTO.java:18-67](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/TaskDTO.java#L18-L67)
- [Warn.java:18-36](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/Warn.java#L18-L36)
- [WarnDTO.java:11-28](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/WarnDTO.java#L11-L28)

## Detailed Component Analysis

### ResourceEntity
- Agent-side ResourceEntity: API-focused DTO with Swagger annotations for documentation.
- SDK-side ResourceEntity: MyBatis-Plus mapped entity targeting MySQL table resource_entity.
- Mapping: Both share similar fields; SDK entity adds explicit table mapping and ID strategy.

```mermaid
erDiagram
RESOURCE_ENTITY {
varchar id PK
varchar ip_address
int port
varchar protocol
varchar auth_type
varchar ac
varchar ci
varchar platform
varchar server_username
varchar server_password
int server_port
int active
datetime create_time
datetime update_time
}
```

**Diagram sources**
- [schema-mysql.sql:155-176](file://watcher-agent/src/main/resources/schema-mysql.sql#L155-L176)

**Section sources**
- [ResourceEntity.java:12-52](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/ResourceEntity.java#L12-L52)
- [ResourceEntity.java:13-47](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/entity/mysql/ResourceEntity.java#L13-L47)
- [ResourceEntityMapper.java:10-13](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/mapper/ResourceEntityMapper.java#L10-L13)

### DataCenterConfig
- Agent-side DataCenterConfig: Holds data center connection info and type.
- SDK-side: No direct entity; configuration stored in data_center_config table.

```mermaid
erDiagram
DATA_CENTER_CONFIG {
varchar id PK
varchar config_key UK
text config_value
varchar description
datetime create_time
datetime update_time
}
```

**Diagram sources**
- [schema-mysql.sql:122-130](file://watcher-agent/src/main/resources/schema-mysql.sql#L122-L130)

**Section sources**
- [DataCenterConfig.java:11-38](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/DataCenterConfig.java#L11-L38)
- [DataCenterConfigDTO.java:11-26](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/dto/DataCenterConfigDTO.java#L11-L26)

### Task
- Agent-side Task: Business entity with constants for state, type, cycleType, and target object types; includes conversion to TaskDTO.
- Repository contract: TaskRepository defines CRUD-like methods; current implementation logs disabled operations for MySQL single-node mode.

```mermaid
flowchart TD
Start(["Task.convertToDTO()"]) --> CopyProps["Copy properties to TaskDTO"]
CopyProps --> ReturnDTO["Return TaskDTO"]
```

**Diagram sources**
- [Task.java:140-144](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/Task.java#L140-L144)

**Section sources**
- [Task.java:21-146](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/Task.java#L21-L146)
- [TaskRepository.java:12-33](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/repository/TaskRepository.java#L12-L33)
- [TaskRepositoryImpl.java:19-77](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/repository/TaskRepositoryImpl.java#L19-L77)

### Warn
- Agent-side Warn: Tracks latest warning per resource with event and report timestamps.
- SDK-side WarnDTO: Warning record DTO with id as the warning record identifier.

```mermaid
erDiagram
WARN {
varchar id PK
varchar level
varchar title
text content
varchar source
varchar status
datetime warn_time
datetime resolve_time
datetime create_time
datetime update_time
}
```

**Diagram sources**
- [schema-mysql.sql:135-147](file://watcher-agent/src/main/resources/schema-mysql.sql#L135-L147)

**Section sources**
- [Warn.java:18-36](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/Warn.java#L18-L36)
- [WarnDTO.java:11-28](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/WarnDTO.java#L11-L28)

## Dependency Analysis
- Entity-to-DTO mapping:
  - Task.convertToDTO() copies Task to TaskDTO.
  - Warn and WarnDTO are distinct DTOs; no direct conversion method is present.
- Persistence:
  - ResourceEntity (SDK) is mapped via ResourceEntityMapper to resource_entity table.
  - Task and Warn are persisted in MySQL tables task and warn respectively.
- Repository access:
  - TaskRepository defines methods; TaskRepositoryImpl currently logs disabled operations for MySQL single-node mode.

```mermaid
graph LR
Task_Agent["Task (Agent)"] -- "convertToDTO()" --> TaskDTO_SDK["TaskDTO (SDK)"]
Warn_Agent["Warn (Agent)"] -- "DTO variant" --> WarnDTO_SDK["WarnDTO (SDK)"]
ResourceEntity_Agent["ResourceEntity (Agent)"] -. "mapped by" .-> ResourceEntity_SDK["ResourceEntity (SDK)"]
ResourceEntity_SDK -. "Mapper" .-> ResourceEntityMapper["ResourceEntityMapper"]
```

**Diagram sources**
- [Task.java:140-144](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/Task.java#L140-L144)
- [TaskDTO.java:18-67](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/TaskDTO.java#L18-L67)
- [Warn.java:18-36](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/Warn.java#L18-L36)
- [WarnDTO.java:11-28](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/WarnDTO.java#L11-L28)
- [ResourceEntity.java:13-47](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/entity/mysql/ResourceEntity.java#L13-L47)
- [ResourceEntityMapper.java:10-13](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/mapper/ResourceEntityMapper.java#L10-L13)

**Section sources**
- [TaskRepositoryImpl.java:19-77](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/repository/TaskRepositoryImpl.java#L19-L77)

## Performance Considerations
- Indexes
  - operation_log: deleted, time
  - parameter: type+name
  - deploy: status
  - task: status
  - warn: status, level
  - resource: platform, ip_address, usable
  - metric_data: resource_id, metric_type, report_time, platform
- Recommendations
  - Use indexed filters for frequent queries (status, level, platform, ip_address).
  - Batch writes for metrics; avoid row-level contention on hot keys.
  - Consider partitioning metric_data by time for long-term retention.
  - Cache frequently accessed resource configurations keyed by (ip_address, platform).

[No sources needed since this section provides general guidance]

## Troubleshooting Guide
- TaskRepository disabled operations
  - Symptom: All TaskRepository methods log disabled operations and return empty/default values.
  - Cause: Current implementation is adapted for MySQL single-node mode.
  - Action: Implement or replace with MyBatis-Plus repository for full CRUD support.
- Missing mappers
  - Symptom: TaskMapper/WarnMapper not found under watcher-sdk.
  - Action: Add corresponding mapper interfaces extending BaseMapper for task and warn tables.
- Data center credentials exposure
  - Symptom: username/password appear as DTO fields.
  - Action: Enforce encryption/decryption at transport boundaries; validate credentials before persistence.

**Section sources**
- [TaskRepositoryImpl.java:21-29](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/repository/TaskRepositoryImpl.java#L21-L29)
- [TaskRepositoryImpl.java:39-46](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/repository/TaskRepositoryImpl.java#L39-L46)

## Conclusion
The ShowTime monitoring platform employs a clear separation between domain entities (watcher-agent) and SDK-backed persistence (watcher-sdk). ResourceEntity, DataCenterConfig, Task, and Warn form the core data model. DTOs facilitate cross-module communication, with Task providing a conversion method to TaskDTO. The MySQL schema defines robust constraints and indexes for performance. Current repository implementations are simplified for single-node MySQL; production deployments should enable full CRUD via MyBatis-Plus and implement proper caching and retention strategies.

[No sources needed since this section summarizes without analyzing specific files]

## Appendices

### Database Schema Reference
- Tables and indexes
  - sys_user, pwd_strategy, agent_unique_code, deploy, parameter, task, operation_log, data_center_config, warn, resource, metric_data.

```mermaid
erDiagram
SYS_USER {
varchar id PK
varchar username UK
varchar password
datetime last_login_time
datetime create_time
datetime update_time
}
PWD_STRATEGY {
varchar id PK
int min_length
int pwd_complex
int pwd_life_time
datetime create_time
datetime update_time
}
AGENT_UNIQUE_CODE {
varchar id PK
varchar uid UK
datetime create_time
}
DEPLOY {
varchar id PK
varchar name
varchar type
varchar status
varchar host
text config
datetime create_time
datetime update_time
}
PARAMETER {
varchar id PK
varchar type
varchar name
text value
varchar description
datetime create_time
datetime update_time
varchar uk_type_name UK
}
TASK {
varchar id PK
varchar name
varchar type
varchar status
varchar cron_expr
varchar handler_class
text params
datetime create_time
datetime update_time
}
OPERATION_LOG {
varchar id PK
varchar module
varchar operation
varchar operator
varchar result
varchar time
varchar deleted
datetime create_time
}
DATA_CENTER_CONFIG {
varchar id PK
varchar config_key UK
text config_value
varchar description
datetime create_time
datetime update_time
}
WARN {
varchar id PK
varchar level
varchar title
text content
varchar source
varchar status
datetime warn_time
datetime resolve_time
datetime create_time
datetime update_time
}
RESOURCE {
varchar id PK
varchar resource_name
varchar platform
varchar ip_address
int port
varchar protocol
varchar auth_type
varchar ac
varchar ci
varchar server_username
varchar server_password
int server_port
int active
int usable
int remote
datetime end_time
datetime create_time
datetime update_time
varchar uk_ip_platform UK
}
METRIC_DATA {
varchar id PK
varchar resource_id
varchar resource_ip
varchar platform
varchar metric_type
varchar metric_name
varchar metric_value
varchar metric_unit
varchar tags
datetime report_time
datetime create_time
}
```

**Diagram sources**
- [schema-mysql.sql:11-19](file://watcher-agent/src/main/resources/schema-mysql.sql#L11-L19)
- [schema-mysql.sql:28-36](file://watcher-agent/src/main/resources/schema-mysql.sql#L28-L36)
- [schema-mysql.sql:44-49](file://watcher-agent/src/main/resources/schema-mysql.sql#L44-L49)
- [schema-mysql.sql:54-64](file://watcher-agent/src/main/resources/schema-mysql.sql#L54-L64)
- [schema-mysql.sql:69-79](file://watcher-agent/src/main/resources/schema-mysql.sql#L69-L79)
- [schema-mysql.sql:84-95](file://watcher-agent/src/main/resources/schema-mysql.sql#L84-L95)
- [schema-mysql.sql:100-110](file://watcher-agent/src/main/resources/schema-mysql.sql#L100-L110)
- [schema-mysql.sql:122-130](file://watcher-agent/src/main/resources/schema-mysql.sql#L122-L130)
- [schema-mysql.sql:135-147](file://watcher-agent/src/main/resources/schema-mysql.sql#L135-L147)
- [schema-mysql.sql:155-176](file://watcher-agent/src/main/resources/schema-mysql.sql#L155-L176)
- [schema-mysql.sql:185-203](file://watcher-agent/src/main/resources/schema-mysql.sql#L185-L203)

### Sample Data Examples
- ResourceEntity (SDK)
  - Example fields: id, ipAddress, port, protocol, authType, ac, ci, platform, serverUsername, serverPassword, serverPort, active, createTime, updateTime.
- DataCenterConfig (Agent)
  - Example fields: id, ip, username, password, port, datacenterType.
- Task (Agent)
  - Example fields: id, taskName, description, cycleType, cycleDay, cycleTime, taskType, createdTime, availableStartTime, availableEndTime, hash, data (StrategyDTO), resourceId.
- Warn (Agent)
  - Example fields: resourceId, eventTime, type, reportTime.

**Section sources**
- [ResourceEntity.java:13-47](file://watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/entity/mysql/ResourceEntity.java#L13-L47)
- [DataCenterConfig.java:25-38](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/DataCenterConfig.java#L25-L38)
- [Task.java:93-144](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/Task.java#L93-L144)
- [Warn.java:21-36](file://watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/Warn.java#L21-L36)

### Common Query Patterns for Monitoring Dashboards
- Retrieve active resources by platform and usability
  - Filter: platform, usable = 1
  - Index: resource(platform), resource(usable)
- Fetch recent warnings by status and level
  - Filter: status, level
  - Index: warn(status), warn(level)
- Get metrics for a resource within a time window
  - Filter: resource_id, report_time range
  - Index: metric_data(resource_id), metric_data(report_time)
- List tasks by status and type
  - Filter: status, type
  - Index: task(status)

**Section sources**
- [schema-mysql.sql:178-181](file://watcher-agent/src/main/resources/schema-mysql.sql#L178-L181)
- [schema-mysql.sql:149-151](file://watcher-agent/src/main/resources/schema-mysql.sql#L149-L151)
- [schema-mysql.sql:200-203](file://watcher-agent/src/main/resources/schema-mysql.sql#L200-L203)
- [schema-mysql.sql:117](file://watcher-agent/src/main/resources/schema-mysql.sql#L117)

### Data Lifecycle Management and Retention
- Retention policy recommendations
  - Keep raw metrics for 90–180 days; archive older entries.
  - Purge resolved warnings after 30 days; maintain audit trail for 1 year.
  - Rotate operation logs by quarter; compress historical partitions.
- Migration procedures
  - Schema updates: wrap DDL in migrations with pre/post validations.
  - Data migrations: batch process with progress tracking and rollback steps.
  - Backups: snapshot-based backups with point-in-time recovery enabled.

[No sources needed since this section provides general guidance]