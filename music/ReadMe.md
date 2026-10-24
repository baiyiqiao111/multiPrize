# music —— 音乐平台服务

`music` 是 multiPrize 项目中的音乐业务模块，基于 Spring Boot 4 + MyBatis + Redis + Kafka + MinIO 构建，提供**用户注册/激活/登录、音乐管理、评论与回复、点赞、排行榜、站内通知、文件上传、上新邮件推荐**等能力，并自带一套静态页面（`src/main/resources/static`）可直接在浏览器中使用。

---

## 1. 功能概览

| 模块 | 能力 | 关键实现 |
| --- | --- | --- |
| 用户 | 注册、邮箱激活码激活、登录、资料修改、兴趣标签维护 | `UserController` / `UserServiceImpl`，激活码存 Redis（10 分钟过期），邮件通过 QQ SMTP 发送 |
| 音乐 | 新增、修改、删除、分页查询、按标题/ID 查询 | `MusicController` / `MusicServiceImpl`，新增后写入 Redis 排行榜并发 Kafka 消息 |
| 评论 | 发表评论/回复、修改、删除（级联删除子回复）、按音乐查询 | `CommentController` / `CommentServiceImpl`，回复时生成站内通知 |
| 点赞 | 点赞/取消点赞、查询用户点赞列表、查询音乐被点赞用户 | `LikeController` / `LikeServiceImpl`，数据存 Redis List，同时更新排行榜分数 |
| 排行榜 | 取 TopN 热门音乐 | `MusicRankController`，Redis ZSet `music_rank`，点赞 +1、评论 +2 |
| 通知 | 查询某用户的站内通知、删除通知 | `NotificationController`，MySQL `notification` 表 |
| 文件 | 音频/封面上传到 MinIO，返回可访问 URL | `FileController` / `MinioUtil`，启动时自动建桶并设置匿名只读策略 |
| 推荐 | 新歌按标签匹配兴趣用户，发送 HTML 推荐邮件 | Kafka 消费者 `AddMusicConsumer` → `TagServiceImpl.recommend` |

---

## 2. 技术栈

- **JDK 17**、**Spring Boot 4.0.6**（Web MVC）
- **MyBatis 4.0.1**（XML Mapper）+ **MySQL**
- **Redis**（Spring Data Redis，`RedisTemplate` + JSON 序列化）
- **Kafka**（Spring Kafka，生产者/消费者，字符串消息）
- **MinIO 8.5.9**（对象存储，音频与封面）
- **Spring Mail**（QQ 邮箱 SMTP，SSL 465）
- **Lombok**、**Jackson**

---

## 3. 目录结构

```
music/
├── pom.xml
└── src/main/
    ├── java/com/example/music/
    │   ├── MusicApplication.java        # 启动类
    │   ├── common/Result.java           # 通用返回体
    │   ├── config/                      # Kafka / Redis / MinIO 配置
    │   ├── constant/                    # 用户状态枚举、邮件模板、积分常量
    │   ├── consumer/AddMusicConsumer    # 新歌推荐消费者
    │   ├── controller/                  # 7 个 REST 控制器
    │   │   ├── cmd/                     # 入参对象（AddMusicCmd 等）
    │   │   ├── converter/               # Entity → VO 转换
    │   │   └── vo/                      # 出参对象（BaseVo / MusicVo 等）
    │   ├── entity/                      # Music / User / Comment / Notification / UserToken
    │   ├── exception/                   # 业务异常
    │   ├── integration/EmailUtil        # 邮件发送
    │   ├── mapper/                      # MyBatis Mapper 接口
    │   ├── producer/                    # Kafka 生产者
    │   ├── repository/                  # Redis 数据访问（排行榜/标签/点赞/激活码/Token）
    │   ├── service/ + service/impl/     # 业务逻辑
    │   └── util/                        # MinioUtil、ActivateCodeUtil
    └── resources/
        ├── application.properties       # 数据源、Kafka、邮件、MinIO 配置
        ├── logback-spring.xml
        ├── mapper/*.xml                 # 4 个 MyBatis 映射文件
        └── static/                      # 前端页面（index/login/register/detail/rank/...）
```

---

## 4. 架构与数据流

```
浏览器(static 页面)
      │ HTTP
      ▼
Controller ──► Service ──► Mapper ──► MySQL
                 │  │
                 │  ├──► Redis（排行榜 ZSet / 点赞 List / 标签 Set / 激活码 / Token）
                 │  ├──► MinIO（音频、封面）
                 │  ├──► JavaMail（激活邮件、上新推荐邮件）
                 │  └──► Kafka Producer ──► topic: addMusic_topic
                 │                                   │
                 │                                   ▼
                 └──────────────  AddMusicConsumer（按标签匹配用户 → 发推荐邮件）
```

