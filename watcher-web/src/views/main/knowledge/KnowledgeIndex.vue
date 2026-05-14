<template>
  <div class="knowledge-index">
    <KnowledgeLibrary
      v-if="currentView === 'list'"
      @enter="goToChat"
      @manage-docs="goToDocs"
    />
    <DocumentManage
      v-else-if="currentView === 'docs'"
      :kb="selectedKB!"
      @back="currentView = 'list'"
    />
    <ChatAssistant
      v-else-if="currentView === 'chat'"
      :kb="selectedKB!"
      @back="currentView = 'list'"
    />
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
