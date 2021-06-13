package org.vuffy.o2o.dao;

import org.vuffy.o2o.entity.ProductCategory;

import java.util.List;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/6/12 2:28 下午
 */
public interface ProductCategoryDao {
    /**
     * 通过 shopId 查询该店铺中的商品类别
     * @param: [shopId]
     * @return: java.util.List<org.vuffy.o2o.entity.ProductCategory>
     * @author vuffy
     * @date: 2021/6/12 2:29 下午
     */
    List<ProductCategory> queryProductCategoryList(long shopId);

    /**
     * 批量插入商品类别
     * @param: [productCategoryList]
     * @return: int 返回插入影响的行数
     * @author vuffy
     * @date: 2021/6/13 10:43 上午
     */
    int batchInsertProductCategory(List<ProductCategory> productCategoryList);


}
