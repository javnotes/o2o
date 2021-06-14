package org.vuffy.o2o.web.shopadmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.vuffy.o2o.dto.ProductCategoryExecution;
import org.vuffy.o2o.dto.Result;
import org.vuffy.o2o.entity.ProductCategory;
import org.vuffy.o2o.entity.Shop;
import org.vuffy.o2o.enums.ProductCategoryStateEnum;
import org.vuffy.o2o.exceptions.ProductCategoryOperationException;
import org.vuffy.o2o.service.ProductCategoryService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vuffy
 * @version 1.0
 * @description: TODO
 * @date 2021/6/12 3:26 下午
 */
@Controller
@RequestMapping("/shopadmin")
public class ProductCategoryManagementController {
    @Autowired
    private ProductCategoryService productCategoryService;

    @RequestMapping(value = "/getproductcategorylist", method = RequestMethod.GET)
    @ResponseBody
    private Result<List<ProductCategory>> getProductCategoryList(HttpServletRequest request) {

        Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
        List<ProductCategory> list = null;
        if (currentShop != null && currentShop.getShopId() > 0) {
            list = productCategoryService.getProductCategoryList(currentShop.getShopId());
            return new Result<>(true, list);
        } else {
            ProductCategoryStateEnum ps = ProductCategoryStateEnum.INNER_ERROR;
            return new Result<>(false, ps.getState(), ps.getStateInfo());
        }
    }

    @RequestMapping(value = "/addproductcategorys", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> addProductCategorys(@RequestBody List<ProductCategory>
                                                            productCategoryList, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
//        // 为 productCategoryList 中的商品类别设置所属的店铺
//        for (ProductCategory pc : productCategoryList) {
//            pc.setShopId(currentShop.getShopId());
//        }
        if (productCategoryList != null && productCategoryList.size() > 0) {
            // correct
            // 为 productCategoryList 中的商品类别设置所属的店铺
            for (ProductCategory pc : productCategoryList) {
                pc.setShopId(currentShop.getShopId());
            }

            try {
                ProductCategoryExecution pce = productCategoryService.batchAddProductCategory(productCategoryList);
                if (pce.getState() == ProductCategoryStateEnum.SUCCESS.getState()) {
                    // 操作成功，则在数据库中，店铺的商品类别增加成功
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errorMessage", pce.toString());
                }
            } catch (ProductCategoryOperationException e) {
                modelMap.put("success", false);
                modelMap.put("errorMessage", e.toString());
                return modelMap;
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "请输入至少一个商品类别");
        }
        return modelMap;
    }

    @RequestMapping(value = "/removeproductcategory", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> removeProductCategory(Long productCategoryId, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        if (productCategoryId != null && productCategoryId > 0) {
            try {
                Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
                ProductCategoryExecution pce = productCategoryService.deleteProductCategory(productCategoryId, currentShop.getShopId());
                if (pce.getState() == ProductCategoryStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errorMessage", pce.getStateInfo());
                }
            } catch (RuntimeException e) {
                modelMap.put("success", false);
                modelMap.put("success", e.toString());
                return modelMap;
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "请至少选择一个商品类别");
        }
        return modelMap;
    }
}
