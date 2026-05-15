# Design

## Package Structure
```
com.example.project
├── entity/           # JPA Entity
│   ├── User.java
│   └── BaseEntity.java
├── repository/        # Spring Data JPA Repository
│   ├── UserRepository.java
│   └── JpaSpecificationExecutor
├── service/          # Service Layer
│   ├── UserService.java
│   └── impl/
│       └── UserServiceImpl.java
├── controller/       # REST Controller
│   ├── UserController.java
│   └── BaseController.java
├── dto/              # Data Transfer Object
│   ├── UserCreateRequest.java
│   ├── UserUpdateRequest.java
│   └── UserResponse.java
├── vo/               # View Object
│   └── UserDetailVO.java
├── mapper/           # MyBatis-Plus Mapper (可选)
│   └── UserMapper.java
├── exception/        # 自定义异常
│   ├── UserNotFoundException.java
│   └── GlobalExceptionHandler.java
└── config/           # 配置类
    └── JpaConfig.java
```

## File Mapping

| 源码文件 | 测试文件 |
|---------|---------|
| entity/User.java | repository/UserRepositoryTest.java |
| service/UserServiceImpl.java | service/UserServiceTest.java |
| controller/UserController.java | controller/UserControllerTest.java |

## Database Schema

```sql
-- users 表
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX idx_users_email ON users(email);
```

## Test Strategy

| 测试类型 | 工具 | 说明 |
|---------|-----|------|
| Repository 测试 | @DataJpaTest | 使用 H2 内存数据库测试 JPA 操作 |
| Service 单元测试 | JUnit5 + Mockito | 测试业务逻辑，不涉及数据库 |
| Controller 测试 | @WebMvcTest + MockMvc | 测试 HTTP 层，Mock Service |
| 集成测试 | Testcontainers | 使用真实 PostgreSQL 测试 |

## Test Commands

```bash
# 运行所有测试
./mvnw test

# 运行单元测试
./mvnw test -Dtest="*Test"

# 运行集成测试
./mvnw verify -DskipTests=false

# 生成覆盖率报告
./mvnw test jacoco:report
```