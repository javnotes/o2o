package org.vuffy.o2o.dao;

import org.vuffy.o2o.entity.Product;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/6/15 7:42 上午
 */
public interface ProductDao {

    /**
     * 插入商品
     *
     * @param: [product]
     * @return: int
     * @author vuffy
     * @date: 2021/6/15 7:44 上午
     */
    int insertProduct(Product product);

    /**
     * 通过 pdoductId 查询唯一的商品信息（包括商品的详情图），故 dao.xml 有 resultMap
     *
     * @param productId
     * @return
     */
    Product queryProductById(long productId);

    /**
     * 更新（修改）商品信息
     *
     * @param product
     * @return
     */
    int updateProduct(Product product);

    /**
     * 删除指定商品下的所有详情图片
     *
     * @param productId
     * @return
     */
    int deleteProductImgByProductId(long productId);
}
