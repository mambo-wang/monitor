# RAG 知识库后端实施计划

> **For implementer:** Use TDD throughout. Write failing test first. Watch it fail. Then implement.

**Goal:** 完成知识库后端 API 开发，支持知识库 CRUD、文档上传、ChromaDB 向量化、RAG 问答

**Architecture:** 采用 FastAPI 构建 REST API，使用 ChromaDB 持久化向量数据，LangChain 处理文档分块，Ollama Embedding 生成向量，MiniMax LLM 生成回答

**Tech Stack:** Python 3.10+ / FastAPI / ChromaDB / LangChain / Ollama / MiniMax API

---

## 任务列表

<!-- openspec-task: 1 -->
### Task 1: RED — 知识库 CRUD API 测试

**Files:**
- Create: `watcher-ai/tests/test_knowledge_api.py`
- Modify: `watcher-ai/src/api/__init__.py`

**Step 1: Write the failing test**
```python
import pytest
from fastapi.testclient import TestClient
from watcher_ai.src.main import app

client = TestClient(app)

def test_create_knowledge_base():
    """测试创建知识库"""
    response = client.post(
        "/api/knowledge/kbs",
        json={"name": "测试知识库", "description": "测试描述"}
    )
    assert response.status_code == 201
    data = response.json()
    assert "id" in data
    assert data["name"] == "测试知识库"
    assert data["status"] == "idle"

def test_list_knowledge_bases():
    """测试获取知识库列表"""
    response = client.get("/api/knowledge/kbs")
    assert response.status_code == 200
    assert isinstance(response.json(), list)

def test_get_knowledge_base():
    """测试获取单个知识库"""
    # 先创建
    create_resp = client.post(
        "/api/knowledge/kbs",
        json={"name": "测试", "description": ""}
    )
    kb_id = create_resp.json()["id"]
    # 再获取
    response = client.get(f"/api/knowledge/kbs/{kb_id}")
    assert response.status_code == 200
    assert response.json()["id"] == kb_id

def test_get_nonexistent_kb():
    """测试获取不存在的知识库"""
    response = client.get("/api/knowledge/kbs/nonexistent-id")
    assert response.status_code == 404

def test_delete_knowledge_base():
    """测试删除知识库"""
    create_resp = client.post(
        "/api/knowledge/kbs",
        json={"name": "待删除", "description": ""}
    )
    kb_id = create_resp.json()["id"]
    response = client.delete(f"/api/knowledge/kbs/{kb_id}")
    assert response.status_code == 200
    # 验证已删除
    get_resp = client.get(f"/api/knowledge/kbs/{kb_id}")
    assert get_resp.status_code == 404
```

**Step 2: Run test — confirm it fails**
```bash
cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_knowledge_api.py -v
```
Expected: FAIL — module not found or routes not defined

---

<!-- openspec-task: 2 -->
### Task 2: GREEN — 实现知识库 CRUD API

**Files:**
- Create: `watcher-ai/src/api/knowledge.py`
- Create: `watcher-ai/src/models/schemas.py`
- Create: `watcher-ai/src/services/kb_service.py`
- Modify: `watcher-ai/src/main.py`

**Step 1: 确认测试仍失败**
```bash
cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_knowledge_api.py -v
```
Expected: FAIL — module not found

**Step 2: Write minimal implementation**

`watcher-ai/src/models/schemas.py`:
```python
from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class KnowledgeBaseCreate(BaseModel):
    name: str
    description: Optional[str] = ""

class KnowledgeBaseResponse(BaseModel):
    id: str
    name: str
    description: str
    status: str
    document_count: int = 0
    chunk_count: int = 0
    created_at: datetime
    updated_at: datetime
```

