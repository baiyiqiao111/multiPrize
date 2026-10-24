# prize_center —— 奖品中心（库存与扣减流水）

`prize_center` 是 multiPrize 项目中的**奖品中心**：维护奖品（`prize`）的库存，对外提供**扣减库存**接口（`PUT /prize/decrease`），每次扣减同时写入一条**扣减流水**（`order_record`），并以 `outBizNo` 作为幂等键；同时提供奖品与流水的管理/查询接口，以及两个静态管理页面（`prize.html`、`orderRecord.html`）。

它是 `prize_sender` 的下游：`prize_sender` 发奖时调用本服务扣减，`prize_sender` 的定时对账任务再调用本服务的 `/orderRecord/queryByTime` 拉取扣减记录做比对。

---

## 1. 功能概览

| 模块 | 能力 | 关键实现 |
| --- | --- | --- |
| 奖品管理 | 新增、修改（库存/图标）、删除、分页查询、按 ID 查询 | `PrizeController` / `PrizeServiceImpl` |
| 库存扣减 | 按 `code` 扣减 `amount`，库存不足拒绝 | `PUT /prize/decrease`，`@Transactional` |
| 扣减流水 | 每次扣减写一条 `order_record`，`out_biz_no` 唯一 | `OrderRecordMapper.add` |
| 幂等 | 同一 `outBizNo` 重复扣减直接返回成功 | `DuplicateKeyException` 捕获 + 唯一约束 |
| 流水查询 | 按 ID、按 `outBizNo`、按时间段分页、按时间段全量 | `OrderRecordController` |
| 管理页面 | 奖品管理、扣减流水查看 | `static/prize.html`、`static/orderRecord.html` |

---

## 2. 技术栈

- **JDK 17**、**Spring Boot 4.0.7**（Web MVC）
- **Spring Cloud 2025.1.3**：Eureka Client（注册为 `prize-center`，供 Feign 调用）
- **MyBatis 4.0.1**（XML Mapper）+ **MySQL**
- **Lombok**

> `pom.xml` 中引入了 `spring-boot-starter-data-redis` 与 `spring-boot-starter-kafka`，当前代码**未使用**；`mybatis-spring-boot-starter` 被重复声明了两次（见第 13 章）。

---

## 3. 目录结构

```
prize_center/
├── pom.xml
└── src/main/
    ├── java/com/example/prize_center/
    │   ├── PrizeCenterApplication.java
    │   ├── controller/
    │   │   ├── PrizeController          # /prize：add / modify / delete / all / id / decrease
    │   │   ├── OrderRecordController    # /orderRecord：all / queryByTime / id / outBizNo / delete
    │   │   ├── cmd/                     # AddPrizeCmd / ModifyPrizeCmd / AddOrderRecordCmd / ModifyOrderRecordCmd
    │   │   ├── converter/               # Entity → VO
    │   │   └── vo/                      # BaseVo / Single*Vo / Multi*Vo / PrizeVo / OrderRecordVo
    │   ├── entity/                      # Prize / OrderRecord
    │   ├── exception/                   # PrizeNotExistException / PrizeStockNotEnoughException / OrderRecordNotExistException
    │   ├── mapper/                      # PrizeMapper / OrderRecordMapper
    │   └── service/ + service/Impl/     # PrizeService / OrderRecordService
    └── resources/
        ├── application.properties       # 端口 8082、Eureka、数据源、时区
        ├── logback-spring.xml
        ├── mapper/*.xml                 # PrizeMapper.xml / OrderRecordMapper.xml
        └── static/                      # prize.html / orderRecord.html / css / js
```

---

## 4. 架构与数据流