**关键流程**

1. **注册 → 激活**：`POST /user/register` 写入用户（状态 `INIT`）→ 生成 6 位激活码写入 Redis（`active_code:{userId}`，10 分钟）→ 发送激活邮件 → 前端 `PUT /user/activate` 校验激活码，成功后状态改为 `ACTIVE` 并删除激活码。
2. **新增音乐**：`POST /music/add` 校验标题唯一 → 入库 → 写入 Redis 排行榜（初始分 0）→ 发送 Kafka 消息 `addMusic_topic` → 消费者按 `tags` 从 Redis 标签集合中找出兴趣用户 → 逐个发送 HTML 推荐邮件。
3. **点赞/评论影响排行**：点赞 `+1`、评论 `+2`（`DemoConstant.LIKE_SCORE` / `COMMENT_SCORE`），通过 `ZSet.incrementScore` 实时更新，`GET /music-rank/topN` 取 TopN。

---

## 5. 环境依赖

| 组件 | 说明 | 默认地址（见 `application.properties`） |
| --- | --- | --- |
| MySQL | 库名 `music`，需包含 `music`、`user`、`comment`、`notification` 表 | `107.22.136.67:3306` |
| Redis | 排行榜、点赞、标签、激活码、登录 Token | Spring Boot 默认 `localhost:6379` |
| Kafka | topic `addMusic_topic`，消费组 `music-group` | `107.22.136.67:9092` |
| MinIO | 桶 `music`，启动时自动创建并设为匿名只读 | `http://107.22.136.67:9000` |
| QQ 邮箱 SMTP | 发送激活码与推荐邮件 | `smtp.qq.com:465`（SSL） |

> ⚠️ 仓库中的 `application.properties` 含有数据库账号、邮箱授权码、MinIO 密钥等明文配置，正式环境请改为环境变量或配置中心，并**及时更换已泄露的凭据**。

---

## 6. 快速开始

### 6.1 准备数据库

```sql
CREATE DATABASE IF NOT EXISTS music DEFAULT CHARSET utf8mb4;

USE music;

CREATE TABLE music (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  title        VARCHAR(255) NOT NULL,
  content      TEXT,
  tags         VARCHAR(255),
  publish_time DATETIME,
  picture_url  VARCHAR(512),
  sound_url    VARCHAR(512),
  author       VARCHAR(128)
);

CREATE TABLE user (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(64) NOT NULL UNIQUE,
  password      VARCHAR(128) NOT NULL,
  email         VARCHAR(128),
  interests     VARCHAR(512),
  age           INT,
  gender        VARCHAR(16),
  register_time DATETIME,
  status        VARCHAR(16)
);

CREATE TABLE comment (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  user_id      INT NOT NULL,
  music_id     INT NOT NULL,
  parent_id    INT DEFAULT 0,
  comment      TEXT,
  publish_time DATETIME,
  KEY idx_music (music_id),
  KEY idx_parent (parent_id)
);

CREATE TABLE notification (
  id             INT AUTO_INCREMENT PRIMARY KEY,
  from_id        INT NOT NULL,
  to_id          INT NOT NULL,
  content_type   VARCHAR(32),
  content_id     INT,
  operation      VARCHAR(32),
  operation_time DATETIME,
  KEY idx_to (to_id)
);
```

### 6.2 修改配置

按实际环境修改 `src/main/resources/application.properties` 中的 `spring.datasource.*`、`spring.kafka.bootstrap-servers`、`spring.mail.*`、`minio.*`。

### 6.3 启动

```bash
# 在 music 目录下
./mvnw spring-boot:run          # Linux / macOS
mvnw.cmd spring-boot:run        # Windows

# 或打包运行
./mvnw clean package -DskipTests
java -jar target/music-0.0.1-SNAPSHOT.jar
```

启动后访问 `http://localhost:8080/index.html` 进入首页（静态页面位于 `static/`）。

---

## 7. 接口说明

### 7.1 统一返回结构

所有接口返回 `BaseVo`（或继承/包含它的 VO）：

```json
{
  "code": 200,
  "cost": 12,
  "success": true,
  "errorMessage": null
}
```

- `code`：`200` 成功，`500` 业务/系统异常
- `cost`：服务端处理耗时（毫秒）
- `success`：是否成功
- `errorMessage`：失败原因

