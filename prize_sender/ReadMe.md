# prize_sender —— 播放时长统计与多阶段发奖服务

`prize_sender` 是 multiPrize 项目中的**发奖中台**：接收客户端上报的播放时长，按天累计用户总播放时长，再依据**可配置的规则**（JEXL 表达式）计算"发奖阶段"和"发奖数量"，调用奖品中心（`prize-center`）扣减奖品并落库发奖流水；同时通过**定时任务增量对账**校验发奖流水与奖品中心扣减记录是否一致。

---

## 1. 功能概览

| 模块 | 能力 | 关键实现 |
| --- | --- | --- |
| 播放上报 | 接收 `userId / musicId / duration / scene / prizeCode`，写入播放明细 | `SyncController` → `SyncServiceImpl.sync` |
| 时长统计 | 按天累计用户总播放时长（`play_record_statistic`） | 每次上报后重算当天总时长并 upsert |
| 规则计算 | 由配置中心下发 JEXL 表达式，动态计算阶段与数量 | `RuleUtil` + `system_config` 表 |
| 发奖 | 调用 `prize-center` 扣减奖品，写 `prize_record` 流水 | `CoinSenderIntegration` + `@Transactional` |
| 幂等 | 同一用户/场景/奖品/日期/阶段只发一次 | `out_biz_no` 唯一约束 + `DuplicateKeyException` 捕获 |
| 对账 | 每分钟比对"我方发奖流水"与"奖品中心扣减记录" | `CheckSchedule`（`@Scheduled`） |
| 配置管理 | 系统配置的增删改查 | `SystemConfigController` |
| 查询 | 播放明细、播放统计、发奖流水的查询与删除 | 4 个 Controller |

---

## 2. 技术栈

- **JDK 17**、**Spring Boot 4.0.7**（Web MVC）
- **Spring Cloud 2025.1.3**：Eureka Client（服务注册）+ OpenFeign（声明式调用 `prize-center`）
- **MyBatis 4.0.1**（XML Mapper）+ **MySQL**
- **Apache Commons JEXL 3.4.0**（动态规则表达式引擎）
- **Spring Scheduling**（`@EnableScheduling` 定时对账）
- **Lombok**

> `pom.xml` 中引入了 `spring-boot-starter-data-redis` 与 `spring-boot-starter-kafka`，但当前业务代码**并未使用**（见第 12.1 节说明为什么时长统计不用 Redis）。

---

## 3. 目录结构

```
prize_sender/
├── pom.xml
└── src/main/
    ├── java/com/example/prize_sender/
    │   ├── PrizeSenderApplication.java   # @EnableScheduling + @EnableFeignClients
    │   ├── constant/DemoConstant.java    # 配置项 code：PRIZE_AMOUNT_RULE / PRIZE_STAGE_RULE
    │   ├── controller/
    │   │   ├── SyncController            # POST /sync/sync 播放上报 + 发奖
    │   │   ├── PlayRecordController      # 播放明细查询/删除
    │   │   ├── PlayRecordStatisticController # 播放统计查询/删除
    │   │   ├── PrizeRecordController     # 发奖流水查询/删除
    │   │   ├── SystemConfigController    # 配置中心 CRUD
    │   │   ├── cmd/                      # SyncCmd / AddSystemConfigCmd / UpdateSystemConfigCmd
    │   │   ├── converter/                # Entity → VO
    │   │   └── Vo/                       # BaseVo / Single*Vo / Multi*Vo
    │   ├── entity/                       # PlayRecord / PlayRecordStatistic / PrizeRecord / SystemConfig
    │   ├── exception/                    # 4 个业务异常
    │   ├── integration/
    │   │   ├── CoinSenderIntegration     # 发奖（调用 prize-center /prize/decrease）
    │   │   ├── OrderRecordIntegration    # 对账（调用 prize-center /orderRecord/queryByTime）
    │   │   ├── client/PrizeCenterClient  # Feign 客户端
    │   │   └── vo/                       # OrderRecordVo / MultiOrderRecordVo
    │   ├── mapper/                       # 4 个 Mapper 接口
    │   ├── schedule/CheckSchedule        # 每分钟增量对账
    │   ├── service/ + service/impl/      # 5 个 Service
    │   └── util/                         # RuleUtil（JEXL）、DateTimeUtil
    └── resources/
        ├── application.properties        # 端口 8085、Eureka、数据源、时区
        ├── logback-spring.xml
        └── mapper/*.xml                  # 4 个 MyBatis 映射文件
```

