package org.vuffy.o2o.service;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vuffy.o2o.BaseTest;
import org.vuffy.o2o.dto.ImageHolder;
import org.vuffy.o2o.dto.ShopExecution;
import org.vuffy.o2o.entity.Area;
import org.vuffy.o2o.entity.PersonInfo;
import org.vuffy.o2o.entity.Shop;
import org.vuffy.o2o.entity.ShopCategory;
import org.vuffy.o2o.enums.ShopStateEnum;
import org.vuffy.o2o.exceptions.ShopOperationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/5/15 2:23 下午
 */
public class ShopServiceTest extends BaseTest {

    @Autowired
    // @Autowired 告诉Spring，初始化ShopServiceTest时，将ShopService（接口）的实现类动态的注入到shopService中
    private ShopService shopService;

    @Test
    public void testGetShopList() {
        Shop shopCondition = new Shop();
        ShopCategory shopCategory = new ShopCategory();
        shopCategory.setShopCategoryId(4L);
        shopCondition.setShopCategory(shopCategory);
        ShopExecution se = shopService.getShopList(shopCondition, 3, 3);
        System.out.println("店铺列表数：" + se.getShopList().size());
        System.out.println("店铺总数：" + se.getCount());
    }

    @Test
    public void testModifyShop() throws ShopOperationException, FileNotFoundException {
        Shop newShop = new Shop();
        newShop.setShopId(24L);
        newShop.setShopName("新一代");
        File newShopImg =
                new File("/Users/liliansong/Documents/互联网大厂新入职员工各职级薪资对应表（技术线）V4.5 (2020.5).png");
        InputStream is = new FileInputStream(newShopImg);
        ImageHolder imageHolder = new ImageHolder("互联网大厂新入职员工各职级薪资对应表（技术线）V4.5 (2020.5).png", is);
        ShopExecution shopExecution =
                shopService.modifyShop(newShop, imageHolder);
        System.out.println("修改后的图片地址：" + shopExecution.getShop().getShopImg());
    }

    @Test
    @Deprecated
    public void testAddShop() throws FileNotFoundException {

        Shop shop = new Shop();
        // shop.setShopId(5L);
        PersonInfo owner = new PersonInfo();
        Area area = new Area();
        ShopCategory shopCategory = new ShopCategory();
        owner.setUserId(1L);
        area.setAreaId(2);
        shopCategory.setShopCategoryId(1L);
        shop.setPersonInfo(owner);
        shop.setArea(area);
        shop.setShopCategory(shopCategory);
        shop.setShopName("测试的店铺-AddShop");
        shop.setShopDesc("test-2333");
        shop.setShopAddr("test-2333");
        shop.setPhone("test-2333");

        shop.setCreateTime(new Date());
        shop.setEnableStatus(ShopStateEnum.CHECK.getState());
        shop.setAdvice("审核中");
        shop.setCreateTime(new Date());
        shop.setLastEditTime(new Date());

        File shopImg = new File("/Users/liliansong/Documents/woman.jpg");
        FileInputStream shopImgInputStream = new FileInputStream(shopImg);
        ImageHolder imageHolder = new ImageHolder(shopImg.getName(), shopImgInputStream);
        ShopExecution shopExecution = shopService.addShop(shop, imageHolder);

        assertEquals(ShopStateEnum.CHECK.getState(), shopExecution.getState());
    }
}
