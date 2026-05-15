# Day 8 学习笔记：Self-Attention 自注意力机制

## 📚 今日概述

| 项目 | 内容 |
|------|------|
| **主题** | Self-Attention 机制 |
| **核心公式** | Attention(Q,K,V) = Softmax(Q·K^T / √d_k) · V |
| **代码** | day08_self_attention.py, day08_attention_v2.py |

---

## 🎯 学习目标

1. ✅ 理解 Self-Attention 的核心思想：每个词看其他词的相关性
2. ✅ 掌握 Q, K, V 三个向量的含义和作用
3. ✅ 理解 Attention Score 的计算方法
4. ✅ 学会可视化注意力权重

---

## 🧠 核心概念

### 1. 为什么需要 Attention？

```
传统RNN的问题：
─────────────────
"狗咬人" vs "人咬狗" → RNN难以捕捉语序
  ↓
Self-Attention 的解决方案：
  让每个词关注所有其他词，自动学习语义关系
  ↓
"它" → 自动关注 "狗"（不是"人"）
```

### 2. Q/K/V 三角关系

```
         我  爱  AI
         │  │  │
         ▼  ▼  ▼
    ┌────────────────┐
    │   Linear       │  W_Q, W_K, W_V (可学习权重矩阵)
    └────────────────┘
         │  │  │
         ▼  ▼  ▼
      ┌──────────┐
      │    Q     │  Query: "我在找什么？"
      │    K     │  Key:   "我包含什么信息？"
      │    V     │  Value: "信息的实际内容"
      └──────────┘

【通俗理解】
想象你在一个图书馆：
- Query = 你的搜索问题
- Key = 每本书的目录/简介
- Value = 书的实际内容

Attention 就是：根据你的问题，在所有书中找到最相关的几页
```

---

## 📊 Attention 计算流程

### 流程图

```
Step 1: Q, K, V 计算
─────────────────────
输入: X (词嵌入) [seq_len, d_model]

Q = X · W_Q  → [seq_len, d_model]
K = X · W_K  → [seq_len, d_model]
V = X · W_V  → [seq_len, d_model]


Step 2: 计算注意力分数
─────────────────────
       Q [seq, d]
       │
       ▼
    ┌─────┐
    │ Q·K^T │  → [seq, seq] 每个Query对所有Key的相关性
    └─────┘
       │
       ▼
    ÷ √d_k  (缩放，防止梯度消失)
       │
       ▼
    Softmax  → 归一化为概率分布 [0,1]，每行和=1
       │
       ▼
    Attention Weights (注意力权重矩阵)
       │
       ▼
Step 3: 加权求和
─────────────────────
Output = Attention_Weights · V
       = Σ(权重_i × V_i)
       → [seq_len, d_model]
```

### 数学公式

```
Attention(Q, K, V) = Softmax( Q·K^T / √d_k ) · V

参数说明：
- Q: Query向量 [seq_len, d_model]
- K: Key向量   [seq_len, d_model]
- V: Value向量 [seq_len, d_model]
- √d_k: 缩放因子，d_k = d_model / num_heads

为什么除以 √d_k？
→ 防止 d_k 过大时，Q·K^T 的值过大，导致 Softmax 梯度接近 0
```

---

## 🔢 代码核心实现

```python
class SelfAttention:
    def __init__(self, d_model: int, num_heads: int = 1):
        self.d_model = d_model
        self.num_heads = num_heads
        self.d_k = d_model // num_heads
        
        torch.manual_seed(42)
        # 初始化 Q, K, V 的投影矩阵
        self.W_Q = torch.randn(d_model, d_model) * 0.1
        self.W_K = torch.randn(d_model, d_model) * 0.1
        self.W_V = torch.randn(d_model, d_model) * 0.1
    
    def forward(self, x: torch.Tensor, mask: torch.Tensor = None):
        seq_len = x.shape[0]
        
        # 1. 计算 Q, K, V
        Q = torch.matmul(x, self.W_Q)
        K = torch.matmul(x, self.W_K)
        V = torch.matmul(x, self.W_V)
        
        # 2. 计算 Attention Score
        scores = torch.matmul(Q, K.transpose(-2, -1))  # Q·K^T
        scores = scores / math.sqrt(self.d_k)          # 缩放
        
        # 3. 应用 Mask (可选)
        if mask is not None:
            scores = scores.masked_fill(mask == 0, float('-inf'))
        
        # 4. Softmax 得到注意力权重
        attention_weights = F.softmax(scores, dim=-1)
        
        # 5. 加权求和
        output = torch.matmul(attention_weights, V)
        
        return output, attention_weights
```

---

## 🖼️ Attention 可视化热力图

### 示例：代词消解

