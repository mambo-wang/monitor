# Spring Boot + PostgreSQL OpenSpec Schema

基于 TDD 的 Spring Boot + PostgreSQL 项目开发工作流模板。

## 目录结构

```
spring-boot-postgresql/
├── schema.yaml          # 工作流定义
└── templates/
    ├── proposal.md      # 变更提案模板
    ├── spec.md          # 行为规格模板
    ├── design.md        # 技术设计模板
    ├── tasks.md         # TDD 任务模板
    └── plan.md          # 执行计划模板
```

## 工作流程

```
proposal → specs → design → tasks → plans → apply
```

每个阶段都是前一个阶段的输入，最终通过 `apply` 执行。

## 使用方式

```bash
# 创建新功能变更
cd your-project
openspec new spring-boot-postgresql --name "add-user-management"
```

## 模板说明

### proposal.md
用 WHEN/THEN 格式描述每个可测试行为。

### spec.md
用 GIVEN/WHEN/THEN 格式编写场景化行为规格。

### design.md
包含：
- Package 结构
- 文件映射表
- 数据库 Schema
- 测试策略

### tasks.md
原子化 TDD 任务列表，每个任务包含：
- RED: 编写失败测试
- GREEN: 最小实现
- REFACTOR: 重构（可选）

### plan.md
详细执行计划，每个步骤对应 tasks.md 中的一个任务。

## 适用场景

- Spring Boot Web 应用
- PostgreSQL 数据库
- JPA / Spring Data JPA
- MyBatis / MyBatis-Plus
- Flyway / Liquibase 迁移
- JUnit5 + Mockito 测试

## 测试命令

```bash
# 单元测试
./mvnw test

# 集成测试
./mvnw verify -DskipTests=false

# 覆盖率
./mvnw test jacoco:report
```

## 扩展

如需自定义，可修改：
- `schema.yaml` 中的 artifacts 和 apply 规则
- `templates/` 下的各个模板文件