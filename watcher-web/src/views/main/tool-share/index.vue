<template>
  <div class="tool-share-container">
    <div class="content">
      <div class="folder-panel">
        <div class="panel-header">
          <h3>{{ $t('message.toolShare.folders') }}</h3>
          <div class="header-actions">
            <el-button size="small" type="danger" text @click="showDeleteFolderDialog">
              <el-icon><delete /></el-icon>
            </el-button>
            <el-button size="small" type="primary" @click="showCreateFolderDialog">
              <el-icon><folder-add /></el-icon>
            </el-button>
          </div>
        </div>
        <div v-if="folders.length === 0" class="empty-tip">
          {{ $t('message.toolShare.noFolders') }}
        </div>
        <div
          v-for="folder in folders"
          :key="folder.id"
          class="folder-item"
          :class="{ active: selectedFolderId === folder.id }"
          @click="enterFolder(folder)"
        >
          <el-icon><folder /></el-icon>
          <span class="folder-name">{{ folder.name }}</span>
        </div>
      </div>

      <div class="file-panel">
        <div class="panel-header">
          <h3>{{ $t('message.toolShare.files') }}</h3>
          <el-button size="small" type="success" @click="showUploadDialog">
            <el-icon><upload /></el-icon>
            {{ $t('message.toolShare.uploadFile') }}
          </el-button>
        </div>
        <el-table :data="files" style="width: 100%">
          <el-table-column prop="toolName" :label="$t('message.toolShare.toolName')" />
          <el-table-column prop="toolDesc" :label="$t('message.toolShare.toolDesc')" />
          <el-table-column prop="downloadCount" :label="$t('message.toolShare.downloadCount')" width="120" />
          <el-table-column :label="$t('message.toolShare.actions')" width="120">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="downloadFile(row)">
                {{ $t('message.toolShare.download') }}
              </el-button>
              <el-button size="small" type="danger" @click="handleDeleteFile(row)">
                <el-icon><delete /></el-icon>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="files.length === 0" class="empty-tip">
          {{ $t('message.toolShare.noFiles') }}
        </div>
      </div>
    </div>

    <!-- 新建文件夹对话框 -->
    <el-dialog v-model="createFolderDialogVisible" :title="$t('message.toolShare.createFolder')" width="400px">
      <el-form>
        <el-form-item :label="$t('message.toolShare.folderName')">
          <el-input v-model="newFolderName" :placeholder="$t('message.toolShare.folderNamePlaceholder')" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createFolderDialogVisible = false">{{ $t('message.common.cancel') }}</el-button>
        <el-button type="primary" @click="createFolder">{{ $t('message.common.confirm') }}</el-button>
      </template>
    </el-dialog>

    <!-- 删除文件夹对话框 -->
    <el-dialog v-model="deleteFolderDialogVisible" title="删除文件夹" width="400px">
      <el-form>
        <el-form-item label="选择文件夹">
          <el-select v-model="folderToDeleteId" placeholder="请选择要删除的文件夹">
            <el-option
              v-for="folder in folders"
              :key="folder.id"
              :label="folder.name"
              :value="folder.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deleteFolderDialogVisible = false">{{ $t('message.common.cancel') }}</el-button>
        <el-button type="danger" @click="confirmDeleteFolder">{{ $t('message.common.confirm') }}</el-button>
      </template>
    </el-dialog>

    <!-- 上传文件对话框 -->
    <el-dialog v-model="uploadDialogVisible" :title="$t('message.toolShare.uploadFile')" width="500px">
      <el-form>
        <el-form-item :label="$t('message.toolShare.selectFile')">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :file-list="fileList"
          >
            <el-button>{{ $t('message.toolShare.chooseFile') }}</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item :label="$t('message.toolShare.toolName')" required>
          <el-input v-model="uploadForm.toolName" :placeholder="$t('message.toolShare.toolNamePlaceholder')" />
        </el-form-item>
        <el-form-item :label="$t('message.toolShare.toolDesc')">
          <el-input v-model="uploadForm.toolDesc" type="textarea" :placeholder="$t('message.toolShare.toolDescPlaceholder')" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">{{ $t('message.common.cancel') }}</el-button>
        <el-button type="primary" :loading="uploading" @click="uploadFile">{{ $t('message.common.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFoldersApi, createFolderApi, getFilesApi, uploadFileApi, getDownloadUrl, deleteFolderApi, deleteFileApi } from '@/api/tool-share/api'

const folders = ref<any[]>([])
const files = ref<any[]>([])
const currentFolderId = ref<number | null>(null)
const selectedFolderId = ref<number | null>(null)

// 新建文件夹
const createFolderDialogVisible = ref(false)
const newFolderName = ref('')

// 删除文件夹
const deleteFolderDialogVisible = ref(false)
const folderToDeleteId = ref<number | null>(null)

// 上传文件
const uploadDialogVisible = ref(false)
const uploading = ref(false)
const fileList = ref<any[]>([])
const selectedFile = ref<any>(null)
const uploadForm = reactive({
  toolName: '',
  toolDesc: ''
})

onMounted(async () => {
  await loadData()
})

const loadData = async () => {
  await loadFolders()
  await loadFiles()
}

const loadFolders = async () => {
  try {
    const res = await getFoldersApi(null)
    folders.value = res.data || []
  } catch (error) {
    console.error('加载文件夹失败', error)
    folders.value = []
  }
}

const loadFiles = async () => {
  try {
    const res = await getFilesApi(currentFolderId.value)
    files.value = res.data || []
  } catch (error) {
    console.error('加载文件列表失败', error)
    files.value = []
  }
}

const showCreateFolderDialog = () => {
  newFolderName.value = ''
  createFolderDialogVisible.value = true
}

const createFolder = async () => {
  if (!newFolderName.value.trim()) {
    ElMessage.warning({
      message: '请输入文件夹名称',
      type: 'warning'
    })
    return
  }
  try {
    await createFolderApi({ name: newFolderName.value.trim() })
    ElMessage.success({
      message: '创建文件夹成功',
      type: 'success'
    })
    createFolderDialogVisible.value = false
    await loadFolders()
  } catch (error) {
    console.error('创建文件夹失败', error)
  }
}

const enterFolder = (folder: any) => {
  selectedFolderId.value = folder.id
  currentFolderId.value = folder.id
  loadFiles()
}

const showDeleteFolderDialog = () => {
  folderToDeleteId.value = null
  deleteFolderDialogVisible.value = true
}

const confirmDeleteFolder = async () => {
  if (!folderToDeleteId.value) {
    ElMessage.warning({ message: '请选择要删除的文件夹', type: 'warning' })
    return
  }
  try {
    const res = await deleteFolderApi(folderToDeleteId.value)
    if (res.success) {
      ElMessage.success({ message: '删除成功', type: 'success' })
      deleteFolderDialogVisible.value = false
      if (selectedFolderId.value === folderToDeleteId.value) {
        selectedFolderId.value = null
        currentFolderId.value = null
      }
      await loadFolders()
    } else {
      ElMessage.error({ message: res.message || '删除失败', type: 'error' })
    }
  } catch (error) {
    console.error('删除文件夹失败', error)
  }
}

const handleDeleteFile = async (row: any) => {
  try {
    await deleteFileApi(row.id)
    ElMessage.success({ message: '删除成功', type: 'success' })
    await loadFiles()
  } catch (error) {
    console.error('删除文件失败', error)
  }
}

const showUploadDialog = () => {
  fileList.value = []
  selectedFile.value = null
  uploadForm.toolName = ''
  uploadForm.toolDesc = ''
  uploadDialogVisible.value = true
}

const handleFileChange = (file: any) => {
  selectedFile.value = file.raw
}

const downloadFile = (row: any) => {
  window.open(getDownloadUrl(row.id), '_blank')
}

const uploadFile = async () => {
  if (!selectedFile.value) {
    ElMessage.warning({
      message: '请选择文件',
      type: 'warning'
    })
    return
  }
  if (!uploadForm.toolName.trim()) {
    ElMessage.warning({
      message: '请输入工具名称',
      type: 'warning'
    })
    return
  }
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('toolName', uploadForm.toolName.trim())
    formData.append('toolDesc', uploadForm.toolDesc.trim())
    if (currentFolderId.value !== null) {
      formData.append('folderId', String(currentFolderId.value))
    }
    await uploadFileApi(formData)
    ElMessage.success({
      message: '上传成功',
      type: 'success'
    })
    uploadDialogVisible.value = false
    await loadFiles()
  } catch (error) {
    console.error('上传文件失败', error)
  } finally {
    uploading.value = false
  }
}
</script>

<style lang="scss" scoped>
.tool-share-container {
  padding: 20px;
  height: 100%;

  .content {
    display: flex;
    gap: 20px;
    height: calc(100% - 0px);
  }

  .folder-panel {
    width: 300px;
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    flex-direction: column;

    .panel-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      h3 {
        margin: 0;
      }

      .header-actions {
        display: flex;
        gap: 8px;
      }
    }

    .folder-item {
      display: flex;
      align-items: center;
      padding: 12px;
      cursor: pointer;
      border-radius: 4px;
      margin-bottom: 4px;

      &:hover {
        background: #f5f7fa;
      }

      &.active {
        background: #409eff;
        color: #fff;

        &:hover {
          background: #409eff;
        }
      }

      .folder-name {
        margin-left: 8px;
      }
    }
  }

  .file-panel {
    flex: 1;
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    flex-direction: column;

    .panel-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      h3 {
        margin: 0;
      }
    }
  }

  .empty-tip {
    color: #999;
    text-align: center;
    padding: 20px;
  }
}
</style>