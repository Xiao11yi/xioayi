# 项目接口测试状态

> 测试时间：2026-05-19
> 项目状态：✅ 已启动（端口 8080）
> 测试用户：`admin` / `123456`
> 测试结果：**19 项全部通过** ✅

---

## 统一返回格式

```json
{"code": 200, "message": "操作成功", "data": { ... }}
```

| code | 说明 |
|------|------|
| 200/201 | 成功 |
| 400/401/403 | 参数错误/未授权/无权限 |
| 404/500 | 资源不存在/服务器错误 |

---

## API 端点

### 认证管理 — `/api/auth`

| 方法 | 路径 | 认证 | 结果 |
|------|------|------|------|
| POST | `/api/auth/login` | 无需 Token | ✅ |
| POST | `/api/auth/logout` | 无需 Token | ✅ |
| GET | `/api/auth/api/hello` | 需 Token | ✅ |

### 商品管理 — `/api/products`

| 方法 | 路径 | 认证 | 结果 |
|------|------|------|------|
| GET | `/api/products` | 无需 Token | ✅ |
| GET | `/api/products/{id}` | 无需 Token | ✅ |
| POST | `/api/products` | 需 Token | ✅ |
| PUT | `/api/products/{id}` | 需 Token | ✅ |
| DELETE | `/api/products/{id}` | 需 Token | ✅ |

### 优惠券管理 — `/api/coupons`

| 方法 | 路径 | 认证 | 结果 |
|------|------|------|------|
| GET | `/api/coupons` | 无需 Token | ✅ |
| GET | `/api/coupons/{id}` | 无需 Token | ✅ |
| POST | `/api/coupons` | 需 Token | ✅ |
| PUT | `/api/coupons/{id}` | 需 Token | ✅ |
| DELETE | `/api/coupons/{id}` | 需 Token | ✅ |

**异常覆盖：** 密码错误 → 401，登出后访问 → 401，删除不存在资源 → 404

---

## AOP 操作日志

`@Log` 注解 + `LogAspect` 切面自动记录操作到 `sys_oper_log` 表。

覆盖 8 种场景：登录（成功/失败）、注销、新增/更新/删除商品、新增/更新/删除优惠券 — 全部通过 ✅

---

## 安全规则

| 规则 | 说明 |
|------|------|
| `POST /api/auth/login, /logout` | 公开 |
| `GET /api/products/**`、`GET /api/coupons/**` | 公开 |
| 其余接口 | 需 Bearer Token |
| 登出后 Token 立即失效 | 返回 401 |

---

## 数据库依赖

| 组件 | 地址 |
|------|------|
| MySQL | `localhost:3306 / auth_demo` |
| Redis | `localhost:6379` |

---

## Swagger / OpenAPI

| 项 | 值 |
|----|-----|
| 库 | `springdoc-openapi-starter-webmvc-ui:2.8.12` |
| JSON | `/api-docs` | UI | `/swagger-ui.html` |

### Security 放行

| 路径 | 状态 |
|------|------|
| `/swagger-ui.html`, `/swagger-ui/**`, `/api-docs`, `/api-docs/**`, `/v3/api-docs/**`, `/webjars/**` | ✅ 全部放行 |

### 修复项

| 维度 | 状态 |
|------|------|
| 依赖版本 | ✅ 2.6.0 → 2.8.12（修复 Spring Boot 3.5.x 兼容性） |
| 全局 API 信息 | ✅ `OpenApiConfig.java` — 标题/版本/描述 + Bearer JWT 安全方案 |
| Controller 注解 | ✅ 添加 `@Tag` + `@Operation`（认证管理、商品管理、优惠券管理） |
| 模型注解 | ✅ 添加 `@Schema`（LoginRequest、LoginResponse、Product、Coupon） |
| 分页查询 | ✅ 新增 `MybatisPlusConfig.java` + `mybatis-plus-jsqlparser`（修复 total 始终为 0） |

---

## 故障修复记录

### Swagger `/api-docs` 500
- **原因：** springdoc 2.6.0 不兼容 Spring Boot 3.5.x（`ControllerAdviceBean` 构造器移除）
- **修复：** 升级至 2.8.12 + GlobalExceptionHandler 添加异常日志

### 登录 403
- **原因：** 密码哈希不匹配 + 未配置 AuthenticationEntryPoint
- **修复：** SecurityConfig 添加 AuthenticationEntryPoint 返回 401

### 中文编码乱码
- **原因：** response.setContentType 未指定 charset
- **修复：** `"application/json;charset=UTF-8"`

### 新增商品 500
- **原因：** data.sql 未重置 AUTO_INCREMENT，主键冲突
- **修复：** data.sql 末尾添加 `ALTER TABLE product AUTO_INCREMENT = 100;`

### 删除不存在商品 500
- **原因：** getProductById 抛出 RuntimeException
- **修复：** 改为抛出 NoSuchElementException，全局处理返回 404

### 分页查询 total 始终为 0
- **原因：** MyBatis-Plus 3.5.10 缺少 `mybatis-plus-jsqlparser` 依赖，`PaginationInnerInterceptor` 未注册，分页不计数
- **修复：** `pom.xml` 添加 `mybatis-plus-jsqlparser:3.5.10` + 新增 `MybatisPlusConfig.java` 配置拦截器

### 商品查询详情接口缺失
- **原因：** `ProductController` 缺少 `@GetMapping("/{id}")` 端点，`GET /api/products/{id}` 返回 405
- **修复：** `ProductController.java` 新增 `get()` 方法，调用 `productService.getProductById()`
