<template>
  <div class="knowledge-library">
    <div class="header">
      <h2>知识库管理</h2>
      <el-button type="primary" @click="showCreateDialog = true">
        <i class="el-icon-plus"></i> 新建知识库
      </el-button>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="知识库列表" name="kbs">
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
          <el-table-column label="操作" width="320" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="enterKB(row)">进入</el-button>
              <el-button size="small" @click="goToDocs(row)">文档</el-button>
              <el-button size="small" type="warning" @click="buildKB(row)" :loading="row.status === 'building'">构建</el-button>
              <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="对话历史" name="history">
        <div class="history-header">
          <el-select v-model="selectedKbId" placeholder="选择知识库" clearable style="width: 200px">
            <el-option v-for="kb in kbs" :key="kb.id" :label="kb.name" :value="kb.id"></el-option>
          </el-select>
          <el-button @click="loadHistory" :loading="historyLoading">刷新</el-button>
        </div>
        <el-table :data="historySessions" v-loading="historyLoading" stripe>
          <el-table-column prop="title" label="标题" min-width="120">
            <template #default="{ row }">
              {{ row.title || '未命名会话' }}
            </template>
          </el-table-column>
          <el-table-column prop="message_count" label="消息数" width="80" align="center"></el-table-column>
          <el-table-column prop="updated_at" label="更新时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.updated_at) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="viewHistorySession(row)">查看</el-button>
              <el-button size="small" type="danger" @click="deleteHistorySession(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="historyTotal > 0" class="pagination">
          <el-pagination
            v-model:current-page="historyPage"
            :page-size="20"
            :total="historyTotal"
            layout="prev, pager, next"
            @current-change="loadHistory"
          ></el-pagination>
        </div>
      </el-tab-pane>
    </el-tabs>

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

    <!-- 编辑知识库对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑知识库" width="400">
      <el-form :model="editForm" label-width="80">
        <el-form-item label="名称" required>
          <el-input v-model="editForm.name" placeholder="请输入知识库名称"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" type="textarea" placeholder="可选"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 历史会话详情对话框 -->
    <el-dialog v-model="showHistoryDialog" title="对话详情" width="700">
      <div class="session-messages">
        <div v-for="(msg, idx) in sessionMessages" :key="idx" :class="['message', msg.role]">
          <div class="message-avatar">{{ msg.role === 'user' ? '我' : 'AI' }}</div>
          <div class="message-content">{{ msg.content }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listKnowledgeBases, createKnowledgeBase, deleteKnowledgeBase, buildKnowledgeBase, listChatHistory, getSessionDetail, deleteSession, updateKnowledgeBase, type KnowledgeBase, type ChatSession, type ChatHistoryItem } from '@/api/knowledge'

const emit = defineEmits<{
  (e: 'enter', kb: KnowledgeBase): void
  (e: 'manage-docs', kb: KnowledgeBase): void
}>()

const loading = ref(false)
const kbs = ref<KnowledgeBase[]>([])
const showCreateDialog = ref(false)
const createForm = reactive({ name: '', description: '' })

// 历史相关
const activeTab = ref('kbs')
const selectedKbId = ref('')
const historyLoading = ref(false)
const historySessions = ref<ChatSession[]>([])
const historyPage = ref(1)
const historyTotal = ref(0)
const showHistoryDialog = ref(false)
const sessionMessages = ref<ChatHistoryItem[]>([])
const currentUserId = 'default_user' // TODO: 从用户系统获取

// 编辑相关
const showEditDialog = ref(false)
const editingKB = ref<KnowledgeBase | null>(null)
const editForm = reactive({ name: '', description: '' })

onMounted(() => loadKBs())

watch(activeTab, (tab) => {
  if (tab === 'history' && historySessions.value.length === 0) {
    loadHistory()
  }
})

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

function goToDocs(kb: KnowledgeBase) {
  emit('manage-docs', kb)
}

function openEditDialog(kb: KnowledgeBase) {
  editingKB.value = kb
  editForm.name = kb.name
  editForm.description = kb.description
  showEditDialog.value = true
}

async function handleEdit() {
  if (!editingKB.value || !editForm.name.trim()) {
    ElMessage.warning('请输入名称')
    return
  }
  try {
    await updateKnowledgeBase(editingKB.value.id, editForm)
    ElMessage.success('更新成功')
    showEditDialog.value = false
    loadKBs()
  } catch (e) {
    ElMessage.error('更新失败')
  }
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

// 历史相关方法
async function loadHistory() {
  historyLoading.value = true
  try {
    const params: any = { user_id: currentUserId, page: historyPage.value }
    if (selectedKbId.value) {
      params.kb_id = selectedKbId.value
    }
    const res = await listChatHistory(currentUserId, historyPage.value, 20)
    historySessions.value = res.sessions
    historyTotal.value = res.total
  } catch (e) {
    ElMessage.error('加载历史记录失败')
  } finally {
    historyLoading.value = false
  }
}

async function viewHistorySession(session: ChatSession) {
  try {
    const res = await getSessionDetail(session.id, currentUserId)
    sessionMessages.value = res.messages
    showHistoryDialog.value = true
  } catch (e) {
    ElMessage.error('加载会话详情失败')
  }
}

async function deleteHistorySession(session: ChatSession) {
  await ElMessageBox.confirm('确定删除此会话？', '提示')
  try {
    await deleteSession(session.id, currentUserId)
    ElMessage.success('删除成功')
    loadHistory()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}
</script>

<style scoped>
.knowledge-library { padding: 20px; }
.header { display: flex; justify-content: space-between; margin-bottom: 20px; }
.header h2 { margin: 0; }
.history-header { display: flex; gap: 10px; margin-bottom: 15px; }
.pagination { margin-top: 15px; display: flex; justify-content: center; }
.session-messages { max-height: 400px; overflow-y: auto; }
.message { display: flex; gap: 10px; margin-bottom: 15px; }
.message.user { flex-direction: row-reverse; }
.message-avatar { width: 32px; height: 32px; border-radius: 50%; background: #409eff; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 12px; flex-shrink: 0; }
.message.assistant .message-avatar { background: #67c23a; }
.message-content { max-width: 70%; padding: 8px 12px; border-radius: 8px; background: #f5f5f5; line-height: 1.5; }
.message.user .message-content { background: #409eff; color: #fff; }
</style>
