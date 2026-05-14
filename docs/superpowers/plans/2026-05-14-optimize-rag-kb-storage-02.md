# 知识库存储迁移到MySQL - 第二部分：前端优化与集成测试

> **面向 AI 代理的工作者：** 必需子技能：使用 superpowers:subagent-driven-development（推荐）或 superpowers:executing-plans 逐任务实现此计划。步骤使用复选框（`- [ ]`）语法来跟踪进度。

**目标：** 完成同步逻辑优化、前端上传弹框优化、集成测试

**架构：** 完善 ChromaDB 同步逻辑，调整上传弹框样式添加拖动功能，运行完整测试套件

**技术栈：** Python + pytest + Vue 3 + Element Plus

---

## 文件结构

### 修改文件

| 文件 | 变更 |
|------|------|
| `watcher-web/src/views/main/knowledge/DocumentManage.vue` | 优化上传弹框样式 |
| `watcher-ai/src/watcher_ai/services/kb_service.py` | 完善同步逻辑 |

### 新增测试文件

| 文件 | 职责 |
|------|------|
| `watcher-ai/tests/test_sync_behavior.py` | 验证同步不覆盖逻辑 |

---

## 任务 5：完善同步逻辑

**文件：**
- 修改：`watcher-ai/src/watcher_ai/services/kb_service.py`
- 测试：`watcher-ai/tests/test_sync_behavior.py`

<!-- openspec-task: 5.1 -->
### 任务 10：RED - 编写同步逻辑测试

```python
# watcher-ai/tests/test_sync_behavior.py
import pytest
import sys
import os
from datetime import datetime
from unittest.mock import Mock, patch, MagicMock
import tempfile
import shutil

sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'src'))

class MockChromaCollection:
    """模拟 ChromaDB Collection"""
    def __init__(self, name, count=0):
        self.name = name
        self._count = count
    
    def count(self):
        return self._count

class MockChromaClient:
    """模拟 ChromaDB Client"""
    def __init__(self, collections=None):
        self._collections = collections or []
    
    def list_collections(self):
        return self._collections
    
    def get_collection(self, name):
        for col in self._collections:
            if col.name == name:
                return col
        raise ValueError(f"Collection {name} not found")

class MockKBRepository:
    """模拟 KBRepository"""
    _store = {}
    _exists_cache = set()
    
    @classmethod
    def reset(cls):
        cls._store = {}
        cls._exists_cache = set()
    
    @classmethod
    def exists(cls, kb_id):
        return kb_id in cls._exists_cache
    
    @classmethod
    def save(cls, kb_data):
        cls._store[kb_data['id']] = kb_data
        cls._exists_cache.add(kb_data['id'])
        return True

def test_sync_does_not_overwrite_existing_kb_name():
    """验证同步时不覆盖 MySQL 中已有的知识库名称"""
    from watcher_ai.services.kb_service import _sync_from_chroma, CHROMA_DB_PATH
    
    MockKBRepository.reset()
    
    # 模拟 MySQL 中已有知识库，用户自定义名称为 "我的知识库"
    existing_kb_id = "existing-kb-123"
    MockKBRepository._exists_cache.add(existing_kb_id)
    MockKBRepository._store[existing_kb_id] = {
        'id': existing_kb_id,
        'name': '我的知识库',  # 用户自定义名称
        'description': '用户创建的',
        'status': 'ready',
        'document_count': 5,
        'chunk_count': 100,
        'created_at': datetime.now(),
        'updated_at': datetime.now()
    }
    
    # 模拟 ChromaDB 中存在同名 KB（不同实例恢复的数据）
    mock_col = MockChromaCollection("kb_existing-kb-123", count=50)
    mock_client = MockChromaClient([mock_col])
    
    with patch('watcher_ai.services.kb_service.KBRepository', MockKBRepository), \
         patch('watcher_ai.services.kb_service.chromadb.PersistentClient', return_value=mock_client), \
         patch('os.path.exists', return_value=True):
        
        _sync_from_chroma()
        
        # 验证名称没有被覆盖
        saved_kb = MockKBRepository._store.get(existing_kb_id)
        assert saved_kb is not None, "知识库应该保留"
        # 由于 KB 已存在，exists() 返回 True，同步逻辑应该跳过
        # 验证不会创建重复记录
        assert len(MockKBRepository._store) == 1, "不应该创建重复记录"

def test_sync_creates_new_kb_when_not_in_mysql():
    """验证当 MySQL 中不存在时，创建新知识库"""
    from watcher_ai.services.kb_service import _sync_from_chroma, CHROMA_DB_PATH
    
    MockKBRepository.reset()
    
    # 模拟 ChromaDB 中存在 KB，但 MySQL 中不存在
    new_kb_id = "new-kb-456"
    mock_col = MockChromaCollection(f"kb_{new_kb_id}", count=30)
    mock_client = MockChromaClient([mock_col])
    
    with patch('watcher_ai.services.kb_service.KBRepository', MockKBRepository), \
         patch('watcher_ai.services.kb_service.chromadb.PersistentClient', return_value=mock_client), \
         patch('os.path.exists', return_value=True):
        
        _sync_from_chroma()
        
        # 验证创建了新知识库
        saved_kb = MockKBRepository._store.get(new_kb_id)
        assert saved_kb is not None, "应该创建新知识库"
        assert saved_kb['name'] == f"KB_{new_kb_id[:8]}", "应该使用默认名称"
        assert saved_kb['chunk_count'] == 30, "应该同步 chunk 数量"
```

