# Design

## File Structure

### 修改文件

| 文件路径 | 操作 | 说明 |
|---------|------|------|
| `watcher-web/src/views/system/login.vue` | 修改 | 登录页增加用户名输入框，移除 `username: "admin"` 默认值 |
| `watcher-web/src/layout/Menu/menu.ts` | 修改 | 删除 `/agent`、`/tenant`、`/net` 三个菜单块，增加 `/knowledge` 菜单 |
| `watcher-web/src/locale/modules/zh-cn/menu.ts` | 修改 | 删除 `agent`、`tenant`、`net` 的国际化配置，增加 `knowledge` |
| `watcher-web/src/locale/modules/en/menu.ts` | 修改 | 同上，英文翻译 |

### 新增文件

| 文件路径 | 说明 |
|---------|------|
| `watcher-web/src/views/main/knowledge/index.vue` | 知识库页面（问答界面） |

### 测试文件

| 文件路径 | 说明 |
|---------|------|
| `watcher-web/tests/unit/login.spec.ts` | 登录页用户名输入框单元测试 |
| `watcher-web/tests/unit/menu.spec.ts` | 菜单配置单元测试 |

---

## Test Strategy

### `watcher-web/tests/unit/login.spec.ts`
- **测试策略**: 单元测试（Vitest + @vue/test-utils）
- **覆盖点**:
  - 用户名输入框可见性
  - 用户名默认值清空
  - 用户名密码非空校验
  - 登录表单提交触发

### `watcher-web/tests/unit/menu.spec.ts`
- **测试策略**: 单元测试
- **覆盖点**:
  - 菜单列表不包含 `/agent`、`/tenant`、`/net` 路径
  - 菜单列表包含 `/knowledge` 路径
  - 知识库菜单 meta.title 为 `message.menu.knowledge.name`

### 测试运行命令
```bash
cd watcher-web && npm run test:unit
```

---

## 改动说明

### 1. 登录页 (`login.vue`)
- 在密码输入框上方增加用户名输入框，使用 `el-input` 组件
- `form.username` 初始值从 `"admin"` 改为 `""`
- 密码输入框的 `placeholder` 保持国际化翻译

### 2. 菜单配置 (`menu.ts`)
- 删除第 36-51 行（`/agent` 节点管理）
- 删除第 52-66 行（`/tenant` 租户信息认证）
- 删除第 82-112 行（`/net` 网络参数管理）
- 在 `resource` 菜单后增加知识库菜单：
  ```typescript
  {
    path: "/knowledge",
    redirect: "/knowledge/index",
    meta: { title: "message.menu.knowledge.name", icon: "el-icon-document" },
    hideMenu: false,
    children: [
      {
        path: "index",
        component: createNameComponent(() => import('@/views/main/knowledge/index.vue')),
        meta: { title: "message.menu.knowledge.name", icon: "el-icon-document", hideClose: true },
      },
    ],
  }
  ```

### 3. 知识库页面 (`/knowledge/index.vue`)
- 创建基础问答界面
- 包含输入框和历史记录列表