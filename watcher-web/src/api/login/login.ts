import request from '@/utils/system/request'

/** 登录api */
export function loginApi(data: object) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

/** 获取用户信息Api */
export function getInfoApi(data: object) {
  return request({
    url: '/user/info',
    method: 'post',
    data
  })
}

/** 退出登录Api */
export function loginOutApi() {
  return request({
    url: '/user/logout',
    method: 'post',
  })
}

/** 获取用户信息Api */
export function modifyPassword(data: object) {
  return request({
    url: '/user/modifyUser',
    method: 'put',
    data
  })
}

/** 获取密码策略 */
export function getPasswordComplexity() {
  return request({
    url: '/user/search/complexity',
    method: 'get'
  })
}

/** 获取登录后需要展示的菜单 */
export function getMenuApi() {
  return request({
    url: '/menu/list',
    method: 'post',
  })
}