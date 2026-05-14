# Tasks

## Atomic TDD Task List

### Feature: 知识库管理 API

- [x] RED: 编写知识库 CRUD API 测试——测试创建、获取列表、获取详情、删除
- [x] GREEN: 实现知识库 CRUD API——在 knowledge.py 中实现基础路由和内存存储

### Feature: 文档上传与管理

- [x] RED: 编写文档上传 API 测试——测试上传、列表、删除功能
- [x] GREEN: 实现文档上传 API——支持 PDF/Markdown/TXT，保存到 uploads 目录

### Feature: ChromaDB 向量化

- [x] RED: 编写 ChromaDB 服务测试——测试创建 collection、添加向量、查询向量
- [x] GREEN: 实现 ChromaDB 服务——封装 ChromaDB 客户端，提供 CRUD 操作

### Feature: 文档处理与文本分割

- [x] RED: 编写文档处理服务测试——测试加载文档、分割文本
- [x] GREEN: 实现文档处理服务——使用 LangChain 加载和分割文档

### Feature: 知识库构建

- [x] RED: 编写构建 API 测试——测试构建流程、状态更新
- [x] GREEN: 实现构建 API——将文档向量化存入 ChromaDB

### Feature: RAG 问答

- [x] RED: 编写 RAG 问答 API 测试——测试问答、检索功能
- [x] GREEN: 实现 RAG 问答——检索相关片段，调用 LLM 生成回答

### Feature: 前端知识库列表页

- [x] RED: 编写知识库列表页测试——测试创建、删除、构建按钮
- [x] GREEN: 实现知识库列表页——使用 Element Plus 表格展示和操作

### Feature: 前端文档管理页

- [x] RED: 编写文档管理页测试——测试上传、列表展示
- [x] GREEN: 实现文档管理页——支持拖拽上传和文件列表展示

### Feature: 前端 RAG 问答页

- [x] RED: 编写问答页测试——测试发送消息、显示回答
- [x] GREEN: 实现 RAG 问答页——对话式界面，支持流式输出
