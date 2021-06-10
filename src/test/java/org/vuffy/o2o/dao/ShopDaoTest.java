package org.vuffy.o2o.dao; // package org.vuffy.o2o.dao;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vuffy.o2o.BaseTest;
import org.vuffy.o2o.entity.Area;
import org.vuffy.o2o.entity.PersonInfo;
import org.vuffy.o2o.entity.Shop;
import org.vuffy.o2o.entity.ShopCategory;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ShopDaoTest extends BaseTest {
  @Autowired private ShopDao shopDao;


  @Test
  public void testQueryShopListAndCount() {
    Shop shopCondition = new Shop();
    PersonInfo owner = new PersonInfo();
    owner.setUserId(1L);
    shopCondition.setPersonInfo(owner);

    List<Shop> shopList = shopDao.queryShopList(shopCondition, 0, 5);
    int count = shopDao.queryShopCount(shopCondition);
    System.out.println("店铺列表的大小：" + shopList.size());
    System.out.println(count);

    ShopCategory shopCategory = new ShopCategory();
    shopCategory.setShopCategoryId(4L);
    shopCondition.setShopCategory(shopCategory);
    List<Shop> shopList1 = shopDao.queryShopList(shopCondition, 0, 5);
    int count1 = shopDao.queryShopCount(shopCondition);
    System.out.println("店铺列表的大小：" + shopList1.size());
    System.out.println(count1);
  }

  @Test
  public void testQueryByShopId() {
    long shopId = 24;
    Shop shop = shopDao.queryByShopId(shopId);
    System.out.println(shop.getArea().getAreaId());
    System.out.println(shop.getArea().getAreaName());
  }

  @Test
  @Ignore
  public void testInsertShop() {
    Shop shop = new Shop();
    PersonInfo owner = new PersonInfo();
    Area area = new Area();
    ShopCategory shopCategoty = new ShopCategory();
    owner.setUserId(1L);
    area.setAreaId(2);
    shopCategoty.setShopCategoryId(1L);
    shop.setPersonInfo(owner);
    shop.setArea(area);
    shop.setShopCategory(shopCategoty);
    shop.setShopName("测试店铺1");
    shop.setShopDesc("测试描述1");
    shop.setShopAddr("测试地址1");
    shop.setPhone("测试手机号码1");

    shop.setCreateTime(new Date());
    shop.setEnableStatus(0);
    shop.setAdvice("审核中");
    shop.setShopImg("测试图片1");
    int effectNumber = shopDao.insertShop(shop);
    assertEquals(1, effectNumber);
  }

  @Test
  @Ignore
  public void testInsertShop1() {
    Shop shop = new Shop();
    PersonInfo owner = new PersonInfo();
    Area area = new Area();
    ShopCategory shopCategory = new ShopCategory();
    owner.setUserId(1L);
    area.setAreaId(2);
    shopCategory.setShopCategoryId(1L);
    shop.setPersonInfo(owner);
    shop.setArea(area);
    shop.setShopCategory(shopCategory);
    shop.setShopName("测试的店铺");
    shop.setShopDesc("test");
    shop.setShopAddr("test");
    shop.setPhone("test");
    shop.setShopImg("test");
    shop.setCreateTime(new Date());
    shop.setEnableStatus(0);
    shop.setAdvice("审核中");
    int effectedNum = shopDao.insertShop(shop);
    assertEquals(1, effectedNum);
  }

  @Test
  @Ignore
  public void testUpdateShop() {
    Shop shop = new Shop();
    shop.setShopId(5L);
    PersonInfo owner = new PersonInfo();
    Area area = new Area();
    ShopCategory shopCategory = new ShopCategory();
    owner.setUserId(1L);
    area.setAreaId(2);
    shopCategory.setShopCategoryId(1L);
    shop.setPersonInfo(owner);
    shop.setArea(area);
    shop.setShopCategory(shopCategory);
    shop.setShopName("测试的店铺-5");
    shop.setShopDesc("test");
    shop.setShopAddr("test");
    shop.setPhone("test");
    shop.setShopImg("test");
    shop.setCreateTime(new Date());
    shop.setEnableStatus(0);
    shop.setAdvice("审核中");
    shop.setLastEditTime(new Date());
    int effectedNum = shopDao.updateShop(shop);
    assertEquals(1, effectedNum);
  }
}
