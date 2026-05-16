<template>
  <el-dialog
    v-model="visible"
    title="历史会话"
    width="600px"
    @close="handleClose"
  >
    <div class="session-list" v-if="sessions.length > 0">
      <div
        v-for="session in sessions"
        :key="session.id"
        class="session-item"
        @click="handleSelectSession(session)"
      >
        <div class="session-info">
          <div class="session-title">{{ session.title || '无标题会话' }}</div>
          <div class="session-meta">
            <span>{{ formatDate(session.created_at) }}</span>
            <span>{{ session.message_count }} 条消息</span>
          </div>
        </div>
        <el-button
          type="danger"
          size="small"
          circle
          icon="el-icon-delete"
          @click.stop="handleDeleteSession(session.id)"
        ></el-button>
      </div>
    </div>
    <el-empty v-else description="暂无历史会话"></el-empty>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getChatSessions, deleteChatSession } from '@/api/knowledge'

const props = defineProps<{
  modelValue: boolean
  kbId: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'select-session', session: any): void
}>()

const visible = ref(props.modelValue)
const sessions = ref<any[]>([])

watch(() => props.modelValue, async (val) => {
  visible.value = val
  if (val) {
    await loadSessions()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

async function loadSessions() {
  try {
    const res = await getChatSessions(props.kbId)
    sessions.value = res.sessions || []
  } catch (e) {
    ElMessage.error('加载会话列表失败')
  }
}

function handleSelectSession(session: any) {
  emit('select-session', session)
  visible.value = false
}

async function handleDeleteSession(sessionId: string) {
  try {
    await ElMessageBox.confirm('确定要删除这个会话吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteChatSession(sessionId)
    ElMessage.success('删除成功')
    await loadSessions()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

function handleClose() {
  visible.value = false
}

function formatDate(dateStr: string) {
  const date = new Date(dateStr)
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString()
}
</script>

<style scoped>
.session-list {
  max-height: 400px;
  overflow-y: auto;
}
.session-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
}
.session-item:hover {
  background: #f5f5f5;
}
.session-info {
  flex: 1;
}
.session-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}
.session-meta {
  font-size: 12px;
  color: #999;
}
.session-meta span {
  margin-right: 12px;
}
</style>