```
prize_sender ──Feign(prize-center)──► PUT /prize/decrease?code&amount&outBizNo
                                          │
                                          ▼
                                   PrizeServiceImpl.decrease()  @Transactional
                                          ├─1─ queryByCode(code)          → 奖品不存在 → PrizeNotExistException
                                          ├─2─ stock < amount ?           → 库存不足 → PrizeStockNotEnoughException
                                          ├─3─ UPDATE prize SET stock = stock - amount
                                          └─4─ INSERT order_record(out_biz_no, code, amount, prize_time)
                                                   └─ out_biz_no 唯一 → 重复扣减抛 DuplicateKeyException
                                                        └─ Controller 捕获 → 返回 success=true（幂等）

prize_sender 对账任务 ──Feign──► GET /orderRecord/queryByTime?startTime&endTime
                                          └─ SELECT ... WHERE prize_time BETWEEN ? AND ?
```

---

## 5. 环境依赖

| 组件 | 说明 | 默认值（见 `application.properties`） |
| --- | --- | --- |
| MySQL | 库名 `prize_center`，表 `prize`、`order_record` | `107.22.136.67:3306` |
| Eureka | 注册中心，本服务名 `prize-center` | `http://107.22.136.67:8761/eureka/` |
| 时区 | `serverTimezone` 与 `spring.jackson.time-zone` 必须一致 | `America/Los_Angeles` |

> ⚠️ 数据库账号密码为明文，生产环境请改为环境变量/配置中心。

---

## 6. 快速开始

### 6.1 建表

```sql
CREATE DATABASE IF NOT EXISTS prize_center DEFAULT CHARSET utf8mb4;
USE prize_center;

-- 奖品（库存）
CREATE TABLE prize (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  code        VARCHAR(64) NOT NULL,
  stock       INT NOT NULL DEFAULT 0,
  icon        VARCHAR(512),
  update_time DATETIME,
  UNIQUE KEY uk_code (code)          -- 按 code 扣减，必须唯一
);

-- 扣减流水（幂等基石）
CREATE TABLE order_record (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  out_biz_no VARCHAR(255) NOT NULL,
  code       VARCHAR(64) NOT NULL,
  amount     INT NOT NULL,
  prize_time DATETIME NOT NULL,
  UNIQUE KEY uk_out_biz_no (out_biz_no),
  KEY idx_time (prize_time),
  KEY idx_code_time (code, prize_time)
);

-- 初始化一个奖品
INSERT INTO prize (code, stock, icon, update_time) VALUES ('COIN', 1000000, null, now());
```

### 6.2 启动

```bash
# 先启动 eureka-server
./mvnw spring-boot:run          # Linux / macOS
mvnw.cmd spring-boot:run        # Windows
```

服务端口 `8082`，注册名 `prize-center`。管理页面：`http://localhost:8082/prize.html`、`http://localhost:8082/orderRecord.html`。

---

## 7. 接口说明

### 7.1 统一返回结构

`BaseVo`：

```json
{ "code": 200, "cost": 12, "success": true, "errorMessage": null }
```

单条查询返回 `Single*Vo`（`baseVo` + 业务对象），列表查询返回 `Multi*Vo`（`baseVo` + `xxxVoList`）。

### 7.2 奖品 `/prize`

| 方法 | 路径 | 参数 | 说明 |
| --- | --- | --- | --- |
| POST | `/prize/add` | Body: `{code, stock, icon}` | 新增奖品 |
| PUT | `/prize/modify` | Body: `{id, stock, icon, code}` | 修改奖品（库存/图标） |
| DELETE | `/prize/delete` | `id` | 删除奖品 |
| GET | `/prize/all` | `startIndex`, `size` | 分页查询奖品 |
| GET | `/prize/id` | `id` | 按 ID 查询 |
| PUT | `/prize/decrease` | `code`, `amount`, `outBizNo` | **扣减库存**（幂等），供 `prize_sender` 调用 |

`decrease` 的返回约定：

| 情况 | 返回 |
| --- | --- |
| 扣减成功 | `code=200, success=true` |
| `outBizNo` 已存在（重复扣减） | `code=200, success=true`（幂等，视为成功） |
| 奖品不存在 / 库存不足 / 其他异常 | `code=500, success=false` |

### 7.3 扣减流水 `/orderRecord`

