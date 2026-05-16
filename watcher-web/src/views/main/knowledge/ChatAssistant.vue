<template>
  <div class="chat-assistant">
    <div class="header">
      <el-button @click="$emit('back')">
        <i class="el-icon-arrow-left"></i> 返回
      </el-button>
      <h2>{{ kb.name }} - RAG 问答</h2>
      <div class="header-actions">
        <el-button size="small" @click="handleNewSession">
          新建会话
        </el-button>
        <el-button size="small" @click="showHistoryDialog = true">
          历史会话
        </el-button>
      </div>
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
            <div v-if="msg.sources && showSources && msg.role === 'assistant'" class="message-sources">
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

    <HistorySessions
      v-model="showHistoryDialog"
      :kb-id="kb.id"
      @select-session="handleSelectHistorySession"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { chatWithHistory, chatWithKB, getSessionDetail, type KnowledgeBase, type ChatMessage } from '@/api/knowledge'
import HistorySessions from './HistorySessions.vue'

const props = defineProps<{
  kb: KnowledgeBase
  sessionId?: string
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
const currentSessionId = ref(props.sessionId || '')
const currentUserId = 'default_user' // TODO: 从用户系统获取
const showHistoryDialog = ref(false)

async function handleSend() {
  const text = inputText.value.trim()
  if (!text || loading.value) return
  if (props.kb.status !== 'ready') {
    ElMessage.warning('请先构建知识库')
    return
  }
  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  loading.value = true
  scrollToBottom()
  try {
    const response = await chatWithHistory({
      user_id: currentUserId,
      kb_id: props.kb.id,
      question: text,
      session_id: currentSessionId.value || undefined
    })
    currentSessionId.value = response.session_id
    messages.value.push({
      role: 'assistant',
      content: response.answer
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

function handleNewSession() {
  messages.value = []
  currentSessionId.value = ''
}

function handleSelectHistorySession(session: any) {
  currentSessionId.value = session.id
  // 加载历史消息
  loadHistoryMessages(session.id)
}

async function loadHistoryMessages(sessionId: string) {
  try {
    const currentUserId = 'default_user'
    const res = await getSessionDetail(sessionId, currentUserId)
    // 将历史消息转换为 Message 格式并展示
    messages.value = res.messages.map((msg: any) => ({
      role: msg.role as 'user' | 'assistant',
      content: msg.content
    }))
    scrollToBottom()
  } catch (e: any) {
    ElMessage.error('加载历史消息失败')
  }
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
