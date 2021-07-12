package org.vuffy.o2o.service.impl;

import checkers.units.quals.A;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vuffy.o2o.cache.JedisUtil;
import org.vuffy.o2o.dao.ShopCategoryDao;
import org.vuffy.o2o.entity.ShopCategory;
import org.vuffy.o2o.exceptions.ShopCategoryOperationException;
import org.vuffy.o2o.service.ShopCategoryService;

import java.io.IOException;
import java.util.ArrayList;
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

    @Autowired
    private JedisUtil.Keys jedisKeys;
    @Autowired
    private JedisUtil.Strings jedisStrings;

    private static Logger logger = LoggerFactory.getLogger(ShopCategoryServiceImpl.class);

    @Override
    @Transactional
    public List<ShopCategory> getShopCategoryList(ShopCategory shopCategoryCondition) {

        // 定义 redis 的 key 前缀
        String key = SHOPCATEGORYLISTKEY;
        // 定义接收对象
        List<ShopCategory> shopCategoryList = null;
        // 定义 jackson 数据转换操作类
        ObjectMapper mapper = new ObjectMapper();
        // 拼接出 redis 的 key
        if (shopCategoryCondition == null) {
            // 若查询条件为空，则列出所有首页大类，即 parentId 为空的店铺类别
            key = key + "_firstlevel";
        } else if (shopCategoryCondition != null && shopCategoryCondition.getParent() != null
                && shopCategoryCondition.getParent().getShopCategoryId() != null) {
            // 若 parentId 不为空，则列出该 parentId 下的所有子类别
            key = key + "_parent" + shopCategoryCondition.getParent().getShopCategoryId();
        } else if (shopCategoryCondition != null) {
            // 列出所有子类别，不管其属于哪个类，都列出来
            key = key + "_secondlevel";
        }
        // 判断 key 是否存在
        if (!jedisKeys.exists(key)) {
            // 若 key 不存在，则从 DB 里面取出相应数据
            shopCategoryList = shopCategoryDao.queryShopCategory(shopCategoryCondition);
            // 将相关的实体类集合转换成string,存入redis里面对应的key中
            String jsonString;
            try {
                jsonString = mapper.writeValueAsString(shopCategoryList);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new ShopCategoryOperationException(e.getMessage());
            }
            jedisStrings.set(key, jsonString);
        } else {
            // 若 key 存在，则从 Redis 里面取出相应数据
            String jsonString = jedisStrings.get(key);
            // 指定要将 String 转换成的集合类型
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, ShopCategory.class);
            try {
                // 将相关 key 对应的 value 里的的 String 转换成对象的实体类集合
                shopCategoryList = mapper.readValue(jsonString, javaType);
            } catch (JsonParseException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new ShopCategoryOperationException(e.getMessage());
            } catch (JsonMappingException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new ShopCategoryOperationException(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new ShopCategoryOperationException(e.getMessage());
            }
        }
        return shopCategoryList;
    }
}
