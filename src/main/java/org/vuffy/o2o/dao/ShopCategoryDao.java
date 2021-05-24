package org.vuffy.o2o.dao;

import org.apache.ibatis.annotations.Param;
import org.vuffy.o2o.entity.ShopCategory;

import java.util.List;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/5/23 11:35 上午
 */
public interface ShopCategoryDao {
    List<ShopCategory> queryShopCategory(@Param("shopCategoryCondition") ShopCategory shopCategoryCondition);
}
