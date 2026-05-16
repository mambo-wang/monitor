import request from '@/utils/system/request'

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

// 对话历史相关类型
export interface ChatSession {
  id: string
  user_id: string
  kb_id: string
  title: string
  message_count: number
  created_at: string
  updated_at: string
}

export interface ChatHistoryItem {
  id: number
  session_id: string
  role: string
  content: string
  sources: any
  created_at: string
}

export interface ChatHistoryListResponse {
  sessions: ChatSession[]
  total: number
  page: number
  page_size: number
}

export interface SessionDetailResponse {
  session: ChatSession
  messages: ChatHistoryItem[]
}

export interface SearchRequest {
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

export async function searchKB(kbId: string, query: string, top_k: number = 3): Promise<SearchResult> {
  const res = await request.post<SearchResult>(`/api/knowledge/kbs/${kbId}/search`, {
    query,
    top_k
  })
  return res.data
}

// 对话历史 API
export async function listChatHistory(userId: string, page: number = 1, pageSize: number = 20): Promise<ChatHistoryListResponse> {
  const res = await request.get<ChatHistoryListResponse>('/api/knowledge/chat/history', {
    params: { user_id: userId, page, page_size: pageSize }
  })
  return res.data
}

export async function getSessionDetail(sessionId: string, userId: string): Promise<SessionDetailResponse> {
  const res = await request.get<SessionDetailResponse>(`/api/knowledge/chat/sessions/${sessionId}`, {
    params: { user_id: userId }
  })
  return res.data
}

export async function deleteSession(sessionId: string, userId: string): Promise<void> {
  await request.delete(`/api/knowledge/chat/sessions/${sessionId}`, {
    params: { user_id: userId }
  })
}

export async function chatWithHistory(data: {
  user_id: string
  kb_id: string
  question: string
  session_id?: string
}): Promise<{ answer: string; session_id: string }> {
  const res = await request.post('/api/knowledge/chat/with-history', data)
  return res.data
}

// 按知识库查询会话列表（用于历史会话弹窗）
export async function getChatSessions(kbId: string): Promise<ChatHistoryListResponse> {
  const res = await request.get<ChatHistoryListResponse>('/api/knowledge/chat/history', {
    params: { kb_id: kbId, page: 1, page_size: 100 }
  })
  return res.data
}

// 删除会话
export async function deleteChatSession(sessionId: string): Promise<void> {
  await request.delete(`/api/knowledge/chat/sessions/${sessionId}`)
}
