package com.olivia.xioayi.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.olivia.xioayi.annotation.BusinessType;
import com.olivia.xioayi.annotation.Log;
import com.olivia.xioayi.common.ApiResponse;
import com.olivia.xioayi.dao.Product;
import com.olivia.xioayi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<Page<Product>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(productService.listProducts(page, size));
    }

    @Log(title = "商品管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ApiResponse<Product> create(@RequestBody Product product) {
        return ApiResponse.success(productService.createProduct(product));
    }

    @Log(title = "商品管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public ApiResponse<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return ApiResponse.success(productService.updateProduct(id, product));
    }

    @Log(title = "商品管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success();
    }
}