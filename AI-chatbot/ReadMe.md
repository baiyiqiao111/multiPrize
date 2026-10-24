# AI-chatbot —— 智能数据助手（Tool Calling Agent）

`AI-chatbot` 是 multiPrize 项目中的**智能问答服务**：用户用自然语言提问（"我昨天听了多久""为什么我的奖励变少了"），服务通过 **Spring AI ChatClient + Tool Calling** 让大模型自主选择工具，调用 `prize-sender` / `prize-center` 拉取播放记录、发奖记录、发奖规则，再由大模型把结构化数据整理成中文回答。

核心设计原则：**大模型只负责"选工具 + 组织语言"，所有计算与比对都在 Java 代码里完成**，避免大模型算错日期、算错奖励。

---

## 1. 功能概览

| 模块 | 能力 | 关键实现 |
| --- | --- | --- |
| 对话入口 | `POST /chat/ask`，按 `userId + sessionId` 隔离会话 | `McpChatBotController` |
| Agent | 多轮 Tool Calling（最多 10 轮），带记忆 | `AgentConfig` + `DetailedToolCallingAdvisor` |
| 工具集 | 查播放记录、查发奖记录、查规则、**检查发奖是否正常** | `AIAgentTool`（`@Tool`） |
| 发奖核对 | 按天重放规则计算"应发"，与实际发奖逐阶段比对 | `PrizeCheckService` |
| 跨服务调用 | Feign 调用 `prize-sender` / `prize-center` | `AiChatBotIntegration` + `*Client` |
| 意图识别（旧链路） | 让模型输出 JSON 意图 → 代码执行查询 → 再让模型转文本 | `IntentRecognizeService` |
| 会话管理 | 内存会话上下文，超长自动裁剪 | `McpContextManager` / `McpProfessionalContextManager` |

---

## 2. 技术栈

- **JDK 17**、**Spring Boot 4.0.7**（Web MVC）
- **Spring AI 2.0.0**：`ChatClient`、`@Tool`、`ToolCallingAdvisor`、`MessageChatMemoryAdvisor`、`spring-ai-starter-model-openai`
- **Spring Cloud 2025.1.3**：Eureka Client + OpenFeign
- **OkHttp 4.12.0**：旧链路直接调用 OpenAI HTTP 接口
- **Apache Commons JEXL 3.4.0**：与 `prize_sender` 使用同一表达式引擎计算发奖规则
- **Jackson**、**Lombok**

> `pom.xml` 中引入了 `spring-boot-starter-data-redis`（注释写"生产必加"）与 Kotlin 相关依赖，当前代码**未使用**；`maven-compiler-plugin` 中 `compile`/`testCompile` 执行被重复声明（见第 13 章）。

---

## 3. 目录结构

```
AI-chatbot/
├── pom.xml
└── src/main/
    ├── java/com/example/AI_chatbot/
    │   ├── AiChatbotApplication.java
    │   ├── agent/
    │   │   ├── MusicAgent.java                  # 函数式接口 chat(sessionId, userInput, userId)
    │   │   └── DetailedToolCallingAdvisor.java  # 多轮工具调用 + 详细日志
    │   ├── config/
    │   │   ├── AgentConfig.java                 # 构建 ChatClient（system prompt / tools / advisors）
    │   │   └── JsonConfig.java                  # ObjectMapper
    │   ├── context/
    │   │   ├── McpContextManager.java           # 通用会话上下文（内存）
    │   │   └── McpProfessionalContextManager.java # 意图抽取专用上下文
    │   ├── controller/
    │   │   ├── McpChatBotController.java        # /chat/ask、/chat/reset
    │   │   ├── cmd/ChatQueryCmd.java            # question / userId / sessionId
    │   │   └── Vo/                              # AnswerVo / BaseVo / SystemConfigVo
    │   ├── entity/                              # ChatMessage / OpenAIChatRequest / OpenAiChatResponse / Usage / BizCondition
    │   ├── exception/                           # 5 个跨服务查询异常
    │   ├── Integration/
    │   │   ├── AiChatBotIntegration.java        # 统一封装 Feign 调用 + 异常兜底
    │   │   ├── client/PrizeSenderClient.java    # @FeignClient("prize-sender")
    │   │   ├── client/PrizeCenterClient.java    # @FeignClient("prize-center")
    │   │   └── vo/                              # PlayRecordVo / PrizeRecordVo / OrderRecordVo / SystemConfigVo ...
    │   ├── service/
    │   │   ├── McpChatBotService.java           # 旧链路：纯对话
    │   │   ├── IntentRecognizeService.java      # 旧链路：意图识别 + 查询 + 转文本
    │   │   ├── OpenAiClientService.java         # OkHttp 直连 OpenAI
    │   │   └── PrizeCheckService.java           # 发奖核对（核心业务）
    │   └── tool/AIAgentTool.java                # 4 个 @Tool
    └── resources/
        ├── application.properties               # 端口 8088、Eureka、OpenAI、Spring AI
        └── logback.xml
```

