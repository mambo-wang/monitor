import { describe, it, expect } from 'vitest'
import menuList from '@/layout/Menu/menu'

describe('菜单配置测试', () => {
  it('菜单列表应该包含工具分享菜单', () => {
    const toolShareMenu = menuList.find(item => item.path === '/tool-share')
    expect(toolShareMenu).toBeDefined()
  })

  it('工具分享菜单应该有正确的 meta 配置', () => {
    const toolShareMenu = menuList.find(item => item.path === '/tool-share')
    expect(toolShareMenu?.meta.title).toBe('message.menu.toolShare.name')
    expect(toolShareMenu?.meta.icon).toBe('el-icon-share')
  })

  it('工具分享菜单应该在知识库菜单之后', () => {
    const toolShareIndex = menuList.findIndex(item => item.path === '/tool-share')
    const knowledgeIndex = menuList.findIndex(item => item.path === '/knowledge')
    expect(toolShareIndex).toBeGreaterThan(knowledgeIndex)
  })

  it('工具分享菜单应该有子路由', () => {
    const toolShareMenu = menuList.find(item => item.path === '/tool-share')
    expect(toolShareMenu?.children).toBeDefined()
    expect(toolShareMenu?.children).toHaveLength(1)
    expect(toolShareMenu?.children?.[0].path).toBe('index')
  })

  it('工具分享子路由应该指向正确的组件', () => {
    const toolShareMenu = menuList.find(item => item.path === '/tool-share')
    const child = toolShareMenu?.children?.[0]
    expect(child?.component).toBeDefined()
    expect(child?.meta?.title).toBe('message.menu.toolShare.name')
  })
})

describe('国际化配置测试', () => {
  it('中文菜单应该包含 toolShare', async () => {
    const zhMenu = (await import('@/locale/modules/zh-cn/menu')).default
    expect(zhMenu.menu.toolShare).toBeDefined()
    expect(zhMenu.menu.toolShare.name).toBe('工具分享')
  })

  it('英文菜单应该包含 toolShare', async () => {
    const enMenu = (await import('@/locale/modules/en/menu')).default
    expect(enMenu.menu.toolShare).toBeDefined()
    expect(enMenu.menu.toolShare.name).toBe('Tool Share')
  })
})