`watcher-ai/src/services/kb_service.py`:
```python
from typing import Dict, List, Optional
from datetime import datetime
import uuid

# 内存存储
_kb_store: Dict[str, dict] = {}

class KBService:
    @staticmethod
    def create(name: str, description: str = "") -> dict:
        kb_id = str(uuid.uuid4())
        now = datetime.now()
        kb = {
            "id": kb_id,
            "name": name,
            "description": description,
            "status": "idle",
            "document_count": 0,
            "chunk_count": 0,
            "created_at": now,
            "updated_at": now
        }
        _kb_store[kb_id] = kb
        return kb

    @staticmethod
    def list_all() -> List[dict]:
        return list(_kb_store.values())

    @staticmethod
    def get_by_id(kb_id: str) -> Optional[dict]:
        return _kb_store.get(kb_id)

    @staticmethod
    def delete(kb_id: str) -> bool:
        if kb_id in _kb_store:
            del _kb_store[kb_id]
            return True
        return False
```

`watcher-ai/src/api/knowledge.py`:
```python
from fastapi import APIRouter, HTTPException
from watcher_ai.src.models.schemas import KnowledgeBaseCreate, KnowledgeBaseResponse
from watcher_ai.src.services.kb_service import KBService

router = APIRouter(prefix="/api/knowledge", tags=["knowledge"])

@router.post("/kbs", response_model=KnowledgeBaseResponse, status_code=201)
def create_kb(req: KnowledgeBaseCreate):
    kb = KBService.create(req.name, req.description)
    return kb

@router.get("/kbs", response_model=list[KnowledgeBaseResponse])
def list_kbs():
    return KBService.list_all()

@router.get("/kbs/{kb_id}", response_model=KnowledgeBaseResponse)
def get_kb(kb_id: str):
    kb = KBService.get_by_id(kb_id)
    if not kb:
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    return kb

@router.delete("/kbs/{kb_id}")
def delete_kb(kb_id: str):
    if not KBService.delete(kb_id):
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    return {"message": "deleted"}
```

`watcher-ai/src/main.py` 中添加:
```python
from watcher_ai.src.api.knowledge import router as knowledge_router
app.include_router(knowledge_router)
```

**Step 3: Run test — confirm it passes**
```bash
cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_knowledge_api.py -v
```
Expected: PASS

**Step 4: Commit**
```bash
git add watcher-ai/src/api/knowledge.py watcher-ai/src/models/schemas.py watcher-ai/src/services/kb_service.py watcher-ai/src/main.py watcher-ai/tests/test_knowledge_api.py && git commit -m "feat: 知识库CRUD API"
```

---

<!-- openspec-task: 3 -->
### Task 3: RED — 文档上传 API 测试

**Files:**
- Create: `watcher-ai/tests/test_document_api.py`

**Step 1: Write the failing test**
```python
import pytest
from fastapi.testclient import TestClient
from watcher_ai.src.main import app
import io

client = TestClient(app)

def test_upload_document():
    """测试上传文档"""
    # 先创建知识库
    kb_resp = client.post("/api/knowledge/kbs", json={"name": "测试KB"})
    kb_id = kb_resp.json()["id"]
    # 上传文件
    files = {"file": ("test.txt", io.BytesIO(b"Hello World"), "text/plain")}
    response = client.post(f"/api/knowledge/kbs/{kb_id}/documents", files=files)
    assert response.status_code == 201
    data = response.json()
    assert data["file_name"] == "test.txt"
    assert data["status"] == "pending"

def test_upload_unsupported_format():
    """测试上传不支持的格式"""
    kb_resp = client.post("/api/knowledge/kbs", json={"name": "测试KB"})
    kb_id = kb_resp.json()["id"]
    files = {"file": ("test.exe", io.BytesIO(b"data"), "application/octet-stream")}
    response = client.post(f"/api/knowledge/kbs/{kb_id}/documents", files=files)
    assert response.status_code == 400
    assert "Unsupported" in response.json()["detail"]

def test_list_documents():
    """测试列出文档"""
    kb_resp = client.post("/api/knowledge/kbs", json={"name": "测试KB"})
    kb_id = kb_resp.json()["id"]
    # 上传后列出
    files = {"file": ("doc.md", io.BytesIO(b"# Title"), "text/markdown")}
    client.post(f"/api/knowledge/kbs/{kb_id}/documents", files=files)
    response = client.get(f"/api/knowledge/kbs/{kb_id}/documents")
    assert response.status_code == 200
    assert len(response.json()) >= 1

def test_delete_document():
    """测试删除文档"""
    kb_resp = client.post("/api/knowledge/kbs", json={"name": "测试KB"})
    kb_id = kb_resp.json()["id"]
    files = {"file": ("to_delete.txt", io.BytesIO(b"content"), "text/plain")}
    upload_resp = client.post(f"/api/knowledge/kbs/{kb_id}/documents", files=files)
    doc_id = upload_resp.json()["id"]
    response = client.delete(f"/api/knowledge/kbs/{kb_id}/documents/{doc_id}")
    assert response.status_code == 200
```

