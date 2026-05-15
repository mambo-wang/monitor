# Tasks

## Workflow
每个功能必须完成以下 TDD 循环：
1. RED: 编写失败测试
2. GREEN: 最小实现使测试通过
3. REFACTOR: 重构优化代码

---

## Feature: 用户管理

### [ ] RED: 编写 UserRepository 测试
- **测试内容**：验证 `findById` 和 `save` 方法行为
- **测试文件**：`src/test/java/com/example/project/repository/UserRepositoryTest.java`
- **验证命令**：`./mvnw test -Dtest=UserRepositoryTest`

### [ ] GREEN: 实现 UserRepository
- **实现文件**：`src/main/java/com/example/project/repository/UserRepository.java`
- **依赖**：JPA Entity 定义
- **验证命令**：`./mvnw test -Dtest=UserRepositoryTest`

### [ ] REFACTOR: 重构 UserRepository（可选）
- 优化查询方法命名
- 添加自定义查询方法

---

## Feature: 用户服务

### [ ] RED: 编写 UserService 单元测试
- **测试内容**：验证 `createUser` 和 `getUserById` 业务逻辑
- **测试文件**：`src/test/java/com/example/project/service/UserServiceTest.java`
- **验证命令**：`./mvnw test -Dtest=UserServiceTest`

### [ ] GREEN: 实现 UserService
- **实现文件**：`src/main/java/com/example/project/service/impl/UserServiceImpl.java`
- **依赖**：UserRepository
- **验证命令**：`./mvnw test -Dtest=UserServiceTest`

---

## Feature: 用户控制器

### [ ] RED: 编写 UserController 测试
- **测试内容**：验证 HTTP 请求/响应行为
- **测试文件**：`src/test/java/com/example/project/controller/UserControllerTest.java`
- **验证命令**：`./mvnw test -Dtest=UserControllerTest`

### [ ] GREEN: 实现 UserController
- **实现文件**：`src/main/java/com/example/project/controller/UserController.java`
- **依赖**：UserService, DTO 类
- **验证命令**：`./mvnw test -Dtest=UserControllerTest`

---

## Feature: 数据库迁移

### [ ] RED: 编写 Flyway 迁移测试（可选）
- **测试内容**：验证数据库 schema 迁移正确性
- **测试文件**：`src/test/java/com/example/project/migration/FlywayMigrationTest.java`

### [ ] GREEN: 创建 Flyway 迁移脚本
- **迁移文件**：`src/main/resources/db/migration/V1__create_users_table.sql`
- **验证命令**：`./mvnw flyway:migrate`

---

## Completion Criteria

- [ ] 所有 RED 测试都已通过
- [ ] 代码符合 design.md 中的结构
- [ ] 测试覆盖率 ≥ 80%
- [ ] 无重大代码异味