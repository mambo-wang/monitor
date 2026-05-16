import axios, {AxiosError, AxiosRequestConfig, AxiosResponse, AxiosInstance} from 'axios'
import store from '@/store'
import {ElMessage} from 'element-plus'
//在 axios 请求拦截器里面
import qs from 'qs'

const baseURL: any = import.meta.env.VITE_BASE_URL
const service: AxiosInstance = axios.create({
    baseURL: baseURL,
})
service.interceptors.request.use(
    (config: AxiosRequestConfig) => {
        if (config.method === 'delete') {
            config.paramsSerializer = function (params) {
                return qs.stringify(params, {arrayFormat: 'repeat'})
            }
        }
        // JWT鉴权处理
        if (store.getters['user/token']) {
            config.headers['token'] = store.state.user.token
        }
        return config
    },
    (error: AxiosError) => {
        console.log(error) // for debug
        return Promise.reject(error)
    }
)

service.interceptors.response.use(
    (response: AxiosResponse) => {
        const res = response.data;
        // 支持两种响应格式: state=0 或 code=0
        if (res.state === 0 || res.code === 0) {
            return res
        } else {
            showError(res)
            return Promise.reject(res)
        }
    },
    (error: AxiosError) => {
        console.log(error) // for debug
        const badMessage: any = error.message || error
        const code = parseInt(badMessage.toString().replace('Request failed with status code ', ''))
        showError({code, message: badMessage})
        return Promise.reject(error)
    }
)

// 错误处理
function showError(error: any) {
    console.log(error)
    // token过期，清除本地数据，并跳转至登录页面
    if (error.code === 401 || error.code === 403) {
        // to re-login
        function setCookie(cname: string, cvalue: string, exdays: number) {
            var d = new Date();
            d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
            var expires = "expires=" + d.toUTCString();
            document.cookie = cname + "=" + cvalue + "; " + expires;
        }

        setCookie('AC_TOKEN', '', -1);
        window.localStorage.removeItem('tabs')
        window.localStorage.removeItem('vuex')
        window.sessionStorage.removeItem('vuex')
        window.location.reload()
    } else {
        ElMessage({
            message: error.msg || error.message || error.failureMessage || '服务异常',
            type: 'error',
            duration: 3 * 1000
        })
    }

}

export default service


