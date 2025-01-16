// import { loginApi, getInfoApi, loginOutApi } from '@/api/user'
import {loginApi, loginOutApi} from "@/api/login/login"
import {ActionContext} from 'vuex'

export interface userState {
    token: string,
    info: object
}

const state = (): userState => ({
    token: '', // 登录token
    info: {},  // 用户信息
})

// getters
const getters = {
    token(state: userState) {
        return state.token
    }
}

// mutations
const mutations = {
    tokenChange(state: userState, token: string) {
        state.token = token
    },
    infoChange(state: userState, info: object) {
        state.info = info
    }
}

// actions
const actions = {
    // login by login.vue
    login({commit, dispatch}: ActionContext<userState, userState>, params: any) {
        return new Promise((resolve, reject) => {
            loginApi(params).then(res => {
                commit('tokenChange', res.data);
                dispatch('getInfo', {token: res.data, ...params}).then(infoRes => {
                    resolve(res.data.token)
                })
            }).catch(err => {
                console.log(err)
                reject(err)
            })
        })
    },
    // get user info after user logined
    getInfo({commit}: ActionContext<userState, userState>, params: any) {
        return new Promise((resolve, reject) => {
            commit('infoChange', params)
            resolve(params)
        })
    },

    // login out the system after user click the loginOut button
    loginOut({commit}: ActionContext<userState, userState>) {
        loginOutApi().then(res => {
            window.localStorage.removeItem('tabs')
            window.localStorage.removeItem('vuex')
            window.sessionStorage.removeItem('vuex')
            window.location.reload()
        }).catch(error => {

        })
    }
}

export default {
    namespaced: true,
    state,
    actions,
    getters,
    mutations
}
