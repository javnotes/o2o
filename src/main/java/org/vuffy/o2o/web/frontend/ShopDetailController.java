package org.vuffy.o2o.web.frontend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.vuffy.o2o.dto.ProductExecution;
import org.vuffy.o2o.entity.Product;
import org.vuffy.o2o.entity.ProductCategory;
import org.vuffy.o2o.entity.Shop;
import org.vuffy.o2o.service.ProductCategoryService;
import org.vuffy.o2o.service.ProductService;
import org.vuffy.o2o.service.ShopService;
import org.vuffy.o2o.util.HttpServletRequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/frontend")
public class ShopDetailController {
    @Autowired
    private ShopService shopService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCategoryService productCategoryService;

    /**
     * 获取店铺信息及其有哪些商品类别
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/listshopdetailpageinfo", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listShopDetailPageInfo(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        // 获取前台传来的 shopId
        long shopId = HttpServletRequestUtil.getLong(request, "shopId");
        Shop shop = null;
        List<ProductCategory> productCategoryList = null;
        if (shopId != -1) {
            // 获取店铺id=shopId的店铺xinxi
            shop = shopService.getShopById(shopId);
            // 获取该店铺中有哪些商品类别
            productCategoryList = productCategoryService.getProductCategoryList(shopId);
            modelMap.put("shop", shop);
            modelMap.put("productCategoryList", productCategoryList);
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "empty shopId");
        }
        return modelMap;
    }

    /**
     * 根据查询条件，列出该店铺中的商品
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/listproductsbyshop", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listProductsByShop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        // 获取页码
        int pageIndex = HttpServletRequestUtil.getInteger(request, "pageIndex");
        // 获取显示的最大行数
        int pageSize = HttpServletRequestUtil.getInteger(request, "pageSize");
        // 获取店铺id
        long shopId = HttpServletRequestUtil.getLong(request, "shopId");
        // 空值判断
        if ((pageIndex > -1) && (pageSize > -1) && (shopId > -1)) {
            // 尝试获取商品类别id
            long productCategoryId = HttpServletRequestUtil.getLong(request, "productCategoryId");
            // 尝试获取（模糊查找）商品名
            String productName = HttpServletRequestUtil.getString(request, "productName");
            // 组合查询条件
            Product productCondition = compactProductCondition4Search(shopId, productCategoryId, productName);
            // 传入查询条件、分页信息，返回结果及个数
            ProductExecution pe = productService.getProductList(productCondition, pageIndex, pageSize);
            modelMap.put("productList", pe.getProductList());
            modelMap.put("count", pe.getCount());
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errorMessage", "Error:empty pageSize or pageIndex or shopId");
        }
        return modelMap;
    }

    /**
     * 将查询条件封装至 Product 对象
     *
     * @param shopId
     * @param productCategoryId
     * @param productName
     * @return
     */
    private Product compactProductCondition4Search(long shopId, long productCategoryId, String productName) {
        Product productCondition = new Product();
        // 查询哪个店铺下的商品
        Shop shop = new Shop();
        shop.setShopId(shopId);
        productCondition.setShop(shop);
        if (productCategoryId != -1L) {
            // 查询某个商品类别下有哪些商品
            ProductCategory productCategory = new ProductCategory();
            productCategory.setProductCategoryId(productCategoryId);
            productCondition.setProductCategory(productCategory);
        }
        if (productName != null) {
            // 按商品名称（模糊）查询商品
            productCondition.setProductName(productName);
        }
        // 只能查出状态为上架的商品
        productCondition.setEnableStatus(1);
        return productCondition;
    }

}
