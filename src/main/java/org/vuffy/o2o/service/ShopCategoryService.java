package org.vuffy.o2o.service;

import org.vuffy.o2o.entity.ShopCategory;

import java.util.List;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/5/23 3:44 下午
 */
public interface ShopCategoryService {

    String SHOPCATEGORYLISTKEY = "shopcategorylist";

    /**
     * 根据查询条件获取 ShopCategoryList 列表
     *
     * @param shopCategoryCondition
     * @return
     */
    List<ShopCategory> getShopCategoryList(ShopCategory shopCategoryCondition);
}
