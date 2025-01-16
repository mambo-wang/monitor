import enLocale from 'element-plus/lib/locale/lang/en'
import system from './en/system'
import common from './en/common'
import menu from './en/menu'
import agent from './en/agent/index'
import tenant from "./en/tenant/index"
import initConfig from "./en/init-config/index"

const lang = {
    el: enLocale.el, // element-plus i18 setting
    message: {
        language: 'English',
        ...system,
        ...common,
        ...menu,
        ...agent,
        ...tenant,
        ...initConfig
    }
}

export default lang