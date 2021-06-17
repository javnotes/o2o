package org.vuffy.o2o.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vuffy.o2o.dao.ProductDao;
import org.vuffy.o2o.dao.ProductImgDao;
import org.vuffy.o2o.dto.ImageHolder;
import org.vuffy.o2o.dto.ProductExecution;
import org.vuffy.o2o.entity.Product;
import org.vuffy.o2o.entity.ProductImg;
import org.vuffy.o2o.enums.ProductStateEnum;
import org.vuffy.o2o.exceptions.ProductOperationException;
import org.vuffy.o2o.service.ProductService;
import org.vuffy.o2o.util.ImageUtil;
import org.vuffy.o2o.util.PathUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/6/16 5:12 上午
 */

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private ProductImgDao productImgDao;

    /**
     * 1.处理缩略图,获取缩略图相对路徑并赋值给 product
     * 2.往 tb_product 写入商品信息,获取 productId
     * 3.结合 productId 批量处理商品详情图
     * 4.将商品详情图列表批量插入 tb_product_img 中
     * @param: [product, imageHolder商品缩略图, imageHolderList 商品详情图]
     * @return: org.vuffy.o2o.dto.ProductExecution
     * @author vuffy
     * @date: 2021/6/17 5:14 上午
     */
    @Override
    @Transactional
    public ProductExecution addProduct(Product product, ImageHolder imageHolder, List<ImageHolder> imageHolderList) throws ProductOperationException {
        // 空值判断
        if (product != null && product.getShop() != null && product.getShop().getShopId() != null) {
            // 给商品赋值默认信息
            product.setCreateTime(new Date());
            product.setLastEditTime(new Date());
            // 添加商品的默认状态为：上架
            product.setEnableStatus(1);
            // 商品缩略图不为空，则添加
            if (imageHolder != null) {
                addThumbnail(product, imageHolder);
            }
            try {
                // 创建商品信息
                int effectedNum = productDao.insertProduct(product);
                if (effectedNum <= 0) {
                    throw new ProductOperationException("创建商品失败");
                }
            } catch (Exception e) {
                throw new ProductOperationException("创建商品失败" + e.getMessage());
            }

            // 若商品详情图不为空
            if (imageHolderList != null && imageHolderList.size() > 0) {
                addProductImagList(product, imageHolderList);
            }
            return new ProductExecution(ProductStateEnum.SUCCESS, product);
        } else {
            // 参数有空值
            return new ProductExecution(ProductStateEnum.EMPTY);
        }
    }

    /**
     * 批量添加(商品详情)图片
     * @param: [product, imageHolderList]
     * @return: void 
     * @author vuffy
     * @date: 2021/6/17 8:07 下午
     */
    private void addProductImagList(Product product, List<ImageHolder> imageHolderList) {
        // 获取图片存储路径--》存放到相应店铺的文件夹下
        String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
        List<ProductImg> productImgList = new ArrayList<>();
        // 遍历图片，添加进 productImg 实体类中
        for (ImageHolder imageHolder : imageHolderList) {
            String imgAddr = ImageUtil.generateNormalImg(imageHolder, dest);
            ProductImg productImg = new ProductImg();
            productImg.setImgAddr(imgAddr);
            productImg.setProductId(product.getProductId());
            productImg.setCreateTime(new Date());
            productImgList.add(productImg);
        }
        // 如果有图片需要添加，就执行批量操作
        if (productImgList.size() > 0) {
            try {
                int effectedNum = productImgDao.batchInsertProductImg(productImgList);
                if (effectedNum <= 0) {
                    throw new ProductOperationException("创建商品详情图片失败");
                }
            } catch (Exception e) {
                throw new ProductOperationException("创建商品详情图片失败" + e.toString());
            }
         }
        
    }

    /**
     * 添加商品的缩略图
     * @param: [product, imageHolder]
     * @return: void
     * @author vuffy
     * @date: 2021/6/17 12:44 下午
     */
    private void addThumbnail(Product product, ImageHolder imageHolder) {
        String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
        String thumbnailAddr = ImageUtil.generateThumbnail(imageHolder, dest);
        product.setImgAddr(thumbnailAddr);
    }
}
