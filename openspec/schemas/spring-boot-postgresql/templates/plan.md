# Plan

## Execution Mode
**REQUIRED**: Use `superpowers:subagent-driven-development` skill.
**DO NOT** use `executing-plans` or `inline execution`.

---

## Step 1: RED - 编写 UserRepository 测试

**任务 ID**: Feature: 用户管理 - RED

**测试文件**: `src/test/java/com/example/project/repository/UserRepositoryTest.java`

**断言内容**:
```java
@Test
void findById_existingUser_returnsUser() {
    // GIVEN 数据库预置数据
    // WHEN repository.findById(1)
    // THEN 返回 Optional 包含 User
}

@Test
void findById_nonExisting_returnsEmpty() {
    // WHEN repository.findById(999)
    // THEN 返回 Optional.empty()
}
```

**预期失败原因**: 编译错误（UserRepository 尚不存在）

**验证命令**: `./mvnw test -Dtest=UserRepositoryTest`

**交付物**: 测试失败输出日志

---

## Step 2: GREEN - 实现 UserRepository

**任务 ID**: Feature: 用户管理 - GREEN

**实现文件**: `src/main/java/com/example/project/repository/UserRepository.java`

**最小实现**:
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
```

**验证命令**: `./mvnw test -Dtest=UserRepositoryTest`

**交付物**: 测试通过输出日志

---

## Step 3: RED - 编写 UserService 测试

**任务 ID**: Feature: 用户服务 - RED

**测试文件**: `src/test/java/com/example/project/service/UserServiceTest.java`

**断言内容**:
```java
@Test
void createUser_validRequest_returnsUserResponse() {
    // GIVEN 有效的 UserCreateRequest
    // WHEN userService.createUser(request)
    // THEN 返回 UserResponse，包含生成的 ID
}

@Test
void getUserById_nonExisting_throwsException() {
    // WHEN userService.getUserById(999)
    // THEN 抛出 UserNotFoundException
}
```

**预期失败原因**: UserService 尚未实现

**验证命令**: `./mvnw test -Dtest=UserServiceTest`

**交付物**: 测试失败输出日志

---

## Step 4: GREEN - 实现 UserService

**任务 ID**: Feature: 用户服务 - GREEN

**实现文件**:
- `src/main/java/com/example/project/service/UserService.java`
- `src/main/java/com/example/project/service/impl/UserServiceImpl.java`

**验证命令**: `./mvnw test -Dtest=UserServiceTest`

**交付物**: 测试通过输出日志

---

## Step 5: RED - 编写 UserController 测试

**任务 ID**: Feature: 用户控制器 - RED

**测试文件**: `src/test/java/com/example/project/controller/UserControllerTest.java`

**断言内容**:
```java
@Test
void createUser_validRequest_returns201() {
    // WHEN mockMvc.perform(post("/api/users"))
    // THEN status 201, content UserResponse
}

@Test
void createUser_invalidRequest_returns400() {
    // WHEN mockMvc.perform(post("/api/users").content("{}"))
    // THEN status 400, content 错误详情
}
```

**预期失败原因**: UserController 尚未实现

**验证命令**: `./mvnw test -Dtest=UserControllerTest`

**交付物**: 测试失败输出日志

---

## Step 6: GREEN - 实现 UserController

**任务 ID**: Feature: 用户控制器 - GREEN

**实现文件**: `src/main/java/com/example/project/controller/UserController.java`

**验证命令**: `./mvnw test -Dtest=UserControllerTest`

**交付物**: 测试通过输出日志

---

## Final Verification

```bash
# 运行完整测试套件
./mvnw test

# 验证覆盖率
./mvnw test jacoco:report
open target/site/jacoco/index.html
```

---

## Execution Mode Selection
**REQUIRED**: Use `superpowers:subagent-driven-development` skill.
**DO NOT** use `executing-plans` or `inline execution`.