package com.olivia.xioayi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.olivia.xioayi.dao.Product;
import com.olivia.xioayi.mapper.ProductMapper;
import com.olivia.xioayi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public Page<Product> listProducts(int page, int size) {
        // 按创建时间倒序分页查询
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Product getProductById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new NoSuchElementException("商品不存在: id=" + id);
        }
        return product;
    }

    @Override
    public Product createProduct(Product product) {
        // 设置创建时间和更新时间
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.insert(product);
        return product;
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        // 验证商品是否存在
        getProductById(id);
        product.setId(id);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);
        return productMapper.selectById(id);  // 返回更新后的数据
    }

    @Override
    public void deleteProduct(Long id) {
        getProductById(id);  // 验证存在
        productMapper.deleteById(id);
    }
}