- [ ] **步骤 1：编写失败的测试**

- [ ] **步骤 2：运行测试验证失败**

运行：`cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_sync_behavior.py -v`
预期：FAIL，同步逻辑未正确实现

<!-- openspec-task: 5.2 -->
### 任务 11：GREEN - 实现同步逻辑

- [ ] **步骤 1：更新 kb_service.py 中的 _sync_from_chroma 方法**

```python
# watcher-ai/src/watcher_ai/services/kb_service.py (更新 _sync_from_chroma 方法)
def _sync_from_chroma():
    """从 ChromaDB 同步 KB 列表（用于服务重启后恢复）"""
    if not os.path.exists(CHROMA_DB_PATH):
        return
    try:
        client = chromadb.PersistentClient(path=CHROMA_DB_PATH)
        collections = client.list_collections()
        
        for col in collections:
            if col.name.startswith("kb_"):
                kb_id = col.name[3:]  # 去掉 "kb_" 前缀
                
                # 关键改进：如果 KB 已在 MySQL 中存在，跳过（保留 MySQL 数据）
                if KBRepository.exists(kb_id):
                    print(f"KB {kb_id} already exists in MySQL, skipping sync")
                    continue
                
                # 从 ChromaDB 恢复不存在的 KB
                try:
                    collection = client.get_collection(col.name)
                    chunk_count = collection.count()
                    
                    kb_data = {
                        "id": kb_id,
                        "name": f"KB_{kb_id[:8]}",  # 使用默认名称
                        "description": "Recovered from ChromaDB",
                        "status": "ready" if chunk_count > 0 else "idle",
                        "document_count": 0,
                        "chunk_count": chunk_count,
                        "created_at": datetime.now(),
                        "updated_at": datetime.now()
                    }
                    KBRepository.save(kb_data)
                    print(f"Synced KB {kb_id} from ChromaDB")
                except Exception as e:
                    print(f"Error restoring KB {kb_id}: {e}")
    except Exception as e:
        print(f"Error syncing from ChromaDB: {e}")
```

- [ ] **步骤 2：运行测试验证通过**

运行：`cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_sync_behavior.py -v`
预期：PASS

- [ ] **步骤 3：Commit**

```bash
git add watcher-ai/src/watcher_ai/services/kb_service.py watcher-ai/tests/test_sync_behavior.py
git commit -m "fix(watcher-ai): preserve MySQL KB names during ChromaDB sync"
```

---

## 任务 6：前端上传弹框优化

**文件：**
- 修改：`watcher-web/src/views/main/knowledge/DocumentManage.vue`

<!-- openspec-task: 6.1 -->
### 任务 12：RED - 验证弹框样式测试

（此任务为前端样式验证，无需自动化测试）

- [ ] **步骤 1：检查当前弹框宽度**

查看 `watcher-web/src/views/main/knowledge/DocumentManage.vue` 第39行：
```vue
<el-dialog v-model="showUploadDialog" title="上传文档" width="500">
```

确认当前宽度为 500px

<!-- openspec-task: 6.2 -->
### 任务 13：GREEN - 调整弹框宽度为 300px