```
输入句子: "The cat sat on the mat because it was tired"

Attention 权重热力图：
─────────────────────────────────────────────────────────
        The   cat   sat   on   the   mat  becaus   it   was  tired
─────────────────────────────────────────────────────────
The    0.05  0.10  0.02  0.03  0.01  0.05  0.02  0.03  0.01  0.01
cat    0.02  0.05  0.08  0.02  0.01  0.15  0.02  0.45  0.02  0.02
sat    0.01  0.05  0.03  0.02  0.01  0.02  0.02  0.02  0.01  0.02
on     0.01  0.02  0.02  0.05  0.25  0.10  0.02  0.02  0.01  0.01
the    0.01  0.02  0.02  0.02  0.03  0.02  0.01  0.01  0.01  0.01
mat    0.02  0.10  0.02  0.08  0.02  0.05  0.02  0.05  0.02  0.02
beca.  0.01  0.02  0.02  0.02  0.01  0.02  0.02  0.02  0.01  0.02
it     0.01  0.40  0.02  0.02  0.01  0.05  0.02  0.02  0.02  0.02  ← 最高！
was    0.01  0.02  0.02  0.02  0.01  0.02  0.02  0.02  0.03  0.15
tired  0.01  0.02  0.02  0.02  0.01  0.02  0.02  0.02  0.10  0.05
─────────────────────────────────────────────────────────

💡 关键发现：
"it"（代词）对 "cat" 的注意力权重高达 0.45（最高）
模型自动学会了"代词消解"——理解"它"指的是"猫"
```

---

## 🧩 多头注意力 (Multi-Head Attention)

### 核心思想

```
单头注意力：只能捕捉一种类型的相关性

多头注意力：多个"头"同时工作，捕捉不同层面的关系
  - Head 1: 关注语法结构（主语↔谓语）
  - Head 2:关注语义相似（苹果↔水果）
  - Head 3: 关注指代关系（it↔cat）

每个头有独立的 W_Q, W_K, W_V
```

### 可视化

```
┌─────────────────────────────────────────────────────┐
│              Multi-Head Attention                     │
├─────────────────────────────────────────────────────┤
│                                                      │
│  Input X ──→ ┌─────────────────────────────────┐   │
│              │  Head 1: W_Q¹, W_K¹, W_V¹       │   │
│              │  Head 2: W_Q², W_K², W_V²       │   │
│              │  ...                             │   │
│              │  Head h: W_Qʰ, W_Kʰ, W_Vʰ       │   │
│              └─────────────────────────────────┘   │
│                         │                           │
│                         ▼                           │
│                   Concat[h个输出]                    │
│                         │                           │
│                         ▼                           │
│              Linear (输出变换)                       │
│                         │                           │
│                         ▼                           │
│                      Output                         │
│                                                      │
└─────────────────────────────────────────────────────┘

数学公式：
MultiHead(Q,K,V) = Concat(head_1, ..., head_h) · W_O

其中 head_i = Attention(Q·W_Q^i, K·W_K^i, V·W_V^i)
```

---

## 💡 为什么 Attention 如此重要？

```
┌────────────────────────────────────────────────────────────┐
│                    Self-Attention 的优势                    │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  ✅ 并行计算                                               │
│     RNN 必须顺序计算（后一个依赖前一个）                    │
│     Attention 矩阵运算可并行 → GPU友好，训练速度快           │
│                                                            │
│  ✅ 捕获长距离依赖                                          │
│     RNN: 距离越远，信息衰减越严重                           │
│     Attention: 任意两个位置直接计算，一步到位               │
│     例: "北京今天...空气...差" vs "北京空气...差"         │
│                                                            │
│  ✅ 可解释性强                                             │
│     热力图直观显示模型在"看"什么                            │
│                                                            │
│  ✅ 位置无关                                               │
│     任意位置关系都能建模，不受距离限制                       │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

---

## 🔗 与前后的联系

```
Day 7 (向量数据库)          Day 8 (Self-Attention)         Day 9 (位置编码)
      │                            │                            │
      ▼                            ▼                            ▼
RAG的检索能力              Transformer的核心                  注入位置信息
      │                            │                            │
      └────────────────────────────┼────────────────────────────┘
                                   │
                                   ▼
                         ┌─────────────────┐
                         │  Attention is   │
                         │  All You Need!  │
                         └─────────────────┘
```

---

## ❓ 自测问题

1. **Q/K/V 分别代表什么？在实际计算中起什么作用？**

2. **为什么 Attention Score 需要除以 √d_k？**

3. **多头注意力相比单头有什么优势？**

4. **从热力图如何判断模型是否学会了"代词消解"？**

---

## 📖 扩展阅读

- [Attention Is All You Need](https://arxiv.org/abs/1706.03762)
- [Jay Alammar: The Illustrated Transformer](http://jalammar.github.io/illustrated-transformer/)
- [The Annotated Transformer](http://nlp.seas.harvard.edu/2018/04/03/attention.html)

---

*笔记整理日期：2026-04-10*
*代码位置：/Users/kirito/repos/ai-agent/python-study/day08_self_attention/*
