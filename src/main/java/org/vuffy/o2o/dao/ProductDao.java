package org.vuffy.o2o.dao;

import org.vuffy.o2o.entity.Product;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/6/15 7:42 上午
 */
public interface ProductDao {

    Product queryProductByProductId(long productId);

    /**
     * 插入商品
     * @param: [product]
     * @return: int
     * @author vuffy
     * @date: 2021/6/15 7:44 上午
     */
    int insertProduct(Product product);
}
