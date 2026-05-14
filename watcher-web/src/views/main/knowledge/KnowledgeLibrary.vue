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
          <el-button size="small" @click="goToDocs(row)">文档</el-button>
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
  (e: 'manage-docs', kb: KnowledgeBase): void
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