| 方法 | 路径 | 参数 | 说明 |
| --- | --- | --- | --- |
| GET | `/orderRecord/all` | `statTime`, `endTime`（`yyyy-MM-dd'T'HH:mm:ss`）, `startIndex`, `size` | 按时间段分页查询 |
| GET | `/orderRecord/queryByTime` | `startTime`, `endTime`（`yyyy-MM-dd'T'HH:mm:ss`） | 按时间段全量查询（**对账用**） |
| GET | `/orderRecord/id` | `id` | 按 ID 查询 |
| GET | `/orderRecord/outBizNo` | `outBizNo` | 按幂等键查询 |
| DELETE | `/orderRecord/delete` | `id` | 删除流水 |

---

## 8. 数据表与索引

| 表 | 用途 | 关键索引 |
| --- | --- | --- |
| `prize` | 奖品与库存 | `UNIQUE(code)`（扣减按 code 定位） |
| `order_record` | 扣减流水 | `UNIQUE(out_biz_no)`（幂等）、`(prize_time)`（对账按时间扫）、`(code, prize_time)` |

---

## 9. 对外提供（被 prize_sender 调用）

| 调用方 | 接口 | 用途 |
| --- | --- | --- |
| `prize_sender` 发奖 | `PUT /prize/decrease` | 扣减库存 + 写流水 |
| `prize_sender` 对账 | `GET /orderRecord/queryByTime` | 拉取时间段内扣减记录 |

---

## 10. 配置项

| 配置 | 值 | 说明 |
| --- | --- | --- |
| `server.port` | `8082` | 独立端口（music=8080、prize_sender=8085、AI-chatbot=8088、eureka=8761） |
| `spring.application.name` | `prize-center` | 服务名，不能含下划线 |
| `eureka.instance.ip-address` | `${EUREKA_INSTANCE_IP:107.22.136.67}` | 公网 IP 注册 |
| `spring.jackson.time-zone` | `America/Los_Angeles` | 与数据源时区一致 |

---

## 11. 前端页面

| 页面 | 说明 |
| --- | --- |
| `static/prize.html` | 奖品管理：列表、新增、修改库存/图标、删除 |
| `static/orderRecord.html` | 扣减流水：按时间段查询、按 `outBizNo` 查询 |
| `static/css/qqmusic.css`、`static/js/shell.js` | 公共样式与脚本 |

---

## 12. 重点功能设计

### 12.1 库存扣减：为什么用"读-判断-写"而不是 Redis 预扣

#### 12.1.1 需求特征

- 扣减必须**不超卖**：库存 10 时并发 100 个请求，最多只能成功 10 个。
- 扣减必须**可追溯**：每一笔扣减都要有流水，能按 `outBizNo` 反查。
- 扣减必须**幂等**：上游重试不能重复扣。
- 库存量级不大（奖品种类少、单奖品库存万级），但**并发写集中在少数几行**。

#### 12.1.2 方案对比

| 方案 | 不超卖 | 可追溯 | 复杂度 | 问题 |
| --- | --- | --- | --- | --- |
| 先查库存再更新（无锁） | ❌ | ✅ | 低 | 并发下"查-改"之间有窗口，必然超卖 |
| `synchronized` / 单机锁 | ✅（单机） | ✅ | 低 | 多实例部署失效；锁粒度粗，吞吐低 |
| Redis 预扣（`DECR`） | ✅ | ❌ | 中 | 库存与流水分离，Redis 丢数据即账实不符；需额外对账回补 |
| **MySQL 事务 + 条件更新（本项目）** | ✅ | ✅ | 低 | 高并发下行锁竞争，需控制事务粒度 |

本项目选择 **MySQL 事务**，因为奖品库存是**账本型数据**（与 `prize_sender` 的播放时长同理）：不能丢、必须可查、必须与流水同事务。

#### 12.1.3 当前实现

