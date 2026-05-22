import request from '@/utils/system/request'

export const getFoldersApi = (parentId?: number) => {
  return request({ url: '/api/tool-share/folders', method: 'get', params: { parentId } })
}

export const createFolderApi = (data: { name: string; parentId?: number }) => {
  return request({ url: '/api/tool-share/folder', method: 'post', data })
}

export const getFilesApi = (folderId?: number) => {
  return request({ url: '/api/tool-share/files', method: 'get', params: { folderId } })
}

export const uploadFileApi = (formData: FormData) => {
  return request({ url: '/api/tool-share/upload', method: 'post', data: formData, headers: { 'Content-Type': 'multipart/form-data' } })
}

export const getDownloadUrl = (id: number) => {
  return `/api/tool-share/download/${id}`
}