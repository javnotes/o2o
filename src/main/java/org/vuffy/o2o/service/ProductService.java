package org.vuffy.o2o.service;

import org.vuffy.o2o.dto.ImageHolder;
import org.vuffy.o2o.dto.ProductCategoryExecution;
import org.vuffy.o2o.dto.ProductExecution;
import org.vuffy.o2o.entity.Product;
import org.vuffy.o2o.entity.ProductImg;
import org.vuffy.o2o.exceptions.ProductOperationException;

import java.util.List;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/6/16 5:00 上午
 */
public interface ProductService {

    /**
     * 添加商品信息（含图片处理）
     * 参数：商品、缩略图、商品详情图片
     * @author vuffy
     * @date 2021/6/16 5:08 上午
     * @version 1.0
     */
    ProductExecution addProduct(Product product, ImageHolder imageHolder, List<ImageHolder> imageHolderList) throws ProductOperationException;

    /**
     * 通过商品 id 查询唯一的商品信息
     * @param productId
     * @return
     */
    Product getProductById(long productId);

    /**
     * 修改商品信息，包括图片的处理
     * @param product
     * @param thumbnail
     * @param productImgList
     * @return
     * @throws ProductOperationException
     */
    ProductExecution modifyProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImgList)
        throws ProductOperationException;

    /**
     * 查询商品列表并分页，可输入的条件：商品名（模糊）、商品状态、店铺id、商品类别
     * @param productCondition
     * @param pageIndex
     * @param pageSize
     * @return
     */
    ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize);
}
