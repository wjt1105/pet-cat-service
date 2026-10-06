# 城市上门喂猫预约平台（PetSitter）

基于 Spring Cloud Alibaba 微服务架构的城市上门喂猫预约平台，覆盖 **用户注册登录 → 服务浏览 → 下单 → 支付 → 消息通知** 完整业务闭环。

## 一、技术栈

| 分类 | 技术 | 版本 |
|---|---|---|
| 核心框架 | Spring Boot | 2.7.15 |
| 微服务框架 | Spring Cloud Alibaba | 2021.0.1.0 |
| 注册中心 | Nacos | 1.4.2（严格对齐） |
| 网关 | Spring Cloud Gateway | - |
| 远程调用 | OpenFeign + LoadBalancer | - |
| 熔断降级 | Sentinel | 1.8.3 |
| 分布式事务 | Seata（AT 模式） | 1.4.2（严格对齐） |
| ORM | MyBatis-Plus | 3.5.3.1 |
| 数据库 | MySQL | 8.0 |
| 缓存/分布式锁 | Redis + Lua | - |
| 消息队列 | RabbitMQ | - |
| 认证 | JWT（jjwt 0.11.5） | - |
| 构建 | Maven 多模块聚合 | JDK 11 |

## 二、系统架构

```
                            ┌─────────────┐
                            │   客户端     │
                            └──────┬──────┘
                                   ▼
                    ┌──────────────────────────┐
                    │   gateway（8080）          │
                    │  JWT 全局鉴权 / 跨域 / 路由  │
                    └────────────┬─────────────┘
              ┌──────────┬───────┴───────┬──────────┐
              ▼          ▼               ▼          ▼
        user-service  service-service  order-service  pay-service
          (8001)        (8002)           (8003)        (8004)
              │          │▲ Sentinel      │  ▲ Feign    │ Feign
              │          │└─ 熔断降级      │  │ 回调改单  │
              │          │                ▼  │           │
              │          │         ┌─────────────────┐   │
              │          └─Feign──▶│ Seata TC (8091) │◀──┘
              │            扣库存   │ AT 分布式事务     │
              │                   └─────────────────┘
              │                          │
        ┌─────┴──────┐         ┌─────────┴─────────┐
        ▼            ▼         ▼                   ▼
     MySQL 8      Redis      RabbitMQ          Nacos (8848)
   (undo_log)   (Lua 锁)   (order.exchange)   Sentinel (8085)
                                    │
                                    ▼
                              msg-service（8005）
                          消费下单消息 / 模拟短信通知
```

### 模块说明

| 模块 | 端口 | 职责 |
|---|---|---|
| common | - | 统一 Result、状态码枚举、JWT 工具、实体类（被各服务依赖，无 Web 依赖） |
| gateway | 8080 | 统一入口：JWT 鉴权（白名单放行登录/注册）、全局跨域、路由转发 |
| user-service | 8001 | 注册、登录（BCrypt 加密）、签发 JWT |
| service-service | 8002 | 喂猫服务列表/详情、原子扣库存（`stock >= count` 行级锁）、Sentinel 熔断 |
| order-service | 8003 | 下单核心：Redis+Lua 分布式锁、Seata 全局事务、RabbitMQ 下单消息 |
| pay-service | 8004 | 模拟支付、支付回调改订单状态、防重复支付校验 |
| msg-service | 8005 | RabbitMQ 消费者：下单成功通知（模拟短信/站内信），手动 ACK |

## 三、环境准备（版本已严格对齐）

> **版本依据**：以下 Nacos / Sentinel / Seata 版本不是随意选择，而是直接取自
> `spring-cloud-alibaba-dependencies:2021.0.1.0` 官方 BOM 中绑定的客户端版本
> （nacos-client **1.4.2**、sentinel **1.8.3**、seata **1.4.2**）。
> Server 端与 Client 端同版本可彻底避免协议不兼容问题。