---

## 4. 架构与数据流

```
客户端
  │ POST /sync/sync {userId, musicId, duration, scene, prizeCode}
  ▼
SyncController ──► SyncServiceImpl.sync()
                     │
                     ├─1─ playRecordService.add()            → MySQL play_record（明细）
                     ├─2─ 查当天全部明细并求和 totalDuration
                     ├─3─ play_record_statistic upsert       → MySQL（按天汇总）
                     ├─4─ systemConfigService.queryByCode()  → MySQL system_config（规则）
                     ├─5─ RuleUtil.calculatePrizeStage/Amount(JEXL)
                     └─6─ sendPrize() @Transactional
                            ├─ CoinSenderIntegration.send() ──Feign──► prize-center /prize/decrease
                            └─ prizeRecordMapper.add()      → MySQL prize_record（流水，out_biz_no 唯一）

CheckSchedule（每分钟）
  ├─ prizeRecordMapper.queryByTime(now-60s, now)          → 我方流水
  ├─ OrderRecordIntegration.queryOrderRecordListByTime()  → prize-center 扣减记录
  └─ 按 outBizNo 比对条数与金额，不一致则 error 日志告警
```

---

## 5. 环境依赖

| 组件 | 说明 | 默认值（见 `application.properties`） |
| --- | --- | --- |
| MySQL | 库名 `prize_sender`，表 `play_record`、`play_record_statistic`、`prize_record`、`system_config` | `107.22.136.67:3306` |
| Eureka | 服务注册中心，本服务名 `prize-sender` | `http://107.22.136.67:8761/eureka/` |
| prize-center | 奖品中心，Feign 按服务名调用 | 由 Eureka 解析 |
| 时区 | 数据源 `serverTimezone`、Jackson `time-zone` 必须一致 | `America/Los_Angeles` |

> ⚠️ `application.properties` 中数据库账号密码为明文，生产环境请改为环境变量/配置中心。

---

## 6. 快速开始

### 6.1 建表

```sql
CREATE DATABASE IF NOT EXISTS prize_sender DEFAULT CHARSET utf8mb4;
USE prize_sender;

-- 播放明细（高频写入，建议按月分表，见 12.1）
CREATE TABLE play_record (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id   INT NOT NULL,
  music_id  INT NOT NULL,
  duration  INT NOT NULL,
  sync_time DATETIME NOT NULL,
  KEY idx_user_time (user_id, sync_time)
);

-- 按天汇总（user_id + play_date 唯一）
CREATE TABLE play_record_statistic (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id        INT NOT NULL,
  total_duration INT NOT NULL DEFAULT 0,
  play_date      VARCHAR(16) NOT NULL,
  last_sync_time DATETIME,
  UNIQUE KEY uk_user_date (user_id, play_date)
);

-- 发奖流水（out_biz_no 唯一，幂等基石）
CREATE TABLE prize_record (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id    INT NOT NULL,
  scene      VARCHAR(64),
  code       VARCHAR(64),
  date_str   VARCHAR(16),
  stage      INT,
  amount     INT,
  prize_time DATETIME,
  out_biz_no VARCHAR(255) NOT NULL,
  UNIQUE KEY uk_out_biz_no (out_biz_no),
  KEY idx_time (prize_time),
  KEY idx_user_time (user_id, prize_time)
);

-- 配置中心
CREATE TABLE system_config (
  id    INT AUTO_INCREMENT PRIMARY KEY,
  code  VARCHAR(64) NOT NULL,
  value VARCHAR(1024) NOT NULL,
  UNIQUE KEY uk_code (code)
);

-- 初始化规则（JEXL 表达式，变量 totalDuration 单位为秒）
INSERT INTO system_config (code, value) VALUES
('PRIZE_STAGE_RULE',  'totalDuration >= 3600 ? 3 : (totalDuration >= 1800 ? 2 : (totalDuration >= 600 ? 1 : 0))'),
('PRIZE_AMOUNT_RULE', 'totalDuration >= 3600 ? 30 : (totalDuration >= 1800 ? 20 : (totalDuration >= 600 ? 10 : 0))');
```

