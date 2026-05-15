# 🤖 AI Agent 工程师学习笔记

> 本笔记涵盖 Day 1-4 的核心知识点与代码示例，记录从 Python 高级特性到 FastAPI Web 服务的学习过程。

---

## 📅 Day 1 - Python 装饰器（Decorators）

### 1.1 什么是装饰器？

装饰器是一种**设计模式**，用于在不修改原函数的情况下，给函数添加额外功能。

**核心原理：** 装饰器是一个接收函数作为参数的函数，返回一个新函数。

```python
def my_decorator(func):
    def wrapper():
        print("在原函数之前执行")  # 前置逻辑
        func()                      # 调用原函数
        print("在原函数之后执行")  # 后置逻辑
    return wrapper

@my_decorator
def say_hello():
    print("Hello!")

# 调用时会输出：
# 在原函数之前执行
# Hello!
# 在原函数之后执行
```

### 1.2 带参数的装饰器

如果装饰器需要参数，需要再包一层：

```python
def repeat(num_times):
    def decorator(func):
        def wrapper(*args, **kwargs):
            for _ in range(num_times):
                func(*args, **kwargs)
        return wrapper
    return decorator

@repeat(3)
def say_hello():
    print("Hello!")

say_hello()  # Hello! 会输出3次
```

### 1.3 @functools.wraps 保留原函数信息

使用装饰器后，函数的元信息（`__name__`、`__doc__`）会被替换。`@functools.wraps` 解决这个问题：

```python
import functools
import time

def measure_time(func):
    @functools.wraps(func)  # 保留原函数的元信息
    def wrapper(*args, **kwargs):
        before = time.time()
        result = func(*args, **kwargs)
        after = time.time()
        elapsed = after - before
        print(f"函数执行了 {elapsed:.3f} 秒")
        return result
    return wrapper
```

### 1.4 装饰器使用场景

| 场景 | 示例 |
|------|------|
| 日志记录 | `@log` 记录函数调用 |
| 性能计时 | `@measure_time` 计算执行时间 |
| 权限校验 | `@require_login` 检查登录状态 |
| 缓存 | `@cache` 缓存结果 |

---

## 📅 Day 2 - Pydantic 数据验证

### 2.1 为什么需要 Pydantic？

Pydantic 是一个**数据验证库**，用于：
- 自动验证数据类型
- 提供清晰的错误信息
- 支持复杂的数据模型

### 2.2 基础用法 - BaseModel

```python
from pydantic import BaseModel

class User(BaseModel):
    name: str
    age: int
    email: str

# 自动验证类型
user = User(name="张三", age=25, email="zhangsan@example.com")
print(user.model_dump())
```

### 2.3 字段约束 - Field

```python
from pydantic import BaseModel, Field

class Order(BaseModel):
    order_id: str
    amount: float
    items: list[str] = Field(default_factory=list)
    discount: Optional[float] = None
```

### 2.4 自定义验证器 - @field_validator

```python
from pydantic import BaseModel, field_validator

class User(BaseModel):
    name: str
    age: int
    email: str

    @field_validator('age')
    @classmethod
    def age_must_be_positive(cls, v):
        if v <= 0:
            raise ValueError('年龄必须大于0')
        return v

    @field_validator('email')
    @classmethod
    def email_format(cls, v):
        if '@' not in v:
            raise ValueError('邮箱格式不正确')
        return v

# 验证失败示例
# user = User(name="李四", age=-5, email="invalid")
# ValueError: 年龄必须大于0
```

### 2.5 Pydantic 在 FastAPI 中的应用

```python
from pydantic import BaseModel
from typing import Optional

class User(BaseModel):
    name: str
    age: int
    email: Optional[str] = None

@app.post("/user")
def create_user(user: User):
    return {"message": f"用户 {user.name} 创建成功", "data": user}
```

### 2.6 常见字段类型

| 类型 | 说明 |
|------|------|
| `str` | 字符串 |
| `int` | 整数 |
| `float` | 浮点数 |
| `bool` | 布尔值 |
| `list[str]` | 字符串列表 |
| `Optional[str]` | 可选字符串 |
| `dict` | 字典 |

---

## 📅 Day 3 - FastAPI 基础 & async/await

### 3.1 FastAPI 简介

FastAPI 是一个现代、快速的 Python Web 框架，支持：
- 自动生成 API 文档
- 类型提示验证
- 异步支持
- 高性能

### 3.2 基础路由

```python
from fastapi import FastAPI

app = FastAPI()

@app.get("/")
def read_root():
    return {"message": "你好，FastAPI！"}

@app.get("/hello/{name}")
def say_hello(name: str):
    return {"message": f"Hello, {name}!"}
```

### 3.3 同步 vs 异步

**同步函数：** 传统函数，执行时阻塞

```python
@app.get("/sync")
def sync_endpoint():
    return {"type": "同步"}
```

**异步函数：** 使用 `async/await`，适合 I/O 密集型操作

```python
import asyncio

@app.get("/async")
async def async_endpoint():
    await asyncio.sleep(1)  # 模拟异步操作（如调用外部API）
    return {"type": "异步", "delay": "1秒"}
```

### 3.4 async/await 核心概念