**Step 2: Run test — confirm it fails**
```bash
cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_document_api.py -v
```
Expected: FAIL — route not defined

---

<!-- openspec-task: 4 -->
### Task 4: GREEN — 实现文档上传 API

**Files:**
- Create: `watcher-ai/src/services/document_store.py`
- Modify: `watcher-ai/src/api/knowledge.py`

**Step 1: 确认测试仍失败**
```bash
cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_document_api.py -v
```
Expected: FAIL

**Step 2: Write minimal implementation**

`watcher-ai/src/services/document_store.py`:
```python
from typing import Dict, List, Optional
from datetime import datetime
import uuid
import os
import shutil

UPLOAD_DIR = os.path.join(os.path.dirname(__file__), "../../uploads")

class DocumentStore:
    _doc_store: Dict[str, dict] = {}
    _kb_docs: Dict[str, List[str]] = {}  # kb_id -> [doc_ids]

    @staticmethod
    def init():
        os.makedirs(UPLOAD_DIR, exist_ok=True)

    @staticmethod
    def save_file(kb_id: str, filename: str, content: bytes) -> dict:
        kb_id_safe = kb_id.replace("-", "_")
        kb_dir = os.path.join(UPLOAD_DIR, kb_id_safe)
        os.makedirs(kb_dir, exist_ok=True)
        file_path = os.path.join(kb_dir, filename)
        with open(file_path, "wb") as f:
            f.write(content)
        doc_id = str(uuid.uuid4())
        doc = {
            "id": doc_id,
            "kb_id": kb_id,
            "file_name": filename,
            "file_path": file_path,
            "file_size": len(content),
            "status": "pending",
            "chunk_count": 0,
            "created_at": datetime.now()
        }
        DocumentStore._doc_store[doc_id] = doc
        if kb_id not in DocumentStore._kb_docs:
            DocumentStore._kb_docs[kb_id] = []
        DocumentStore._kb_docs[kb_id].append(doc_id)
        return doc

    @staticmethod
    def list_by_kb(kb_id: str) -> List[dict]:
        doc_ids = DocumentStore._kb_docs.get(kb_id, [])
        return [DocumentStore._doc_store[did] for did in doc_ids if did in DocumentStore._doc_store]

    @staticmethod
    def get_by_id(doc_id: str) -> Optional[dict]:
        return DocumentStore._doc_store.get(doc_id)

    @staticmethod
    def delete(doc_id: str) -> bool:
        doc = DocumentStore._doc_store.get(doc_id)
        if not doc:
            return False
        # 删除文件
        if os.path.exists(doc["file_path"]):
            os.remove(doc["file_path"])
        # 从 kb_docs 中移除
        kb_id = doc["kb_id"]
        if kb_id in DocumentStore._kb_docs and doc_id in DocumentStore._kb_docs[kb_id]:
            DocumentStore._kb_docs[kb_id].remove(doc_id)
        del DocumentStore._doc_store[doc_id]
        return True
```

