# Execution Plan

## 后端实现

### Step 1: RED — 知识库 CRUD API 测试
- Test file: `watcher-ai/tests/test_knowledge_api.py`
- Assertion: 测试 POST 创建、GET 列表、GET 详情、DELETE 删除
- Expected failure: 模块不存在
- Verify: `cd watcher-ai && pytest tests/test_knowledge_api.py -v`

### Step 2: GREEN — 知识库 CRUD API 实现
- Pass test from: Step 1
- Minimal code: 在 `watcher-ai/src/api/knowledge.py` 实现基础路由
- Verify: `cd watcher-ai && pytest tests/test_knowledge_api.py -v`

### Step 3: RED — 文档上传 API 测试
- Test file: `watcher-ai/tests/test_document_api.py`
- Assertion: 测试上传 PDF/MD/TXT、列表查询、删除
- Expected failure: 上传接口不存在
- Verify: `cd watcher-ai && pytest tests/test_document_api.py -v`

### Step 4: GREEN — 文档上传 API 实现
- Pass test from: Step 3
- Minimal code: 实现文件上传路由，保存到 `watcher-ai/uploads/`
- Verify: `cd watcher-ai && pytest tests/test_document_api.py -v`

### Step 5: RED — ChromaDB 服务测试
- Test file: `watcher-ai/tests/test_chroma_service.py`
- Assertion: 测试创建 collection、添加向量、查询
- Expected failure: chroma_service 模块不存在
- Verify: `cd watcher-ai && pytest tests/test_chroma_service.py -v`

### Step 6: GREEN — ChromaDB 服务实现
- Pass test from: Step 5
- Minimal code: 实现 `watcher-ai/src/services/chroma_service.py`
- Verify: `cd watcher-ai && pytest tests/test_chroma_service.py -v`

### Step 7: RED — 文档处理服务测试
- Test file: `watcher-ai/tests/test_document_service.py`
- Assertion: 测试加载 PDF/MD/TXT、文本分割
- Expected failure: document_service 模块不存在
- Verify: `cd watcher-ai && pytest tests/test_document_service.py -v`

### Step 8: GREEN — 文档处理服务实现
- Pass test from: Step 7
- Minimal code: 实现 `watcher-ai/src/services/document_service.py`
- Verify: `cd watcher-ai && pytest tests/test_document_service.py -v`

### Step 9: RED — 构建与问答 API 测试
- Test file: `watcher-ai/tests/test_rag_api.py`
- Assertion: 测试构建知识库、RAG 问答、检索
- Expected failure: 接口返回 500
- Verify: `cd watcher-ai && pytest tests/test_rag_api.py -v`

### Step 10: GREEN — 构建与问答 API 实现
- Pass test from: Step 9
- Minimal code: 实现构建和问答路由
- Verify: `cd watcher-ai && pytest tests/test_rag_api.py -v`

## 前端实现

### Step 11: RED — 前端 API 调用测试
- Test file: `watcher-web/tests/unit/knowledge.test.ts`
- Assertion: 测试 API 方法调用
- Expected failure: API 方法未定义
- Verify: `cd watcher-web && npm test -- --testPathPattern=knowledge`

### Step 12: GREEN — 前端 API 调用实现
- Pass test from: Step 11
- Minimal code: 实现 `watcher-web/src/api/knowledge.ts`
- Verify: `cd watcher-web && npm test -- --testPathPattern=knowledge`

### Step 13: GREEN — 知识库列表页完善
- Pass test from: Step 11
- Minimal code: 完善 `KnowledgeLibrary.vue` 已有代码
- Verify: 页面功能正常

### Step 14: GREEN — 文档管理页实现
- Pass test from: Step 11
- Minimal code: 创建 `DocumentManage.vue`
- Verify: 页面功能正常

### Step 15: GREEN — RAG 问答页实现
- Pass test from: Step 11
- Minimal code: 创建 `ChatAssistant.vue`
- Verify: 页面功能正常

---

## Execution Mode Selection

REQUIRED: Use `superpowers:subagent-driven-development` skill.
DO NOT use `executing-plans` or inline execution.
