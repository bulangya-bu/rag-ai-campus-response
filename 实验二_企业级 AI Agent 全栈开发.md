广东东软学院

学生实验报告

**实验课程名称：Java高级应用开发**

**实验项目名称：企业级 AI Agent 全栈开发**

**实验类型：设计型**

**指导教师：黄淳笙**

**实验日期： 2026 年 04月 28 日**

|     |     |     |     |
| --- | --- | --- | --- |
| **学生姓名** |     | **学 号** |     |
| **班 级** |     | **专业名称** |     |
| **实验组**<br><br>**其他成员** | **无** |     |     |
| **实验地点** | **B310** |     |     |
| **实验成绩**<br><br>**（教师签名）** |     |     |     |

<div class="joplin-table-wrapper"><table><tbody><tr><td><p><a id="OLE_LINK1"></a><a id="OLE_LINK2"></a><strong>实验目的与要求</strong></p><ul><li>通过本次实验，将体验一种接近企业真实开发的新方式：</li><li>使用 `Codex` 协助你完成项目开发</li><li>在一个完整项目中学习前端、后端、数据库和 AI 集成</li><li>理解什么是企业级项目，什么是工程化开发</li><li>学会在开发过程中与 AI 协同，而不是只依赖 AI 直接产出代码</li></ul><p>完成本实验后，你应当能够：</p><p>1. 理解企业级全栈项目的基本结构</p><p>2. 使用 Spring Boot 开发后端接口</p><p>3. 使用 Vue3 开发前端页面</p><p>4. 使用 MySQL 保存业务数据</p><p>5. 接入大模型，实现聊天、会话、知识库问答等功能</p><p>6. 使用 Git 分支完成协作开发</p><p>7. 能够说明 Codex 在开发中的实际作用</p></td></tr><tr><td><p><strong>实验原理与内容</strong></p><ol><li>详情见实验指导书</li></ol></td></tr><tr><td><p><strong>实验设备与软件环境</strong></p><ol><li>操作系统：Windows 10/11</li><li>硬件平台：Intel/AMD 处理器，建议 16GB+ 内存</li><li>软件环境：<br>    - Java 21（JDK 21）<br>    - Maven 3.9+（使用项目内嵌的 Maven Wrapper）<br>    - Node.js 18+、npm<br>    - MySQL 8.0<br>    - IntelliJ IDEA（推荐）或 VS Code<br>    - 本地大模型服务（OpenAI 兼容接口，如 Gemini-3-flash，地址 localhost:8317）</li></ol></td></tr><tr><td><p><strong>实验过程与结果（可贴图）</strong></p>

<p>要求：写出实验的具体流程和实验的结果，需要分点说明，相应的地方需要截图说明。</p>

<h3>一、项目结构总览</h3>

<p>本项目是一个"校园 AI 知识问答系统"，采用前后端分离架构：</p>
<ul>
    <li><strong>后端</strong>：Spring Boot 4 + Spring Data JPA + MySQL，位于根目录 <code>src/</code></li>
    <li><strong>前端</strong>：Vue 3 + Vue Router 4 + Vite，位于 <code>frontend/</code></li>
    <li><strong>AI 网关</strong>：通过 OpenAI 兼容 REST API 接入本地大模型</li>
</ul>

<p><strong>【截图1】项目完整目录结构</strong><br>
说明：在 IDEA 或 VS Code 中展开整个项目，展示 java 包结构（config、controller、dto、entity、exception、repository、service）以及 frontend 目录（views、components、router、lib）。</p>

<pre><code>web-shiyan2/
├─ src/main/java/com/example/webshiyan2/
│  ├─ config/          # WebConfig, AppProperties, DataInitializer
│  ├─ controller/      # ChatController, KnowledgeController, DashboardController, ModelConfigController
│  ├─ dto/             # 15 个 Java record 请求/响应对象
│  ├─ entity/          # ChatSession, ChatMessage, KnowledgeDocument, AiModelConfig
│  ├─ exception/       # GlobalExceptionHandler, ResourceNotFoundException
│  ├─ repository/      # 4 个 JPA Repository
│  └─ service/         # ChatService, KnowledgeService, AiGatewayService, ModelConfigService, DashboardService
├─ frontend/
│  ├─ src/views/       # DashboardView, ChatView, KnowledgeView, ModelConfigView
│  ├─ src/components/  # MessageBubble, StatusPill
│  ├─ src/router/      # 4 条路由
│  └─ src/lib/api.js   # 封装 fetch 的 API 客户端
└─ pom.xml</code></pre>