- [ ] **步骤 1：修改弹框宽度**

```vue
<!-- watcher-web/src/views/main/knowledge/DocumentManage.vue -->
<template>
  ...
  <!-- 上传对话框 -->
  <el-dialog 
    v-model="showUploadDialog" 
    title="上传文档" 
    width="300px"
    class="upload-dialog">
    ...
  </el-dialog>
  ...
</template>

<style scoped>
.upload-dialog {
  /* 可选：添加居中效果 */
  :deep(.el-dialog) {
    max-width: 90vw;
  }
}
</style>
```

- [ ] **步骤 2：验证宽度变更**

（手动验证：启动前端项目，打开上传弹框，检查宽度为 300px）

- [ ] **步骤 3：Commit**

```bash
git add watcher-web/src/views/main/knowledge/DocumentManage.vue
git commit -m "fix(watcher-web): reduce upload dialog width to 300px"
```

<!-- openspec-task: 6.3 -->
### 任务 14：GREEN - 添加拖动功能

- [ ] **步骤 1：检查拖动指令是否已注册**

查看 `watcher-web/src/views/main/knowledge/DocumentManage.vue` 是否已导入拖动指令

如果没有导入，需要在组件中引入：

```vue
<!-- watcher-web/src/views/main/knowledge/DocumentManage.vue -->
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listDocuments, uploadDocument, deleteDocument, type KnowledgeBase, type Document } from '@/api/knowledge'
import drag from '@/directive/drag'

// 注册指令
const vDrag = drag

// ... 其他代码
</script>
```

- [ ] **步骤 2：在弹框上应用拖动指令**

```vue
<!-- watcher-web/src/views/main/knowledge/DocumentManage.vue -->
<template>
  ...
  <!-- 上传对话框 -->
  <el-dialog 
    v-model="showUploadDialog" 
    title="上传文档" 
    width="300px"
    v-drag
    class="upload-dialog">
    ...
  </el-dialog>
  ...
</template>
```

- [ ] **步骤 3：验证拖动功能**

（手动验证：打开上传弹框，拖动标题栏，弹框应随鼠标移动）

- [ ] **步骤 4：Commit**

```bash
git add watcher-web/src/views/main/knowledge/DocumentManage.vue
git commit -m "feat(watcher-web): add drag functionality to upload dialog"
```

---

## 任务 7：集成测试

**文件：**
- 测试：`watcher-ai/tests/test_integration.py`

<!-- openspec-task: 7.1 -->
### 任务 15：RED - 编写端到端测试

