package org.vuffy.o2o.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vuffy.o2o.cache.JedisUtil;
import org.vuffy.o2o.dao.HeadLineDao;
import org.vuffy.o2o.entity.HeadLine;
import org.vuffy.o2o.exceptions.HeadLineOperationException;
import org.vuffy.o2o.service.HeadLineService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class HeadLineServiceImpl implements HeadLineService {
    @Autowired
    private HeadLineDao headLineDao;

    @Autowired
    private JedisUtil.Keys jedisKeys;
    @Autowired
    private JedisUtil.Strings jedisStrings;

    private static Logger logger = LoggerFactory.getLogger(HeadLineServiceImpl.class);

    @Override
    @Transactional
    public List<HeadLine> getHeadLineList(HeadLine headLineCondition) throws IOException {
        // 定义 redis 的 key 前缀
        // enableStatus=0.不可用 1.可用
        String key = HEADLINELISTKEY;
        // 定义接收对象
        List<HeadLine> headLineList = null;
        // 定义 jackson 的数据转换操作类
        ObjectMapper mapper = new ObjectMapper();
        // 拼接出 redis 的 key
        if (headLineCondition != null && headLineCondition.getEnableStatus() != null) {
            // key = headlinelist_0、headlinelist_1
            key = key + "_" + headLineCondition.getEnableStatus();
        }
        // 判断 key 是否存在
        if (!jedisKeys.exists(key)) {
            // 若 key 不存在，则从 DB 中获取数据，再存入到 Redis 中
            headLineList = headLineDao.queryHeadLine(headLineCondition);
            // 将实体类集合转为Jsong(String)
            String jsonString;
            try {
                jsonString = mapper.writeValueAsString(headLineList);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new HeadLineOperationException(e.getMessage());
            }
            jedisStrings.set(key, jsonString);
        } else {
            // 若 key 存在，则从 Redis 中取出相应的数据(String格式)
            String jsonString = jedisStrings.get(key);
            // 指定将String格式转换为集合类型
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, HeadLine.class);
            try {
                // 将相关 key 对应的value里的string转换为对象的实体类集合
                headLineList = mapper.readValue(jsonString, javaType);
            } catch (JsonParseException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new HeadLineOperationException(e.getMessage());
            } catch (JsonMappingException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new HeadLineOperationException(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new HeadLineOperationException(e.getMessage());
            }
        }
        return headLineList;
    }
}