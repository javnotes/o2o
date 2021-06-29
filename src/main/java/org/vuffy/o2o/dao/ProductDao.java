package org.vuffy.o2o.dao;

import org.apache.ibatis.annotations.Param;
import org.vuffy.o2o.entity.Product;

import java.util.List;

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

    // 这两个方法的productCondition一样，才能保证queryProductList可以使用queryProductCount的结果
    /**
     * 查询店铺的所有商品，得到商品列表并分页，可输入的条件：商品名（模糊）、商品状态、店铺id、商品类别
     * @param productCondition
     * @param rowIndex
     * @param pageSize
     * @return
     */
    List<Product> queryProductList(@Param("productCondition") Product productCondition, @Param("rowIndex") int rowIndex,
                                   @Param("pageSize") int pageSize);

    /**
     * 查询productCondition对应的商品总数
     * @param productCondition
     * @return
     */
    int queryProductCount(@Param("productCondition") Product productCondition);

    /**
     * 删除商品类别之前，将商品类别Id置为空
     * @param productCategoryID
     * @return 影响的行数
     */
    int updateProductCategoryToNull(long productCategoryID);

}