修改 `watcher-ai/src/api/knowledge.py` 添加:
```python
from fastapi import UploadFile, File, HTTPException
from watcher_ai.src.services.document_store import DocumentStore

ALLOWED_EXTENSIONS = {".pdf", ".md", ".txt"}

@router.post("/kbs/{kb_id}/documents", status_code=201)
async def upload_document(kb_id: str, file: UploadFile = File(...)):
    if not KBService.get_by_id(kb_id):
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    ext = os.path.splitext(file.filename)[1].lower()
    if ext not in ALLOWED_EXTENSIONS:
        raise HTTPException(status_code=400, detail=f"Unsupported file format: {ext}")
    content = await file.read()
    doc = DocumentStore.save_file(kb_id, file.filename, content)
    return doc

@router.get("/kbs/{kb_id}/documents")
def list_documents(kb_id: str):
    if not KBService.get_by_id(kb_id):
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    return DocumentStore.list_by_kb(kb_id)

@router.delete("/kbs/{kb_id}/documents/{doc_id}")
def delete_document(kb_id: str, doc_id: str):
    if not KBService.get_by_id(kb_id):
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    if not DocumentStore.delete(doc_id):
        raise HTTPException(status_code=404, detail="Document not found")
    return {"message": "deleted"}
```

**Step 3: Run test — confirm it passes**
```bash
cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_document_api.py -v
```
Expected: PASS

**Step 4: Commit**
```bash
git add watcher-ai/src/services/document_store.py watcher-ai/src/api/knowledge.py watcher-ai/tests/test_document_api.py && git commit -m "feat: 文档上传API"
```

---

<!-- openspec-task: 5 -->
### Task 5: RED — ChromaDB 服务测试

**Files:**
- Create: `watcher-ai/tests/test_chroma_service.py`

**Step 1: Write the failing test**
```python
import pytest
from watcher_ai.src.services.chroma_service import ChromaService

def test_create_and_get_collection():
    """测试创建和获取 collection"""
    kb_id = "test-kb-1"
    collection = ChromaService.get_or_create_collection(kb_id)
    assert collection.name == f"kb_{kb_id}"

def test_add_and_search_vectors():
    """测试添加和检索向量"""
    kb_id = "test-kb-search"
    ChromaService.delete_collection(kb_id)  # 清理
    collection = ChromaService.get_or_create_collection(kb_id)
    # 添加测试向量
    ChromaService.add_vectors(
        kb_id,
        ids=["test1", "test2"],
        documents=["Hello world", "Python programming"],
        metadatas=[{"source": "test"}, {"source": "test"}]
    )
    # 检索
    results = ChromaService.search(kb_id, query="hello", top_k=2)
    assert len(results["ids"][0]) <= 2
    # 清理
    ChromaService.delete_collection(kb_id)

def test_delete_collection():
    """测试删除 collection"""
    kb_id = "test-kb-delete"
    ChromaService.get_or_create_collection(kb_id)
    assert ChromaService.delete_collection(kb_id)
    assert not ChromaService.collection_exists(kb_id)
```

**Step 2: Run test — confirm it fails**
```bash
cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_chroma_service.py -v
```
Expected: FAIL — module not found

---

<!-- openspec-task: 6 -->
### Task 6: GREEN — 实现 ChromaDB 服务

**Files:**
- Create: `watcher-ai/src/services/chroma_service.py`

**Step 1: 确认测试仍失败**
```bash
cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_chroma_service.py -v
```
Expected: FAIL

**Step 2: Write minimal implementation**