---

## 4. 架构与数据流

```
前端 ──POST /chat/ask {question, userId, sessionId}──► McpChatBotController
                                                          │
                                                          ▼
                                                    MusicAgent.chat()
                                                          │
                                                          ▼
                                          ChatClient.prompt()
                                            ├─ system prompt（注入当前时间/今天/星期）
                                            ├─ user(question)
                                            ├─ advisors: MessageChatMemoryAdvisor（conversationId = userId:sessionId）
                                            │            DetailedToolCallingAdvisor（最多 10 轮）
                                            └─ toolContext: {sessionId, userId}
                                                          │
                                    ┌─────────────────────┴─────────────────────┐
                                    ▼                                           ▼
                          大模型决定调用哪个 @Tool                    工具执行（Java 代码）
                                    │                                           │
                                    │  queryPlayRecord      ──Feign──► prize-sender /playRecord/queryAll
                                    │  queryPrizeRecord     ──Feign──► prize-sender /prizeRecord/queryAll
                                    │  querySystemConfig    ──Feign──► prize-sender /system-config/code/{code}
                                    │  checkPrizeRecord     ──► PrizeCheckService（内部多次 Feign）
                                    ▼                                           │
                          工具结果回填 conversationHistory ◄────────────────────┘
                                    │
                                    ▼
                          大模型生成自然语言回答 ──► AnswerVo
```

---

## 5. 环境依赖

| 组件 | 说明 | 默认值（见 `application.properties`） |
| --- | --- | --- |
| OpenAI | Chat Completions 接口 | `https://api.openai.com/v1/chat/completions`，模型 `gpt-5.4-mini` |
| Eureka | 注册中心，本服务名 `ai-chatbot` | `http://107.22.136.67:8761/eureka/` |
| prize-sender | 播放记录 / 发奖记录 / 系统配置 | 由 Eureka 解析 |
| prize-center | 奖品扣减记录 | 由 Eureka 解析 |
| 时区 | Jackson 时区 | `America/Los_Angeles` |

> ⚠️ `openai.api-key` 通过 `${openai.api-key}` 占位，需通过环境变量或启动参数注入；数据库账号密码为明文（当前代码未直接使用数据库）。

---

## 6. 快速开始

### 6.1 启动

```bash
# 先启动 eureka-server、prize_center、prize_sender
export openai.api-key=sk-xxx        # Windows: set openai.api-key=sk-xxx
./mvnw spring-boot:run              # Linux / macOS
mvnw.cmd spring-boot:run            # Windows
```

服务端口 `8088`，注册名 `ai-chatbot`。

### 6.2 调用示例

```bash
# 提问
curl -X POST http://localhost:8088/chat/ask \
  -H "Content-Type: application/json" \
  -d '{"question":"我昨天听了多久，奖励发对了吗？","userId":1001,"sessionId":"s-001"}'

# 重置会话
curl -X POST "http://localhost:8088/chat/reset?sessionId=s-001"
```

