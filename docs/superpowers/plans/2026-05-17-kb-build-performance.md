# 知识库构建性能优化实现计划

> **面向 AI 代理的工作者：** 必需子技能：使用 superpowers:subagent-driven-development（推荐）或 superpowers:executing-plans 逐任务实现此计划。步骤使用复选框（`- [ ]`）语法来跟踪进度。

**目标：** 修复大文件（20MB+ PDF）知识库构建卡住问题，通过批量 embedding 和进度反馈提升构建体验

**架构：** 在 `ChromaService.add_vectors()` 中实现批量 embedding 机制，每批处理多个 chunks 减少 API 调用次数；同时在 `BuildService` 中添加进度回调，支持前端轮询构建进度。

**技术栈：** Python, ChromaDB, Ollama Embedding API, FastAPI

---

## 文件变更清单

| 文件 | 职责 |
|------|------|
| `watcher-ai/src/watcher_ai/services/chroma_service.py` | 实现批量 embedding，添加 `add_vectors_batch()` 方法 |
| `watcher-ai/src/watcher_ai/services/build_service.py` | 添加进度回调机制，更新 KB 状态为 building 时记录开始时间 |
| `watcher-ai/src/watcher_ai/api/knowledge.py` | 添加构建进度查询 API |
| `watcher-web/src/api/knowledge.ts` | 添加 `getBuildProgress()` API 调用 |
| `watcher-web/src/views/main/knowledge/KnowledgeLibrary.vue` | 显示构建进度条，轮询检查状态 |

---

## 任务 1：批量 Embedding 实现

**文件：**
- 修改：`watcher-ai/src/watcher_ai/services/chroma_service.py:54-58`

- [ ] **步骤 1：修改 `add_vectors` 方法，添加批量处理**

将 `add_vectors` 方法修改为：
```python
@staticmethod
def add_vectors(kb_id: str, ids: List[str], documents: List[str], metadatas: List[dict], batch_size: int = 10):
    """添加向量到 collection，批量处理减少 API 调用"""
    collection = ChromaService.get_or_create_collection(kb_id)
    
    # 分批处理，每批 batch_size 个 chunks
    for i in range(0, len(documents), batch_size):
        batch_ids = ids[i:i + batch_size]
        batch_docs = documents[i:i + batch_size]
        batch_metas = metadatas[i:i + batch_size]
        
        # 批量获取 embedding
        embeddings = [ChromaService.get_embedding(doc) for doc in batch_docs]
        collection.add(ids=batch_ids, embeddings=embeddings, documents=batch_docs, metadatas=batch_metas)
```

- [ ] **步骤 2：运行测试验证**

运行：`cd watcher-ai && pytest tests/test_chroma_service.py -v`（如无测试文件则跳过）

---

## 任务 2：构建进度跟踪

**文件：**
- 修改：`watcher-ai/src/watcher_ai/services/kb_repository.py` - 添加 `update_build_progress` 方法
- 修改：`watcher-ai/src/watcher_ai/services/kb_service.py` - 添加 `update_progress` 方法
- 修改：`watcher-ai/src/watcher_ai/services/build_service.py` - 添加进度更新调用

- [ ] **步骤 1：在 `kb_repository.py` 添加进度字段和更新方法**

在 `KBRepository` 中添加：
```python
@staticmethod
def update_build_progress(kb_id: str, progress: int, current_doc: str = "") -> bool:
    """更新构建进度"""
    sql = """
        UPDATE knowledge_bases 
        SET status = %s, description = %s, updated_at = %s 
        WHERE id = %s
    """
    # 使用 description 字段暂存进度信息，格式：building:{progress}:{current_doc}
    status_info = f"building:{progress}:{current_doc}"
    KBRepository._get_client().execute(sql, ("building", status_info, datetime.now(), kb_id))
    return True
```

- [ ] **步骤 2：修改 `build_service.py` 添加进度更新**