### 6.2 启动

```bash
# 先启动 eureka-server 与 prize_center，再启动本服务
./mvnw spring-boot:run          # Linux / macOS
mvnw.cmd spring-boot:run        # Windows
```

服务端口 `8085`，注册名 `prize-sender`。

---

## 7. 接口说明

### 7.1 统一返回结构

`BaseVo`：

```json
{ "code": 200, "cost": 12, "success": true, "errorMessage": null }
```

单条查询返回 `Single*Vo`（含 `baseVo` + 业务对象），列表查询返回 `Multi*Vo`（含 `baseVo` + `xxxVoList`）。

### 7.2 播放上报 `/sync`

| 方法 | 路径 | 参数 | 说明 |
| --- | --- | --- | --- |
| POST | `/sync/sync` | `userId`, `musicId`, `duration`, `scene`, `prizeCode` | 上报播放时长，累计当天总时长并尝试发奖 |

`duration` 单位为**秒**；`scene` 为业务场景（如 `PLAY`），`prizeCode` 为奖品编码。

### 7.3 播放明细 `/playRecord`

| 方法 | 路径 | 参数 | 说明 |
| --- | --- | --- | --- |
| GET | `/playRecord/queryAll` | `userId`, `startTime`, `endTime`（`yyyy-MM-dd HH:mm:ss`，可选） | 查询用户播放明细 |
| GET | `/playRecord/{id}` | - | 按 ID 查询 |
| DELETE | `/playRecord/delete` | `id` | 删除 |

### 7.4 播放统计 `/playRecordStatistic`

| 方法 | 路径 | 参数 | 说明 |
| --- | --- | --- | --- |
| GET | `/playRecordStatistic/queryAll` | - | 查询全部统计 |
| GET | `/playRecordStatistic/{id}` | - | 按 ID 查询 |
| GET | `/playRecordStatistic/queryByUserIdAndPlayDate` | `userId`, `playDate`（`yyyy-MM-dd`） | 按用户+日期查询 |
| DELETE | `/playRecordStatistic/delete` | `id` | 删除 |

### 7.5 发奖流水 `/prizeRecord`

| 方法 | 路径 | 参数 | 说明 |
| --- | --- | --- | --- |
| GET | `/prizeRecord/queryAll` | `userId`, `startTime`, `endTime`, `startIndex`, `pageSize` | 分页查询发奖流水 |
| GET | `/prizeRecord/{id}` | - | 按 ID 查询 |
| DELETE | `/prizeRecord/delete` | `id` | 删除 |

### 7.6 配置中心 `/system-config`

| 方法 | 路径 | 参数 | 说明 |
| --- | --- | --- | --- |
| POST | `/system-config/add` | `code`, `value` | 新增配置 |
| PUT | `/system-config/modify` | `id`, `code`, `value` | 修改配置 |
| DELETE | `/system-config/delete` | `id` | 删除配置 |
| GET | `/system-config/id/{id}` | - | 按 ID 查询 |
| GET | `/system-config/code/{code}` | - | 按 code 查询 |
| GET | `/system-config/page/{start}/{size}` | - | 分页查询 |

---

## 8. 数据表与索引

| 表 | 用途 | 关键索引 |
| --- | --- | --- |
| `play_record` | 播放明细，每次上报一行 | `(user_id, sync_time)` |
| `play_record_statistic` | 用户按天总时长 | `UNIQUE(user_id, play_date)` |
| `prize_record` | 发奖流水 | `UNIQUE(out_biz_no)`、`(prize_time)`、`(user_id, prize_time)` |
| `system_config` | 规则配置 | `UNIQUE(code)` |

---

## 9. 对外依赖（Feign）

`PrizeCenterClient`（`@FeignClient(name = "prize-center")`）：

| 方法 | 路径 | 用途 |
| --- | --- | --- |
| `decrease(code, amount, outBizNo)` | `PUT /prize/decrease` | 扣减奖品库存，`outBizNo` 用于对方幂等 |
| `queryOrderRecordByTime(startTime, endTime)` | `GET /orderRecord/queryByTime` | 按时间段查询扣减记录，用于对账 |

---

## 10. 配置项