`watcher-ai/src/services/chroma_service.py`:
```python
import chromadb
from chromadb.config import Settings
import os
import requests
from typing import List, Dict, Optional

CHROMA_DB_PATH = os.path.join(os.path.dirname(__file__), "../../chroma_db")
OLLAMA_EMBED_URL = os.getenv("OLLAMA_EMBED_URL", "http://localhost:11434/api/embeddings")
OLLAMA_EMBED_MODEL = os.getenv("OLLAMA_EMBED_MODEL", "bge-m3")

chroma_client = chromadb.PersistentClient(path=CHROMA_DB_PATH)

class ChromaService:
    @staticmethod
    def get_embedding(text: str) -> List[float]:
        """调用 Ollama Embedding API 获取向量"""
        resp = requests.post(
            OLLAMA_EMBED_URL,
            json={"model": OLLAMA_EMBED_MODEL, "prompt": text},
            timeout=30
        )
        resp.raise_for_status()
        return resp.json()["embedding"]

    @staticmethod
    def get_or_create_collection(kb_id: str):
        """获取或创建 collection"""
        collection_name = f"kb_{kb_id}"
        return chroma_client.get_or_create_collection(
            name=collection_name,
            metadata={"kb_id": kb_id}
        )

    @staticmethod
    def collection_exists(kb_id: str) -> bool:
        """检查 collection 是否存在"""
        collection_name = f"kb_{kb_id}"
        try:
            chroma_client.get_collection(collection_name)
            return True
        except:
            return False

    @staticmethod
    def delete_collection(kb_id: str) -> bool:
        """删除 collection"""
        collection_name = f"kb_{kb_id}"
        try:
            chroma_client.delete_collection(collection_name)
            return True
        except:
            return False

    @staticmethod
    def add_vectors(kb_id: str, ids: List[str], documents: List[str], metadatas: List[dict]):
        """添加向量到 collection"""
        collection = ChromaService.get_or_create_collection(kb_id)
        embeddings = [ChromaService.get_embedding(doc) for doc in documents]
        collection.add(ids=ids, embeddings=embeddings, documents=documents, metadatas=metadatas)

    @staticmethod
    def search(kb_id: str, query: str, top_k: int = 3) -> Dict:
        """检索向量"""
        collection_name = f"kb_{kb_id}"
        try:
            collection = chroma_client.get_collection(collection_name)
        except:
            return {"ids": [[]], "documents": [[]], "metadatas": [[]], "distances": [[]]}
        query_embedding = ChromaService.get_embedding(query)
        return collection.query(query_embeddings=[query_embedding], n_results=top_k)

    @staticmethod
    def get_count(kb_id: str) -> int:
        """获取 collection 中的向量数量"""
        collection_name = f"kb_{kb_id}"
        try:
            collection = chroma_client.get_collection(collection_name)
            return collection.count()
        except:
            return 0
```

**Step 3: Run test — confirm it passes**
```bash
cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_chroma_service.py -v
```
Expected: PASS

**Step 4: Commit**
```bash
git add watcher-ai/src/services/chroma_service.py watcher-ai/tests/test_chroma_service.py && git commit -m "feat: ChromaDB服务"
```

---

<!-- openspec-task: 7 -->
### Task 7: RED — 文档处理服务测试

**Files:**
- Create: `watcher-ai/tests/test_document_service.py`

**Step 1: Write the failing test**
```python
import pytest
from watcher_ai.src.services.document_service import DocumentProcessor
import os

def test_split_text():
    """测试文本分割"""
    text = "这是第一段。\n\n这是第二段。\n\n这是第三段。"
    chunks = DocumentProcessor.split_text(text)
    assert len(chunks) >= 2
    assert all(isinstance(c, str) for c in chunks)

def test_load_document_not_found():
    """测试加载不存在的文档"""
    docs, err = DocumentProcessor.load_document("/nonexistent/path.pdf")
    assert docs is None
    assert "not exist" in err.lower() or "失败" in err

def test_supported_formats():
    """测试支持的格式"""
    supported = DocumentProcessor.get_supported_extensions()
    assert ".pdf" in supported
    assert ".md" in supported
    assert ".txt" in supported
```

**Step 2: Run test — confirm it fails**
```bash
cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_document_service.py -v
```
Expected: FAIL — module not found

---

<!-- openspec-task: 8 -->
### Task 8: GREEN — 实现文档处理服务

**Files:**
- Create: `watcher-ai/src/services/document_service.py`

**Step 1: 确认测试仍失败**
```bash
cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_document_service.py -v
```
Expected: FAIL

**Step 2: Write minimal implementation**

