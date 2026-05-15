# Day 9 学习笔记：Positional Encoding 位置编码

## 📚 今日概述

| 项目 | 内容 |
|------|------|
| **主题** | 位置编码 (Positional Encoding) |
| **核心问题** | Self-Attention 是位置无关的，需要注入位置信息 |
| **代码** | day09_positional_encoding.py, day09_rope_implementation.py |

---

## 🎯 学习目标

1. ✅ 理解为什么 Self-Attention 需要位置编码
2. ✅ 掌握 Sinusoidal（正弦/余弦）位置编码的原理
3. ✅ 理解相对位置编码 vs 绝对位置编码
4. ✅ 了解 RoPE 旋转位置编码（Llama 使用）

---

## ⚠️ 核心问题：Self-Attention 是"位置盲"的

### 问题演示

```
"狗咬人" → Self-Attention 处理后 → [0.5, 0.3, ...]
"人咬狗" → Self-Attention 处理后 → [0.5, 0.3, ...]  ← 完全相同！

原因：
Self-Attention 的计算是"置换不变"的
每个词只是对所有词做加权求和，顺序变化，结果不变
```

### 问题本质

```
┌─────────────────────────────────────────────────────────────┐
│                    置换不变性 (Permutation Invariance)        │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Attention = Softmax(Q·K^T / √d) · V                       │
│                                                              │
│  在这个公式中：                                              │
│  - Q, K, V 只描述语义关系，不包含位置信息                    │
│  - "狗"和"咬"的关系，和它们在第1位还是第3位无关             │
│  - 因此 "狗咬人" 和 "人咬狗" 得到相同的结果                   │
│                                                              │
└─────────────────────────────────────────────────────────────┘

解决方案：注入位置信息 → Positional Encoding
```

---

## 📝 Sinusoidal 位置编码

### 核心思想

```
传统方法：为每个位置创建一个固定向量（可学习）
         缺点：不能外推到训练时没见过的长度

Sinusoidal：用不同频率的正弦/余弦函数生成位置向量
            优点：可以外推到任意长度！
```

### 数学公式

```
PE(pos, 2i)   = sin( pos / 10000^(2i/d_model) )
PE(pos, 2i+1) = cos( pos / 10000^(2i/d_model) )

其中：
- pos: 位置索引 (0, 1, 2, 3, ...)
- i: 维度索引 (0, 1, 2, ..., d_model/2)
- d_model: 词向量维度（如 512）

公式解读：
- 偶数维度用 sin，奇数维度用 cos
- 不同维度使用不同的频率（由 10000^(2i/d_model) 控制）
```

### 代码实现

```python
class PositionalEncoding:
    def _create_encoding_table(self, max_len: int, d_model: int) -> torch.Tensor:
        # 创建位置索引
        position = torch.arange(0, max_len, dtype=torch.float).unsqueeze(1)
        
        # 创建频率指数
        # div_term[i] = 10000^(-2i/d_model)
        div_term = torch.exp(
            torch.arange(0, d_model, 2, dtype=torch.float) * 
            (-math.log(10000.0) / d_model)
        )
        
        # 创建编码表
        pe = torch.zeros(max_len, d_model)
        
        # 偶数维度: sin
        pe[:, 0::2] = torch.sin(position * div_term)
        
        # 奇数维度: cos
        pe[:, 1::2] = torch.cos(position * div_term)
        
        return pe
    
    def forward(self, x: torch.Tensor) -> torch.Tensor:
        # 词嵌入 + 位置编码
        return x + self.pe[:, :x.size(1), :]
```

---

## 🖼️ Sinusoidal 编码可视化

### 1. 不同维度的波形

```
维度0 (sin, 最低频):       维度1 (cos, 最低频):      维度32 (sin, 较高频):
───────────────────      ────────────────────      ───────────────────
1.0 ┤    ╱╲    ╱╲       1.0 ┤ ╱    ╲    ╱        1.0 ┤ ╱╲╱╲╱╲╱╲╱╲
    │   ╱  ╲  ╱  ╲        │╱      ╲╱          │  ╱╲╱╲╱╲╱╲╱╲╱╲╱╲
-1.0 ┤─╱────╲╱────╲─    -1.0 ┤─╲─────╱╲─       -1.0 ┤╱╲╱╲╱╲╱╲╱╲╱╲╱╲
    position →             position →            position →
    
    变化缓慢              变化缓慢              变化快速
    可感知远距离位置        可感知远距离位置       可感知近距离精细差异
    
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
💡 规律：维度越高（i越大），频率越高，变化越快
         这样设计是为了让不同位置有不同"分辨率"的位置表示
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### 2. 位置编码热力图

```
每个位置（横轴）对应一个 d_model 维的向量（纵轴）
颜色：红色=正值，蓝色=负值，白色=0

热力图特征：
- 低维度：颜色渐变缓慢（长周期）
- 高维度：颜色交替频繁（短周期）
- 相邻位置：编码相似（平滑过渡）
```

---

## 🤔 为什么用正弦/余弦？—— 两大优势

### 优势1：可外推到任意长度

```
训练时见过的位置：0, 1, 2, ..., 1000
                ↓
推理时可以处理：1001, 1002, ..., 10000
                ↓
