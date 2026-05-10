import request from '@/utils/system/request'

/** 登录api */
export function loginApi(data: object) {
  return request({
    url: '/user/login',
    method: 'post',
    baseURL: '/mock',
    data
  })
}

/** 获取用户信息Api */
export function getInfoApi(data: object) {
  return request({
    url: '/user/info',
    method: 'post',
    baseURL: '/mock',
    data
  })
}

/** 退出登录Api */
export function loginOutApi() {
  return request({
    url: '/user/out',
    method: 'post',
    baseURL: '/mock'
  })
}

/** 获取用户信息Api */
export function passwordChange(data: object) {
  return request({
    url: '/user/passwordChange',
    method: 'post',
    baseURL: '/mock',
    data
  })
}

/** 获取登录后需要展示的菜单 */
export function getMenuApi() {
  return request({
    url: '/menu/list',
    method: 'post',
    baseURL: '/mock'
  })
}

// ============ 用户注册审批相关API ============

/** 用户注册 */
export function registerApi(data: { username: string; password: string; confirmPassword: string }) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

/** 获取待审批列表 */
export function getPendingListApi(params: { pageNum: number; pageSize: number }) {
  return request({
    url: '/user/registration/pending',
    method: 'get',
    params
  })
}

/** 获取所有审批记录 */
export function getRegistrationListApi(params: { pageNum: number; pageSize: number }) {
  return request({
    url: '/user/registration/list',
    method: 'get',
    params
  })
}

/** 获取审批详情 */
export function getRegistrationDetailApi(id: string) {
  return request({
    url: `/user/registration/${id}`,
    method: 'get'
  })
}

/** 审批通过 */
export function approveRegistrationApi(id: string) {
  return request({
    url: `/user/registration/approve/${id}`,
    method: 'post'
  })
}

/** 审批拒绝 */
export function rejectRegistrationApi(data: { id: string; rejectReason: string; remark?: string }) {
  return request({
    url: '/user/registration/reject',
    method: 'post',
    data
  })
}

/** 获取用户列表 */
export function getUserListApi(params: { pageNum: number; pageSize: number }) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

/** 获取用户详情 */
export function getUserDetailApi(id: string) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}
