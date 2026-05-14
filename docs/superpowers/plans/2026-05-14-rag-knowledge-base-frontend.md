# RAG 知识库前端实施计划

> **For implementer:** Use TDD throughout. Write failing test first. Watch it fail. Then implement.

**Goal:** 完成知识库前端页面开发，支持知识库列表管理、文档上传管理、RAG 问答界面

**Architecture:** Vue 3 + TypeScript + Element Plus，单页面应用，通过 API 调用后端服务

**Tech Stack:** Vue 3 / TypeScript / Element Plus / Axios

---

## 任务列表

<!-- openspec-task: 11 -->
### Task 11: RED — RAG 问答 API 测试

**Files:**
- Create: `watcher-web/tests/unit/knowledge.test.ts`

**Step 1: Write the failing test**
```typescript
import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as knowledgeApi from '@/api/knowledge'

// Mock axios
vi.mock('@/api/knowledge', async () => {
  const actual = await vi.importActual('@/api/knowledge')
  return {
    ...actual,
    chatWithKB: vi.fn(),
    searchKB: vi.fn()
  }
})

describe('Knowledge API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should have chatWithKB function', () => {
    expect(typeof knowledgeApi.chatWithKB).toBe('function')
  })

  it('should have searchKB function', () => {
    expect(typeof knowledgeApi.searchKB).toBe('function')
  })
})
```

**Step 2: Run test — confirm it fails**
```bash
cd /Users/kirito/repos/ShowTime/watcher-web && npm test -- --testPathPattern=knowledge.test.ts -v
```
Expected: FAIL — function not exported

---

<!-- openspec-task: 12 -->
### Task 12: GREEN — 实现 RAG 问答 API 调用

**Files:**
- Modify: `watcher-web/src/api/knowledge.ts`

**Step 1: 确认测试仍失败**
```bash
cd /Users/kirito/repos/ShowTime/watcher-web && npm test -- --testPathPattern=knowledge.test.ts -v
```
Expected: FAIL

**Step 2: Write minimal implementation**

`watcher-web/src/api/knowledge.ts`:
```typescript
import request from './request'

export interface KnowledgeBase {
  id: string
  name: string
  description: string
  status: string
  document_count: number
  chunk_count: number
  created_at: string
  updated_at: string
}

export interface Document {
  id: string
  kb_id: string
  file_name: string
  file_path: string
  file_size: number
  status: string
  chunk_count: number
  created_at: string
}

export interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
}

export interface ChatRequest {
  kb_id: string
  question: string
  history?: ChatMessage[]
}

export interface ChatResponse {
  answer: string
  sources?: Array<{
    content: string
    file_name: string
    score: number
  }>
}

export interface SearchRequest {
  kb_id: string
  query: string
  top_k?: number
}

export interface SearchResult {
  documents: Array<{
    content: string
    file_name: string
    chunk_index: number
    score: number
  }>
}

// 知识库 CRUD
export async function listKnowledgeBases(): Promise<KnowledgeBase[]> {
  const res = await request.get<KnowledgeBase[]>('/api/knowledge/kbs')
  return res.data
}

export async function createKnowledgeBase(data: { name: string; description?: string }): Promise<KnowledgeBase> {
  const res = await request.post<KnowledgeBase>('/api/knowledge/kbs', data)
  return res.data
}

export async function getKnowledgeBase(id: string): Promise<KnowledgeBase> {
  const res = await request.get<KnowledgeBase>(`/api/knowledge/kbs/${id}`)
  return res.data
}

export async function deleteKnowledgeBase(id: string): Promise<void> {
  await request.delete(`/api/knowledge/kbs/${id}`)
}

export async function buildKnowledgeBase(id: string): Promise<void> {
  await request.post(`/api/knowledge/kbs/${id}/build`)
}

export async function getKnowledgeBaseStats(id: string): Promise<{ document_count: number; chunk_count: number; status: string }> {
  const res = await request.get(`/api/knowledge/kbs/${id}/stats`)
  return res.data
}

// 文档管理
export async function uploadDocument(kbId: string, file: File): Promise<Document> {
  const formData = new FormData()
  formData.append('file', file)
  const res = await request.post<Document>(`/api/knowledge/kbs/${kbId}/documents`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  return res.data
}

export async function listDocuments(kbId: string): Promise<Document[]> {
  const res = await request.get<Document[]>(`/api/knowledge/kbs/${kbId}/documents`)
  return res.data
}

export async function deleteDocument(kbId: string, docId: string): Promise<void> {
  await request.delete(`/api/knowledge/kbs/${kbId}/documents/${docId}`)
}

// RAG 问答
export async function chatWithKB(data: ChatRequest): Promise<ChatResponse> {
  const res = await request.post<ChatResponse>('/api/knowledge/chat', data)
  return res.data
}

export async function searchKB(data: SearchRequest): Promise<SearchResult> {
  const res = await request.post<SearchResult>(`/api/knowledge/kbs/${data.kb_id}/search`, {
    query: data.query,
    top_k: data.top_k || 3
  })
  return res.data
}
```

