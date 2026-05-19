# Xioayi API

Spring Boot 3.5 认证 + 商品管理后端服务。

## 技术栈

| 组件 | 版本 |
|------|------|
| Spring Boot | 3.5.11 |
| Java | 17 |
| MySQL + Redis | Token 黑名单 + 抢券原子计数 |
| MyBatis-Plus | 3.5.10（含 jsqlparser 分页） |
| Spring Security + JWT | — |
| springdoc-openapi | 2.8.12 |
| 支付宝 SDK | alipay-easysdk 2.2.3（沙箱） |

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
├── config/         # Security + JWT + OpenAPI + MyBatis-Plus + Alipay 配置
├── controller/     # Auth, Product, Coupon, Order, AlipayNotify 控制器
├── dao/            # Product, Coupon, UserCoupon, Order, SysOperLog, User 实体
├── dto/            # LoginRequest, LoginResponse
├── mapper/         # MyBatis-Plus Mapper
├── service/        # 业务逻辑（含 CouponCacheService Redis 库存管理）
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
| POST | `/api/orders/product/{productId}` | 创建订单 | 是 |
| GET | `/api/orders/{orderNo}` | 查询订单 | 是 |
| POST | `/api/orders/{orderNo}/pay` | 获取支付宝支付表单 | 是 |
| POST | `/api/alipay/notify` | 支付宝异步通知回调 | 否（公开） |

### 下单支付流程

1. `POST /api/orders/product/{id}` 创建订单（`expire_time = now + 30min`）
2. `POST /api/orders/{orderNo}/pay` 获取支付宝支付表单
3. 前端自动提交表单跳转沙箱支付页
4. 支付宝异步回调 `POST /api/alipay/notify` 更新订单状态
5. 浏览器访问 `http://localhost:8080/pay-test.html` 可视化测试

### 支付宝沙箱

`application.yml` 配置支付宝沙箱参数：

```yaml
alipay:
  appId: 你的沙箱APPID
  merchantPrivateKey: 你的应用私钥（RSA2）
  alipayPublicKey: 支付宝公钥（非应用公钥）
  notifyUrl: http://你的内网穿透地址/api/alipay/notify  # 注意单斜杠
  returnUrl: http://你的内网穿透地址/pay-test.html
  gatewayUrl: https://openapi-sandbox.dl.alipaydev.com/gateway.do
```

> ⚠️ `notifyUrl` 中的内网穿透地址必须保持在线，否则支付宝无法回调。
> 沙箱测试时在支付页面请点击 **"登录账户付款"**，不要扫描二维码。

### 测试支付页面

浏览器打开 [`http://localhost:8080/pay-test.html`](http://localhost:8080/pay-test.html) 即可可视化测试：登录 → 选商品下单 → 跳转支付宝沙箱 → 买家账号登录支付。

### 抢券规则（3 层防护）

| 防护 | 机制 | 触发条件 |
|------|------|---------|
| ① 防抖 | `@Idempotent(ttl=2s)` Redis SET NX | 同一用户抢同一券 2 秒内重复点击 |
| ② 限流拉黑 | Redis 计数器 + 黑名单 | 用户 5 次/分钟 或 设备 10 次/分钟 → 拉黑 30 分钟 |
| ③ 业务约束 | Redis Lua DECR + DB 唯一约束 | 库存耗尽或已领取过 |

## License

MIT
