# Specs

## Scenarios

### Scenario 1: 更新知识库名称
- **GIVEN**: 存在一个知识库 KB1，名称为 "旧名称"
- **WHEN**: 调用 `PATCH /api/knowledge/kbs/KB1` 请求体为 `{"name": "新名称"}`
- **THEN**: KB1 的名称更新为 "新名称"，描述保持不变

### Scenario 2: 更新知识库描述
- **GIVEN**: 存在一个知识库 KB1，描述为 "旧描述"
- **WHEN**: 调用 `PATCH /api/knowledge/kbs/KB1` 请求体为 `{"description": "新描述"}`
- **THEN**: KB1 的描述更新为 "新描述"，名称保持不变

### Scenario 3: 同时更新名称和描述
- **GIVEN**: 存在一个知识库 KB1，名称为 "旧名称"，描述为 "旧描述"
- **WHEN**: 调用 `PATCH /api/knowledge/kbs/KB1` 请求体为 `{"name": "新名称", "description": "新描述"}`
- **THEN**: KB1 的名称和描述同时更新

### Scenario 4: 更新不存在的知识库
- **GIVEN**: 不存在知识库 KB_NotExist
- **WHEN**: 调用 `PATCH /api/knowledge/kbs/KB_NotExist` 请求体为 `{"name": "新名称"}`
- **THEN**: 返回 404 错误，响应体为 `{"detail": "Knowledge base not found"}`

### Scenario 5: 获取文档列表-目录有文件
- **GIVEN**: 知识库 KB1 的 uploads/KB1/ 目录下存在文件 `doc1.pdf` 和 `doc2.md`
- **WHEN**: 调用 `GET /api/knowledge/kbs/KB1/documents`
- **THEN**: 返回包含 doc1.pdf 和 doc2.md 的列表，每个文档包含 kb_id、file_name、file_path、file_size、status、chunk_count、created_at

### Scenario 6: 获取文档列表-目录为空
- **GIVEN**: 知识库 KB1 的 uploads/KB1/ 目录下没有文件
- **WHEN**: 调用 `GET /api/knowledge/kbs/KB1/documents`
- **THEN**: 返回空列表 `[]`

### Scenario 7: 获取文档列表-目录不存在
- **GIVEN**: 知识库 KB1 的 uploads/KB1/ 目录不存在
- **WHEN**: 调用 `GET /api/knowledge/kbs/KB1/documents`
- **THEN**: 返回空列表 `[]`

### Scenario 8: 文档列表服务重启后持久化
- **GIVEN**: 知识库 KB1 的 uploads/KB1/ 目录下存在文件 `doc1.pdf`，chunk_count 为 5
- **WHEN**: Python RAG 服务重启后调用 `GET /api/knowledge/kbs/KB1/documents`
- **THEN**: 返回的文档列表与服务重启前一致，doc1.pdf 的 chunk_count 为 5

### Scenario 9: 文档 chunk_count 从 ChromaDB 查询
- **GIVEN**: ChromaDB 中 kb_ KB1 collection 存储了 doc1.pdf 的 5 个 chunk
- **WHEN**: 调用 `GET /api/knowledge/kbs/KB1/documents`
- **THEN**: 返回的 doc1.pdf 文档的 chunk_count 为 5，status 为 "parsed"

### Scenario 10: 未构建的文档 status 为 pending
- **GIVEN**: 知识库 KB1 的 uploads/KB1/ 目录下存在文件 `doc1.pdf`，但 ChromaDB 中无相关 chunk
- **WHEN**: 调用 `GET /api/knowledge/kbs/KB1/documents`
- **THEN**: 返回的 doc1.pdf 文档的 chunk_count 为 0，status 为 "pending"