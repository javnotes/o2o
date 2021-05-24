package org.vuffy.o2o.service;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vuffy.o2o.BaseTest;
import org.vuffy.o2o.dto.ShopExecution;
import org.vuffy.o2o.entity.Area;
import org.vuffy.o2o.entity.PersonInfo;
import org.vuffy.o2o.entity.Shop;
import org.vuffy.o2o.entity.ShopCategory;
import org.vuffy.o2o.enums.ShopStateEnum;

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
        ShopExecution shopExecution = shopService.addShop(shop, shopImgInputStream, shopImg.getName());

        assertEquals(ShopStateEnum.CHECK.getState(), shopExecution.getState());
    }
}