列表类接口额外返回 `xxxVoList`，单条查询额外返回 `xxxVo`。

### 7.2 用户 `/user`

| 方法 | 路径 | 参数 | 说明 |
| --- | --- | --- | --- |
| POST | `/user/register` | Body: `RegisterCmd` | 注册，返回 `userId`，同时发送激活邮件 |
| PUT | `/user/activate` | `userId`, `inputCode` | 校验激活码并激活账号 |
| POST | `/user/activate/code` | `userId` | 重新发送激活码（10 分钟有效） |
| GET | `/user/activate/state` | `name` | 按用户名换取 `userId` 与激活状态 |
| POST | `/user/login` | `name`, `password` | 登录，Token 写入 Redis（`user_token:{userId}`，1 分钟） |
| PUT | `/user/modify` | Body: `ModifyUserCmd` | 修改用户资料 |
| PUT | `/user/interest` | `userId`, `interests` | 更新兴趣标签（逗号分隔），同步 Redis 标签集合 |
| DELETE | `/user/delete` | `id` | 删除用户 |
| GET | `/user/all` | - | 查询全部用户 |
| GET | `/user/name` | `name` | 模糊查询用户 |
| GET | `/user/id` | `id` | 按 ID 查询用户 |

`RegisterCmd` 字段：`name`、`password`、`email`、`interests`（逗号分隔）、`age`、`gender`。

### 7.3 音乐 `/music`

| 方法 | 路径 | 参数 | 说明 |
| --- | --- | --- | --- |
| POST | `/music/add` | Body: `AddMusicCmd` | 新增音乐（标题唯一），触发排行榜初始化与 Kafka 推荐 |
| PUT | `/music/modify` | Body: `ModifyMusicCmd` | 修改音乐 |
| DELETE | `/music/delete` | `id` | 删除音乐（同时删除其评论与排行榜记录） |
| GET | `/music/all` | `pageNum`(默认1), `pageSize`(默认10) | 分页查询，按发布时间倒序 |
| GET | `/music/title` | `title` | 按标题模糊查询（返回单条） |
| GET | `/music/id` | `id` | 按 ID 查询 |

`AddMusicCmd` 字段：`title`、`content`、`tags`（逗号分隔）、`pictureUrl`、`soundUrl`、`author`。

### 7.4 评论 `/comment`

| 方法 | 路径 | 参数 | 说明 |
| --- | --- | --- | --- |
| POST | `/comment/add` | Body: `AddCommentCmd` | 发表评论；`parentId>0` 时为回复，并给被回复者生成通知 |
| PUT | `/comment/modify` | `comment`, `id` | 修改评论内容 |
| DELETE | `/comment/delete` | `id` | 删除评论及其所有子回复 |
| GET | `/comment/by-music` | `musicId` | 查询某音乐的全部评论 |
| GET | `/comment/id` | `id` | 按 ID 查询评论 |

`AddCommentCmd` 字段：`userId`、`musicId`、`parentId`（顶级评论传 0）、`comment`。

### 7.5 点赞 `/like`

| 方法 | 路径 | 参数 | 说明 |
| --- | --- | --- | --- |
| POST | `/like/add` | `userId`, `musicId` | 点赞（排行榜 +1） |
| DELETE | `/like/delete` | `userId`, `musicId` | 取消点赞（排行榜 -1） |
| GET | `/like/user` | `userId` | 查询用户点赞的音乐列表 |
| GET | `/like/music` | `musicId` | 查询点赞该音乐的用户列表 |

### 7.6 排行榜 `/music-rank`

| 方法 | 路径 | 参数 | 说明 |
| --- | --- | --- | --- |
| GET | `/music-rank/topN` | `n` | 返回热度前 N 的音乐 |

### 7.7 通知 `/notification`

| 方法 | 路径 | 参数 | 说明 |
| --- | --- | --- | --- |
| GET | `/notification/all` | `toId`, `pageNum`(默认1), `pageSize`(默认10) | 分页查询某用户收到的通知 |
| DELETE | `/notification/delete` | `id` | 删除通知 |

### 7.8 文件 `/file`

| 方法 | 路径 | 参数 | 说明 |
| --- | --- | --- | --- |
| POST | `/file/upload` | `multipart/form-data` 字段 `file` | 上传音频/封面到 MinIO，返回 `url` |

单文件上限 50MB（`spring.servlet.multipart.max-file-size`）。

---

## 8. Redis 键设计

