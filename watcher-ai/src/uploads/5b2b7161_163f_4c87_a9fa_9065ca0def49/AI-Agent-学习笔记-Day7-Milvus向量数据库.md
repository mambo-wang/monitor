# 📚 Milvus 向量数据库详解

> 本文档涵盖 Milvus 的核心概念、工作原理、Python 使用方法，以及与项目的 Day 7 练习代码的结合实践。

---

## 一、Milvus 是什么？

**Milvus** 是一个开源的**向量数据库**（Vector Database），专门用于存储、检索和分析**高维向量 Embedding**。

### 1.1 核心能力

| 能力 | 说明 |
|------|------|
| **海量向量存储** | 支持数十亿级向量存储与检索 |
| **相似性搜索** | 找到与给定向量最相似的 Top-K 结果 |
| **高性能** | 通过索引技术实现毫秒级查询 |
| **多模态支持** | 支持图片、文本、音频、视频等多种数据的向量表示 |
| **分布式架构** | 支持水平扩展，适应大规模生产环境 |

### 1.2 解决什么问题？

传统数据库按**精确匹配**查询（WHERE id = 1），但 AI 场景需要**模糊搜索**：

```
用户输入："苹果是一种水果"
传统DB：无法找到"我爱吃苹果"（因为不是精确匹配）
向量DB：能找到语义相近的句子（余弦相似度 > 0.8）
```

**典型应用场景：**
- **RAG（检索增强生成）** — 私域知识库问答
- **以图搜图** — 电商商品推荐
- **语义搜索** — 搜索引擎优化
- **推荐系统** — 用户兴趣匹配
- **异常检测** — 金融风控

---

## 二、核心概念

### 2.1 关键术语

| 术语 | 解释 |
|------|------|
| **Collection（集合）** | 相当于数据库中的"表"，存储一组向量及相关元数据 |
| **Partition（分区）** | 集合的逻辑分区，查询时可指定分区加速 |
| **Vector（向量）** | 高维浮点数数组，由 Embedding 模型生成 |
| **Embedding（向量表示）** | 将文本/图片等非结构化数据转换为固定维度的向量 |
| **Index（索引）** | 加速向量搜索的数据结构（类似数据库索引） |
| **Metric Type（度量方式）** | 计算向量相似度的方法：IP（内积）、COSINE（余弦） |

### 2.2 Milvus Lite vs Milvus（生产级）

| 特性 | Milvus Lite | Milvus（生产） |
|------|-------------|----------------|
| 部署方式 | Python 包，本地文件 | Docker / K8s 集群 |
| 规模 | 百万级向量 | 十亿级向量 |
| 适用场景 | 学习 / 原型验证 | 生产环境 |
| 索引类型 | 基础索引 | 多种高级索引 |

> 💡 **项目使用场景**：Day 7 练习使用 `milvus-lite`，数据存储在本地 `.db` 文件中，学习和快速验证完全够用。

---

## 三、工作原理

### 3.1 整体流程

```
1. 原始数据（文本/图片）
        ↓ [Embedding 模型]
2. 高维向量（e.g. 384维、768维）
        ↓ [Milvus 存储]
3. 向量数据库（创建 Collection）
        ↓ [构建索引]
4. 索引结构（ANNS 算法）
        ↓ [相似性搜索]
5. Top-K 结果（按相似度排序）
```

### 3.2 索引算法（ANNS）

Approximate Nearest Neighbor Search（近似最近邻搜索）：

| 索引类型 | 原理 | 适用场景 |
|----------|------|----------|
| **FLAT** | 暴力搜索，100% 准确 | 小数据集 |
| **IVF_FLAT** | 聚类后扫描，减少扫描量 | 中等规模 |
| **HNSW** | 图索引，导航小世界 | 高性能需求 |
| **GPU_IVF** | GPU 加速的 IVF | 超大规模 |

> Milvus Lite 默认使用 **FLAT** 索引，适合学习场景。