```python
class BuildService:
    @staticmethod
    def build_knowledge_base(kb_id: str, progress_callback=None) -> dict:
        """构建知识库"""
        docs = DocumentStore.list_by_kb(kb_id)
        if not docs:
            raise ValueError("No documents to build")
        
        KBService.update_status(kb_id, "building")
        total_chunks = 0
        total_docs = len(docs)
        
        for idx, doc in enumerate(docs):
            # 更新进度
            progress = int((idx / total_docs) * 100)
            if progress_callback:
                progress_callback(kb_id, progress, doc["file_name"])
            
            chunks, err = DocumentProcessor.process_document(doc["file_path"])
            if err:
                continue
            if not chunks:
                continue
            
            ids = [f"{doc['id']}_{i}" for i in range(len(chunks))]
            documents = [c["content"] for c in chunks]
            metadatas = [{
                "kb_id": kb_id,
                "doc_id": doc["id"],
                "file_name": doc["file_name"],
                "chunk_index": c["chunk_index"]
            } for c in chunks]
            
            try:
                ChromaService.add_vectors(kb_id, ids, documents, metadatas, batch_size=10)
                total_chunks += len(chunks)
                DocumentStore.update_chunk_count(doc["id"], len(chunks))
            except Exception as e:
                print(f"Error adding vectors: {e}")
        
        KBService.update_counts(kb_id, len(docs), total_chunks)
        KBService.update_status(kb_id, "ready")
        return {"status": "ready", "chunk_count": total_chunks}
```

- [ ] **步骤 3：Commit**

```bash
git add watcher-ai/src/watcher_ai/services/
git commit -m "feat: add batch embedding and build progress tracking"
```

---

## 任务 3：添加构建进度查询 API

**文件：**
- 修改：`watcher-ai/src/watcher_ai/api/knowledge.py`

- [ ] **步骤 1：在 `knowledge.py` 添加进度查询端点**

```python
@router.get("/kbs/{kb_id}/build/progress")
def get_build_progress(kb_id: str):
    """获取构建进度"""
    kb = KBService.get_by_id(kb_id)
    if not kb:
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    
    # 从 description 解析进度信息
    desc = kb.get("description", "")
    if kb["status"] == "building" and desc.startswith("building:"):
        parts = desc.split(":")
        progress = int(parts[1]) if len(parts) > 1 else 0
        current_doc = parts[2] if len(parts) > 2 else ""
        return ok_response({
            "status": "building",
            "progress": progress,
            "current_doc": current_doc
        })
    
    return ok_response({
        "status": kb["status"],
        "progress": 100 if kb["status"] == "ready" else 0,
        "current_doc": ""
    })
```

- [ ] **步骤 2：Commit**

```bash
git add watcher-ai/src/watcher_ai/api/knowledge.py
git commit -m "feat: add build progress query API"
```

---

## 任务 4：前端进度显示

**文件：**
- 修改：`watcher-web/src/api/knowledge.ts` - 添加 `getBuildProgress` API
- 修改：`watcher-web/src/views/main/knowledge/KnowledgeLibrary.vue` - 显示进度条

- [ ] **步骤 1：在 `knowledge.ts` 添加进度查询**

```typescript
export async function getBuildProgress(kbId: string): Promise<BuildProgress> {
  const res = await axios.get(`/api/knowledge/kbs/${kbId}/build/progress`)
  return res.data.data
}

export interface BuildProgress {
  status: string
  progress: number
  current_doc: string
}
```

- [ ] **步骤 2：修改 `KnowledgeLibrary.vue` 的 `buildKB` 方法**

```typescript
async function buildKB(kb: KnowledgeBase) {
  try {
    await buildKnowledgeBase(kb.id)
    ElMessage.success('构建已启动')
    
    // 轮询进度
    const timer = setInterval(async () => {
      const progress = await getBuildProgress(kb.id)
      
      if (progress.status === 'building') {
        // 更新进度条
        const btn = document.querySelector(`[data-kb-id="${kb.id}"] .progress-bar`)
        if (btn) {
          (btn as HTMLElement).style.width = `${progress.progress}%`
        }
      } else {
        clearInterval(timer)
        loadKBs()
      }
    }, 2000)
  } catch (e) {
    ElMessage.error('启动构建失败')
  }
}
```

- [ ] **步骤 3：Commit**

```bash
git add watcher-web/src/api/knowledge.ts watcher-web/src/views/main/knowledge/KnowledgeLibrary.vue
git commit -m "feat: add build progress UI"
```

---

## 验证清单

- [ ] 小文件（< 1MB）构建成功，状态变为 ready
- [ ] 大文件（20MB+ PDF）构建时能看到进度百分比
- [ ] 构建完成后 chunk_count 正确
- [ ] 过程中 Ollama 报错不影响构建结果（如有报错仍能完成）

---

**计划已完成并保存到 `docs/superpowers/plans/2026-05-17-kb-build-performance.md`。两种执行方式：**

**1. 子代理驱动（推荐）** - 每个任务调度一个新的子代理，任务间进行审查，快速迭代

**2. 内联执行** - 在当前会话中使用 executing-plans 执行任务，批量执行并设有检查点

**选哪种方式？**