| Key | 类型 | 用途 | 过期 |
| --- | --- | --- | --- |
| `music_rank` | ZSet | 音乐热度排行（member=musicId, score=热度） | 无 |
| `user-like:{userId}` | List | 用户点赞过的音乐 ID | 无 |
| `music-like:{musicId}` | List | 点赞该音乐的用户 ID | 无 |
| `recommend_tag:{tag}` | Set | 关注某标签的用户 ID 集合 | 无 |
| `active_code:{userId}` | String | 邮箱激活码 | 10 分钟 |
| `user_token:{userId}` | String(JSON) | 登录 Token（`UserToken`） | 1 分钟 |

---

## 9. Kafka 说明

| 项 | 值 |
| --- | --- |
| Topic | `addMusic_topic` |
| 消费组 | `music-group` |
| 消息体 | 新增音乐的 `id`（字符串） |
| 生产者 | `AddMusicProducer`（`MusicServiceImpl.add` 中调用） |
| 消费者 | `AddMusicConsumer` → `TagService.recommend` 按标签匹配用户并发送推荐邮件 |

---

## 10. 邮件说明

- 发件人固定为 `1445079235@qq.com`（`EmailUtil`），需在 `application.properties` 配置 QQ 邮箱授权码。
- **激活邮件**：模板见 `DemoConstant.EMAIL_CONTENT`，其中 `123456` 会被替换为真实激活码。
- **上新推荐邮件**：`TagServiceImpl.buildContent` 生成 HTML，包含封面、标题、作者、标签、简介与详情页链接（`app.base-url` + `/detail.html?id={musicId}`，默认 `http://107.22.136.67:8080`）。

---

## 11. 前端页面

`src/main/resources/static/` 下为纯静态页面，由 Spring Boot 直接托管：

| 页面 | 说明 |
| --- | --- |
| `index.html` | 首页 / 音乐列表 |
| `login.html` / `register.html` | 登录、注册 |
| `activate.html` | 邮箱激活码激活 |
| `interest.html` | 兴趣标签选择 |
| `detail.html` | 音乐详情（播放、评论、点赞） |
| `rank.html` | 排行榜 |
| `notification.html` | 站内通知 |
| `music.html` | 音乐管理 |
| `css/qqmusic.css`、`js/shell.js` | 公共样式与脚本 |

---

## 12. 重点功能设计

### 12.1 用户注册与激活方案

#### 12.1.1 方案选型

| 方案 | 优点 | 缺点 | 结论 |
| --- | --- | --- | --- |
| 注册即生效（无激活） | 实现最简单 | 无法校验邮箱真实性，垃圾账号、刷接口成本低 | 不采用 |
| 邮件链接激活（token 放 URL） | 体验好，一次点击完成 | 链接可能被邮件网关预取/安全扫描器提前访问导致误激活；链接泄露即等于激活 | 不采用 |
| **邮件验证码激活（本项目）** | 需用户主动输入，能证明邮箱可达；验证码短时效、一次性；实现简单 | 需用户手动输入 | ✅ 采用 |
| 短信验证码 | 触达率高 | 需要短信服务商，有成本 | 不采用 |

选型要点：**验证码不落库、只存 Redis 并设置 TTL**，天然具备"过期即失效"能力，避免在 MySQL 中维护激活码表与清理任务。

#### 12.1.2 验证码生成

`ActivateCodeUtil.generate()`：

```java
Random random = new Random();
int value = random.nextInt(900000) + 100000;   // [100000, 999999]
return String.valueOf(value);
```

- 生成 **6 位数字**（首位不为 0），共 90 万种组合，配合 10 分钟有效期与"一次性使用"，暴力枚举不可行。
- 生成后立即写入 Redis：`active_code:{userId}` → code，`EX 10min`（`ActiveCodeRepository.set`）。
- 同时用 `DemoConstant.EMAIL_CONTENT.replace("123456", activeCode)` 渲染 HTML 邮件并发送。

> 改进建议：`java.util.Random` 是可预测的伪随机数，安全场景应改用 `SecureRandom`；同时可增加"同一用户 60 秒内只能发送一次"的限流键，防止邮件轰炸。

#### 12.1.3 验证码比对

`UserServiceImpl.activate(userId, inputCode)` 的判定顺序：