`watcher-ai/src/services/document_service.py`:
```python
import os
from typing import Tuple, List
from langchain_community.document_loaders import PyPDFLoader, UnstructuredMarkdownLoader, TextLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter

CHUNK_SIZE = 500
CHUNK_OVERLAP = 50

text_splitter = RecursiveCharacterTextSplitter(
    chunk_size=CHUNK_SIZE,
    chunk_overlap=CHUNK_OVERLAP,
    length_function=len,
    separators=["\n\n", "\n", "。", ". ", " "]
)

class DocumentProcessor:
    SUPPORTED_EXTENSIONS = {".pdf", ".md", ".txt"}

    @staticmethod
    def get_supported_extensions() -> set:
        return DocumentProcessor.SUPPORTED_EXTENSIONS

    @staticmethod
    def load_document(file_path: str) -> Tuple[List, str]:
        """使用 LangChain 加载文档"""
        if not os.path.exists(file_path):
            return None, f"File not exist: {file_path}"
        ext = os.path.splitext(file_path)[1].lower()
        try:
            if ext == ".pdf":
                loader = PyPDFLoader(file_path)
            elif ext == ".md":
                loader = UnstructuredMarkdownLoader(file_path)
            elif ext == ".txt":
                loader = TextLoader(file_path, encoding="utf-8")
            else:
                return None, f"Unsupported format: {ext}"
            docs = loader.load()
            return docs, None
        except Exception as e:
            return None, f"Load failed: {str(e)}"

    @staticmethod
    def split_text(text: str) -> List[str]:
        """分割文本为 chunks"""
        chunks = text_splitter.split_text(text)
        return chunks

    @staticmethod
    def process_document(file_path: str) -> Tuple[List[dict], str]:
        """处理文档，返回 chunks 列表"""
        docs, err = DocumentProcessor.load_document(file_path)
        if err:
            return [], err
        if not docs:
            return [], "Empty document"
        # 合并所有 page_content
        full_text = "\n".join(doc.page_content for doc in docs)
        chunks = DocumentProcessor.split_text(full_text)
        return [{"content": c, "chunk_index": i} for i, c in enumerate(chunks)], None
```

**Step 3: Run test — confirm it passes**
```bash
cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_document_service.py -v
```
Expected: PASS

**Step 4: Commit**
```bash
git add watcher-ai/src/services/document_service.py watcher-ai/tests/test_document_service.py && git commit -m "feat: 文档处理服务"
```

---

<!-- openspec-task: 9 -->
### Task 9: RED — 构建 API 测试

**Files:**
- Create: `watcher-ai/tests/test_build_api.py`

**Step 1: Write the failing test**
```python
import pytest
from fastapi.testclient import TestClient
from watcher_ai.src.main import app
import io

client = TestClient(app)

def test_build_kb_with_documents():
    """测试构建有文档的知识库"""
    # 创建 KB
    kb_resp = client.post("/api/knowledge/kbs", json={"name": "Build测试"})
    kb_id = kb_resp.json()["id"]
    # 上传文档
    files = {"file": ("test.txt", io.BytesIO(b"Hello World. This is a test."), "text/plain")}
    client.post(f"/api/knowledge/kbs/{kb_id}/documents", files=files)
    # 构建
    response = client.post(f"/api/knowledge/kbs/{kb_id}/build")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] in ["building", "ready"]

def test_build_empty_kb():
    """测试构建空知识库"""
    kb_resp = client.post("/api/knowledge/kbs", json={"name": "空KB"})
    kb_id = kb_resp.json()["id"]
    response = client.post(f"/api/knowledge/kbs/{kb_id}/build")
    assert response.status_code == 400
    assert "empty" in response.json()["detail"].lower() or "no documents" in response.json()["detail"].lower()

def test_get_kb_stats():
    """测试获取知识库统计"""
    kb_resp = client.post("/api/knowledge/kbs", json={"name": "Stats测试"})
    kb_id = kb_resp.json()["id"]
    response = client.get(f"/api/knowledge/kbs/{kb_id}/stats")
    assert response.status_code == 200
    assert "chunk_count" in response.json()
```

**Step 2: Run test — confirm it fails**
```bash
cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_build_api.py -v
```
Expected: FAIL — route not defined

---

<!-- openspec-task: 10 -->
### Task 10: GREEN — 实现构建 API