| code | 含义 | 示例值 |
| --- | --- | --- |
| `PRIZE_STAGE_RULE` | 由 `totalDuration` 计算发奖阶段（0 表示不发奖） | `totalDuration >= 3600 ? 3 : (totalDuration >= 1800 ? 2 : (totalDuration >= 600 ? 1 : 0))` |
| `PRIZE_AMOUNT_RULE` | 由 `totalDuration` 计算发奖数量 | `totalDuration >= 3600 ? 30 : (totalDuration >= 1800 ? 20 : (totalDuration >= 600 ? 10 : 0))` |

---

## 11. 前端页面

本服务为纯后端服务，无静态页面；管理端页面位于 `prize_center` 模块。

---

## 12. 重点功能设计

### 12.1 累计播放时长：为什么不用 Redis，而用 MySQL

#### 12.1.1 需求特征

- 每次播放上报都要**累加**当天总时长（写多）。
- 累计值要**长期保留**（用户历史、对账、客服查询、发奖追溯）。
- 累计值必须**准确**，它是发奖的唯一依据，丢一条就可能少发/多发奖品。

#### 12.1.2 为什么不用 Redis

| 维度 | Redis 方案 | 问题 |
| --- | --- | --- |
| **淘汰机制** | 内存有限，`maxmemory-policy` 一旦触发（`allkeys-lru` / `volatile-lru` / `allkeys-random`），**key 会被静默淘汰** | 用户当天累计时长可能被淘汰，重算时数据直接丢失，发奖依据失真 |
| **持久化** | RDB 是快照（可能丢最后一段时间），AOF `everysec` 最多丢 1 秒 | 对"金额相关"的数据，秒级丢失也不可接受 |
| **成本** | 全量用户 × 全量日期常驻内存，成本远高于磁盘 | 用户量 × 天数线性增长，内存扛不住 |
| **查询能力** | 只能按 key 点查，无法按时间范围/用户维度做 SQL 统计 | 对账、报表、客服排查都需要灵活查询 |
| **事务** | 无跨 key 事务，`INCRBY` 与流水落库无法原子 | 累计值与发奖流水可能不一致 |

> 结论：**累计时长是"账本型"数据，必须落盘、必须可查、必须不丢**，因此选择 MySQL。Redis 更适合"可重建、可丢失、可过期"的缓存/排行榜类数据（如 `music` 模块的排行榜）。

#### 12.1.3 用 MySQL 后如何解决"慢"

MySQL 的代价是**写入与查询都比 Redis 慢**，本项目通过三层手段控制：

**① 分表（水平拆分）**

`play_record` 是**只增不改、按时间天然有序**的明细表，是最典型的分表场景：

| 策略 | 说明 |
| --- | --- |
| 按时间分表 | `play_record_202610`、`play_record_202611`… 每月一张，单表行数可控，历史表可归档/冷存 |
| 按用户哈希分表 | `play_record_0..15`（`user_id % 16`），适合"按用户查"为主的场景 |
| 组合 | 先按月分，再按用户哈希，兼顾时间范围查询与用户查询 |

分表后单表数据量下降一个数量级，B+ 树层数变浅，写入与范围查询都更快；同时可把历史月份的表迁到独立库/只读实例。

**② 索引**

| 查询场景 | 索引 |
| --- | --- |
| 查某用户某天的明细（发奖主链路） | `(user_id, sync_time)` 联合索引，`WHERE user_id=? AND sync_time BETWEEN ? AND ?` 走索引范围扫描 |
| 对账按时间查流水 | `prize_record(prize_time)` |
| 用户查自己的发奖记录 | `prize_record(user_id, prize_time)` |
| 统计表按用户+日期 upsert | `UNIQUE(user_id, play_date)`，既保证唯一又提供索引 |

要点：**联合索引把 `user_id` 放最左**，因为主链路永远是"先定位用户，再限定时间"；避免在 `sync_time` 上做函数运算（如 `DATE(sync_time)=?`），否则索引失效。

**③ 仅返回所需字段（覆盖索引 / 减少回表）**

- 主链路只需要"时长求和"，不需要整行数据。理想写法是让数据库直接聚合：

  ```sql
  SELECT COALESCE(SUM(duration), 0)
  FROM play_record
  WHERE user_id = #{userId} AND sync_time >= #{start} AND sync_time < #{end}
  ```

  配合 `(user_id, sync_time, duration)` 覆盖索引，**无需回表**，一次索引扫描即可得到结果，避免把 N 行明细拉回 JVM 再循环累加（当前实现是 `queryAll` 后 Java 循环求和，数据量大时既费网络又费内存，建议改为上面的聚合 SQL）。