```
1. 查用户 → 不存在 → UserNotExistException
2. 状态已是 ACTIVE → 直接返回（幂等，避免重复激活报错）
3. 从 Redis 取 active_code:{userId}
   ├─ 为 null → "激活码已过期，请重新获取"（TTL 到期或从未发送）
   └─ 与 inputCode 不等 → "验证码不匹配，激活失败"
4. 校验通过 → UPDATE user SET status = 'ACTIVE'
5. 删除 Redis 激活码（一次性，防重放）
```

设计要点：

- **以 userId 为 key**：激活码与用户一一绑定，用户 A 的验证码无法激活用户 B。
- **先判状态再判码**：已激活用户重复提交直接成功返回，避免前端重试时报错。
- **比对成功后立即删除**：即使验证码在 TTL 内被截获，也无法二次使用。
- **过期与不匹配区分提示**：前端可据此决定是"重新发送"还是"重新输入"。
- **重发接口** `POST /user/activate/code`：已激活用户会被拒绝（`IllegalArgumentException`），未激活用户重新生成并覆盖旧码（旧码随即失效）。
- **激活页换取 userId**：`GET /user/activate/state?name=xxx` 用用户名换 `userId` 与 `status`，因为激活页通常只有用户名。

#### 12.1.4 时序

```
前端                      UserController            UserServiceImpl        Redis        QQ SMTP
 │ POST /user/register ────────►│                          │                  │             │
 │                              │ register(cmd) ──────────►│ 查重/入库(INIT)   │             │
 │                              │                          │ set active_code ─►│             │
 │                              │                          │ sendMail ────────────────────►│
 │◄──── userId ─────────────────│                          │                  │             │
 │ PUT /user/activate ─────────►│ activate(userId,code) ──►│ get active_code ─►│             │
 │                              │                          │ 比对 → updateStatus(ACTIVE)    │
 │                              │                          │ delete active_code►│            │
 │◄──── success ────────────────│                          │                  │             │
```

---

### 12.2 基于 interest / tag 的异步上新推荐

#### 12.2.1 业务目标

用户注册或修改兴趣时填写 `interests`（如 `"流行,摇滚,民谣"`）；管理员新增音乐时填写 `tags`（如 `"摇滚,独立"`）。当新歌上线时，**找出所有兴趣标签与该歌标签有交集的用户，给他们发一封推荐邮件**。

#### 12.2.2 为什么引入 Set 缓存 interest → userId 的映射

**朴素方案（不缓存）**：音乐被添加时，**先 for 循环这首歌的所有标签，再 for 循环所有用户，判断当前标签是否在该用户的 `interests` 里**：

```java
List<User> allUsers = userMapper.selectAll();          // 全量用户
for (String tag : music.getTags().split(",")) {        // 外层：音乐的每个标签
    for (User user : allUsers) {                       // 内层：所有用户
        if (user.getInterests() != null
                && Arrays.asList(user.getInterests().split(",")).contains(tag)) {
            matchedUsers.add(user);                    // 命中，加入待发邮件列表
        }
    }
}
```

问题推演：

| 问题 | 说明 |
| --- | --- |
| 双重循环 O(k × N) | k 为标签数、N 为用户数，每次上新都要把全部用户拉进内存遍历一遍，用户量上万后 CPU 与内存开销都很可观 |
| 全量加载用户 | `selectAll()` 把整张用户表读进 JVM，用户表越大越慢，且每次上新都重复一次 |
| 字符串拆分开销 | 内层循环里对每个用户反复 `split(",")` 解析 `interests`，产生大量临时对象 |
| 与主库耦合 | 推荐是"旁路"能力，不应把压力压到业务主库上 |
| 无法复用 | 匹配逻辑每次上新都从头算，用户兴趣的"倒排关系"没有被沉淀下来 |

**引入 Redis Set 方案**：维护**倒排索引** `recommend_tag:{tag}` → `Set<userId>`。

- 用户注册 / 修改兴趣时（`UserServiceImpl.register` / `selectInterests`）计算差集：
  - `shouldAdd`：新标签 - 旧标签 → `SADD recommend_tag:{tag} userId`
  - `shouldRemoved`：旧标签 - 新标签 → `SREM recommend_tag:{tag} userId`
  - 只做增量更新，**O(变更标签数)**，而不是全量重建。
- 上新时只需对每个 tag 执行 `SMEMBERS recommend_tag:{tag}`，再在内存中做并集（`HashSet.addAll`），**O(标签数 × 集合大小)**，且完全不碰 MySQL。

对比总结：