**Files:**
- Modify: `watcher-ai/src/api/knowledge.py`
- Create: `watcher-ai/src/services/build_service.py`

**Step 1: 确认测试仍失败**
```bash
cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_build_api.py -v
```
Expected: FAIL

**Step 2: Write minimal implementation**

`watcher-ai/src/services/build_service.py`:
```python
from watcher_ai.src.services.chroma_service import ChromaService
from watcher_ai.src.services.document_service import DocumentProcessor
from watcher_ai.src.services.document_store import DocumentStore
from watcher_ai.src.services.kb_service import KBService
import uuid

class BuildService:
    @staticmethod
    def build_knowledge_base(kb_id: str) -> dict:
        """构建知识库"""
        docs = DocumentStore.list_by_kb(kb_id)
        if not docs:
            raise ValueError("No documents to build")
        # 更新状态为 building
        KBService.update_status(kb_id, "building")
        total_chunks = 0
        for doc in docs:
            chunks, err = DocumentProcessor.process_document(doc["file_path"])
            if err:
                continue
            if not chunks:
                continue
            # 添加到 ChromaDB
            ids = [f"{doc['id']}_{i}" for i in range(len(chunks))]
            documents = [c["content"] for c in chunks]
            metadatas = [{
                "kb_id": kb_id,
                "doc_id": doc["id"],
                "file_name": doc["file_name"],
                "chunk_index": c["chunk_index"]
            } for c in chunks]
            try:
                ChromaService.add_vectors(kb_id, ids, documents, metadatas)
                total_chunks += len(chunks)
                DocumentStore.update_chunk_count(doc["id"], len(chunks))
            except Exception as e:
                print(f"Error adding vectors: {e}")
        # 更新 KB 状态和计数
        KBService.update_counts(kb_id, len(docs), total_chunks)
        KBService.update_status(kb_id, "ready")
        return {"status": "ready", "chunk_count": total_chunks}
```

修改 `watcher-ai/src/services/kb_service.py` 添加:
```python
    @staticmethod
    def update_status(kb_id: str, status: str):
        if kb_id in _kb_store:
            _kb_store[kb_id]["status"] = status
            _kb_store[kb_id]["updated_at"] = datetime.now()

    @staticmethod
    def update_counts(kb_id: str, doc_count: int, chunk_count: int):
        if kb_id in _kb_store:
            _kb_store[kb_id]["document_count"] = doc_count
            _kb_store[kb_id]["chunk_count"] = chunk_count
            _kb_store[kb_id]["updated_at"] = datetime.now()
```

修改 `watcher-ai/src/services/document_store.py` 添加:
```python
    @staticmethod
    def update_chunk_count(doc_id: str, chunk_count: int):
        if doc_id in DocumentStore._doc_store:
            DocumentStore._doc_store[doc_id]["chunk_count"] = chunk_count
            DocumentStore._doc_store[doc_id]["status"] = "parsed"
```

修改 `watcher-ai/src/api/knowledge.py` 添加:
```python
from watcher_ai.src.services.build_service import BuildService

@router.post("/kbs/{kb_id}/build")
def build_kb(kb_id: str):
    if not KBService.get_by_id(kb_id):
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    try:
        result = BuildService.build_knowledge_base(kb_id)
        return result
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        KBService.update_status(kb_id, "error")
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/kbs/{kb_id}/stats")
def get_kb_stats(kb_id: str):
    if not KBService.get_by_id(kb_id):
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    kb = KBService.get_by_id(kb_id)
    chunk_count = ChromaService.get_count(kb_id)
    return {
        "document_count": kb["document_count"],
        "chunk_count": chunk_count,
        "status": kb["status"]
    }
```

**Step 3: Run test — confirm it passes**
```bash
cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_build_api.py -v
```
Expected: PASS

**Step 4: Commit**
```bash
git add watcher-ai/src/services/build_service.py watcher-ai/src/services/kb_service.py watcher-ai/src/services/document_store.py watcher-ai/src/api/knowledge.py watcher-ai/tests/test_build_api.py && git commit -m "feat: 知识库构建API"
```