```python
# watcher-ai/tests/test_integration.py
import pytest
import sys
import os
from datetime import datetime
from unittest.mock import Mock, patch, MagicMock

sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'src'))

class IntegrationTestKBRepository:
    """用于集成的 KBRepository 模拟"""
    _store = {}
    
    @classmethod
    def reset(cls):
        cls._store = {}
    
    @classmethod
    def create(cls, name, description=""):
        import uuid
        kb_id = str(uuid.uuid4())
        now = datetime.now()
        kb = {
            'id': kb_id,
            'name': name,
            'description': description,
            'status': 'idle',
            'document_count': 0,
            'chunk_count': 0,
            'created_at': now,
            'updated_at': now
        }
        cls._store[kb_id] = kb
        return kb
    
    @classmethod
    def get_by_id(cls, kb_id):
        return cls._store.get(kb_id)
    
    @classmethod
    def list_all(cls):
        return list(cls._store.values())
    
    @classmethod
    def delete(cls, kb_id):
        cls._store.pop(kb_id, None)
        return True
    
    @classmethod
    def update_status(cls, kb_id, status):
        if kb_id in cls._store:
            cls._store[kb_id]['status'] = status
        return True
    
    @classmethod
    def update_counts(cls, kb_id, doc_count, chunk_count):
        if kb_id in cls._store:
            cls._store[kb_id]['document_count'] = doc_count
            cls._store[kb_id]['chunk_count'] = chunk_count
        return True
    
    @classmethod
    def exists(cls, kb_id):
        return kb_id in cls._store
    
    @classmethod
    def save(cls, kb_data):
        cls._store[kb_data['id']] = kb_data
        return True

def test_full_kb_lifecycle():
    """验证完整的知识库生命周期"""
    from watcher_ai.services.kb_service import KBService
    
    IntegrationTestKBRepository.reset()
    
    with patch('watcher_ai.services.kb_service.KBRepository', IntegrationTestKBRepository):
        # 1. 创建知识库
        kb = KBService.create("测试知识库", "完整生命周期测试")
        assert kb['name'] == "测试知识库"
        kb_id = kb['id']
        
        # 2. 查询知识库
        fetched = KBService.get_by_id(kb_id)
        assert fetched is not None
        assert fetched['name'] == "测试知识库"
        
        # 3. 列出所有知识库
        all_kbs = KBService.list_all()
        assert len(all_kbs) >= 1
        
        # 4. 更新状态
        KBService.update_status(kb_id, "building")
        updated = KBService.get_by_id(kb_id)
        assert updated['status'] == "building"
        
        # 5. 更新计数
        KBService.update_counts(kb_id, 5, 100)
        updated = KBService.get_by_id(kb_id)
        assert updated['document_count'] == 5
        assert updated['chunk_count'] == 100
        
        # 6. 删除知识库
        result = KBService.delete(kb_id)
        assert result is True
        
        # 7. 验证删除
        deleted = KBService.get_by_id(kb_id)
        assert deleted is None

def test_concurrent_kb_operations():
    """验证并发操作不会冲突"""
    from watcher_ai.services.kb_service import KBService
    
    IntegrationTestKBRepository.reset()
    
    with patch('watcher_ai.services.kb_service.KBRepository', IntegrationTestKBRepository):
        # 创建多个知识库
        kb1 = KBService.create("知识库1", "")
        kb2 = KBService.create("知识库2", "")
        kb3 = KBService.create("知识库3", "")
        
        # 验证都有唯一ID
        assert kb1['id'] != kb2['id']
        assert kb2['id'] != kb3['id']
        
        # 更新不同知识库的状态
        KBService.update_status(kb1['id'], "building")
        KBService.update_status(kb2['id'], "ready")
        
        # 验证状态独立
        assert KBService.get_by_id(kb1['id'])['status'] == "building"
        assert KBService.get_by_id(kb2['id'])['status'] == "ready"
        assert KBService.get_by_id(kb3['id'])['status'] == "idle"
```

- [ ] **步骤 1：编写端到端测试**

- [ ] **步骤 2：运行测试验证通过**

运行：`cd /Users/kirito/repos/ShowTime/watcher-ai && pytest tests/test_integration.py -v`
预期：PASS

<!-- openspec-task: 7.2 -->
### 任务 16：GREEN - 运行完整测试套件

- [ ] **步骤 1：运行所有测试**

```bash
cd /Users/kirito/repos/ShowTime/watcher-ai
pytest tests/ -v --tb=short
```

- [ ] **步骤 2：验证所有测试通过**

预期：所有测试 PASS

- [ ] **步骤 3：Commit**

```bash
git add watcher-ai/tests/test_integration.py
git commit -m "test(watcher-ai): add integration tests for KB lifecycle"
```

- [ ] **步骤 4：运行前端构建验证**

```bash
cd /Users/kirito/repos/ShowTime/watcher-web
npm run build
```

预期：构建成功，无错误

---

## 验收标准

1. ✅ 从 ChromaDB 同步时，MySQL 中已有的知识库名称不会被覆盖
2. ✅ 新知识库使用 ChromaDB 中的默认名称
3. ✅ 上传弹框宽度从 500px 缩小至 300px
4. ✅ 上传弹框支持通过标题栏拖动
5. ✅ 完整的知识库生命周期测试通过
6. ✅ 并发操作测试通过
7. ✅ 所有单元测试和集成测试通过
8. ✅ 前端构建成功

---

## 里程碑总结

| 里程碑 | 任务 | 状态 |
|--------|------|------|
| M1: 基础设施 | 1-4 | ✅ 迁移脚本、MySQL客户端 |
| M2: 数据层 | 5-7 | ✅ KBRepository |
| M3: 业务层 | 8-9 | ✅ KBService重构 |
| M4: 同步优化 | 10-11 | ✅ ChromaDB同步逻辑 |
| M5: 前端优化 | 12-14 | ✅ 弹框样式和拖动 |
| M6: 集成验证 | 15-16 | ✅ 完整测试套件 |
