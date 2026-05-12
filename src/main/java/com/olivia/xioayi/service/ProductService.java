package com.olivia.xioayi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.olivia.xioayi.dao.Product;

public interface ProductService {

    /** 分页查询商品列表 */
    Page<Product> listProducts(int page, int size);

    /** 根据ID查询单个商品 */
    Product getProductById(Long id);

    /** 新增商品 */
    Product createProduct(Product product);

    /** 更新商品 */
    Product updateProduct(Long id, Product product);

    /** 删除商品 */
    void deleteProduct(Long id);
}