```java
@Transactional
public void decrease(String code, int amount, String outBizNo) {
    Prize prize = prizeMapper.queryByCode(code);          // ① 查奖品
    if (prize == null) throw new PrizeNotExistException("奖品不存在");
    int stock = prize.getStock();
    if (stock < amount) throw new PrizeStockNotEnoughException("库存不足");  // ② 判断
    prize.setStock(stock - amount);
    prizeMapper.modify(prize);                            // ③ 更新库存
    OrderRecord orderRecord = new OrderRecord();          // ④ 写流水
    orderRecord.setOutBizNo(outBizNo);
    orderRecord.setAmount(amount);
    orderRecord.setCode(code);
    orderRecordMapper.add(orderRecord);
}
```

**为什么能保证不超卖**：`@Transactional` 下，`UPDATE prize SET stock = ? WHERE id = ?` 会对该行加**排他行锁**，直到事务提交。并发请求在 ③ 处串行化，后到的事务读到的是已提交的最新库存，因此不会出现"两个事务都读到 10 都扣成 9"。

**为什么能保证账实一致**：库存更新与流水插入在**同一事务**，要么都成功，要么都回滚，不会出现"扣了库存没流水"或"有流水没扣库存"。

#### 12.1.4 隐患与改进

| 隐患 | 说明 | 改进 |
| --- | --- | --- |
| **读-判断-写非原子** | ① 的 `SELECT` 是普通读（快照读），③ 的 `UPDATE` 才加锁。若两个事务都读到 stock=10，都判断通过，则 ③ 会依次执行成 9、8——**不会超卖但会"多扣"**（实际库存 10，扣了 2 次各 1，剩 8，这是正确的；但若 amount 不同则可能扣成负数） | 改为**条件更新**：`UPDATE prize SET stock = stock - #{amount} WHERE code = #{code} AND stock >= #{amount}`，用 `affectedRows == 0` 判断失败，一条 SQL 完成"判断 + 扣减"，天然原子 |
| **行锁持有时间长** | 事务内包含 `SELECT` + `UPDATE` + `INSERT`，锁持有时间 = 整个事务 | 缩短事务：先做无锁校验，再在短事务内做条件更新 + 写流水 |
| **热点行竞争** | 单个奖品（如 `COIN`）被所有用户扣减，行锁排队 | 库存分桶（`prize_stock_0..N`，按 `userId % N` 路由），或 Redis 预扣 + 异步落库（需接受最终一致） |
| **`modify` 覆盖全字段** | `PrizeMapper.modify` 会同时更新 `code/stock/icon`，若并发修改图标可能被覆盖 | 拆出专用的 `decreaseStock` 语句，只更新 `stock` |
| **无库存下限保护** | 条件更新缺失时可能扣成负数 | 加 `CHECK (stock >= 0)` 或条件更新 |

---

### 12.2 扣减幂等：唯一约束 + 事务回滚

#### 12.2.1 问题

上游 `prize_sender` 在以下情况会重复调用 `decrease`：

- 网络超时后重试（第一次其实已成功）。
- 上游事务回滚后重放。
- 上游并发上报同一阶段。

若不做幂等，同一笔奖励会被扣两次库存。

#### 12.2.2 实现

```java
// PrizeController
@PutMapping("/decrease")
public BaseVo decrease(String code, int amount, String outBizNo) {
    try {
        prizeService.decrease(code, amount, outBizNo);
        return new BaseVo(200, cost, true, null);
    } catch (DuplicateKeyException e) {
        return new BaseVo(200, cost, true, null);   // 幂等命中，返回成功
    } catch (Exception e) {
        return new BaseVo(500, cost, false, "其它位置异常");
    }
}
```

关键点：

1. **`order_record.out_biz_no` 唯一约束**是幂等的最终防线。
2. 重复请求执行到 ④ 时插入失败抛 `DuplicateKeyException`，**整个事务回滚**——③ 的库存扣减被撤销，库存不会被多扣。
3. Controller 捕获该异常并返回 `success=true`，让上游认为"已处理"，避免无限重试。

#### 12.2.3 时序

