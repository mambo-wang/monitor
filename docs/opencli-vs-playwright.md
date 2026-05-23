# OpenCLI 与 Playwright 对比

> 本文档整理自 OpenCLI 官方文档及实际使用经验

---

## 1. OpenCLI 简介

OpenCLI 是一个**浏览器自动化命令工具**，将网站、桌面应用、外部 CLI 统一封装为 `opencli <site> <command>` 接口，让 AI Agent 可以驱动浏览器而不需要屏幕截图分析。

### 核心能力

| 能力 | 说明 |
|------|------|
| **浏览器驱动** | 通过 Chrome CDP 协议控制浏览器 |
| **适配器 (Adapter)** | 为网站编写可复用的命令脚本 |
| **网络拦截** | 捕获并分析页面 API 请求 |
| **Cookie 注入** | 复用已登录会话 |

### 解决的问题

- AI Agent 需要操作网页，但不想依赖截图/Vision API
- 爬虫/测试需要复用浏览器登录态
- 需要结构化提取网页数据而非屏幕截图

---

## 2. 安装与使用

```bash
# 安装
npm install -g @jackwener/opencli

# 检查环境
opencli doctor

# 浏览器自动化
opencli browser <session> open "https://example.com"
opencli browser <session> state
opencli browser <session> click <ref>
opencli browser <session> fill "<selector>" "<text>"
```

---

## 3. OpenCLI vs Playwright 对比

| 维度 | OpenCLI | Playwright |
|------|---------|------------|
| **定位** | AI Agent 专用浏览器控制 | 通用浏览器自动化框架 |
| **使用方式** | 命令行 / 代码调用 | 代码库 / CLI |
| **会话管理** | 内置 session 概念 | 需手动管理 browser/context/page |
| **选择器** | ref 编号 + CSS 选择器 | CSS/XPath/Role 选择器 |
| **调试** | 结构化 JSON 输出 | 截图/视频/trace |
| **脚本持久化** | Adapter 保存到 `~/.opencli/clis/` | 测试文件持久化 |
| **学习曲线** | 低（命令即 API） | 中（需写代码） |
| **适用场景** | AI Agent 快速集成 | 复杂端到端测试 |

### 3.1 会话管理

**OpenCLI**:
```bash
opencli browser mysession open "https://example.com"
opencli browser mysession click 24
opencli browser mysession close
```
Session 自动维护 ref 编号、tab lease、网络缓存。

**Playwright**:
```javascript
const browser = await chromium.launch();
const context = await browser.newContext();
const page = await context.newPage();
await page.click('button');
await browser.close();
```
需要手动管理 browser/context/page 生命周期。

### 3.2 选择器策略

**OpenCLI**:
```bash
opencli browser s state              # 获取带 [N] ref 的快照
opencli browser s click 24           # 通过 ref 点击
opencli browser s click "[data-id=btn]"  # CSS 选择器
opencli browser s find --css "button"   # 查询元素
```

**Playwright**:
```javascript
await page.click('button.primary');      // CSS
await page.click('text=提交');           // 文本
await page.click('role=button[name="提交"]'); // ARIA role
await page.locator('button').first().click();
```

### 3.3 调试体验

**OpenCLI**:
```bash
opencli browser s state -f json       # 结构化快照
opencli browser s network            # 查看 API 请求
opencli browser s screenshot         # 截图
```
输出是结构化 JSON，适合 Agent 解析。

**Playwright**:
```javascript
await page.screenshot({ path: 'debug.png' });
await page.video().saveAs('recording.webm');
```
需要人工查看截图/视频。

### 3.4 持久化脚本

**OpenCLI Adapter**:
```javascript
// ~/.opencli/clis/showtime/knowledge.js
cli({
  site: 'showtime',
  name: 'knowledge',
  strategy: Strategy.PUBLIC,
  browser: false,
  func: async () => {
    const res = await fetch('http://127.0.0.1:9090/watcher/api/knowledge/kbs');
    return (await res.json()).data;
  }
});
```
保存后即可 `opencli showtime knowledge` 调用。

**Playwright Test**:
```javascript
// tests/knowledge.spec.ts
test('list knowledge bases', async ({ page }) => {
  await page.goto('/knowledge');
  await expect(page.locator('.kb-list')).toBeVisible();
});
```
需要运行 `npx playwright test`。

---

## 4. OpenCLI 适用场景

| 场景 | 推荐程度 | 说明 |
|------|---------|------|
| AI Agent 网页操作 | ⭐⭐⭐⭐⭐ | 内置 session + 结构化输出 |
| 快速脚本/原型 | ⭐⭐⭐⭐⭐ | 命令即 API，无需写代码 |
| API 发现/调试 | ⭐⭐⭐⭐ | `network` 命令捕获 API |
| 登录态复用 | ⭐⭐⭐⭐ | 绑定已登录 Chrome 会话 |
| 复杂端到端测试 | ⭐⭐⭐ | Playwright 更成熟 |

## 5. Playwright 适用场景

| 场景 | 推荐程度 | 说明 |
|------|---------|------|
| 复杂交互流程 | ⭐⭐⭐⭐⭐ | 丰富的等待/断言机制 |
| 跨浏览器测试 | ⭐⭐⭐⭐⭐ | Chromium/Firefox/WebKit |
| CI/CD 集成 | ⭐⭐⭐⭐⭐ | 内置 reporters/traces |
| 组件测试 | ⭐⭐⭐⭐ | Testing Library 集成 |
| 性能测试 | ⭐⭐⭐⭐ | 内置性能指标 |

---

## 6. 总结

```
OpenCLI = AI Agent 的浏览器遥控器
Playwright = 专业的自动化测试框架
```

- **OpenCLI**：专为 AI Agent 设计，轻量、命令式、结构化输出，适合快速集成和脚本复用
- **Playwright**：功能全面、社区活跃、适合复杂测试场景，但需要写代码

**建议**：
- AI Agent 场景 → 用 OpenCLI
- 复杂测试/CI → 用 Playwright
- 探索性测试 → OpenCLI 快速验证后用 Playwright 固化

---

## 7. 参考

- OpenCLI 官方：https://github.com/jackwener/OpenCLI
- OpenCLI 安装文档：`opencli browser --help`
- Playwright 官方：https://playwright.dev