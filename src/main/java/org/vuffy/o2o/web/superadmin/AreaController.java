package org.vuffy.o2o.web.superadmin;


import checkers.oigj.quals.O;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.vuffy.o2o.entity.Area;
import org.vuffy.o2o.service.AreaService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/superadmin")
public class AreaController {

    Logger logger = LoggerFactory.getLogger(AreaController.class);

    @Autowired
    private AreaService areaService;

    @RequestMapping(value = "/listarea", method = RequestMethod.GET)

    //将返回值 modleMap 自动转为 json 对象，返回到前端
    @ResponseBody
    private Map<String, Object> listArea() {

    logger.info("---START---");
    long startTime = System.currentTimeMillis();

        Map<String, Object> modleMap = new HashMap<>();

        List<Area> areaList = new ArrayList<>();

        try {
            areaList = areaService.getAreaList();
            modleMap.put("rows", areaList);
            modleMap.put("total", areaList.size());
        } catch (Exception e) {
            e.printStackTrace();
            modleMap.put("success", false);
            modleMap.put("errMsg", e.toString());
        }

        logger.error("Test error ~ ");

        long endTime = System.currentTimeMillis();
        logger.debug("costTime:[{}ms]", endTime - startTime);
        logger.info("---END---");

        return modleMap;
    }

}
