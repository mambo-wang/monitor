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
    <el-dialog 
      v-model="showUploadDialog" 
      title="上传文档" 
      width="300px"
      v-drag
      class="upload-dialog">
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
import drag from '@/directive/drag'

// 注册拖动指令
const vDrag = drag

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