| 维度 | 无缓存（SQL 匹配） | Redis Set 倒排索引 |
| --- | --- | --- |
| 上新时查询复杂度 | 全表扫描 O(N) | O(k × 平均集合大小)，且为内存操作 |
| 索引可用性 | 逗号字符串无法索引 | Set 天然按 tag 分桶 |
| 写入成本 | 0 | 兴趣变更时增量 SADD/SREM，成本极低 |
| 主库压力 | 高 | 0（仅最后按 userId 批量查用户信息） |
| 数据一致性 | 强一致 | 最终一致（Redis 与 MySQL 可能短暂不一致，可接受） |

> 为什么用 **Set** 而不是 List/ZSet：同一用户对同一标签只需存在一次，Set 天然去重；且 `SADD`/`SREM` 幂等，重复注册/重复修改不会产生脏数据。

#### 12.2.3 为什么异步推荐

**同步方案**：`POST /music/add` 中直接遍历匹配用户并逐个发邮件。

问题推演：

1. **接口耗时不可控**：一封 SMTP 邮件通常 200ms~2s，命中 1000 个用户就是几分钟，HTTP 请求必然超时。
2. **失败放大**：某个邮箱地址错误或 SMTP 抖动，会导致整个新增音乐接口失败——**推荐是附加能力，不应影响主业务**。
3. **无法削峰**：批量导入音乐时会瞬间产生大量邮件，SMTP 服务商有限流，同步发送会被拒。
4. **不可重试**：同步链路失败后没有补偿机制。

**异步方案（Kafka）**：

```
MusicServiceImpl.add()
   ├─ 入库（主业务，同步，必须成功）
   ├─ ZADD music_rank（排行榜初始化）
   └─ kafkaTemplate.send("addMusic_topic", musicId)   ← 只发一条消息，毫秒级返回
                │
                ▼
AddMusicConsumer.listen(message)
   ├─ 按 id 查音乐
   └─ TagService.recommend(music)
         ├─ 拆分 tags → 逐个 SMEMBERS recommend_tag:{tag} → 并集得到 userIds
         ├─ userMapper.queryByIds(userIds) 批量查用户
         └─ 逐个 sendMail（单个失败仅记录日志，不影响其他用户）
```

收益：

| 维度 | 同步 | 异步（Kafka） |
| --- | --- | --- |
| 接口响应 | 秒级~分钟级 | 毫秒级（仅入库 + 发消息） |
| 故障隔离 | 邮件失败 → 新增失败 | 邮件失败 → 仅日志，主流程成功 |
| 削峰 | 无 | Kafka 缓冲，消费者按能力消费 |
| 可扩展 | 单机串行 | 可增加消费者实例/分区并行 |
| 可重试 | 无 | 消费失败可重试（配合 `ack-mode=batch`） |

#### 12.2.4 当前实现的注意点

- 消费者按 `musicId` 重新查库，而不是把整个音乐对象塞进消息，**消息体小、避免脏数据**。
- 单个用户邮件发送失败被 `try-catch` 吞掉并记录日志，保证"一个失败不影响一批"。
- 邮件正文做了 HTML 转义（`safe()`），防止标题/简介中的特殊字符破坏邮件结构。
- 详情页链接由 `app.base-url` 拼接，便于不同环境切换域名。
- 改进方向：消费端可改为批量发送 + 线程池并发；对已发送记录做幂等（如 `musicId + userId` 去重键），避免重复消费导致重复邮件。

---

### 12.3 排行榜整体方案

#### 12.3.1 需求

按"热度"对音乐排序，支持取 TopN。热度由用户行为累积：**点赞 +1、评论 +2**（`DemoConstant.LIKE_SCORE` / `COMMENT_SCORE`）。

#### 12.3.2 方案对比

| 方案 | 说明 | 问题 |
| --- | --- | --- |
| MySQL `ORDER BY like_count + comment_count*2` | 实时聚合 | 每次查询都要聚合/排序，数据量大时慢；点赞评论高频写会锁表 |
| MySQL 冗余 `score` 字段 + 索引 | 写时更新分数 | 高频 `UPDATE` 同一行会产生行锁竞争；TopN 仍需排序 |
| **Redis ZSet（本项目）** | `ZINCRBY` 原子累加，`ZREVRANGE` 直接取 TopN | 需保证与 MySQL 的最终一致 |

选择 ZSet 的理由：排行榜是**典型的"高频写分数 + 低频读 TopN"**场景，ZSet 底层是跳表，`ZINCRBY` O(log N)、`ZREVRANGE 0 n-1` O(log N + n)，天然按分数有序，无需额外排序。

#### 12.3.3 实现细节

