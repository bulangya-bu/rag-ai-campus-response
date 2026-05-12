# 校园 AI 知识问答系统

一个用于课程实验的企业级全栈示例项目，主题为“校园 AI 知识问答系统”。

项目目标：

- 用 `Spring Boot` 提供后端接口
- 用 `Vue 3 + Vite` 搭建前端工作台
- 用 `MySQL` 保存会话和知识库数据
- 接入本地 OpenAI 兼容模型接口，实现校园问答与知识库增强
- 展示前后端分离、工程化目录结构和 AI 协同开发流程

## 技术栈

- 后端：Spring Boot 4、Spring Web、Spring Data JPA、MySQL
- 前端：Vue 3、Vue Router、Vite
- AI 网关：OpenAI 兼容接口
- 数据库：MySQL 8

## 运行环境

已按实验要求写入默认配置：

- MySQL：`localhost:3306`
- 数据库用户：`root`
- 数据库密码：空
- 自动创建数据库：`campus_ai_qa`
- 模型地址：`http://localhost:8317/v1`
- API Key：`111111`
- 模型名：`gemini-3-flash`

后端配置文件位置：

- [application.yml](D:\ideal project\web-shiyan2\src\main\resources\application.yml)

## 项目结构

```text
web-shiyan2
├─ src/main/java/com/example/webshiyan2
│  ├─ config        # 配置、初始化数据、跨域
│  ├─ controller    # REST 接口
│  ├─ dto           # 请求/响应对象
│  ├─ entity        # JPA 实体
│  ├─ exception     # 全局异常处理
│  ├─ repository    # 数据访问层
│  └─ service       # 业务逻辑与 AI 网关
├─ src/main/resources
│  └─ application.yml
├─ frontend
│  ├─ src/components
│  ├─ src/views
│  ├─ src/router
│  └─ src/lib
└─ README.md
```

## 核心功能

### 1. AI 会话

- 新建会话
- 保存多轮消息
- 基于历史上下文继续追问
- 自动根据首条提问生成会话标题

### 2. 校园知识库

- 内置 8 条校园样例知识
- 支持新增、编辑、删除、检索
- 支持按分类和关键字查询

### 3. 知识库增强问答

- 提问时先检索本地知识库
- 把命中的校园知识作为上下文传给大模型
- 返回答案时展示当前轮次命中的知识条目

### 4. 系统总览

- 展示会话数量、消息数量、知识条目数量
- 展示模型连接状态
- 展示最近会话和知识库重点条目

## 已实现的主要接口

### 总览与状态

- `GET /api/dashboard/overview`
- `GET /api/system/status`

### 会话与消息

- `GET /api/chat/sessions`
- `POST /api/chat/sessions`
- `GET /api/chat/sessions/{id}`
- `POST /api/chat/sessions/{id}/messages`
- `DELETE /api/chat/sessions/{id}`

### 知识库

- `GET /api/knowledge-documents`
- `GET /api/knowledge-documents/{id}`
- `POST /api/knowledge-documents`
- `PUT /api/knowledge-documents/{id}`
- `DELETE /api/knowledge-documents/{id}`

## 本地启动

### 1. 启动后端

```powershell
mvn -s .mvn\local-settings.xml spring-boot:run
```

### 2. 启动前端

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

### 3. 访问地址

- 前端开发地址：`http://127.0.0.1:5173`
- 后端接口地址：`http://127.0.0.1:8080/api`

## 数据说明

项目启动后会自动：

1. 创建数据库 `campus_ai_qa`
2. 自动建表
3. 在知识库为空时写入 8 条校园样例数据

主要表：

- `chat_sessions`
- `chat_messages`
- `knowledge_documents`

## Codex 在这个项目中的实际作用

这部分可以直接作为实验说明使用：

1. Codex 帮助完成了项目的分层设计，而不是只生成零散页面。
2. Codex 协助实现了后端实体、接口、前端页面、知识库检索和 AI 网关接入。
3. Codex 在开发过程中承担了“脚手架 + 联调 + 排错 + 结构整理”的角色。
4. 人负责确定业务目标、实验要求、运行环境和技术取舍，Codex 负责加速编码、查漏补缺和验证。
5. 这说明在企业开发里，AI 更像协作式开发助手，而不是完全替代开发者。

## 已完成验证

- Maven 测试通过
- Vue 前端构建通过
- MySQL 本地连接可用
- 本地模型接口 `/v1/models` 可访问

## 关键文件

- [后端入口](D:\ideal project\web-shiyan2\src\main\java\com\example\webshiyan2\WebShiyan2Application.java)
- [聊天服务](D:\ideal project\web-shiyan2\src\main\java\com\example\webshiyan2\service\ChatService.java)
- [AI 网关](D:\ideal project\web-shiyan2\src\main\java\com\example\webshiyan2\service\AiGatewayService.java)
- [知识库服务](D:\ideal project\web-shiyan2\src\main\java\com\example\webshiyan2\service\KnowledgeService.java)
- [前端应用壳](D:\ideal project\web-shiyan2\frontend\src\App.vue)
- [会话页面](D:\ideal project\web-shiyan2\frontend\src\views\ChatView.vue)
- [知识库页面](D:\ideal project\web-shiyan2\frontend\src\views\KnowledgeView.vue)
