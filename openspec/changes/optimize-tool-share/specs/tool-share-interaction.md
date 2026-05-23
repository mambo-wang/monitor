# Spec - Tool Share Page Interaction

## Scenarios

### Scenario 1: 显示顶级文件夹
- GIVEN: 用户在工具分享页面
- WHEN: 页面加载
- THEN: 左侧文件夹列表只显示顶级文件夹（parentId 为 null）

### Scenario 2: 点击文件夹显示文件列表
- GIVEN: 用户在工具分享页面
- WHEN: 用户点击左侧某个文件夹
- THEN: 右侧文件列表显示该文件夹内的所有文件
- AND: 文件按下载量降序排列

### Scenario 3: 返回上级
- GIVEN: 用户已进入某个子文件夹
- WHEN: 用户点击"返回上级"按钮
- THEN: 显示根目录的文件列表（parentId 为 null 的文件）

### Scenario 4: 空文件夹提示
- GIVEN: 用户点击的文件夹内没有文件
- WHEN: 文件夹加载完成
- THEN: 显示"暂无文件"提示

### Scenario 5: 文件按下载量排序
- GIVEN: 文件夹内有多个文件
- WHEN: 文件列表加载
- THEN: 文件按 downloadCount 降序排列