<h3>二、后端环境配置与启动</h3>

<h4>2.1 数据库与服务器配置</h4>
<p>在 <code>src/main/resources/application.yml</code> 中配置：</p>
<ul>
    <li>MySQL 连接：<code>jdbc:mysql://localhost:3306/campus_ai_qa</code>，自动建库（createDatabaseIfNotExist=true）</li>
    <li>JPA 自动建表：<code>ddl-auto: update</code></li>
    <li>服务端口：8080</li>
    <li>AI 大模型地址：<code>http://localhost:8317/v1</code>，模型名 <code>gemini-3-flash</code>，温度 0.35</li>
    <li>CORS 允许前端地址：<code>localhost:5173</code> 和 <code>127.0.0.1:5173</code></li>
</ul>

<p><strong>【截图2】application.yml 配置文件内容</strong><br>
说明：展示完整的 YAML 配置，包括数据库连接、AI 模型参数、CORS 跨域设置。</p>

<h4>2.2 Maven 依赖</h4>
<p><code>pom.xml</code> 引入的核心依赖：</p>
<ul>
    <li>spring-boot-starter-web —— REST 接口</li>
    <li>spring-boot-starter-data-jpa —— 数据库 ORM</li>
    <li>spring-boot-starter-validation —— 参数校验</li>
    <li>mysql-connector-j —— MySQL 驱动</li>
    <li>h2 —— 测试用内存数据库</li>
</ul>

<p><strong>【截图3】pom.xml 文件中的依赖配置</strong><br>
说明：展示 Maven 依赖部分，包含 Spring Boot 4.0.6、Java 21、MySQL 等关键依赖。</p>

<h4>2.3 启动后端</h4>
<p>在项目根目录执行以下命令启动后端：</p>
<pre><code>mvn -s .mvn\local-settings.xml spring-boot:run</code></pre>
<p>启动后 Spring Boot 会自动：</p>
<ol>
    <li>连接 MySQL 并创建数据库 <code>campus_ai_qa</code></li>
    <li>通过 JPA 自动创建 4 张表：<code>chat_sessions</code>、<code>chat_messages</code>、<code>knowledge_documents</code>、<code>ai_model_configs</code></li>
    <li><code>DataInitializer</code> 运行，写入 8 条校园知识样例数据和 1 条默认模型配置</li>
</ol>

<p><strong>【截图4】后端启动成功的控制台日志</strong><br>
说明：展示 Spring Boot 启动日志，包含端口 8080、JPA 建表、初始化数据写入等信息。</p>

<h3>三、前端环境配置与启动</h3>

<h4>3.1 前端项目结构</h4>
<p>前端使用 Vue 3 + Vite 构建，通过 <code>vite.config.js</code> 配置代理转发 <code>/api</code> 请求到后端 8080 端口。</p>

<h4>3.2 启动前端</h4>
<pre><code>cd frontend
npm.cmd install
npm.cmd run dev</code></pre>
<p>前端默认运行在 <code>http://127.0.0.1:5173</code>。</p>

<p><strong>【截图5】前端启动成功的控制台日志</strong><br>
说明：展示 Vite 开发服务器启动日志，显示 Local 地址和代理配置。</p>

<h3>四、功能页面展示</h3>

<h4>4.1 系统总览页面（Dashboard）</h4>
<p>访问 http://127.0.0.1:5173/dashboard，展示：</p>
<ul>
    <li>项目描述区（企业级全栈实验演示）</li>
    <li>四个指标卡片：会话数、消息数、知识条目数、模型状态（在线/离线）</li>
    <li>最近会话列表（最近 6 条）</li>
    <li>知识库重点条目（最近 4 条）</li>
    <li>运行环境确认面板：数据库、激活配置、模型名、模型地址、检查时间</li>
</ul>