- 列表查询避免 `SELECT *`，只查 VO 需要的列（`id, user_id, music_id, duration, sync_time`），减少 IO 与反序列化开销。
- 分页查询必须带 `LIMIT`（`prize_record` 已实现 `limit #{startIndex}, #{pageSize}`），避免全量返回。

**④ 汇总表兜底**

`play_record_statistic` 保存"用户 + 日期 → 总时长"，查询当天/历史总时长时**直接读汇总表**（唯一索引点查，O(1)），不必每次扫明细。明细表只承担"可追溯"职责。

#### 12.1.4 小结

| 方案 | 数据安全 | 查询能力 | 成本 | 结论 |
| --- | --- | --- | --- | --- |
| Redis 累计 | 可能被淘汰/丢数据 | 弱 | 高（内存） | ❌ |
| MySQL 单表 | 安全 | 强 | 低 | 数据量大后慢 |
| **MySQL + 分表 + 索引 + 只取所需字段 + 汇总表** | 安全 | 强 | 低 | ✅ 采用 |

---

### 12.2 引入自定义配置中心，替代 if-else 硬编码

#### 12.2.1 硬编码的问题

如果直接在代码里写：

```java
int stage;
if (totalDuration >= 3600)      stage = 3;
else if (totalDuration >= 1800) stage = 2;
else if (totalDuration >= 600)  stage = 1;
else                            stage = 0;

int amount;
if (stage == 3)      amount = 30;
else if (stage == 2) amount = 20;
else if (stage == 1) amount = 10;
else                 amount = 0;
```

问题推演：

| 问题 | 说明 |
| --- | --- |
| 改规则要发版 | 运营想调整门槛（如 600s → 300s）或奖励数量，必须改代码、走测试、重新部署，响应慢 |
| 规则分散 | 阶段与数量两套 if-else 分散在不同方法，容易改漏、改错 |
| 无法灰度/回滚 | 新规则上线只能整体切换，出问题只能回滚版本 |
| 不可审计 | 谁在什么时候改了什么规则，代码里看不出来 |
| 扩展性差 | 新增场景（不同 `scene` 不同规则）会变成 if-else 套 if-else |

#### 12.2.2 配置中心方案

本项目用 `system_config` 表作为**轻量配置中心**，把规则以 **JEXL 表达式**的形式存起来：

```
system_config
┌────┬──────────────────┬──────────────────────────────────────────────┐
│ id │ code             │ value                                        │
├────┼──────────────────┼──────────────────────────────────────────────┤
│ 1  │ PRIZE_STAGE_RULE │ totalDuration >= 3600 ? 3 : (totalDuration >= 1800 ? 2 : (totalDuration >= 600 ? 1 : 0)) │
│ 2  │ PRIZE_AMOUNT_RULE│ totalDuration >= 3600 ? 30 : (totalDuration >= 1800 ? 20 : (totalDuration >= 600 ? 10 : 0)) │
└────┴──────────────────┴──────────────────────────────────────────────┘
```

运行时：

```java
String stageRule  = systemConfigService.queryByCode(DemoConstant.PRIZE_STAGE_RULE).getValue();
String amountRule = systemConfigService.queryByCode(DemoConstant.PRIZE_AMOUNT_RULE).getValue();
int stage  = RuleUtil.calculatePrizeStage(stageRule, totalDuration);
int amount = RuleUtil.calculatePrizeAmount(amountRule, totalDuration);
```

`RuleUtil` 用 JEXL 求值：

```java
JexlEngine jexlEngine = new JexlBuilder().create();
Map<String, Object> map = new HashMap<>();
map.put("totalDuration", totalDuration);
JexlExpression expression = jexlEngine.createExpression(rule);
return (int) expression.evaluate(new MapContext(map));
```

#### 12.2.3 收益对比

