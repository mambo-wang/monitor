# Design

## 技术栈

参考项目使用以下技术栈，本项目保持一致：
- **Embedding**: Ollama 本地部署 bge-m3 模型
- **向量数据库**: ChromaDB (持久化存储)
- **文档处理**: LangChain (PDF/Markdown/TXT 加载、文本分割)
- **LLM**: MiniMax API (MiniMax-M2.7)
- **后端**: Python FastAPI
- **前端**: Vue 3 + TypeScript + Element Plus

## File Structure

### 后端 (watcher-ai/)

```
watcher-ai/
├── src/
│   ├── __init__.py
│   ├── main.py              # FastAPI 入口
│   ├── config.py            # 配置管理
│   ├── api/
│   │   ├── __init__.py
│   │   └── knowledge.py     # 知识库 API 路由
│   ├── models/
│   │   ├── __init__.py
│   │   └── schemas.py      # Pydantic 模型
│   ├── services/
│   │   ├── __init__.py
│   │   ├── chroma_service.py    # ChromaDB 操作
│   │   ├── document_service.py   # 文档处理
│   │   └── llm_service.py        # LLM 调用
│   └── views/
│       └── __init__.py
├── uploads/                 # 文档上传目录
├── chroma_db/              # ChromaDB 数据目录
├── requirements.txt
└── .env.example
```

### 前端 (watcher-web/)

```
watcher-web/src/
├── api/
│   └── knowledge.ts         # 知识库 API 调用
└── views/main/knowledge/
    ├── KnowledgeLibrary.vue # 知识库列表页
    ├── DocumentManage.vue   # 文档管理页
    └── ChatAssistant.vue    # RAG 问答页
```

## Test Strategy

### 后端单元测试 (pytest)
- `tests/test_knowledge_api.py` - API 接口测试 (集成测试)
- `tests/test_document_service.py` - 文档服务测试
- `tests/test_chroma_service.py` - ChromaDB 服务测试

### 测试运行命令
```bash
cd watcher-ai
pytest tests/ -v
```

## API 设计

### 知识库管理
| Method | Endpoint | 描述 |
|--------|----------|------|
| POST | `/api/knowledge/kbs` | 创建知识库 |
| GET | `/api/knowledge/kbs` | 获取知识库列表 |
| GET | `/api/knowledge/kbs/{id}` | 获取知识库详情 |
| DELETE | `/api/knowledge/kbs/{id}` | 删除知识库 |
| POST | `/api/knowledge/kbs/{id}/build` | 构建知识库 |
| GET | `/api/knowledge/kbs/{id}/stats` | 获取统计信息 |

### 文档管理
| Method | Endpoint | 描述 |
|--------|----------|------|
| POST | `/api/knowledge/kbs/{id}/documents` | 上传文档 |
| GET | `/api/knowledge/kbs/{id}/documents` | 获取文档列表 |
| GET | `/api/knowledge/kbs/{id}/documents/{doc_id}` | 获取文档详情 |
| DELETE | `/api/knowledge/kbs/{id}/documents/{doc_id}` | 删除文档 |

### RAG 问答
| Method | Endpoint | 描述 |
|--------|----------|------|
| POST | `/api/knowledge/chat` | RAG 问答 |
| POST | `/api/knowledge/kbs/{id}/search` | 检索知识库 |

## 数据模型

### KnowledgeBase (知识库)
```python
id: str              # UUID
name: str            # 名称
description: str     # 描述
status: str          # idle/building/ready/error
document_count: int  # 文档数
chunk_count: int     # 块数
created_at: datetime
updated_at: datetime
```

### Document (文档)
```python
id: str              # UUID
kb_id: str           # 知识库ID
file_name: str       # 文件名
file_path: str       # 文件路径
file_size: int       # 文件大小
status: str          # pending/parsed/error
chunk_count: int     # 块数
created_at: datetime
```

## ChromaDB 集合设计

每个知识库对应一个 ChromaDB collection：
- Collection name: `kb_{kb_id}`
- Metadata: `{"kb_id": str, "file_name": str, "chunk_index": int}`