<p><strong>【截图6】系统总览页面（浏览器全屏截图）</strong><br>
说明：展示左侧导航栏、顶部状态区域、指标卡片、最近会话列表、知识库重点条目、运行环境确认面板。</p>

<h4>4.2 AI 会话页面（Chat）</h4>
<p>访问 http://127.0.0.1:5173/chat，提供以下功能：</p>
<ul>
    <li>左侧会话列表：显示所有会话标题、预览文本、消息数量，可新建/切换/删除会话</li>
    <li>中间会话工作区：展示对话消息流，支持多轮对话</li>
    <li>右侧引用知识面板：展示当前轮次 AI 回答命中的知识条目（RAG 效果）</li>
    <li>快捷提问按钮（奖学金、宿舍报修、选课、校园卡）</li>
    <li>底部输入框，支持 Enter 发送、Shift+Enter 换行</li>
</ul>
<p>会话会自动根据第一条提问生成标题，AI 回答时会先检索知识库，将相关内容作为上下文传入大模型，实现 RAG（检索增强生成）。</p>

<p><strong>【截图7】AI 会话页面——提问"奖学金申请需要准备哪些材料？"</strong><br>
说明：展示提问后的效果，左侧会话列表自动以问题命名，中间显示 AI 回答，右侧展示命中的知识条目引用。</p>

<p><strong>【截图8】多轮对话效果</strong><br>
说明：展示连续提问（如先问奖学金，再追问申请表在哪里提交），展示会话上下文保持能力。</p>

<h4>4.3 知识库管理页面（Knowledge）</h4>
<p>访问 http://127.0.0.1:5173/knowledge，展示系统内置的 8 条校园知识：</p>
<ul>
    <li>图书馆借阅与超期处理（图书馆）</li>
    <li>奖学金评定时间与申请材料（学生事务）</li>
    <li>课程补退选办理说明（教务）</li>
    <li>宿舍报修流程（后勤）</li>
    <li>校园网账号与密码重置（信息化）</li>
    <li>毕业实习手续办理（就业）</li>
    <li>心理咨询预约流程（心理健康）</li>
    <li>校园卡补办与充值（生活服务）</li>
</ul>
<p>支持新建、编辑、删除、搜索（按标题/标签/内容关键字）、按分类筛选。</p>

<p><strong>【截图9】知识库管理页面——展示 8 条内置知识条目</strong><br>
说明：展开左侧知识条目列表，右侧展示某一条目的编辑表单，包含标题、分类、标签、来源、摘要、正文。</p>

<h4>4.4 模型设置页面（Model Settings）</h4>
<p>访问 http://127.0.0.1:5173/model-settings，支持：</p>
<ul>
    <li>新增/编辑/删除模型配置</li>
    <li>设置模型名称、模型服务 URL、API Key、模型名</li>
    <li>激活/切换当前使用的模型配置（无需重启）</li>
    <li>测试连接——点击"测试连接"按钮验证模型服务是否可达</li>
    <li>API Key 脱敏显示（maskedApiKey）</li>
</ul>

<p><strong>【截图10】模型设置页面</strong><br>
说明：展示左侧模型配置列表（含激活标识）、右侧编辑表单、测试连接结果（成功状态）。</p>

<h3>五、关键代码说明</h3>

<h4>5.1 后端分层架构</h4>
<p>后端采用经典的四层架构：</p>
<ul>
    <li><strong>Controller 层</strong>：接收 HTTP 请求，调用 Service，返回统一 ApiResponse 格式</li>
    <li><strong>Service 层</strong>：业务逻辑（会话管理、知识检索、AI 网关通信）</li>
    <li><strong>Repository 层</strong>：Spring Data JPA 数据访问</li>
    <li><strong>Entity 层</strong>：JPA 实体映射 MySQL 表</li>
</ul>

<p><strong>【截图11】Controller 层——ChatController.java 核心代码</strong><br>
说明：展示 ChatController 中 5 个接口方法（listSessions、createSession、getConversation、sendMessage、deleteSession），注意统一返回 ApiResponse 格式。</p>

