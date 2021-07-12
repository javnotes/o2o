package org.vuffy.o2o.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vuffy.o2o.BaseTest;
import org.vuffy.o2o.entity.ShopCategory;

import java.util.List;

public class ShopCategoryServiceTest extends BaseTest {

    @Autowired
    private ShopCategoryService shopCategoryService;

    @Test
    public void testGetShopCategoryList() {
        ShopCategory shopCategory = new ShopCategory();
        List<ShopCategory> shopCategoryList = shopCategoryService.getShopCategoryList(shopCategory);
        System.out.println(shopCategoryList.size());
    }
}