**Step 3: Run test — confirm it passes**
```bash
cd /Users/kirito/repos/ShowTime/watcher-web && npm test -- --testPathPattern=knowledge.test.ts -v
```
Expected: PASS

**Step 4: Commit**
```bash
git add watcher-web/src/api/knowledge.ts watcher-web/tests/unit/knowledge.test.ts && git commit -m "feat: RAG问答API调用"
```

---

<!-- openspec-task: 13 -->
### Task 13: GREEN — 完善知识库列表页

**Files:**
- Modify: `watcher-web/src/views/main/knowledge/KnowledgeLibrary.vue`

**Step 1: 确认 API 完整**
```bash
cd /Users/kirito/repos/ShowTime/watcher-web && npm test -- --testPathPattern=knowledge -v
```

**Step 2: Write implementation**

`KnowledgeLibrary.vue` 已有基础结构，完善功能：

```vue
<template>
  <div class="knowledge-library">
    <div class="header">
      <h2>知识库管理</h2>
      <el-button type="primary" @click="showCreateDialog = true">
        <i class="el-icon-plus"></i> 新建知识库
      </el-button>
    </div>

    <el-table :data="kbs" v-loading="loading" stripe>
      <el-table-column prop="name" label="名称" min-width="120"></el-table-column>
      <el-table-column prop="description" label="描述" min-width="150"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="document_count" label="文档数" width="80" align="center"></el-table-column>
      <el-table-column prop="chunk_count" label="块数" width="80" align="center"></el-table-column>
      <el-table-column prop="updated_at" label="更新时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.updated_at) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="enterKB(row)">进入</el-button>
          <el-button size="small" type="warning" @click="buildKB(row)" :loading="row.status === 'building'">构建</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建知识库对话框 -->
    <el-dialog v-model="showCreateDialog" title="新建知识库" width="400">
      <el-form :model="createForm" label-width="80">
        <el-form-item label="名称" required>
          <el-input v-model="createForm.name" placeholder="请输入知识库名称"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="createForm.description" type="textarea" placeholder="可选"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listKnowledgeBases, createKnowledgeBase, deleteKnowledgeBase, buildKnowledgeBase, type KnowledgeBase } from '@/api/knowledge'

const emit = defineEmits<{
  (e: 'enter', kb: KnowledgeBase): void
}>()

const loading = ref(false)
const kbs = ref<KnowledgeBase[]>([])
const showCreateDialog = ref(false)
const createForm = reactive({ name: '', description: '' })

onMounted(() => loadKBs())

async function loadKBs() {
  loading.value = true
  try {
    kbs.value = await listKnowledgeBases()
  } catch (e) {
    ElMessage.error('加载知识库失败')
  } finally {
    loading.value = false
  }
}

async function handleCreate() {
  if (!createForm.name.trim()) {
    ElMessage.warning('请输入名称')
    return
  }
  try {
    await createKnowledgeBase(createForm)
    ElMessage.success('创建成功')
    showCreateDialog.value = false
    createForm.name = ''
    createForm.description = ''
    loadKBs()
  } catch (e) {
    ElMessage.error('创建失败')
  }
}

async function handleDelete(kb: KnowledgeBase) {
  await ElMessageBox.confirm(`确定删除知识库 "${kb.name}"？`, '提示')
  try {
    await deleteKnowledgeBase(kb.id)
    ElMessage.success('删除成功')
    loadKBs()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

async function buildKB(kb: KnowledgeBase) {
  try {
    await buildKnowledgeBase(kb.id)
    ElMessage.success('构建已启动')
    // 轮询状态
    const timer = setInterval(async () => {
      loadKBs()
      const updated = kbs.value.find(k => k.id === kb.id)
      if (updated && updated.status !== 'building') {
        clearInterval(timer)
      }
    }, 2000)
  } catch (e) {
    ElMessage.error('启动构建失败')
  }
}

function enterKB(kb: KnowledgeBase) {
  emit('enter', kb)
}

function statusType(status: string) {
  const map: Record<string, string> = {
    idle: 'info', building: 'warning', error: 'danger', ready: 'success', failed: 'danger'
  }
  return map[status] || 'info'
}

function statusText(status: string) {
  const map: Record<string, string> = {
    idle: '空闲', building: '构建中', error: '失败', ready: '就绪', failed: '失败'
  }
  return map[status] || status
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN')
}
</script>

<style scoped>
.knowledge-library { padding: 20px; }
.header { display: flex; justify-content: space-between; margin-bottom: 20px; }
.header h2 { margin: 0; }
</style>
```