<p><strong>【截图12】Service 层——AiGatewayService.java 中调用大模型的核心代码</strong><br>
说明：展示 generateAnswer 方法构建 system prompt（含知识库上下文）、通过 RestClient 调用 OpenAI 兼容接口 /v1/chat/completions、解析 ChatCompletionResponse 提取回答内容、以及 fallback 降级机制。</p>

<p><strong>【截图13】知识库检索——KnowledgeService.java 中评分算法</strong><br>
说明：展示 scoreDocument 方法的三种评分策略——精确匹配（+12 分）、Token 分词匹配（4 字以上+4 分，否则+2 分）、字符窗口匹配（+1 分），以及 findRelevantDocuments 方法。</p>

<h4>5.2 数据库实体设计（ER 图）</h4>

<p><strong>【截图14】四个 JPA 实体类</strong><br>
说明：并排展示 ChatSession、ChatMessage（含 ManyToOne 外键关联 session_id）、KnowledgeDocument、AiModelConfig 四个实体的字段定义。</p>

<p>数据表关系：</p>
<ul>
    <li><code>chat_sessions</code>（会话） 1 → N <code>chat_messages</code>（消息）</li>
    <li><code>knowledge_documents</code>（知识条目）独立表，通过关键词匹配被 RAG 引用</li>
    <li><code>ai_model_configs</code>（模型配置）独立表，active 字段标记当前激活配置</li>
</ul>

<h4>5.3 RAG 工作流程</h4>
<p>用户提问 → 前端 POST /api/chat/sessions/{id}/messages → ChatService.sendMessage()：</p>
<ol>
    <li>保存用户消息到数据库</li>
    <li>如果是第一条消息，自动用提问内容重命名会话标题</li>
    <li>KnowledgeService.findRelevantDocuments() 对所有知识条目评分，取 Top 4</li>
    <li>AiGatewayService.generateAnswer() 将命中的知识条目作为 system prompt 上下文</li>
    <li>调用本地大模型 /v1/chat/completions</li>
    <li>保存 AI 回复（含引用知识 ID 和标题）</li>
    <li>返回给前端，右侧面板展示引用知识</li>
</ol>

<p><strong>【截图15】ChatService.sendMessage() 完整流程代码</strong><br>
说明：展示上述 1-7 步的完整 Java 方法代码。</p>

<h3>六、测试验证</h3>
<p>项目包含两个测试类：</p>
<ul>
    <li><strong>WebShiyan2ApplicationTests</strong> —— Spring 上下文加载测试</li>
    <li><strong>KnowledgeServiceTests</strong> —— 使用 Mockito 验证知识库检索排序（中文问题"奖学金申请要准备什么材料"应优先匹配"奖学金评定时间与申请材料"条目）</li>
</ul>

<p><strong>【截图16】测试运行结果</strong><br>
说明：展示在 IDEA 中运行 Maven 测试（<code>mvn -s .mvn\local-settings.xml test</code>）通过的控制台输出。</p>

<h3>七、数据持久化验证</h3>
<p>项目启动后，可通过 MySQL 客户端查看自动创建的表和数据：</p>
<ul>
    <li><code>chat_sessions</code> 表：会话记录</li>
    <li><code>chat_messages</code> 表：消息记录（role、content、citationIds）</li>
    <li><code>knowledge_documents</code> 表：8 条种子知识数据</li>
    <li><code>ai_model_configs</code> 表：默认模型配置</li>
</ul>

<p><strong>【截图17】MySQL 数据库表结构及数据</strong><br>
说明：使用 IDEA Database 工具或 MySQL Workbench 查看 campus_ai_qa 数据库中的四张表，展示表结构和初始数据。</p>

</td></tr>
<tr><td><p><strong>操作异常问题与解决方案（重点）</strong></p>

<p><strong>1. MySQL 连接失败 —— "Access denied for user 'root'@'localhost'"</strong></p>
<ul>
    <li><strong>问题</strong>：application.yml 中 MySQL 密码为空，但本地 MySQL root 用户配置了密码</li>
    <li><strong>解决</strong>：修改 application.yml 中的 spring.datasource.password 为本地实际密码，或修改 MySQL 用户认证方式为 mysql_native_password</li>
</ul>

