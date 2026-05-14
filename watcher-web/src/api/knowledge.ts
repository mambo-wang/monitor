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