| 维度 | if-else 硬编码 | 配置中心 + JEXL |
| --- | --- | --- |
| 调整规则 | 改代码 + 发版 | 改一行配置，**无需发版** |
| 生效速度 | 分钟~小时级 | 下一次请求即生效 |
| 回滚 | 回滚版本 | 改回旧表达式 |
| 可审计 | 无 | 配置表可记录修改人与时间（建议加 `update_by/update_time`） |
| 多场景扩展 | 代码分支爆炸 | 按 `scene` 增加配置项即可 |
| 表达能力 | 受限于代码结构 | 支持三元、比较、算术、逻辑运算，可写复杂规则 |

#### 12.2.4 设计要点与改进

- **规则与代码解耦**：代码只负责"取规则 → 求值 → 用结果"，规则内容完全由运营维护。
- **表达式安全**：JEXL 只暴露 `totalDuration` 一个变量，且表达式由内部管理端写入，避免任意代码执行；如需对外开放，应使用 `JexlPermissions.RESTRICTED` 限制可调用的类与方法。
- **性能**：当前每次上报都 `createExpression` 并编译，建议缓存 `code → JexlExpression`（如 `ConcurrentHashMap`），配置变更时失效。
- **兜底**：`queryByCode` 返回 null 时会 NPE，建议配置缺失时使用默认规则并告警。
- **演进方向**：可替换为 Nacos / Apollo 等成熟配置中心，获得推送式刷新、灰度发布、版本历史等能力；当前 `system_config` 表方案胜在零额外组件、与业务库同事务。

---

### 12.3 多阶段发奖的幂等设计

#### 12.3.1 问题

用户播放时长会持续增长，可能依次跨过 600s、1800s、3600s 三个门槛，对应 stage 1/2/3。每次上报都会重新计算 stage，因此**同一阶段会被反复计算到**。若不做幂等：

- 用户在第 601s 上报时发 stage 1，第 602s 上报时又算出 stage 1 → **重复发奖**。
- 客户端网络重试、消息重复投递 → **重复发奖**。
- 并发上报（多端同时播放）→ 两个线程同时判断"未发过" → **重复发奖**。

#### 12.3.2 幂等键设计

```java
String outBizNo = scene + "_" + userId + "_" + prizeCode + "_" + dateStr + "_" + stage;
// 例：PLAY_1001_COIN_2026-10-23_2
```

幂等键包含 **场景 + 用户 + 奖品 + 日期 + 阶段** 五个维度：

| 维度 | 作用 |
| --- | --- |
| `scene` | 不同业务场景互不影响 |
| `userId` | 用户隔离 |
| `prizeCode` | 同一场景可发多种奖品 |
| `dateStr` | **按天重置**，今天发过 stage 2，明天还能再发 stage 2 |
| `stage` | 阶段隔离，stage 1 与 stage 2 是两笔独立奖励 |

#### 12.3.3 实现：唯一约束 + 事务 + 异常回滚

```java
@Transactional
public void sendPrize(int stage, SyncCmd syncCmd, int amount, String dateStr) {
    String outBizNo = syncCmd.getScene() + "_" + syncCmd.getUserId() + "_"
                    + syncCmd.getPrizeCode() + "_" + dateStr + "_" + stage;

    coinSenderIntegration.send(syncCmd.getPrizeCode(), amount, outBizNo);  // ① 调奖品中心（带 outBizNo）

    PrizeRecord prizeRecord = new PrizeRecord();
    prizeRecord.setOutBizNo(outBizNo);
    // ... 其余字段
    prizeRecordMapper.add(prizeRecord);                                    // ② 落库，out_biz_no 唯一
}
```

调用方：

```java
try {
    sendPrize(stage, syncCmd, amount, dateStr);
} catch (DuplicateKeyException e) {
    log.info("此阶段已发奖无需重复发放, stage={}", stage);   // ③ 幂等命中，正常返回
}
```

**为什么这样能保证幂等**：

1. **数据库唯一约束是最终防线**：`prize_record.out_biz_no` 建 `UNIQUE KEY`，同一 `outBizNo` 第二次插入必然抛 `DuplicateKeyException`。
2. **事务保证"发奖 + 落库"原子**：`sendPrize` 标注 `@Transactional`，若第 ② 步插入失败（唯一键冲突），事务回滚，**第 ① 步的扣减在业务语义上被视为未完成**，由对账任务补偿（见 12.4）。
3. **异常被捕获而非抛出**：`DuplicateKeyException` 被捕获后只记日志，接口仍返回成功——因为"已经发过了"对调用方来说就是成功，避免客户端因报错而无限重试。
4. **并发安全**：两个线程同时进入 `sendPrize`，都会先调奖品中心（对方也按 `outBizNo` 幂等），随后只有一个能成功插入，另一个被唯一键拦下，**不会产生两条流水**。