---

## 7. 接口说明

### 7.1 统一返回结构

`BaseVo`：`{ "code": 200, "cost": 1234, "success": true, "errorMessage": null }`

### 7.2 对话 `/chat`

| 方法 | 路径 | 参数 | 说明 |
| --- | --- | --- | --- |
| POST | `/chat/ask` | Body: `{question, userId, sessionId}` | 提问，返回 `AnswerVo{answer, baseVo}` |
| POST | `/chat/reset` | `sessionId`（query） | 清空会话上下文 |

> `@CrossOrigin` 已开启，前端可直接跨域调用。

### 7.3 工具（供大模型调用，非 HTTP 接口）

| 工具名 | 参数 | 说明 |
| --- | --- | --- |
| `queryPlayRecord` | `startTime`, `endTime`, `ToolContext` | 查用户播放明细 |
| `queryPrizeRecord` | `startTime`, `endTime`, `pageStart`, `pageSize`, `ToolContext` | 查用户发奖记录 |
| `querySystemConfig` | `code`（`PRIZE_STAGE_RULE` / `PRIZE_AMOUNT_RULE`） | 查发奖规则 |
| `checkPrizeRecord` | `startDate`, `endDate`, `ToolContext` | **检查发奖是否正常**（核心） |

`ToolContext` 中的 `userId` 由前端传入，**不来自大模型**——防止模型被诱导查询他人数据。

---

## 8. 对外依赖（Feign）

| 客户端 | 方法 | 下游接口 |
| --- | --- | --- |
| `PrizeSenderClient` | `queryPlayRecord` | `GET prize-sender /playRecord/queryAll` |
| | `queryPlayRecordStatistic` | `GET prize-sender /playRecordStatistic/queryByUserIdAndPlayDate` |
| | `queryPrizeRecord` | `GET prize-sender /prizeRecord/queryAll` |
| | `querySystemConfigByCode` | `GET prize-sender /system-config/code/{code}` |
| `PrizeCenterClient` | `queryOrderRecord` | `GET prize-center /orderRecord/all`（时间格式 `yyyy-MM-dd'T'HH:mm:ss`） |

---

## 9. 配置项

| 配置 | 值 | 说明 |
| --- | --- | --- |
| `server.port` | `8088` | 独立端口 |
| `spring.application.name` | `ai-chatbot` | 服务名 |
| `openai.api-url` | `https://api.openai.com/v1/chat/completions` | 旧链路（OkHttp）使用 |
| `openai.model` | `gpt-5.4-mini` | 模型 |
| `openai.temperature` | `0.7` | 温度 |
| `openai.max-message-size` | `16` | 会话上下文最大消息条数 |
| `spring.ai.openai.api-key` | `${openai.api-key}` | Spring AI 独立凭证 |
| `spring.ai.openai.chat.options.model` | `${openai.model}` | Spring AI 模型 |
| `music-agent.max-iterations` | `10`（默认） | 最大工具调用轮次 |

---

## 10. 前端页面

本服务为纯后端服务，无静态页面；前端通过 `POST /chat/ask` 接入（已开启 CORS）。

---

## 11. 重点功能设计

### 11.1 为什么用 Tool Calling，而不是"让大模型输出 JSON 再解析"

#### 11.1.1 旧链路（`IntentRecognizeService`）的问题

旧链路是典型的 **Prompt → JSON → 代码解析 → 查询 → 再 Prompt 转文本**：

```
用户问题 ──► 大模型（system prompt 要求输出 JSON）──► {"intentCode":"PRIZE_RECORD", ...}
                                                          │
                                              ObjectMapper 解析（可能失败）
                                                          │
                                              代码 if-else 分发查询
                                                          │
                                              再调一次大模型转成中文
```

推演问题：

