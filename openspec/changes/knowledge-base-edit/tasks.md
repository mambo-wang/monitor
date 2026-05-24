# Tasks

## Atomic TDD Task List

### Feature: 知识库编辑功能

- [x] RED: 编写 KBRepository.update() 测试——测试更新 name、更新 description、更新两者、KB 不存在
- [x] GREEN: 实现 KBRepository.update() 方法——支持 name 和 description 可选更新
- [x] REFACTOR: 重构 KBRepository.update()——简化 SQL 构建逻辑

- [x] RED: 编写 KBService.update() 测试——测试调用 KBRepository.update
- [x] GREEN: 实现 KBService.update() 方法——封装 KBRepository.update

- [x] RED: 编写 PATCH /kbs/{kb_id} API 测试——测试成功更新、KB 不存在返回 404
- [x] GREEN: 实现 PATCH 端点——调用 KBService.update() 并返回更新后的 KB

---

### Feature: 文档列表持久化

- [x] RED: 编写 ChromaService.count_by_file() 测试——测试统计指定文件的 chunk 数
- [x] GREEN: 实现 ChromaService.count_by_file() 方法——根据 file_name 查询 metadata

- [x] RED: 编写 DocumentStore.list_by_kb() 测试——测试扫描目录返回文档列表、目录为空返回空列表
- [x] GREEN: 实现 DocumentStore.list_by_kb() 方法——扫描 uploads/{kb_id} 目录，从 ChromaDB 查询 chunk_count

- [x] RED: 编写文档列表 API 测试——测试 GET /kbs/{kb_id}/documents 返回正确的文档信息
- [x] GREEN: 修改 GET /documents 端点——使用新的 DocumentStore.list_by_kb()