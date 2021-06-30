package org.vuffy.o2o.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vuffy.o2o.dao.ShopCategoryDao;
import org.vuffy.o2o.entity.ShopCategory;
import org.vuffy.o2o.service.ShopCategoryService;

import java.util.List;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/5/23 3:44 下午
 */
@Service
public class ShopCategoryServiceImpl implements ShopCategoryService {
    // 将 Dao层 当作是成员变量，注入使用
    @Autowired
    private ShopCategoryDao shopCategoryDao;

    @Override
    public List<ShopCategory> getShopCategoryList(ShopCategory shopCategoryCondition) {
        return shopCategoryDao.queryShopCategory(shopCategoryCondition);
    }
}