```
请求A（首次）                          请求B（重试，同一 outBizNo）
  │ ① SELECT prize (stock=100)           │ ① SELECT prize (stock=100)
  │ ② 100 >= 10 ✅                       │ ② 100 >= 10 ✅
  │ ③ UPDATE stock = 90  ← 行锁          │ ③ UPDATE stock = 90  ← 等待行锁
  │ ④ INSERT order_record(outBizNo) ✅   │ ④ INSERT order_record(outBizNo) ❌ DuplicateKey
  │ COMMIT                               │ ROLLBACK（③ 的扣减被撤销）
  │                                      │ Controller 捕获 → 返回 success=true
  ▼                                      ▼
最终库存 90，流水 1 条
```

#### 12.2.4 隐患与改进

| 隐患 | 说明 | 改进 |
| --- | --- | --- |
| **先扣库存后撞唯一键** | 重复请求会先执行 `UPDATE`（拿锁、改数据），再在 `INSERT` 时失败回滚，**白白消耗一次行锁与回滚开销** | 先 `INSERT order_record`（唯一键快速失败），再扣库存；或先 `SELECT ... WHERE out_biz_no = ?` 做快速判断（仍需唯一约束兜底） |
| **`DuplicateKeyException` 被泛化捕获** | 任何唯一键冲突（如 `prize.code`）都会被当成"幂等命中"返回成功 | 判断异常信息是否包含 `uk_out_biz_no` |
| **`amount <= 0` 未校验** | 传负数会"加库存" | 入口校验 `amount > 0` |
| **`outBizNo` 为空** | 空串也能插入一次，第二次才冲突 | 入口校验非空 |
| **上游失败语义** | 上游 `CoinSenderIntegration` 吞异常，可能"我方扣了、上游没记流水" | 依赖 `prize_sender` 的增量对账补偿（见 `prize_sender` README 12.4） |

---

### 12.3 扣减流水与对账支撑

#### 12.3.1 流水表设计

`order_record` 只记录**扣减事实**，不记录余额：

| 字段 | 说明 |
| --- | --- |
| `out_biz_no` | 上游幂等键，唯一 |
| `code` | 奖品编码 |
| `amount` | 扣减数量 |
| `prize_time` | 扣减时间（`now()`） |

设计要点：

- **只增不改**：流水是事实记录，不允许修改（`modify` 接口存在但业务上不应使用）。
- **不存余额**：余额由 `prize.stock` 维护，流水用于审计与对账，避免双写不一致。
- **`prize_time` 索引**：对账按时间窗口查询（`WHERE prize_time BETWEEN ? AND ?`），必须有索引，否则每分钟对账会全表扫描。

#### 12.3.2 对账链路

```
prize_sender.CheckSchedule（每分钟）
   ├─ 我方 prize_record（outBizNo → amount）
   └─ 本服务 GET /orderRecord/queryByTime(start, end)（outBizNo → amount）
        └─ 逐笔比对：条数 + 金额
```

因此本服务的 `/orderRecord/queryByTime` 必须：

- **返回全量**（不分页），否则条数比对必然不一致；
- **时间边界包含**（`BETWEEN` 是闭区间），与上游窗口对齐；
- **时间格式统一**（`yyyy-MM-dd'T'HH:mm:ss`），且时区与上游一致（`America/Los_Angeles`）。

#### 12.3.3 改进方向

1. **流水表分表**：`order_record` 与 `play_record` 一样是只增表，量大后按月分表。
2. **增加 `status` 字段**：区分 `SUCCESS` / `ROLLBACK`，便于排查"扣了又回滚"的记录。
3. **增加 `biz_type` / `operator`**：区分不同上游（`prize_sender`、人工调整、活动补发）。
4. **禁止删除流水**：`DELETE /orderRecord/delete` 会破坏对账，建议改为逻辑删除或仅限管理员。
5. **提供汇总接口**：`GET /orderRecord/sum?start&end` 直接返回 `SUM(amount) GROUP BY code`，减少对账时的数据传输。

---

### 12.4 服务注册与跨服务调用

#### 12.4.1 为什么用 Eureka + Feign

