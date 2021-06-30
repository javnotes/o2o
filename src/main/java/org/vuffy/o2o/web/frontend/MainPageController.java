package org.vuffy.o2o.web.frontend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.vuffy.o2o.entity.HeadLine;
import org.vuffy.o2o.entity.ShopCategory;
import org.vuffy.o2o.service.HeadLineService;
import org.vuffy.o2o.service.ShopCategoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 响应对主页的请求
 */
@Controller
@RequestMapping("/frontend")
public class MainPageController {
    @Autowired
    private ShopCategoryService shopCategoryService;

    @Autowired
    private HeadLineService headLineService;

    @RequestMapping(value = "/listmainpageinfo", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listMainPgeInfo() {
        Map<String, Object> modelMap = new HashMap<>();
        List<ShopCategory> shopCategoryList = new ArrayList<>();

        try {
            // 获取一级店铺类别列表，即 parent_id 为空的 ShopCategory
            shopCategoryList = shopCategoryService.getShopCategoryList(null);
            modelMap.put("shopCategoryList", shopCategoryList);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errorMessage", e.toString());
            return modelMap;
        }

        List<HeadLine> headLineList = new ArrayList<>();
        try {
            // 获取头条列表（状态为 1）
            HeadLine headLineCondition = new HeadLine();
            headLineCondition.setEnableStatus(1);
            headLineList = headLineService.getHeadLineList(headLineCondition);
            modelMap.put("headLineList", headLineList);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errorMessage", e.toString());
            return modelMap;
        }
        modelMap.put("success", true);
        return modelMap;
    }


}
