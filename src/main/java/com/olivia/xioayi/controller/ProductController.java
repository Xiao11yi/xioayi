package com.olivia.xioayi.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.olivia.xioayi.annotation.BusinessType;
import com.olivia.xioayi.annotation.Log;
import com.olivia.xioayi.common.ApiResponse;
import com.olivia.xioayi.dao.Product;
import com.olivia.xioayi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品管理", description = "商品的增删改查操作")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "商品分页列表", description = "公开接口，无需登录，返回分页商品数据")
    @GetMapping
    public ApiResponse<Page<Product>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(productService.listProducts(page, size));
    }

    @Operation(summary = "查询商品详情", description = "公开接口，按 ID 查询商品，不存在则返回 404")
    @GetMapping("/{id}")
    public ApiResponse<Product> get(@PathVariable Long id) {
        return ApiResponse.success(productService.getProductById(id));
    }

    @Operation(summary = "新增商品", description = "需要登录，创建并返回新的商品信息")
    @Log(title = "商品管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ApiResponse<Product> create(@RequestBody Product product) {
        return ApiResponse.success(productService.createProduct(product));
    }

    @Operation(summary = "更新商品", description = "需要登录，按 ID 更新商品信息并返回更新后的数据")
    @Log(title = "商品管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public ApiResponse<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return ApiResponse.success(productService.updateProduct(id, product));
    }

    @Operation(summary = "删除商品", description = "需要登录，按 ID 删除商品，不存在则返回 404")
    @Log(title = "商品管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success();
    }
}