### 3.3 相似度度量

```python
# 余弦相似度（Cosine Similarity）
# 范围 [-1, 1]，越接近 1 越相似
cosine_sim = dot(a, b) / (||a|| * ||b||)

# 内积（Inner Product / IP）
# 向量方向一致时值越大
ip = sum(a[i] * b[i] for i in range(len(a)))
```

---

## 四、Python 使用详解

### 4.1 安装依赖

```bash
pip install pymilvus milvus-lite sentence-transformers torch
```

> 如果连不上 HuggingFace，执行：`export HF_ENDPOINT=https://hf-mirror.com`

### 4.2 完整使用流程

```python
# ========================
# Step 1: 准备向量（Embedding）
# ========================
from sentence_transformers import SentenceTransformer

model = SentenceTransformer("all-MiniLM-L6-v2")  # 输出 384 维向量

docs = [
    "我喜欢吃苹果",
    "今天天气很好",
    "苹果是一种水果",
]
vectors = model.encode(docs).tolist()  # numpy → list

# ========================
# Step 2: 连接 Milvus
# ========================
from pymilvus import MilvusClient

# Milvus Lite：本地文件存储（不存在会自动创建）
client = MilvusClient(uri="./milvus_demo.db")

# ========================
# Step 3: 创建 Collection（表）
# ========================
client.create_collection(
    collection_name="my_docs",   # 集合名
    dimension=384,               # 向量维度（必须与模型输出一致）
    overwrite=True,              # 如果已存在则覆盖
)

# ========================
# Step 4: 插入数据
# ========================
data = [
    {"id": 0, "text": docs[0], "vector": vectors[0]},
    {"id": 1, "text": docs[1], "vector": vectors[1]},
    {"id": 2, "text": docs[2], "vector": vectors[2]},
]
client.insert(collection_name="my_docs", data=data)

# ========================
# Step 5: 搜索相似向量
# ========================
query = "水果"
query_vector = model.encode([query]).tolist()[0]

results = client.search(
    collection_name="my_docs",
    data=[query_vector],
    limit=3,                  # 返回 Top-3
    output_fields=["text"],   # 返回哪些字段
)

# ========================
# Step 6: 查看结果
# ========================
for result in results[0]:
    print(f"文本: {result['entity']['text']}")
    print(f"相似度距离: {result['distance']:.4f}")
    print("---")
```

**输出示例：**
```
文本: 苹果是一种水果
相似度距离: 0.5234
---
文本: 我喜欢吃苹果
相似度距离: 0.6123
---
文本: 今天天气很好
相似度距离: 1.2341
---
```

> 注意：`distance` 越小表示越相似（FLAT 索引下与余弦距离相关）。

---

## 五、进阶用法

### 5.1 元数据过滤

Milvus 支持在向量搜索时**结合标量字段过滤**，缩小搜索范围：

```python
# 插入带元数据的数据
data = [
    {"id": 0, "text": "苹果是一种水果", "category": "food", "vector": vectors[0]},
    {"id": 1, "text": "今天天气很好", "category": "weather", "vector": vectors[1]},
    {"id": 2, "text": "机器学习很有趣", "category": "tech", "vector": vectors[2]},
]
client.insert(collection_name="my_docs", data=data)

# 搜索时只查 category == 'food' 的文档
results = client.search(
    collection_name="my_docs",
    data=[query_vector],
    limit=3,
    filter="category == 'food'",  # 元数据过滤
    output_fields=["text", "category"],
)
```

### 5.2 使用 Milvus 官方 Embedding 函数

`pymilvus[model]` 内置了 Embedding 功能，无需额外安装 sentence-transformers：

```python
from pymilvus import MilvusClient
from pymilvus import model

# 使用 Milvus 内置 Embedding 模型
embedding_fn = model.DefaultEmbeddingFunction()
print("向量维度:", embedding_fn.dim)  # 768

docs = ["AI started in 1956", "Alan Turing founded AI"]
vectors = embedding_fn.encode_documents(docs)

query_vectors = embedding_fn.encode_queries(["Who started AI?"])
```