| 问题 | 说明 |
| --- | --- |
| **JSON 不稳定** | 模型可能输出 ```json 围栏、中文全角引号、多余解释文字，需要 `stripCodeFence` 兜底，仍可能解析失败 |
| **意图枚举写死** | 新增能力要改 prompt + 改 if-else + 改 `BizCondition`，扩展成本高 |
| **无法多步推理** | "为什么奖励变少"需要先查播放、再查规则、再查发奖、再比对，单次 JSON 抽取做不到 |
| **两次调用成本** | 每次问答至少 2 次大模型调用，延迟翻倍 |
| **参数靠模型填** | 日期、分页等参数由模型生成，容易漏填（代码里不得不写 `getStartIndex()==null?0:...` 兜底） |

#### 11.1.2 新链路（Tool Calling）

```
用户问题 ──► 大模型（带 tools 定义）
                │
                ├─ 决定调用 checkPrizeRecord(startDate, endDate)
                │      └─ Java 执行：查播放 → 查规则 → 计算应发 → 查实发 → 比对
                ├─ 拿到工具结果（结构化）
                └─ 生成自然语言回答
```

| 维度 | JSON 抽取 | Tool Calling |
| --- | --- | --- |
| 输出稳定性 | 依赖模型严格输出 JSON | 工具参数由框架按 schema 校验 |
| 多步能力 | ❌ | ✅ 最多 10 轮 |
| 扩展新能力 | 改 prompt + if-else | 加一个 `@Tool` 方法 |
| 调用次数 | 2 次 | 1 + N 次（N = 工具轮次） |
| 参数来源 | 全部由模型生成 | 敏感参数（`userId`）走 `ToolContext` |

#### 11.1.3 关键实现

```java
// 1. 声明工具：description 就是给大模型看的"什么时候调用"
@Tool(name = "checkPrizeRecord", description = "检查用户指定日期范围内的发奖是否正常……")
public PrizeCheckService.PrizeCheckResult checkPrizeRecord(
        @ToolParam(description = "检查开始日期，格式yyyy-MM-dd……") String startDate,
        @ToolParam(description = "检查结束日期……") String endDate,
        ToolContext toolContext) {          // 无 @ToolParam → 不来自大模型
    return prizeCheckService.check((Integer) toolContext.getContext().get("userId"), startDate, endDate);
}
```

```java
// 2. 注册工具 + 记忆 + 多轮 advisor
ChatClient chatClient = chatClientBuilder
        .defaultSystem("当前时间是：{currentTime}（今天是 {today}，{dayOfWeek}）……")
        .defaultOptions(OpenAiChatOptions.builder().parallelToolCalls(true))
        .defaultTools(aiAgentTool)
        .defaultAdvisors(memoryAdvisor, detailedToolCallingAdvisor)
        .build();
```

```java
// 3. 每次请求注入当前时间 + 会话隔离 + 工具上下文
chatClient.prompt()
        .system(s -> s.param("currentTime", now.format(...))
                      .param("today", now.format(...))
                      .param("dayOfWeek", ...))
        .user(userInput)
        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId + ":" + sessionId))
        .toolContext(Map.of("sessionId", sessionId, "userId", userId))
        .call().content();
