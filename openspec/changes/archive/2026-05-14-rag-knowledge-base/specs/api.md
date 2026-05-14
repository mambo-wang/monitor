# Spec: 知识库 API

## Scenarios

### Scenario 1: 创建知识库
- GIVEN: 用户请求创建一个新知识库
- WHEN: POST `/api/knowledge/kbs` with `{"name": "产品文档", "description": "产品手册"}`
- THEN: 返回 201 状态码，知识库 ID 已生成，状态为 idle

### Scenario 2: 获取知识库列表
- GIVEN: 系统中有多个知识库
- WHEN: GET `/api/knowledge/kbs`
- THEN: 返回 200 状态码，包含所有知识库信息列表

### Scenario 3: 获取不存在的知识库
- GIVEN: 请求获取一个不存在的知识库
- WHEN: GET `/api/knowledge/kbs/{invalid_id}`
- THEN: 返回 404 状态码，错误信息 "Knowledge base not found"

### Scenario 4: 删除知识库
- GIVEN: 知识库存在
- WHEN: DELETE `/api/knowledge/kbs/{id}`
- THEN: 返回 200 状态码，知识库及其文档被删除，ChromaDB collection 被删除

### Scenario 5: 构建知识库
- GIVEN: 知识库已上传文档
- WHEN: POST `/api/knowledge/kbs/{id}/build`
- THEN: 返回 200 状态码，状态变为 building，文档被向量化存入 ChromaDB

### Scenario 6: 构建空知识库
- GIVEN: 知识库没有任何文档
- WHEN: POST `/api/knowledge/kbs/{id}/build`
- THEN: 返回 400 状态码，错误信息 "No documents to build"

### Scenario 7: 上传不支持的文档格式
- GIVEN: 用户上传 .exe 文件
- WHEN: POST `/api/knowledge/kbs/{id}/documents` with .exe file
- THEN: 返回 400 状态码，错误信息 "Unsupported file format"

### Scenario 8: RAG 问答 - 知识库为空
- GIVEN: 知识库尚未构建
- WHEN: POST `/api/knowledge/chat` with `{"kb_id": "xxx", "question": "产品有哪些功能"}`
- THEN: 返回 400 状态码，错误信息 "Knowledge base is empty, please build first"
