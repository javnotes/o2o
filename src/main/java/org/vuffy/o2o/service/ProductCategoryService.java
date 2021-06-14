package org.vuffy.o2o.service;

import org.vuffy.o2o.dto.ProductCategoryExecution;
import org.vuffy.o2o.entity.ProductCategory;
import org.vuffy.o2o.exceptions.ProductCategoryOperationException;

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

    /**
     * 使用到事务，故得定义异常
     * @param: [productCategoryList]
     * @return: org.vuffy.o2o.dto.ProductCategoryExecution
     * @author vuffy
     * @date: 2021/6/13 2:34 下午
     */
    ProductCategoryExecution batchAddProductCategory(List<ProductCategory> productCategoryList)
        throws ProductCategoryOperationException;

    /**
     * 将店铺中此类别下的商品里的类别id置为空，再删除掉该商品类别
     * @author vuffy
     * @date 2021/6/14 10:16 下午
     * @version 1.0
     */
    ProductCategoryExecution deleteProductCategory(long productCategoryId, long shopId)
        throws ProductCategoryOperationException;
}
