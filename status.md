# 项目接口测试状态

> 测试时间：2026-05-12
> 项目状态：✅ 已启动（端口 8080）
> 测试用户：`admin` / `123456`

---

## 统一返回格式

所有接口统一返回格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

| code | message | 说明 |
|------|---------|------|
| 200 | 操作成功 | 正常返回 |
| 201 | 创建成功 | 资源创建 |
| 400 | 请求参数错误 | 参数校验失败 |
| 401 | 未授权 | 未登录或 Token 无效 |
| 403 | 无权限 | 权限不足 |
| 404 | 资源不存在 | 资源未找到 |
| 500 | 服务器内部错误 | 服务端异常 |

---

## 认证模块 — AuthController

**路径前缀：** `/api/auth`

| 方法 | 路径 | 认证要求 | 测试结果 | 响应 data |
|------|------|----------|----------|-----------|
| POST | `/api/auth/login` | 无需 Token | ✅ 通过 | `{"token":"...", "username":"admin"}` |
| POST | `/api/auth/logout` | 无需 Token | ✅ 通过 | `null` |
| GET | `/api/auth/api/hello` | 需 Token | ✅ 通过 | `"Hello, admin"` |

### 异常场景

| 场景 | 响应 |
|------|------|
| 错误密码登录 | `{"code":401, "message":"未授权", "data":null}` |
| 登出后携带 Token 访问 | `{"code":401, "message":"未授权", "data":null}` |

---

## 商品管理 — ProductController

**路径前缀：** `/api/products`

| 方法 | 路径 | 认证要求 | 测试结果 | 响应 data |
|------|------|----------|----------|-----------|
| GET | `/api/products?page=1&size=10` | 无需 Token | ✅ 通过 | `Page<Product>` |
| POST | `/api/products` | 需 Token | ✅ 通过 | 创建的商品对象 |
| PUT | `/api/products/{id}` | 需 Token | ✅ 通过 | 更新后的商品对象 |
| DELETE | `/api/products/{id}` | 需 Token | ✅ 通过 | `null` |

### 异常场景

| 场景 | 响应 |
|------|------|
| 删除不存在的商品 (id=999) | `{"code":404, "message":"资源不存在", "data":null}` |

### 完整 CRUD 测试流程

```bash
# 登录获取 Token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 新增商品（含中文建议从文件读取 body）
curl -s -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json; charset=UTF-8" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"Mac mini","description":"M4 芯片 16GB 内存","price":5999.00,"stock":30}'

# 查询列表（公开接口）
curl -s "http://localhost:8080/api/products?page=1&size=10"

# 更新商品
curl -s -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"MacBook Pro 2025","price":22999.00,"stock":40}'

# 删除商品
curl -s -X DELETE http://localhost:8080/api/products/5 \
  -H "Authorization: Bearer $TOKEN"

# 删除不存在的商品（返回 404）
curl -s -X DELETE http://localhost:8080/api/products/999 \
  -H "Authorization: Bearer $TOKEN"

# 登出
curl -s -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer $TOKEN"
```

---

## 模拟数据（data.sql 启动时自动插入）

| ID | 名称 | 价格 | 库存 |
|----|------|------|------|
| 1 | MacBook Pro | ¥19,999 | 50 |
| 2 | iPhone 16 Pro | ¥9,999 | 100 |
| 3 | AirPods Pro 2 | ¥1,899 | 200 |
| 4 | iPad Air | ¥4,799 | 80 |
| 5 | Apple Watch S10 | ¥3,199 | 60 |

> 自增值已设为 100，用户新增商品从 ID 100 开始，不与模拟数据冲突。

---

## 安全规则

| 规则 | 说明 |
|------|------|
| `POST /api/auth/login, /logout` | 公开访问 |
| `GET /api/products/**` | 公开访问 |
| 其余所有接口 | 需 Bearer Token 认证 |
| 登出后 Token 立即失效（Redis 中删除） | 后续请求返回 401 |

---

## 数据库依赖

| 组件 | 地址 |
|------|------|
| MySQL | `localhost:3306 / auth_demo` |
| Redis | `localhost:6379` |

---

## 接口测试结论

- **11 项测试全部通过** ✅
- 统一返回格式 `{code, message, data}` 已落地
- 全局异常处理覆盖认证、权限、资源不存在、服务器错误
- 认证流程（登录 → 认证请求 → 登出 → 登出后拒绝）完整验证通过
- 商品 CRUD（增删改查 + 异常场景）完整验证通过
- 日志 AOP 切面随 CRUD 操作自动触发
- 模拟数据 5 条，自增值隔离，不影响用户新增

---

## 故障修复记录

### 登录 403 问题

**现象：** `POST /api/auth/login` 返回 403 空响应

**原因：**
1. 密码为 `123456`，但 DataInitializer 写入了 `admin123` 的哈希
2. SecurityConfig 未配置 `AuthenticationEntryPoint`，认证失败默认返回 403

**修复：** `SecurityConfig.java` — 添加 `AuthenticationEntryPoint` 返回 401 及 `ApiResponse` 格式

### 中文编码乱码

**现象：** 登出后访问返回 `{"code":401,"message":"???","data":null}`

**原因：** SecurityConfig 中 `response.setContentType(MediaType.APPLICATION_JSON_VALUE)` 未指定 charset

**修复：** 改为 `response.setContentType("application/json;charset=UTF-8")`

### 新增商品 500 错误

**现象：** `POST /api/products` 返回 500

**原因：** `data.sql` 使用固定 ID 插入模拟数据，但未重置 MySQL `AUTO_INCREMENT`，导致后续新增时主键冲突

**修复：** `data.sql` — 末尾添加 `ALTER TABLE product AUTO_INCREMENT = 100;`

### 删除不存在商品 500 错误

**现象：** `DELETE /api/products/999` 返回 500

**原因：** `ProductServiceImpl.getProductById()` 抛出 `RuntimeException`，全局异常处理返回 500

**修复：** `ProductServiceImpl.java` — 改为抛出 `NoSuchElementException`，全局异常处理返回 404
