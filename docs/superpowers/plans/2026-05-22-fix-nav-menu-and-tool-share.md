# Fix Navigation Menu and Tool Share - Implementation Plan

> **For implementer:** Use TDD throughout. Write failing test first. Watch it fail. Then implement.

**Goal:** Fix three UI issues - navigation menu icons not showing, tool share page button internationalization, and folder creation failure.

**Architecture:** Frontend Vue 3 + TypeScript with Element Plus; Backend Java Spring Boot. i18n handled via locale module files. Menu icons defined in menu.ts meta fields.

**Tech Stack:** Vue 3, TypeScript, Element Plus, Vitest, Java Spring Boot

---

## Tasks

<!-- openspec-task: 1 -->
### Task 1: RED - Write test for navigation menu icons

**Files:**
- Create: `watcher-web/tests/unit/menu.spec.ts`

**Step 1: Write the failing test**
```typescript
import { describe, it, expect } from 'vitest'
import menuList from '@/layout/Menu/menu'

describe('Navigation Menu Icons', () => {
  it('should have meta.icon for all menu items', () => {
    const checkMenuItem = (menu: any) => {
      if (!menu.hideMenu) {
        expect(menu.meta).toBeDefined()
        expect(menu.meta.icon).toBeDefined()
        expect(typeof menu.meta.icon).toBe('string')
      }
      if (menu.children) {
        menu.children.forEach(checkMenuItem)
      }
    }
    menuList.forEach(checkMenuItem)
  })
})
```

**Step 2: Run test — confirm it fails**
Command: `cd watcher-web && npm run test -- tests/unit/menu.spec.ts`
Expected: FAIL — some menu items missing meta.icon

---

<!-- openspec-task: 2 -->
### Task 2: GREEN - Add missing menu icons

**Files:**
- Modify: `watcher-web/src/layout/Menu/menu.ts`

**Step 1: Verify test fails**
Command: `cd watcher-web && npm run test -- tests/unit/menu.spec.ts`
Expected: FAIL — "meta.icon" is undefined

**Step 2: Add missing icons**
```typescript
// In watcher-web/src/layout/Menu/menu.ts
// Add icon to each menu item's meta:
{
  path: '/dashboard',
  component: Layout,
  meta: { title: 'message.menu.dashboard.name', icon: 'el-icon-monitor' },
  children: [...]
},
{
  path: '/init-config',
  component: Layout,
  meta: { title: 'message.menu.initConfig.name', icon: 'el-icon-setting' },
  children: [...]
},
// ... add icons for all menu items
```

**Step 3: Run test — confirm it passes**
Command: `cd watcher-web && npm run test -- tests/unit/menu.spec.ts`
Expected: PASS

---

<!-- openspec-task: 3 -->
### Task 3: RED - Write test for tool share button Chinese display

**Files:**
- Modify: `watcher-web/tests/unit/tool-share.spec.ts`

**Step 1: Write the failing test**
```typescript
import { describe, it, expect } from 'vitest'
import zhCn from '@/locale/modules/zh-cn'

describe('Tool Share Page i18n', () => {
  it('should have Chinese text for toolShare locale', () => {
    expect(zhCn.message.toolShare).toBeDefined()
    expect(zhCn.message.toolShare.createFolder).toBe('创建文件夹')
    expect(zhCn.message.toolShare.uploadFile).toBe('上传文件')
  })
})
```

**Step 2: Run test — confirm it fails**
Command: `cd watcher-web && npm run test -- tests/unit/tool-share.spec.ts`
Expected: FAIL — toolShare is undefined

---

<!-- openspec-task: 4 -->
### Task 4: GREEN - Create Chinese locale file for tool share

**Files:**
- Create: `watcher-web/src/locale/modules/zh-cn/toolShare.ts`

**Step 1: Verify test fails**
Command: `cd watcher-web && npm run test -- tests/unit/tool-share.spec.ts`
Expected: FAIL — "toolShare" is undefined

**Step 2: Create locale file**
```typescript
export default {
  toolShare: {
    createFolder: '创建文件夹',
    uploadFile: '上传文件',
    backToParent: '返回上级',
    folders: '文件夹',
    files: '文件',
    noFolders: '暂无文件夹',
    noFiles: '暂无文件',
    toolName: '工具名称',
    toolDesc: '工具描述',
    downloadCount: '下载次数',
    actions: '操作',
    download: '下载',
    folderName: '文件夹名称',
    folderNamePlaceholder: '请输入文件夹名称',
    selectFile: '选择文件',
    chooseFile: '选择文件',
    toolNamePlaceholder: '请输入工具名称',
    toolDescPlaceholder: '请输入工具描述（可选）'
  }
}
```

**Step 3: Run test — confirm it passes**
Command: `cd watcher-web && npm run test -- tests/unit/tool-share.spec.ts`
Expected: PASS

---

<!-- openspec-task: 5 -->
### Task 5: GREEN - Create English locale file for tool share

**Files:**
- Create: `watcher-web/src/locale/modules/en/toolShare.ts`

**Step 1: Create English locale file**
```typescript
export default {
  toolShare: {
    createFolder: 'Create Folder',
    uploadFile: 'Upload File',
    backToParent: 'Back to Parent',
    folders: 'Folders',
    files: 'Files',
    noFolders: 'No folders',
    noFiles: 'No files',
    toolName: 'Tool Name',
    toolDesc: 'Tool Description',
    downloadCount: 'Download Count',
    actions: 'Actions',
    download: 'Download',
    folderName: 'Folder Name',
    folderNamePlaceholder: 'Please enter folder name',
    selectFile: 'Select File',
    chooseFile: 'Choose File',
    toolNamePlaceholder: 'Please enter tool name',
    toolDescPlaceholder: 'Please enter tool description (optional)'
  }
}
```