### 5.3 删除与更新

```python
# 删除单条记录
client.delete(
    collection_name="my_docs",
    pks=[1],  # 要删除的 ID 列表
)

# 删除整个 Collection
client.drop_collection(collection_name="my_docs")

# 查看 Collection 详情
info = client.get_collection_stats("my_docs")
print(info)
```

---

## 六、项目练习代码对照

Day 7 的练习代码已完整覆盖上述流程：

```
python-study/day7_embedding_milvus/
├── day7_vector_db.py        # 主练习：文本向量搜索（使用 sentence-transformers）
└── day7_milvus_demo.py       # 补充练习：Milvus 内置 Embedding + 元数据过滤
```

### day7_vector_db.py 核心逻辑

```python
# 1. 加载模型（all-MiniLM-L6-v2，384维）
model = SentenceTransformer("all-MiniLM-L6-v2")

# 2. 连接本地 Milvus 数据库
client = MilvusClient(uri="./milvus_demo.db")

# 3. 创建集合（384维向量）
client.create_collection("my_docs", dimension=384, overwrite=True)

# 4. 批量插入文档向量
client.insert("my_docs", data=[...])

# 5. 查询相似文档
results = client.search("my_docs", data=[query_vector], limit=3, output_fields=["text"])
```

### day7_milvus_demo.py 补充知识点

```python
# 1. 使用 Milvus 内置 Embedding（768维）
embedding_fn = model.DefaultEmbeddingFunction()

# 2. 元数据过滤搜索
client.search(collection_name="demo_collection",
              data=query_vectors,
              filter="subject == 'biology'",  # 只查 biology 分类
              limit=2)
```

---

## 七、常见问题

### Q1: `milvus_lite` 和 `pymilvus` 有什么区别？

| 包名 | 角色 |
|------|------|
| `pymilvus` | Python SDK，客户端库，所有版本都需要 |
| `milvus-lite` | 轻量级嵌入式服务器，Milvus Lite 专用 |

安装命令：`pip install pymilvus milvus-lite`

### Q2: 向量维度必须精确匹配吗？

**必须！** 模型输出的向量维度（e.g. 384）必须在创建 Collection 时指定，**不匹配会报错**。

常见模型维度：
- `all-MiniLM-L6-v2` → 384 维
- `all-mpnet-base-v2` → 768 维
- `BAAI/bge-large-zh-v1.5` → 1024 维

### Q3: Milvus Lite 能存多少数据？

Milvus Lite 使用本地 SQLite 存储，理论上**百万级向量**没问题，但性能会下降。

生产环境建议用完整 Milvus（Docker 部署），支持**十亿级向量**。

### Q4: 搜索结果的距离（distance）怎么理解？

取决于度量类型：
- **IP（内积）**：越大越相似
- **余弦相似度**：通常 Milvus 内部会做归一化，越小越相似

在本项目的 FLAT 索引下，`distance` 值越小表示越相似。

---

## 八、延伸学习路线

```
Day 7: Milvus 基础 ✓
  ↓
下一步: RAG 实战
  - LangChain / LlamaIndex 集成向量数据库
  - 构建本地知识库问答系统
  ↓
更深入: 向量数据库选型
  - Milvus vs Pinecone vs Weaviate vs Qdrant
  - 各种索引算法原理（HNSW、IVF、PQ）
```

---

## 九、参考资料

- [Milvus 官方文档](https://milvus.io/docs/zh/overview.md)
- [Pymilvus SDK 文档](https://milvus.io/docs/zh/sdk-and-tools/overview.md)
- [Day 7 练习代码](../python-study/day7_embedding_milvus/day7_vector_db.py)

---

*本文档对应 Day 7 学习内容*
