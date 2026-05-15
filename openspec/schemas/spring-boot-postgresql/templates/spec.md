# Specs

## 1. Repository Layer
<!-- Entity 持久化行为规格 -->

### findById
```gherkin
GIVEN 数据库中存在 id=1 的 User 记录
WHEN userRepository.findById(1) 被调用
THEN 返回 Optional<User> 包含该记录

GIVEN 数据库中不存在 id=999 的记录
WHEN userRepository.findById(999) 被调用
THEN 返回 Optional.empty()
```

### save
```gherkin
GIVEN 有效的 User 实体（无 id）
WHEN userRepository.save(user) 被调用
THEN 返回包含生成主键的实体

GIVEN User 实体的 name 字段为 null
WHEN userRepository.save(user) 被调用
THEN 抛出 DataIntegrityViolationException 或 ConstraintViolationException
```

## 2. Service Layer
<!-- 业务逻辑行为规格 -->

### createUser
```gherkin
GIVEN 合法的 UserCreateRequest（含 name, email）
WHEN userService.createUser(request) 被调用
THEN 创建用户并返回 UserResponse

GIVEN email 已被其他用户占用
WHEN userService.createUser(request) 被调用
THEN 抛出 UserAlreadyExistsException
```

### getUserById
```gherkin
GIVEN 用户 ID=1 存在
WHEN userService.getUserById(1) 被调用
THEN 返回 UserResponse

GIVEN 用户 ID=999 不存在
WHEN userService.getUserById(999) 被调用
THEN 抛出 UserNotFoundException
```

## 3. Controller Layer
<!-- HTTP 接口行为规格 -->

### POST /api/users
```gherkin
GIVEN 合法的用户创建请求
WHEN POST /api/users 请求发送
THEN HTTP 201 Created，响应体包含用户信息

GIVEN 非法的请求体（name 为空）
WHEN POST /api/users 请求发送
THEN HTTP 400 Bad Request，响应体包含校验错误详情
```

### GET /api/users/{id}
```gherkin
GIVEN 用户 ID=1 存在
WHEN GET /api/users/1 请求发送
THEN HTTP 200 OK，响应体包含用户信息 JSON

GIVEN 用户 ID=999 不存在
WHEN GET /api/users/999 请求发送
THEN HTTP 404 Not Found
```

## 4. Edge Cases
<!-- 边界情况 -->

- 分页边界：page=0, page=10000, size=-1
- 字符串长度：name 超过 255 字符
- 特殊字符：name 包含 emoji、SQL 注入字符串
- 并发问题：同一用户快速多次创建