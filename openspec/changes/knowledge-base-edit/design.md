# Design

## File Structure

```
watcher-ai/src/watcher_ai/
├── services/
│   ├── kb_repository.py    # [修改] 新增 update() 方法
│   ├── kb_service.py       # [修改] 新增 update() 封装
│   └── document_store.py   # [修改] 改为扫描 uploads 目录

watcher-ai/src/watcher_ai/api/
└── knowledge.py            # [修改] 新增 PATCH 端点，修改文档列表逻辑

watcher-ai/tests/
├── test_kb_service.py      # [新增] 知识库服务测试
└── test_knowledge_api.py   # [新增] 知识库 API 测试
```

## Implementation Details

### 1. 知识库编辑功能

**KBRepository** (`kb_repository.py`)
```python
@staticmethod
def update(kb_id: str, name: str = None, description: str = None) -> bool:
    """更新知识库的名称和描述"""
    # 只有 name 时: UPDATE ... SET name = %s, updated_at = %s WHERE id = %s
    # 只有 description 时: UPDATE ... SET description = %s, updated_at = %s WHERE id = %s
    # 两者都有时: UPDATE ... SET name = %s, description = %s, updated_at = %s WHERE id = %s
```

**KBService** (`kb_service.py`)
```python
@staticmethod
def update(kb_id: str, name: str = None, description: str = None) -> bool:
    """更新知识库"""
    return KBRepository.update(kb_id, name, description)
```

**API Endpoint** (`knowledge.py`)
```python
@router.patch("/kbs/{kb_id}")
def update_kb(kb_id: str, req: KnowledgeBaseUpdate):
    if not KBService.get_by_id(kb_id):
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    KBService.update(kb_id, req.name, req.description)
    return ok_response(KBService.get_by_id(kb_id))
```

### 2. 文档列表持久化

**DocumentStore** (`document_store.py`)
```python
@staticmethod
def list_by_kb(kb_id: str) -> List[dict]:
    """扫描 uploads/{kb_id} 目录获取文档列表"""
    kb_id_safe = kb_id.replace("-", "_")
    kb_dir = os.path.join(UPLOAD_DIR, kb_id_safe)
    if not os.path.exists(kb_dir):
        return []

    docs = []
    for filename in os.listdir(kb_dir):
        file_path = os.path.join(kb_dir, filename)
        if os.path.isfile(file_path):
            # 从 ChromaDB 查询 chunk_count
            chunk_count = ChromaService.count_by_file(kb_id, filename)
            docs.append({
                "kb_id": kb_id,
                "file_name": filename,
                "file_path": file_path,
                "file_size": os.path.getsize(file_path),
                "status": "parsed" if chunk_count > 0 else "pending",
                "chunk_count": chunk_count,
                "created_at": datetime.fromtimestamp(os.path.getctime(file_path))
            })
    return docs
```

**ChromaService** 需要新增方法：
```python
@staticmethod
def count_by_file(kb_id: str, file_name: str) -> int:
    """查询指定文件在 ChromaDB 中的块数量"""
```

## Test Strategy

| Test File | 测试策略 | 测试内容 |
|-----------|----------|----------|
| `test_kb_service.py` | 单元测试 | `update()` 方法：正常更新、只更新 name、只更新 description、KB 不存在 |
| `test_knowledge_api.py` | 集成测试 | `PATCH /kbs/{kb_id}` 端点：成功更新、KB 不存在返回 404、`GET /documents` 服务重启后列表一致 |

### Test Commands

```bash
# 运行所有测试
cd watcher-ai && python -m pytest tests/test_kb_service.py tests/test_knowledge_api.py -v

# 运行单个测试文件
cd watcher-ai && python -m pytest tests/test_kb_service.py -v
```