`MusicRankRepository`（key = `music_rank`）：

| 操作 | Redis 命令 | 触发点 |
| --- | --- | --- |
| 新歌入榜 | `ZADD music_rank 0 musicId` | `MusicServiceImpl.add` |
| 加分 | `ZINCRBY music_rank delta musicId` | 点赞 `+1`、评论 `+2` |
| 减分 | `ZINCRBY music_rank -delta musicId` | 取消点赞 `-1` |
| 下榜 | `ZREM music_rank musicId` | `MusicServiceImpl.delete` |
| 取 TopN | `ZREVRANGE music_rank 0 n-1` | `GET /music-rank/topN` |

查询链路：`MusicRankServiceImpl.getTopN(n)` → 先取 TopN 的 musicId 列表 → `musicMapper.queryByIds(ids)` 批量查详情。`MusicMapper.xml` 中 `queryByIds` 使用 `ORDER BY FIELD(id, ...)`，**保证返回顺序与 Redis 排名一致**（否则 MySQL 的 IN 查询不保证顺序，排行榜会乱序）。

#### 12.3.4 设计要点与改进

- **分数与业务解耦**：分数只存在 Redis，MySQL 不冗余热度字段，避免双写一致性问题。
- **删除音乐时同步下榜**，避免 TopN 中出现已删除的 id（`queryByIds` 查不到会少一条）。
- **冷启动**：Redis 重启后 `music_rank` 丢失，TopN 为空。改进方案：定时任务从 MySQL 的点赞/评论统计表重建 ZSet，或使用 Redis AOF/RDB 持久化。
- **防刷**：当前点赞用 List 存储，可重复点赞导致分数虚高，建议改为 Set 去重或加"用户-音乐"唯一约束。
- **时间衰减**：当前为累计分，老歌会长期霸榜；如需"近期热门"，可改为按时间窗口分桶（如 `music_rank:2026-10`）或引入衰减因子。

---

### 12.4 评论区整体方案

#### 12.4.1 数据模型

`comment` 表采用**邻接表（parent_id）**模型：

| 字段 | 说明 |
| --- | --- |
| `id` | 评论 ID |
| `user_id` | 评论人 |
| `music_id` | 所属音乐 |
| `parent_id` | 父评论 ID，**0 表示顶级评论** |
| `comment` | 内容 |
| `publish_time` | 发布时间 |

选择邻接表而非嵌套集/路径枚举：评论深度浅（通常 2 层：评论 + 回复），邻接表写入简单、无需维护额外字段。

#### 12.4.2 核心能力

| 能力 | 实现 |
| --- | --- |
| 发表评论 | `CommentServiceImpl.add`：插入评论 → 排行榜 `+2` → 若 `parentId != 0` 则给父评论作者生成通知 |
| 查询某歌评论 | `queryByMusicId`：`WHERE music_id = ? ORDER BY publish_time DESC`（最新在前） |
| 查询回复 | `queryByParentId`：`WHERE parent_id = ? ORDER BY publish_time ASC`（对话顺序） |
| 修改评论 | 先查存在性（不存在抛 `CommentNotExistException`），再更新内容 |
| 删除评论 | **BFS 级联删除**：用队列从目标评论开始，逐层查出子回复并入队，全部删除 |

#### 12.4.3 级联删除为什么用 BFS

```java
Queue<Integer> q = new LinkedList<>();
q.offer(id);
while (!q.isEmpty()) {
    int poll = q.poll();
    commentMapper.delete(poll);
    for (Comment c : commentMapper.queryByParentId(poll)) {
        q.offer(c.getId());
    }
}
```

- 评论树可能有多层（回复的回复），用队列逐层展开可覆盖任意深度。
- 相比递归，BFS 不会因深度过大导致栈溢出。
- 相比数据库 `ON DELETE CASCADE`，应用层控制更灵活（可同时做通知清理、积分调整）。

#### 12.4.4 设计要点与改进

- **回复即通知**：`add` 中当 `parentId != 0` 时，构造 `Notification(fromId=评论人, toId=父评论作者, contentType="COMMENT", contentId=父评论ID, operation="APPLY")`，把"评论"与"通知"解耦为两个服务。
- **索引**：`music_id`、`parent_id` 均建索引，保证按歌查询与按父查子都是索引扫描。
---

### 12.5 系统通知整体方案

#### 12.5.1 数据模型

`notification` 表：

