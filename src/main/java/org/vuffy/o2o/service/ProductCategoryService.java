package org.vuffy.o2o.service;

import org.vuffy.o2o.entity.ProductCategory;

import java.util.List;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/6/12 3:19 下午
 */
public interface ProductCategoryService {
    /**
     * 根据 shopId 查询某店铺下的商品类别
     * @author vuffy
     * @date 2021/6/12 3:20 下午
     * @version 1.0
     */
    List<ProductCategory> getProductCategoryList(long shopId);
}
