# Xioayi API

Spring Boot 3.5 认证 + 商品管理后端服务。

## 技术栈

| 组件 | 版本 |
|------|------|
| Spring Boot | 3.5.11 |
| Java | 17 |
| MySQL + Redis | — |
| MyBatis-Plus | 3.5.10（含 jsqlparser 分页） |
| Spring Security + JWT | — |
| springdoc-openapi | 2.8.12 |

## 快速开始

### 前置条件

- JDK 17+
- MySQL（创建数据库 `auth_demo`）
- Redis（默认 localhost:6379）

### 启动

```bash
# 配置数据库连接（src/main/resources/application.yml）
# 默认 root / ********

./mvnw spring-boot:run
```

### 测试用户

| 用户名 | 密码 |
|--------|------|
| admin | 123456 |

## API 文档

启动后访问：

- Swagger UI：`http://localhost:8080/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8080/api-docs`

所有需认证的接口可在 Swagger UI 中点击 **Authorize** 填入 `Bearer <token>` 后直接测试。

## 项目结构

```
src/main/java/com/olivia/xioayi/
├── annotation/     # @Log 注解
├── aspect/         # AOP 操作日志切面
├── common/         # 统一响应 + 全局异常处理
├── config/         # Security + JWT + OpenAPI + MyBatis-Plus 配置
├── controller/     # Auth, Product, Coupon 控制器
├── dao/            # Product, Coupon, SysOperLog, User 实体
├── dto/            # LoginRequest, LoginResponse
├── mapper/         # MyBatis-Plus Mapper
├── service/        # 业务逻辑
└── util/           # JWT 工具类
```

## 接口列表

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/auth/login` | 登录 | 否 |
| POST | `/api/auth/logout` | 登出 | 否 |
| GET | `/api/auth/api/hello` | 会话检查 | 是 |
| GET | `/api/products` | 商品分页列表 | 否 |
| POST | `/api/products` | 新增商品 | 是 |
| PUT | `/api/products/{id}` | 更新商品 | 是 |
| DELETE | `/api/products/{id}` | 删除商品 | 是 |
| GET | `/api/coupons` | 优惠券分页列表 | 否 |
| GET | `/api/coupons/{id}` | 优惠券详情 | 否 |
| POST | `/api/coupons` | 新增优惠券 | 是 |
| PUT | `/api/coupons/{id}` | 更新优惠券 | 是 |
| DELETE | `/api/coupons/{id}` | 删除优惠券 | 是 |
| POST | `/api/coupons/{id}/grab` | 抢优惠券 | 是 |

### 抢券规则

- 同一用户不可重复领取同一张优惠券
- 达到 `usage_limit` 后无法继续领取（`usage_limit=0` 表示不限制）
- 只能在 `start_time` ~ `end_time` 时间内领取
- `used_count` 使用原子递增，并发安全

## License

MIT
