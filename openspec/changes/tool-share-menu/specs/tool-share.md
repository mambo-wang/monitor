# Spec: Tool Share

## Scenarios

### Scenario 1: 左导航显示工具分享菜单
- GIVEN: 用户已登录系统
- WHEN: 左侧导航菜单渲染
- THEN: 显示"工具分享"菜单项
- THEN: 菜单图标为 `el-icon-share`

### Scenario 2: 工具分享页面显示文件夹和文件列表
- GIVEN: 用户点击"工具分享"菜单
- WHEN: 页面加载完成
- THEN: 显示文件夹列表区域
- THEN: 显示文件列表区域
- THEN: 文件列表包含文件名、工具名称、工具作用、下载量

### Scenario 3: 新建文件夹
- GIVEN: 用户在工具分享页面
- WHEN: 点击"新建文件夹"按钮
- THEN: 弹出对话框
- THEN: 用户输入文件夹名称后点击确认
- THEN: 文件夹创建成功并显示在列表中

### Scenario 4: 上传工具文件
- GIVEN: 用户在工具分享页面
- WHEN: 点击"上传文件"按钮
- THEN: 弹出上传对话框
- THEN: 显示文件选择框、工具名称输入框、工具作用输入框
- THEN: 用户选择文件、填写信息后点击上传
- THEN: 上传完成后文件出现在文件列表中

### Scenario 5: 上传文件支持任意格式
- GIVEN: 用户在上传对话框
- WHEN: 选择文件
- THEN: 支持任意格式的文件（exe、zip、pdf、doc 等）
- THEN: 文件大小限制为 100MB

### Scenario 6: 下载工具文件并统计下载量
- GIVEN: 用户在文件列表中
- WHEN: 点击文件旁边的"下载"按钮
- THEN: 浏览器开始下载文件
- THEN: 后台该文件的下载量加 1

### Scenario 7: 进入文件夹查看内容
- GIVEN: 用户在工具分享页面根目录
- WHEN: 点击某个文件夹
- THEN: 页面显示该文件夹内的文件列表
- THEN: 显示"返回上级目录"按钮