#### 12.3.4 时序

```
线程A / 线程B（并发上报，都算出 stage=2）
   │
   ├─► prize-center /prize/decrease(outBizNo=PLAY_1001_COIN_2026-10-23_2)
   │        └─ 对方按 outBizNo 幂等：第一次真正扣减，第二次直接返回成功
   │
   ├─► INSERT prize_record(out_biz_no=PLAY_1001_COIN_2026-10-23_2)
   │        ├─ 线程A：成功
   │        └─ 线程B：DuplicateKeyException → 事务回滚 → 捕获 → 记日志
   │
   └─► 最终：只有一条流水，只扣一次奖品
```

#### 12.3.5 设计要点与改进

- **幂等键必须由调用方生成并透传**：`outBizNo` 同时传给奖品中心，实现**跨系统幂等**（我方不重复扣，对方也不重复扣）。
- **唯一约束不可省略**：仅靠"先查后插"在并发下必然失效（查与插之间存在窗口）。
- **当前实现的隐患**：
  1. `CoinSenderIntegration.send` 内部 `catch(Exception e)` 只打日志**不抛出**，导致奖品中心调用失败时事务仍会提交，产生"有流水但没扣奖"的脏数据。建议改为抛出异常让事务回滚，或引入本地消息表/对账补偿。
  2. `sendPrize` 是同类内部调用（`this.sendPrize(...)`），**`@Transactional` 不生效**（Spring AOP 代理不拦截自调用）。应把 `sendPrize` 抽到独立 Bean，或通过 `AopContext.currentProxy()` 调用。
  3. 建议在 `prize_record` 上增加 `status`（`INIT/SUCCESS/FAIL`），先落 `INIT` 再调外部，成功后更新为 `SUCCESS`，形成"本地事务 + 状态机"的可靠发奖模型。
  4. 建议对 `DuplicateKeyException` 做精确判断（是否 `uk_out_biz_no` 冲突），避免其他唯一键冲突被误吞。

---

### 12.4 定时任务增量对账

#### 12.4.1 为什么需要对账

发奖链路跨两个系统（`prize_sender` 与 `prize-center`），存在多种不一致可能：

| 场景 | 结果 |
| --- | --- |
| 我方调扣减成功，落库失败（DB 抖动/唯一键冲突） | 对方扣了，我方无流水 |
| 我方落库成功，调扣减失败（网络超时但对方已处理） | 我方有流水，对方可能扣了也可能没扣 |
| 消息/请求重复 | 一方多一条 |
| 金额计算不一致（规则版本不同） | 条数相同但金额不同 |

这些情况无法靠单次请求的返回值完全判断，必须**事后比对**。

#### 12.4.2 增量对账实现

`CheckSchedule` 每分钟执行一次（`@Scheduled(cron = "0 */1 * * * ?")`）：

```java
Date startTime = new Date(System.currentTimeMillis() - 60_000L);  // 上一分钟
Date endTime   = new Date();                                      // 当前

List<PrizeRecord>  prizeRecordList = prizeRecordMapper.queryByTime(startTime, endTime);          // 我方
List<OrderRecordVo> orderRecordVos = orderRecordIntegration.queryOrderRecordListByTime(startTime, endTime); // 对方

// ① 条数比对
if (prizeRecordList.size() != orderRecordVos.size()) { log.error(...); }

// ② 按 outBizNo 逐笔比对金额
Map<String, Integer> prizeRecordMap = match1(prizeRecordList);   // outBizNo → amount
Map<String, Integer> orderRecordMap = match2(orderRecordVos);    // outBizNo → amount
for (String key : prizeRecordMap.keySet()) {
    if (!prizeRecordMap.get(key).equals(orderRecordMap.get(key))) { log.error(...); }
}
```

#### 12.4.3 为什么是"增量"而不是"全量"