<p><strong>2. 前端跨域请求被拦截 —— CORS 错误</strong></p>
<ul>
    <li><strong>问题</strong>：前端运行在 localhost:5173，后端在 localhost:8080，跨域请求被浏览器拦截</li>
    <li><strong>解决</strong>：后端的 WebConfig 配置了 CORS 跨域设置，同时 Vite 配置了 proxy 将 /api 请求转发到后端。需要确保 application.yml 中 cors.allowed-origins 包含前端地址</li>
</ul>

<p><strong>3. 大模型服务连接失败 —— "Connection refused: localhost:8317"</strong></p>
<ul>
    <li><strong>问题</strong>：AI 网关默认指向 localhost:8317，本地未启动大模型服务</li>
    <li><strong>解决</strong>：启动本地大模型服务（如 Gemini-3-flash），或在模型设置页面修改为可访问的模型地址。系统在 AI 不可用时自动降级（fallback），返回知识库内容作为参考</li>
</ul>

<p><strong>4. 前端页面空白 / API 请求 404</strong></p>
<ul>
    <li><strong>问题</strong>：前端 Vite 代理配置不正确，/api 请求未正确转发到后端</li>
    <li><strong>解决</strong>：检查 frontend/vite.config.js 中的 proxy 配置，确保 target 指向 http://localhost:8080</li>
</ul>

<p><strong>5. 启动后端时端口被占用 —— "Port 8080 was already in use"</strong></p>
<ul>
    <li><strong>问题</strong>：8080 端口被其他进程占用</li>
    <li><strong>解决</strong>：使用 <code>netstat -ano | findstr :8080</code> 查找占用进程并关闭，或在 application.yml 中修改 server.port</li>
</ul>

<p><strong>6. Maven 依赖下载慢</strong></p>
<ul>
    <li><strong>问题</strong>：首次构建需要下载大量依赖，默认 Maven 中央仓库下载速度慢</li>
    <li><strong>解决</strong>：项目已配置阿里云镜像（.mvn/local-settings.xml），使用 <code>mvn -s .mvn\local-settings.xml</code> 指定镜像配置加速下载</li>
</ul>

</td></tr>
<tr><td><p><strong>实验总结</strong></p>

<p>通过本次实验，完成了一个企业级全栈 AI 问答系统的开发，涵盖以下技术要点和实践经验：</p>

<ol>
    <li><strong>前后端分离架构</strong>：后端 Spring Boot 提供 REST API，前端 Vue 3 + Vite 独立开发部署，通过 HTTP 通信和 CORS 跨域协作，理解了企业级项目的工程化目录结构和分层设计理念。</li>
    <li><strong>数据库设计与 ORM 映射</strong>：使用 JPA 实体自动建表，设计了会话、消息、知识库、模型配置四张表及其关联关系，掌握了 Spring Data JPA 的使用方式。</li>
    <li><strong>RAG 检索增强生成</strong>：实现了关键词评分的知识库检索算法（精确匹配 + 分词匹配 + 字符窗口匹配），将命中的知识条目作为大模型上下文，让 AI 回答有据可依，避免凭空编造。这是企业级 AI 应用的重要实践。</li>
    <li><strong>AI 网关集成</strong>：通过 OpenAI 兼容 REST API 接入本地大模型，实现了可切换的模型配置管理、连接测试、API Key 安全处理等功能，掌握了 AI Gateway 的设计模式。</li>
    <li><strong>异常处理体系</strong>：使用 @RestControllerAdvice 实现全局异常统一处理，区分不同异常类型（404、400、502、500）返回对应的 HTTP 状态码和友好提示，学习了企业级应用的健壮性要求。</li>
    <li><strong>AI 协同开发</strong>：在开发过程中使用 AI 辅助工具完成了项目的分层设计、代码生成、联调排错等工作，体验了 AI 作为"协作式开发助手"在企业开发中的实际作用——人负责确定业务目标、技术选型和架构决策，AI 负责加速编码和查漏补缺。</li>
</ol>

<p>本次实验不仅掌握了全栈开发的技术技能，更重要的是理解了企业级 AI 应用的完整开发流程：从需求分析、系统设计、数据库建模、前后端开发、AI 集成到测试验证，体会了工程化思维在实际项目中的重要性。</p>

</td></tr>
</tbody></table></div>
