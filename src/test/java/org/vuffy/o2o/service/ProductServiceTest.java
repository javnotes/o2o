package org.vuffy.o2o.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vuffy.o2o.BaseTest;
import org.vuffy.o2o.dto.ImageHolder;
import org.vuffy.o2o.dto.ProductExecution;
import org.vuffy.o2o.entity.Product;
import org.vuffy.o2o.entity.ProductCategory;
import org.vuffy.o2o.entity.Shop;
import org.vuffy.o2o.enums.ProductStateEnum;
import org.vuffy.o2o.exceptions.ShopOperationException;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/6/17 9:12 下午
 */
public class ProductServiceTest extends BaseTest {

    @Autowired
    private ProductService productService;

    @Test
    public void testProduct() throws ShopOperationException, FileNotFoundException {
        // 创建 shopId=27 && productCategory=9 的商品实例，并给其成员变量赋值
        Product product = new Product();

        Shop shop = new Shop();
        shop.setShopId(27L);
        ProductCategory pc = new ProductCategory();
        pc.setProductCategoryId(9L);

        product.setShop(shop);
        product.setProductCategory(pc);
        product.setProductName("测试商品-1");
        product.setProductDesc("测试商品-1");
        product.setPriority(20);
        product.setCreateTime(new Date());
        product.setEnableStatus(ProductStateEnum.SUCCESS.getState());

        // 创建缩略图文件流
        File thumbnailFile = new File("/Users/liliansong/Documents/woman-5584374.jpg");
        InputStream is = new FileInputStream(thumbnailFile);
        ImageHolder thumbnail = new ImageHolder(thumbnailFile.getName(), is);

        // 创建两个商品详情图片文件流，并将它们添加到详情图列表中
        File productImg1 = new File("/Users/liliansong/Documents/v2-ab414665a1630339319868cfd67696a1_1440w.jpg");
        InputStream is1 = new FileInputStream(productImg1);
        File productImg2 = new File("/Users/liliansong/Documents/互联网大厂新入职员工各职级薪资对应表（技术线）V4.5 (2020.5).png");
        InputStream is2 = new FileInputStream(productImg2);

        List<ImageHolder> imageHolderList = new ArrayList<>();
        imageHolderList.add(new ImageHolder(productImg1.getName(), is1));
        imageHolderList.add(new ImageHolder(productImg2.getName(), is2));

        // 添加商品验证
        ProductExecution pe = productService.addProduct(product, thumbnail, imageHolderList);
        assertEquals(ProductStateEnum.SUCCESS.getState(), pe.getState());


    }
}