| 中间件 | 推荐版本 | 端口 | 说明 |
|---|---|---|---|
| **JDK** | **11（推荐）** | - | Nacos/Seata 1.4.x 官方仅支持 JDK 8/11；本机路径 `C:\Users\<用户>\.jdks\ms-11.0.32` |
| MySQL | 8.0.28 ~ 8.0.36 | 3306 | 数据库，账号 root/root；驱动 8.0.33 |
| Nacos Server | **1.4.2** | 8848 | 注册中心，单机模式 `startup.cmd -m standalone` |
| Redis | 6.2.x（Windows 可用 5.0.14 移植版） | 6379 | 分布式锁；Lettuce 6.1 客户端向下兼容 |
| Erlang/OTP | **23.3.4.11** | - | RabbitMQ 运行环境（必须先装） |
| RabbitMQ | **3.8.34** | 5672 | 与 Erlang 23.3 配对，guest/guest；spring-amqp 2.4.x 完整兼容 |
| Sentinel Dashboard | **1.8.3** | 8085 | 与客户端同版本（可选，不影响启动） |
| Seata Server | **1.4.2** | 8091 | TC 事务协调器，注册到 Nacos（application=seata-server，group=SEATA_GROUP） |

### JDK 选型（重要）

| 方案 | JDK | Nacos | Seata | 说明 |
|---|---|---|---|---|
| **A. 严格对齐（推荐）** | 11 | 1.4.2 | 1.4.2 | 官方测试组合，零兼容性风险，直接可用 |
| B. JDK 17 | 17 | 2.2.3 | 1.5.2 | 需手动覆盖版本：父 pom 的 dependencyManagement 中显式声明 `io.seata:seata-spring-boot-starter:1.5.2`，Nacos 2.x Server 对 1.4.x Client 向后兼容 |

⚠️ **切勿使用 JDK 21/23**：本项目已验证 JDK 23（`openjdk-23.0.2`）下旧版 Lombok 会抛
`NoSuchFieldError: JCTree$JCImport.qualid`，且 Seata 1.4.2 在高版本 JDK 存在反射访问问题。
在 IDEA 中务必把 Project SDK、Module Language Level、Run Configuration JRE 三处统一为 **JDK 11**。

## 四、启动步骤

```bash
# 1. 初始化数据库（建库 petsitter + 建表 + 测试数据）
mysql -uroot -proot < sql/schema.sql
mysql -uroot -proot petsitter < sql/data.sql

# 2. 启动中间件（版本见"环境准备"，推荐 JDK 11 环境）
#    Nacos 1.4.2:  startup.cmd -m standalone
#    Redis:        redis-server
#    RabbitMQ:     rabbitmq-service start（需先装 Erlang 23.3）
#    Sentinel:     java -jar sentinel-dashboard-1.8.3.jar --server.port=8085
#    Seata 1.4.2:  seata-server.bat（conf/application.yml 中 registry 配置为 Nacos）

# 3. 编译整个工程
mvn clean install -DskipTests

# 4. 按依赖顺序启动微服务（IDEA 直接运行各 Application 类）
#    GatewayApplication → UserServiceApplication → ServiceServiceApplication
#    → OrderServiceApplication → PayServiceApplication → MsgServiceApplication
```

## 五、接口一览（网关入口 http://localhost:8080）

| 功能 | 方法 | 路径 | 鉴权 |
|---|---|---|---|
| 用户注册 | POST | /api/user/register | 白名单 |
| 用户登录 | POST | /api/user/login | 白名单 |
| 服务列表 | GET | /api/service/list | Token |
| 服务详情 | GET | /api/service/{id} | Token |
| 创建订单 | POST | /api/order/create | Token |
| 订单详情 | GET | /api/order/{orderNo} | Token |
| 模拟支付 | POST | /api/pay/pay | Token |
| 支付单查询 | GET | /api/pay/{paymentNo} | Token |

测试账号：`meow01 / 123456`（用户）、`sitter01 / 123456`（铲屎官）、`admin / 123456`（管理员）

## 六、面试亮点

**1. Seata AT 分布式事务（下单链路）**
- `@GlobalTransactional` 标注在**独立 Bean**（`OrderGlobalTxService`）而非业务类内部方法，规避同类自调用导致 AOP 代理失效的经典坑
- 自定义 `FeignXidInterceptor` 透传 XID，保证 service-service 分支事务加入同一全局事务
- 两端 `tx-service-group`、Nacos 注册分组完全对齐，两库均建 `undo_log` 表

