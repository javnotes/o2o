package org.vuffy.o2o.dao;

import org.vuffy.o2o.entity.Shop;

public interface ShopDao {

    /**
     * 新增店铺，-1 由 MyBatis 返回，插入失败
     * 实现类由 Mybatis 实现，故需要配置 mapper xml 文件：ShopDao.xml,insert id="insertShop"
     * @param shop
     * @return
     */
    int insertShop(Shop shop);

    /**
     * 更新店铺
     * 实现类在 ShopDao.xml，update id="updateShop"
     * @param shop
     * @return
     */
    int updateShop(Shop shop);
}
