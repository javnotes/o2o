package org.vuffy.o2o.dao;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.vuffy.o2o.BaseTest;
import org.vuffy.o2o.entity.Product;
import org.vuffy.o2o.entity.ProductCategory;
import org.vuffy.o2o.entity.ProductImg;
import org.vuffy.o2o.entity.Shop;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static checkers.units.UnitsTools.s;
import static org.junit.Assert.assertEquals;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/6/15 1:46 下午
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductDaoTest extends BaseTest {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProductImgDao productImgDao;

    @Test
    public void testAInsertProduct() throws Exception {
        Shop shop1 = new Shop();
        shop1.setShopId(27L);

        ProductCategory pc1 = new ProductCategory();
        pc1.setProductCategoryId(9L);

        // 初始化三个商品实例并添加进 shopId=1 的店铺
        Product product1 = new Product();
        product1.setProductName("测试商品1");
        product1.setProductDesc("描述商品1");
        product1.setImgAddr("test1");
        product1.setPriority(1);
        product1.setEnableStatus(1);
        product1.setCreateTime(new Date());
        product1.setLastEditTime(new Date());
        product1.setShop(shop1);
        product1.setProductCategory(pc1);

        Product product2 = new Product();
        product2.setProductName("测试商品2");
        product2.setProductDesc("描述商品2");
        product2.setImgAddr("test2");
        product2.setPriority(2);
        product2.setEnableStatus(1);
        product2.setCreateTime(new Date());
        product2.setLastEditTime(new Date());
        product2.setShop(shop1);
        product2.setProductCategory(pc1);

        Product product3 = new Product();
        product3.setProductName("测试商品3");
        product3.setProductDesc("描述商品3");
        product3.setImgAddr("test3");
        product3.setPriority(3);
        product3.setEnableStatus(1);
        product3.setCreateTime(new Date());
        product3.setLastEditTime(new Date());
        product3.setShop(shop1);
        product3.setProductCategory(pc1);

        // 判断三次添加是否成功
        int effectedNum = productDao.insertProduct(product1);
        assertEquals(1, effectedNum);
        effectedNum = productDao.insertProduct(product2);
        assertEquals(1, effectedNum);
        effectedNum = productDao.insertProduct(product3);
        assertEquals(1, effectedNum);
    }

    @Test
    // 测试和
    public void testBQueryProductList() throws Exception {
        Product productCondition = new Product();
        //  分页查询，此时条件为空，则查询该所有商品，包括下架的
        List<Product> products = productDao.queryProductList(productCondition, 0, 3);
        assertEquals(3, products.size());

        int count = productDao.queryProductCount(productCondition);
        assertEquals(7, count);

        // 查询商品名称中含"测试"的商品个数（数据库中已有5个）
        productCondition.setProductName("商品");
        products = productDao.queryProductList(productCondition, 0, 3);
        assertEquals(3, products.size());

        count = productDao.queryProductCount(productCondition);
        assertEquals(5, count);
    }

    @Test
    public void testCQueryProductById() throws Exception {
        long productId = 8;

        // 初始化两个商品详情图的实例，作为商品 productId=8 的详情图
        // 批量插入至商品详情图表中
        ProductImg productImg1 = new ProductImg();
        productImg1.setImgAddr("测试图片1-QueryProductById");
        productImg1.setImgDesc("测试图片1-QueryProductById");
        productImg1.setPriority(1);
        productImg1.setCreateTime(new Date());
        productImg1.setProductId(productId);

        ProductImg productImg2 = new ProductImg();
        productImg2.setImgAddr("测试图片2-QueryProductById");
        productImg2.setImgDesc("测试图片2-QueryProductById");
        productImg2.setPriority(1);
        productImg2.setCreateTime(new Date());
        productImg2.setProductId(productId);

        List<ProductImg> productImgList = new ArrayList<>();
        productImgList.add(productImg1);
        productImgList.add(productImg2);
        int effectedNum = productImgDao.batchInsertProductImg(productImgList);
        assertEquals(2, effectedNum);

        // 查询 productId=8 的商品信息，校验返回的详情图实例列表的 size 是否为 2
        Product product = productDao.queryProductById(productId);
        assertEquals(2, product.getProductImgList().size());

        // 删除新增的两个商品详情图
        effectedNum = productImgDao.deleteProductImgByProductId(productId);
        assertEquals(2, effectedNum);
    }

    @Test
    public void testDUpdateProduct() throws Exception {
        Product product = new Product();
        ProductCategory pc = new ProductCategory();
        Shop shop = new Shop();

        shop.setShopId(27L);
        pc.setProductCategoryId(11L);
        product.setProductId(8L);
        product.setShop(shop);
        // 修改 productId=8 的商品的名称、描述、类别，校验影响的行数是否为 1
        product.setProductName("测试商品名称-testDUpdateProduct");
        product.setProductDesc("测试商品描述-testDUpdateProduct");
        product.setProductCategory(pc);
        int effectedNum = productDao.updateProduct(product);
        assertEquals(1, effectedNum);
    }

    @Test
    public void testUpdateProductCategoryToNull() {
        int effectedNum = productDao.updateProductCategoryToNull(10L);
        assertEquals(1, effectedNum);
    }
}