**2. Redis + Lua 分布式锁防超卖**
- 加锁 `SETNX + PEXPIRE`、释放 `GET 校验持有者 + DEL` 均封装为 Lua 原子脚本
- 锁粒度为 serviceId，**锁在 Seata 全局事务提交之后才释放**，杜绝"锁已放、事务未提交"的并发窗口
- 配合数据库 `WHERE stock >= count` 行级条件更新，形成应用层 + 数据库层双保险

**3. 网关统一鉴权**
- `GlobalFilter` 解析 JWT 后将 `X-User-Id` 写入请求头透传，业务服务零鉴权代码、无重复解析
- 白名单（登录/注册）AntPathMatcher 匹配，OPTIONS 预检请求直接放行

**4. RabbitMQ 可靠消息**
- 事务提交成功后才发送下单消息，保证消息语义准确
- 消费端手动 ACK：成功 `basicAck`、失败 `basicNack(requeue=true)`，配合 `prefetch=1` 防丢失防压垮
- 生产端 `__TypeId__` 与消费端本地类通过 `idClassMapping` 映射，跨服务 JSON 反序列化无需共享消息类

**5. Sentinel 双层熔断**
- 方法级 `@SentinelResource` + 独立 BlockHandler 类返回统一 Result JSON
- URL 级 `BlockExceptionHandler` Bean 替换默认纯文本响应，HTTP 429 + 统一错误体

**6. 支付一致性设计**
- 支付前 5 重校验：订单存在性、归属（只能付自己的单）、状态（防重复支付）、金额一致性（防篡改）、支付单唯一
- 回调改订单状态失败时抛异常回滚本地支付单，两边状态强一致

## 七、踩坑记录

| 问题 | 原因 | 解决方案 |
|---|---|---|
| `SC_TOO_MANY_REQUESTS` 编译报错 | 该常量是 Servlet 5.0（Jakarta/Tomcat 10）才加入，Spring Boot 2.7 的 javax.servlet 4.0 不存在 | 直接用字面量 `429`，并加注释说明 |
| `@GlobalTransactional` 不生效 | 注解基于 AOP 代理，同类内部方法自调用绕过代理 | 将事务方法抽到独立 Bean（`OrderGlobalTxService`），由外层注入调用 |
| MQ 消费端反序列化失败 | 生产端 Jackson 写入 `__TypeId__` 指向 order-service 的类，消费端无此类 | `DefaultJackson2JavaTypeMapper.setIdClassMapping` 做类型 ID → 本地类映射 |
| Feign 熔断 FallbackFactory 找不到类 | Spring Cloud 2021 中 `FallbackFactory` 包路径从 `feign.hystrix` 迁移到 `org.springframework.cloud.openfeign` | 使用新包路径 `org.springframework.cloud.openfeign.FallbackFactory` |
| MyBatis-Plus 分页不生效 | 3.5.x 必须手动注册 `MybatisPlusInterceptor` + `PaginationInnerInterceptor` | 配置类中显式声明 Bean 并指定 MySQL 方言 |
| 网关引入 common 后启动失败 | common 若引入 spring-boot-starter-web 会与 Gateway 的 WebFlux 冲突 | common 只保留 mybatis-plus-annotation、jjwt 等纯 POJO 依赖，不含 Web |
| Seata 分支事务不回滚 | 只有发起方配置 Seata，分支方（service-service）未接入、XID 无法绑定 | 两端都引入 seata starter，`tx-service-group` 一致，undo_log 建在同一业务库 |
| druid POM 警告 | Seata 传递依赖 druid 1.1.23 的 POM 有 checksum 警告 | 仅警告，不影响编译运行，可忽略 |
| `NoSuchFieldError: JCTree$JCImport ... qualid` | JDK 21+（本机实测 JDK 23）中 `JCImport.qualid` 字段类型由 `JCTree` 变为 `JCFieldAccess`，Boot 2.7.15 自带的 Lombok 1.18.28 不兼容 | 父 pom 显式覆盖 `<lombok.version>1.18.30</lombok.version>`；根本建议是改用 JDK 11/17，并检查 IDEA Lombok 插件 |