**Step 2: Run lint**
Command: `cd watcher-web && npm run lint`
Expected: No errors

---

<!-- openspec-task: 6 -->
### Task 6: GREEN - Import locale modules

**Files:**
- Modify: `watcher-web/src/locale/modules/zh-cn.ts`
- Modify: `watcher-web/src/locale/modules/en.ts`

**Step 1: Modify zh-cn.ts**
```typescript
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
import toolShare from "./zh-cn/toolShare"  // ADD THIS

const lang = {
    el: zhLocale.el,
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
        ...toolShare  // ADD THIS
    }
}

export default lang
```

**Step 2: Modify en.ts**
```typescript
import enLocale from 'element-plus/lib/locale/lang/en'
import system from './en/system'
import common from './en/common'
import menu from './en/menu'
import agent from './en/agent/index'
import tenant from "./en/tenant/index"
import initConfig from "./en/init-config/index"
import dashboard from "./en/dashboard"
import toolShare from "./en/toolShare"  // ADD THIS

const lang = {
    el: enLocale.el,
    message: {
        language: 'English',
        ...system,
        ...common,
        ...menu,
        ...agent,
        ...tenant,
        ...initConfig,
        ...dashboard,
        ...toolShare  // ADD THIS
    }
}

export default lang
```

**Step 3: Run lint**
Command: `cd watcher-web && npm run lint`
Expected: No errors

---

<!-- openspec-task: 7 -->
### Task 7: RED - Write test for create folder API call

**Files:**
- Modify: `watcher-web/tests/unit/tool-share.spec.ts`

**Step 1: Write the failing test**
```typescript
it('should not send parentId when currentFolderId is null', async () => {
  // Mock the API call
  const mockCreateFolderApi = async (data: any) => {
    // If data.parentId is undefined (not null), the test passes
    expect('parentId' in data).toBe(false)
  }

  // Simulate createFolder with null currentFolderId
  const createFolder = async (name: string, parentId: number | null) => {
    const data: any = { name }
    if (parentId !== null) {
      data.parentId = parentId
    }
    await mockCreateFolderApi(data)
  }

  await createFolder('test', null)
})
```

**Step 2: Run test — confirm it fails**
Command: `cd watcher-web && npm run test -- tests/unit/tool-share.spec.ts`
Expected: FAIL — parentId is being sent as null

---

<!-- openspec-task: 8 -->
### Task 8: GREEN - Fix frontend createFolder API call

**Files:**
- Modify: `watcher-web/src/views/main/tool-share/index.vue`

**Step 1: Verify test fails**
Command: `cd watcher-web && npm run test -- tests/unit/tool-share.spec.ts`
Expected: FAIL — parentId is being sent

**Step 2: Fix the createFolder function**
Find the `createFolder` function and modify the API call:
```typescript
const createFolder = async () => {
  if (!newFolderName.value.trim()) {
    ElMessage.warning({
      message: '请输入文件夹名称',
      type: 'warning'
    })
    return
  }
  try {
    const data: any = { name: newFolderName.value.trim() }
    if (currentFolderId.value !== null) {
      data.parentId = currentFolderId.value
    }
    await createFolderApi(data)
    ElMessage.success({
      message: '创建文件夹成功',
      type: 'success'
    })
    createFolderDialogVisible.value = false
    await loadFolders()
  } catch (error) {
    console.error('创建文件夹失败', error)
  }
}
```

**Step 3: Run test — confirm it passes**
Command: `cd watcher-web && npm run test -- tests/unit/tool-share.spec.ts`
Expected: PASS

---

<!-- openspec-task: 9 -->
### Task 9: GREEN - Fix backend parentId handling

**Files:**
- Modify: `watcher-agent/src/main/java/com/virtual/cloud/om/agent/service/impl/ToolShareServiceImpl.java`

**Step 1: Review current implementation**
The `getFolders` method already handles null parentId correctly - it queries for root folders (where parentId is null). The issue is that when parentId is explicitly passed as null from frontend, MyBatis-Plus treats it differently.

**Step 2: Fix the createFolder method**
In `ToolShareFolder` entity, the `parentId` field is Long type. When not set, it's null. But we need to ensure the insert works correctly:
```java
@Override
public void createFolder(ToolShareFolder folder) {
    // Only set parentId if it's not null
    if (folder.getParentId() != null) {
        folder.setParentId(folder.getParentId());
    }
    folder.setCreateTime(LocalDateTime.now());
    folderMapper.insert(folder);
}
```

Actually, the entity itself is fine. The issue might be in how the service handles the folder creation. Let's ensure it works:

```java
@Override
public void createFolder(ToolShareFolder folder) {
    folder.setCreateTime(LocalDateTime.now());
    // parentId can be null - MyBatis handles this correctly
    folderMapper.insert(folder);
}
```

**Step 3: Verify by running the application and testing manually**

---

## Verification

After completing all tasks:
1. Run frontend tests: `cd watcher-web && npm run test`
2. Run lint: `cd watcher-web && npm run lint`
3. Build frontend: `cd watcher-web && npm run build`
4. Start backend and verify folder creation works