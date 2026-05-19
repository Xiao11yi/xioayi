# Swagger API 文档使用指南

## 技术栈

项目使用 **springdoc-openapi**（v2.6.0），自动生成 OpenAPI 3.0 规范文档，并提供 Swagger UI 可视化界面。

| 组件 | 说明 |
|------|------|
| 库 | `springdoc-openapi-starter-webmvc-ui:2.6.0` |
| 规范 | OpenAPI 3.0 (v3) |
| UI | Swagger UI（内嵌） |
| JSON 端点 | `/api-docs` |
| YAML 端点 | `/api-docs.yaml`（默认支持） |

---

## 快速开始

### 启动项目

```bash
# 确保 MySQL 和 Redis 已启动，然后：
./mvnw spring-boot:run
```

### 访问文档

| 地址 | 说明 |
|------|------|
| `http://localhost:8080/swagger-ui.html` | Swagger UI 交互式页面 |
| `http://localhost:8080/api-docs` | OpenAPI JSON 原始数据 |

---

## 在 Swagger UI 中认证

项目接口默认需要 JWT 认证，Swagger UI 提供全局 Authorize 机制：

1. 打开 `http://localhost:8080/swagger-ui.html`
2. 点击右上角 **Authorize** 按钮
3. 在 `bearerAuth` 输入框中输入 `Bearer <你的 token>`（含 `Bearer` 前缀）
4. 点击 **Authorize** → **Close**
5. 之后所有 "需 Token" 的接口都会自动携带此 Token

### 获取 Token

调用登录接口或直接执行：

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

从响应中复制 `token` 值填入 Authorize 对话框。

---

## API 分组与接口

| Tag | Controller | 公开接口 | 需 Token |
|-----|-----------|----------|----------|
| 认证管理 | `AuthController` | 登录、登出 | 会话检查 |
| 商品管理 | `ProductController` | 列表查询 | 新增、更新、删除 |

---

## 开发指南：如何为新接口添加文档

### 1. 新建 Controller

```java
@Tag(name = "订单管理", description = "订单的增删改查操作")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Operation(summary = "订单列表", description = "分页查询当前用户订单")
    @GetMapping
    public ApiResponse<Page<Order>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // ...
    }

    @Operation(summary = "创建订单", description = "需要登录，创建新订单")
    @PostMapping
    public ApiResponse<Order> create(@RequestBody Order order) {
        // ...
    }
}
```

### 2. 新建 DTO

```java
@Data
public class OrderRequest {
    @Schema(description = "商品 ID", example = "1")
    private Long productId;

    @Schema(description = "数量", example = "2")
    private Integer quantity;
}
```

### 3. 新建 Entity

```java
@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    @Schema(description = "订单 ID")
    private Long id;

    @Schema(description = "订单金额")
    private BigDecimal totalAmount;
}
```

> 无需额外配置，springdoc 会自动扫描 `@RestController` 和模型类，生成对应的文档条目。

---

## 常用注解速查

### 类级别

| 注解 | 位置 | 作用 |
|------|------|------|
| `@Tag(name="...", description="...")` | Controller | 分组标签，显示在 Swagger UI 顶部 |
| `@Schema(description="...")` | DTO/Entity | 描述整个模型类（加在类上时） |

### 方法级别

| 注解 | 作用 |
|------|------|
| `@Operation(summary="...", description="...")` | 描述接口的简短标题和详细说明 |
| `@ApiResponse(responseCode="...", description="...")` | 描述特定响应状态码（已全局统一处理，一般无需加） |

### 字段级别

| 注解 | 作用 |
|------|------|
| `@Schema(description="...", example="...")` | 描述字段含义和示例值 |
| `@Schema(hidden = true)` | 从文档中隐藏该字段 |

### 参数级别

| 注解 | 作用 |
|------|------|
| `@Parameter(description="...")` | 描述路径/查询参数（springdoc 会自动从 `@PathVariable`、`@RequestParam` 生成，非必需） |

---

## 配置参考（application.yml）

```yaml
springdoc:
  api-docs:
    path: /api-docs          # OpenAPI JSON 路径
  swagger-ui:
    path: /swagger-ui.html   # Swagger UI 路径
    tags-sorter: alpha       # 标签按字母排序
    operations-sorter: alpha # 操作按字母排序
  packages-to-scan: com.olivia.xioayi.controller  # 扫描范围（可选，默认自动扫描）
  paths-to-match: /api/**    # 匹配路径（可选）
```

当前配置项见 `src/main/resources/application.yml` 第 39-45 行。

---

## 安全配置说明

Swagger 相关路径已在 `SecurityConfig.java` 中放行（第 36-43 行）：

```java
.requestMatchers(
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/v3/api-docs/**",
    "/api-docs",
    "/api-docs/**",
    "/webjars/**"
).permitAll()
```

新增公开接口时，如需放行请在该 `requestMatchers` 链中继续添加。

---

## 验证文档生效

```bash
# 检查 JSON 端点是否返回数据
curl -s http://localhost:8080/api-docs | jq '.info'
# 应输出: { "title": "Xioayi API", "version": "1.0.0", ... }

# 检查接口定义数
curl -s http://localhost:8080/api-docs | jq '.paths | length'
# 应输出接口数量
```

打开浏览器访问 `http://localhost:8080/swagger-ui.html` 可交互式测试所有接口。