因为 sin/cos 函数可以计算任意大小的输入！
```

### 优势2：允许模型学习相对位置

```
【数学性质】
sin(A)·sin(B) + cos(A)·cos(B) = cos(A-B)

在 Attention 计算中，会出现 PE_a · PE_b^T 这样的项
这个项与 (a-b) 有关，即相对位置！

因此模型可以通过学习掌握相对位置关系
```

---

## 🔄 RoPE 旋转位置编码（Llama 使用）

### 核心创新

```
Sinusoidal: 把位置向量加到词嵌入上（绝对位置编码）
            最终表示 = 词嵌入 + 位置嵌入

RoPE:      把 Query 和 Key 向量旋转一个角度（相对位置编码）
            旋转角度与位置成正比
            效果：Attention 分数天然包含相对位置信息
```

### 2D 简化理解

```
【旋转矩阵】
R(pos, θ) = | cos(pos·θ)  -sin(pos·θ) |
            | sin(pos·θ)   cos(pos·θ) |

【2D 向量旋转示例】
原始向量 v = [1, 0]（指向x轴正方向）

位置0: v' = R(0·θ) · v = [1, 0]               ──────→
位置1: v' = R(1·θ) · v = [cos(θ), sin(θ)]     ───╱
位置2: v' = R(2·θ) · v                         ─╱
                                                   ↑
                              旋转角度随位置线性增加
```

### 旋转效果可视化

```
位置0: ──────→
位置1: ───╱
位置2: ─╱
位置3:╱
       ↑
    逆时针旋转
```

### 为什么 RoPE 更适合长序列？

```
【关键数学性质】

位置 m 的 Query 和位置 n 的 Key 的点积：

Attention(m,n) = Q(m) · K(n)
               = (R(m)·q) · (R(n)·k)
               = q · (R(m-n) · k)  ← 只依赖相对位置 (m-n)！

【结论】
1. RoPE 的注意力分数天然只与相对位置有关
2. 无论序列多长，相邻位置的区分度保持一致
3. Llama 能支持 32k+ token 的重要原因之一
```

---

## 📊 位置编码对比

| 类型 | 方法 | 优点 | 缺点 | 代表模型 |
|------|------|------|------|----------|
| **Sinusoidal** | sin/cos函数 | 可外推到任意长度 | 难以学习复杂相对位置 | Transformer (Google) |
| **Learned** | 可学习的绝对位置向量 | 模型自适应 | 不能外推 | BERT, GPT-2 |
| **RoPE** | 旋转矩阵 | 相对位置，长上下文友好 | 实现复杂 | **Llama**, GLM, ChatGLM |
| **ALiBi** | Attention+线性偏置 | 不需要位置嵌入 | 需要调参 | BLOOM |

---

## 🔗 与前后内容的联系

```
Day 8 (Self-Attention)                    Day 9 (Positional Encoding)
       │                                         │
       ▼                                         │
┌──────────────────────┐                        │
│ Self-Attention       │                        │
│ 本身只看语义关系      │ ──────────────────────► │
│ 不包含位置信息        │   注入位置信息          │
└──────────────────────┘                        │
       │                                         │
       ▼                                         ▼
┌─────────────────────────────────────────────────────────┐
│                    Transformer Encoder                    │
│  Input Embedding + Positional Encoding ──► Multi-Head   │
│                                          Self-Attention  │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │  LayerNorm  │
                    │  FeedForward│
                    │  Residual   │
                    └─────────────┘
```

### 在 RAG/Agent 中的作用

```
用户问题 "苹果是什么"
       │
       ▼
┌─────────────────┐
│ 1. 词嵌入        │ → 只包含语义信息
└─────────────────┘
       │
       │ + Positional Encoding
       ▼
┌─────────────────┐
│ 2. 位置编码      │ → 注入"第几个字"的信息
└─────────────────┘
       │
       ▼
┌─────────────────┐
│ 3. Self-Attention │ → 理解语义关系 + 位置关系
└─────────────────┘
       │
       ▼
┌─────────────────┐
│ 4. 生成回答      │
└─────────────────┘
```

---

## ❓ 自测问题

1. **为什么 Self-Attention 本身无法区分"狗咬人"和"人咬狗"？**

2. **Sinusoidal 位置编码的公式是什么？偶数维和奇数维分别用什么函数？**

3. **RoPE 和 Sinusoidal 的核心区别是什么？**

4. **为什么 RoPE 比 Sinusoidal 更适合长序列？**

5. **位置编码是如何加到词嵌入上的？**

---

## 📖 扩展阅读

### 论文
- [Attention Is All You Need](https://arxiv.org/abs/1706.03762) - 原始 Transformer
- [RoFormer: Enhanced Transformer with RoPE](https://arxiv.org/abs/2104.09864) - RoPE 论文

### 博客
- [Jay Alammar: The Illustrated Transformer](http://jalammar.github.io/illustrated-transformer/)
- [苏剑林: 旋转式位置编码](https://spaces.ac.cn/archives/8265)
- [苏剑林: 让研究人员绞尽脑汁的认证问题](https://spaces.ac.cn/archives/8454)

---

*笔记整理日期：2026-04-10*
*代码位置：/Users/kirito/repos/ai-agent/python-study/day09_positional_encoding/*
