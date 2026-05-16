<template>
  <div class="knowledge-container">
    <div class="knowledge-header">
      <h2>{{ $t('message.knowledge.title') }}</h2>
    </div>
    <div class="knowledge-content">
      <div class="chat-area">
        <div class="chat-history" ref="chatHistoryRef">
          <div
            v-for="(item, index) in chatHistory"
            :key="index"
            class="chat-item"
            :class="{ 'user': item.role === 'user', 'assistant': item.role === 'assistant' }"
          >
            <div class="chat-avatar">
              <el-icon v-if="item.role === 'user'" class="el-icon-user"></el-icon>
              <el-icon v-else class="el-icon-service"></el-icon>
            </div>
            <div class="chat-message">
              <div class="message-content">{{ item.content }}</div>
              <div class="message-time">{{ item.time }}</div>
            </div>
          </div>
        </div>
        <div class="chat-input">
          <el-input
            v-model="question"
            type="textarea"
            :rows="3"
            :placeholder="$t('message.knowledge.placeholder')"
            @keyup.enter.ctrl="submitQuestion"
          />
          <el-button type="primary" :loading="loading" @click="submitQuestion">
            {{ $t('message.knowledge.submit') }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const question = ref('')
const loading = ref(false)
const chatHistoryRef = ref<HTMLElement>()

interface ChatItem {
  role: 'user' | 'assistant'
  content: string
  time: string
}

const chatHistory = reactive<ChatItem[]>([])

const getCurrentTime = () => {
  const now = new Date()
  return now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const submitQuestion = async () => {
  if (!question.value.trim()) {
    ElMessage.warning({
      message: '请输入问题',
      type: 'warning'
    })
    return
  }

  const userQuestion = question.value.trim()
  chatHistory.push({
    role: 'user',
    content: userQuestion,
    time: getCurrentTime()
  })

  question.value = ''
  loading.value = true

  setTimeout(() => {
    chatHistory.push({
      role: 'assistant',
      content: '知识库功能正在开发中，请稍后再试。',
      time: getCurrentTime()
    })
    loading.value = false
  }, 1000)
}
</script>

<style lang="scss" scoped>
.knowledge-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px;

  .knowledge-header {
    margin-bottom: 20px;

    h2 {
      font-size: 20px;
      font-weight: 600;
      color: #333;
    }
  }

  .knowledge-content {
    flex: 1;
    display: flex;
    flex-direction: column;

    .chat-area {
      flex: 1;
      display: flex;
      flex-direction: column;
      background: #fff;
      border-radius: 8px;
      padding: 20px;

      .chat-history {
        flex: 1;
        overflow-y: auto;
        margin-bottom: 20px;

        .chat-item {
          display: flex;
          margin-bottom: 20px;

          &.user {
            flex-direction: row-reverse;
          }

          .chat-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: #409eff;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 20px;
          }

          .chat-message {
            max-width: 70%;
            margin-left: 10px;

            .message-content {
              padding: 12px 16px;
              border-radius: 8px;
              background: #f0f2f5;
              color: #333;
              line-height: 1.6;
            }

            .message-time {
              font-size: 12px;
              color: #999;
              margin-top: 4px;
            }
          }

          &.user .chat-message {
            margin-left: 0;
            margin-right: 10px;

            .message-content {
              background: #409eff;
              color: #fff;
            }
          }
        }
      }

      .chat-input {
        display: flex;
        gap: 10px;
        align-items: flex-end;

        .el-textarea {
          flex: 1;
        }
      }
    }
  }
}
</style>