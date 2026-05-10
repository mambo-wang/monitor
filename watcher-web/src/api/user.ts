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

// ============ 用户注册审批相关接口 ============

/** 注册申请请求参数 */
export interface RegisterRequest {
  username: string;
  password: string;
  remark?: string;
}

/** 注册申请列表项 */
export interface RegisterListItemVO {
  id: string;
  username: string;
  status: 'pending' | 'approved' | 'rejected';
  submitTime: string;
  approver: string;
  approveTime: string;
  rejectReason: string;
  remark: string;
  rejectCount: number;
}

/** 注册申请详情 */
export interface RegisterDetailVO extends RegisterListItemVO {
  historyList: RegisterListItemVO[];
}

/** 提交注册申请 */
export const register = (data: RegisterRequest) => {
  return request.post("/user/register", null, { params: data });
};

/** 获取待审批注册申请列表 */
export const getPendingRegisterList = () => {
  return request.get<{ data: RegisterListItemVO[] }>("/user/register/pending");
};

/** 获取注册申请详情（含历史） */
export const getRegisterDetail = (id: string) => {
  return request.get<{ data: RegisterDetailVO }>(`/user/register/${id}`);
};

/** 同意注册申请 */
export const approveRegister = (id: string) => {
  return request.post(`/user/register/${id}/approve`);
};

/** 拒绝注册申请 */
export const rejectRegister = (data: { id: string; rejectReason?: string }) => {
  return request.post("/user/register/reject", data);
};

/** 获取用户列表 */
export const getUserList = () => {
  return request.get<{ data: string[] }>("/user/list");
};