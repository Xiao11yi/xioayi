package com.olivia.xioayi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.olivia.xioayi.annotation.BusinessType;
import com.olivia.xioayi.annotation.Log;
import com.olivia.xioayi.dao.Product;
import com.olivia.xioayi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /** 分页查询，默认第1页、每页10条 */
    @GetMapping
    public ResponseEntity<Page<Product>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.listProducts(page, size));
    }

    @Log(title = "商品管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @Log(title = "商品管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @Log(title = "商品管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}