| 方案 | 问题 |
| --- | --- |
| 硬编码 IP:PORT | 实例扩容/迁移需改配置并重启 |
| Nginx 反向代理 | 需额外维护，且无法感知实例健康 |
| **Eureka + Feign（本项目）** | 服务名寻址、自动负载均衡、实例上下线自动感知 |

#### 12.4.2 关键配置

```properties
spring.application.name=prize-center          # 服务名，不能含下划线（否则不是合法主机名）
eureka.client.service-url.defaultZone=${EUREKA_SERVER_URL:http://107.22.136.67:8761/eureka/}
eureka.instance.prefer-ip-address=true
eureka.instance.ip-address=${EUREKA_INSTANCE_IP:107.22.136.67}   # EC2 只有内网 IP，需上报公网 IP
```

上游声明：

```java
@FeignClient(name = "prize-center")
public interface PrizeCenterClient { ... }
```

#### 12.4.3 设计要点与改进

- **服务名规范**：`prize-center` 用中划线，避免下划线导致主机名非法。
- **IP 注册**：云主机网卡只有内网 IP，必须显式上报公网 IP，否则跨机器调用失败。
- **超时与重试**：Feign 默认超时较短，扣减接口应配置合理超时（如 3s）并**禁止自动重试**（重试会放大重复扣减，虽然幂等能兜住，但会浪费资源）。
- **熔断降级**：建议接入 Resilience4j，奖品中心不可用时快速失败，避免上游线程堆积。
- **安全**：当前接口无鉴权，任何能访问 `8082` 的服务都能扣库存，建议加内网隔离或签名校验。

---

## 13. 已知问题与改进建议

1. **敏感配置明文**：数据库账号密码写在 `application.properties`，建议迁移到环境变量/配置中心。
2. **`pom.xml` 重复依赖**：`mybatis-spring-boot-starter` 声明了两次（一次带版本、一次 `compile`），建议删除重复项。
3. **未使用的依赖**：`spring-boot-starter-data-redis`、`spring-boot-starter-kafka` 未使用，建议移除。
4. **`prize.code` 无唯一约束**：`queryByCode` 若返回多条会抛 `TooManyResultsException`，建议加 `UNIQUE(code)`。
5. **`decrease` 读-判断-写非原子**：建议改为 `UPDATE ... SET stock = stock - #{amount} WHERE code = #{code} AND stock >= #{amount}` 条件更新。
6. **`PrizeMapper.modify` 覆盖全字段**：扣减库存时会连带覆盖 `code/icon`，建议拆出专用 `decreaseStock`。
7. **`amount <= 0` 未校验**：负数会加库存，建议入口校验。
8. **`DuplicateKeyException` 泛化捕获**：任何唯一键冲突都返回成功，建议精确判断 `uk_out_biz_no`。
9. **`OrderRecordServiceImpl.modify` 判空对象错误**：`if (orderRecord == null)` 应为 `if (orderRecordModify == null)`；且 `setCode(orderRecord.getCode())` 是自赋值，无实际作用。
10. **`OrderRecordMapper.modify` SQL 语法错误**：`amount = #{amount}` 后缺少逗号（`prize_time = now()` 前），执行会报错。
11. **`OrderRecordController.queryAll` 参数名拼写**：`statTime` 应为 `startTime`，易与前端约定不一致。
12. **`PrizeController.queryById` 不存在时返回 500**：建议改为 404。
13. **`BaseVo` setter 拼写**：`setErrorMassage` 与字段 `errorMessage` 不一致，Jackson 序列化依赖 getter 尚可，但建议统一。
14. **缺少全局异常处理**：各 Controller 重复 try-catch，建议使用 `@RestControllerAdvice`。
15. **流水可被删除**：`DELETE /orderRecord/delete` 会破坏对账，建议限制或改为逻辑删除。

---

## 14. 测试

```bash
./mvnw test
```

当前仅包含 Spring Boot 默认的上下文加载测试（`PrizeCenterApplicationTests`）。
