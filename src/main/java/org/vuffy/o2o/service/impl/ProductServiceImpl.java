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
import org.vuffy.o2o.util.PageCalculator;
import org.vuffy.o2o.util.PathUtil;

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
     *
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

    @Override
    public Product getProductById(long productId) {
        return productDao.queryProductById(productId);
    }

    @Override
    @Transactional
    // 1.若缩略图参数有值，则处理缩略图；
    // 若原先存在缩略图，则先删除原缩略图再添加新图，之后获取新缩略图相对路径并赋值给 product
    // 2.若商品详情图列表参数有值，对商品详情图片列表进行同样的操作（同 1）
    // 3.将 tb_product_img 下面的该商品原先的商品详情图记录全部清除
    // 4.更新 tb_product_img 以及 tb_product 的信息
    public ProductExecution modifyProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImgList) throws ProductOperationException {
        // 判断传入的 product 是否为空
        if (product != null && product.getShop() != null && product.getShop().getShopId() != null) {
            // 设置修改商品时的默认修改属性
            product.setLastEditTime(new Date());

            // 先获取商品的原缩略图信息，
            Product tempProduct = productDao.queryProductById(product.getProductId());

            // 若需修改商品缩略图，且原商品缩略图不为空，则删除原商品缩略图，并添加新商品缩略图
            if (thumbnail != null) {
                if (tempProduct.getImgAddr() != null) {
                    // 删除实际图片及文件夹
                    ImageUtil.deleteFileOrPath(tempProduct.getImgAddr());
                }
                // 保存图片，更新 product 中保存的图片地址
                addThumbnail(product, thumbnail);
            }
            // 如果需修改商品详情图
            if (productImgList != null && productImgList.size() > 0) {
                // 删除原有的商品详情图（若有）
                if (tempProduct.getProductImgList().size() > 0) {
                    deleteProductImgList(product.getProductId());
                }
                addProductImagList(product, productImgList);
            }

            try {
                // 更新商品信息
                int effectedNum = productDao.updateProduct(product);
                if (effectedNum <= 0) {
                    throw new ProductOperationException("更新商品信息失败");
                }
                return new ProductExecution(ProductStateEnum.SUCCESS, product);
            } catch (Exception e) {
                throw new ProductOperationException("更新商品信息失败：" + e.toString());
            }
        } else {
            return new ProductExecution(ProductStateEnum.EMPTY);
        }
    }

    @Override
    public ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize) {
        // 前端的页码转换为数据库的行码，调用dao层取回指定页码的商品列表
        int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
        List<Product> productList = productDao.queryProductList(productCondition, rowIndex, pageSize);

        // 基于相同条件，查询符合条件的商品总个数
        int count = productDao.queryProductCount(productCondition);
        ProductExecution pe = new ProductExecution();
        pe.setProductList(productList);
        pe.setCount(count);
        return pe;
    }

    /**
     * 删除某个商品下的所有详情图（若有）
     *
     * @param productId
     */
    private void deleteProductImgList(Long productId) {
        // 根据 productId 获取原来的图片
        List<ProductImg> productImgList = productImgDao.queryProductImgList(productId);
        // 删除原图片
        for (ProductImg productImg : productImgList) {
            ImageUtil.deleteFileOrPath(productImg.getImgAddr());
        }
        // 删除 productImg 中保存的图片信息
        productImgDao.deleteProductImgByProductId(productId);
    }

    /**
     * 批量添加(商品详情)图片
     *
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
     *
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