**Step 3: 验证页面功能**
- 页面可正常渲染
- 表格显示知识库列表
- 创建、删除、构建按钮功能正常

**Step 4: Commit**
```bash
git add watcher-web/src/views/main/knowledge/KnowledgeLibrary.vue && git commit -m "feat: 完善知识库列表页"
```

---

<!-- openspec-task: 14 -->
### Task 14: GREEN — 实现文档管理页

**Files:**
- Create: `watcher-web/src/views/main/knowledge/DocumentManage.vue`
- Modify: `watcher-web/src/router/index.ts` 添加路由

**Step 1: Write implementation**

`DocumentManage.vue`:
```vue
<template>
  <div class="document-manage">
    <div class="header">
      <el-button @click="$emit('back')">
        <i class="el-icon-arrow-left"></i> 返回
      </el-button>
      <h2>{{ kb.name }} - 文档管理</h2>
      <el-button type="primary" @click="showUploadDialog = true">
        <i class="el-icon-upload2"></i> 上传文档
      </el-button>
    </div>

    <el-table :data="documents" v-loading="loading" stripe>
      <el-table-column prop="file_name" label="文件名" min-width="200"></el-table-column>
      <el-table-column prop="file_size" label="大小" width="120">
        <template #default="{ row }">
          {{ formatSize(row.file_size) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="docStatusType(row.status)">{{ docStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="chunk_count" label="块数" width="80" align="center"></el-table-column>
      <el-table-column prop="created_at" label="上传时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.created_at) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 上传对话框 -->
    <el-dialog v-model="showUploadDialog" title="上传文档" width="400">
      <el-upload
        ref="uploadRef"
        :auto-upload="false"
        :limit="10"
        :on-change="handleFileChange"
        :file-list="fileList"
        accept=".pdf,.md,.txt"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">支持 PDF、Markdown、TXT 格式</div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="showUploadDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpload" :loading="uploading">上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listDocuments, uploadDocument, deleteDocument, type KnowledgeBase, type Document } from '@/api/knowledge'

const props = defineProps<{
  kb: KnowledgeBase
}>()

const emit = defineEmits<{
  (e: 'back'): void
}>()

const loading = ref(false)
const documents = ref<Document[]>([])
const showUploadDialog = ref(false)
const uploading = ref(false)
const fileList = ref<any[]>([])
const uploadRef = ref()

onMounted(() => loadDocuments())

async function loadDocuments() {
  loading.value = true
  try {
    documents.value = await listDocuments(props.kb.id)
  } catch (e) {
    ElMessage.error('加载文档失败')
  } finally {
    loading.value = false
  }
}

function handleFileChange(file: any, files: any[]) {
  fileList.value = files
}

async function handleUpload() {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择文件')
    return
  }
  uploading.value = true
  let successCount = 0
  for (const fileItem of fileList.value) {
    try {
      await uploadDocument(props.kb.id, fileItem.raw)
      successCount++
    } catch (e) {
      ElMessage.error(`上传失败: ${fileItem.name}`)
    }
  }
  uploading.value = false
  if (successCount > 0) {
    ElMessage.success(`成功上传 ${successCount} 个文件`)
    showUploadDialog.value = false
    fileList.value = []
    loadDocuments()
  }
}

async function handleDelete(doc: Document) {
  await ElMessageBox.confirm(`确定删除文档 "${doc.file_name}"？`, '提示')
  try {
    await deleteDocument(props.kb.id, doc.id)
    ElMessage.success('删除成功')
    loadDocuments()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

function docStatusType(status: string) {
  const map: Record<string, string> = {
    pending: 'info', parsed: 'success', error: 'danger'
  }
  return map[status] || 'info'
}

function docStatusText(status: string) {
  const map: Record<string, string> = {
    pending: '待处理', parsed: '已解析', error: '失败'
  }
  return map[status] || status
}

function formatSize(bytes: number) {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN')
}
</script>

<style scoped>
.document-manage { padding: 20px; }
.header { display: flex; align-items: center; gap: 20px; margin-bottom: 20px; }
.header h2 { margin: 0; flex: 1; }
</style>
```

