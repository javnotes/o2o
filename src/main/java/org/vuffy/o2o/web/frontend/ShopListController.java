package org.vuffy.o2o.web.frontend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.vuffy.o2o.dto.ShopExecution;
import org.vuffy.o2o.entity.Area;
import org.vuffy.o2o.entity.Shop;
import org.vuffy.o2o.entity.ShopCategory;
import org.vuffy.o2o.service.AreaService;
import org.vuffy.o2o.service.ShopCategoryService;
import org.vuffy.o2o.service.ShopService;
import org.vuffy.o2o.util.HttpServletRequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/frontend")
public class ShopListController {

    @Autowired
    private ShopCategoryService shopCategoryService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private ShopService shopService;

    /**
     * 返回商品列表里的ShopCategory列表（一、二级）、区域信息的列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/listshopspageinfo", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listShopPageInfo(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        // 尝试从前端请求中获取parentId
        long parentId = HttpServletRequestUtil.getLong(request, "parentId");
        List<ShopCategory> shopCategoryList = null;
        if (parentId != -1) {
            // parentId 存在，则取出该一级 ShopCategory 下的二级 ShopCategory 列表
            try {
                ShopCategory shopCategoryCondition = new ShopCategory();
                ShopCategory parent = new ShopCategory();
                parent.setShopCategoryId(parentId);
                shopCategoryCondition.setParent(parent);
                shopCategoryList = shopCategoryService.getShopCategoryList(shopCategoryCondition);
            } catch (Exception e) {
                modelMap.put("success", false);
                modelMap.put("errorMessage", e.toString());
            }
        } else {
            try {
                // parentId 不存在，则取出所有一级目录ShopCategory （用户在首页选择的是全部商店列表）
                shopCategoryList = shopCategoryService.getShopCategoryList(null);
            } catch (Exception e) {
                modelMap.put("success", false);
                modelMap.put("errorMessage", e.toString());
            }
        }
        modelMap.put("shopCategoryList", shopCategoryList);

        List<Area> areaList = null;
        try {
            // 获取区域列表
            areaList = areaService.getAreaList();
            modelMap.put("areaList", areaList);
            modelMap.put("success", true);
            return modelMap;
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errorMessage", e.toString());
        }
        return modelMap;
    }

    /**
     * 获取特定查询条件下的店铺列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/listshops", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listShops(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        // 获取页码
        int pageIndex = HttpServletRequestUtil.getInteger(request, "pageIndex");
        // 获取每页显示的行数
        int pageSize = HttpServletRequestUtil.getInteger(request, "pageSize");
        // 非空判断
        if ((pageIndex > -1) && (pageSize > -1)) {
            // 尝试获取一级类别Id
            long parentId = HttpServletRequestUtil.getLong(request, "parentId");
            // 尝试获取特定的二级类别Id
            long shopCategoryId = HttpServletRequestUtil.getLong(request, "shopCategoryId");
            // 尝试获取区域Id
            int areaId = HttpServletRequestUtil.getInteger(request, "areaId");
            // 尝试获取店铺名字（模糊查询）
            String shopName = HttpServletRequestUtil.getString(request, "shopName");
            // 获取组合后的查询条件
            Shop shopCondition = compactShopCondition4Search(parentId, shopCategoryId, areaId, shopName);
            //根据查询条件、分页信息得到店铺列表及个数
            ShopExecution se = shopService.getShopList(shopCondition, pageIndex, pageSize);
            modelMap.put("shopList", se.getShopList());
            modelMap.put("count", se.getCount());
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "Error:empty pageSize or pageIndex");
        }
        return modelMap;
    }

    /**
     * 组合查询条件，封装至 shopCondition 中
     *
     * @param parentId
     * @param shopCategoryId
     * @param areaId
     * @param shopName
     * @return
     */
    private Shop compactShopCondition4Search(long parentId, long shopCategoryId, int areaId, String shopName) {
        Shop shopCondition = new Shop();
        if (parentId != -1L) {
            // 查询某一个一级ShopCategory下面的所有二级 ShopCategory 里面的店铺列表
            ShopCategory childCategory = new ShopCategory();
            ShopCategory parentCategory = new ShopCategory();
            parentCategory.setShopCategoryId(parentId);
            childCategory.setParent(parentCategory);
            shopCondition.setShopCategory(childCategory);
        }
        if (shopCategoryId != -1L) {
            // 查询某二级ShopCategoryId下，所有的店铺
            ShopCategory shopCategory = new ShopCategory();
            shopCategory.setShopCategoryId(shopCategoryId);
            shopCondition.setShopCategory(shopCategory);
        }
        if (areaId != -1L) {
            //查询某区域Id 下的店铺
            Area area = new Area();
            area.setAreaId(areaId);
            shopCondition.setArea(area);
        }
        if (shopName != null) {
            // （模糊）查询名字
            shopCondition.setShopName(shopName);
        }

        // 前端展示的店铺的状态须是审核成功
        shopCondition.setEnableStatus(1);

        return shopCondition;

    }
}
