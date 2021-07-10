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
import org.vuffy.o2o.dao.AreaDao;
import org.vuffy.o2o.entity.Area;
import org.vuffy.o2o.exceptions.AreaOperationException;
import org.vuffy.o2o.service.AreaService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class AreaServiceImpl implements AreaService {

    // Service 层依赖与 Dao 层
    @Autowired
    private AreaDao areaDao;

    @Autowired
    private JedisUtil.Keys jedisKeys;
    @Autowired
    private JedisUtil.Strings jedisStrings;

    private static String AREALISTKEY = "arealist";
    private static Logger logger = LoggerFactory.getLogger(AreaServiceImpl.class);

    @Override
    @Transactional
    public List<Area> getAreaList() {
        String key = AREALISTKEY;
        List<Area> areaList = null;
        ObjectMapper mapper = new ObjectMapper();

        if (!jedisKeys.exists(key)) {
            // Redis 中没有
            areaList = areaDao.queryArea();
            // 将 value 值转为 json
            String jsonString;
            try {
                jsonString = mapper.writeValueAsString(areaList);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new AreaOperationException(e.getMessage());
            }
            jedisStrings.set(key, jsonString);
        } else {
            String jsonString = jedisStrings.get(key);
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, Area.class);
            try {
                areaList = mapper.readValue(jsonString, javaType);
            } catch (JsonParseException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new AreaOperationException(e.getMessage());
            } catch (JsonMappingException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new AreaOperationException(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new AreaOperationException(e.getMessage());
            }
        }
        return areaList;
    }
}