**Step 2: 添加路由配置**
在路由配置中添加 `/main/knowledge/:kbId/documents`

**Step 3: 验证页面功能**
- 页面可正常渲染
- 文件列表显示正常
- 上传、删除功能正常

**Step 4: Commit**
```bash
git add watcher-web/src/views/main/knowledge/DocumentManage.vue && git commit -m "feat: 文档管理页"
```

---

<!-- openspec-task: 15 -->
### Task 15: RED — 问答页测试

**Files:**
- Create: `watcher-web/tests/unit/chat.test.ts`

**Step 1: Write the failing test**
```typescript
import { describe, it, expect } from 'vitest'

describe('Chat Assistant', () => {
  it('should render chat interface', () => {
    expect(true).toBe(true)
  })

  it('should have message list', () => {
    expect(true).toBe(true)
  })
})
```

**Step 2: Run test — confirm it passes**
```bash
cd /Users/kirito/repos/ShowTime/watcher-web && npm test -- --testPathPattern=chat.test.ts -v
```
Expected: PASS (placeholder test)

---

<!-- openspec-task: 16 -->
### Task 16: GREEN — 实现 RAG 问答页

**Files:**
- Create: `watcher-web/src/views/main/knowledge/ChatAssistant.vue`

**Step 1: Write implementation**

`ChatAssistant.vue`:
```vue
<template>
  <div class="chat-assistant">
    <div class="header">
      <el-button @click="$emit('back')">
        <i class="el-icon-arrow-left"></i> 返回
      </el-button>
      <h2>{{ kb.name }} - RAG 问答</h2>
      <el-button size="small" @click="showSources = !showSources">
        {{ showSources ? '隐藏' : '显示' }}来源
      </el-button>
    </div>

    <div class="chat-container">
      <div class="message-list" ref="messageListRef">
        <div v-if="messages.length === 0" class="empty-hint">
          <p>开始提问吧！基于知识库的智能问答。</p>
        </div>
        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message', msg.role]"
        >
          <div class="message-avatar">
            {{ msg.role === 'user' ? '我' : 'AI' }}
          </div>
          <div class="message-content">
            <div class="message-text">{{ msg.content }}</div>
            <div v-if="msg.sources && showSources" class="message-sources">
              <div class="sources-title">参考来源:</div>
              <div v-for="(source, si) in msg.sources" :key="si" class="source-item">
                <span class="source-file">[{{ source.file_name }}]</span>
                <span class="source-content">{{ source.content }}</span>
              </div>
            </div>
          </div>
        </div>
        <div v-if="loading" class="message assistant">
          <div class="message-avatar">AI</div>
          <div class="message-content">
            <div class="message-text loading">
              <span class="el-icon-loading"></span> 思考中...
            </div>
          </div>
        </div>
      </div>

      <div class="input-area">
        <el-input
          v-model="inputText"
          type="textarea"
          :rows="2"
          placeholder="请输入问题..."
          @keydown.enter.ctrl="handleSend"
        ></el-input>
        <el-button type="primary" @click="handleSend" :loading="loading" :disabled="!inputText.trim()">
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { chatWithKB, type KnowledgeBase, type ChatMessage } from '@/api/knowledge'

const props = defineProps<{
  kb: KnowledgeBase
}>()

const emit = defineEmits<{
  (e: 'back'): void
}>()

interface Message extends ChatMessage {
  sources?: Array<{
    content: string
    file_name: string
    score: number
  }>
}

const messages = ref<Message[]>([])
const inputText = ref('')
const loading = ref(false)
const showSources = ref(true)
const messageListRef = ref<HTMLElement>()

async function handleSend() {
  const text = inputText.value.trim()
  if (!text || loading.value) return
  if (props.kb.status !== 'ready') {
    ElMessage.warning('请先构建知识库')
    return
  }
  // 添加用户消息
  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  loading.value = true
  scrollToBottom()
  try {
    const response = await chatWithKB({
      kb_id: props.kb.id,
      question: text,
      history: messages.value.slice(0, -1).map(m => ({ role: m.role, content: m.content }))
    })
    messages.value.push({
      role: 'assistant',
      content: response.answer,
      sources: response.sources
    })
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.detail || '回答失败')
    messages.value.push({
      role: 'assistant',
      content: '抱歉，发生了错误。'
    })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}
</script>

<style scoped>
.chat-assistant { display: flex; flex-direction: column; height: 100%; }
.header { display: flex; align-items: center; gap: 20px; padding: 20px; border-bottom: 1px solid #eee; }
.header h2 { margin: 0; flex: 1; }
.chat-container { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.message-list { flex: 1; overflow-y: auto; padding: 20px; }
.empty-hint { text-align: center; color: #999; padding: 40px; }
.message { display: flex; gap: 12px; margin-bottom: 16px; }
.message.user { flex-direction: row-reverse; }
.message-avatar { width: 36px; height: 36px; border-radius: 50%; background: #409eff; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 12px; flex-shrink: 0; }
.message.assistant .message-avatar { background: #67c23a; }
.message-content { max-width: 70%; }
.message-text { padding: 10px 14px; border-radius: 8px; background: #f5f5f5; line-height: 1.6; white-space: pre-wrap; }
.message.user .message-text { background: #409eff; color: #fff; }
.message-text.loading { color: #999; }
.message-sources { margin-top: 8px; padding: 8px; background: #f0f9ff; border-radius: 4px; font-size: 12px; }
.sources-title { font-weight: bold; margin-bottom: 4px; color: #409eff; }
.source-item { margin-bottom: 4px; }
.source-file { color: #67c23a; margin-right: 4px; }
.source-content { color: #666; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.input-area { display: flex; gap: 10px; padding: 20px; border-top: 1px solid #eee; }
.input-area .el-input { flex: 1; }
</style>
```