| 维度 | 全量对账 | 增量对账（本项目） |
| --- | --- | --- |
| 扫描范围 | 全部历史流水 | 仅最近 1 分钟 |
| 单次耗时 | 随数据量线性增长，最终跑不完 | 恒定，秒级完成 |
| 对业务影响 | 大范围扫表，影响线上查询 | 走 `prize_time` 索引，影响极小 |
| 发现问题时效 | 天级（通常 T+1） | **分钟级** |
| 补偿窗口 | 发现问题时已过很久 | 发现问题时数据新鲜，便于立即补偿 |

增量对账的核心是**用时间窗口把"无限增长的数据"切成"固定大小的小块"**，每分钟只处理一小片，既保证时效又保证性能。历史数据可通过"日终全量对账"兜底（见改进）。

#### 12.4.4 设计要点与改进

- **时间窗口重叠**：当前窗口是 `[now-60s, now]`，相邻两次执行在边界上可能重叠或漏掉（任务执行耗时、时钟漂移）。建议改为**水位线（watermark）**机制：记录上次对账到的最大 `prize_time`，下次从该水位开始，并保留 1~2 分钟重叠窗口。
- **只告警不补偿**：当前仅 `log.error`，需要人工介入。建议增加：
  - 差异落库到 `reconcile_diff` 表，便于追踪与重放；
  - 自动补偿：我方有流水对方无 → 重发扣减；对方有扣减我方无 → 补写流水（需人工确认）。
- **比对逻辑缺陷**：`match1/match2` 用 `Map.put` 覆盖，同一 `outBizNo` 多条时只保留最后一条，且 `orderRecordMap.get(key)` 可能为 `null` 导致 NPE（`Integer` 拆箱）。建议改为 `Map<String, List<...>>` 或 `Map<String, Integer>` 累加金额，并用 `Objects.equals` 比较。
- **异常吞掉**：`OrderRecordIntegration` 在调用失败时返回空列表，会导致"对方 0 条"的误报。建议区分"调用失败"与"确实无数据"，失败时跳过本轮对账并告警。
- **分布式部署**：多实例部署时 `@Scheduled` 会在每个实例都执行，造成重复对账。建议加分布式锁（Redis `SETNX`）或使用 ShedLock。
- **日终全量兜底**：每天凌晨对前一天全量数据做一次对账，弥补增量窗口可能遗漏的数据。

---

## 13. 已知问题与改进建议

1. **敏感配置明文**：数据库账号密码写在 `application.properties`，建议迁移到环境变量/配置中心。
2. **`@Transactional` 自调用失效**：`SyncServiceImpl.sync` 内部调用 `this.sendPrize(...)`，事务注解不生效，建议拆分到独立 Bean。
3. **发奖失败被吞**：`CoinSenderIntegration.send` 捕获异常后不抛出，奖品中心失败时仍会写流水，建议抛出异常或引入本地消息表。
4. **`DateTimeUtil.getDateStr` 忽略入参**：方法内部 `format(new Date())` 而非 `format(date)`，导致对账查询的时间范围永远是"当前时间"，建议改为 `format(date)`。
5. **`PlayRecordServiceImpl.modify` 传错对象**：校验用的是 `record`，更新时却传 `playRecord`，建议统一。
6. **`PlayRecordController.queryById` 恒返回 500**：成功分支也构造了 `code=500, "音乐不存在"`，建议改为 200。
7. **`SyncController` 异常日志文案错误**：catch 中打印"删除播放统计记录失败"，建议改为"播放上报失败"。
8. **`RuleUtil` 每次编译表达式**：建议缓存 `JexlExpression`，并复用单例 `JexlEngine`。
9. **`system_config` 缺少唯一约束与审计字段**：建议 `UNIQUE(code)` + `update_by/update_time`。
10. **`play_record` 无分表**：数据量增长后主链路变慢，建议按月分表（见 12.1.3）。
11. **`queryAll` 使用 `SELECT *` 且无分页**：`play_record` 查询建议只取所需列并加 `LIMIT`。
12. **未使用的依赖**：`spring-boot-starter-data-redis`、`spring-boot-starter-kafka` 当前未使用，建议移除或明确后续用途。
13. **缺少全局异常处理**：各 Controller 重复 try-catch，建议使用 `@RestControllerAdvice`。

---

## 14. 测试

```bash
./mvnw test
```

当前仅包含 Spring Boot 默认的上下文加载测试（`PrizeSenderApplicationTests`）。