```

#### 11.1.4 设计要点

- **`description` 就是 prompt**：工具描述写得越具体（"当用户询问发奖是否正常、奖励是否发对、为什么奖励变少/变多时调用"），模型选错工具的概率越低。
- **敏感参数不进模型**：`userId` 通过 `ToolContext` 由前端传入，模型无法伪造，避免越权查询。
- **时间由代码注入**：模型不知道"今天"是几号，每次请求把 `currentTime/today/dayOfWeek` 填进 system prompt，否则"查今天"会算错。
- **会话隔离**：`conversationId = userId + ":" + sessionId`，避免不同用户用相同 `sessionId` 导致上下文串线。
- **轮次上限**：`maxIterations = 10`，防止模型陷入"反复调工具"死循环；达到上限时返回提示而非抛异常。

---

### 11.2 发奖核对：为什么"计算"必须放在代码里

#### 11.2.1 问题

用户问"为什么我的奖励变少了"，需要回答：

1. 当天听了多久？
2. 按当前规则，每个阶段应发多少？
3. 实际发了多少？
4. 哪个阶段不一致？

如果让大模型自己算，会出现：

| 风险 | 说明 |
| --- | --- |
| 算错时长 | 把多条播放记录相加容易出错 |
| 算错阶段 | 规则是 JEXL 表达式，模型可能理解偏差 |
| 算错"应发" | 多阶段发奖是**逐秒累加、进入新阶段时按当时时长计算**，不是"最终时长一次性算" |
| 编造数据 | 模型可能"补全"不存在的记录 |

#### 11.2.2 实现：`PrizeCheckService`

```java
public PrizeCheckResult check(int userId, String startDateStr, String endDateStr) {
    // ① 参数校验：日期格式、未来日期截断到今天、最多 31 天
    // ② 查规则（PRIZE_STAGE_RULE / PRIZE_AMOUNT_RULE）
    // ③ 一次查出整个范围的发奖记录，按 dateStr 分组（只保留 PLAY_MUSIC 场景）
    // ④ 逐天：
    //      - 查当天播放明细，求和 totalDuration
    //      - calculateExpectedPrize(stageRule, amountRule, totalDuration) 计算应发
    //      - 从分组结果取当天实发，按 stage 累加
    //      - compare(expected, actual) 逐阶段比对
    // ⑤ 汇总 allMatched
}
```

**"应发"的计算为什么要逐秒重放**：

```java
static Map<Integer, Integer> calculateExpectedPrize(String stageRule, String amountRule, int totalDuration) {
    Map<Integer, Integer> expected = new TreeMap<>();
    for (int duration = 1; duration <= totalDuration; duration += 30) {   // 每 30 秒采样一次
        int stage = evaluate(stageExpression, duration);
        if (stage == 0 || expected.containsKey(stage)) continue;          // 已发过的阶段跳过
        int amount = evaluate(amountExpression, duration);
        if (amount != 0) expected.put(stage, amount);
    }
    return expected;
}
```

因为 `prize_sender` 的发奖逻辑是：**每次上报都重算 stage，进入新 stage 时按"当时的 totalDuration"计算 amount**。例如规则为 `>=600s → stage1/10份`、`>=1800s → stage2/20份`，用户最终听了 2000s：

- 正确应发：stage1 = 10（在 600s 时算）、stage2 = 20（在 1800s 时算）。
- 若错误地"用最终时长一次性算"：只会得到 stage2 = 20，**漏掉 stage1**。

因此必须**模拟时长增长过程**，在每个阶段首次出现时按当时时长计算数量。采样步长 30s 是精度与性能的折中（规则门槛通常是整百秒，30s 步长不会漏阶段）。

#### 11.2.3 比对逻辑

```java
static List<String> compare(Map<Integer, Integer> expected, Map<Integer, Integer> actual) {
    Set<Integer> stages = new TreeSet<>(expected.keySet());
    stages.addAll(actual.keySet());                       // 并集，覆盖"多发"的情况
    for (Integer stage : stages) {
        Integer e = expected.get(stage), a = actual.get(stage);
        if (e == null)      differences.add("第" + stage + "阶段多发：按规则不应发放，实际发放" + a + "份");
        else if (a == null) differences.add("第" + stage + "阶段漏发：预计发放" + e + "份，实际未发放");
        else if (!e.equals(a)) differences.add("第" + stage + "阶段数量不符：预计" + e + "，实际" + a);
    }
}
```

三类差异：**多发 / 漏发 / 数量不符**，覆盖了所有不一致情况。

#### 11.2.4 设计要点与改进

- **口径一致**：与 `prize_sender` 使用同一个 JEXL 引擎（`commons-jexl3`）和同一份规则配置，保证"应发"计算口径完全一致。
- **只核对 `PLAY_MUSIC` 场景**：其他场景（如活动补发）不参与比对，避免误报。
- **性能**：发奖记录一次查出整个范围再按天分组，避免 N 天 N 次查询；播放记录仍需按天查（每天一次）。
- **改进**：
  1. 采样步长 30s 在极端规则（如 `>=1s`）下可能不准，建议改为**直接求解规则门槛**（解析表达式中的常量）或按 1s 步长 + 提前终止。
  2. `MAX_DAYS = 31` 是硬编码，建议配置化。
  3. 跨服务调用失败时 `AiChatBotIntegration` 返回空列表，会被误判为"漏发"，建议区分"调用失败"与"确实无数据"。
  4. 可增加"规则版本"记录，避免规则变更后历史数据比对失真。

---

### 11.3 多轮工具调用：`DetailedToolCallingAdvisor`

#### 11.3.1 为什么需要自定义 Advisor

Spring AI 默认的 `ToolCallingAdvisor` 只执行**一轮**工具调用：模型返回 tool_calls → 执行 → 把结果回填 → 结束。但真实场景需要多轮：

```
第1轮：模型 → 调用 queryPlayRecord
第2轮：模型 → 看到播放记录，调用 querySystemConfig
第3轮：模型 → 看到规则，调用 queryPrizeRecord
第4轮：模型 → 生成最终回答
```

#### 11.3.2 实现要点

```java
for (int iteration = 1; iteration <= maxIterations; iteration++) {
    ChatClientResponse resp = chain.copy(this).nextCall(iterationRequest);
    printModelResponse(resp.chatResponse());                 // 打印模型输出/工具调用/参数

    if (!resp.chatResponse().hasToolCalls()) return resp;    // 没有工具调用 → 完成

    if (iteration == maxIterations) {                        // 达到上限 → 返回提示而非抛异常
        return resp.mutate().chatResponse(limitedResponse).build();
    }

    ToolExecutionResult result = toolCallingManager.executeToolCalls(prompt, resp.chatResponse());
    printToolResults(result);                                // 打印工具结果

    if (result.returnDirect()) { ... }                       // 工具要求直接返回
    instructions = result.conversationHistory();             // 回填历史，进入下一轮
}
```

#### 11.3.3 设计要点

- **可观测性**：每轮打印"模型输出 / 结束原因 / 调用的工具名与参数 / 工具返回结果"，排查"模型为什么没调工具""工具参数为什么错"时非常关键。
- **上限保护**：达到 `maxIterations` 时返回"已达到最大思考轮次上限"，而不是抛异常，保证接口可用。
- **`returnDirect`**：某些工具（如 `checkPrizeRecord`）结果已足够完整，可让模型直接返回，减少一轮调用。
- **改进**：日志中打印完整工具结果可能包含用户隐私数据，生产环境应脱敏或降级为 DEBUG。

---

### 11.4 会话上下文管理

#### 11.4.1 两套上下文管理器

| 类 | 用途 | system prompt |
| --- | --- | --- |
| `McpContextManager` | 旧链路（`McpChatBotService`）纯对话 | "你是一个专业问答机器人……" |
| `McpProfessionalContextManager` | 旧链路（`IntentRecognizeService`）意图抽取 | "你是业务查询参数抽取器，只输出JSON……" + 当前时间 |

新链路（`MusicAgent`）**不使用**这两个类，而是用 Spring AI 的 `MessageChatMemoryAdvisor` + `ChatMemory`。

#### 11.4.2 裁剪策略

```java
while (chatMessageList.size() > maxMessageSize) {
    chatMessageList.remove(1);   // 保留 index 0 的 system prompt，删除最早的一条
}
```

- `maxMessageSize = 16`，控制 token 成本。
- **保留 system prompt**：否则模型会"忘记身份"。
- **按条数裁剪而非 token 数**：实现简单，但长消息场景下可能仍超限，建议改为按 token 估算裁剪。

#### 11.4.3 隐患

| 隐患 | 说明 | 改进 |
| --- | --- | --- |
| **内存存储** | 多实例部署时，同一 `sessionId` 的请求可能落到不同实例，上下文丢失 | 改用 Redis（`pom.xml` 已引入依赖但未使用） |
| **无过期** | `sessionContextMap` 只增不减，长期运行内存泄漏 | 加 TTL 或定时清理 |
| **线程安全** | `ConcurrentHashMap` 只保证 Map 安全，`List` 的 `add/remove` 非线程安全 | 改用 `CopyOnWriteArrayList` 或加锁 |
| **两套实现重复** | 两个 ContextManager 代码几乎相同 | 抽取公共基类 |

---

## 12. 已知问题与改进建议

1. **敏感配置**：`openai.api-key` 依赖外部注入（正确），但数据库账号密码明文写在 `application.properties`（当前未使用，建议删除）。
2. **`application.properties` 中文乱码**：`# OpenAI??`、`# MCP???????token??` 等注释已乱码，建议统一 UTF-8 保存。
3. **`pom.xml` 重复插件执行**：`maven-compiler-plugin` 中 `compile`/`testCompile` 被声明两次，建议删除重复项。
4. **未使用的依赖**：`spring-boot-starter-data-redis`、Kotlin 相关依赖未使用，建议移除或明确用途。
5. **`AIAgentTool` 静态导入日志**：`import static reactor.netty.http.HttpConnectionLiveness.log;` 误用了 Reactor Netty 的 logger，应改为 Lombok `@Slf4j`。
6. **`AiChatBotIntegration` 吞异常返回空列表**：下游失败会被当成"无数据"，导致 `checkPrizeRecord` 误报"漏发"，建议抛出异常或返回带错误标记的结果。
7. **`PrizeCheckService` 采样步长 30s**：极端规则下可能漏阶段，建议按规则门槛精确求解。
8. **`MAX_DAYS = 31` 硬编码**：建议配置化。
9. **`PrizeCheckService` 每次编译 JEXL 表达式**：`createExpression` 在循环外已提取，但每次 `check` 仍会重新编译，建议按规则字符串缓存。
10. **`LocalDate.now()` 使用 JVM 默认时区**：与 `America/Los_Angeles` 不一致时"今天"会错，建议显式指定 `ZoneId`。
11. **会话上下文内存存储**：多实例/重启丢失，建议迁移到 Redis 并加 TTL。
12. **`McpContextManager` 列表非线程安全**：建议 `CopyOnWriteArrayList`。
13. **`OpenAiClientService` 每次新建 `OkHttpClient`**：连接池无法复用，建议改为单例 Bean。
14. **`OpenAiClientService` 返回 null 而非抛异常**：调用方需处处判空，建议抛业务异常。
15. **`IntentRecognizeService` 与 `MusicAgent` 两条链路并存**：`McpChatBotController` 中旧链路已注释，建议删除或明确保留原因。
16. **`DetailedToolCallingAdvisor` 日志打印完整工具结果**：可能泄露用户数据，建议脱敏。
17. **`@CrossOrigin` 全开放**：生产环境建议限制来源。
18. **无鉴权**：`userId` 由前端传入，任何人可冒充任意用户查询数据，建议接入登录态校验。

---

## 13. 测试

```bash
./mvnw test
```

当前仅包含 Spring Boot 默认的上下文加载测试（`AiChatbotApplicationTests`）。建议补充：

- `PrizeCheckService.calculateExpectedPrize` 的单元测试（多阶段、边界时长、规则为 0 的情况）；
- `PrizeCheckService.compare` 的单元测试（多发/漏发/数量不符）；
- `stripCodeFence` 的单元测试（带围栏、不带围栏、无换行）。
