package org.vuffy.o2o.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vuffy.o2o.dao.ShopCategoryDao;
import org.vuffy.o2o.dao.ShopDao;
import org.vuffy.o2o.dto.ShopExecution;
import org.vuffy.o2o.entity.Shop;
import org.vuffy.o2o.enums.ShopStateEnum;
import org.vuffy.o2o.exceptions.ShopOperationException;
import org.vuffy.o2o.service.ShopService;
import org.vuffy.o2o.util.ImageUtil;
import org.vuffy.o2o.util.PageCalculator;
import org.vuffy.o2o.util.PathUtil;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

  // 以成员变量的方式注入进来
  @Autowired private ShopDao shopDao;

  @Override
  public ShopExecution getShopList(Shop shopCondition, int pageIndex, int pageSize) {
    int rowIndex = PageCalculator.calculateRowIndex(pageIndex,pageSize);
    List<Shop> shopList = shopDao.queryShopList(shopCondition,rowIndex,pageSize);
    int count = shopDao.queryShopCount(shopCondition);
    ShopExecution shopExecution = new ShopExecution();
    if(shopList != null) {
      shopExecution.setShopList(shopList);
      shopExecution.setCount(count);
    } else {
      shopExecution.setState(ShopStateEnum.INNER_ERROR.getState());
    }
    return shopExecution;
  }

  @Override
  @Transactional
  /**
   * 先往数据库添加店铺记录 然后存储图片(存储到图片服务器) 之后再把图片路径更新到数据库中 1. 检查 shop 是否合法：空 2. 添加 shop -> shopImg ->
   * addShopImg() -> generateThumbnail()
   *
   * @param: [shop, shopImg]
   * @return: org.vuffy.o2o.dto.ShopExecution
   * @author vuffy
   * @date: 2021/5/15 10:41 上午
   */
  public ShopExecution addShop(Shop shop, InputStream shopImgInputstream, String fileName) {
    // 判断是该店铺信息是否可以增加，空值判断
    if (shop == null) {
      return new ShopExecution(ShopStateEnum.NULL_SHOP);
    }
    if (shop.getShopCategory() == null) {
      return new ShopExecution(ShopStateEnum.NULL_SHOPCATEGORY);
    }
    if (shop.getArea() == null) {
      return new ShopExecution(ShopStateEnum.NULL_SHOPAREA);
    }

    // 初始化并增加店铺信息
    try {
      // shop 初始化赋值
      shop.setEnableStatus(0);
      shop.setCreateTime(new Date());
      shop.setLastEditTime(new Date());

      // 向数据库中执行插入
      // 此时 shop 的 shopImg 为空
      int effectNum = shopDao.insertShop(shop);
      // 判断插入是否有效
      if (effectNum <= 0) {
        throw new ShopOperationException("店铺添加失败");
      } else {
        // 店铺添加成功后，再添加图片
        if (shopImgInputstream != null) {
          // 添加图片，用shop的Id创建图片的目录，并将 shopImg 文件流存入到该目录中
          // 该方法会将 图片的存储目录 更新到shop中
          try {
            // 图片存储成功后，即可在 shop 实例中获取图片的存储地址，后续再更新到数据库中
            addShopImg(shop, shopImgInputstream, fileName);
            // 得到图片的地址
            // shop.getShopImg();
          } catch (Exception e) {
            throw new ShopOperationException("Add ShopImg Error : " + e.getMessage());
          }

          // 将 shop 中的 shopImg 更新到数据库中
          effectNum = shopDao.updateShop(shop);
          if (effectNum <= 0) {
            throw new ShopOperationException("更新图片地址失败");
          }
        }
      }
    } catch (Exception e) {
      throw new ShopOperationException("AddShop Error : " + e.getMessage());
    }
    return new ShopExecution(ShopStateEnum.CHECK, shop);
  }

  @Override
  public Shop getShopById(long shopId) {
    return shopDao.queryByShopId(shopId);
  }

  /**
   * 1.判断是否需要修改图片 2.更新店铺信息
   *
   * @param: [shop, shopImgInputStream, fileName]
   * @return: org.vuffy.o2o.dto.ShopExecution
   * @author vuffy
   * @date: 2021/5/30 5:46 下午
   */
  @Override
  public ShopExecution modifyShop(Shop shop, InputStream shopImgInputStream, String fileName)
      throws ShopOperationException {
    if (shop == null || shop.getShopId() == null) {
      return new ShopExecution(ShopStateEnum.NULL_SHOP);
    } else {
      try {
        if (shopImgInputStream != null && fileName != null && !"".equals(fileName)) {
          Shop tempShop = shopDao.queryByShopId(shop.getShopId());
          if (tempShop.getShopImg() != null) {
            ImageUtil.deleteFileOrPath(tempShop.getShopImg());
          }
          addShopImg(shop, shopImgInputStream, fileName);
        }

        shop.setLastEditTime(new Date());
        int effectedNum = shopDao.updateShop(shop);
        if (effectedNum <= 0) {
          return new ShopExecution(ShopStateEnum.INNER_ERROR);
        } else {
          shop = shopDao.queryByShopId(shop.getShopId());
          return new ShopExecution(ShopStateEnum.SUCCESS, shop);
        }
      } catch (Exception e) {
        throw new ShopOperationException("ModifyShop Error:" + e.getMessage());
      }
    }
  }

  /**
   * 存储图片
   *
   * @param: [shop, shopImg]
   * @return: void
   * @author vuffy
   * @date: 2021/5/15 11:16 上午
   */
  private void addShopImg(Shop shop, InputStream shopImgInputStream, String fileName) {
    // 1.获取存储 店铺图片 目录的相对路径值，最后一层文件夹是shopId
    String dest = PathUtil.getShopImagePath(shop.getShopId());
    // 2. 创建存储的路径，存储图片，返回相对路径值
    // 参数1：File；参数2：dest，相对目录，不含文件名；返回值：相对路径，含文件名
    String shopImgAddr = ImageUtil.generateThumbnail(shopImgInputStream, fileName, dest);
    // 将图片保存后的相对路径(含文件名)，更新到 shop 实例，下一步是更新数据中的 shop
    shop.setShopImg(shopImgAddr);
  }
}