```python
import asyncio

async def fetch_data():
    # 模拟异步操作
    await asyncio.sleep(1)
    return "数据"

async def main():
    result = await fetch_data()  # 等待异步操作完成
    print(result)

# 运行异步函数
asyncio.run(main())
```

### 3.5 查询参数与路径参数

**路径参数：**  URL 中的参数

```python
@app.get("/items/{item_id}")
def read_item(item_id: int):
    return {"item_id": item_id}
```

**查询参数：** `?key=value` 形式的参数

```python
@app.get("/search")
def search(q: str, limit: int = 10):
    return {"query": q, "limit": limit}
```

### 3.6 请求体（Body）

```python
from pydantic import BaseModel

class PowerRequest(BaseModel):
    base: float
    exp: float

@app.post("/power")
def power(request: PowerRequest):
    result = request.base ** request.exp
    return {"result": result}
```

### 3.7 FastAPI 启动方式

```bash
# 方式1：直接运行
python3 day3_fastapi.py

# 方式2：使用 uvicorn
uvicorn main:app --reload

# 访问文档
# http://localhost:8000/docs  (Swagger UI)
# http://localhost:8000/redoc  (ReDoc)
```

---

## 📅 Day 4 - FastAPI WebSocket

### 4.1 WebSocket vs HTTP

| 特性 | HTTP | WebSocket |
|------|------|-----------|
| 通信模式 | 请求-响应 | 双向实时 |
| 连接方式 | 短连接 | 长连接 |
| 服务器推送 | ❌ 不支持 | ✅ 支持 |
| 适用场景 | REST API | 聊天、实时数据 |

### 4.2 WebSocket 基础 - Echo 服务器

```python
from fastapi import FastAPI, WebSocket
import uvicorn

app = FastAPI()

@app.websocket("/ws")
async def websocket_echo(websocket: WebSocket):
    await websocket.accept()  # 接受连接
    try:
        while True:
            data = await websocket.receive_text()  # 接收消息
            await websocket.send_text(f"Echo: {data}")  # 发送消息
    except Exception as e:
        print(f"连接异常: {e}")
```

### 4.3 连接管理器 - ConnectionManager

为了支持多人聊天，需要管理多个连接：

```python
class ConnectionManager:
    def __init__(self):
        self.active_connections: Dict[str, WebSocket] = {}
    
    async def connect(self, websocket: WebSocket, user_id: str):
        await websocket.accept()
        self.active_connections[user_id] = websocket
    
    def disconnect(self, user_id: str):
        if user_id in self.active_connections:
            del self.active_connections[user_id]
    
    async def broadcast(self, message: str):
        """广播消息给所有客户端"""
        for user_id, websocket in self.active_connections.items():
            await websocket.send_text(message)
```

### 4.4 聊天室完整示例

```python
from fastapi import FastAPI, WebSocket, WebSocketDisconnect

app = FastAPI()
manager = ConnectionManager()

@app.websocket("/ws/{user_name}")
async def websocket_chat(websocket: WebSocket, user_name: str):
    user_id = str(uuid.uuid4())[:8]
    await manager.connect(websocket, user_id, user_name)
    
    # 广播用户加入
    await manager.broadcast(f"{user_name} 加入了聊天室")
    
    try:
        while True:
            data = await websocket.receive_text()
            await manager.broadcast(f"{user_name}: {data}")
    except WebSocketDisconnect:
        manager.disconnect(user_id)
        await manager.broadcast(f"{user_name} 离开了聊天室")
```

### 4.5 前端 HTML/JavaScript 客户端

```html
<script>
    const ws = new WebSocket("ws://localhost:8000/ws/用户名");
    
    ws.onopen = function() {
        console.log("连接已建立");
    };
    
    ws.onmessage = function(event) {
        console.log("收到消息:", event.data);
    };
    
    // 发送消息
    ws.send("Hello!");
</script>
```

### 4.6 运行 WebSocket 聊天室

```bash
cd python-study/day4_websocket
python3 websocket_chat_room.py
# 浏览器打开 http://localhost:8000
# 打开多个标签页测试多人聊天
```

---

## 🛠️ 环境配置

### 安装依赖

```bash
pip install fastapi uvicorn pydantic
```

### 快速运行

```bash
# WebSocket 聊天室
cd python-study/day4_websocket
python3 websocket_chat_room.py

# FastAPI REST API
cd python-study
python3 day3_fastapi.py
```

---

## 📊 学习进度总结

| Day | 主题 | 状态 | 关键技能 |
|-----|------|------|----------|
| Day 1 | Python 装饰器 | ✅ | 函数式编程、AOP |
| Day 2 | Pydantic 数据验证 | ✅ | 数据建模、验证 |
| Day 3 | FastAPI 基础 | ✅ | REST API、async |
| Day 4 | WebSocket | ✅ | 实时通信、连接管理 |

---

## 📚 延伸学习资源

- [FastAPI 官方文档](https://fastapi.tiangolo.com/zh/)
- [Pydantic 官方文档](https://docs.pydantic.dev/)
- [WebSocket 协议说明](https://developer.mozilla.org/zh-CN/docs/Web/API/WebSocket)

---

## 🔜 接下来的学习

- Day 5: SQL 高级查询
- Day 6: Redis 缓存实战
- Day 7: 向量数据库概述

---

*本笔记由 AI Agent 代码助手生成*
