package org.vuffy.o2o.dao;

import org.vuffy.o2o.entity.ProductImg;

import java.util.List;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/6/15 7:46 上午
 */
public interface ProductImgDao {

    List<ProductImg> queryProductImgList(long productId);

    /**
     * 批量增加商品图片
     * @param: [productImgList]
     * @return: int
     * @author vuffy
     * @date: 2021/6/15 7:48 上午
     */
    int batchInsertProductImg(List<ProductImg> productImgList);

    /**
     * 删除指定商品下的所有详情图
     * @param productId
     * @return
     */
    int deleteProductImgByProductId(long productId);
}