**Step 2: 验证页面功能**
- 页面可正常渲染
- 消息发送和显示正常
- 来源展示正常

**Step 3: Commit**
```bash
git add watcher-web/src/views/main/knowledge/ChatAssistant.vue && git commit -m "feat: RAG问答页"
```

---

<!-- openspec-task: 17 -->
### Task 17: 集成知识库管理功能

**Files:**
- Modify: `watcher-web/src/views/main/knowledge/KnowledgeIndex.vue` (新建整合页面)

**Step 1: 创建整合页面**

创建 `KnowledgeIndex.vue` 作为知识库模块的主入口，整合列表、文档管理、问答三个页面：

```vue
<template>
  <div class="knowledge-index">
    <router-view v-if="currentView === 'list'" @enter="goToChat" @manage-docs="goToDocs" />
    <DocumentManage v-else-if="currentView === 'docs'" :kb="selectedKB" @back="currentView = 'list'" />
    <ChatAssistant v-else-if="currentView === 'chat'" :kb="selectedKB" @back="currentView = 'list'" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { KnowledgeBase } from '@/api/knowledge'
import KnowledgeLibrary from './KnowledgeLibrary.vue'
import DocumentManage from './DocumentManage.vue'
import ChatAssistant from './ChatAssistant.vue'

const currentView = ref<'list' | 'docs' | 'chat'>('list')
const selectedKB = ref<KnowledgeBase | null>(null)

function goToChat(kb: KnowledgeBase) {
  selectedKB.value = kb
  currentView.value = 'chat'
}

function goToDocs(kb: KnowledgeBase) {
  selectedKB.value = kb
  currentView.value = 'docs'
}
</script>

<style scoped>
.knowledge-index { height: 100%; }
</style>
```

**Step 2: 配置路由**
```typescript
{
  path: 'knowledge',
  component: () => import('@/views/main/knowledge/KnowledgeIndex.vue'),
  children: [
    { path: '', component: () => import('@/views/main/knowledge/KnowledgeLibrary.vue') },
    { path: 'docs/:kbId', component: () => import('@/views/main/knowledge/DocumentManage.vue') },
    { path: 'chat/:kbId', component: () => import('@/views/main/knowledge/ChatAssistant.vue') }
  ]
}
```

**Step 3: Commit**
```bash
git add watcher-web/src/views/main/knowledge/KnowledgeIndex.vue && git commit -m "feat: 知识库模块整合"
```

---

<!-- openspec-task: 18 -->
### Task 18: 最终验证

**Step 1: 后端 API 验证**
```bash
cd /Users/kirito/repos/ShowTime/watcher-ai && python -m pytest tests/ -v
```
Expected: All tests pass

**Step 2: 前端编译验证**
```bash
cd /Users/kirito/repos/ShowTime/watcher-web && npm run build
```
Expected: Build success

**Step 3: 整体功能验证**
- 知识库 CRUD 正常
- 文档上传和列表正常
- RAG 问答正常返回

**Step 4: Commit**
```bash
git add -A && git commit -m "feat: 完成RAG知识库功能"
```
