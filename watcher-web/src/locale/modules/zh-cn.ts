import zhLocale from 'element-plus/lib/locale/lang/zh-cn'
import system from './zh-cn/system'
import common from './zh-cn/common'
import menu from './zh-cn/menu'
import agent from './zh-cn/agent/index'
import tenant from "./zh-cn/tenant/index"
import initConfig from "./zh-cn/init-config/index"
import resource from "./zh-cn/resource/index"
import dashboard from "./zh-cn/dashboard"
import user from "./zh-cn/user/index"
import toolShare from "./zh-cn/toolShare"

const lang = {
    el: zhLocale.el, // element内部国际化
    message: {
        language: '中文',
        ...system,
        ...common,
        ...menu,
        ...agent,
        ...tenant,
        ...initConfig,
        ...resource,
        ...dashboard,
        ...user,
        ...toolShare
    }
}

export default lang
