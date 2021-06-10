package org.vuffy.o2o.dao;

import org.apache.ibatis.annotations.Param;
import org.vuffy.o2o.entity.Shop;

import java.util.List;

public interface ShopDao {

  /**
   * 分页查询店铺，可输入：店铺名（模糊查询）、店铺状态、店铺类别、区域id，owner
   *
   * @param: [shopCondation 传入的条件, rowIndex 从第*行开始取数, pageSize 返回数据的条数]
   * @return: java.util.List<org.vuffy.o2o.entity.Shop>
   * @author vuffy
   * @date: 2021/6/5 2:11 下午
   */
  List<Shop> queryShopList(
      @Param("shopCondition") Shop shopCondition,
      @Param("rowIndex") int rowIndex,
      @Param("pageSize") int pageSize);
  /**
   * 返回 queryShopList 的总数，他俩的查询条件相同
   *
   * @author vuffy
   * @date 2021/6/5 2:56 下午
   * @version 1.0
   */
  int queryShopCount(@Param("shopCondition") Shop shopCondition);

  /**
   * 通过 shopId 查询店铺
   *
   * @author vuffy
   * @date 2021/5/30 2:50 下午
   * @version 1.0
   */
  Shop queryByShopId(long shopId);

  /**
   * 新增店铺，-1 由 MyBatis 返回，插入失败 实现类由 Mybatis 实现，故需要配置 mapper xml 文件：ShopDao.xml,insert
   * id="insertShop"
   *
   * @param shop
   * @return
   */
  int insertShop(Shop shop);

  /**
   * 更新店铺 实现类在 ShopDao.xml，update id="updateShop"
   *
   * @param shop
   * @return
   */
  int updateShop(Shop shop);
}