| 字段 | 说明 |
| --- | --- |
| `from_id` | 触发者（如回复者） |
| `to_id` | 接收者（如被回复者） |
| `content_type` | 关联内容类型，当前为 `COMMENT` |
| `content_id` | 关联内容 ID（如父评论 ID） |
| `operation` | 操作类型，当前为 `APPLY`（回复） |
| `operation_time` | 操作时间 |

设计为**通用事件表**：`content_type + content_id + operation` 三元组描述"谁对什么做了什么"，后续可扩展 `LIKE`（点赞）、`SYSTEM`（系统公告）等类型而无需改表结构。

#### 12.5.2 产生与消费

```
CommentServiceImpl.add()
   └─ parentId != 0
        └─ NotificationService.add(notification)   ← 写入 MySQL
                    │
                    ▼
GET /notification/all?toId=xxx&pageNum=1&pageSize=10
   └─ NotificationMapper.queryAll(toId, start, size)
        WHERE to_id = ? ORDER BY operation_time DESC LIMIT ?, ?
```

- **产生**：目前仅"回复评论"会生成通知，由 `CommentServiceImpl` 主动调用 `NotificationService.add`。
- **查询**：按接收者 `to_id` 分页，按时间倒序，`idx_to` 索引保证高效。
- **删除**：`DELETE /notification/delete?id=` 单条删除（用户手动清理已读/无用通知）。

#### 12.5.3 设计要点

- **写扩散 vs 读扩散**：本项目采用**写扩散**（事件发生时立即为接收者写一条记录），优点是查询简单（`WHERE to_id = ?` 直接分页），缺点是粉丝量大的场景写入放大。当前是"回复评论"这种点对点场景，写扩散完全合适。
- **与业务解耦**：通知表不存评论正文，只存 `content_id`，前端点击通知时再按 `content_type` 跳转到对应详情页（如 `/detail.html?id={musicId}`），避免内容修改后通知内容不一致。
- **失败不影响主流程**：通知写入在评论插入之后，即使通知失败也不应回滚评论（当前未加事务，天然满足；若加事务需注意隔离）。
- **分页**：`start = (pageNum - 1) * pageSize` 在 Controller 中计算，Mapper 使用 `LIMIT #{start}, #{size}`。

#### 12.5.4 改进方向

1. **未读状态**：当前无 `is_read` 字段，无法展示红点/未读数。建议增加 `is_read TINYINT DEFAULT 0`，并提供 `PUT /notification/read` 批量已读接口。
2. **通知类型扩展**：`content_type` 可扩展为 `COMMENT` / `LIKE` / `SYSTEM`，`operation` 可扩展为 `APPLY` / `CANCEL` / `ANNOUNCE`，前端按类型渲染不同文案。
3. **异步化**：通知写入可改为 Kafka 异步（与推荐一致），避免评论接口因通知表写入变慢。
4. **清理策略**：通知只增不减会持续膨胀，建议增加定时任务清理 90 天前的已读通知，或按用户保留最近 N 条。
5. **聚合展示**：同一用户对同一评论的多次回复可聚合为"xxx 等人回复了你"，减少通知噪音。

---

## 13. 已知问题与改进建议

1. **敏感配置明文**：数据库密码、邮箱授权码、MinIO 密钥直接写在配置文件中，建议迁移到环境变量/配置中心并轮换凭据。
2. **密码明文存储**：`User.password` 未加密，建议使用 BCrypt 等哈希算法。
3. **登录态过短**：`user_token` 仅 1 分钟过期，且接口未做统一鉴权拦截，建议引入 JWT 或 Spring Security。
4. **Kafka 配置重复**：`KafkaConfig` 与 `application.properties` 配置冲突，建议只保留一处。
5. **点赞幂等性**：Redis List 允许重复点赞，建议改用 Set 或在业务层去重。
6**`AddUserProducer` 未注入**：`kafkaTemplate` 字段缺少 `@Autowired`，且当前未被使用，建议补全或删除。
7**`MusicServiceImpl.add` 中 `musicRankRepository.addMusic(music.getId())`** 在 `useGeneratedKeys` 回填前调用存在风险，建议统一使用 `musicInDb.getId()`。
8**`MusicMapper.queryByTitle` 使用 `LIKE`**，但业务上按标题唯一校验，建议改为精确匹配或加唯一索引。
9**激活码随机源**：`ActivateCodeUtil` 使用 `java.util.Random`，建议改为 `SecureRandom`，并增加发送频率限制。

---

## 14. 测试

```bash
./mvnw test
```

当前仅包含 Spring Boot 默认的上下文加载测试（`MusicApplicationTests`）。
